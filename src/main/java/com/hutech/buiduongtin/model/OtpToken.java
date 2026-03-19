package com.hutech.buiduongtin.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "otp_tokens")
public class OtpToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 6, nullable = false)
    private String code;

    private int amount; // voucher amount the OTP is for

    private int requiredPoints;

    private boolean used = false;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
