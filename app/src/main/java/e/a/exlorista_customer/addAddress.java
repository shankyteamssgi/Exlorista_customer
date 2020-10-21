package e.a.exlorista_customer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.location.LocationRequest;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class addAddress extends AppCompatActivity {

    enum userPermissionResponse{LOCATION_NOT_ENABLED,PERMISSION_DENIED,PERMISSION_DENIED_WITH_NEVERASKAGAIN};
    // Location
    private static final int REQUEST_CODE_LOCATION_PERMISSION=1;
    private static final int REQUEST_CHECK_SETTINGS=2;

    private EditText completeAddressET,addressLandmarkET,addOtherTagET;
    private Spinner stateSpinner,citySpinner,areaSpinner;
    private Button saveAddressB;
    private LinearLayout cityChoiceLL,stateChoiceLL,homeTagLL,officeTagLL;
    private ArrayList<HashMap<String,String>> stateData;
    private ArrayList<HashMap<String,String>> cityData;
    private ArrayList<HashMap<String,String>> areaData;
    LocationRequest locationRequest;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        setTitle("Add address");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext=this;

        completeAddressET=findViewById(R.id.completeAddressET);
        addressLandmarkET=findViewById(R.id.addressLandmarkET);
        addOtherTagET=findViewById(R.id.addOtherTagET);
        stateSpinner=findViewById(R.id.stateSpinner);
        citySpinner=findViewById(R.id.citySpinner);
        areaSpinner=findViewById(R.id.areaSpinner);
        homeTagLL=findViewById(R.id.homeTagLL);
        officeTagLL=findViewById(R.id.officeTagLL);
        saveAddressB=findViewById(R.id.saveAddressB);
        cityChoiceLL=findViewById(R.id.cityChoiceLL);
        stateChoiceLL=findViewById(R.id.stateChoiceLL);

        //homeTagLL.setBackground(ContextCompat.getDrawable(mContext,R.drawable.border_store_page));

        initializeGeographyData(true,true,true);

        if(auxiliary.gpsEnabled(mContext)){
            // GPS is enabled
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                // Permission granted
            } else{
                // Permission not granted
            }
        } else{
            // GPS is disabled
        }
        takeActionForLocationPermissionNotGranted();
    }

    private void takeActionForLocationPermissionGranted(){

        // Location permission granted. Take following actions:
        /*
        - Make dropdown for state and city gone. For both state and city, hide the complete LinearLayout encapsulating Spinner and TextView.
        - Fetch state, city from location.
        - Match name of city and state with name of city and state in database and fetch id of city and state.
          If match is unsuccessful, fetch states from database, make dropdown for state and city visible and display list of cities based on state chosen.
        - Retrieve list of areas from database based on the fetched/chosen state and city. Put those areas in the spinner for area.
        - Validate inputs.
        - If input validation is successful, add address to database.
         */
    }

    private void takeActionForLocationPermissionNotGranted(){

        // Location permission not granted. Take following actions:
        /*
        - Make dropdown for state and city visible.
        - Retrieve list of areas from database based on chosen state and city. Put those areas in the spinner for area.
        - Validate inputs.
        - If input validation is successful, add address to database.
         */

        stateChoiceLL.setVisibility(View.VISIBLE);
        cityChoiceLL.setVisibility(View.VISIBLE);
        fetchStateData(auxiliary.SERVER_URL+"/addressBookManager.php");
        stateSpinner.setAdapter(new ArrayAdapter<String>(mContext
                ,android.R.layout.simple_spinner_dropdown_item
                ,auxiliary.arrayListOfHMtoArrayListOfHMVals(stateData)));
        citySpinner.setAdapter(new ArrayAdapter<String>(mContext
                ,android.R.layout.simple_spinner_dropdown_item
                ,new String[]{auxiliary.SPINNER_UNSELECTED_CITY}));
        areaSpinner.setAdapter(new ArrayAdapter<String>(mContext
                ,android.R.layout.simple_spinner_dropdown_item
                ,new String[]{auxiliary.SPINNER_UNSELECTED_AREA}));
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position!=0){
                    fetchCityData(auxiliary.SERVER_URL+"/addressBookManager.php"
                            ,stateData.get(position).keySet().iterator().next());
                    citySpinner.setAdapter(new ArrayAdapter<String>(mContext
                            ,android.R.layout.simple_spinner_dropdown_item
                            ,auxiliary.arrayListOfHMtoArrayListOfHMVals(cityData)));
                } else{
                    initializeGeographyData(false,true,true);
                    citySpinner.setAdapter(new ArrayAdapter<String>(mContext
                            ,android.R.layout.simple_spinner_dropdown_item
                            ,new String[]{auxiliary.SPINNER_UNSELECTED_CITY}));
                    areaSpinner.setAdapter(new ArrayAdapter<String>(mContext
                            ,android.R.layout.simple_spinner_dropdown_item
                            ,new String[]{auxiliary.SPINNER_UNSELECTED_AREA}));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position!=0){
                    fetchAreaData(auxiliary.SERVER_URL+"/addressBookManager.php"
                            ,stateData.get(position).keySet().iterator().next()
                            ,cityData.get(position).keySet().iterator().next());
                    areaSpinner.setAdapter(new ArrayAdapter<>(mContext
                            ,android.R.layout.simple_spinner_dropdown_item
                            ,auxiliary.arrayListOfHMtoArrayListOfHMVals(areaData)));
                } else{
                    initializeGeographyData(false,false,true);
                    areaSpinner.setAdapter(new ArrayAdapter<String>(mContext
                            ,android.R.layout.simple_spinner_dropdown_item
                            ,new String[]{auxiliary.SPINNER_UNSELECTED_AREA}));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean validateInput(){
        boolean validationStatus=true;
        if(completeAddressET.getText().length()==0){
            return false;
        }
        if(stateSpinner.getSelectedItemPosition()==0){
            return false;
        }
        if(citySpinner.getSelectedItemPosition()==0){
            return false;
        }
        if(areaSpinner.getSelectedItemPosition()==0){
            return false;
        }
        return !validationStatus;
    }

    private void initializeGeographyData(boolean initialize_state_data,
                                         boolean initialize_city_data,
                                         boolean initialize_area_data){
        if(initialize_state_data){
            stateData=new ArrayList<HashMap<String, String>>();
            stateData.add(new HashMap<String, String>(){{put("0",auxiliary.SPINNER_UNSELECTED_STATE);}});
        }
        if(initialize_city_data){
            cityData=new ArrayList<HashMap<String, String>>();
            cityData.add(new HashMap<String, String>(){{put("0",auxiliary.SPINNER_UNSELECTED_CITY);}});
        }
        if(initialize_area_data){
            areaData=new ArrayList<HashMap<String, String>>();
            areaData.add(new HashMap<String, String>(){{put("0",auxiliary.SPINNER_UNSELECTED_AREA);}});
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void fetchStateData(final String urlWebService){
        // Fetch list of states.
        class FetchStateData extends AsyncTask<Void,Void,Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos=new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>(){
                        {
                            put(auxiliary.PPK_INITIAL_CHECK,auxiliary.PPV_INITIAL_CHECK);
                            put(auxiliary.PPK_REQUESTTYPE,auxiliary.PPV_REQUESTTYPE_ADDRESSSTATEDATAFETCH);
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
                    //Log.i("sql (addAddress->fetchStateData)",sb.toString().trim());
                    if(!sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_FAIL) &&
                            !sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_NOT_SET)){
                        JSONArray jsonArray=new JSONArray(sb.toString().trim());
                        for(int i=0;i<jsonArray.length();i++){
                            final JSONObject jsonObject=jsonArray.getJSONObject(i);
                            stateData.add(new HashMap<String, String>(){
                                {
                                    put(jsonObject.getString("state_id"),jsonObject.getString("state_name"));
                                }
                            });
                        }
                    } else{
                        Log.i("PPK","Check fail in addAddress.java in fetchStateData");
                    }
                } catch (MalformedURLException mue){
                    Log.i("exception","MalformedURLException occurred in fetchAreas (addAddress.java)");
                    mue.printStackTrace();
                } catch (IOException io){
                    Log.i("exception","IOException occurred in fetchAreas (addAddress.java)");
                    io.printStackTrace();
                } catch (Exception e){
                    Log.i("exception","Exception occurred in fetchAreas (addAddress.java)");
                    e.printStackTrace();
                }
                return null;
            }
        }
        FetchStateData fetchStateData=new FetchStateData();
        try {
            fetchStateData.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void fetchCityData(final String urlWebService, final String state_id){
        // Fetch list of cities based on state id.
        class FetchCityData extends AsyncTask<Void,Void,Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos=new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>(){
                        {
                            put(auxiliary.PPK_INITIAL_CHECK,auxiliary.PPV_INITIAL_CHECK);
                            put(auxiliary.PPK_REQUESTTYPE,auxiliary.PPV_REQUESTTYPE_ADDRESSCITYDATAFETCH);
                            put(auxiliary.PPK_STATEID,state_id);
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
                    Log.i("sql (addAddress->fetchCityData)",sb.toString().trim());
                    if(!sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_FAIL) &&
                            !sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_NOT_SET)){
                        JSONArray jsonArray=new JSONArray(sb.toString().trim());
                        for(int i=0;i<jsonArray.length();i++){
                            final JSONObject jsonObject=jsonArray.getJSONObject(i);
                            cityData.add(new HashMap<String, String>(){
                                {
                                    put(jsonObject.getString("city_id"),jsonObject.getString("city_name"));
                                }
                            });
                        }
                    } else{
                        Log.i("PPK","Check fail in addAddress.java in fetchCityData");
                    }
                } catch (MalformedURLException mue){
                    Log.i("exception","MalformedURLException occurred in fetchCityData (addAddress.java)");
                    mue.printStackTrace();
                } catch (IOException io){
                    Log.i("exception","IOException occurred in fetchCityData (addAddress.java)");
                    io.printStackTrace();
                } catch (Exception e){
                    Log.i("exception","Exception occurred in fetchCityData (addAddress.java)");
                    e.printStackTrace();
                }
                return null;
            }
        }
        FetchCityData fetchCityData=new FetchCityData();
        try {
            fetchCityData.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void fetchAreaData(final String urlWebService, final String state_id, final String city_id){
        // Fetches list of areas based on ids of state and city
        class FetchAreaData extends AsyncTask<Void,Void,Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos=new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>(){
                        {
                            put(auxiliary.PPK_INITIAL_CHECK,auxiliary.PPV_INITIAL_CHECK);
                            put(auxiliary.PPK_REQUESTTYPE,auxiliary.PPV_REQUESTTYPE_ADDRESSAREADATAFETCH);
                            put(auxiliary.PPK_STATEID,state_id);
                            put(auxiliary.PPK_CITYID,city_id);
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
                    Log.i("sql (addAddress->fetchAreaData)",sb.toString().trim());
                    if(!sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_FAIL) &&
                            !sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_NOT_SET)){
                        JSONArray jsonArray=new JSONArray(sb.toString().trim());
                        for(int i=0;i<jsonArray.length();i++){
                            final JSONObject jsonObject=jsonArray.getJSONObject(i);
                            areaData.add(new HashMap<String, String>(){
                                {
                                    put(jsonObject.getString("area_id"),jsonObject.getString("area_name"));
                                }
                            });
                        }
                    } else{
                        Log.i("PPK","Check fail in addAddress.java in fetchAreaData");
                    }
                } catch (MalformedURLException mue){
                    Log.i("exception","MalformedURLException occurred in fetchAreas (addAddress.java)");
                    mue.printStackTrace();
                } catch (IOException io){
                    Log.i("exception","IOException occurred in fetchAreas (addAddress.java)");
                    io.printStackTrace();
                } catch (Exception e){
                    Log.i("exception","Exception occurred in fetchAreas (addAddress.java)");
                    e.printStackTrace();
                }
                return null;
            }
        }
        FetchAreaData fetchAreaData=new FetchAreaData();
        try {
            fetchAreaData.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
