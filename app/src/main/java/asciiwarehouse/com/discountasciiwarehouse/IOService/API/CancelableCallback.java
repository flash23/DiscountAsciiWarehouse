package asciiwarehouse.com.discountasciiwarehouse.IOService.API;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mirkomesner on 11/25/15.
    Just to be able to cancel request.. we should use RetroFit 2.0 for next realease of Ascii app ;)
 */

public abstract class CancelableCallback<T> implements Callback<T> {

    private static List<CancelableCallback> mList = new ArrayList<>();

    private boolean isCanceled = false;
    private Object mTag = null;

    private static Object monitor = new Object();
    private static int threadIn = 0;

    public static void cancelAll() {
        for (Iterator<CancelableCallback> iterator = mList.iterator(); iterator.hasNext(); ) {
            CancelableCallback callback = iterator.next();

            callback.isCanceled = true;
            iterator.remove();
        }
    }

    public static void cancel(Object tag) {
        if (tag != null)
            for (CancelableCallback callback : mList) {
                if (tag.equals(callback.mTag))
                    callback.cancel();
            }
    }

    public CancelableCallback() {
        mList.add(this);
    }

    public CancelableCallback(Object tag) {

        mTag = tag;
        mList.add(this);

    }

    public void cancel() {

        isCanceled = true;
        mList.remove(this);

    }

    @Override
    public final void success(T t, Response response) {

        mList.remove(this);

        if (!isCanceled) {
            onSuccess(t, response);
        }
    }

    @Override
    public final void failure(RetrofitError error) {

        mList.remove(this);

        if (!isCanceled)
            onFailure(error);
    }

    public abstract void onSuccess(T t, Response response);

    public abstract void onFailure(RetrofitError error);
}