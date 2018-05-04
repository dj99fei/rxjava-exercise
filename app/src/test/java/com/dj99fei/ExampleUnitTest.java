package com.dj99fei;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testSqrt() {
        System.out.println(Sqrter.sqrt(3));
    }

    public static class Sqrter {

        static float sqrtImprove(float guess, int x) {
            return ((x / guess) + guess) / 2;
        }

        public static float sqrt(int x) {
            int time = 0;
            float result = 1.0f;
            while (true) {
                result = sqrtImprove(result, x);
                if (++time == 3) {
                    return result;
                }
            }
        }
    }
}