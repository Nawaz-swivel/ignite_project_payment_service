package com.swivel.ignite.payment.exception;

/**
 * PaymentMonthInvalid Exception
 */
public class PaymentMonthInvalidException extends RuntimeException {

    /**
     * PaymentMonthInvalid Exception with error message.
     *
     * @param errorMessage error message
     */
    public PaymentMonthInvalidException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * PaymentMonthInvalid Exception with error message and throwable error
     *
     * @param errorMessage error message
     * @param error        error
     */
    public PaymentMonthInvalidException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
