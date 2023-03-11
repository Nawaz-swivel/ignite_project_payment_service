package com.swivel.ignite.payment.enums;

import lombok.Getter;

/**
 * Month Enum
 */
@Getter
public enum Month {
    JANUARY(0, "JANUARY"),
    FEBRUARY(1, "FEBRUARY"),
    MARCH(2, "MARCH"),
    APRIL(3, "APRIL"),
    MAY(4, "MAY"),
    JUNE(5, "JUNE"),
    JULY(6, "JULY"),
    AUGUST(7, "AUGUST"),
    SEPTEMBER(8, "SEPTEMBER"),
    OCTOBER(9, "OCTOBER"),
    NOVEMBER(10, "NOVEMBER"),
    DECEMBER(11, "DECEMBER");

    private final int monthInt;
    private final String monthString;

    Month(int monthInt, String monthString) {
        this.monthInt = monthInt;
        this.monthString = monthString;
    }
}
