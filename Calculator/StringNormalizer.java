package ru.compscicenter.java2016.calculator;

/*
  Created by sandulmv on 10.10.16.
 */

final class StringNormalizer {
    private StringNormalizer() {
    }
    static String prepare(String expr) {
        expr = StringNormalizer.toLowerCase(StringNormalizer.removeWhitespaces(expr));
        return expr;
    }

    private static String removeWhitespaces(String expr) {
        expr = expr.replaceAll("\\s", "");
        return expr;
    }

    private static String toLowerCase(String expr) {
        expr = expr.toLowerCase();
        return expr;
    }
}
