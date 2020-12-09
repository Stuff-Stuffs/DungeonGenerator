package io.github.stuff_stuffs.dungeon_generator.util;

import java.util.Random;

public class Xoroshiro256 extends Random {
    private long s0;
    private long s1;
    private long s2;
    private long s3;

    public Xoroshiro256(final long seed) {
        setSeed(seed);
    }

    @Override
    public synchronized void setSeed(final long seed) {
        s0 = seed;
        s1 = seed + 1;
        s2 = seed + 0xaaaaaaaaaaaaaaaaL;
        s3 = (seed + 3) * 0xaaaaaaaaaaaaaaaaL;
    }

    @Override
    public long nextLong() {
        final long result = s0 + s3;
        final long t = s1 << 17;
        s2 ^= s0;
        s3 ^= s1;
        s1 ^= s2;
        s0 ^= s3;
        s2 ^= t;
        s3 = (s3 << 45) | (s3 >>> 19);
        return result;
    }

    @Override
    public int nextInt() {
        return (int) (nextLong() >>> 32);
    }

    @Override
    public int nextInt(final int n) {
        return (int) nextLong(n);
    }

    public long nextLong(final long n) {
        if (n <= 0) {
            throw new IllegalArgumentException("illegal bound " + n + " (must be positive)");
        } else {
            long t = nextLong();
            final long nMinus1 = n - 1;
            if ((n & nMinus1) == 0) {
                return t >>> Long.numberOfLeadingZeros(nMinus1) & nMinus1;
            } else {
                long u = t >>> 1;
                while (u + nMinus1 - (t = u % n) < 0) {
                    u = nextLong() >>> 1;
                }

                return t;
            }
        }
    }

    @Override
    public double nextGaussian() {
        return super.nextGaussian();
    }

    @Override
    public double nextDouble() {
        return Double.longBitsToDouble(0x3ff0000000000000L | nextLong() >>> 12) - 1;
    }

    @Override
    public float nextFloat() {
        return (float) (nextLong() >>> 40) * 0x1.0p-24f;
    }

    @Override
    public boolean nextBoolean() {
        return nextLong() > 0;
    }

    @Override
    public void nextBytes(final byte[] bytes) {
        int i = bytes.length;

        while (i != 0) {
            int n = Math.min(i, 8);

            for (long bits = nextLong(); n-- != 0; bits >>= 8) {
                --i;
                bytes[i] = (byte) ((int) bits);
            }
        }
    }
}

