package edu.petrov.multithreading;

import java.util.concurrent.atomic.AtomicInteger;

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