package e.a.exlorista_customer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class chooseAddress extends AppCompatActivity {


    Bundle extras;
    deliveryType.DELIVERYTYPE deliveryType;

    Button addNewAddrB,proceedToPayB;
    LinearLayout addrListLL;

    Context mContext;

    ArrayList<String[]> savedAddresses;
    RadioButton[] addressRadioButton;
    int selectedAddressIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_address);

        mContext=this;

        extras=getIntent().getExtras();
        if(extras!=null){
            deliveryType=(deliveryType.DELIVERYTYPE)extras.getSerializable(auxiliary.DELIVERYTYPE);
        }

        addNewAddrB=findViewById(R.id.addNewAddrB);
        addrListLL=findViewById(R.id.addrListLL);
        proceedToPayB=findViewById(R.id.proceedToPayB);

        populateAddressList();
        for(int i=0;i<addressRadioButton.length;i++){
            final int i1=i;
            addressRadioButton[i1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedAddressIndex=i1;
                    addressRadioButton[i1].setChecked(true);
                    for(int j=0;j<addressRadioButton.length;j++){
                        if(j!=i1){
                            addressRadioButton[j].setChecked(false);
                        }
                    }
                }
            });
        }

        proceedToPayB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceedToPayment();
            }
        });
    }

    private void populateAddressList(){
        savedAddresses=getSavedAddressesFromDb(auxiliary.SERVER_URL+"/addressBookManager.php",auxiliary.DUMMYVAL_CUSTID);
        addressRadioButton=new RadioButton[savedAddresses.size()];
        for(int i=0;i<savedAddresses.size();i++){

            LinearLayout addressSelectionLL, addressDetailLL;
            TextView addressTagTV, addressCompleteTV;
            String address;

            addressTagTV=new TextView(mContext);
            addressCompleteTV=new TextView(mContext);
            addressTagTV.setLayoutParams(
                    new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            addressCompleteTV.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );
            addressTagTV.setText(savedAddresses.get(i)[1]);
            address=savedAddresses.get(i)[2]+
                    ", "+savedAddresses.get(i)[3]+
                    ", "+savedAddresses.get(i)[4]+
                    ", "+savedAddresses.get(i)[5];
            addressCompleteTV.setText(address);

            addressDetailLL=new LinearLayout(mContext);
            addressDetailLL.setOrientation(LinearLayout.VERTICAL);
            addressDetailLL.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            0,LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );
            addressDetailLL.addView(addressTagTV);
            addressDetailLL.addView(addressCompleteTV);

            addressRadioButton[i]=new RadioButton(mContext);

            addressSelectionLL=new LinearLayout(mContext);
            addressSelectionLL.setOrientation(LinearLayout.HORIZONTAL);
            addressSelectionLL.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );
            addressSelectionLL.addView(addressDetailLL);
            addressSelectionLL.addView(addressRadioButton[i]);

            addrListLL.addView(addressSelectionLL);
        }
    }

    private void proceedToPayment(){
        Intent chooseAddressToProceedToPaymentIntent=new Intent(chooseAddress.this,payment.class);
        chooseAddressToProceedToPaymentIntent.putExtra(auxiliary.DELIVERYTYPE,deliveryType);
        switch (deliveryType){
            case SLOTTED:
                String deliverySlotId=extras.getString(auxiliary.DELIVERYSLOTID);
                chooseAddressToProceedToPaymentIntent.putExtra(auxiliary.DELIVERYSLOTID,deliverySlotId);
                break;
            case SCHEDULED:
                String deliveryDateTime=extras.getString(auxiliary.DELIVERYDATETIME);
                chooseAddressToProceedToPaymentIntent.putExtra(auxiliary.DELIVERYDATETIME,deliveryDateTime);
                break;
        }
        chooseAddressToProceedToPaymentIntent.putExtra(auxiliary.DUMMYKEY_CUSTID,auxiliary.DUMMYVAL_CUSTID);
        startActivity(chooseAddressToProceedToPaymentIntent);
    }

    private ArrayList<String[]> getSavedAddressesFromDb(final String urlWebService, final String cust_id){
        class GetSavedAddressesFromDb extends AsyncTask<Void,Void,Void>{

            private ArrayList<String[]> saved_addresses=new ArrayList<String[]>();

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
                            put(auxiliary.PPK_CUSTID,cust_id);
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
                        JSONArray addressJsonArray=jsonArray.getJSONArray(i);
                        saved_addresses.add(new String[]{addressJsonArray.getString(0)
                                ,addressJsonArray.getString(1)
                                ,addressJsonArray.getString(2)
                                ,addressJsonArray.getString(3)
                                ,addressJsonArray.getString(4)
                                ,addressJsonArray.getString(5)});
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            private ArrayList<String[]> getSavedAddresses(){
                return saved_addresses;
            }
        }
        GetSavedAddressesFromDb getSavedAddressesFromDb=new GetSavedAddressesFromDb();
        try {
            getSavedAddressesFromDb.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return getSavedAddressesFromDb.getSavedAddresses();
    }

}