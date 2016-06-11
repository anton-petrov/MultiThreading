package edu.petrov.multithreading;

import java.util.Random;
import java.util.stream.IntStream;

class MySemaphore implements Semaphore {

    private volatile int permits;
    private final Object lock = new Object();

    public MySemaphore(int permits) {
        this.permits = permits;
    }

    public void setPermits(int permits) {
        synchronized (lock) {
            this.permits = permits >= 0 ? permits : 0;
        }
    }

    @Override
    public void acquire() throws InterruptedException {
        synchronized (lock) {
            while (permits == 0) {
                lock.wait();
            }
            decrementPermits(1);
        }
    }

    @Override
    public void acquire(int permits) throws InterruptedException {
        synchronized (lock) {
            while (this.permits - permits <= 0) {
                lock.wait();
            }
            decrementPermits(permits);
        }
    }

    private void decrementPermits(int permits) {
        synchronized (lock) {
            setPermits(this.permits - permits);
        }
    }

    private void incrementPermits(int permits) {
        synchronized (lock) {
            setPermits(this.permits + permits);
        }
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
        return permits;
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

    public static void main(String[] args) throws InterruptedException {
        int[] array = new int[]{1, 2, 3, 4}; // 1 + 4 + 9 + 16
        System.out.println(new MySquareSum().getSquareSum(array, 4));
    }
}
