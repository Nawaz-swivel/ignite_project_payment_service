package com.swivel.ignite.payment.service;

import com.swivel.ignite.payment.dto.response.StudentResponseDto;
import com.swivel.ignite.payment.exception.RegistrationServiceHttpClientErrorException;
import com.swivel.ignite.payment.wrapper.StudentResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Registration Microservice
 */
@Slf4j
@Service
public class RegistrationService {

    private static final String FAILED_TO_GET_STUDENT_INFO = "Failed to get student info";
    private final RestTemplate restTemplate;
    private final String getStudentInfoUrl;

    public RegistrationService(@Value("${registration.baseUrl}") String baseUrl,
                               @Value("${registration.studentInfoUrl}") String studentInfoUrl,
                               RestTemplate restTemplate) {
        this.getStudentInfoUrl = baseUrl + studentInfoUrl;
        this.restTemplate = restTemplate;
    }

    /**
     * This method is used to get a student info by student id from registration microservice
     *
     * @param studentId student id
     * @return student response dto
     * @throws IOException
     */
    public StudentResponseDto getStudentInfo(String studentId) throws IOException {
        Map<String, String> uriParam = new HashMap<>();
        uriParam.put("studentId", studentId);

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(getStudentInfoUrl).build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            log.debug("Calling registration service to get student info. url: {},", getStudentInfoUrl);
            ResponseEntity<StudentResponseWrapper> result = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
                    entity, StudentResponseWrapper.class, uriParam);
            String responseBody = Objects.requireNonNull(result.getBody()).getData().toLogJson();
            log.debug("Getting student info by student id was successful. statusCode: {}, response: {}",
                    result.getStatusCode(), responseBody);
            return Objects.requireNonNull(result.getBody()).getData();
        } catch (HttpClientErrorException e) {
            throw new RegistrationServiceHttpClientErrorException(e.getStatusCode(), FAILED_TO_GET_STUDENT_INFO,
                    e.getResponseBodyAsString(), e);
        }
    }
}
