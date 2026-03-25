package com.paulaccesso.visitor.controller;

import com.paulaccesso.visitor.dto.CheckoutRequest;
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
}