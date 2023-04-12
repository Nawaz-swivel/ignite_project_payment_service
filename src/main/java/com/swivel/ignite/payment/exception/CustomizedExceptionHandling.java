package com.swivel.ignite.payment.exception;

import com.swivel.ignite.payment.enums.ErrorResponseStatusType;
import com.swivel.ignite.payment.enums.ResponseStatusType;
import com.swivel.ignite.payment.wrapper.ErrorResponseWrapper;
import com.swivel.ignite.payment.wrapper.ResponseWrapper;
import com.swivel.ignite.payment.wrapper.RestErrorResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomizedExceptionHandling extends ResponseEntityExceptionHandler {

    private static final String ERROR_MESSAGE = "Oops!! Something went wrong. Please try again.";

    @ExceptionHandler(PaymentServiceException.class)
    public ResponseEntity<ResponseWrapper> handlePaymentServiceException(PaymentServiceException exception,
                                                                         WebRequest request) {
        ResponseWrapper responseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR, ErrorResponseStatusType
                .INTERNAL_SERVER_ERROR.getMessage(), null, ERROR_MESSAGE, ErrorResponseStatusType
                .INTERNAL_SERVER_ERROR.getCode());
        return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(StudentNotEnrolledInTuitionException.class)
    public ResponseEntity<ResponseWrapper> handleStudentNotEnrolledInTuitionException(
            StudentNotEnrolledInTuitionException exception, WebRequest request) {
        ResponseWrapper responseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR, ErrorResponseStatusType
                .STUDENT_NOT_ENROLLED_IN_TUITION.getMessage(), null, ERROR_MESSAGE,
                ErrorResponseStatusType.STUDENT_NOT_ENROLLED_IN_TUITION.getCode());
        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentMonthInvalidException.class)
    public ResponseEntity<ResponseWrapper> handlePaymentMonthInvalidException(PaymentMonthInvalidException exception,
                                                                              WebRequest request) {
        ResponseWrapper responseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR, ErrorResponseStatusType
                .INVALID_PAYMENT_MONTH.getMessage(), null, ERROR_MESSAGE, ErrorResponseStatusType
                .INVALID_PAYMENT_MONTH.getCode());
        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentAlreadyMadeException.class)
    public ResponseEntity<ResponseWrapper> handlePaymentAlreadyMadeException(PaymentAlreadyMadeException exception,
                                                                             WebRequest request) {
        ResponseWrapper responseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR, ErrorResponseStatusType
                .PAYMENT_ALREADY_MADE.getMessage(), null, ERROR_MESSAGE, ErrorResponseStatusType
                .PAYMENT_ALREADY_MADE.getCode());
        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StudentServiceHttpClientErrorException.class)
    public ResponseEntity<ResponseWrapper> handleStudentServiceHttpClientErrorException(
            StudentServiceHttpClientErrorException exception, WebRequest request) {
        ResponseWrapper responseWrapper = new RestErrorResponseWrapper(ResponseStatusType.ERROR,
                ErrorResponseStatusType.STUDENT_INTERNAL_SERVER_ERROR.getMessage(), exception.responseBody,
                ERROR_MESSAGE, ErrorResponseStatusType.STUDENT_INTERNAL_SERVER_ERROR.getCode());
        return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
