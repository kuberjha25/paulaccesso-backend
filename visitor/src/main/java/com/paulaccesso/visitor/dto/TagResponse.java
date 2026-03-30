package com.paulaccesso.visitor.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TagResponse {
    private Long id;
    private String tagNumber;
    private boolean isAvailable;
    private Long assignedToVisitorId;
    private LocalDateTime assignedAt;
    private LocalDateTime releasedAt;
}
