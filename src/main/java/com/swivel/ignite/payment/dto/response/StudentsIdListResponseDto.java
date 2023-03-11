package com.swivel.ignite.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Student Id List DTO for response
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudentsIdListResponseDto extends ResponseDto {

    private List<String> studentIds;
}
