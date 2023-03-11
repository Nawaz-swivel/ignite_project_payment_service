package com.swivel.ignite.payment.repository;

import com.swivel.ignite.payment.entity.Payment;
import com.swivel.ignite.payment.enums.Month;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Payment Repository
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    /**
     * This method is used to get all paid students by id for which they have paid to a tuition and month
     *
     * @param tuitionId tuition id
     * @param month     month
     * @return student id list
     */
    @Query(value = "SELECT p.studentId FROM Payment p WHERE p.tuitionId=:tuitionId AND p.month=:month")
    List<String> getAllStudentIdByTuitionIdAndMonth(String tuitionId, Month month);

    /**
     * This method checks if a payment exists for a tuition id, month, student id
     *
     * @param tuitionId tuition id
     * @param month     month
     * @param studentId student id
     * @return true/ false
     */
    boolean existsByTuitionIdAndMonthAndStudentId(String tuitionId, Month month, String studentId);

    /**
     * This method deletes all payments corresponding to a tuition
     *
     * @param tuitionId tuition id
     */
    @Transactional
    void deleteAllByTuitionId(String tuitionId);

    /**
     * This method deletes all payments corresponding to a student
     *
     * @param studentId student id
     */
    @Transactional
    void deleteAllByStudentId(String studentId);
}
