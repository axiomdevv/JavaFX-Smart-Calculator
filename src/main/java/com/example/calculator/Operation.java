package com.example.calculator;

public enum Operation {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/");

    private final String symbol;

    private Operation (String symbol){
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public static Operation FromSymbol(String text){
        for(Operation op : Operation.values()){
            if(op.symbol.equals(text)){
                return op;
            }
        }
        return null;
    }




}
