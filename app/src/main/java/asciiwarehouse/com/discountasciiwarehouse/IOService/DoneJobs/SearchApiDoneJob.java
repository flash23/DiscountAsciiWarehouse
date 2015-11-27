package asciiwarehouse.com.discountasciiwarehouse.IOService.DoneJobs;

import java.util.List;

import asciiwarehouse.com.discountasciiwarehouse.IOService.MODEL.WarehouseItemModel;

/**
 * Created by mirkomesner on 11/22/15.
 */
public class SearchApiDoneJob {

    public boolean succes = false;
    public List<WarehouseItemModel> warehouseItemModelList = null;

    public boolean reloaded = true;

    public SearchApiDoneJob(boolean succes, List<WarehouseItemModel> warehouseItemModelList, boolean reloaded)
    {
        this.succes = succes;
        this.reloaded = reloaded;
        if(warehouseItemModelList != null)
            this.warehouseItemModelList = warehouseItemModelList;
    }
}
