package ru.compscicenter.java2016.calculator;

/*
  Created by sandulmv on 10.10.16.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class SimpleCalculator implements Calculator {
    public final double calculate(final String expr) {
        String preparedExpr = StringNormalizer.prepare(expr);
        Result res = this.parsePlusMinus(preparedExpr, 0);
        return res.getAccumulatedValue();
    }

    private Result parsePlusMinus(final String expr, int startOfSubExpression) {
        Result curState = this.parseMulDiv(expr, startOfSubExpression);
        startOfSubExpression = curState.getStartOfSubExpression();
        while (startOfSubExpression < expr.length()) {
            char operation = expr.charAt(startOfSubExpression);
            if (operation != '+' && operation != '-') {
                break;
            }
            double curAccValue = curState.getAccumulatedValue();
            curState = this.parseMulDiv(expr, startOfSubExpression + 1);
            switch (operation) {
                case '+':
                    curAccValue += curState.getAccumulatedValue();
                    break;
                case '-':
                    curAccValue -= curState.getAccumulatedValue();
                    break;
                default:
            }
            startOfSubExpression = curState.getStartOfSubExpression();
            curState.setAccumulatedValue(curAccValue);
        }
        return curState;
    }

    private Result parseMulDiv(final String expr, int startOfSubExpression) {
        Result curState = this.parsePower(expr, startOfSubExpression);
        startOfSubExpression = curState.getStartOfSubExpression();
        while (startOfSubExpression < expr.length()) {
            char operation = expr.charAt(startOfSubExpression);
            if (operation != '*' && operation != '/') {
                break;
            }
            double curAccValue = curState.getAccumulatedValue();
            curState = this.parsePower(expr, startOfSubExpression + 1);
            switch (operation) {
                case '*':
                    curAccValue *= curState.getAccumulatedValue();
                    break;
                case '/':
                    curAccValue /= curState.getAccumulatedValue();
                    break;
                default:
            }
            startOfSubExpression = curState.getStartOfSubExpression();
            curState.setAccumulatedValue(curAccValue);
        }
        return curState;
    }

    private Result parsePower(final String expr, int startOfSubExpression) {
        Result curState = this.parseParenthesis(expr, startOfSubExpression);
        startOfSubExpression = curState.getStartOfSubExpression();
        List<Double> accumulatedValues = new ArrayList<>();
        accumulatedValues.add(curState.getAccumulatedValue());
        while (startOfSubExpression < expr.length()) {
            char operation = expr.charAt(startOfSubExpression);
            if (operation != '^') {
                break;
            }
            curState = this.parseParenthesis(expr, startOfSubExpression + 1);
            accumulatedValues.add(curState.getAccumulatedValue());
            startOfSubExpression = curState.getStartOfSubExpression();
        }
        if (accumulatedValues.size() > 1) {
            curState.setAccumulatedValue(this.processMultiplePower(accumulatedValues));
        }
        return curState;
    }

    private double processMultiplePower(final List<Double> values) {
        Collections.reverse(values);
        double accPower = values.get(0);
        for (int i = 1; i < values.size(); ++i) {
            accPower = Math.pow(values.get(i), accPower);
        }
        return accPower;
    }

    private Result parseParenthesis(final String expr, int startOfSubExpression) {
        Result curState;
        if (expr.charAt(startOfSubExpression) == '(') {
            curState = this.parsePlusMinus(expr, startOfSubExpression + 1);
            curState.setStartOfSubExpression(curState.getStartOfSubExpression() + 1);
        } else {
            curState = this.parseFunction(expr, startOfSubExpression);
        }
        return curState;
    }

    private Result parseFunction(final String expr, int startOfSubExpression) {
        Result curState;
        String funcName = this.getFunctionName(expr, startOfSubExpression);
        switch (funcName) {
            case "":
                return this.parseNumber(expr, startOfSubExpression);
            case "+":
            case "-":
                curState = this.parseMulDiv(expr, startOfSubExpression + 1);
                break;
            default:
                curState = this.parseParenthesis(expr, startOfSubExpression + funcName.length());
        }
        //noinspection ConstantConditions
        Function<Double, Double> function = AvailableFunctions.getByName(funcName).getFunction();
        curState.setAccumulatedValue(function.apply(curState.getAccumulatedValue()));
        return curState;
    }

    private String getFunctionName(String expr, int startOfSubExpression) {
        StringBuilder name = new StringBuilder();
        int idx = startOfSubExpression;
        int exprLen = expr.length();
        char nextChar = expr.charAt(idx);
        if (Character.isDigit(nextChar)) {
            return "";
        }
        if (nextChar == '-' || nextChar == '+') {
            return String.valueOf(nextChar);
        }
        while (idx + 1 < exprLen && nextChar != '(') {
            name.append(nextChar);
            nextChar = expr.charAt(++idx);
        }
        return name.toString();
    }

    private Result parseNumber(String expr, int startOfSubExpression) {
        int idx = startOfSubExpression;
        int exprLen = expr.length();
        StringBuilder number = new StringBuilder();
        boolean endOfNumber = false;
        while (idx < exprLen && !endOfNumber) {
            while (idx < exprLen && Character.isDigit(expr.charAt(idx))) {
                number.append(expr.charAt(idx));
                ++idx;
            }
            if (idx >= exprLen) {
                break;
            }
            char nextChar = expr.charAt(idx);
            switch (nextChar) {
                case '.':
                case 'e':
                    number.append(nextChar);
                    ++idx;
                    continue;
                case '+':
                case '-':
                    if (expr.charAt(idx - 1) == 'e') {
                        number.append(nextChar);
                        ++idx;
                        continue;
                    }
                    endOfNumber = true;
                    continue;
                default: endOfNumber = true;
            }
        }
        double curAccValue = Double.parseDouble(number.toString());
        return new Result(curAccValue, idx);
    }
}
