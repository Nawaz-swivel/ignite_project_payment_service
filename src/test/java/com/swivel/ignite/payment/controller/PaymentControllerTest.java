package com.swivel.ignite.payment.controller;

import com.swivel.ignite.payment.dto.request.PaymentCreateRequestDto;
import com.swivel.ignite.payment.dto.response.StudentResponseDto;
import com.swivel.ignite.payment.entity.Payment;
import com.swivel.ignite.payment.enums.ErrorResponseStatusType;
import com.swivel.ignite.payment.enums.Month;
import com.swivel.ignite.payment.enums.SuccessResponseStatusType;
import com.swivel.ignite.payment.exception.*;
import com.swivel.ignite.payment.service.PaymentService;
import com.swivel.ignite.payment.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class tests {@link PaymentController} class
 */
class PaymentControllerTest {

    private static final String STUDENT_ID = "sid-123456789";
    private static final String TUITION_ID = "tid-123456789";
    private static final String PAYMENT_ID = "pid-123456789";
    private static final Month MONTH_JANUARY = Month.JANUARY;
    private static final long DATE_JANUARY = 1672511400000L;
    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String ERROR_STATUS = "ERROR";
    private static final String SUCCESS_MESSAGE = "Successfully returned the data.";
    private static final String ERROR_MESSAGE = "Oops!! Something went wrong. Please try again.";
    private static final String ERROR = "ERROR";
    private static final String MAKE_TUITION_PAYMENT_URI = "/api/v1/payment/make";
    private static final String GET_ALL_PAYMENT_BY_TUITION_ID_AND_MONTH_URI = "/api/v1/payment/get/all/{tuitionId}/{month}";
    private static final String DELETE_ALL_BY_TUITION_ID_URI = "/api/v1/payment/delete/all/tuition/{tuitionId}";
    private static final String DELETE_ALL_BY_STUDENT_ID_URI = "/api/v1/payment/delete/all/student/{studentId}";
    private MockMvc mockMvc;
    @Mock
    private PaymentService paymentService;
    @Mock
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        PaymentController paymentController = new PaymentController(paymentService, studentService);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    /**
     * Start of tests for make tuition payment
     * Api context: /api/v1/payment/make
     */
    @Test
    void Should_ReturnOk_When_MakingTuitionPaymentIsSuccessful() throws Exception {
        when(studentService.getStudentInfo(anyString())).thenReturn(getSampleStudentResponseDto());
        when(paymentService.makeTuitionPayment(any(PaymentCreateRequestDto.class), any(StudentResponseDto.class)))
                .thenReturn(getSamplePayment());

        mockMvc.perform(MockMvcRequestBuilders.post(MAKE_TUITION_PAYMENT_URI)
                        .content(getSamplePaymentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.MADE_PAYMENT.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.MADE_PAYMENT.getCode()))
                .andExpect(jsonPath("$.data.id").value(PAYMENT_ID))
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_MakingTuitionPaymentForMissingRequiredFields() throws Exception {
        PaymentCreateRequestDto dto = getSamplePaymentCreateRequestDto();
        dto.setMonth(null);

        mockMvc.perform(MockMvcRequestBuilders.post(MAKE_TUITION_PAYMENT_URI)
                        .content(dto.toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_MakingTuitionPaymentForStudentNotEnrolledInTuition() throws Exception {
        when(studentService.getStudentInfo(anyString())).thenReturn(getSampleStudentResponseDto());
        when(paymentService.makeTuitionPayment(any(PaymentCreateRequestDto.class), any(StudentResponseDto.class)))
                .thenThrow(new StudentNotEnrolledInTuitionException(ERROR));

        mockMvc.perform(MockMvcRequestBuilders.post(MAKE_TUITION_PAYMENT_URI)
                        .content(getSamplePaymentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.STUDENT_NOT_ENROLLED_IN_TUITION
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.STUDENT_NOT_ENROLLED_IN_TUITION
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_MakingTuitionPaymentForPaymentMonthInvalid() throws Exception {
        when(studentService.getStudentInfo(anyString())).thenReturn(getSampleStudentResponseDto());
        when(paymentService.makeTuitionPayment(any(PaymentCreateRequestDto.class), any(StudentResponseDto.class)))
                .thenThrow(new PaymentMonthInvalidException(ERROR));

        mockMvc.perform(MockMvcRequestBuilders.post(MAKE_TUITION_PAYMENT_URI)
                        .content(getSamplePaymentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INVALID_PAYMENT_MONTH
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.INVALID_PAYMENT_MONTH
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_MakingTuitionPaymentForPaymentAlreadyMade() throws Exception {
        when(studentService.getStudentInfo(anyString())).thenReturn(getSampleStudentResponseDto());
        when(paymentService.makeTuitionPayment(any(PaymentCreateRequestDto.class), any(StudentResponseDto.class)))
                .thenThrow(new PaymentAlreadyMadeException(ERROR));

        mockMvc.perform(MockMvcRequestBuilders.post(MAKE_TUITION_PAYMENT_URI)
                        .content(getSamplePaymentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.PAYMENT_ALREADY_MADE
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.PAYMENT_ALREADY_MADE
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_MakingTuitionPaymentForStudentMicroserviceCallFailed()
            throws Exception {
        when(studentService.getStudentInfo(anyString()))
                .thenThrow(new StudentServiceHttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR));

        mockMvc.perform(MockMvcRequestBuilders.post(MAKE_TUITION_PAYMENT_URI)
                        .content(getSamplePaymentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType
                        .STUDENT_INTERNAL_SERVER_ERROR.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType
                        .STUDENT_INTERNAL_SERVER_ERROR.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_MakingTuitionPaymentIsFailed() throws Exception {
        when(studentService.getStudentInfo(anyString())).thenReturn(getSampleStudentResponseDto());
        when(paymentService.makeTuitionPayment(any(PaymentCreateRequestDto.class), any(StudentResponseDto.class)))
                .thenThrow(new PaymentServiceException(ERROR));

        mockMvc.perform(MockMvcRequestBuilders.post(MAKE_TUITION_PAYMENT_URI)
                        .content(getSamplePaymentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    /**
     * Start of tests for get all payment by tuition id and month
     * Api context: /api/v1/payment/get/all/{tuitionId}/{month}
     */
    @Test
    void Should_ReturnOk_When_GettingAllPaymentByTuitionIdAndMonthIsSuccessful() throws Exception {
        when(paymentService.getAllStudentIdByTuitionIdAndMonth(anyString(), any(Month.class)))
                .thenReturn(getSampleStudentIdList());

        String uri = GET_ALL_PAYMENT_BY_TUITION_ID_AND_MONTH_URI.replace("{tuitionId}", TUITION_ID)
                .replace("{month}", MONTH_JANUARY.getMonthString());
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .content(getSamplePaymentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.READ_PAYMENT.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.READ_PAYMENT.getCode()))
                .andExpect(jsonPath("$.data.studentIds[0]").value(STUDENT_ID))
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_GettingAllPaymentByTuitionIdAndMonthIsFailed() throws Exception {
        when(paymentService.getAllStudentIdByTuitionIdAndMonth(anyString(), any(Month.class)))
                .thenThrow(new PaymentServiceException(ERROR));

        String uri = GET_ALL_PAYMENT_BY_TUITION_ID_AND_MONTH_URI.replace("{tuitionId}", TUITION_ID)
                .replace("{month}", MONTH_JANUARY.getMonthString());
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .content(getSamplePaymentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    /**
     * Start of tests for delete all by tuition id
     * Api context: /api/v1/payment/delete/all/tuition/{tuitionId}
     */
    @Test
    void Should_ReturnOk_When_DeletingAllByTuitionIdIsSuccessful() throws Exception {
        doNothing().when(paymentService).deleteAllByTuitionId(anyString());

        String uri = DELETE_ALL_BY_TUITION_ID_URI.replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                        .content(getSamplePaymentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.DELETE_PAYMENT.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.DELETE_PAYMENT.getCode()))
                .andExpect(jsonPath("$.data.studentIds[0]").doesNotExist())
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_DeletingAllByTuitionIdIsFailed() throws Exception {
        doThrow(new PaymentServiceException(ERROR)).when(paymentService).deleteAllByTuitionId(anyString());

        String uri = DELETE_ALL_BY_TUITION_ID_URI.replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                        .content(getSamplePaymentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    /**
     * Start of tests for delete all by student id
     * Api context: /api/v1/payment/delete/all/student/{studentId}
     */
    @Test
    void Should_ReturnOk_When_DeletingAllByStudentIdIsSuccessful() throws Exception {
        doNothing().when(paymentService).deleteAllByStudentId(anyString());

        String uri = DELETE_ALL_BY_STUDENT_ID_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                        .content(getSamplePaymentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.DELETE_PAYMENT.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.DELETE_PAYMENT.getCode()))
                .andExpect(jsonPath("$.data.studentIds[0]").doesNotExist())
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_DeletingAllByStudentIdIsFailed() throws Exception {
        doThrow(new PaymentServiceException(ERROR)).when(paymentService).deleteAllByStudentId(anyString());

        String uri = DELETE_ALL_BY_STUDENT_ID_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                        .content(getSamplePaymentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    /**
     * This method returns a sample PaymentCreateRequestDto
     *
     * @return PaymentCreateRequestDto
     */
    private PaymentCreateRequestDto getSamplePaymentCreateRequestDto() {
        return new PaymentCreateRequestDto(STUDENT_ID, TUITION_ID, MONTH_JANUARY);
    }

    /**
     * This method creates a sample StudentResponseDto
     *
     * @return StudentResponseDto
     * @throws ParseException
     */
    private StudentResponseDto getSampleStudentResponseDto() throws ParseException {
        StudentResponseDto responseDto = new StudentResponseDto();
        responseDto.setTuitionId(TUITION_ID);
        responseDto.setTuitionJoinedOn(new Date(DATE_JANUARY));
        return responseDto;
    }

    /**
     * This method returns a sample Payment
     *
     * @return Payment
     */
    private Payment getSamplePayment() {
        Payment payment = new Payment();
        payment.setId(PAYMENT_ID);
        return payment;
    }

    /**
     * This method returns a sample Student id list
     *
     * @return List of StudentId
     */
    private List<String> getSampleStudentIdList() {
        List<String> studentIds = new ArrayList<>();
        studentIds.add(STUDENT_ID);
        return studentIds;
    }
}
