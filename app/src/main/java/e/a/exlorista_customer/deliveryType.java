package e.a.exlorista_customer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.JsonArray;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class deliveryType extends AppCompatActivity {

    RadioGroup deliveryTypesRG,deliverySlotsRG;
    RadioButton deliveryInstantRB, deliverySlottedRB, deliveryScheduledRB, deliveryTakeawayRB;
    RadioButton selectedDeliveryTypeRB;
    LinearLayout chooseSlotOrDateTimeLL;
    LinearLayout chooseSlotLL, chooseDateTimeLL;
    TextView selectDateTV, selectTimeTV;
    TextView cartItemCount_deliveryTypeTV,cartGrandTotalAmount_deliveryTypeTV;
    Button cartProceed_deliveryTypeB;

    enum DELIVERYTYPE{INSTANT,SLOTTED,SCHEDULED,TAKEAWAY};
    ArrayList<String[]> deliveryTypeIdAndVisibilities;
    String selectedDeliveryTypeId;
    deliveryType.DELIVERYTYPE selectedDeliveryType;
    ArrayList<String[]> deliverySlots;
    int selectedDeliverySlotIndex;
    String date;
    int hour,minute;


    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_type);

        deliveryTypesRG=findViewById(R.id.deliveryTypesRG);
        deliverySlotsRG=findViewById(R.id.deliverySlotsRG);
        deliveryInstantRB=findViewById(R.id.deliveryInstantRB);
        deliverySlottedRB=findViewById(R.id.deliverySlottedRB);
        deliveryScheduledRB=findViewById(R.id.deliveryScheduledRB);
        deliveryTakeawayRB=findViewById(R.id.deliveryTakeawayRB);
        chooseSlotOrDateTimeLL=findViewById(R.id.chooseSlotOrDateTimeLL);
        chooseSlotLL=findViewById(R.id.chooseSlotLL);
        chooseDateTimeLL=findViewById(R.id.chooseDateTimeLL);
        selectDateTV=findViewById(R.id.selectDateTV);
        selectTimeTV=findViewById(R.id.selectTimeTV);
        cartItemCount_deliveryTypeTV=findViewById(R.id.cartItemCount_deliveryTypeTV);
        cartGrandTotalAmount_deliveryTypeTV=findViewById(R.id.cartGrandTotalAmount_deliveryTypeTV);
        cartProceed_deliveryTypeB=findViewById(R.id.cartProceed_deliveryTypeB);

        assignCartValuesInBottomSheet();

        populateVisibleDeliveryTypes();

        deliveryTypesRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedButtonId) {
                displayMoreDetails(checkedButtonId);
            }
        });

        deliverySlotsRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedButtonId) {
                selectedDeliverySlotIndex=checkedButtonId;
            }
        });

        selectDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar=Calendar.getInstance();
                DatePickerDialog datePickerDialog=new DatePickerDialog(deliveryType.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,day);
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM/dd/yy", Locale.US);
                        date=simpleDateFormat.format(calendar.getTime());
                        selectDateTV.setText(date);
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setTitle("Select date");
                datePickerDialog.show();
            }
        });

        selectTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar=Calendar.getInstance();
                TimePickerDialog timePickerDialog=new TimePickerDialog(deliveryType.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int _hour, int _minute) {
                        hour=_hour;
                        minute=_minute;
                        selectTimeTV.setText(hour + ":" + minute);
                    }
                },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
                timePickerDialog.setTitle("Select time");
                timePickerDialog.show();
            }
        });

        cartProceed_deliveryTypeB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(validateInput()){
                    proceedToChooseAddress();
                }
            }
        });
    }

    private void assignCartValuesInBottomSheet(){
        ArrayList<String> prodIdsInCart=auxiliarycart.prodIdsInCartOGC(mContext);
        int cartTotalAmount=auxiliarycart.getCartTotalAmount(mContext,prodIdsInCart);
        int cartDeliveryCharges=auxiliarycart.getCartDeliveryCharges(cartTotalAmount);
        int cartGrandTotalAmount=cartTotalAmount+cartDeliveryCharges;
        cartItemCount_deliveryTypeTV.setText(Integer.toString(prodIdsInCart.size()));
        cartGrandTotalAmount_deliveryTypeTV.setText(Integer.toString(cartGrandTotalAmount));
    }

    private void populateVisibleDeliveryTypes(){
        deliveryTypeIdAndVisibilities=getDeliveryTypeIdAndVisibilitiesFromDb(
                auxiliary.SERVER_URL+"/fetchDeliveryTypeData.php");
        deliveryInstantRB.setVisibility(deliveryTypeIdAndVisibilities.get(0)[1].equals("1") ? View.VISIBLE:View.GONE);
        deliverySlottedRB.setVisibility(deliveryTypeIdAndVisibilities.get(1)[1].equals("1")? View.VISIBLE:View.GONE);
        deliveryScheduledRB.setVisibility(deliveryTypeIdAndVisibilities.get(2)[1].equals("1")? View.VISIBLE:View.GONE);
        deliveryTakeawayRB.setVisibility(deliveryTypeIdAndVisibilities.get(3)[1].equals("1")? View.VISIBLE:View.GONE);
    }

    private void populateDeliverySlots(){
        deliverySlots=getDeliverySlotsFromDb(auxiliary.SERVER_URL+"/fetchDeliveryTypeData.php");
        for(int i=0;i<deliverySlots.size();i++){
            RadioButton slotRB=new RadioButton(mContext);
            slotRB.setText(deliverySlots.get(i)[0]);
            deliverySlotsRG.addView(slotRB);
        }
    }

    private void displayMoreDetails(int checked_button_id){
        switch (checked_button_id) {
            case R.id.deliveryInstantRB:
                // instant (reset / do nothing)
                selectedDeliveryType=DELIVERYTYPE.INSTANT;
                selectedDeliveryTypeId=deliveryTypeIdAndVisibilities.get(0)[0];
                chooseSlotOrDateTimeLL.setVisibility(View.GONE);
                break;
            case R.id.deliverySlottedRB:
                selectedDeliveryType=DELIVERYTYPE.SLOTTED;
                selectedDeliveryTypeId=deliveryTypeIdAndVisibilities.get(1)[0];
                chooseSlotOrDateTimeLL.setVisibility(View.VISIBLE);
                chooseSlotLL.setVisibility(View.VISIBLE);
                chooseDateTimeLL.setVisibility(View.GONE);
                populateDeliverySlots();
                break;
            case R.id.deliveryScheduledRB:
                selectedDeliveryType=DELIVERYTYPE.SCHEDULED;
                selectedDeliveryTypeId=deliveryTypeIdAndVisibilities.get(2)[0];
                chooseSlotOrDateTimeLL.setVisibility(View.VISIBLE);
                chooseSlotLL.setVisibility(View.GONE);
                chooseDateTimeLL.setVisibility(View.VISIBLE);
                break;
            case R.id.deliveryTakeawayRB:
                // takeaway (reset / do nothing)
                selectedDeliveryType=DELIVERYTYPE.TAKEAWAY;
                selectedDeliveryTypeId=deliveryTypeIdAndVisibilities.get(3)[0];
                chooseSlotOrDateTimeLL.setVisibility(View.GONE);
                break;
        }
    }

    private boolean validateInput(){
        // INCOMPLETE
        // If deliveryType is SCHEDULED validate for date and time details
        boolean input_validation=true;
        if(selectedDeliveryType==null){
            input_validation=false;
            Toast.makeText(mContext,"Delivery type not selected",Toast.LENGTH_LONG).show();
        } else{
            switch (selectedDeliveryType){
                case SLOTTED:
                    if(deliverySlotsRG.getCheckedRadioButtonId()==-1){
                        input_validation=false;
                        Toast.makeText(mContext,"Delivery slot not selected",Toast.LENGTH_LONG).show();
                    }
                    break;
                case SCHEDULED:
                    break;
            }
        }
        return input_validation;
    }

    private void proceedToChooseAddress(){
        if(selectedDeliveryType==DELIVERYTYPE.TAKEAWAY){
            Intent deliveryTypeToPaymentIntent=new Intent(deliveryType.this,payment.class);
            deliveryTypeToPaymentIntent.putExtra(auxiliary.DELIVERYTYPEID,selectedDeliveryTypeId);
            startActivity(deliveryTypeToPaymentIntent);
        } else{
            Intent deliveryTypeToChooseAddressIntent=new Intent(deliveryType.this,chooseAddress.class);
            deliveryTypeToChooseAddressIntent.putExtra(auxiliary.DELIVERYTYPE,selectedDeliveryType);
            switch (selectedDeliveryType){
                case INSTANT:
                    deliveryTypeToChooseAddressIntent.putExtra(auxiliary.DELIVERYTYPEID,selectedDeliveryTypeId);
                    break;
                case SLOTTED:
                    deliveryTypeToChooseAddressIntent.putExtra(auxiliary.DELIVERYTYPEID,selectedDeliveryTypeId);
                    deliveryTypeToChooseAddressIntent.putExtra(auxiliary.DELIVERYSLOTID,deliverySlots.get(selectedDeliverySlotIndex)[1]);
                    break;
                case SCHEDULED:
                    deliveryTypeToChooseAddressIntent.putExtra(auxiliary.DELIVERYTYPEID,selectedDeliveryTypeId);
                    deliveryTypeToChooseAddressIntent.putExtra(auxiliary.DELIVERYDATETIME
                            ,formatDateTime(date,Integer.toString(hour),Integer.toString(minute)));
                    break;
            }
            startActivity(deliveryTypeToChooseAddressIntent);
        }
    }

    private String formatDateTime(String date, String hour, String minute){
        return date+"_"+hour+"_"+minute;
    }

    private ArrayList<String[]> getDeliveryTypeIdAndVisibilitiesFromDb(final String urlWebService){
        class GetDeliveryTypeIdAndVisibilitiesFromDb extends AsyncTask<Void,Void,Void>{

            private ArrayList<String[]> delivery_type_id_and_visibilities=new ArrayList<String[]>();

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
                        JSONArray id_and_visibility=jsonArray.getJSONArray(i);
                        delivery_type_id_and_visibilities.add(new String[]
                                {id_and_visibility.getString(0)
                                        ,id_and_visibility.getString(1)});
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            private ArrayList<String[]> getDeliveryTypeIdAndVisibilities(){
                return delivery_type_id_and_visibilities;
            }
        }
        GetDeliveryTypeIdAndVisibilitiesFromDb getDeliveryTypeIdAndVisibilitiesFromDb=new GetDeliveryTypeIdAndVisibilitiesFromDb();
        try {
            getDeliveryTypeIdAndVisibilitiesFromDb.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return getDeliveryTypeIdAndVisibilitiesFromDb.getDeliveryTypeIdAndVisibilities();
    }

    private ArrayList<String[]> getDeliverySlotsFromDb(final String urlWebService){
        class GetDeliverySlotsFromDb extends AsyncTask<Void,Void,Void>{

            private ArrayList<String[]> delivery_slots=new ArrayList<String[]>();

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
                        JSONArray slot=jsonArray.getJSONArray(i);
                        delivery_slots.add(new String[]{slot.getString(0),slot.getString(1)});
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            private ArrayList<String[]> getDeliverySlots(){
                return delivery_slots;
            }
        }
        GetDeliverySlotsFromDb getDeliverySlotsFromDb=new GetDeliverySlotsFromDb();
        try {
            getDeliverySlotsFromDb.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return getDeliverySlotsFromDb.getDeliverySlots();
    }

}