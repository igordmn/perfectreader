package com.dmi.util.lang

import spock.lang.Specification

import static com.dmi.util.lang.IntegerPercent.multiply
import static com.dmi.util.lang.IntegerPercent.valuePercent

class IntegerPercentSpec extends Specification {
    def "multiply is inverse for valuePercent"() {
        expect:
        multiply(valuePercent(1688014, 4222902) as int, 4222902 as int) == 1688014;
        multiply(valuePercent(33338, 4222902) as int, 4222902 as int) == 33338;
        multiply(valuePercent(0, 1) as int, 1) == 0;
        multiply(valuePercent(1, 1) as int, 1) == 1;
        multiply(valuePercent(0, Integer.MAX_VALUE) as int, Integer.MAX_VALUE as int) == 0;
        multiply(valuePercent(1, Integer.MAX_VALUE) as int, Integer.MAX_VALUE) == 1;
        multiply(valuePercent(Integer.MAX_VALUE, Integer.MAX_VALUE) as int, Integer.MAX_VALUE as int) == Integer.MAX_VALUE;
    }

    def "valuePercent with zero maxValue"() {
        expect:
        valuePercent(0, 0) == 0;
    }
}
