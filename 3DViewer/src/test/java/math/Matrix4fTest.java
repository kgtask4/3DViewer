package math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.vsu.cs.math.Matrix4f;
import ru.vsu.cs.math.Vector3f;

public class Matrix4fTest {

    @Test
    public void testIdentity() {
        Matrix4f m = new Matrix4f();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                float expected = (i == j) ? 1f : 0f;
                Assertions.assertEquals(expected, m.get(i,j), 1e-7);
            }
        }
    }

    @Test
    public void testMatrixMultiplication() {
        Matrix4f m1 = new Matrix4f();
        m1.set(0,0,2f); m1.set(0,1,0f); m1.set(0,2,0f); m1.set(0,3,0f);
        m1.set(1,0,0f); m1.set(1,1,2f); m1.set(1,2,0f); m1.set(1,3,0f);
        m1.set(2,0,0f); m1.set(2,1,0f); m1.set(2,2,2f); m1.set(2,3,0f);
        m1.set(3,0,0f); m1.set(3,1,0f); m1.set(3,2,0f); m1.set(3,3,1f);

        Matrix4f m2 = new Matrix4f();
        Matrix4f result = m1.mul(m2);

        // Результат умножения m1 на единичную матрицу равен m1
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Assertions.assertEquals(m1.get(i,j), result.get(i,j), 1e-7);
            }
        }
    }

    @Test
    public void testVectorMultiplication() {
        // Для единичной матрицы v должен остаться неизменным
        Matrix4f m = new Matrix4f();
        Vector3f v = new Vector3f(1f,2f,3f);
        Vector3f res = m.mul(v);

        Assertions.assertEquals(1f, res.x, 1e-7);
        Assertions.assertEquals(2f, res.y, 1e-7);
        Assertions.assertEquals(3f, res.z, 1e-7);
    }

    @Test
    public void testTranslation() {
        Matrix4f t = Matrix4f.translation(10f,20f,30f);
        Vector3f v = new Vector3f(1f,1f,1f);
        Vector3f res = t.mul(v);

        // (1 + 10, 1 + 20, 1 + 30) = (11, 21, 31)
        Assertions.assertEquals(11f, res.x, 1e-7);
        Assertions.assertEquals(21f, res.y, 1e-7);
        Assertions.assertEquals(31f, res.z, 1e-7);
    }

    @Test
    public void testScale() {
        Matrix4f s = Matrix4f.scale(2f,3f,4f);
        Vector3f v = new Vector3f(1f,1f,1f);
        Vector3f res = s.mul(v);

        // (2*1, 3*1, 4*1) = (2, 3, 4)
        Assertions.assertEquals(2f, res.x, 1e-7);
        Assertions.assertEquals(3f, res.y, 1e-7);
        Assertions.assertEquals(4f, res.z, 1e-7);
    }

    @Test
    public void testRotationX() {
        float angle = (float)Math.toRadians(90);
        Matrix4f rx = Matrix4f.rotationX(angle);
        Vector3f v = new Vector3f(0f,1f,0f);
        Vector3f res = rx.mul(v);

        // (0,1,0) должен стать (0,0,1)
        Assertions.assertEquals(0f, res.x, 1e-7);
        Assertions.assertEquals(0f, res.y, 1e-7);
        Assertions.assertEquals(1f, res.z, 1e-7);
    }

    @Test
    public void testLookAt() {
        // Точка должна оказаться в центре координат камеры
        Vector3f eye = new Vector3f(0f,0f,10f);
        Vector3f target = new Vector3f(0f,0f,0f);
        Vector3f up = new Vector3f(0f,1f,0f);

        Matrix4f view = Matrix4f.lookAt(eye, target, up);
        Vector3f v = new Vector3f(0f,0f,0f);
        Vector3f res = view.mul(v);

        Assertions.assertEquals(0f, res.x, 1e-7);
        Assertions.assertEquals(0f, res.y, 1e-7);
        Assertions.assertTrue(res.z < 0f);
    }

    @Test
    public void testPerspective() {
        Matrix4f p = Matrix4f.perspective((float)Math.toRadians(90), 1f, 0.1f, 100f);

        // p[0,0] должен быть ~ 1/(tan(fov/2)) при aspect = 1
        // fov=90°, tan(45°)=1, 1/1=1
        Assertions.assertEquals(1f, p.get(0,0), 1e-6);
        // p[1,1] такое же
        Assertions.assertEquals(1f, p.get(1,1), 1e-6);
        // Проверка, что матрица не является единичной
        Assertions.assertNotEquals(1f, p.get(2,2), 1e-6);
        Assertions.assertEquals(-1f, p.get(3,2), 1e-6);
    }
}