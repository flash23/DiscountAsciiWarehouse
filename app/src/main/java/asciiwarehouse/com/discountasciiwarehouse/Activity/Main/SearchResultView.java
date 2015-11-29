package asciiwarehouse.com.discountasciiwarehouse.Activity.Main;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import asciiwarehouse.com.discountasciiwarehouse.R;

/**
 * Created by mirkomesner on 11/23/15.
 */
//cell of RecycleView
public class SearchResultView extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView itemFace;
    public TextView itemPrice;
    public TextView itemInStock;

    public TextView loadingRL;

    public ViewHolderClicks mListener;

    public SearchResultView(View itemView, ViewHolderClicks listener) {
        super(itemView);

        itemFace = (TextView) itemView.findViewById(R.id.item_face);
        itemPrice = (TextView) itemView.findViewById(R.id.item_price);
        itemInStock = (TextView) itemView.findViewById(R.id.item_instock);

        itemView.setOnClickListener(this);

        loadingRL = (TextView) itemView.findViewById(R.id.loading);

        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        mListener.onClick(view, getLayoutPosition());
    }

    public static interface ViewHolderClicks {
        public void onClick(View caller, int position);

    }
}
