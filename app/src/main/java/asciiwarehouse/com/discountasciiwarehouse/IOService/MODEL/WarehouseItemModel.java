package asciiwarehouse.com.discountasciiwarehouse.IOService.MODEL;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by mirkomesner on 11/20/15.
 */
public class WarehouseItemModel {
    @Expose
    public String type;///" "face"
    @Expose
    public String id;//":"0-4itmqrfkz6e9izfr",
    @Expose
    public int size;//":24,
    @Expose
    public int price;//":379,
    @Expose
    public String face;//":"( .-. )",
    @Expose
    public int stock;//":7,
    @Expose
    public List<String> tags;//":["flat","bored"]
}
