package cljgl;

public class Vector3f {

    public static float[] normalize(float[] in_vec3) {
        float length = (float) Math.sqrt(Math.pow(in_vec3[0], 2) + Math.pow(in_vec3[1], 2) + Math.pow(in_vec3[2], 2));
        return new float[]{
                in_vec3[0] / length,
                in_vec3[1] / length,
                in_vec3[2] / length
        };
    }

    public static float[] subtract(float[] in_vec3_1, float[] in_vec3_2) {
        return new float[]{
                in_vec3_1[0] - in_vec3_2[0],
                in_vec3_1[1] - in_vec3_2[1],
                in_vec3_1[2] - in_vec3_2[2]
        };
    }

    // (let [x "in_vec3_1", y "in_vec3_2"]
    //  (str/join ",\n"
    //            (for [i (range 1 4)
    //                  :let [i (rem i 3)
    //                        i+1 (rem (inc i) 3)]]
    //              (str x \[ i \]
    //                   "*"
    //                   y \[ i+1 \]
    //                   "-"
    //                   x \[ i+1 \]
    //                   "*"
    //                   y \[ i \]))))
    public static float[] vectorProduct(float[] in_vec3_1, float[] in_vec3_2) {
        return new float[]{
                in_vec3_1[1] * in_vec3_2[2] - in_vec3_1[2] * in_vec3_2[1],
                in_vec3_1[2] * in_vec3_2[0] - in_vec3_1[0] * in_vec3_2[2],
                in_vec3_1[0] * in_vec3_2[1] - in_vec3_1[1] * in_vec3_2[0]
        };
    }
}
