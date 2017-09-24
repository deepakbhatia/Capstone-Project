package com.obelix.receiptsbox;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by obelix on 08/07/2017.
 */

public class ReceiptsAdapter extends CursorRecyclerViewAdapter<ReceiptsAdapter.ViewHolder> {


    private static final String NOT_EXISTS = "NOT_EXISTS";
    private ActionMode.Callback mChoiceMode;

    private Context mContext;

    private String TAG = "ReceiptsAdapter";
    private AppCompatActivity activity;

    public final static String FOOD_CATEGORY_LABEL = "Food";
    public final static String HEALTH_CATEGORY_LABEL = "Health";
    public final static String TRANSPORT_CATEGORY_LABEL = "Transport";
    public final static String ENTERTAINMENT_CATEGORY_LABEL = "Entertainment";
    public final static String OTHER_CATEGORY_LABEL = "Other Household";

    public ReceiptsAdapter(AppCompatActivity activity, Context context, Cursor c, int flags) {
        super(context, c);

        this.mContext = context;

        this.activity = activity;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

        ViewHolder holder = viewHolder;
        cursor.moveToPosition(cursor.getPosition());
        holder.setData(cursor);


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_receipt_item, parent, false);

        ViewHolder holder = new ViewHolder(view);

        view.setTag(holder);

        return holder;
    }

    private Object mActionMode;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_receipt_actions, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {


            switch (item.getItemId()) {
                case R.id.action_archive:
                    //shareCurrentItem();


                    EventBus.getDefault().post(new ReceiptActions(true, false, Constants.selectedItems));

                    mode.finish(); // Action picked, so close the CAB

                    Toast.makeText(mContext, mContext.getResources().getString(R.string.reload_message), Toast.LENGTH_LONG).show();

                    return true;
                case R.id.action_delete:

                    EventBus.getDefault().post(new ReceiptActions(false, true, Constants.selectedItems));

                    //shareCurrentItem();
                    mode.finish(); // Action picked, so close the CAB

                    Toast.makeText(mContext, mContext.getResources().getString(R.string.reload_message), Toast.LENGTH_LONG).show();

                    return true;
                default:
                    return false;
            }

        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;

            for (int i = 0; i < Constants.selectedItemViewsList.size(); i++) {
                View v = Constants.selectedItemViewsList.get(i);

                v.setSelected(false);

                ContentValues receiptVals = (ContentValues) v.getTag();

                ReceiptDetailActivity.setColor(mContext, v, receiptVals.getAsString(ReceiptItemContract.ReceiptItems.COL_type));

            }

            Constants.selectedItemViews.clear();

            Constants.selectedItems.clear();

            Constants.selectedItemViewsList.clear();

        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        //public final View mView;

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

        public Receipt mItem;

        public ViewHolder(View view) {
            super(view);


            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            view.setLongClickable(true);

        }

        public void setData(Cursor cursor) {


            ContentValues receiptValue = new ContentValues();

            receiptValue.put(ReceiptItemContract.ReceiptItems.COL_ID, cursor.getLong(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_ID)));

            receiptValue.put(ReceiptItemContract.ReceiptItems.COL_amount, cursor.getDouble(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_amount)));
            receiptValue.put(ReceiptItemContract.ReceiptItems.COL_archived, cursor.getInt(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_archived)));
            receiptValue.put(ReceiptItemContract.ReceiptItems.COL_card_payment, cursor.getInt(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_card_payment)));
            receiptValue.put(ReceiptItemContract.ReceiptItems.COL_date, cursor.getLong(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_date)));
            receiptValue.put(ReceiptItemContract.ReceiptItems.COL_deleted, cursor.getInt(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_deleted)));
            receiptValue.put(ReceiptItemContract.ReceiptItems.COL_place, cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_place)));
            receiptValue.put(ReceiptItemContract.ReceiptItems.COL_title, cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_title)));
            receiptValue.put(ReceiptItemContract.ReceiptItems.COL_type, cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_type)));
            receiptValue.put(ReceiptItemContract.ReceiptItems.COL_cloud_id, cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_cloud_id)));

            mReceiptCard.setTag(receiptValue);
            String tag = cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_type));
            mReceiptTagView.setText(tag);

            ReceiptDetailActivity.setColor(mContext, mReceiptCard, tag);
            mReceiptDateView.setText(getDate(cursor.getLong(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_date))));

            mReceiptDateView.setTag(cursor.getLong(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_date)));
            mReceiptPlaceView.setText(cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_place)));

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);

            mReceiptPriceView.setText(sp.getString(mContext.getString(R.string.currency_key), mContext.getString(R.string.default_currency)).concat(" ") + String.valueOf(cursor.getDouble(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_amount))));

            mReceiptTitleView.setText(cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_title)));

        }

        @OnClick({R.id.tag_receipt, R.id.date_receipt})
        public void searchByClicked(View v) {
            Toast.makeText(mContext, ((TextView) v).getText().toString(), Toast.LENGTH_LONG).show();

            if (v.getId() == R.id.date_receipt) {
                EventBus.getDefault().post(new SortBy(String.valueOf(v.getTag()), "date"));
            } else if (v.getId() == R.id.tag_receipt) {
                EventBus.getDefault().post(new SortBy(((TextView) v).getText().toString(), "type"));

            }
        }

        @Override
        public String toString() {
            return null;
        }

        @Override
        public void onClick(View view) {


            ContentValues receiptVals = (ContentValues) view.getTag();

            if (Constants.selectedItems.size() != 0 && !Constants.selectedItems.containsKey(receiptVals.getAsLong(ReceiptItemContract.ReceiptItems.COL_ID))) {

                Constants.selectedItems.put(receiptVals.getAsLong(ReceiptItemContract.ReceiptItems.COL_ID), receiptVals);
                Constants.selectedItemViews.put(view.getId(), view);

                Constants.selectedItemViewsList.add(view);
                view.setBackgroundColor(Color.DKGRAY);

            } else if (Constants.selectedItems.containsKey(receiptVals.getAsLong(ReceiptItemContract.ReceiptItems.COL_ID))) {

                Constants.selectedItemViews.remove(view.getId());

                view.setSelected(false);
                ReceiptDetailActivity.setColor(mContext, view, receiptVals.getAsString(ReceiptItemContract.ReceiptItems.COL_type));

            }

        }

        @Override
        public boolean onLongClick(View view) {

            //activity.startSupportActionMode(mChoiceMode);
            //multiSelector.setSelected(this, true);

            if (mActionMode != null) {
                return false;
            }
            mActionMode = activity.startSupportActionMode(mActionModeCallback);


            ContentValues receiptVals = (ContentValues) view.getTag();

            Constants.selectedItems.put(receiptVals.getAsLong(ReceiptItemContract.ReceiptItems.COL_ID), receiptVals);
            Constants.selectedItemViews.put(view.getId(), view);

            Constants.selectedItemViewsList.add(view);

            view.setBackgroundColor(Color.DKGRAY);


            // Start the CAB using the ActionMode.Callback defined above
            view.setSelected(true);


            return true;
        }
    }


    public static String getDate(long timeStamp) {

        try {
            DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
}
