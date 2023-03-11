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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Payment Service
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * This method is used to make a payment to a tuition by student
     *
     * @param requestDto         payment request dto
     * @param studentResponseDto student response dto from registration microservice
     * @return Payment
     */
    @Transactional
    public Payment makeTuitionPayment(PaymentCreateRequestDto requestDto, StudentResponseDto studentResponseDto) {
        try {
            if (studentResponseDto.getTuitionId() == null || !studentResponseDto.getTuitionId().equals(requestDto
                    .getTuitionId())) {
                throw new StudentNotEnrolledInTuitionException("Student not enrolled in tuition");
            }
            if (!isPaymentMonthValid(requestDto.getMonth(), studentResponseDto.getTuitionJoinedOn()))
                throw new PaymentMonthInvalidException("Payment month invalid");
            if (isPaymentAlreadyMade(requestDto))
                throw new PaymentAlreadyMadeException("Payment already made");

            Payment payment = new Payment(requestDto, true);
            return paymentRepository.save(payment);
        } catch (DataAccessException e) {
            throw new PaymentServiceException("Failed to make tuition payment for studentId: " + requestDto
                    .getStudentId() + ", tuitionId: " + requestDto.getTuitionId(), e);
        }
    }

    /**
     * This method is used to check if a student is eligible to pay for a given month for the given tuition
     *
     * @param month             month
     * @param tuitionJoinedDate student joined date to tuition
     * @return true/false
     */
    private boolean isPaymentMonthValid(Month month, Date tuitionJoinedDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tuitionJoinedDate);
        return month.getMonthInt() >= calendar.get(Calendar.MONTH);
    }

    /**
     * This method is used to get all student id by tuition id, month
     *
     * @param tuitionId tuition id
     * @param month     month
     * @return student id list
     */
    public List<String> getAllStudentIdByTuitionIdAndMonth(String tuitionId, Month month) {
        try {
            return paymentRepository.getAllStudentIdByTuitionIdAndMonth(tuitionId, month);
        } catch (DataAccessException e) {
            throw new PaymentServiceException("Failed to get all payments by tuitionId and month", e);
        }
    }

    /**
     * This method is used to check if the payment for the given month and tuition is been made already
     *
     * @param requestDto payment request dto
     * @return true/false
     */
    private boolean isPaymentAlreadyMade(PaymentCreateRequestDto requestDto) {
        try {
            return paymentRepository.existsByTuitionIdAndMonthAndStudentId(requestDto.getTuitionId(), requestDto.getMonth(),
                    requestDto.getStudentId());
        } catch (DataAccessException e) {
            throw new PaymentServiceException("Failed to check whether payment already made", e);
        }
    }

    /**
     * This method is used to delete all payments by tuition id
     *
     * @param tuitionId tuition id
     */
    public void deleteAllByTuitionId(String tuitionId) {
        try {
            paymentRepository.deleteAllByTuitionId(tuitionId);
        } catch (DataAccessException e) {
            throw new PaymentServiceException("Failed to delete all payment by tuitionId for tuitionId: " + tuitionId, e);
        }
    }

    /**
     * This method is used to delete all payments by student id
     *
     * @param studentId student id
     */
    public void deleteAllByStudentId(String studentId) {
        try {
            paymentRepository.deleteAllByStudentId(studentId);
        } catch (DataAccessException e) {
            throw new PaymentServiceException("Failed to delete all payment by studentId for studentId: " + studentId, e);
        }
    }
}
