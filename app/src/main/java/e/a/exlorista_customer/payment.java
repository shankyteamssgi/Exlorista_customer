package e.a.exlorista_customer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class payment extends AppCompatActivity {

    RadioGroup choosePaymentMethodRG;
    TextView cartItemCount_paymentTV,cartGrandTotalAmount_paymentTV;
    Button cartProceed_paymentB;

    ArrayList<String[]> paymMetIdAndName;
    ArrayList<RadioButton> paymMethods;
    String selectedPaymMetId;
    boolean paymentIsSuccessful;
    String paymentId;

    Context mContext;

    Bundle extras;
    deliveryType.DELIVERYTYPE deliveryType;
    String deliveryTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mContext=this;

        extras=getIntent().getExtras();
        if(extras!=null){
            deliveryType=(deliveryType.DELIVERYTYPE) extras.getSerializable(auxiliary.DELIVERYTYPE);
            deliveryTypeId=extras.getString(auxiliary.DELIVERYTYPEID);
        }

        paymentIsSuccessful=false;

        choosePaymentMethodRG=findViewById(R.id.choosePaymentMethodRG);
        cartItemCount_paymentTV=findViewById(R.id.cartItemCount_paymentTV);
        cartGrandTotalAmount_paymentTV=findViewById(R.id.cartGrandTotalAmount_paymentTV);
        cartProceed_paymentB=findViewById(R.id.cartProceed_paymentB);

        assignCartValuesInBottomSheet();

        populateEligiblePaymentMethods();

        cartProceed_paymentB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(paymentIsSuccessful && validateInput()){
                    auxiliaryordermanagement.placeOrderWrapper(mContext,paymentId);
                    updateFcmToken();
                    auxiliarycart.clearCart(mContext);
                    proceedToLiveOrderStatus();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // IMPLEMENTATION INCOMPLETE
        // Implementation will depend on the flow of this activity
        if(!paymentIsSuccessful){
            auxiliarycart.clearCart(mContext);
        }
    }

    private void assignCartValuesInBottomSheet(){
        ArrayList<String> prodIdsInCart=auxiliarycart.prodIdsInCartOGC(mContext);
        int cartTotalAmount=auxiliarycart.getCartTotalAmount(mContext,prodIdsInCart);
        int cartDeliveryCharges=auxiliarycart.getCartDeliveryCharges(cartTotalAmount);
        int cartGrandTotalAmount=cartTotalAmount+cartDeliveryCharges;
        cartItemCount_paymentTV.setText(Integer.toString(prodIdsInCart.size()));
        cartGrandTotalAmount_paymentTV.setText(Integer.toString(cartGrandTotalAmount));
    }

    private void populateEligiblePaymentMethods(){
        paymMetIdAndName=getPaymMetIdAndNameFromDb(auxiliary.SERVER_URL+"/fetchPaymentData.php",deliveryTypeId);
        for(int i=0;i<paymMetIdAndName.size();i++){
            RadioButton paymMetRB=new RadioButton(mContext);
            paymMetRB.setText(paymMetIdAndName.get(i)[1]);
            paymMetRB.setId(ViewCompat.generateViewId());
            choosePaymentMethodRG.addView(paymMetRB);
        }
    }

    private void assignPaymentResults(){
        // INCOMPLETE
        // Implementation of this method is dependent on implementation of payment gateway.
        // Here, assign relevant values to paymentIsSuccessful and paymentId
    }
    
    private void updateFcmToken(){
        try{
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
            npe.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean validateInput(){
        boolean input_validation=true;
        if(choosePaymentMethodRG.getCheckedRadioButtonId()==-1){
            input_validation=false;
            Toast.makeText(mContext,"Payment method not selected",Toast.LENGTH_LONG).show();
        } else{
            for(int i=0;i<paymMethods.size();i++){
                if(choosePaymentMethodRG.getCheckedRadioButtonId()==paymMethods.get(i).getId()){
                    selectedPaymMetId=paymMetIdAndName.get(i)[0];
                    break;
                }
            }
        }
        return input_validation;
    }

    private void proceedToLiveOrderStatus(){
        Intent paymentToLiveOrderStatusIntent=new Intent(payment.this,liveOrderStatus.class);
        startActivity(paymentToLiveOrderStatusIntent);
    }

    private ArrayList<String[]> getPaymMetIdAndNameFromDb(final String urlWebService,final String deliverytype_id){
        class GetPaymMetIdAndNameFromDb extends AsyncTask<Void,Void,Void>{

            private ArrayList<String[]> paym_id_and_name=new ArrayList<String[]>();

            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>() {
                        {
                            put(auxiliary.PPK_INITIAL_CHECK, auxiliary.PPV_INITIAL_CHECK);
                            put(auxiliary.PPK_DELIVERYTYPEID,deliverytype_id);
                        }
                    }));
                    dos.flush();
                    dos.close();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json);
                    }
                    JSONArray jsonArray=new JSONArray(sb.toString().trim());
                    for(int i=0;i<jsonArray.length();i++){
                        JSONArray id_and_name=jsonArray.getJSONArray(i);
                        paym_id_and_name.add(new String[]{id_and_name.getString(0),id_and_name.getString(1)});
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
            private ArrayList<String[]> getPaymMetIdAndName(){
                return paym_id_and_name;
            }
        }
        GetPaymMetIdAndNameFromDb getPaymMetIdAndNameFromDb=new GetPaymMetIdAndNameFromDb();
        try {
            getPaymMetIdAndNameFromDb.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return getPaymMetIdAndNameFromDb.getPaymMetIdAndName();
    }

}
