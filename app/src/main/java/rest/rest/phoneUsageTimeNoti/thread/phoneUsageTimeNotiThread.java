package rest.rest.phoneUsageTimeNoti.thread;

import android.os.Handler;


public class phoneUsageTimeNotiThread extends Thread{
    Handler handler;
    boolean isRun = true;

    public phoneUsageTimeNotiThread(Handler handler){
        this.handler = handler;
    }

    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }

    public void run(){
        while(isRun){
            handler.sendEmptyMessage(0);
            try{
                Thread.sleep(3600000);
            }catch (Exception e) {}
        }
    }
}

