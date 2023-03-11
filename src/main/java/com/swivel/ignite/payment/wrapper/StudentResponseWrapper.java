package com.swivel.ignite.payment.wrapper;

import com.swivel.ignite.payment.dto.BaseDto;
import com.swivel.ignite.payment.dto.response.StudentResponseDto;
import com.swivel.ignite.payment.enums.ResponseStatusType;
import lombok.Getter;
import lombok.Setter;

/**
 * Student response wrapper
 */
@Getter
@Setter
public class StudentResponseWrapper implements BaseDto {

    private ResponseStatusType status;
    private String message;
    private StudentResponseDto data;
    private String displayMessage;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
