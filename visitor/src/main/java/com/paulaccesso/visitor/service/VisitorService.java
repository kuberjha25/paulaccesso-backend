package com.paulaccesso.visitor.service;

import com.paulaccesso.visitor.dto.CheckoutRequest;
import com.paulaccesso.visitor.dto.VisitorRequest;
import com.paulaccesso.visitor.dto.VisitorResponse;
import com.paulaccesso.visitor.dto.UserDto;
import com.paulaccesso.visitor.entity.MeetingStatus;
import com.paulaccesso.visitor.entity.Role;
import com.paulaccesso.visitor.entity.User;
import com.paulaccesso.visitor.entity.Visitor;
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
    public VisitorResponse checkOutVisitor(Long visitorId, CheckoutRequest request) {
        Visitor visitor = visitorRepository.findById(visitorId)
            .orElseThrow(() -> new RuntimeException("Visitor not found"));
        
        visitor.setCheckOutTime(LocalDateTime.now());
        visitor.setActive(false);
        
        if (request != null && request.getCheckoutPhoto() != null) {
            visitor.setCheckoutPhoto(request.getCheckoutPhoto());
        }
        
        Visitor updatedVisitor = visitorRepository.save(visitor);
        log.info("Visitor checked out: {}", visitorId);
        
        return mapToResponse(updatedVisitor);
    }
    
    public List<VisitorResponse> getAllVisitors(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        
        if (user == null) return List.of();
        
        List<Visitor> visitors;
        if (user.getRole() == Role.ADMIN) {
            visitors = visitorRepository.findAll();
        } else {
            visitors = visitorRepository.findAll();
        }
        
        return visitors.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
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
        response.setCheckInTime(visitor.getCheckInTime());
        response.setCheckOutTime(visitor.getCheckOutTime());
        response.setActive(visitor.isActive());
        response.setMeetingStatus(visitor.getMeetingStatus() != null ? visitor.getMeetingStatus().toString() : "PENDING");
        
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
}