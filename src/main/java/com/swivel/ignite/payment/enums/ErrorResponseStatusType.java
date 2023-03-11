package com.swivel.ignite.payment.enums;

import lombok.Getter;

/**
 * Enum values for Error Response
 */
@Getter
public enum ErrorResponseStatusType {

    MISSING_REQUIRED_FIELDS(4001, "Missing required fields"),
    STUDENT_NOT_ENROLLED_IN_TUITION(4002, "Student not enrolled in tuition"),
    INVALID_PAYMENT_MONTH(4003, "Invalid payment month"),
    PAYMENT_ALREADY_MADE(4004, "Payment to the given details have been made already"),
    INTERNAL_SERVER_ERROR(5000, "Internal Server Error"),
    REGISTRATION_INTERNAL_SERVER_ERROR(5001, "Registration Service - Internal Server Error");

    private final int code;
    private final String message;

    ErrorResponseStatusType(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
