/*
 * ThreadPool.java
 *
 * Created on June 28, 2002, 8:28 PM
 */

package com.dnsalias.java.sqlclient.util;

import java.util.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class ThreadPool implements Runnable
{
    private static final int MAXIMUM_THREADS = 2;
    private static final long THREAD_EXPIRES = 1000*60*10; //Ten minutes
    
    private static ArrayList threads = new ArrayList();
    
    private Runnable runnable = null;
    private Thread thread;
    
    private ThreadPool() 
    {
        thread = new Thread(this);
        thread.start();
    }

    public void run()
    {
        boolean running = true;
        long lastran = System.currentTimeMillis();
        
        while(running)
        {
            try
            {
                if(runnable != null) 
                {
                    runnable.run();
                    
                    lastran = System.currentTimeMillis();
                    runnable = null;
                    
                    if(!threads.contains(this) && threads.size() < MAXIMUM_THREADS) threads.add(this);
                    System.out.println("Thread pool size: "+threads.size());
                }
                    
                try{Thread.sleep(THREAD_EXPIRES);} catch(InterruptedException e) {}
                
                if(lastran+THREAD_EXPIRES < System.currentTimeMillis() && runnable == null)
                {
                    running = false;
                    threads.remove(this);
                }
            }
            catch(Throwable e)
            {
                e.printStackTrace();
                runnable = null;
            }
        }
        
        System.out.println("Thread expired");
    }
    
    public static synchronized void run(Runnable runnable)
    {
        ThreadPool thread;
        
        if(threads.size() > 0) thread = (ThreadPool)threads.remove(0);
        else thread = new ThreadPool();
        
        thread.runnable = runnable;
        thread.thread.interrupt();
    }
}