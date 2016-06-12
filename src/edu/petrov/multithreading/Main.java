package edu.petrov.multithreading;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

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

    private static void testMySquareSum() throws ExecutionException, InterruptedException {
        int[] array = new int[]{1, 2, 3, 4}; // 1 + 4 + 9 + 16
        System.out.println(new MySquareSum().__getSquareSum(array, 4));
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        testMySquareSum();
        testMySemaphore();
    }
}
