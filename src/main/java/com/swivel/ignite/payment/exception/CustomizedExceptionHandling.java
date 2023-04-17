package com.swivel.ignite.payment.exception;

import com.swivel.ignite.payment.enums.ErrorResponseStatusType;
import com.swivel.ignite.payment.enums.ResponseStatusType;
import com.swivel.ignite.payment.wrapper.ErrorResponseWrapper;
import com.swivel.ignite.payment.wrapper.ResponseWrapper;
import com.swivel.ignite.payment.wrapper.RestErrorResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class CustomizedExceptionHandling extends ResponseEntityExceptionHandler {

    private static final String ERROR_MESSAGE = "Oops!! Something went wrong. Please try again.";

    /**
     * This method returns an error response for payment service exceptions
     *
     * @param exception exception
     * @return error response
     */
    @ExceptionHandler(PaymentServiceException.class)
    public ResponseEntity<ResponseWrapper> handlePaymentServiceException(PaymentServiceException exception) {
        ResponseWrapper responseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR, ErrorResponseStatusType
                .INTERNAL_SERVER_ERROR.getMessage(), null, ERROR_MESSAGE, ErrorResponseStatusType
                .INTERNAL_SERVER_ERROR.getCode());
        log.error(exception.getMessage());
        return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * This method returns an error response for student not enrolled in tuition exceptions
     *
     * @param exception exception
     * @return error response
     */
    @ExceptionHandler(StudentNotEnrolledInTuitionException.class)
    public ResponseEntity<ResponseWrapper> handleStudentNotEnrolledInTuitionException(
            StudentNotEnrolledInTuitionException exception) {
        ResponseWrapper responseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR, ErrorResponseStatusType
                .STUDENT_NOT_ENROLLED_IN_TUITION.getMessage(), null, ERROR_MESSAGE,
                ErrorResponseStatusType.STUDENT_NOT_ENROLLED_IN_TUITION.getCode());
        log.error(exception.getMessage());
        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method returns an error response for payment month invalid exceptions
     *
     * @param exception exception
     * @return error response
     */
    @ExceptionHandler(PaymentMonthInvalidException.class)
    public ResponseEntity<ResponseWrapper> handlePaymentMonthInvalidException(PaymentMonthInvalidException exception) {
        ResponseWrapper responseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR, ErrorResponseStatusType
                .INVALID_PAYMENT_MONTH.getMessage(), null, ERROR_MESSAGE, ErrorResponseStatusType
                .INVALID_PAYMENT_MONTH.getCode());
        log.error(exception.getMessage());
        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method returns an error response for payment already made exceptions
     *
     * @param exception exception
     * @return error response
     */
    @ExceptionHandler(PaymentAlreadyMadeException.class)
    public ResponseEntity<ResponseWrapper> handlePaymentAlreadyMadeException(PaymentAlreadyMadeException exception) {
        ResponseWrapper responseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR, ErrorResponseStatusType
                .PAYMENT_ALREADY_MADE.getMessage(), null, ERROR_MESSAGE, ErrorResponseStatusType
                .PAYMENT_ALREADY_MADE.getCode());
        log.error(exception.getMessage());
        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method returns an error response for student microservice call exceptions
     *
     * @param exception exception
     * @return error response
     */
    @ExceptionHandler(StudentServiceHttpClientErrorException.class)
    public ResponseEntity<ResponseWrapper> handleStudentServiceHttpClientErrorException(
            StudentServiceHttpClientErrorException exception) {
        ResponseWrapper responseWrapper = new RestErrorResponseWrapper(ResponseStatusType.ERROR,
                ErrorResponseStatusType.STUDENT_INTERNAL_SERVER_ERROR.getMessage(), exception.responseBody,
                ERROR_MESSAGE, ErrorResponseStatusType.STUDENT_INTERNAL_SERVER_ERROR.getCode());
        log.error(exception.getMessage());
        return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
