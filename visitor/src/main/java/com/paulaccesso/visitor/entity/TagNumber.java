// File: visitor/src/main/java/com/paulaccesso/visitor/entity/TagNumber.java
package com.paulaccesso.visitor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tag_numbers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_number", nullable = false, unique = true)
    private String tagNumber;

    @Column(name = "is_available")
    private boolean isAvailable = true;

    @Column(name = "assigned_to_visitor_id")
    private Long assignedToVisitorId;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "released_at")
    private LocalDateTime releasedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}