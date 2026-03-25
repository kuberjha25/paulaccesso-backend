package com.paulaccesso.visitor.repository;

import com.paulaccesso.visitor.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    List<Visitor> findByUserIdOrderByCheckInTimeDesc(Long userId);
    
    @Query("SELECT COUNT(v) FROM Visitor v WHERE DATE(v.checkInTime) = CURRENT_DATE")
    long countTodayVisitors();
    
    @Query("SELECT COUNT(v) FROM Visitor v WHERE v.checkOutTime IS NULL")
    long countActiveVisitors();
    
    @Query("SELECT v FROM Visitor v WHERE v.checkOutTime IS NULL ORDER BY v.checkInTime DESC")
    List<Visitor> findActiveVisitors();
}