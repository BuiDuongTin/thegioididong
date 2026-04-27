package com.hutech.buiduongtin.model;

import com.hutech.buiduongtin.model.enums.PaymentMethod;
import com.hutech.buiduongtin.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_user_id", columnList = "user_id"),
        @Index(name = "idx_orders_phone_number", columnList = "phone_number")
})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String shippingAddress;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String note;
    private double totalPrice;
    private String paymentMethod;
    private String paymentStatus = PaymentStatus.PENDING.code();

    private int earnedPoints;
    private int usedPoints;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

    @PrePersist
    @PreUpdate
    private void normalizeStatusFields() {
        this.paymentStatus = PaymentStatus.fromCode(this.paymentStatus).code();
        this.paymentMethod = PaymentMethod.fromCode(this.paymentMethod).code();
    }

    public PaymentStatus getPaymentStatusEnum() {
        return PaymentStatus.fromCode(paymentStatus);
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = PaymentStatus.fromCode(paymentStatus).code();
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus == null ? PaymentStatus.PENDING.code() : paymentStatus.code();
    }

    public PaymentMethod getPaymentMethodEnum() {
        return PaymentMethod.fromCode(paymentMethod);
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = PaymentMethod.fromCode(paymentMethod).code();
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod == null ? PaymentMethod.COD.code() : paymentMethod.code();
    }
}
