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
    private ArrayList<String> prodIdsInCart;
    private ArrayList<String> prodCountsInCart;
    private ArrayList<String> prodMrpsInCart;
    private ArrayList<String> prodStorepricesInCart;

    private String cartTotal;
    private String cartDeliveryCharge;
    private String cartGrandTotal;

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
            prodIdsInCart=extras.getStringArrayList(auxiliary.PRODIDS_INCART);
            cartTotal=extras.getString(auxiliary.CART_TOTAL);
            cartDeliveryCharge=extras.getString(auxiliary.CART_DELIVERYCHARGE);
            cartGrandTotal=extras.getString(auxiliary.CART_GRANDTOTAL);
            prodCountsInCart=new ArrayList<String>();
            prodMrpsInCart=new ArrayList<String>();
            prodStorepricesInCart=new ArrayList<String>();
            for(String i : prodIdsInCart){
                prodCountsInCart.add(auxiliarycart.thisProdCountInCartOGC(mContext,i,storeId));
                prodMrpsInCart.add(auxiliarycart.getCartProdInternalHashMapValueOGC(mContext,i,auxiliarycart.IHMKEY_PRODMRP));
                prodStorepricesInCart.add(auxiliarycart.getCartProdInternalHashMapValueOGC(mContext,i,auxiliarycart.IHMKEY_PRODSTOREPRICE));
            }
        }
        try{
            String sql=auxiliaryordermanagement.placeOrder(
                    auxiliary.SERVER_URL+"/orderManagement.php"
                    ,custId
                    ,storeId
                    ,auxiliary.arrayListToStr(prodIdsInCart,"_")
                    ,auxiliary.arrayListToStr(prodCountsInCart,"_")
                    ,auxiliary.arrayListToStr(prodMrpsInCart,"_")
                    ,auxiliary.arrayListToStr(prodStorepricesInCart,"_")
                    ,cartTotal
                    ,cartDeliveryCharge
                    ,cartGrandTotal
                    ,CUSTADDR_ID
                    ,PAYM_ID);
            Log.i("LOS","sql -> "+sql);
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                //Log.i("FCM", "getInstanceId failed", task.getException());
                                return;
                            }
                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            Log.i("FCM","token -> "+token);
                            String sql=auxiliaryfcmmanager.sendTokenToServer(auxiliary.SERVER_URL+"/fcmTokenManagement.php"
                                    ,auxiliary.DUMMYVAL_CUSTID
                                    ,token);
                            Log.i("FCM","sql -> "+sql);
                        }
                    });
        } catch (NullPointerException npe){
            Log.i("LOS","npe occurred");
            npe.printStackTrace();
        } catch (Exception e){
            Log.i("LOS","exception occurred");
            e.printStackTrace();
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
