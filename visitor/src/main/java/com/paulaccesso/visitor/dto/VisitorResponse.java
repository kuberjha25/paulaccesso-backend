package com.paulaccesso.visitor.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VisitorResponse {
    private Long id;
    private String name;
    private String mobile;
    private String email;
    private String company;
    private String address;
    private String personToMeet;
    private String purpose;
    private String photo;
    private String checkoutPhoto;
    private String idProof;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private boolean active;
    private String meetingStatus;
    private UserDto personToMeetDetails;
}