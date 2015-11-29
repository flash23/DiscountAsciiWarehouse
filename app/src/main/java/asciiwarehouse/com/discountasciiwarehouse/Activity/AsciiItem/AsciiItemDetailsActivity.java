package asciiwarehouse.com.discountasciiwarehouse.Activity.AsciiItem;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import asciiwarehouse.com.discountasciiwarehouse.IOService.MODEL.WarehouseItemModel;
import asciiwarehouse.com.discountasciiwarehouse.R;
import de.greenrobot.event.EventBus;

/**
 * Created by mirkomesner on 11/27/15.
 */
///details of item
public class AsciiItemDetailsActivity extends Activity{

    private WarehouseItemModel item;

    TextView itemFace;
    TextView itemPrice;
    TextView itemOnlyOne;
    RelativeLayout itemBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ascii_details);

        itemFace = (TextView) findViewById(R.id.item_face);
        itemPrice = (TextView) findViewById(R.id.item_price);
        itemOnlyOne = (TextView) findViewById(R.id.item_onlyoneinstock);
        itemBuy = (RelativeLayout) findViewById(R.id.buyButton);


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
        itemPrice.setText("$"+item.price);

        itemOnlyOne.setVisibility(item.stock <= 1 ? View.VISIBLE : View.INVISIBLE);
        itemOnlyOne.setText(getResources().getString(item.stock == 1 ? R.string.onlyoneinstock : R.string.outofstock));

        itemBuy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //handle buy
            }
        });
    }

}
