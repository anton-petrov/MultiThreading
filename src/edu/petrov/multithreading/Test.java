package edu.petrov.multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * Created by anton on 6/12/16.
 */
public class Test {

    public static void main(String[] args) throws Exception {
        test2();
    }

    private static void test() {
        Exchanger<String> exchanger = new Exchanger<>();
        Random random = new Random();
        IntStream.range(0, 2).forEach((i) -> new Thread(() -> {
            try {
                Thread.sleep(random.nextInt(1000));
                String name = Thread.currentThread().getName();
                System.out.println(name + " ready to exchange");
                System.out.println(name + " < - > " + exchanger.exchange(name));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    public static void test2() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> f = executorService.submit(() -> {
            throw new RuntimeException("Exception happened");
        });
        try {
            System.out.println("result: " + f.get());
        } catch (Exception e) {
        }
        executorService.shutdown();
    }

    private static void test3() throws ExecutionException, InterruptedException {
        List<Callable<String>> callables = new ArrayList<>();
        IntStream.range(0, 3).forEach(i -> callables.add(() -> String.valueOf(i)));

        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<String>> result = executor.invokeAll(callables);

        for (Future f : result) {
            System.out.println(f.get());
        }
        executor.shutdown();
    }

    public static void test4() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        System.out.println("Task scheduled");
        executorService.scheduleAtFixedRate(() -> System.out.println("Task executed"), 1, 1, TimeUnit.SECONDS);
    }

}
