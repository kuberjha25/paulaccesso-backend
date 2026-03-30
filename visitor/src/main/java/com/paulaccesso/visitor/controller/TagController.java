// File: visitor/src/main/java/com/paulaccesso/visitor/controller/TagController.java
package com.paulaccesso.visitor.controller;

import com.paulaccesso.visitor.dto.TagAssignmentRequest;
import com.paulaccesso.visitor.dto.TagResponse;
import com.paulaccesso.visitor.dto.VisitorResponse;
import com.paulaccesso.visitor.service.VisitorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final VisitorService visitorService;

    // Get all available tags
    @GetMapping("/available")
    public ResponseEntity<List<TagResponse>> getAvailableTags() {
        return ResponseEntity.ok(visitorService.getAvailableTags());
    }

    // Get all tags
    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags() {
        return ResponseEntity.ok(visitorService.getAllTags());
    }

    // Assign tag to visitor
    @PostMapping("/assign")
    public ResponseEntity<VisitorResponse> assignTagToVisitor(@Valid @RequestBody TagAssignmentRequest request) {
        VisitorResponse response = visitorService.assignTagToVisitor(request);
        return ResponseEntity.ok(response);
    }
}