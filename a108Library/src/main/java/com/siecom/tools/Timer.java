package com.siecom.tools;
public class Timer
{
  private long timeOutMs = 0L;
  private boolean bTimeOut = false;
  Timer tm = null;
  public Timer() {
    this.timeOutMs = 0L;
    this.bTimeOut = false;
  }

  public Timer(long timeOutMs) {
    this.timeOutMs = timeOutMs;
    this.bTimeOut = false;
  }

  public void setTimeOutMs(long ms) {
    this.timeOutMs = ms;
  }

  public long getTimeOutMs() {
    return this.timeOutMs;
  }

  public void setTimOut() {
    this.bTimeOut = true;
  }

  public void start() {
	  
    tm = this;

    Thread thread = new Thread(new Runnable()
    {
      public void run()
      {
        long startTime = System.currentTimeMillis();

        while (!tm.timeOut())
        {
          if (System.currentTimeMillis() - startTime >= tm.getTimeOutMs()) {
            tm.setTimOut();
            break;
          }
        }
      }
    });
    thread.start();
  }

  public boolean timeOut() {
    return this.bTimeOut;
  }
}