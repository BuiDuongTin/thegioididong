package com.hutech.buiduongtin.repository;

import com.hutech.buiduongtin.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    void deleteByProductId(Long productId);
}
