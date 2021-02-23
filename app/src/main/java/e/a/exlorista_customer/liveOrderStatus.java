package e.a.exlorista_customer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.ArrayList;

public class liveOrderStatus extends AppCompatActivity {

    private Bundle extras;
    private String storeId;
    private String custId;
    private String orderId;

    Context mContext;

    // Test values (Due to incomplete implementation)
    private final String CUSTADDR_ID="1";
    private final String PAYM_ID="1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_order_status);

        /*setTitle("Live order status");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        mContext=this;

        extras=getIntent().getExtras();
        if(extras!=null){
            storeId=extras.getString(auxiliary.STORE_ID);
            custId=extras.getString(auxiliary.DUMMYKEY_CUSTID);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
