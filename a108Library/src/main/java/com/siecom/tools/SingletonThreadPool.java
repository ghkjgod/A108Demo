package com.siecom.tools;

/**
 * Created by Administrator on 2015/10/29.
 */
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingletonThreadPool {
    private static ExecutorService  instance = Executors.newFixedThreadPool(1);
    private SingletonThreadPool(){

    }
    public static ExecutorService getInstance() {
        
        return instance;
    }
}
