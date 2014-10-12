package com.dmi.util.hamcrest

import org.hamcrest.Matcher
import org.hamcrest.collection.IsArray

import static org.hamcrest.Matchers.array
import static spock.util.matcher.HamcrestMatchers.closeTo

class HamcrestMatchers {
    static IsArray<Number> arrayCloseTo(Number[] items, Number error) {
        List<Matcher<Number>> matchers = new ArrayList<>();
        for (float item : items) {
            matchers.add(closeTo(item, error));
        }
        return array(matchers.toArray() as Matcher<Number>[]);
    }
}
