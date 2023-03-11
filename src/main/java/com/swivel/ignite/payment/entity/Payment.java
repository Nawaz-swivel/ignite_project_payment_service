package com.swivel.ignite.payment.entity;

import com.swivel.ignite.payment.dto.request.PaymentCreateRequestDto;
import com.swivel.ignite.payment.enums.Month;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Payment entity
 */
@Entity
@Table(name = "payment")
@NoArgsConstructor
@Getter
@Setter
public class Payment implements Serializable {

    @Transient
    private static final String PAYMENT_ID_PREFIX = "pid-";

    @Id
    private String id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Month month;
    @Column(nullable = false)
    private boolean paid;
    @Column(nullable = false)
    private String studentId;
    @Column(nullable = false)
    private String tuitionId;

    public Payment(PaymentCreateRequestDto requestDto, boolean paid) {
        this.id = PAYMENT_ID_PREFIX + UUID.randomUUID();
        this.studentId = requestDto.getStudentId();
        this.tuitionId = requestDto.getTuitionId();
        this.paid = paid;
        this.month = requestDto.getMonth();
    }
}
