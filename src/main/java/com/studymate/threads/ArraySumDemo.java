package com.studymate.threads;

import java.util.Random;

/**
 * Small benchmark harness that compares sequential vs parallel
 * computation of the sum of a large int array.
 */
public class ArraySumDemo {

    public static void main(String[] args) throws InterruptedException {
        int size = 5_000_000;
        int[] data = new int[size];
        Random random = new Random(42);

        for (int i = 0; i < size; i++) {
            data[i] = random.nextInt(100);
        }

        // Sequential baseline
        long t0 = System.currentTimeMillis();
        long sequentialSum = 0;
        for (int value : data) {
            sequentialSum += value;
        }
        long t1 = System.currentTimeMillis();
        long seqTime = t1 - t0;

        System.out.println("Sequential: sum=" + sequentialSum + " time=" + seqTime + " ms");

        int[] threadCounts = {1, 2, 4, 8};

        for (int threads : threadCounts) {
            t0 = System.currentTimeMillis();
            ArraySumTask[] tasks = new ArraySumTask[threads];
            int chunk = size / threads;

            for (int i = 0; i < threads; i++) {
                int start = i * chunk;
                int end = (i == threads - 1) ? size : (i + 1) * chunk;
                tasks[i] = new ArraySumTask(data, start, end);
                tasks[i].start();
            }

            long parallelSum = 0;
            for (ArraySumTask task : tasks) {
                task.join();
                parallelSum += task.getPartialSum();
            }

            t1 = System.currentTimeMillis();
            long parTime = t1 - t0;

            System.out.println(threads + " threads: sum=" + parallelSum + " time=" + parTime + " ms");
        }
    }
}
