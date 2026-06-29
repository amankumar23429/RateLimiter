package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        RateLimiter rateLimiter = new RateLimiter(10 , 5);

        ExecutorService pool = Executors.newFixedThreadPool(10);

        Runnable task = () -> {
            if(rateLimiter.tryAcquire()) {
                System.out.println("Allowed " + Thread.currentThread().getName());
            }
            else{
                System.out.println("Rejected " + Thread.currentThread().getName());
            }
        };

        for(int i = 0; i<20; i++) {
            pool.submit(task);
        }

        try {
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
        rateLimiter.shutdown();
    }
}