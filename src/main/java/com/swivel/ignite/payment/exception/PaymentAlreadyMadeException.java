package com.swivel.ignite.payment.exception;

/**
 * PaymentAlreadyMadeException
 */
public class PaymentAlreadyMadeException extends RuntimeException {

    /**
     * PaymentAlreadyMadeException with error message.
     *
     * @param errorMessage error message
     */
    public PaymentAlreadyMadeException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * PaymentAlreadyMadeException with error message and throwable error
     *
     * @param errorMessage error message
     * @param error        error
     */
    public PaymentAlreadyMadeException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
