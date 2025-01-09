package math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.vsu.cs.math.Vector2f;

public class Vector2fTest {

    @Test
    public void testAdd() {
        Vector2f v1 = new Vector2f(1f, 2f);
        Vector2f v2 = new Vector2f(3f, 4f);
        Vector2f result = v1.add(v2);

        Assertions.assertEquals(4f, result.x, 1e-7);
        Assertions.assertEquals(6f, result.y, 1e-7);
    }

    @Test
    public void testSub() {
        Vector2f v1 = new Vector2f(5f, 5f);
        Vector2f v2 = new Vector2f(1f, 2f);
        Vector2f result = v1.sub(v2);

        Assertions.assertEquals(4f, result.x, 1e-7);
        Assertions.assertEquals(3f, result.y, 1e-7);
    }

    @Test
    public void testDot() {
        Vector2f v1 = new Vector2f(1f, 2f);
        Vector2f v2 = new Vector2f(2f, 3f);
        float dot = v1.dot(v2);

        Assertions.assertEquals(1*2 + 2*3, dot, 1e-7);
    }

    @Test
    public void testMul() {
        Vector2f v1 = new Vector2f(2f, 3f);
        Vector2f v2 = v1.mul(2f);

        Assertions.assertEquals(4f, v2.x, 1e-7);
        Assertions.assertEquals(6f, v2.y, 1e-7);
    }
}
