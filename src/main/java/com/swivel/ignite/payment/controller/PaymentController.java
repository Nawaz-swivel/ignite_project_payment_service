package com.swivel.ignite.payment.controller;

import com.swivel.ignite.payment.dto.request.PaymentCreateRequestDto;
import com.swivel.ignite.payment.dto.response.PaymentResponseDto;
import com.swivel.ignite.payment.dto.response.StudentResponseDto;
import com.swivel.ignite.payment.dto.response.StudentsIdListResponseDto;
import com.swivel.ignite.payment.entity.Payment;
import com.swivel.ignite.payment.enums.ErrorResponseStatusType;
import com.swivel.ignite.payment.enums.Month;
import com.swivel.ignite.payment.enums.SuccessResponseStatusType;
import com.swivel.ignite.payment.exception.*;
import com.swivel.ignite.payment.service.PaymentService;
import com.swivel.ignite.payment.service.RegistrationService;
import com.swivel.ignite.payment.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Payment Controller
 */
@RestController
@RequestMapping("api/v1/payment")
@Slf4j
public class PaymentController extends Controller {

    private final PaymentService paymentService;
    private final RegistrationService registrationService;

    @Autowired
    public PaymentController(PaymentService paymentService, RegistrationService registrationService) {
        this.paymentService = paymentService;
        this.registrationService = registrationService;
    }

    /**
     * This method used to make a tuition payment of a student
     *
     * @param requestDto payment create request dto
     * @return success/ error response
     */
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> makeTuitionPayment(@RequestBody PaymentCreateRequestDto requestDto) {
        try {
            if (!requestDto.isRequiredAvailable()) {
                log.error("Required fields missing in payment create request DTO for make tuition payment");
                return getBadRequestResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            StudentResponseDto studentResponseDto = registrationService.getStudentInfo(requestDto.getStudentId());
            Payment payment = paymentService.makeTuitionPayment(requestDto, studentResponseDto);
            log.debug("Made payment for studentId: {}, tuitionId: {}", requestDto.getStudentId(), requestDto
                    .getTuitionId());
            PaymentResponseDto responseDto = new PaymentResponseDto(payment);
            return getSuccessResponse(SuccessResponseStatusType.MADE_PAYMENT, responseDto);
        } catch (StudentNotEnrolledInTuitionException e) {
            log.error("Student is not enrolled in the tuition for make tuition payment", e);
            return getBadRequestResponse(ErrorResponseStatusType.STUDENT_NOT_ENROLLED_IN_TUITION);
        } catch (PaymentMonthInvalidException e) {
            log.error("Student is not eligible to pay for the month: {}, as he was not enrolled in the class" +
                    "at this time", requestDto.getMonth(), e);
            return getBadRequestResponse(ErrorResponseStatusType.INVALID_PAYMENT_MONTH);
        } catch (PaymentAlreadyMadeException e) {
            log.error("Payment is already made", e);
            return getBadRequestResponse(ErrorResponseStatusType.PAYMENT_ALREADY_MADE);
        } catch (RegistrationServiceHttpClientErrorException e) {
            log.error("Failed to get student info from Registration Micro Service.", e);
            return getInternalServerErrorResponse(ErrorResponseStatusType.REGISTRATION_INTERNAL_SERVER_ERROR,
                    e.responseBody);
        } catch (PaymentServiceException | IOException e) {
            log.error("Making tuition payment was failed for requestDto: {}", requestDto.toLogJson(), e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method is used to get all payments by tuitionId and month
     *
     * @param tuitionId tuition id
     * @param month     month
     * @return success(student id list)/ error response
     */
    @GetMapping(path = "/get/all/{tuitionId}/{month}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> getAllPaymentByTuitionIdAndMonth(@PathVariable(name = "tuitionId")
                                                                            String tuitionId,
                                                                            @PathVariable(name = "month")
                                                                            String month) {
        try {
            List<String> studentIds = paymentService.getAllStudentIdByTuitionIdAndMonth(tuitionId, Month.valueOf(month));
            StudentsIdListResponseDto responseDto = new StudentsIdListResponseDto(studentIds);
            log.debug("Returned all the payment by tuition id: {}, month: {}", tuitionId, month);
            return getSuccessResponse(SuccessResponseStatusType.READ_PAYMENT, responseDto);
        } catch (PaymentServiceException e) {
            log.error("Get all payment was failed for tuitionId: {}, month: {}", tuitionId, month, e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method is used to delete all tuition by id
     *
     * @param tuitionId tuition id
     * @return success/ error response
     */
    @DeleteMapping(path = "/delete/all/tuition/{tuitionId}")
    public ResponseEntity<ResponseWrapper> deleteAllByTuitionId(@PathVariable(name = "tuitionId") String tuitionId) {
        try {
            paymentService.deleteAllByTuitionId(tuitionId);
            log.debug("Successfully deleted all tuition by id: {}", tuitionId);
            return getSuccessResponse(SuccessResponseStatusType.DELETE_PAYMENT, null);
        } catch (PaymentServiceException e) {
            log.error("Deleting all payments by tuitionId was failed for tuitionId: {}", tuitionId, e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method is used to delete all students by id
     *
     * @param studentId student id
     * @return success/ error response
     */
    @DeleteMapping(path = "/delete/all/student/{studentId}")
    public ResponseEntity<ResponseWrapper> deleteAllByStudentId(@PathVariable(name = "studentId") String studentId) {
        try {
            paymentService.deleteAllByStudentId(studentId);
            log.debug("Successfully deleted all students by id: {}", studentId);
            return getSuccessResponse(SuccessResponseStatusType.DELETE_PAYMENT, null);
        } catch (PaymentServiceException e) {
            log.error("Deleting all payments by studentId was failed for studentId: {}", studentId, e);
            return getInternalServerErrorResponse();
        }
    }
}
