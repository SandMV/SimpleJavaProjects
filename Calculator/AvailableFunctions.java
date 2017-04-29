package ru.compscicenter.java2016.calculator;

/*
  Created by sandulmv on 10.10.16.
 */
import java.util.function.Function;

enum AvailableFunctions {
    UNARY_PLUS(x -> x, "+"),
    UNARY_MINUS(x -> -x, "-"),
    ABS(Math::abs, "abs"),
    SIN(Math::sin, "sin"),
    COS(Math::cos, "cos");

    private final Function<Double, Double> function;
    private final String name;

    AvailableFunctions(final Function<Double, Double> f, final String fName) {
        this.function = f;
        this.name = fName;
    }

    public Function<Double, Double> getFunction(){
        return function;
    }

    public static AvailableFunctions getByName(final String name) {
        for (AvailableFunctions af : AvailableFunctions.values()) {
            if (!af.name.equals(name)) {
                continue;
            }
            return af;
        }
        return null;
    }

}
