import calculator.Calculator;
import calculator.UserInterface;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;


public class UserInterfaceTest {
    private static final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;
    private static final InputStream originalIn = System.in;
    Calculator calculator;

    @BeforeAll
    static void setUpStreams() {
        System.setOut(new PrintStream(out));
    }

    @AfterAll
    static void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @BeforeEach
    void setUpCalculator() {
        calculator = new Calculator(new HashMap<>(), new ArrayDeque<>());
    }

    @ParameterizedTest
    @CsvSource({"/exit, bye", "/help, The program calculates the sum of", "/wa, Unknown command"})
    void testCommands(String input, String expected) {
        UserInterface userInterface = new UserInterface(new Scanner(
                new ByteArrayInputStream(input.getBytes())), calculator);
        userInterface.start();
        assertTrue(out.toString().contains(expected));
    }

    @ParameterizedTest
    @CsvSource({"2+2, 4", "6*7-8+4/2, 36"})
    void testCalculationOutput(String input, String expected) {
        UserInterface userInterface = new UserInterface(new Scanner(
                new ByteArrayInputStream(input.getBytes())), calculator);
        userInterface.start();
        assertTrue(out.toString().contains(expected));
    }

    @ParameterizedTest
    @CsvSource({"a=2", "b=6", "a = 4", "c = 775656456456", "g = a"})
    void testCorrectVariable(String input){
        UserInterface userInterface = new UserInterface(new Scanner(
                new ByteArrayInputStream(input.getBytes())), calculator);
        userInterface.start();
        assertTrue(out.toString().contains("Variable added"));
    }
    @ParameterizedTest
    @CsvSource({"=2, Invalid identifier", "b=, Invalid assignment", "a = b, Unknown variable",
            "c = a6456, Invalid assignment", "f6 = 666, Invalid identifier"})
    void testIncorrectVariable(String input, String expected){
        UserInterface userInterface = new UserInterface(new Scanner(
                new ByteArrayInputStream(input.getBytes())), calculator);
        userInterface.start();
        assertTrue(out.toString().contains(expected));
    }



}
