import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import springbook.learningtest.template.Calculator;
import java.io.IOException;

public class CalcSumTest {
    private Calculator calculator;
    private String filepath;
    @Before
    public void setUp() {
        calculator = new Calculator();
        filepath = getClass().getResource("numbers.txt").getPath();
    }

    @Test
    public void sumOfNumbers() throws IOException {
        int sum = this.calculator.calcSum(filepath);
        assertEquals(sum, 10);
    }

    @Test
    public void multiplyOfNumbers() throws IOException {
        int sum = this.calculator.calcMul(filepath);
        assertEquals(sum, 24);
    }

}
