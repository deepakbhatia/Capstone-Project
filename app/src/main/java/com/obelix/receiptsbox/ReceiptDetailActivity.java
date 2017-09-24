package com.obelix.receiptsbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.obelix.receiptsbox.ReceiptsAdapter.ENTERTAINMENT_CATEGORY_LABEL;
import static com.obelix.receiptsbox.ReceiptsAdapter.FOOD_CATEGORY_LABEL;
import static com.obelix.receiptsbox.ReceiptsAdapter.HEALTH_CATEGORY_LABEL;
import static com.obelix.receiptsbox.ReceiptsAdapter.OTHER_CATEGORY_LABEL;
import static com.obelix.receiptsbox.ReceiptsAdapter.TRANSPORT_CATEGORY_LABEL;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ReceiptDetailActivity extends AppCompatActivity {


    @BindView(R.id.receipt_detail_toolbar)
    public Toolbar mReceiptToolbar;
    @BindView(R.id.card_receipt)
    public CardView mReceiptCard;
    @BindView(R.id.receipt_item_title)
    public TextView mReceiptTitleView;
    @BindView(R.id.price_receipt)
    public TextView mReceiptPriceView;
    @BindView(R.id.location_receipt)
    public TextView mReceiptPlaceView;
    @BindView(R.id.date_receipt)
    public TextView mReceiptDateView;
    @BindView(R.id.tag_receipt)
    public TextView mReceiptTagView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_receipt_detail);

        ButterKnife.bind(this);
        Intent receiptIntent = getIntent();

        mReceiptToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_clear_black_24dp));

        setSupportActionBar(mReceiptToolbar);
        mReceiptTagView.setBackground(null);
        mReceiptDateView.setBackground(null);

        mReceiptTagView.setClickable(false);
        mReceiptDateView.setClickable(false);


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mReceiptPriceView.setText(sp.getString(this.getString(R.string.currency_key), this.getString(R.string.default_currency)).concat(" ") + receiptIntent.getStringExtra(DbSchema.COL_amount));
        mReceiptTitleView.setText(receiptIntent.getStringExtra(DbSchema.COL_title));

        mReceiptDateView.setText(ReceiptsAdapter.getDate(Long.parseLong(receiptIntent.getStringExtra(DbSchema.COL_date))));

        String tag = receiptIntent.getStringExtra(DbSchema.COL_type);
        mReceiptTagView.setText(tag);

        mReceiptPlaceView.setText(receiptIntent.getStringExtra(DbSchema.COL_place));


        setColor(this, mReceiptCard,tag);
    }

    public static void setColor(Context m, View v, String tag) {


        switch (tag) {
            case FOOD_CATEGORY_LABEL:
                v.setBackgroundColor(m.getResources().getColor(R.color.colorFood));
                break;
            case HEALTH_CATEGORY_LABEL:
                v.setBackgroundColor(m.getResources().getColor(R.color.colorHealth));

                break;
            case TRANSPORT_CATEGORY_LABEL:
                v.setBackgroundColor(m.getResources().getColor(R.color.colorTransport));

                break;
            case ENTERTAINMENT_CATEGORY_LABEL:
                v.setBackgroundColor(m.getResources().getColor(R.color.colorEntertainment));

                break;
            case OTHER_CATEGORY_LABEL:
                v.setBackgroundColor(m.getResources().getColor(R.color.colorOther));

                break;
        }

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }





}
