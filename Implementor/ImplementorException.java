package ru.compscicenter.java2016.implementor;

public class ImplementorException extends Exception {
    public ImplementorException(final String message) {
        super(message);
    }

    public ImplementorException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
