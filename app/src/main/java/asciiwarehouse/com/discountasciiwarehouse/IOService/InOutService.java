package asciiwarehouse.com.discountasciiwarehouse.IOService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

import asciiwarehouse.com.discountasciiwarehouse.IOService.DOJOBS.GenericDoJob;
import asciiwarehouse.com.discountasciiwarehouse.IOService.DOJOBS.SearchApiDoJob;
import asciiwarehouse.com.discountasciiwarehouse.IOService.DoneJobs.ServiceStarted;
import asciiwarehouse.com.discountasciiwarehouse.IOService.JOBS.SearchApiJob;
import de.greenrobot.event.EventBus;

/**
 * Created by mirkomesner on 11/20/15.
 */
//We are using Service for network communication because we want persistence.
//When user moves our app in the background Service will stay alive and it will finish all given tasks
//If we have had implemented network communication in Activities user would be able to cancel upload(of lets say big image)
// by just moving the app in background.
//For communication between Service and Activities we use EventBus library. No need to worry about broadcasters handlers etc..
//If we start to expend this app with location services, notification handling etc... all of that should go in this Service
public class InOutService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);

        synchronized (monitorWrite) {
            jobs = new ArrayList<GenericDoJob>();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        if(intent.getBooleanExtra("isFirstLunch",false))
            EventBus.getDefault().postSticky(new ServiceStarted());

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    List<GenericDoJob> jobs;
    boolean isRunning = false;

    public class MonitorObject {
    }

    MonitorObject monitorRead = new MonitorObject();
    MonitorObject monitorWrite = new MonitorObject();

    //Small implementation of producers consumers problem...Managing of network requests like jobs on the line.
    //The be correct lunching of requests is done synchronously but they are all run in there own threads
    //The thread in which the service shall run
    private class serviceThread implements Runnable {

        public void run() {
            try {
                while (true) {
                    synchronized (monitorRead) {
                        if (jobs.size() == 0) {
                            isRunning = false;
                            break;
                        }

                        GenericDoJob job = jobs.get(0);
                        job.doJob();
                    }

                    synchronized (monitorWrite) {
                        jobs.remove(0);
                    }

                    try {
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void runJobs() {
        if (!isRunning)
        {
            isRunning = true;
            Thread serviceThread = new Thread(new serviceThread());
            serviceThread.start();
        }
    }

    ///////////////////////////////////////

    // This method will be called when a SomeOtherEvent is posted
    //Here we inform object who is listening for this EventBus event... In this case adapter requested items for Ascii warehouse
    public void onEvent(SearchApiJob event){
        synchronized (monitorWrite) {
            SearchApiJob searchJob =  event;
            jobs.add(new SearchApiDoJob(searchJob));
        }
        runJobs();
    }
}
