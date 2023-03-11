package com.swivel.ignite.payment.dto.response;

import com.swivel.ignite.payment.dto.BaseDto;

/**
 * ResponseDto - All responseDto classes are needed to extend this class.
 */
public class ResponseDto implements BaseDto {

    @Override
    public String toLogJson() {
        return toJson();
    }
}
