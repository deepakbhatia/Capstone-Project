package com.obelix.receiptsbox;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.provider.BaseColumns._ID;
import static com.obelix.receiptsbox.Constants.receiptsEndpoint;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ReceiptItemFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        QueryFirebaseTask.QueryCompleteInterface{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_NEW_RECEIPTS = "add_receipts";
    private static final String NOT_EXISTS = "";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    //Content Loading
    private static final int RECEIPT_LOADER = 0;
    private static final int SORT_DATE_RECEIPT_LOADER = 10;
    private static final int SORT_TYPE_RECEIPT_LOADER = 11;
    private static final int ARCHIVED_RECEIPT_LOADER = 12;

    public final String TAG = "ReceiptItemFragment";

    /**
     * A projection of all columns in the items table.
     */
    public static final String[] PROJECTION_ALL = {
            _ID,
            DbSchema.COL_title,
            DbSchema.COL_type,
            DbSchema.COL_date,
            DbSchema.COL_place,
            DbSchema.COL_amount,
            DbSchema.COL_card_payment,
            DbSchema.COL_archived,
            DbSchema.COL_deleted,
            DbSchema.COL_cloud_id};


    private ReceiptsAdapter mReceiptAdapter;
    private ContentResolver contentResolver;
    private DatabaseReference mDatabase;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReceiptItemFragment() {
    }



    @SuppressWarnings("unused")
    public static ReceiptItemFragment newInstance(int columnCount, boolean showNew) {
        ReceiptItemFragment fragment = new ReceiptItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putBoolean(ARG_NEW_RECEIPTS, showNew);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        getActivity().setTitle(R.string.app_name);
        //setRetainInstance(true);
    }

    @BindView(R.id.list)
    public RecyclerView recyclerView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {


        contentResolver = getActivity().getContentResolver();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        if(getArguments().getBoolean(ARG_NEW_RECEIPTS))
            getLoaderManager().initLoader(RECEIPT_LOADER, null, this);
        else
        {
            getLoaderManager().initLoader(ARCHIVED_RECEIPT_LOADER, null, this);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        super.onActivityCreated(savedInstanceState);
    }

    public static String RECEIPT_SORT_DATE = "date";

    public static String RECEIPT_SORT_TYPE = "type";

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SortBy sortBy){
//        Toast.makeText(getActivity(), sortBy.type+":"+sortBy.type, Toast.LENGTH_SHORT).show();

        if(sortBy.type.contains(RECEIPT_SORT_DATE))
        {
            if(Constants.authenticated){
                new QueryFirebaseTask(this).execute("priority",String.valueOf((Long.parseLong(sortBy.value))));
            }else{
                Bundle bundle = new Bundle();

                bundle.putLong(RECEIPT_SORT_DATE,Long.parseLong(sortBy.value));
                getLoaderManager().restartLoader(SORT_DATE_RECEIPT_LOADER, bundle, this);

            }


        }else  if(sortBy.type.contains(RECEIPT_SORT_TYPE))
        {
            if(Constants.authenticated){
                new QueryFirebaseTask(this).execute(RECEIPT_SORT_TYPE,sortBy.value);

            }else{

                Bundle bundle = new Bundle();

                bundle.putString(RECEIPT_SORT_TYPE, sortBy.value);
                getLoaderManager().restartLoader(SORT_TYPE_RECEIPT_LOADER, bundle, this);

            }

        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Receipt receipt){
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.receipt_created_message), Toast.LENGTH_SHORT).show();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipt_item_list, container, false);

        ButterKnife.bind(this, view);

        mReceiptAdapter = new ReceiptsAdapter((AppCompatActivity) getActivity(),getActivity(), null, 0);

        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mColumnCount));
        }

        recyclerView.setAdapter(mReceiptAdapter);
        return view;
    }


        @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String sortOrder = ReceiptItemContract.ReceiptItems.COL_date + " DESC";

        Uri receiptUri = ReceiptItemContract.ReceiptItems.buildReceiptWithDate(
                System.currentTimeMillis());

        if(i == ARCHIVED_RECEIPT_LOADER){

            receiptUri = ReceiptItemContract.ReceiptItems.buildArchivedReceiptWithDate(
                    System.currentTimeMillis());
        }
        else if(i == SORT_DATE_RECEIPT_LOADER){

            receiptUri = ReceiptItemContract.ReceiptItems.buildReceiptWithDate(
                    bundle.getLong(RECEIPT_SORT_DATE));
        }else  if(i == SORT_TYPE_RECEIPT_LOADER) {

            Toast.makeText(getActivity(),""+bundle.getString(RECEIPT_SORT_TYPE),Toast.LENGTH_LONG).show();

            receiptUri = ReceiptItemContract.ReceiptItems.buildReceiptWithTypeAndDate(
                    bundle.getString(RECEIPT_SORT_TYPE), System.currentTimeMillis());
        }

        return new CursorLoader(getActivity(),
                receiptUri,
                PROJECTION_ALL,
                null,
                null,
                sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mReceiptAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceiptActions receiptActions){

        mDatabase = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.baseUrl+receiptsEndpoint);

        HashMap<Long, ContentValues> receiptItems = receiptActions.selectedItems;

        Set<Long> receiptIds = receiptItems.keySet();

        Iterator receiptIdIt = receiptIds.iterator();

        while(receiptIdIt.hasNext()){
            Long receiptId = (Long)receiptIdIt.next();

            ContentValues receiptValues = receiptItems.get(receiptId);

            String cloud_id = receiptValues.getAsString(ReceiptItemContract.ReceiptItems.COL_cloud_id);

            //ContentResolver cr = getActivity().getContentResolver();
            if(receiptActions.archive && contentResolver!=null)
            {

                if(Constants.authenticated){


                    if(!cloud_id.equals(NOT_EXISTS)){
                        Map<String,Object> receiptMap = new HashMap<String,Object>();
                        receiptMap.put(ReceiptItemContract.ReceiptItems.COL_archived, 1);

                        mDatabase.child(cloud_id).updateChildren(receiptMap);
                    }

                }
               
                {

                    receiptValues.put(ReceiptItemContract.ReceiptItems.COL_archived,1);

                    contentResolver.update(ReceiptItemContract.ReceiptItems.buildReceipt(receiptId),receiptValues, ReceiptItemContract.ReceiptItems.TABLE_NAME+
                            "." + ReceiptItemContract.ReceiptItems.COL_ID + " = ?",new String[]{String.valueOf(receiptId)});

                }
            }
            else if(receiptActions.delete){

                if(Constants.authenticated){
                    mDatabase.child(cloud_id).removeValue();

                }else{
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.local_deletion_alert),Toast.LENGTH_LONG).show();

                }
                {
                    contentResolver.delete(ReceiptItemContract.ReceiptItems.buildReceipt(receiptId), ReceiptItemContract.ReceiptItems.TABLE_NAME +
                            "." + ReceiptItemContract.ReceiptItems.COL_ID + " = ?", new String[]{String.valueOf(receiptId)});
                }
            }
        }


        getLoaderManager().restartLoader(RECEIPT_LOADER, null, this);
    }

    @Override
    public void queryCompleted(MatrixCursor matrixCursor) {
        if(matrixCursor!=null)
        mReceiptAdapter.swapCursor(matrixCursor);
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Receipt item);
    }


}
