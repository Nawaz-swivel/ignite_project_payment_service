package com.swivel.ignite.payment.enums;

import lombok.Getter;

/**
 * Enum values for Error Response
 */
@Getter
public enum ErrorResponseStatusType {

    MISSING_REQUIRED_FIELDS(400, "Missing required fields"),
    STUDENT_NOT_ENROLLED_IN_TUITION(400, "Student not enrolled in tuition"),
    INVALID_PAYMENT_MONTH(400, "Invalid payment month"),
    PAYMENT_ALREADY_MADE(400, "Payment to the given details have been made already"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    STUDENT_INTERNAL_SERVER_ERROR(500, "Student Service - Internal Server Error");

    private final int code;
    private final String message;

    ErrorResponseStatusType(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
