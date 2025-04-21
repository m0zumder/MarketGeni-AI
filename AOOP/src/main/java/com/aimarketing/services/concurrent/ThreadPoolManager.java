package com.aimarketing.services.concurrent;

import java.util.concurrent.*;
import java.util.Map;
import java.util.HashMap;

public class ThreadPoolManager {
    private static ThreadPoolManager instance;
    private final Map<String, ExecutorService> executorServices;
    
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 2;
    private static final long KEEP_ALIVE_TIME = 60L;
    
    private ThreadPoolManager() {
        executorServices = new HashMap<>();
        initializeExecutors();
    }
    
    public static synchronized ThreadPoolManager getInstance() {
        if (instance == null) {
            instance = new ThreadPoolManager();
        }
        return instance;
    }
    
    private void initializeExecutors() {
        // Executor for AI tasks
        executorServices.put("ai", new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            new ThreadFactory() {
                private int count = 1;
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("AI-Worker-" + count++);
                    thread.setDaemon(true);
                    return thread;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
        ));
        
        // Executor for API tasks
        executorServices.put("api", new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            new ThreadFactory() {
                private int count = 1;
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("API-Worker-" + count++);
                    thread.setDaemon(true);
                    return thread;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
        ));
    }
    
    public Future<?> submitAITask(Runnable task) {
        return executorServices.get("ai").submit(task);
    }
    
    public <T> Future<T> submitAITask(Callable<T> task) {
        return executorServices.get("ai").submit(task);
    }
    
    public Future<?> submitAPITask(Runnable task) {
        return executorServices.get("api").submit(task);
    }
    
    public <T> Future<T> submitAPITask(Callable<T> task) {
        return executorServices.get("api").submit(task);
    }
    
    public void shutdown() {
        executorServices.values().forEach(ExecutorService::shutdown);
    }
    
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        boolean terminated = true;
        for (ExecutorService executor : executorServices.values()) {
            terminated &= executor.awaitTermination(timeout, unit);
        }
        return terminated;
    }
}