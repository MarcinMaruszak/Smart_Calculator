package calculator;

import java.security.InvalidParameterException;
import java.util.Scanner;

public class UserInterface {
    private Scanner scanner;
    private Calculator calculator;

    public UserInterface(Scanner scanner, Calculator calculator) {
        this.scanner = scanner;
        this.calculator = calculator;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void start() {
        String input;
        while (scanner.hasNext()) {
            input = scanner.nextLine();

            if (input.equals("/exit")) {
                System.out.println("bye");
                break;
            } else if (input.equals("/help")) {
                System.out.println("The program calculates the sum of numbers\n" +
                        "Even number of minuses gives a plus, and the odd number of minuses gives a minus!");
            } else if (input.startsWith("/")) {
                System.out.println("Unknown command");
            } else if (input.contains("=")) {
                try {
                    calculator.addVariable(input);
                    System.out.println("Variable added");
                } catch (InvalidParameterException e) {
                    System.out.println(e.getMessage());
                }
            } else if (!input.isBlank()) {
                try {
                    calculator.convertToPostfix(input);
                    System.out.println(calculator.calculateSum());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        System.out.println(calculator.getValues());
    }
}
