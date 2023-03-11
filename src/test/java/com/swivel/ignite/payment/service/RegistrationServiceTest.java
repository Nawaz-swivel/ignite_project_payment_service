package com.swivel.ignite.payment.service;

import com.swivel.ignite.payment.dto.response.StudentResponseDto;
import com.swivel.ignite.payment.exception.RegistrationServiceHttpClientErrorException;
import com.swivel.ignite.payment.wrapper.StudentResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * This class tests {@link RegistrationService} class
 */
class RegistrationServiceTest {

    private static final String STUDENT_ID = "sid-123456789";
    private static final String BASE_URL = "http://localhost:8082/ignite-registration-service";
    private static final String STUDENT_INFO_URL = "/api/v1/student/get/{studentId}";
    @Mock
    private RestTemplate restTemplate;
    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        registrationService = new RegistrationService(BASE_URL, STUDENT_INFO_URL, restTemplate);
    }

    /**
     * Start of tests for getStudentInfo method
     */
    @Test
    void Should_ReturnStudentResponseDto_When_GettingStudentInfoIsSuccessful() throws IOException {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class),
                anyMap())).thenReturn(getSampleResponseEntity());
        assertEquals(STUDENT_ID, registrationService.getStudentInfo(STUDENT_ID).getStudentId());
    }

    @Test
    void Should_ThrowRegistrationServiceHttpClientErrorException_When_GettingStudentInfoIsFailed() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class),
                anyMap())).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        RegistrationServiceHttpClientErrorException exception =
                assertThrows(RegistrationServiceHttpClientErrorException.class, () -> registrationService
                        .getStudentInfo(STUDENT_ID));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Failed to get student info",
                exception.getMessage());
    }

    /**
     * This method returns a sample ResponseEntity
     *
     * @return ResponseEntity
     */
    private ResponseEntity<StudentResponseWrapper> getSampleResponseEntity() {
        return new ResponseEntity<>(getSampleStudentResponseWrapper(), HttpStatus.OK);
    }

    /**
     * This method returns a sample StudentResponseWrapper
     *
     * @return StudentResponseWrapper
     */
    private StudentResponseWrapper getSampleStudentResponseWrapper() {
        StudentResponseWrapper responseWrapper = new StudentResponseWrapper();
        responseWrapper.setData(getSampleStudentResponseDto());
        return responseWrapper;
    }

    /**
     * This method returns a sample StudentResponseDto
     *
     * @return StudentResponseDto
     */
    private StudentResponseDto getSampleStudentResponseDto() {
        StudentResponseDto responseDto = new StudentResponseDto();
        responseDto.setStudentId(STUDENT_ID);
        return responseDto;
    }
}
