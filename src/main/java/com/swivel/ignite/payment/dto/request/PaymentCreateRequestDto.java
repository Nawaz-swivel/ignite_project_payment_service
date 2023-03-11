package com.swivel.ignite.payment.dto.request;

import com.swivel.ignite.payment.enums.Month;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for Payment creation request
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateRequestDto extends RequestDto {

    private String studentId;
    private String tuitionId;
    private Month month;

    @Override
    public String toLogJson() {
        return toJson();
    }

    @Override
    public boolean isRequiredAvailable() {
        return isNonEmpty(studentId) && isNonEmpty(tuitionId)
                && month != null;
    }
}
