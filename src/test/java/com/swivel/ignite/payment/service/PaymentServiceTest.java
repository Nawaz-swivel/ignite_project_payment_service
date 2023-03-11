package com.swivel.ignite.payment.service;

import com.swivel.ignite.payment.dto.request.PaymentCreateRequestDto;
import com.swivel.ignite.payment.dto.response.StudentResponseDto;
import com.swivel.ignite.payment.entity.Payment;
import com.swivel.ignite.payment.enums.Month;
import com.swivel.ignite.payment.exception.PaymentAlreadyMadeException;
import com.swivel.ignite.payment.exception.PaymentMonthInvalidException;
import com.swivel.ignite.payment.exception.PaymentServiceException;
import com.swivel.ignite.payment.exception.StudentNotEnrolledInTuitionException;
import com.swivel.ignite.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * This class tests {@link PaymentService} class
 */
class PaymentServiceTest {

    private static final String TUITION_ID = "tid-123456789";
    private static final String STUDENT_ID = "sid-123456789";
    private static final Month MONTH_MARCH = Month.MARCH;
    private static final long TUITION_JOINED = 1677609000000L;
    private static final String ERROR = "ERROR";
    private PaymentService paymentService;
    @Mock
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        initMocks(this);
        paymentService = new PaymentService(paymentRepository);
    }

    /**
     * Start of tests for makeTuitionPayment method
     */
    @Test
    void Should_ReturnPayment_When_MakingTuitionPaymentIsSuccessful() {
        when(paymentRepository.existsByTuitionIdAndMonthAndStudentId(anyString(), any(Month.class), anyString()))
                .thenReturn(false);
        paymentService.makeTuitionPayment(getSamplePaymentCreateRequestDto(), getSampleStudentResponseDto());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void Should_ThrowStudentNotEnrolledInTuitionException_When_MakingTuitionPaymentForStudentNotEnrolledInTuition() {
        PaymentCreateRequestDto paymentCreateRequestDto = getSamplePaymentCreateRequestDto();
        StudentResponseDto studentResponseDto = getSampleStudentResponseDto();
        studentResponseDto.setTuitionId(null);

        StudentNotEnrolledInTuitionException exception = assertThrows(StudentNotEnrolledInTuitionException.class, () ->
                paymentService.makeTuitionPayment(paymentCreateRequestDto, studentResponseDto));
        assertEquals("Student not enrolled in tuition", exception.getMessage());
    }

    @Test
    void Should_ThrowPaymentMonthInvalidException_When_MakingTuitionPaymentForPaymentMonthInvalid() {
        PaymentCreateRequestDto requestDto = getSamplePaymentCreateRequestDto();
        requestDto.setMonth(Month.JANUARY);
        StudentResponseDto responseDto = getSampleStudentResponseDto();

        PaymentMonthInvalidException exception = assertThrows(PaymentMonthInvalidException.class, () ->
                paymentService.makeTuitionPayment(requestDto, responseDto));
        assertEquals("Payment month invalid", exception.getMessage());
    }

    @Test
    void Should_ThrowPaymentAlreadyMadeException_When_MakingTuitionPaymentForPaymentAlreadyMade() {
        PaymentCreateRequestDto requestDto = getSamplePaymentCreateRequestDto();
        StudentResponseDto responseDto = getSampleStudentResponseDto();
        when(paymentRepository.existsByTuitionIdAndMonthAndStudentId(anyString(), any(Month.class), anyString()))
                .thenReturn(true);
        PaymentAlreadyMadeException exception = assertThrows(PaymentAlreadyMadeException.class, () ->
                paymentService.makeTuitionPayment(requestDto, responseDto));
        assertEquals("Payment already made", exception.getMessage());
    }

    @Test
    void Should_ThrowPaymentServiceException_When_MakingTuitionPaymentForFailedToCheckWhetherPaymentAlreadyMade() {
        PaymentCreateRequestDto requestDto = getSamplePaymentCreateRequestDto();
        StudentResponseDto responseDto = getSampleStudentResponseDto();

        when(paymentRepository.existsByTuitionIdAndMonthAndStudentId(anyString(), any(Month.class), anyString()))
                .thenThrow(new DataAccessException(ERROR) {
                });
        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
                paymentService.makeTuitionPayment(requestDto, responseDto));
        assertEquals("Failed to check whether payment already made", exception.getMessage());
    }

    @Test
    void Should_ThrowPaymentServiceException_When_MakingTuitionPaymentIsFailed() {
        PaymentCreateRequestDto requestDto = getSamplePaymentCreateRequestDto();
        StudentResponseDto responseDto = getSampleStudentResponseDto();

        when(paymentRepository.existsByTuitionIdAndMonthAndStudentId(anyString(), any(Month.class), anyString()))
                .thenReturn(false);
        doThrow(new DataAccessException(ERROR) {
        }).when(paymentRepository).save(any(Payment.class));
        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
                paymentService.makeTuitionPayment(requestDto, responseDto));
        assertEquals("Failed to make tuition payment for studentId: " + STUDENT_ID + ", tuitionId: " +
                TUITION_ID, exception.getMessage());
    }

    /**
     * Start of tests for getAllStudentIdByTuitionIdAndMonth method
     */
    @Test
    void Should_ReturnStudentIdList_When_GettingAllStudentIdByTuitionIdAndMonthIsSuccessful() {
        when(paymentRepository.getAllStudentIdByTuitionIdAndMonth(anyString(), any(Month.class)))
                .thenReturn(getSampleStudentIdList());
        assertEquals(STUDENT_ID, paymentService.getAllStudentIdByTuitionIdAndMonth(TUITION_ID, Month.JANUARY).get(0));
    }

    @Test
    void Should_ThrowPaymentServiceException_When_GettingAllStudentIdByTuitionIdAndMonthIsFailed() {
        when(paymentRepository.getAllStudentIdByTuitionIdAndMonth(anyString(), any(Month.class)))
                .thenThrow(new DataAccessException(ERROR) {
                });
        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
                paymentService.getAllStudentIdByTuitionIdAndMonth(TUITION_ID, Month.JANUARY));
        assertEquals("Failed to get all payments by tuitionId and month", exception.getMessage());
    }

    /**
     * Start of tests for deleteAllByTuitionId method
     */
    @Test
    void Should_DeleteAllByTuitionId_When_DeletingAllByTuitionIdIsSuccessful() {
        doNothing().when(paymentRepository).deleteAllByTuitionId(anyString());
        paymentService.deleteAllByTuitionId(TUITION_ID);
        verify(paymentRepository).deleteAllByTuitionId(anyString());
    }

    @Test
    void Should_ThrowPaymentServiceException_When_DeletingAllByTuitionIdIsFailed() {
        doThrow(new DataAccessException(ERROR) {
        }).when(paymentRepository).deleteAllByTuitionId(anyString());
        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
                paymentService.deleteAllByTuitionId(TUITION_ID));
        assertEquals("Failed to delete all payment by tuitionId for tuitionId: " + TUITION_ID,
                exception.getMessage());
    }

    /**
     * Start of tests for deleteAllByStudentId method
     */
    @Test
    void Should_DeleteAllByStudentId_When_DeletingAllByStudentIdIsSuccessful() {
        doNothing().when(paymentRepository).deleteAllByStudentId(anyString());
        paymentService.deleteAllByStudentId(STUDENT_ID);
        verify(paymentRepository).deleteAllByStudentId(anyString());
    }

    @Test
    void Should_ThrowPaymentServiceException_When_DeletingAllByStudentIdIsFailed() {
        doThrow(new DataAccessException(ERROR) {
        }).when(paymentRepository).deleteAllByStudentId(anyString());
        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
                paymentService.deleteAllByStudentId(STUDENT_ID));
        assertEquals("Failed to delete all payment by studentId for studentId: " + STUDENT_ID,
                exception.getMessage());
    }

    /**
     * This method returns a sample student id list
     *
     * @return student id list
     */
    private List<String> getSampleStudentIdList() {
        List<String> list = new ArrayList<>();
        list.add(STUDENT_ID);
        return list;
    }

    /**
     * This method returns a sample StudentResponseDto
     *
     * @return StudentResponseDto
     */
    private StudentResponseDto getSampleStudentResponseDto() {
        StudentResponseDto dto = new StudentResponseDto();
        dto.setTuitionId(TUITION_ID);
        dto.setTuitionJoinedOn(new Date(TUITION_JOINED));
        return dto;
    }

    /**
     * This method returns a sample PaymentCreateRequestDto
     *
     * @return PaymentCreateRequestDto
     */
    private PaymentCreateRequestDto getSamplePaymentCreateRequestDto() {
        PaymentCreateRequestDto dto = new PaymentCreateRequestDto();
        dto.setTuitionId(TUITION_ID);
        dto.setStudentId(STUDENT_ID);
        dto.setMonth(MONTH_MARCH);
        return dto;
    }
}
