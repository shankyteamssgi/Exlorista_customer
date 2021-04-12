package e.a.exlorista_customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import e.a.exlorista_customer.NetworkManagers.VolleyNM;

public class trackOrder extends AppCompatActivity {
    // Views
    LinearLayout noOrderToTrackLL;
    ImageView noOrderToTrackIV;
    RecyclerView trackableOrdersRV;
    RecyclerView.LayoutManager trackableOrdersRVLayoutManager;
    RecyclerView.Adapter trackableOrdersRVAdapter;

    // Fields
    ArrayList<String> orderId;
    ArrayList<String> orderPlacedOn;
    ArrayList<String> orderGrandTotal;
    ArrayList<String> orderDiffProdCount;

    // Progress bar
    ProgressDialog progressDialog;

    // Context
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        // set context
        mContext=this;

        // assign views
        noOrderToTrackLL=findViewById(R.id.noOrderToTrackLL);
        noOrderToTrackIV=findViewById(R.id.noOrderToTrackIV);
        trackableOrdersRV=findViewById(R.id.trackableOrdersRV);

        // assign fields
        orderId=new ArrayList<String>();
        orderPlacedOn=new ArrayList<String>();
        orderGrandTotal=new ArrayList<String>();
        orderDiffProdCount=new ArrayList<String>();

        // show progress bar
        progressDialog=new ProgressDialog(mContext);
        progressDialog.setMessage(auxiliary.PROGRESS_DIALOG_MESSAGE);
        progressDialog.show();

        // fetch server response and handle
        VolleyNM.requestDynamic(VolleyNM.HttpMethod.POST
                , auxiliary.SERVER_URL + "/orderManagement.php"
                , new HashMap<String, String>() {
                    {
                        put(auxiliary.PPK_REQUESTTYPE,auxiliary.PPV_REQUESTTYPE_FETCHTRACKABLEORDERS);
                        put(auxiliary.PPK_CUSTID,auxiliaryuseraccountmanager.getUserIdFromSP(mContext));
                    }
                }
                , new VolleyNM.ServerCallback() {
                    @Override
                    public void onSuccess(String response) {
                        // handle json and assign fields
                        handleJson(response);

                        // load recyclerview
                        trackableOrdersRVLayoutManager=new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);
                        trackableOrdersRVAdapter=new TrackableOrderAdapter(mContext,orderId,orderPlacedOn,orderGrandTotal,orderDiffProdCount);
                        trackableOrdersRV.setLayoutManager(trackableOrdersRVLayoutManager);
                        trackableOrdersRV.setAdapter(trackableOrdersRVAdapter);

                        // dismiss progress bar
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onEmptyResult() {
                        Log.i("TO","Empty result (Volley)");
                    }

                    @Override
                    public void onFailure() {
                        Log.i("TO","Failure (Volley)");
                    }

                    @Override
                    public void onError(VolleyError ve) {
                        Log.i("TO","VolleyError (Volley)");
                        ve.printStackTrace();
                    }
                });
    }

    private void handleJson(String response){
        try {
            JSONArray jsonArray=new JSONArray(response);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                orderId.add(jsonObject.getString("_order_id"));
                orderPlacedOn.add(customDateTimeFormat(jsonObject.getString("_order_date")
                        ,jsonObject.getString("_order_time")));
                orderGrandTotal.add(jsonObject.getString("_order_grandTotal"));
                orderDiffProdCount.add(jsonObject.getString("_order_diffProd"));
            }
        } catch (JSONException e) {
            Log.i("TO","server response -> "+response);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String customDateTimeFormat(String date,String time){
        return date+" at "+time;
    }
}