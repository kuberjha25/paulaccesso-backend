// File: visitor/src/main/java/com/paulaccesso/visitor/entity/Visitor.java
package com.paulaccesso.visitor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    private String email;

    private String company;

    @Column(length = 500)
    private String address;

    @Column(name = "person_to_meet", nullable = false)
    private String personToMeet;

    @Column(nullable = false)
    private String purpose;

    @Column(columnDefinition = "LONGTEXT")
    private String photo;
    
    @Column(columnDefinition = "LONGTEXT")
    private String checkoutPhoto;
    
    @Column(columnDefinition = "LONGTEXT")
    private String idProof;

    @Column(name = "tag_number", unique = true)
    private String tagNumber; // NEW FIELD

    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "is_active")
    private boolean active = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_status")
    private MeetingStatus meetingStatus = MeetingStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}