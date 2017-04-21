package com.udacity.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_STOCK_LOADER = 1;
    private static final String STOCK_SYMBOL = DetailActivity.class.getCanonicalName() + ".STOCK_SYMBOL";
    @BindView(R.id.chart)
    LineChart lineChart;
    private String symbol;

    public static Intent createCallingIntent(Context context, String stock) {
        Intent callingIntent = new Intent(context, DetailActivity.class);
        callingIntent.putExtra(STOCK_SYMBOL, stock);
        return callingIntent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        symbol = getIntent().getStringExtra(STOCK_SYMBOL);

        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        getSupportLoaderManager().initLoader(DETAIL_STOCK_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Contract.Quote.URI, new String[]{Contract.Quote.COLUMN_HISTORY}, Contract.Quote.COLUMN_SYMBOL + " like ?",
                                new String[]{symbol}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        String history = data.getString(0);
        String[] quotes = history.split("\n");
        if (quotes[0] != "") {
            List<Entry> entries = new ArrayList<>(quotes.length);
            for (int i = 0; i < quotes.length; i++) {
                String[] parts = quotes[i].split(",");
                entries.add(new Entry(i, Float.parseFloat(parts[1])));
            }
            LineDataSet dataSet = new LineDataSet(entries, symbol);
            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChart.getDescription()
                     .setText("");
            lineChart.setContentDescription(getString(R.string.content_description_stock_chart, symbol));
            lineChart.invalidate();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
