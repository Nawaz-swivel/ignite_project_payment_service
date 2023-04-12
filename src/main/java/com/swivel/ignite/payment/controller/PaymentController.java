package com.swivel.ignite.payment.controller;

import com.swivel.ignite.payment.dto.request.PaymentCreateRequestDto;
import com.swivel.ignite.payment.dto.response.PaymentResponseDto;
import com.swivel.ignite.payment.dto.response.StudentResponseDto;
import com.swivel.ignite.payment.dto.response.StudentsIdListResponseDto;
import com.swivel.ignite.payment.entity.Payment;
import com.swivel.ignite.payment.enums.ErrorResponseStatusType;
import com.swivel.ignite.payment.enums.Month;
import com.swivel.ignite.payment.enums.SuccessResponseStatusType;
import com.swivel.ignite.payment.service.PaymentService;
import com.swivel.ignite.payment.service.StudentService;
import com.swivel.ignite.payment.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    private final StudentService studentService;

    @Autowired
    public PaymentController(PaymentService paymentService, StudentService studentService) {
        this.paymentService = paymentService;
        this.studentService = studentService;
    }

    /**
     * This method used to make a tuition payment of a student
     *
     * @param requestDto payment create request dto
     * @return success/ error response
     */
    @PostMapping(path = "/make", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> makeTuitionPayment(@RequestBody PaymentCreateRequestDto requestDto,
                                                              HttpServletRequest request) throws IOException {
        String token = request.getHeader(AUTH_HEADER);
        if (!requestDto.isRequiredAvailable()) {
            log.error("Required fields missing in payment create request DTO for make tuition payment");
            return getBadRequestResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
        }
        StudentResponseDto studentResponseDto = studentService.getStudentInfo(requestDto.getStudentId(), token);
        Payment payment = paymentService.makeTuitionPayment(requestDto, studentResponseDto);
        log.debug("Made payment for studentId: {}, tuitionId: {}", requestDto.getStudentId(), requestDto
                .getTuitionId());
        PaymentResponseDto responseDto = new PaymentResponseDto(payment);
        return getSuccessResponse(SuccessResponseStatusType.MADE_PAYMENT, responseDto);
    }

    /**
     * This method is used to get all payments by tuitionId and month
     *
     * @param tuitionId tuition id
     * @param month     month
     * @return success(student id list)/ error response
     */
    @GetMapping(path = "/get/all/{tuitionId}/{month}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> getAllPaymentByTuitionIdAndMonth(@PathVariable(name = "tuitionId")
                                                                            String tuitionId,
                                                                            @PathVariable(name = "month")
                                                                            String month) {
        List<String> studentIds = paymentService.getAllStudentIdByTuitionIdAndMonth(tuitionId, Month.valueOf(month));
        StudentsIdListResponseDto responseDto = new StudentsIdListResponseDto(studentIds);
        log.debug("Returned all the payment by tuition id: {}, month: {}", tuitionId, month);
        return getSuccessResponse(SuccessResponseStatusType.READ_PAYMENT, responseDto);
    }

    /**
     * This method is used to delete all tuition by id
     *
     * @param tuitionId tuition id
     * @return success/ error response
     */
    @DeleteMapping(path = "/delete/all/tuition/{tuitionId}")
    public ResponseEntity<ResponseWrapper> deleteAllByTuitionId(@PathVariable(name = "tuitionId") String tuitionId) {
        paymentService.deleteAllByTuitionId(tuitionId);
        log.debug("Successfully deleted all tuition by id: {}", tuitionId);
        return getSuccessResponse(SuccessResponseStatusType.DELETE_PAYMENT, null);
    }

    /**
     * This method is used to delete all students by id
     *
     * @param studentId student id
     * @return success/ error response
     */
    @DeleteMapping(path = "/delete/all/student/{studentId}")
    public ResponseEntity<ResponseWrapper> deleteAllByStudentId(@PathVariable(name = "studentId") String studentId) {
        paymentService.deleteAllByStudentId(studentId);
        log.debug("Successfully deleted all students by id: {}", studentId);
        return getSuccessResponse(SuccessResponseStatusType.DELETE_PAYMENT, null);
    }
}
