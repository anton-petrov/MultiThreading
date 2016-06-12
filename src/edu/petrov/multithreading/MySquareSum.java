package edu.petrov.multithreading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by anton on 6/10/16.
 */


public class MySquareSum implements SquareSum {

    private long getPartialSquareSum(int[] values, int beginIndex, int endIndex) {
        return Arrays.stream(values, beginIndex, endIndex <= values.length ? endIndex : values.length).map(a -> a * a)
                .reduce(0, (a, b) -> a + b);
    }

    private class SquareSumCallable implements Callable<Long> {

        final int[] data;
        final int beginIndex;
        final int endIndex;
        private final Phaser phaser;

        public SquareSumCallable(final int[] data, int beginIndex, int endIndex, Phaser phaser) {
            this.data = data;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.phaser = phaser;

            this.phaser.register();
        }

        @Override
        public Long call() throws Exception {
            Long result = getPartialSquareSum(data, beginIndex, endIndex);
            phaser.arrive();
            return result;
        }
    }

    private class SquareSumRunnable implements Runnable {

        final long[] result;
        final int resultIndex;
        final int[] data;
        final int beginIndex;
        final int endIndex;
        private final Phaser phaser;

        public SquareSumRunnable(Phaser phaser, long[] result, int resultIndex, int[] data, int beginIndex, int endIndex) {
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
            result[resultIndex] = getPartialSquareSum(data, beginIndex, endIndex);
            phaser.arrive();
        }

    }

    /**
     * Calculate sum of squares for integer array elements.
     * Implements using ForkJoinPool and Runnable
     *
     * @param values
     * @param numberOfThreads
     * @return
     * @throws InterruptedException
     */
    @Override
    public long getSquareSum(final int[] values, int numberOfThreads) throws InterruptedException {
        if (numberOfThreads > values.length) {
            numberOfThreads = values.length / 2;
        }
        final ForkJoinPool threads = ForkJoinPool.commonPool();
        long[] results = new long[numberOfThreads];
        Phaser phaser = new Phaser();
        phaser.register();
        for (int beginIndex = 0, resultIndex = 0, numberOfElements = values.length / numberOfThreads;
             resultIndex < numberOfThreads;
             beginIndex += numberOfElements, resultIndex++) {

            int endIndex = resultIndex == numberOfThreads - 1 ? values.length : beginIndex + numberOfElements;
            threads.submit(new SquareSumRunnable(phaser, results, resultIndex, values, beginIndex, endIndex));
        }
        phaser.arriveAndAwaitAdvance();
        return Arrays.stream(results).reduce(0, (a, b) -> a + b);
    }


    /**
     * Calculate sum of squares for integer array elements.
     * Implements using ExecutorService and Callable
     *
     * @param values
     * @param numberOfThreads
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public long __getSquareSum(final int[] values, int numberOfThreads) throws ExecutionException, InterruptedException {
        if (numberOfThreads > values.length) {
            numberOfThreads = values.length / 2;
        }
        final int numberOfElements = values.length / numberOfThreads;
        Phaser phaser = new Phaser();
        phaser.register();
        List<Callable<Long>> workThreads = new ArrayList<>();
        for (int beginIndex = 0, currentThreadNumber = 0; currentThreadNumber < numberOfThreads;
             beginIndex += numberOfElements, currentThreadNumber++) {

            int endIndex = currentThreadNumber == numberOfThreads - 1 ? values.length : beginIndex + numberOfElements;
            workThreads.add(new SquareSumCallable(values, beginIndex, endIndex, phaser));
        }

        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<Long>> result = executor.invokeAll(workThreads);

        long resultSquareSum = 0;
        for (Future<Long> f : result) {
            resultSquareSum += f.get();
        }
        executor.shutdown();

        return resultSquareSum;
    }

}
