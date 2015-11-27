package asciiwarehouse.com.discountasciiwarehouse.IOService.DOJOBS;

import android.app.Activity;

import java.util.List;

import asciiwarehouse.com.discountasciiwarehouse.IOService.API.Api;
import asciiwarehouse.com.discountasciiwarehouse.IOService.API.CancelableCallback;
import asciiwarehouse.com.discountasciiwarehouse.IOService.API.SearchApi;
import asciiwarehouse.com.discountasciiwarehouse.IOService.DoneJobs.SearchApiDoneJob;
import asciiwarehouse.com.discountasciiwarehouse.IOService.JOBS.SearchApiJob;
import asciiwarehouse.com.discountasciiwarehouse.IOService.MODEL.WarehouseItemModel;
import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mirkomesner on 11/20/15.
 */
public class SearchApiDoJob implements GenericDoJob {
    SearchApiJob searchJob;
    Activity context;

    public static String TAG = "SEARCHAPI";

    public static int number = 0;

    public SearchApiDoJob(SearchApiJob searchJob)
    {
        this.searchJob = searchJob;
    }

    @Override
    public void doJob() {

        SearchApi search = Api.getInstance().restAdapter.create(SearchApi.class);

        //From some reason onlyInStock is int not bool
        search.getShopItems(searchJob.skip, searchJob.limit, searchJob.q, searchJob.onlyInStock?1:0, new CancelableCallback<List<WarehouseItemModel>>(TAG) {
            @Override
            public void onSuccess(List<WarehouseItemModel> warehouseItemModelList, Response response) {
                EventBus.getDefault().post(new SearchApiDoneJob(true, warehouseItemModelList, searchJob.reload));
            }

            public void onFailure(RetrofitError error) {
                EventBus.getDefault().post(new SearchApiDoneJob(false, null, false));
            }
        });

    }
}
