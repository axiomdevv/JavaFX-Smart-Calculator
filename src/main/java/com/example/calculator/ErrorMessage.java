package com.example.calculator;

public enum ErrorMessage {
    OVERFLOW("OVERFLOW - Number too large"),
    DIVIDE_BY_ZERO("Impossible - Division by Zero"),
    NEGATIVE_SQRT("Error - Negative Value in Square Root");

    private final String message ;

    private ErrorMessage(String message){
        this.message = message;
    }

    public String getMessage() { return message; }

}
