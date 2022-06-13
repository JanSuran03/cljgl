package cljgl;

public class Matrix4f {
    public static final int SIZE = 4;

    public static float[] blank() {
        return new float[16];
    }

    public static float[] identity() {
        return new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1};
    }

    public static float[] orthogonal(float left, float right, float bottom, float top, float near, float far) {
        float[] ret = identity();
        ret[0] = 2.0f / (right - left); // 0 + 0 * 4
        ret[5] = 2.0f / (top - bottom); // 1 + 1 * 4
        ret[10] = 2.0f / (near - far); // 2 + 2 * 4
        ret[12] = (left + right) / (left - right); // 0 + 3 * 4
        ret[13] = (bottom + top) / (bottom - top); // 1 + 3 * 4
        ret[14] = (near + far) / (near - far); // 2 + 3 * 4
        return ret;
    }

    public static float[] scale(float scaleX, float scaleY, float scaleZ) {
        return new float[]{
                scaleX, 0, 0, 0,
                0, scaleY, 0, 0,
                0, 0, scaleZ, 0,
                0, 0, 0, 1
        };
    }

    public static float[] multiply(float[] mat1, float[] mat2) {
        float[] ret = blank();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                float sum = 0;
                for (int i = 0; i < SIZE; i++) {
                    sum += mat1[row * SIZE + i] * mat2[col * SIZE + i];
                }
                ret[SIZE * row + col] = sum;
            }
        }
        return ret;
    }
}
