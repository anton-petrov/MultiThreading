package edu.petrov.multithreading;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

class MySemaphore implements Semaphore {

    private final AtomicInteger permits = new AtomicInteger(0);
    private final Object lock = new Object();

    public MySemaphore(final int permits) {
        setPermits(permits);
    }

    private void setPermits(final int permits) {
        this.permits.set(permits);
    }

    private int getPermits() {
        return this.permits.get();
    }

    @Override
    public void acquire() throws InterruptedException {
        synchronized (lock) {
            while (!(getPermits() > 0)) {
                lock.wait();
            }
            decrementPermits(1);
        }
    }

    @Override
    public void acquire(int permits) throws InterruptedException {
        synchronized (lock) {
            while (getPermits() - permits <= 0) {
                lock.wait();
            }
            decrementPermits(permits);
        }
    }

    private void decrementPermits(int permits) {
        this.permits.getAndAdd(-permits);
    }

    private void incrementPermits(int permits) {
        this.permits.getAndAdd(permits);
    }

    @Override
    public void release() {
        synchronized (lock) {
            incrementPermits(1);
            lock.notifyAll();
        }
    }

    @Override
    public void release(int permits) {
        synchronized (lock) {
            incrementPermits(permits);
            lock.notifyAll();
        }
    }

    @Override
    public int getAvailablePermits() {
        return permits.get();
    }
}


public class Main {

    private static Random random = new Random();

    private static int getRandomInteger(int lowBound, int highBound) {
        return random.nextInt(highBound - lowBound) + lowBound;
    }

    private static void testMySemaphore() {
        MySemaphore semaphore = new MySemaphore(2);
        IntStream.range(0, 10).forEach((i) -> {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println("Thread #" + i + " started!");
                    Thread.sleep(getRandomInteger(1000, 2000));
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int[] array = new int[]{1, 2, 3, 4}; // 1 + 4 + 9 + 16
        System.out.println(new MySquareSum().__getSquareSum(array, 4));
    }
}
