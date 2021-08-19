import calculator.Calculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {

    Calculator calculator;

    @BeforeEach
    void init() {
        calculator = new Calculator(new HashMap<>(), new ArrayDeque<>());
    }

    @Test
    void testGetValue() {
        calculator.getValues().put("a", BigInteger.valueOf(20));
        assertEquals(BigInteger.valueOf(20), calculator.getValue("a"));
    }


    @ParameterizedTest
    @CsvSource({"a=5, a, 5", "b = 7, b, 7", "c  =6, c, 6"})
    void testACorrectAddVariable(String input, String key, BigInteger expected) {
        calculator.addVariable(input);
        assertEquals(expected, calculator.getValue(key));
    }

    @ParameterizedTest
    @CsvSource({"a =, Invalid assignment", "=6, Invalid identifier,", "=, Invalid assignment"})
    void testIncorrectAddVariable(String input, String expectedMessage) {
        Exception exception = assertThrows(InvalidParameterException.class, () -> calculator.addVariable(input));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"+, 0", "-, 0", "*, 1", "/, 1", "^, 2", "(, 3", "), 3", "a, -1", "6, -1"})
    void testPriority(String operator, int expected) {
        assertEquals(expected, calculator.priorityOf(operator));
    }

    @ParameterizedTest
    @MethodSource
    void testPostFix(String input, Deque<String> expected) {
        calculator.convertToPostfix(input);
        assertArrayEquals(expected.toArray(), calculator.getPostfix().toArray());
    }

    static Stream<Arguments> testPostFix() {
        return Stream.of(
                Arguments.of("2+2", new ArrayDeque<>(List.of("2", "2", "+"))),
                Arguments.of("4^5/(5*2)+2", new ArrayDeque<>(List.of("4", "5", "^", "5", "2", "*", "/", "2", "+"))));
    }

    @ParameterizedTest
    @CsvSource({"10^2/(5*2)+2, 12", "3 + 8 * ((4 + 3) * 2 + 1) - 6 / (2 + 1), 121", "8 * 3 + 12 * (4 - 2), 48"})
    void testCalculation(String input, BigInteger expected) {
        calculator.convertToPostfix(input);
        assertEquals(expected, calculator.calculateSum());
    }

    @ParameterizedTest
    @MethodSource
    void testCorrectVariable(String[] variableInputs, String equation, BigInteger expected) {
        for (String varString : variableInputs) {
            calculator.addVariable(varString);
        }
        calculator.convertToPostfix(equation);
        assertEquals(expected, calculator.calculateSum());
    }

    static Stream<Arguments> testCorrectVariable() {
        return Stream.of(
                Arguments.of(new String[]{"a=4", "b=5", "c=6"}, "a*2+b*3+c*(2+3)", "53"),
                Arguments.of(new String[]{"a=2", "g=8"}, "a*(g/4)+g/a", "8")
        );
    }

    @ParameterizedTest
    @MethodSource
    void testMissingVariable(String[] variableInputs, String equation) {
        for (String varString : variableInputs) {
            calculator.addVariable(varString);
        }
        calculator.convertToPostfix(equation);
        Exception exception = assertThrows(InvalidParameterException.class,
                () -> calculator.calculateSum());

        assertEquals("Unknown variable", exception.getMessage());
    }

    static Stream<Arguments> testMissingVariable() {
        return Stream.of(
                Arguments.of(new String[]{"a=4", "b=5"}, "a*2+b*3+c*(2+3)"),
                Arguments.of(new String[]{"a=2"}, "a*(g/4)+g/a")
        );
    }

}
