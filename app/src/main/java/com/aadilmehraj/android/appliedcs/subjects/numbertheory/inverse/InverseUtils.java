package com.aadilmehraj.android.appliedcs.subjects.numbertheory.inverse;


import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;


/**
 * Utility class for finding additive and multiplicative inverse of numbers
 * using Tabular and Extended Euclidean Algorithm.
 */
public class InverseUtils {


    /**
     * Data class representing number with corresponding additive and multiplicative inverse.
     */
    public static class Number implements Parcelable {

        private long mNumber;
        private long mAdditiveInverse;
        private long mMultiplicativeInverse;

        public Number() {
        }

        public Number(long number, long additiveInverse, long multiplicativeInverse) {
            mNumber = number;
            mAdditiveInverse = additiveInverse;
            mMultiplicativeInverse = multiplicativeInverse;
        }

        protected Number(Parcel in) {
            mNumber = in.readLong();
            mAdditiveInverse = in.readLong();
            mMultiplicativeInverse = in.readLong();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(mNumber);
            dest.writeLong(mAdditiveInverse);
            dest.writeLong(mMultiplicativeInverse);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Number> CREATOR = new Creator<Number>() {
            @Override
            public Number createFromParcel(Parcel in) {
                return new Number(in);
            }

            @Override
            public Number[] newArray(int size) {
                return new Number[size];
            }
        };

        public void setNumber(long number) {
            mNumber = number;
        }

        public void setAdditiveInverse(long additiveInverse) {
            mAdditiveInverse = additiveInverse;
        }

        public void setMultiplicativeInverse(long multiplicativeInverse) {
            mMultiplicativeInverse = multiplicativeInverse;
        }

        public long getNumber() {
            return mNumber;
        }

        public long getAdditiveInverse() {
            return mAdditiveInverse;
        }

        public long getMultiplicativeInverse() {
            return mMultiplicativeInverse;
        }

        @Override
        public String toString() {
            return "Number{"
                + "mNumber= "
                + mNumber
                + ", mAdditiveInverse= "
                + mAdditiveInverse
                + ", mMultiplicativeInverse= "
                + mMultiplicativeInverse
                + '}';
        }
    }

    /**
     * This method calculates the additive and multiplicative inverses of all numbers
     * less than n with respect to (mod n).
     *
     * @param n positive integer defining (mod n).
     * @return {@link List<Number>} of all numbers from 0 to (n - 1).
     */
    public static List<Number> getAllInverses(long n) {
        ArrayList<Number> numbers = new ArrayList<>();

        if (n <= 0) {
            return numbers;
        }

        for (long i = 0; i < n; i++) {

            long additiveInverse = -1;
            long multiplicativeInverse = -1;
            Number number = new Number();
            number.setNumber(i);

            for (long j = 1; j < n; j++) {
                if (i == 0) {
                    additiveInverse = 0;
                    multiplicativeInverse = -1;
                    number.setAdditiveInverse(additiveInverse);
                    number.setMultiplicativeInverse(multiplicativeInverse);
                    break;
                }
                if ((i + j) % n == 0) {
                    additiveInverse = j;
                }

                if ((i * j) % n == 1) {
                    multiplicativeInverse = j;
                }
            }
            number.setAdditiveInverse(additiveInverse);
            number.setMultiplicativeInverse(multiplicativeInverse);
            numbers.add(number);
        }

        return numbers;
    }


    /**
     * Calculates the Greatest Common Divisor (GCD) of a and b using Euclid's Algorithm.
     *
     * @param a first number
     * @param b second number
     * @return the GCD of a and b.
     */
    public static long euclideanGCD(long a, long b) {
        if (b == 0) {
            return a;
        }
        return euclideanGCD(b, a % b);
    }


    /**
     * Calculates Greatest Common Divisor (GCD), x and y for a and b
     * using Extended Euclidean Algorithm.
     *
     * Here GCD(a, b) = a * x + b * y
     *
     * @param a first number
     * @param b second number
     * @return an Array of size = 3, with elements as GCD(a,b), x, y respectively.
     */
    public static long[] extendedEuclideanGCD(long a, long b) {
        long[] xy = new long[3];
        long s1 = 1, s2 = 0;
        long t1 = 0, t2 = 1;
        long q = -1, temp;
        while (b > 0) {
            q = a / b;

            temp = a % b;
            a = b;
            b = temp;

            temp = s1 - q * s2;
            s1 = s2;
            s2 = temp;

            temp = t1 - q * t2;
            t1 = t2;
            t2 = temp;
        }
        xy[0] = a;
        xy[1] = s1;
        xy[2] = t1;

        return xy;
    }


    /**
     * Calculates the multiplicative inverse of a mod n using Extended Euclidean Algorithm.
     * Here a and n should be relatively prime to each other otherwise inverse doesn't exist.
     *
     * @param n define the modulus value.
     * @param a defines the number whose multiplicative inverse is to be found.
     * @return the multiplicative inverse of 'a' otherwise -1 if inverse doesn't exist.
     */
    public static long getMultiplicativeInverse(long n, long a) {

        if (euclideanGCD(n, a) != 1) {
            return -1;
        }
        long[] xy = extendedEuclideanGCD(n, a);
        long inverse = xy[2];
        // If inverse is negative, convert it to positive value.
        if (inverse < 0) {
            long q = Math.abs(inverse / n) + 1;
            inverse = inverse + q * n;
        }
        return inverse;
    }

    /**
     * Calculates the Additive inverse of a mod n.
     *
     * @param n define the modulus value
     * @param a defines the number whose additive inverse is to be found.
     *
     * @return  the additive inverse of a mod n.
     */
    public static long getAdditiveInverse(long n, long a) {
        return n - a;
    }


    /**
     * This method return the {@link Number} that contains Additive and multiplicative inverse
     * (if exists) for a mod n.
     *
     * @param n define the modulus value
     * @param a defines the number whose additive and multiplicative inverse is to be found.
     *
     * @return the {@link Number} containing Additive and multiplicative inverse.
     */
    public static Number getInverse(long n, long a) {
        long additiveInverse = getAdditiveInverse(n, a);
        long multiplicativeInverse = getMultiplicativeInverse(n, a);

        return new Number(a, additiveInverse, multiplicativeInverse);
    }


    /**
     * This method calculates the inverses of all {@link Number} less than n
     * using Extended Euclidean Algorithm.
     *
     *
     * @param n defines the modulus value
     * @return  {@link List<Number>} consisting of additive and multiplicative inverse.
     */
    public static List<Number> getAllInversesUsingGCD(long n) {
        ArrayList<Number> numbers = new ArrayList<>();

        if (n <= 0) {
            return numbers;
        }

        for (int i = 0; i < n; i++) {

            if (i == 0) {
                numbers.add(new Number(0, 0, -1));
                continue;
            }
            numbers.add(getInverse(n, i));
        }
        return numbers;
    }
}
