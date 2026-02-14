package com.example.calculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.math.MathContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CalculatorController {

    @FXML
    private TextField textField;

    private BigDecimal firstOperand = BigDecimal.ZERO;
    private BigDecimal result = BigDecimal.ZERO;
    private Operation operator;
    private boolean isEnteringNewNumber = true;
    private boolean isError = false;

    private static final BigDecimal PERCENT_DIVISOR = new BigDecimal("100");
    private static final RoundingMode DIVISION_ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final int DIVISION_SCALE = 16;
    private static final int MAX_INPUT_DIGITS = 16;

    private static final BigDecimal UPPER_LIMIT = new BigDecimal("1000000000000000"); // 1E15
    private static final BigDecimal LOWER_LIMIT = new BigDecimal("0.0000000001"); // 1E-10

    private List<Button> buttonsToLock = new ArrayList<>();


    private BigDecimal getCurrentNumber(){
        try {
            return new BigDecimal(textField.getText());
        }catch (NumberFormatException e){
            return BigDecimal.ZERO;
        }
    }

    public void updateDisplay(BigDecimal number){

        if (Math.abs(number.scale()) > 10000 || number.precision() > 10000) {
            displayErrors(ErrorMessage.OVERFLOW);
            isEnteringNewNumber = true;
            return;
        }


        if (number.abs().compareTo(UPPER_LIMIT) >= 0 ||
                (number.abs().compareTo(BigDecimal.ZERO) > 0 && number.abs().compareTo(LOWER_LIMIT) < 0)) {

            textField.setText(String.format("%.4E", number));

        } else {
            textField.setText(number.stripTrailingZeros().toPlainString());
        }
    }


    public void DisplayInput(String value){

        if (!isEnteringNewNumber && textField.getText().replace("-", "").replace(".","").length() >= MAX_INPUT_DIGITS) {
            return;
        }

        if (value.equals(".") && (textField.getText().isEmpty() || isEnteringNewNumber)) {
            textField.setText("0.");
            isEnteringNewNumber = false;
            return;
        }

        if(value.equals(".") && textField.getText().contains(".")){return;}

        if(isEnteringNewNumber){
            textField.setText(value);
            isEnteringNewNumber = false;
        } else {
            textField.setText(textField.getText() + value);
        }
    }

    @FXML
    public void handleNumber(ActionEvent e){
        if (isError) {
            handleErase();
        }
        Button btn = (Button) e.getSource();
        String NumberValue = btn.getText();
        DisplayInput(NumberValue);
    }

    @FXML
    public void handleOperator(ActionEvent e){
        Button btn = (Button) e.getSource();
        StoreOperatorAndNumber1( btn.getText());
    }

    public void StoreOperatorAndNumber1(String value ){
        if(operator != null && !isEnteringNewNumber){
            handleEquals();
        }

        operator = Operation.FromSymbol(value);
        firstOperand = getCurrentNumber();
        isEnteringNewNumber = true;
    }

    @FXML
    public void handleEquals(){
        if(operator == null || textField.getText().isEmpty() || textField.getText().equals("-") || textField.getText().equals(".")){
            return;
        }

        BigDecimal num2 = getCurrentNumber();

        if (operator == Operation.DIV && num2.compareTo(BigDecimal.ZERO) == 0){
            displayErrors(ErrorMessage.DIVIDE_BY_ZERO);
            operator = null;
            isEnteringNewNumber = true;
            return;
        }

        result = switch (operator){
            case ADD -> firstOperand.add(num2);
            case SUB -> firstOperand.subtract(num2);
            case MUL -> firstOperand.multiply(num2);
            case DIV -> firstOperand.divide(num2,DIVISION_SCALE,DIVISION_ROUNDING_MODE);
        };

        updateDisplay(result);
        operator = null;
        isEnteringNewNumber = true;
    }

    @FXML
    public void handleErase(){
        isError = false;

        textField.setText("0");
        firstOperand = BigDecimal.ZERO;
        isEnteringNewNumber = true;
        operator = null;

        textField.getStyleClass().remove("display-error");
        buttonsToLock.forEach(btn -> btn.setDisable(false));

    }

    @FXML
    public void handleDelete(){
        if (isError) {
            handleErase();
            return;
        }
        String text = textField.getText();

        if(text.length() > 1){
            textField.setText(text.substring(0,text.length()-1));
        }else{
            textField.setText("0");
            isEnteringNewNumber = true;
        }
    }

    @FXML
    public void handleSign(){
        if(!textField.getText().isEmpty()){
            BigDecimal currentNum =getCurrentNumber();
            currentNum = currentNum.multiply(new BigDecimal("-1"));
            updateDisplay(currentNum);
        }
    }

    @FXML
    public void handleSquare(){
        try {
            if(!textField.getText().isEmpty())
            {
                BigDecimal currentNum = getCurrentNumber();
                currentNum = currentNum.pow(2,new MathContext(MAX_INPUT_DIGITS));
                updateDisplay(currentNum);
            }
        }catch(ArithmeticException e) {
            displayErrors(ErrorMessage.OVERFLOW);
        }
        isEnteringNewNumber = true;
    }

    @FXML
    public void handleSqrt(){
        if(!textField.getText().isEmpty()){
            BigDecimal currentNum = getCurrentNumber();

            if(currentNum.compareTo(BigDecimal.ZERO) < 0){
                displayErrors(ErrorMessage.NEGATIVE_SQRT);
                return;
            }
            currentNum = currentNum.sqrt(new MathContext(MAX_INPUT_DIGITS));
            updateDisplay(currentNum);
        }
        isEnteringNewNumber = true;
    }

    public void handlePercent(){

        if(operator == null || textField.getText().isEmpty()){return;}


        BigDecimal currentNum = getCurrentNumber();
        BigDecimal percent = currentNum.divide(PERCENT_DIVISOR,DIVISION_SCALE,DIVISION_ROUNDING_MODE);

        if (operator == Operation.DIV && percent.compareTo(BigDecimal.ZERO) == 0){
            displayErrors(ErrorMessage.DIVIDE_BY_ZERO);
            operator = null;
            isEnteringNewNumber = true;
            return;
        }


        currentNum = switch (operator) {
            case ADD -> firstOperand.add((firstOperand.multiply(percent)));
            case SUB -> firstOperand.subtract((firstOperand.multiply(percent)));
            case MUL -> firstOperand.multiply((percent));
            case DIV -> firstOperand.divide((percent), DIVISION_SCALE, DIVISION_ROUNDING_MODE);
        };

        firstOperand = currentNum;

        updateDisplay(currentNum);
        isEnteringNewNumber = true;
        operator = null;
    }

    @FXML
    private GridPane grid;

    @FXML
    public void initialize() {
        for (Node node : grid.getChildren()) {
            if (node instanceof Button btn) {
                if(btn.getStyleClass().contains("options") || btn.getStyleClass().contains("equal")){
                    buttonsToLock.add(btn);
                }
                btn.setPickOnBounds(false);
            }
        }
        textField.setFocusTraversable(false);
    }

    public void displayErrors(ErrorMessage message){
        isError = true;
        textField.setText(message.getMessage());

        if(!textField.getStyleClass().contains("display-error")){
            textField.getStyleClass().add("display-error");
        }
        buttonsToLock.forEach(btn -> btn.setDisable(true));

    }

    public void initKeyboard(Scene scene){
        scene.addEventFilter(KeyEvent.KEY_TYPED, (event) ->{
            String input = event.getCharacter();

            if(input.matches("[0-9.]")){
                if (isError) handleErase();
                DisplayInput(input);
                event.consume();
            }
            else if(input.matches("[+\\-*/]")){
                if (isError) handleErase();
                StoreOperatorAndNumber1(input);
                event.consume();
            }
            else if(input.equals("%")){
                handlePercent();
                event.consume();
            }
            else if(input.equals("=")){
                handleEquals();
                event.consume();
            }
        });

        scene.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
            KeyCode code = event.getCode();

            if(code == KeyCode.ENTER){
                if(isError){
                    handleErase();
                }else{
                    handleEquals();
                }
                event.consume();
            }
            else if(code == KeyCode.BACK_SPACE){
                if (isError) {
                    handleErase();
                } else {
                    handleDelete();
                }
                event.consume();
            }
            else if(code == KeyCode.ESCAPE){
                handleErase();
                event.consume();
            }
        });
    }
}