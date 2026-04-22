package com.paulaccesso.visitor.controller;

import com.paulaccesso.visitor.dto.CheckoutRequest;
import com.paulaccesso.visitor.dto.TagAssignmentRequest;
import com.paulaccesso.visitor.dto.TagResponse;
import com.paulaccesso.visitor.dto.VisitorRequest;
import com.paulaccesso.visitor.dto.VisitorResponse;
import com.paulaccesso.visitor.service.VisitorService;
import com.paulaccesso.visitor.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/visitors")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<VisitorResponse> registerVisitor(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody VisitorRequest request) {

        String jwt = token.substring(7);
        Long userId = jwtService.getUserIdFromToken(jwt);

        VisitorResponse response = visitorService.registerVisitor(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/assign-tag")
    public ResponseEntity<VisitorResponse> assignTagToVisitor(@Valid @RequestBody TagAssignmentRequest request) {
        VisitorResponse response = visitorService.assignTagToVisitor(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tags/available")
    public ResponseEntity<List<TagResponse>> getAvailableTags() {
        return ResponseEntity.ok(visitorService.getAvailableTags());
    }

    @PutMapping("/{id}/checkout")
    public ResponseEntity<VisitorResponse> checkOutVisitor(
            @PathVariable Long id,
            @RequestBody(required = false) CheckoutRequest request) {

        VisitorResponse response = visitorService.checkOutVisitor(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<VisitorResponse>> getAllVisitors(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtService.getUserIdFromToken(jwt);

        List<VisitorResponse> visitors = visitorService.getAllVisitors(userId);
        return ResponseEntity.ok(visitors);
    }

    @GetMapping("/active")
    public ResponseEntity<List<VisitorResponse>> getActiveVisitors(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtService.getUserIdFromToken(jwt);

        List<VisitorResponse> visitors = visitorService.getActiveVisitors();
        return ResponseEntity.ok(visitors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitorResponse> getVisitorById(@PathVariable Long id) {
        VisitorResponse visitor = visitorService.getVisitorById(id);
        return ResponseEntity.ok(visitor);
    }

    @GetMapping("/stats/today")
    public ResponseEntity<Map<String, Long>> getTodayStats(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtService.getUserIdFromToken(jwt);

        long count = visitorService.getTodayVisitorCount();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/active")
    public ResponseEntity<Map<String, Long>> getActiveStats(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtService.getUserIdFromToken(jwt);

        long count = visitorService.getActiveVisitorCount();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // ✅ RESEND EMAIL ENDPOINT - POST method
    @PostMapping("/{id}/resend-email")
    public ResponseEntity<Map<String, String>> resendMeetingEmail(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            visitorService.resendMeetingEmail(id);
            response.put("message", "Meeting request email resent successfully");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }
}