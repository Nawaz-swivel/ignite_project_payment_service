package com.swivel.ignite.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Student DTO for response
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseDto extends ResponseDto {

    private String studentId;
    private String tuitionId;
    private Date tuitionJoinedOn;
}
