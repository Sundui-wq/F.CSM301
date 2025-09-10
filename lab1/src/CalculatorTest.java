import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class CalculatorTest {
    @Test
    public void testAddTwoPositiveNumbers() {
        Calculator calculator = new Calculator();
        int result = calculator.add(3, 4);
        assertEquals(7, result, "3 + 4 нь 7-тэй тэнцүү байх ёстой");
    }
    @Test
    public void testAddPositiveAndNegativeNumbers() {
        Calculator calculator = new Calculator();
        int result = calculator.add(3, -4);
        assertEquals(-1, result, "3 + (-)4 нь -1-тэй тэнцүү байх ёстой");
    }
    @Test
    public void testMultiplyTwoPositiveNumbers() {
    Calculator calculator = new Calculator();
    int result = calculator.multiply(3, 4);
    assertEquals(12, result, "3 * 4 нь 12-тэй тэнцүү байх ёстой");
    }
}