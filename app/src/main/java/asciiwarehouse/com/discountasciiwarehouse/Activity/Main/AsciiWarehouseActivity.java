package asciiwarehouse.com.discountasciiwarehouse.Activity.Main;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import asciiwarehouse.com.discountasciiwarehouse.R;
import de.greenrobot.event.EventBus;

public class AsciiWarehouseActivity extends AppCompatActivity {

    public SwipeRefreshLayout refresher;
    public RecyclerView recyclerView;
    private ActionBar actionBar;
    private EditText search;
    private CheckBox instockCheckBox;
    private AsciiRecyclerViewAdapter recyclerViewAdapter;
    private GridLayoutManager gaggeredGridLayoutManager;

    public int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    boolean firstScroll = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ascii_warehouse);

        setupUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ///TODO

        int visPos = gaggeredGridLayoutManager.findFirstCompletelyVisibleItemPosition();
        //it can be made better but this is ok for now...
        // on start visPos is -1
        if(visPos==-1)
            populateUI(true);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void setupUI()
    {
        refresher = (SwipeRefreshLayout)findViewById(R.id.asciirefresher);
        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                           @Override
                                           public void onRefresh() {populateUI(true);
                                           }
                                       });

        recyclerView=(RecyclerView) findViewById(R.id.asciirecyclerview);
        recyclerViewAdapter=new AsciiRecyclerViewAdapter(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.setHasFixedSize(true);

        gaggeredGridLayoutManager = new GridLayoutManager(this,AsciiRecyclerViewAdapter.GRID_SPAN);

        gaggeredGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return recyclerViewAdapter.spansize.length > 0 ? recyclerViewAdapter.spansize[position] : 0;
            }
        });

        recyclerView.setLayoutManager(gaggeredGridLayoutManager);

        //We want to load when user reaches end of RecyclerView
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(firstScroll)
                {
                    firstScroll = false;
                    return;
                }

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = gaggeredGridLayoutManager.getItemCount();
                firstVisibleItem = gaggeredGridLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }

                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    populateUI(false);

                    loading = true;
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        // add the custom view to the action bar
        actionBar.setCustomView(R.layout.actionbar_view);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

        search = (EditText) actionBar.getCustomView().findViewById( R.id.searchfield);

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String searchString = s.toString();
                populateUI(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view == search) {
                    if (b) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(view, 0);
                    } else {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search.clearFocus();
                if(search.getText().toString()!="")
                    populateUI(true);
                return false;
            }
        });


        instockCheckBox = (CheckBox) findViewById(R.id.inStockCheckBox);
        instockCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                populateUI(true);
            }
            }
        );
    }

    private void populateUI(boolean reload)
    {
        recyclerViewAdapter.fetchItems(search.getText().toString(), instockCheckBox.isChecked(), reload);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //We recalculate cell widths.... It can be optimized....
            recyclerViewAdapter.calculateSpanSize();
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
