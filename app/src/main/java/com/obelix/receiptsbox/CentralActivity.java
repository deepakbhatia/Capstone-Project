package com.obelix.receiptsbox;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CentralActivity extends AppCompatActivity implements
        ReceiptItemFragment.OnListFragmentInteractionListener
         {


    private static final String AUTHENTICATED = "authenticated";
    private TextView mTextMessage;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    //Content Loading
    private static final int RECEIPT_LOADER = 0;

    public final static int ADD_RECEIPT_CODE = 1;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            fragmentManager = getSupportFragmentManager();
            ReceiptItemFragment receiptItemFragment;

            ReceiptItemFragment archivedReceiptItemFragment;
            switch (item.getItemId()) {


                case R.id.navigation_receipts:
                    //mTextMessage.setText(R.string.title_home);
                    receiptItemFragment = ReceiptItemFragment.newInstance(0,true);
                    fragmentManager.beginTransaction()

                            .replace(R.id.content, receiptItemFragment)
                            .commit();
                    break;
                case R.id.navigation_archived:
                   // mTextMessage.setText(R.string.title_dashboard);
                    archivedReceiptItemFragment = ReceiptItemFragment.newInstance(0,false);
                    fragmentManager.beginTransaction()

                            .replace(R.id.content, archivedReceiptItemFragment)
                            .commit();

                    break;
                case R.id.navigation_reports:
                    //mTextMessage.setText(R.string.title_notifications);
                    receiptItemFragment = ReceiptItemFragment.newInstance(0,false);

                    Intent intent = new Intent(appCompatActivity,PhoneAuthActivity.class);

                    appCompatActivity.startActivity(intent);

                    break;


            }


            return true;
        }

    };
    private CentralActivity appCompatActivity;
    private String receipt_id = "receipt_id";
    private String MyPREFERENCES = "receipt_pref";

    @BindView(R.id.add_receipt)
    public FloatingActionButton add_receipt_fab;

    @BindView(R.id.navigation)
    public BottomNavigationView navigation;
             private String TAG = "CentralActivity";

             @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_central);

        ButterKnife.bind(this);

        appCompatActivity = this;

        if(!readAuthenticated()){
            Toast.makeText(this, getResources().getString(R.string.unauth_storage_message),Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, getResources().getString(R.string.auth_storage_message),Toast.LENGTH_LONG).show();

        }

        //mTextMessage = (TextView) findViewById(R.id.message);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
             @Override
             protected void onResume() {
                 super.onResume();

                 SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

                 Log.d(TAG,sp.getString("currency","$"));
                 navigation.setSelectedItemId(R.id.navigation_receipts);

             }

             @Override
    protected void onStart() {
        super.onStart();


    }

    private boolean readAuthenticated(){
        SharedPreferences sp = getSharedPreferences(AUTHENTICATED,MODE_PRIVATE);

        Constants.authenticated = sp.getBoolean(AUTHENTICATED,false);

        Log.d(TAG,""+Constants.authenticated);
        return Constants.authenticated;
    }
             @Override
             public boolean onCreateOptionsMenu(Menu menu) {
                 // Inflate the menu; this adds items to the action bar if it is present.
                 getMenuInflater().inflate(R.menu.menu_main, menu);
                 return true;
             }

             @Override
             public boolean onOptionsItemSelected(MenuItem item) {
                 // Handle action bar item clicks here. The action bar will
                 // automatically handle clicks on the Home/Up button, so long
                 // as you specify a parent activity in AndroidManifest.xml.
                 int id = item.getItemId();

                 //noinspection SimplifiableIfStatement
                 if (id == R.id.action_settings) {
                     startActivity(new Intent(this, SettingsActivity.class));
                     return true;
                 }

                 return super.onOptionsItemSelected(item);
             }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case CentralActivity.ADD_RECEIPT_CODE:
            {

                if(resultCode == Activity.RESULT_OK){

                    EventBus.getDefault().post(new Receipt());
                }

            }
        }
    }
    @Override
    public void onListFragmentInteraction(Receipt item) {

    }

    @OnClick(R.id.add_receipt)
    public void createReceipt(){

        Intent receiptIntent = new Intent(this, AddReceipt.class);

        startActivityForResult(receiptIntent, ADD_RECEIPT_CODE);

    }

}
