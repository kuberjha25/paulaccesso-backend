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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Visitor visitor = new Visitor();
        visitor.setName(request.getName());
        visitor.setMobile(request.getMobile());
        visitor.setEmail(request.getEmail());
        visitor.setCompany(request.getCompany());
        visitor.setAddress(request.getAddress());
        visitor.setPersonToMeet(request.getPersonToMeet());
        visitor.setPurpose(request.getPurpose());
        visitor.setPhoto(request.getPhoto());
        visitor.setIdProof(request.getIdProof());
        visitor.setCheckInTime(LocalDateTime.now());
        visitor.setActive(true);
        visitor.setMeetingStatus(MeetingStatus.PENDING);
        visitor.setUser(user);
        
        // Set tag number if provided in request (optional during registration)
        if (request.getTagNumber() != null && !request.getTagNumber().isEmpty()) {
            // Check if tag exists and is available
            TagNumber tagNumber = tagNumberRepository.findByTagNumber(request.getTagNumber())
                    .orElseThrow(() -> new RuntimeException("Tag number not found"));
            
            if (!tagNumber.isAvailable()) {
                throw new RuntimeException("Tag number is already assigned to another visitor");
            }
            
            // Assign tag
            tagNumberRepository.assignTagToVisitor(request.getTagNumber(), null);
            visitor.setTagNumber(request.getTagNumber());
        }

        Visitor savedVisitor = visitorRepository.save(visitor);
        log.info("Visitor registered: {} by {}", savedVisitor.getName(), user.getEmail());

        sendNotificationToEmployee(request.getPersonToMeet(), savedVisitor);

        return mapToResponse(savedVisitor);
    }

    private void sendNotificationToEmployee(String employeeEmail, Visitor visitor) {
        userRepository.findByEmail(employeeEmail).ifPresent(employee -> {
            emailService.sendVisitorNotification(employee, visitor);
            log.info("Notification sent to {} for visitor: {}", employeeEmail, visitor.getName());
        });
    }

    @Transactional
    public VisitorResponse updateMeetingStatus(Long visitorId, String status) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));

        MeetingStatus meetingStatus = MeetingStatus.valueOf(status);
        visitor.setMeetingStatus(meetingStatus);
        Visitor updatedVisitor = visitorRepository.save(visitor);

        userRepository.findByEmail(visitor.getPersonToMeet()).ifPresent(employee -> {
            emailService.sendMeetingStatusUpdate(employee, visitor, status);
        });

        return mapToResponse(updatedVisitor);
    }

    @Transactional
    public VisitorResponse assignTagToVisitor(TagAssignmentRequest request) {
        // Check if visitor exists
        Visitor visitor = visitorRepository.findById(request.getVisitorId())
                .orElseThrow(() -> new RuntimeException("Visitor not found"));
        
        // Check if visitor is active
        if (!visitor.isActive()) {
            throw new RuntimeException("Cannot assign tag to checked out visitor");
        }
        
        // Check if visitor already has a tag
        if (visitor.getTagNumber() != null && !visitor.getTagNumber().isEmpty()) {
            throw new RuntimeException("Visitor already has a tag: " + visitor.getTagNumber());
        }
        
        // Check if tag exists and is available
        TagNumber tagNumber = tagNumberRepository.findByTagNumber(request.getTagNumber())
                .orElseThrow(() -> new RuntimeException("Tag number not found"));
        
        if (!tagNumber.isAvailable()) {
            throw new RuntimeException("Tag number is already assigned to another visitor");
        }
        
        // Assign tag
        tagNumberRepository.assignTagToVisitor(request.getTagNumber(), visitor.getId());
        
        // Update visitor with tag number
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
        
        // Release tag if visitor has one
        if (visitor.getTagNumber() != null && !visitor.getTagNumber().isEmpty()) {
            tagNumberRepository.releaseTag(visitor.getTagNumber());
            log.info("Tag {} released from visitor: {}", visitor.getTagNumber(), visitor.getName());
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

            List<Visitor> visitors;
            if (user.getRole() == Role.ADMIN) {
                visitors = visitorRepository.findAll();
            } else {
                visitors = visitorRepository.findAll();
            }

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
        response.setCheckInTime(visitor.getCheckInTime());
        response.setCheckOutTime(visitor.getCheckOutTime());
        response.setActive(visitor.isActive());
        response.setMeetingStatus(
                visitor.getMeetingStatus() != null ? visitor.getMeetingStatus().toString() : "PENDING");

        userRepository.findByEmail(visitor.getPersonToMeet()).ifPresent(emp -> {
            UserDto dto = new UserDto();
            dto.setId(emp.getId());
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