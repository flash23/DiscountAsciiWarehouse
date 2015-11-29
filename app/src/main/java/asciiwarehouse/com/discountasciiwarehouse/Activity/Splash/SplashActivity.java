package asciiwarehouse.com.discountasciiwarehouse.Activity.Splash;

import android.app.Activity;
import android.os.Bundle;

import asciiwarehouse.com.discountasciiwarehouse.R;
import de.greenrobot.event.EventBus;

/**
 * Created by mirkomesner on 11/22/15.
 */
public class SplashActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(CloseSplashEvent event)
    {
        finish();
    }

}
