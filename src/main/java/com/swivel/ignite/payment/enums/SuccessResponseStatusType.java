package com.swivel.ignite.payment.enums;

import lombok.Getter;

/**
 * Enum values for Success Response
 */
@Getter
public enum SuccessResponseStatusType {

    MADE_PAYMENT(2000, "Successfully made the payment"),
    READ_PAYMENT(2001, "Successfully read the payment"),
    DELETE_PAYMENT(2002, "Successfully deleted the payment");

    private final int code;
    private final String message;

    SuccessResponseStatusType(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
