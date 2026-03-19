package com.hutech.buiduongtin.repository;

import com.hutech.buiduongtin.model.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findByIdAndCode(Long id, String code);
}
