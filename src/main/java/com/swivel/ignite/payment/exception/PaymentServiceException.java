package com.swivel.ignite.payment.exception;

/**
 * PaymentServiceException
 */
public class PaymentServiceException extends RuntimeException {

    /**
     * PaymentServiceException with error message.
     *
     * @param errorMessage error message
     */
    public PaymentServiceException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * PaymentServiceException with error message and throwable error
     *
     * @param errorMessage error message
     * @param error        error
     */
    public PaymentServiceException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
