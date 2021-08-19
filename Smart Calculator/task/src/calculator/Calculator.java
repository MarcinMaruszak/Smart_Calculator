package calculator;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {
    private Map<String, BigInteger> values;
    private Deque<String> postfix;

    public Calculator(Map<String, BigInteger> values, ArrayDeque<String> postfix) {
        this.values = values;
        this.postfix = postfix;
    }


    public Map<String, BigInteger> getValues() {
        return values;
    }

    public void setValues(Map<String, BigInteger> values) {
        this.values = values;
    }

    public Deque<String> getPostfix() {
        return postfix;
    }

    public void setPostfix(Deque<String> postfix) {
        this.postfix = postfix;
    }

    public BigInteger getValue(String key) throws InvalidParameterException {
        BigInteger value = values.get(key);

        if (value == null) {
            throw new InvalidParameterException("Unknown variable");
        }
        return value;
    }

    public void addVariable(String input) throws InvalidParameterException {
        input = input.replace(" ", "");
        String[] array = input.split("=");

        if (array.length < 2) {
            throw new InvalidParameterException("Invalid assignment");
        }
        if (!array[0].matches("[a-zA-Z]+")) {
            throw new InvalidParameterException("Invalid identifier");
        }
        if (containsLettersAndNumbers(array[1]) || array.length > 2) {
            throw new InvalidParameterException("Invalid assignment");
        }
        if (!array[1].matches("[-]?\\d+")) {
            BigInteger x = getValue(array[1]);
            array[1] = String.valueOf(x);
        }
        values.put(array[0], new BigInteger(array[1]));
    }

    private boolean containsLettersAndNumbers(String s) {
        return s.matches(".*[a-zA-Z].*") && s.matches(".*\\d.*");
    }

    public int priorityOf(String operator) {
        if (operator.equals("+") || operator.equals("-")) return 0;

        if (operator.equals("*") || operator.equals("/")) return 1;

        if (operator.equals("^")) return 2;

        if (operator.equals("(") || operator.equals(")")) return 3;

        return -1;
    }


    public void convertToPostfix(String equation) {
        equation = equation.replaceAll(" ", "");
        Pattern pattern = Pattern.compile("[()]|[\\^+\\-/*]+|\\w+");
        Matcher matcher = pattern.matcher(equation);

        Deque<String> operators = new ArrayDeque<>();

        int i = 0;

        while (matcher.find(i)) {
            String element = matcher.group();

            if (isOperator(element)) {
                try {
                    element = getOperator(element);
                    pushOperator(element, operators);

                } catch (InvalidParameterException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                postfix.offer(element);
            }
            i = matcher.end();
        }
        while (!operators.isEmpty()){
            postfix.offer(operators.pop());
        }
        if(postfix.contains("(")){
            throw new InvalidParameterException("Invalid expression");
        }
    }

    private void pushOperator(String operator, Deque<String> operators) {
        if(operator.equals(")")) {
            while ((!operators.isEmpty() && !operators.peek().equals("("))) {
                postfix.offer(operators.poll());
            }
            if(operators.isEmpty()){
                throw new InvalidParameterException("Invalid expression");
            }
            operators.poll();
        }else if(operators.isEmpty() || operators.peek().equals("(") || operator.equals("(")
                || priorityOf(operator) > priorityOf(operators.peek()) ) {
            operators.push(operator);
        }else {
            while(!operators.isEmpty() && priorityOf(operator) <= priorityOf(operators.peek())
                    && !operators.peek().equals("(")){
                postfix.offer(operators.poll());
            }
            operators.push(operator);
        }
    }

    private boolean isOperator(String element) {
        return element.matches("[\\^()+\\-/*]+");
    }

    private String getOperator(String operator) throws InvalidParameterException {
        if (operator.length() == 1) return operator;
        if (operator.matches("\\++")) return "+";
        if (operator.matches("[*\\\\]{2}")) throw new InvalidParameterException("Invalid expression");

        long count = operator.chars()
                .filter(ch -> ch == '-')
                .count();
        return count % 2 == 0 ? "+" : "-";
    }

    public BigInteger calculateSum() {
        Deque<BigInteger> result = new ArrayDeque<>();
        while (!postfix.isEmpty()){
            String element = postfix.pop();
            if(isNumber(element)){
                result.push(new BigInteger(element));
            }else if(isOperator(element)){
               BigInteger number = calculate(result.pop(), result.pop(), element);
               result.push(number);
            }else {
                BigInteger x = getValue(element);
                result.push(x);
            }
        }
        return result.poll();
    }

    private BigInteger calculate(BigInteger pop, BigInteger pop1, String element) {
        switch (element){
            case "+":
                return pop.add(pop1);
            case "-":
                return pop1.subtract(pop);
            case "*":
                return pop.multiply(pop1);
            case "/":
                return pop1.divide(pop);
            case "^":
                return pop1.pow(pop.intValue());
            default:
                throw new InvalidParameterException("Wrong operator");
        }
    }

    private boolean isNumber(String input){
        try{
            new BigInteger(input);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
