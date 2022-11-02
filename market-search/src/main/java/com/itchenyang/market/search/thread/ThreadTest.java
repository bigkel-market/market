package com.itchenyang.market.search.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author BigKel
 * @createTime 2022/10/22
 */
public class ThreadTest {
    /**
     * 串行化
     */
    public void Serialization() throws ExecutionException, InterruptedException  {
        /**
         * 线程串行化
         * 1) thenRunAsync  不能获取到上一步结果，也没有返回值，直接执行
         * 2) thenAcceptAsync  能够能获取到上一步结果，没有返回值
         * 3) thenApplyAsync   能够获取到上一步结果，也有返回值
         */
        ExecutorService executor = Executors.newFixedThreadPool(5);
        System.out.println("异步执行开始");
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程为: " + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果为: " + i);
            return i;
        }, executor).thenApplyAsync(res -> {
            System.out.println("ApplyAsync");
            return res * 2;
        }, executor);
        // Integer result = future.get();
        System.out.println("异步执行结束");
    }

    public void BothAllRunSuccess() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        System.out.println("异步执行开始");
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程为: " + Thread.currentThread().getId());
            return "Hello";
        }, executor);
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程为: " + Thread.currentThread().getId());
            return "World";
        }, executor);
        // 任务1和任务2中，有任何一个执行异常，则这步不会执行
//        future1.runAfterBothAsync(future2, () -> {
//            System.out.println("任务1和任务2都执行完后，执行");
//        }, executor);
//        future1.thenAcceptBothAsync(future2, (f1, f2) -> {
//            System.out.println(f1 + " " + f2);
//        }, executor);
        CompletableFuture<String> result = future1.thenCombineAsync(future2, (f1, f2) -> {
            System.out.println(f1 + " " + f2);
            return f1 + " " + f2;
        }, executor);
        System.out.println("异步执行结束: " + result.get());
    }

    public void EitherAllRunSuccess() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        System.out.println("异步执行开始");
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程为: " + Thread.currentThread().getId());
            return 10 / 2;
        }, executor);
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            System.out.println("任务2线程为: " + Thread.currentThread().getId());
            return 10 / 5;
        }, executor);
        /**
         * 只要调用runAfterEitherAsync方法的任务不出异常，
         * 那么无论任务2是没开始执行，还是任务2有异常，则以下都会执行
         * 反之，以下不会执行
         */
//        future1.runAfterEitherAsync(future2, () -> {
//            System.out.println("任务1和任务2其中一个执行完后，执行");
//        }, executor);
        future1.acceptEitherAsync(future2, (res) -> {
            System.out.println("res: " + res * 2);
        }, executor);
//        CompletableFuture<Object> result = future1.applyToEitherAsync(future2, (res) -> {
//            System.out.println("res: " + res * 2);
//            return res * 2;
//        }, executor);
        System.out.println("异步执行结束");
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        System.out.println("异步执行开始");
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程为: " + Thread.currentThread().getId());
            return 10 / 1;
        }, executor);
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程为: " + Thread.currentThread().getId());
            return 10 / 0;
        }, executor);
        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程为: " + Thread.currentThread().getId());
            return 10 / 0;
        }, executor);

//        CompletableFuture<Void> allOf = CompletableFuture.allOf(future1, future2, future3);
        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(future1, future2, future3);
        anyOf.get();
        System.out.println("异步执行结束");
    }
}
