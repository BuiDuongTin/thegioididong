package com.hutech.buiduongtin.repository;

import com.hutech.buiduongtin.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByPhoneNumberOrderByIdDesc(String phoneNumber);

    List<Order> findByUserUsernameOrderByIdDesc(String username);

    @Query("SELECT COALESCE(SUM(o.earnedPoints), 0) - COALESCE(SUM(o.usedPoints), 0) FROM Order o WHERE o.phoneNumber = :phoneNumber AND o.user IS NULL")
    int getAvailablePoints(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT COALESCE(SUM(o.earnedPoints), 0) - COALESCE(SUM(o.usedPoints), 0) FROM Order o WHERE o.user.username = :username")
    int getAvailablePointsByUsername(@Param("username") String username);
}
