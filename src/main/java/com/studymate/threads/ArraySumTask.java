package com.studymate.threads;

/**
 * Helper thread that computes the sum of a slice of an int array.
 * Used to demonstrate basic parallel processing (Lab 8, exercise 1).
 */
public class ArraySumTask extends Thread {

    private final int[] data;
    private final int startInclusive;
    private final int endExclusive;
    private long partialSum;

    public ArraySumTask(int[] data, int startInclusive, int endExclusive) {
        this.data = data;
        this.startInclusive = startInclusive;
        this.endExclusive = endExclusive;
    }

    @Override
    public void run() {
        long sum = 0;
        for (int i = startInclusive; i < endExclusive; i++) {
            sum += data[i];
        }
        this.partialSum = sum;
    }

    public long getPartialSum() {
        return partialSum;
    }
}
