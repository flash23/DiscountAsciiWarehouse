package asciiwarehouse.com.discountasciiwarehouse.IOService.API;

import java.util.List;

import asciiwarehouse.com.discountasciiwarehouse.IOService.MODEL.WarehouseItemModel;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by mirkomesner on 11/20/15.
 */
//siple code using interface :)
public interface SearchApi {
    @GET("/search")
    public void getShopItems(
            @Query("skip") Integer skip,
            @Query("limit") Integer limit,
            @Query("q") String q,
            @Query("onlyInStock") int onlyInStock,
            CancelableCallback<List<WarehouseItemModel>> response);
}