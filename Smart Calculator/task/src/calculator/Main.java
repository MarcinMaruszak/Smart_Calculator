package calculator;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        new UserInterface(new Scanner(System.in), new Calculator(new HashMap<>(), new ArrayDeque<>())).start();
    }
}
