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
        float width = right - left, height = top - bottom, depth = near - far;
        return new float[]{
                2.0f / width, 0, 0, 0,
                0, 2.0f / height, 0, 0,
                0, 0, 2.0f / depth, 0,
                -(left + right) / width, -(bottom + top) / height, -(near + far) / depth, 1
        };
    }

    public static float[] translation(float x, float y, float z) {
        return new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                x, y, z, 1
        };
    }

    public static float[] scale(float scaleX, float scaleY, float scaleZ) {
        return new float[]{
                scaleX, 0, 0, 0,
                0, scaleY, 0, 0,
                0, 0, scaleZ, 0,
                0, 0, 0, 1
        };
    }

    public static float[] rotateX(float rad_angle) {
        float sin = (float) Math.sin(rad_angle);
        float cos = (float) Math.cos(rad_angle);
        return new float[]{
                1, 0, 0, 0,
                0, cos, sin, 0,
                0, -sin, cos, 0,
                0, 0, 0, 1
        };
    }

    public static float[] rotateY(float rad_angle) {
        float sin = (float) Math.sin(rad_angle);
        float cos = (float) Math.cos(rad_angle);
        return new float[]{
                cos, 0, -sin, 0,
                0, 1, 0, 0,
                sin, 0, cos, 0,
                0, 0, 0, 1
        };
    }

    public static float[] rotateZ(float rad_angle) {
        float sin = (float) Math.sin(rad_angle);
        float cos = (float) Math.cos(rad_angle);
        return new float[]{
                cos, sin, 0, 0,
                -sin, cos, 0, 0,
                0, 0, 1, 0,
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

    //(str/join "," (for [i (range 4)
    //                    j (range 4)]
    //                (str "mat[" (+ (* j 4) i) "]")))
    public static float[] transpose(float[] mat) {
        return new float[]{
                mat[0], mat[4], mat[8], mat[12],
                mat[1], mat[5], mat[9], mat[13],
                mat[2], mat[6], mat[10], mat[14],
                mat[3], mat[7], mat[11], mat[15]
        };
    }

    //(str/join \, (for [i [\x \y \z]
    //                   j (range 3)]
    //               (str i "_axis[" j \])))
    public static float[] lookAt(float[] camera_pos, float[] target_point, float[] up_vec) {
        float[] z_axis = Vector3f.normalize(Vector3f.subtract(camera_pos, target_point));
        float[] x_axis = Vector3f.normalize(Vector3f.vectorProduct(up_vec, z_axis));
        float[] y_axis = Vector3f.normalize(Vector3f.vectorProduct(z_axis, x_axis));
        return new float[]{
                x_axis[0], x_axis[1], x_axis[2], 0,
                y_axis[0], y_axis[1], y_axis[2], 0,
                z_axis[0], z_axis[1], z_axis[2], 0,
                camera_pos[0], camera_pos[1], camera_pos[2], 1
        };
    }
}
