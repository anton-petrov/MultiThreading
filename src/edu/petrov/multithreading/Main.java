package edu.petrov.multithreading;

import java.util.Random;

class MySemaphore implements Semaphore {

    private volatile int permits;

    public MySemaphore(int permits) {
        this.permits = permits;
    }

    public synchronized void setPermits(int permits) {
        this.permits = permits >= 0 ? permits : 0;
    }

    @Override
    public synchronized void acquire() throws InterruptedException {
        while (permits == 0) {
            wait();
        }
        decrementPermits(1);
    }

    @Override
    public synchronized void acquire(int permits) throws InterruptedException {
        while (this.permits - permits <= 0) {
            wait();
        }
        decrementPermits(permits);
    }

    private synchronized void decrementPermits(int permits) {
        setPermits(this.permits - permits);
    }

    private synchronized void incrementPermits(int permits) {
        setPermits(this.permits + permits);
    }

    @Override
    public synchronized void release() {
        incrementPermits(1);
        notifyAll();
    }

    @Override
    public synchronized void release(int permits) {
        incrementPermits(permits);
        notifyAll();
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

    public static void main(String[] args) throws InterruptedException {


        MySemaphore semaphore = new MySemaphore(2);

        for (int i = 1; i <= 10; i++) {
            final Integer threadNumber = i;
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println("Thread #" + threadNumber + " started!");
                    Thread.sleep(getRandomInteger(1000, 2000));
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
