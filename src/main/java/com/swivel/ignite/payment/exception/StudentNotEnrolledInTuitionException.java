package com.swivel.ignite.payment.exception;

/**
 * StudentNotEnrolledInTuition Exception
 */
public class StudentNotEnrolledInTuitionException extends RuntimeException {

    /**
     * StudentNotEnrolledInTuition Exception with error message.
     *
     * @param errorMessage error message
     */
    public StudentNotEnrolledInTuitionException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * StudentNotEnrolledInTuition Exception with error message and throwable error
     *
     * @param errorMessage error message
     * @param error        error
     */
    public StudentNotEnrolledInTuitionException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
