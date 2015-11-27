package asciiwarehouse.com.discountasciiwarehouse.Activity.AsciiItem;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import asciiwarehouse.com.discountasciiwarehouse.IOService.MODEL.WarehouseItemModel;
import asciiwarehouse.com.discountasciiwarehouse.R;
import de.greenrobot.event.EventBus;

/**
 * Created by mirkomesner on 11/27/15.
 */
public class AsciiItemDetailsActivity extends Activity{

    private WarehouseItemModel item;

    TextView itemFace;
    TextView itemPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ascii_details);

        itemFace = (TextView) findViewById(R.id.item_face);
        itemPrice = (TextView) findViewById(R.id.item_price);

        EventBus.getDefault().registerSticky(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(WarehouseItemModel item)
    {
        this.item = item;
        EventBus.getDefault().removeStickyEvent(item);

        populateUI();
    }

    private void populateUI()
    {
        itemFace.setText(item.face);
        itemPrice.setText(item.price+"");
    }

}
