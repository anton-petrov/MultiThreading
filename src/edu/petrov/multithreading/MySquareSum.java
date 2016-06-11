package edu.petrov.multithreading;

import sun.jvm.hotspot.opto.Phase;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Phaser;
import java.util.concurrent.RunnableFuture;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Created by anton on 6/10/16.
 */


public class MySquareSum implements SquareSum {

    private class SquareSumThread implements Runnable {

        final long[] result;
        final int resultIndex;
        final int[] data;
        final int beginIndex;
        final int endIndex;
        private final Phaser phaser;

        public SquareSumThread(Phaser phaser, long[] result, int resultIndex, int[] data, int beginIndex, int endIndex) {
            this.result = result;
            this.resultIndex = resultIndex;
            this.data = data;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.phaser = phaser;

            this.phaser.register();
        }

        @Override
        public void run() {
            result[resultIndex] = getPartialSquareSum(data, beginIndex, endIndex <= data.length ? endIndex : data.length);
            phaser.arrive();
        }

        private long getPartialSquareSum(int[] values, int beginIndex, int endIndex) {
            return Arrays.stream(values, beginIndex, endIndex).map(a -> a * a).reduce(0, (a, b) -> a + b);
        }
    }

    private ForkJoinPool executors = ForkJoinPool.commonPool();

    @Override
    public long getSquareSum(int[] values, int numberOfThreads) throws InterruptedException {
        long[] results = new long[numberOfThreads];
        Phaser phaser = new Phaser();
        phaser.register();
        for (int beginIndex = 0, resultIndex = 0, numberOfElements = values.length / numberOfThreads;
             beginIndex < values.length;
             beginIndex += numberOfElements, resultIndex++) {

            executors.submit(new SquareSumThread(phaser, results, resultIndex, values, beginIndex, beginIndex + numberOfElements));
        }
        phaser.arriveAndAwaitAdvance();
        return Arrays.stream(results).reduce(0, (a, b) -> a + b);
    }
}
