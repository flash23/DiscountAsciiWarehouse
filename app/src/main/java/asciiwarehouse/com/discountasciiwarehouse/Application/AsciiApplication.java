package asciiwarehouse.com.discountasciiwarehouse.Application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import asciiwarehouse.com.discountasciiwarehouse.Activity.AsciiItem.AsciiItemDetailsActivity;
import asciiwarehouse.com.discountasciiwarehouse.Activity.AsciiItem.OpenAsciiDetailsJob;
import asciiwarehouse.com.discountasciiwarehouse.Activity.Main.AsciiWarehouseActivity;
import asciiwarehouse.com.discountasciiwarehouse.Activity.Splash.CloseSplashEvent;
import asciiwarehouse.com.discountasciiwarehouse.Activity.Splash.SplashActivity;
import asciiwarehouse.com.discountasciiwarehouse.IOService.DoneJobs.ServiceErrorStop;
import asciiwarehouse.com.discountasciiwarehouse.IOService.DoneJobs.ServiceStarted;
import asciiwarehouse.com.discountasciiwarehouse.IOService.InOutService;
import de.greenrobot.event.EventBus;

/**
 * Created by mirkomesner on 11/22/15.
 */
public class AsciiApplication extends Application implements Application.ActivityLifecycleCallbacks {

    //We want to control activities and services life from one place application

    private static Context mContext;
    private static Activity currentAtivity;

    public static Activity getCurrentAtivity(){
        return currentAtivity;
    }

    public static Context getContext(){
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(this);

        this.mContext = this;
    }

    //region waiting for events for app activity life cycles
    //wait for service to start... all the network communication and caching is in service
    public void onEvent(ServiceStarted event){

        //LET'S see the splash ;)
        Handler handler = new Handler();
        final Context context = this;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //kill the splash
                EventBus.getDefault().postSticky(new CloseSplashEvent());

                lunchAsciiMain();

            }
        }, 1000);///we are to fast for splash ;)
    }

    //Relunch Service... we want it alive
    public void onEvent(ServiceErrorStop event){
        Intent i= new Intent(this, InOutService.class);
        this.startService(i);
    }

    //Relunch Service... we want it alive
    public void onEvent(OpenAsciiDetailsJob event){
        lunchAsciiDetails(event);
    }
    //endregion

    //region WE control lifecycle of activites

    private int resumed;
    private int stopped;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if(resumed == stopped )//first start 0 == 0 or else we came from background
        {
            EventBus.getDefault().register(this);

            //first start or app was removed from memory... we can build in support for saved states
            if(activity.getClass() == SplashActivity.class) {
                lunchService();
            }
        }
        ++resumed;

        currentAtivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;

        if(resumed == stopped)
            EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
    //endregion
    private void lunchService()
    {
        Intent i = new Intent(this, InOutService.class);
        i.putExtra("isFirstLunch", true);
        this.startService(i);
    }

    private void lunchAsciiMain()
    {
        if(currentAtivity!=null) {
            Intent mainActy = new Intent(currentAtivity, AsciiWarehouseActivity.class);
            currentAtivity.startActivity(mainActy);
        }
    }

    private void lunchAsciiDetails(OpenAsciiDetailsJob event)
    {
        if(currentAtivity!=null) {
            Intent deatilsActy = new Intent(currentAtivity, AsciiItemDetailsActivity.class);
            currentAtivity.startActivity(deatilsActy);
            //we don't pass data using old looking Intents :)
            EventBus.getDefault().postSticky(event.item);
        }
    }
}
