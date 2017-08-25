package com.obelix.receiptsbox;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddReceipt extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private String receipt_id = "receipt_id";
    private String MyPREFERENCES = "receipt_pref";

    private static final String firebase_receipts_root = "receipts";

    private static final String TAG = "AddReceipt";
    @BindView(R.id.add_datepicker_date)
    public TextView receiptDate;
    @BindView(R.id.add_edittext_title)
    public EditText edittextTitle;
    @BindView(R.id.add_edittext_place)
    public EditText edittextPlace;
    @BindView(R.id.add_edittext_amount)
    public EditText edittextAmount;
    @BindView(R.id.add_card_payment)
    public CheckBox card_payment;
    @BindView(R.id.receipt_category)
    public Spinner receipt_category;
    @BindView(R.id.save_receipt)
    public Button save_receipt;
    DatePickerDialog datePickerDialog;
    private DatabaseReference mDatabase;
    private String receipt_address;
    private AddReceipt currentActivity;

    public static String FIREBASE_DATE_SORT_KEY = "priority";

    @OnClick(R.id.add_datepicker_date)
    public void showDateDialog(){
            datePickerDialog.show();
    }
    @OnClick(R.id.save_receipt)
    public void saveReceipt(){
        long date = Long.parseLong(String.valueOf(receiptDate.getTag()));
        String amount_string = edittextAmount.getText().toString();

        String title = edittextTitle.getText().toString();

        card_payment.isChecked();

        String type = String.valueOf(receipt_category.getSelectedItem());

        int receipt_id = getLocalReceiptId();
        if(type!=null && String.valueOf(date)!=null && (!title.equals(""))  && receipt_address!=null && ( !amount_string.equals(""))){
            double amount = Double.parseDouble(amount_string);

            Receipt receipt = new Receipt(String.valueOf(receipt_id),type,title,date,receipt_address,amount,card_payment.isChecked()?1:0);

            storeContentValues(receipt);

            storeLocalReceiptId(receipt_id++);
        }else{
            Toast.makeText(this, R.string.error_form_filled_message,Toast.LENGTH_LONG).show();
        }

    }

    private int getLocalReceiptId(){
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        return sharedpreferences.getInt(receipt_id,0);
    }
    private void storeLocalReceiptId(int count){

        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putInt(receipt_id, count);

        editor.apply();
    }
    private void storeContentValues(Receipt receipt){


        ContentValues receiptValue = new ContentValues();


        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_cloud_id, receipt.cloud_id);

        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_amount, receipt.amount);



        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_archived, receipt.archived);


        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_card_payment, receipt.card_payment);


        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_date, Long.valueOf(receipt.date));



        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_deleted, receipt.deleted);


        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_place, receipt.place);


        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_title, receipt.title);


        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_type, receipt.type);


        if(Constants.authenticated){

            Map<String, Object> receiptMap = new HashMap<>();

            receiptMap.put(ReceiptItemContract.ReceiptItems.COL_cloud_id, receipt.cloud_id);
            receiptMap.put(ReceiptItemContract.ReceiptItems.COL_amount, receipt.amount);
            receiptMap.put(ReceiptItemContract.ReceiptItems.COL_archived, receipt.archived);
            receiptMap.put(ReceiptItemContract.ReceiptItems.COL_card_payment, receipt.card_payment);
            receiptMap.put(ReceiptItemContract.ReceiptItems.COL_date, Long.valueOf(receipt.date));
            receiptMap.put(ReceiptItemContract.ReceiptItems.COL_deleted, receipt.deleted);
            receiptMap.put(ReceiptItemContract.ReceiptItems.COL_place, receipt.place);
            receiptMap.put(ReceiptItemContract.ReceiptItems.COL_title, receipt.title);
            receiptMap.put(ReceiptItemContract.ReceiptItems.COL_type, receipt.type);
            receiptMap.put(FIREBASE_DATE_SORT_KEY,Long.valueOf(receipt.date));

            String key = mDatabase.push().getKey();

            receiptMap.put(ReceiptItemContract.ReceiptItems.COL_cloud_id, key);

            receiptValue.put(ReceiptItemContract.ReceiptItems.COL_cloud_id, key);

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put( key, receiptMap);
            //childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
            //mDatabase.setValue(receiptValue);

            mDatabase.updateChildren(childUpdates);
        }

        getContentResolver().insert(ReceiptItemContract.CONTENT_URI, receiptValue);




        this.setResult(Activity.RESULT_OK);

        finish();


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_receipt);

        ButterKnife.bind(this);


        currentActivity = this;
        mDatabase = FirebaseDatabase.getInstance().getReference(firebase_receipts_root);

        String locale = getResources().getConfiguration().locale.getCountry();
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)

                .setCountry(locale)
                .build();
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setHint(getResources().getString(R.string.place_autocomplete_search_hint));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                receipt_address  = place.getAddress().toString();
            }

            @Override
            public void onError(Status status) {

                Toast.makeText(currentActivity,status.toString(),Toast.LENGTH_LONG).show();
                //Log.i(TAG, "An error occurred: " + status);
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog  = new DatePickerDialog(
                this, this,newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        String[] myResArray = getResources().getStringArray(R.array.receipt_types);
        List<String> myResArrayList = Arrays.asList(myResArray);
        ArrayAdapter receipt_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,myResArrayList);

        receipt_category.setAdapter(receipt_adapter);


    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        String date_val = day+"/"+(month+1)+"/"+year;
        receiptDate.setText(date_val);


        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(date_val);

            long startDate = date.getTime();
            receiptDate.setTag(startDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
