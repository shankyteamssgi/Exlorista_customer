package e.a.exlorista_customer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e.a.exlorista_customer.NetworkManagers.VolleyNM;
import e.a.exlorista_customer.Singletons.AppSingleton;
import e.a.exlorista_customer.Singletons.VolleyRequestQueueSingleton;

public class liveOrderStatus extends AppCompatActivity {
    // Views
    // TextView (order id)
    TextView orderIdTV;

    // ImageView (check marks)
    ImageView orderPlaced_checkMarkIV;
    ImageView orderAccepted_checkMarkIV;
    ImageView orderAvailabilityCheck_checkMarkIV;
    ImageView orderPackaged_checkMarkIV;
    ImageView orderHandover_checkMarkIV;

    // ProgressBar
    ProgressBar orderPlacedPB;
    ProgressBar orderAcceptedPB;
    ProgressBar orderAvailabilityCheckPB;
    ProgressBar orderPackagedPB;
    ProgressBar orderHandoverPB;

    // TextView (order description)
    TextView orderPlacedDescTV;
    TextView orderAcceptedDescTV;
    TextView orderAvailabilityCheckDescTV;
    TextView orderPackagedDescTV;
    TextView orderHandoverDescTV;
    
    // Button (only in availability check)
    Button orderAvailabilityCheckProceedAnywaysB;
    Button orderAvailabilityCheckCancelOrderB;

    // Bundle (from intent)
    private Bundle extras;

    // Bundle fields
    private String orderId;

    // Fields
    String storeName;
    String storeFcmToken;
    String orderStatus;

    // Context
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_order_status);

        /*setTitle("Live order status");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        // set context
        mContext=this;

        // get extras from bundle and assign corresponding bundle fields
        extras=getIntent().getExtras();
        if(extras!=null){
            orderId=extras.getString(auxiliary.ORDER_ID);
        }

        // assign views
        orderIdTV=findViewById(R.id.orderIdTV);

        orderPlaced_checkMarkIV=findViewById(R.id.orderPlaced_checkMarkIV);
        orderAccepted_checkMarkIV=findViewById(R.id.orderAccepted_checkMarkIV);
        orderAvailabilityCheck_checkMarkIV=findViewById(R.id.orderAvailabilityCheck_checkMarkIV);
        orderPackaged_checkMarkIV=findViewById(R.id.orderPackaged_checkMarkIV);
        orderHandover_checkMarkIV=findViewById(R.id.orderHandover_checkMarkIV);

        orderPlacedPB=findViewById(R.id.orderPlacedPB);
        orderAcceptedPB=findViewById(R.id.orderAcceptedPB);
        orderAvailabilityCheckPB=findViewById(R.id.orderAvailabilityCheckPB);
        orderPackagedPB=findViewById(R.id.orderPackagedPB);
        orderHandoverPB=findViewById(R.id.orderHandoverPB);

        orderPlacedDescTV=findViewById(R.id.orderPlacedDescTV);
        orderAcceptedDescTV=findViewById(R.id.orderAcceptedDescTV);
        orderAvailabilityCheckDescTV=findViewById(R.id.orderAvailabilityCheckDescTV);
        orderPackagedDescTV=findViewById(R.id.orderPackagedDescTV);
        orderHandoverDescTV=findViewById(R.id.orderHandoverDescTV);

        // initially fetch fields through a network request
        VolleyNM.requestDynamic(VolleyNM.HttpMethod.POST
                , auxiliary.SERVER_URL + "/orderManagement.php"
                , new HashMap<String, String>() {
                    {
                        put(auxiliary.PPK_ORDERID,orderId);
                    }
                }
                , new VolleyNM.ServerCallback() {
                    @Override
                    public void onSuccess(String response) {
                        // handle json and assign fields
                        handleJson(response);
                    }

                    @Override
                    public void onEmptyResult() {

                    }

                    @Override
                    public void onFailure() {

                    }

                    @Override
                    public void onError(VolleyError ve) {

                    }
                });
        
        // set onClickListener on availability check buttons (proceed anyways & cancel order)
        orderAvailabilityCheckProceedAnywaysB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeOrderStatus(orderId,auxiliary.PPV_ORDERSTATUS_CONFIRMED);
                updateUi(orderStatus);
            }
        });
        orderAvailabilityCheckCancelOrderB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeOrderStatus(orderId,auxiliary.PPV_ORDERSTATUS_CANCELLED);
                updateUi(orderStatus);
            }
        });
    }


    private void changeOrderStatus(final String order_id, final String order_status){
        // change order status here
        orderStatus=order_status;

        // change order status in db
        VolleyNM.request(VolleyNM.HttpMethod.POST
                ,auxiliary.SERVER_URL+"/orderManagement.php"
                ,new HashMap<String, String>(){
                    {
                        put(auxiliary.PPK_ORDERID,order_id);
                        put(auxiliary.PPK_ORDERSTATUS,orderStatus);
                    }
                });
    }


    private void handleJson(String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            storeName=jsonObject.getString("store_name");
            storeFcmToken=jsonObject.getString("store_fcmToken");
            String order_status=jsonObject.getString("orderstat_name");
            if(!orderStatus.equals(order_status)){
                orderStatus=order_status;
                updateUi(orderStatus);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateUi(String order_status){

    }


    @Override
    public void onBackPressed() {
        auxiliarycart.clearCart(mContext);
        Intent liveOrderStatusToMainActivityIntent=new Intent(liveOrderStatus.this,MainActivity.class);
        liveOrderStatusToMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        liveOrderStatusToMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(liveOrderStatusToMainActivityIntent);
    }

}
