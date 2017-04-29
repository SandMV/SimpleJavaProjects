package ru.compscicenter.java2016.calculator;

/*
 * Created by sandulmv on 10.10.16.
 */

class Result {
    private double accumulatedValue;
    private int startOfSubExpression;

    Result(double acc, int startPosition) {
        this.accumulatedValue = acc;
        this.startOfSubExpression = startPosition;
    }

    public double getAccumulatedValue() {
        return accumulatedValue;
    }

    public int getStartOfSubExpression() {
        return startOfSubExpression;
    }

    public void setAccumulatedValue(double accumulatedValue) {
        this.accumulatedValue = accumulatedValue;
    }

    public void setStartOfSubExpression(int restOfExpression) {
        this.startOfSubExpression = restOfExpression;
    }
}
