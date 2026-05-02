package com.paulaccesso.visitor.service;

import com.paulaccesso.visitor.dto.CheckoutRequest;
import com.paulaccesso.visitor.dto.TagAssignmentRequest;
import com.paulaccesso.visitor.dto.TagResponse;
import com.paulaccesso.visitor.dto.VisitorRequest;
import com.paulaccesso.visitor.dto.VisitorResponse;
import com.paulaccesso.visitor.dto.UserDto;
import com.paulaccesso.visitor.entity.MeetingStatus;
import com.paulaccesso.visitor.entity.Role;
import com.paulaccesso.visitor.entity.TagNumber;
import com.paulaccesso.visitor.entity.User;
import com.paulaccesso.visitor.entity.Visitor;
import com.paulaccesso.visitor.repository.TagNumberRepository;
import com.paulaccesso.visitor.repository.UserRepository;
import com.paulaccesso.visitor.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TagNumberRepository tagNumberRepository;

    @Transactional
    public VisitorResponse registerVisitor(Long userId, VisitorRequest request) {
        User registeredBy = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find employee by empId
        User employee = userRepository.findEmployeeByEmpId(request.getPersonToMeetEmpId())
                .orElseThrow(
                        () -> new RuntimeException("Employee not found with EmpId: " + request.getPersonToMeetEmpId()));

        Visitor visitor = new Visitor();
        visitor.setName(request.getName());
        visitor.setMobile(request.getMobile());
        visitor.setEmail(request.getEmail());
        visitor.setCompany(request.getCompany());
        visitor.setAddress(request.getAddress());
        visitor.setPersonToMeet(employee.getEmail());
        visitor.setPurpose(request.getPurpose());
        visitor.setPhoto(request.getPhoto());
        visitor.setIdProof(request.getIdProof());
        visitor.setCheckInTime(LocalDateTime.now());
        visitor.setActive(true);
        visitor.setMeetingStatus(MeetingStatus.PENDING);
        visitor.setUser(registeredBy);

        if (request.getTagNumber() != null && !request.getTagNumber().isEmpty()) {
            TagNumber tagNumber = tagNumberRepository.findByTagNumber(request.getTagNumber())
                    .orElseThrow(() -> new RuntimeException("Tag number not found"));

            // Check if tag is available
            if (!tagNumber.isAvailable()) {
                throw new RuntimeException("Tag number is already assigned to another visitor");
            }

            // ✅ Additional safety check - ensure no active visitor has this tag
            Visitor existingVisitor = visitorRepository.findActiveVisitorByTagNumber(request.getTagNumber());
            if (existingVisitor != null) {
                throw new RuntimeException(
                        "Tag number is already assigned to an active visitor: " + existingVisitor.getName());
            }

            tagNumberRepository.assignTagToVisitor(request.getTagNumber(), null);
            visitor.setTagNumber(request.getTagNumber());
        }

        Visitor savedVisitor = visitorRepository.save(visitor);
        log.info("Visitor registered: {} by {}", savedVisitor.getName(), registeredBy.getEmail());

        // Send email to employee
        sendMeetingRequestToEmployee(employee, savedVisitor);

        return mapToResponse(savedVisitor);
    }

    private void sendMeetingRequestToEmployee(User employee, Visitor visitor) {
        emailService.sendVisitorNotificationToEmployee(employee, visitor);
        log.info("Meeting request sent to employee: {} ({})", employee.getName(), employee.getEmpId());
    }

    @Transactional
    public VisitorResponse updateMeetingStatus(Long visitorId, String status) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));

        MeetingStatus meetingStatus = MeetingStatus.valueOf(status);
        visitor.setMeetingStatus(meetingStatus);
        Visitor updatedVisitor = visitorRepository.save(visitor);

        if ("ACCEPTED".equals(status)) {
            User receptionist = visitor.getUser();
            List<User> employees = userRepository.findEmployeesByEmail(visitor.getPersonToMeet());
            User employee = employees.isEmpty() ? null : employees.get(0);

            if (receptionist != null && employee != null) {
                emailService.sendNotificationToReceptionist(receptionist, visitor, employee);
                log.info("Receptionist {} notified to assist visitor: {}", receptionist.getEmail(), visitor.getName());
            }
        }

        log.info("Meeting status updated to {} for visitor: {}", status, visitorId);
        return mapToResponse(updatedVisitor);
    }

    // Add this method to your existing VisitorService.java
    @Transactional
    public void resendMeetingEmail(Long visitorId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));

        if (!visitor.isActive()) {
            throw new RuntimeException("Cannot resend email for checked out visitor");
        }

        if (visitor.getMeetingStatus() != MeetingStatus.PENDING) {
            throw new RuntimeException("Meeting request already " + visitor.getMeetingStatus());
        }

        // Find employee by email
        List<User> employees = userRepository.findEmployeesByEmail(visitor.getPersonToMeet());

        if (employees.isEmpty()) {
            throw new RuntimeException("Employee not found with email: " + visitor.getPersonToMeet());
        }

        // Resend email to all employees
        for (User employee : employees) {
            emailService.sendVisitorNotificationToEmployee(employee, visitor);
            log.info("Meeting request email resent to: {} ({})", employee.getName(), employee.getEmail());
        }

        log.info("Meeting request email resent for visitor: {}", visitor.getName());
    }

    @Transactional
    public VisitorResponse assignTagToVisitor(TagAssignmentRequest request) {
        Visitor visitor = visitorRepository.findById(request.getVisitorId())
                .orElseThrow(() -> new RuntimeException("Visitor not found"));

        if (!visitor.isActive()) {
            throw new RuntimeException("Cannot assign tag to checked out visitor");
        }

        if (visitor.getTagNumber() != null && !visitor.getTagNumber().isEmpty()) {
            throw new RuntimeException("Visitor already has a tag: " + visitor.getTagNumber());
        }

        TagNumber tagNumber = tagNumberRepository.findByTagNumber(request.getTagNumber())
                .orElseThrow(() -> new RuntimeException("Tag number not found"));

        if (!tagNumber.isAvailable()) {
            throw new RuntimeException("Tag number is already assigned to another visitor");
        }

        tagNumberRepository.assignTagToVisitor(request.getTagNumber(), visitor.getId());
        visitor.setTagNumber(request.getTagNumber());
        Visitor updatedVisitor = visitorRepository.save(visitor);

        log.info("Tag {} assigned to visitor: {}", request.getTagNumber(), visitor.getName());

        return mapToResponse(updatedVisitor);
    }

    @Transactional
    public VisitorResponse checkOutVisitor(Long visitorId, CheckoutRequest request) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));

        visitor.setCheckOutTime(LocalDateTime.now());
        visitor.setActive(false);

        if (request != null && request.getCheckoutPhoto() != null) {
            visitor.setCheckoutPhoto(request.getCheckoutPhoto());
        }

        if (visitor.getTagNumber() != null && !visitor.getTagNumber().isEmpty()) {
            String tagNumber = visitor.getTagNumber(); // Store tag number before clearing
            tagNumberRepository.releaseTag(tagNumber);
            visitor.setTagNumber(null); // ✅ CRITICAL FIX: Clear the tag number from visitor record
            log.info("Tag {} released from visitor: {}", tagNumber, visitor.getName());
        }

        Visitor updatedVisitor = visitorRepository.save(visitor);
        log.info("Visitor checked out: {}", visitorId);

        return mapToResponse(updatedVisitor);
    }

    public List<VisitorResponse> getAllVisitors(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                log.warn("User not found with id: {}", userId);
                return List.of();
            }

            List<Visitor> visitors = visitorRepository.findAll();

            return visitors.stream()
                    .map(this::mapToResponse)
                    .filter(response -> response != null)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error getting visitors for user {}: {}", userId, e.getMessage());
            return List.of();
        }
    }

    public VisitorResponse getVisitorById(Long visitorId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));
        return mapToResponse(visitor);
    }

    public List<VisitorResponse> getActiveVisitors() {
        return visitorRepository.findActiveVisitors().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public long getTodayVisitorCount() {
        return visitorRepository.countTodayVisitors();
    }

    public long getActiveVisitorCount() {
        return visitorRepository.countActiveVisitors();
    }

    public List<TagResponse> getAvailableTags() {
        return tagNumberRepository.findByIsAvailableTrue().stream()
                .map(this::mapToTagResponse)
                .collect(Collectors.toList());
    }

    public List<TagResponse> getAllTags() {
        return tagNumberRepository.findAll().stream()
                .map(this::mapToTagResponse)
                .collect(Collectors.toList());
    }

    private VisitorResponse mapToResponse(Visitor visitor) {
        VisitorResponse response = new VisitorResponse();
        response.setId(visitor.getId());
        response.setName(visitor.getName());
        response.setMobile(visitor.getMobile());
        response.setEmail(visitor.getEmail());
        response.setCompany(visitor.getCompany());
        response.setAddress(visitor.getAddress());
        response.setPersonToMeet(visitor.getPersonToMeet());
        response.setPurpose(visitor.getPurpose());
        response.setPhoto(visitor.getPhoto());
        response.setCheckoutPhoto(visitor.getCheckoutPhoto());
        response.setIdProof(visitor.getIdProof());
        response.setTagNumber(visitor.getTagNumber());

        // ✅ REMOVE these conversions - they're not needed
        // Database already stores in IST because of timezone settings
        response.setCheckInTime(visitor.getCheckInTime());
        response.setCheckOutTime(visitor.getCheckOutTime());

        response.setActive(visitor.isActive());
        response.setMeetingStatus(
                visitor.getMeetingStatus() != null ? visitor.getMeetingStatus().toString() : "PENDING");

        userRepository.findFirstByEmail(visitor.getPersonToMeet()).ifPresent(emp -> {
            UserDto dto = new UserDto();
            dto.setId(emp.getId());
            dto.setEmpId(emp.getEmpId());
            dto.setName(emp.getName());
            dto.setEmail(emp.getEmail());
            dto.setDesignation(emp.getDesignation());
            dto.setPhoto(emp.getPhoto());
            dto.setRole(emp.getRole().toString());
            response.setPersonToMeetDetails(dto);
        });

        return response;
    }

    private TagResponse mapToTagResponse(TagNumber tagNumber) {
        TagResponse response = new TagResponse();
        response.setId(tagNumber.getId());
        response.setTagNumber(tagNumber.getTagNumber());
        response.setAvailable(tagNumber.isAvailable());
        response.setAssignedToVisitorId(tagNumber.getAssignedToVisitorId());
        response.setAssignedAt(tagNumber.getAssignedAt());
        response.setReleasedAt(tagNumber.getReleasedAt());
        return response;
    }
}