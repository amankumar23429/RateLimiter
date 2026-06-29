package org.example;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class RateLimiter {
    private final long capacity;
    private final long refillRatePerSecond;
    AtomicLong token;
    ScheduledExecutorService scheduler;

    public RateLimiter(long capacity, long refillRatePerSecond){
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.token = new AtomicLong(capacity);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::refill, 1, 1, TimeUnit.SECONDS);
    }

    void refill(){
        long newCount = Math.min(capacity, token.get()+refillRatePerSecond);
        token.set(newCount);
    }

    public boolean tryAcquire(){
        long currentToken;
        do{
            currentToken = token.get();
            if(currentToken==0) return false;
        }while(!token.compareAndSet(currentToken, currentToken-1));
        return true;
    }

    public void shutdown(){
        scheduler.shutdown();
    }
}
