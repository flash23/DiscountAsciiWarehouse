package asciiwarehouse.com.discountasciiwarehouse.Activity.Main;

import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import asciiwarehouse.com.discountasciiwarehouse.Activity.AsciiItem.OpenAsciiDetailsJob;
import asciiwarehouse.com.discountasciiwarehouse.IOService.API.CancelableCallback;
import asciiwarehouse.com.discountasciiwarehouse.IOService.DoneJobs.SearchApiDoneJob;
import asciiwarehouse.com.discountasciiwarehouse.IOService.JOBS.SearchApiJob;
import asciiwarehouse.com.discountasciiwarehouse.IOService.MODEL.WarehouseItemModel;
import asciiwarehouse.com.discountasciiwarehouse.R;
import de.greenrobot.event.EventBus;

/**
 * Created by mirkomesner on 11/22/15.
 */
public class AsciiRecyclerViewAdapter extends RecyclerView.Adapter<SearchResultView> {

    public List<WarehouseItemModel> items;
    public int[]spansize;

    private int numOfRows = 5;

    AsciiWarehouseActivity context;

    public static int GRID_SPAN = 60;

    public AsciiRecyclerViewAdapter(AsciiWarehouseActivity context)
    {
        this.context = context;
        EventBus.getDefault().register(this);
        spansize = new int[0];
        items = new ArrayList<WarehouseItemModel>();

        calculateNumOfRows();
    }

    //region
    // We need to calculate widths of "grid" cells depending on smile
    //We use RecyclerView with Grid layout manager. Because RecyclerView with GridLayoutManager has fixed number of columns and rows and
    //we "hack" the RecyclerView by using more columns then we need(GRID_SPAN = 60). As we incrise GRID_SPAN number we will have better
    //"fit" of cells to there actual sizes. Of corse we span one row to match the RecyclerView width

    //This number will dictate  how many items to preload per screen... we can assume that in majority of case we will fit 3 smiles per row
    //It can be refined more
    //We don't load one item per request because this is expensive for network and server...
    // we try to guess how many items will fit the screen
    private void calculateNumOfRows()
    {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //Action Bar Height
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        int actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        //Status Bar Height
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        int layoutheight = metrics.heightPixels - actionBarHeight - statusBarHeight;

        View layoutView = LayoutInflater.from(context).inflate(R.layout.search_result_view, null);
        TextView faceTV = (TextView)layoutView.findViewById(R.id.item_face);
        TextView priceTV = (TextView)layoutView.findViewById(R.id.item_price);
        TextView instockTV = (TextView)layoutView.findViewById(R.id.item_instock);

        faceTV.setText("(._.)");
        priceTV.setText("100");
        instockTV.setText("30");

        layoutView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int rowHeight = layoutView.getMeasuredWidth();

        numOfRows = (int)((float)layoutheight/(float)rowHeight);

        int y=0;
    }

    //Fancy little algorithm for calculating spans of each cell so the cells will look nice in grid... It can be optimized more by
    //look up table because layoutView.measure(ViewGroup.LayoutP...  is expensive... and there is no need to recalculate every
    //time we load more items from server
    public void calculateSpanSize()
    {
        final View layoutView = LayoutInflater.from(context).inflate(R.layout.search_result_view, null);
        TextView faceTV = (TextView)layoutView.findViewById(R.id.item_face);
        TextView priceTV = (TextView)layoutView.findViewById(R.id.item_price);
        TextView instockTV = (TextView)layoutView.findViewById(R.id.item_instock);
        final DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        spansize = new int[items.size()];
        int curSubSpan = 0;
        int startIndexOfSpan = 0;
        for(int i = 0; i < spansize.length; i++)
        {
            faceTV.setText(items.get(i).face);
            priceTV.setText(items.get(i).price+"");
            instockTV.setText(items.get(i).stock + "");
            layoutView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int width=layoutView.getMeasuredWidth();

            spansize[i] = (int)((float)width/((float)metrics.widthPixels/(float)GRID_SPAN)+3.0f);

            curSubSpan+=spansize[i];

            if(curSubSpan==GRID_SPAN) {
                curSubSpan = 0;
                startIndexOfSpan = i+1;
            }
            else if(curSubSpan>GRID_SPAN && i < (spansize.length - 1))
            {
                int delta = GRID_SPAN - (curSubSpan - spansize[i]);

                while(delta>0)
                {
                    int m = delta%(i-startIndexOfSpan);
                    int index = startIndexOfSpan + m;
                    spansize[index]++;
                    delta--;
                }

                curSubSpan = spansize[i];
                startIndexOfSpan = i;
            }
            else if(i == (spansize.length - 1))
            {
                if(curSubSpan>GRID_SPAN)
                {
                    int delta = GRID_SPAN - (curSubSpan - spansize[i]);

                    while(delta>0)
                    {
                        int m = delta%(i-startIndexOfSpan);
                        int index = startIndexOfSpan + m;
                        spansize[index]++;
                        delta--;
                    }

                     spansize[i] = GRID_SPAN;
                }
                else
                {
                    int delta = GRID_SPAN - curSubSpan;

                    while(delta>0)
                    {
                        int m = delta%(i + 1 -startIndexOfSpan);
                        int index = startIndexOfSpan + m;
                        spansize[index]++;
                        delta--;
                    }

                }
            }
        }

    }
    //endregion

    @Override
    public SearchResultView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_view, null);

        SearchResultView rcv = new SearchResultView(layoutView,new SearchResultView.ViewHolderClicks() {
            public void onClick(View v, int position) {
                WarehouseItemModel item = items.get(position);
                EventBus.getDefault().post(new OpenAsciiDetailsJob(item));
            };
        });

        return rcv;
    }

    @Override
    public void onBindViewHolder(SearchResultView holder, int position) {
        holder.itemFace.setText(items.get(position).face);
        holder.itemPrice.setText(items.get(position).price + "");
        holder.itemInStock.setText(items.get(position).stock + "");

        holder.loadingRL.setAlpha(0.0f);//we recycle so always turnoff loading
        if(itemLoading == position) {
            if(holder.itemView.getWidth()>0 && holder.itemView.getHeight()>0) {
                holder.loadingRL.setWidth(holder.itemView.getWidth());
                holder.loadingRL.setHeight(holder.itemView.getHeight());
            }
//            AlphaAnimation anim = new AlphaAnimation(0.0f,1.0f);
//            anim.setFillAfter(true);
//            anim.setDuration(1000);
            holder.loadingRL.animate().alpha(1.0f).setDuration(1000);

            itemLoading = -1;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //Request to fill one screen of items
    int itemLoading = -1;
    public void fetchItems(String searchString, boolean onlyInStock, boolean reload)
    {
        context.refresher.post(new Runnable() {
            @Override
            public void run() {
                context.refresher.setRefreshing(true);
            }
        });

        CancelableCallback.cancelAll();
        //CancelableCallback.cancel(SearchApiDoJob.TAG);
        if(!reload)
        {
            itemLoading = items.size()-1;
            notifyItemChanged(items.size()-1);//we want loading text on last item
        }
        EventBus.getDefault().post(new SearchApiJob(reload?0:items.size(), 3*numOfRows, searchString, onlyInStock, reload));
    }

    public void onEventBackgroundThread(SearchApiDoneJob event)
    {
        if(event.succes) {
            if(event.reloaded) {
                context.previousTotal = 0;
                if(items.size()>0)
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        context.recyclerView.scrollToPosition(0);
                    }
                });
                items = event.warehouseItemModelList;

            } else
                items.addAll(event.warehouseItemModelList);

            calculateSpanSize();
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();

                }
            });
        }

        context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    context.refresher.setRefreshing(false);

            }
        });

    }
}
