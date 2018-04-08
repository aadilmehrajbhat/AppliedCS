package com.aadilmehraj.android.appliedcs.subjects.numbertheory.inverse;


import com.aadilmehraj.android.appliedcs.subjects.numbertheory.inverse.InverseUtils.Number;
import java.util.ArrayList;
import java.util.List;


/**
 * This method calculates the multiplicative and additive inverse of a number
 * using Galois Field.
 */
public class GaloisFieldUtils {

    public static final int IRREDUCIBLE_POLY_3 = 0b0000_1011;
    public static final int IRREDUCIBLE_POLY_8 = 0b0001_1011;
    private static final String TAG = GaloisFieldUtils.class.getSimpleName();

    private GaloisFieldUtils() {
    }

    /**
     * Calculates the additive and multiplicative inverse of all {@link Number}
     * less than 2^n.
     *
     * @param n defines the highest degree of polynomial
     * @return the {@link List<Number>} of all numbers less than 2^n.
     */
    public static List<Number> getAllInverseUsingGF(long n) {

        ArrayList<Number> numbers = new ArrayList<>();

        for (int i = 0; i < (1 << n); i++) {                // 1<<n represents 2^n
            for (int j = 0; j < (1 << n); j++) {

                if ((i == 0) && (j == 0)) {
                    numbers.add(new Number(0, 0, -1));
                    continue;
                }
                long result = GaloisFieldUtils.multiply(i, j, n);
                if (result == 1) {
                    numbers.add(new Number(i, i, j));
                }
            }
        }
        return numbers;
    }


    /**
     * This method multiplies the two polynomials a and b using polynomial arithmetic with modulus
     * of polynomial of degree n.
     *
     * @param a representing the first polynomial
     * @param b representing the second polynomial
     * @param n represents the highest degree of irreducible polynomial
     * @return the result of multiplication of two polynomials using GF(2^n).
     */
    public static long multiply(long a, long b, long n) {

        long inverse = 0;

        ArrayList<Long> multiples = (ArrayList<Long>) getAllMultiples(a, n);
        ArrayList<Integer> powers = (ArrayList<Integer>) getPowers(b);

        for (Integer power : powers) {
            inverse = inverse ^ multiples.get(power);
        }

        return inverse;
    }


    /**
     * This method get the {@link List<Long>} of all numbers which represents the multiples of x * x
     * n times.
     * i.e it calculates
     * b
     * b * b,
     * b * b * b,
     * b * b * b * b,
     * ....
     * b * b * b * ... * b (n times)
     *
     * multiplication is based on GF(2^n).
     *
     * @param b the number whose multiples are tobe found
     * @param n the GF(2^n)
     *
     * @return the {@link List<Long>} of numbers representing the multiples of n.
     */
    private static List<Long> getAllMultiples(long b, long n) {

        ArrayList<Long> multiples = new ArrayList<>();
        final long MOD;
        final int MAX;
        final long SHIFT = n - 1;

        if (n == 3) {
            MOD = IRREDUCIBLE_POLY_3;
            MAX = 0x7;
        } else if (n == 8) {
            MOD = IRREDUCIBLE_POLY_8;
            MAX = 0xFF;
        } else {
            throw new IllegalArgumentException("Galois Field not defined for n = " + n);
        }

        long prevPower = b;
        long temp;
        multiples.add(prevPower);

        while (--n > 0) {

            if ((prevPower & (1 << (SHIFT))) != 0) {
                temp = (prevPower << 1) ^ MOD;
                temp &= MAX;
                //        System.out.println(
                //            Long.toBinaryString(prevPower << 1)
                //                + " x "
                //                + Long.toBinaryString(i)
                //                + " = "
                //                + Long.toBinaryString(temp));
            } else {
                temp = prevPower << 1;
                temp &= MAX;
                //        System.out.println(
                //            Long.toBinaryString(prevPower << 1)
                //                + " x "
                //                + Long.toBinaryString(i)
                //                + " = "
                //                + Long.toBinaryString(temp));
            }

            multiples.add(temp);
            prevPower = temp;
        }
        return multiples;
    }


    /**
     * This method returns the {@link List<Integer>} representing the powers of a number
     * where bi, ith bit is 1 in a.
     * e.g      1001011 = {1, 2, 4, 7}  as ith bit is 1
     *
     * @param a the number whose powers are to be calculated
     * @return  the {@link List<Number>} representing the numbers.
     */
    private static List<Integer> getPowers(long a) {
        ArrayList<Integer> powers = new ArrayList<>();

        //    System.out.println("Binary Expansion of " + a + " : " + getBinaryString(getBytes(a)));
        int i = 0;
        while (a >= (1 << i)) {
            if ((a & (1 << i)) != 0) {
                powers.add(i++);
                continue;
            }
            i++;
        }
        return powers;
    }


    /**
     * This method returns the bytes in binary string.
     *
     * @param bytes the array that is to be displayed
     * @return the {@link String} of bytes.
     */
    public static String getBinaryString(byte[] bytes) {
        StringBuilder binaryString = new StringBuilder();

        for (byte b : bytes) {
            binaryString
                .append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'))
                .append(" ");
        }
        return binaryString.toString();
    }

    /**
     * Utility method that return the bytes value of n.
     *
     * @param n the number
     * @return the byte array
     */
    private static byte[] getBytes(long n) {
        return (new byte[]{(new Long(n)).byteValue()});
    }

}
