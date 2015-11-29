package asciiwarehouse.com.discountasciiwarehouse.Activity.AsciiItem;

import asciiwarehouse.com.discountasciiwarehouse.IOService.JOBS.GenericJob;
import asciiwarehouse.com.discountasciiwarehouse.IOService.MODEL.WarehouseItemModel;

/**
 * Created by mirkomesner on 11/27/15.
 */
//We are not sending intents around....using fancy and simple EventBus
public class OpenAsciiDetailsJob extends GenericJob{
    public WarehouseItemModel item;

    public OpenAsciiDetailsJob(WarehouseItemModel item)
    {
        this.item = item;
    }
}
