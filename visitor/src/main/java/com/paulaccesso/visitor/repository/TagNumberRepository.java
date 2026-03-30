package com.paulaccesso.visitor.repository;

import com.paulaccesso.visitor.entity.TagNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TagNumberRepository extends JpaRepository<TagNumber, Long> {
    
    Optional<TagNumber> findByTagNumberAndIsAvailableTrue(String tagNumber);
    
    Optional<TagNumber> findByTagNumber(String tagNumber);
    
    List<TagNumber> findByIsAvailableTrue();
    
    @Modifying
    @Transactional
    @Query("UPDATE TagNumber t SET t.isAvailable = false, t.assignedToVisitorId = :visitorId, t.assignedAt = CURRENT_TIMESTAMP WHERE t.tagNumber = :tagNumber AND t.isAvailable = true")
    int assignTagToVisitor(String tagNumber, Long visitorId);
    
    @Modifying
    @Transactional
    @Query("UPDATE TagNumber t SET t.isAvailable = true, t.assignedToVisitorId = null, t.releasedAt = CURRENT_TIMESTAMP WHERE t.tagNumber = :tagNumber")
    int releaseTag(String tagNumber);
    
    @Modifying
    @Transactional
    @Query("UPDATE TagNumber t SET t.isAvailable = true, t.assignedToVisitorId = null, t.releasedAt = CURRENT_TIMESTAMP WHERE t.assignedToVisitorId = :visitorId")
    int releaseTagByVisitorId(Long visitorId);
    
    Optional<TagNumber> findByAssignedToVisitorId(Long visitorId);
}
