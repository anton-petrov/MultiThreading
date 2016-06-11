package edu.petrov.multithreading;

/**
 * Created by anton on 6/10/16.
 */
public interface SquareSum {
    long getSquareSum(int[] values, int numberOfThreads) throws InterruptedException;
}
