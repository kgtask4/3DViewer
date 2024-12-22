package math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.vsu.cs.math.Vector3f;

public class Vector3fTest {

    @Test
    public void testAdd() {
        Vector3f v1 = new Vector3f(1f, 2f, 3f);
        Vector3f v2 = new Vector3f(4f, 5f, 6f);
        Vector3f result = v1.add(v2);

        Assertions.assertEquals(5f, result.x, 1e-7);
        Assertions.assertEquals(7f, result.y, 1e-7);
        Assertions.assertEquals(9f, result.z, 1e-7);
    }

    @Test
    public void testSub() {
        Vector3f v1 = new Vector3f(5f, 5f, 5f);
        Vector3f v2 = new Vector3f(1f, 2f, 3f);
        Vector3f result = v1.sub(v2);

        Assertions.assertEquals(4f, result.x, 1e-7);
        Assertions.assertEquals(3f, result.y, 1e-7);
        Assertions.assertEquals(2f, result.z, 1e-7);
    }

    @Test
    public void testDot() {
        Vector3f v1 = new Vector3f(1f, 2f, 3f);
        Vector3f v2 = new Vector3f(2f, 3f, 4f);
        float dot = v1.dot(v2);

        Assertions.assertEquals(1*2 + 2*3 + 3*4, dot, 1e-7);
    }

    @Test
    public void testCross() {
        Vector3f v1 = new Vector3f(1f, 0f, 0f);
        Vector3f v2 = new Vector3f(0f, 1f, 0f);
        Vector3f cross = v1.cross(v2);

        Assertions.assertEquals(0f, cross.x, 1e-7);
        Assertions.assertEquals(0f, cross.y, 1e-7);
        Assertions.assertEquals(1f, cross.z, 1e-7);
    }

    @Test
    public void testLengthAndNormalize() {
        Vector3f v = new Vector3f(3f, 4f, 0f);
        Assertions.assertEquals(5f, v.length(), 1e-7);

        v.normalize();
        Assertions.assertEquals(3f/5f, v.x, 1e-7);
        Assertions.assertEquals(4f/5f, v.y, 1e-7);
        Assertions.assertEquals(0f, v.z, 1e-7);
    }

    @Test
    public void testEqualsVector() {
        Vector3f v1 = new Vector3f(1f,2f,3f);
        Vector3f v2 = new Vector3f(1f,2f,3f);
        Vector3f v3 = new Vector3f(1f,2f,3.00001f);

        Assertions.assertTrue(v1.equals(v2));
        Assertions.assertFalse(v1.equals(v3));
    }
}