package com.swivel.ignite.payment.dto.response;

import com.swivel.ignite.payment.entity.Payment;
import com.swivel.ignite.payment.enums.Month;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Payment DTO for response
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto extends ResponseDto {

    private String id;
    private String studentId;
    private String tuitionId;
    private Month month;
    private boolean paid;

    public PaymentResponseDto(Payment payment) {
        this.id = payment.getId();
        this.studentId = payment.getStudentId();
        this.tuitionId = payment.getTuitionId();
        this.month = payment.getMonth();
        this.paid = payment.isPaid();
    }
}
