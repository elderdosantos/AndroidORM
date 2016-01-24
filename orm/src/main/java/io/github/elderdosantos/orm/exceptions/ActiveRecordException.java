package io.github.elderdosantos.orm.exceptions;

public class ActiveRecordException extends Exception {

    private static final long serialVersionUID = 1;

    public ActiveRecordException (String message) {
        super(message);
    }
}