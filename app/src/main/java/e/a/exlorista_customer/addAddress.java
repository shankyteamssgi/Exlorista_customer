package e.a.exlorista_customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class addAddress extends AppCompatActivity {

    private EditText completeAddressET, addressLandmarkET, addOtherTagET;
    private Spinner stateSpinner, citySpinner, areaSpinner;
    private Button saveAddressB;
    private LinearLayout cityChoiceLL, stateChoiceLL, homeTagLL, officeTagLL;
    private TextView homeTagTextTV, officeTagTextTV;
    private ArrayList<HashMap<String,String>> stateData;
    private ArrayList<HashMap<String, String>> cityData;
    private ArrayList<HashMap<String, String>> areaData;

    enum addressTag {HOME, OFFICE, CUSTOM_TAG};
    addressTag selectedAddressTag;
    private Context mContext;

    String current_state, current_city, current_latitude, current_longitude;
    String customer_id,address_complete,address_landmark;
    StringBuilder address_tag;
    String selected_area;
    String address_areaId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        //setTitle("Add address");
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        current_state = getIntent().getStringExtra(auxiliary.ADDR_STATE);
        current_city = getIntent().getStringExtra(auxiliary.ADDR_CITY);
        current_latitude = getIntent().getStringExtra(auxiliary.ADDR_LAT);
        current_longitude = getIntent().getStringExtra(auxiliary.ADDR_LONG);

        //areaData initlize

        mContext = this;
        saveAddressB = findViewById(R.id.saveAddressB);
        completeAddressET = findViewById(R.id.completeAddressET);
        addressLandmarkET = findViewById(R.id.addressLandmarkET);
        addOtherTagET = findViewById(R.id.addOtherTagET);
        stateSpinner = findViewById(R.id.stateSpinner);
        citySpinner = findViewById(R.id.citySpinner);
        areaSpinner = findViewById(R.id.areaSpinner);
        homeTagLL = findViewById(R.id.homeTagLL);
        officeTagLL = findViewById(R.id.officeTagLL);
        homeTagTextTV = findViewById(R.id.homeTagTextTV);
        officeTagTextTV = findViewById(R.id.officeTagTextTV);
        saveAddressB = findViewById(R.id.saveAddressB);
        cityChoiceLL = findViewById(R.id.cityChoiceLL);
        stateChoiceLL = findViewById(R.id.stateChoiceLL);
        selectedAddressTag = null;


        initializeGeographyData(true, true, true);
        initializeAddressTagButtons();
        addListenersToTagViews();

        takeActionForLocationPermissionNotGranted();
        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position != 0) {
                   selected_area = areaSpinner.getSelectedItem().toString().trim();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        saveAddressB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInput()){
                    addAddressToDb(auxiliary.SERVER_URL + "/addressBookManager.php"
                            , customer_id
                            , address_complete
                            , address_landmark
                            , current_latitude
                            , current_longitude
                            , address_areaId
                            , address_tag.toString().trim());
                    Toast.makeText(mContext,"Address added successfully",Toast.LENGTH_LONG).show();
                    finish();
                } else{
                    Log.i("addAddress","input validation failed");
                }
            }
        });

    }

    private void takeActionForLocationPermissionNotGranted() {

        // Location permission not granted. Take following actions:
        /*
        - Make dropdown for state and city visible.
        - Retrieve list of areas from database based on chosen state and city. Put those areas in the spinner for area.
        - Validate inputs.
        - If input validation is successful, add address to database.
         */

        if (current_state == null && current_city == null) {

            stateChoiceLL.setVisibility(View.VISIBLE);
            cityChoiceLL.setVisibility(View.VISIBLE);
            //change hear
            // fatchStateID_and_CityID(auxiliary.SERVER_URL+"/addressBookManager.php",currantState,currentCity);
            fetchStateData(auxiliary.SERVER_URL + "/addressBookManager.php");
            stateSpinner.setAdapter(new ArrayAdapter<String>(mContext
                    , android.R.layout.simple_spinner_dropdown_item
                    , auxiliary.arrayListOfHMtoArrayListOfHMVals(stateData)));
            citySpinner.setAdapter(new ArrayAdapter<String>(mContext
                    , android.R.layout.simple_spinner_dropdown_item
                    , new String[]{auxiliary.SPINNER_UNSELECTED_CITY}));
            areaSpinner.setAdapter(new ArrayAdapter<String>(mContext
                    , android.R.layout.simple_spinner_dropdown_item
                    , new String[]{auxiliary.SPINNER_UNSELECTED_AREA}));
            stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    if (position != 0) {
                        fetchCityData(auxiliary.SERVER_URL + "/addressBookManager.php"
                                , stateData.get(position).keySet().iterator().next());
                        citySpinner.setAdapter(new ArrayAdapter<String>(mContext
                                , android.R.layout.simple_spinner_dropdown_item
                                , auxiliary.arrayListOfHMtoArrayListOfHMVals(cityData)));
                    } else {
                        initializeGeographyData(false, true, true);
                        citySpinner.setAdapter(new ArrayAdapter<String>(mContext
                                , android.R.layout.simple_spinner_dropdown_item
                                , new String[]{auxiliary.SPINNER_UNSELECTED_CITY}));
                        areaSpinner.setAdapter(new ArrayAdapter<String>(mContext
                                , android.R.layout.simple_spinner_dropdown_item
                                , new String[]{auxiliary.SPINNER_UNSELECTED_AREA}));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    if (position != 0) {
                        fetchAreaData(auxiliary.SERVER_URL + "/addressBookManager.php"
                                , stateData.get(position).keySet().iterator().next()
                                , cityData.get(position).keySet().iterator().next(), null, null);
                        areaSpinner.setAdapter(new ArrayAdapter<>(mContext
                                , android.R.layout.simple_spinner_dropdown_item
                                , auxiliary.arrayListOfHMtoArrayListOfHMVals(areaData)));
                    } else {
                        initializeGeographyData(false, false, true);
                        areaSpinner.setAdapter(new ArrayAdapter<String>(mContext
                                , android.R.layout.simple_spinner_dropdown_item
                                , new String[]{auxiliary.SPINNER_UNSELECTED_AREA}));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            stateChoiceLL.setVisibility(View.GONE);
            cityChoiceLL.setVisibility(View.GONE);

            fetchAreaData(auxiliary.SERVER_URL + "/addressBookManager.php"
                    , null
                    , null, current_state, current_city);

            areaSpinner.setAdapter(new ArrayAdapter<>(mContext
                    , android.R.layout.simple_spinner_dropdown_item
                    , auxiliary.arrayListOfHMtoArrayListOfHMVals(areaData)));


        }


    }

//    private void addAddressByValidationToDb(String urlWebService){
//        if(validateInput()){
//            // fetch relevant address fields from views
//            String customer_id ="";
//            String address_complete=completeAddressET.getText().toString().trim();
//            String address_landmark=addressLandmarkET.getText().toString().trim();
//
//            String address_areaId=areaData.get(areaSpinner.getSelectedItemPosition()).get("area_id");
//            StringBuilder address_tag=new StringBuilder();
//            switch (selectedAddressTag){
//                case HOME:
//                    address_tag.append("home");
//                    break;
//                case OFFICE:
//                    address_tag.append("office");
//                    break;
//                case CUSTOM_TAG:
//                    address_tag.append(addOtherTagET.getText().toString().trim());
//                    break;
//            }
//            // add address to database
//            addAddressToDb(urlWebService
//                    ,customer_id
//                    ,address_complete
//                    ,address_landmark
//                    ,current_latitude
//                    ,current_longitude
//                    ,address_areaId
//                    ,address_tag.toString().trim());
//        } else{
//            // don't add address to database
//        }
//    }

    private void addAddressToDb(final String urlWebService,
                                final String customer_id,
                                final String address_complete,
                                final String address_landmark,
                                final String address_latitude,
                                final String address_longtude,
                                final String address_areaId,
                                final String address_tag) {
        class AddAddressToDb extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>() {
                        {
                            put(auxiliary.PPK_INITIAL_CHECK, auxiliary.PPV_INITIAL_CHECK);
                            put(auxiliary.PPK_REQUESTTYPE, auxiliary.PPV_REQUESTTYPE_ADDRESSADD);
                            put(auxiliary.PPK_CUSTID, customer_id);
                            put(auxiliary.PPK_ADDRCOMPLETE, address_complete);
                            put(auxiliary.PPK_ADDRLANDMARK, address_landmark);
                            put(auxiliary.PPK_ADDRLAT, address_latitude);
                            put(auxiliary.PPK_ADDRLONG, address_longtude);
                            put(auxiliary.PPK_AREAID, address_areaId);
                            put(auxiliary.PPK_ADDRTAG, address_tag);
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
                } catch (MalformedURLException mue) {
                    Log.i("exception", "MalformedURLException occurred in addAddressToDb (addAddress.java)");
                    mue.printStackTrace();
                } catch (IOException io) {
                    Log.i("exception", "IOException occurred in addAddressToDb (addAddress.java)");
                    io.printStackTrace();
                } catch (Exception e) {
                    Log.i("exception", "Exception occurred in addAddressToDb (addAddress.java)");
                    e.printStackTrace();
                }
                return null;
            }
        }
        AddAddressToDb addAddressToDb = new AddAddressToDb();
        try {
            addAddressToDb.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        boolean validationStatus=true;
        StringBuilder error_message = new StringBuilder();
        try {
            customer_id = auxiliary.DUMMYVAL_CUSTID;
            address_complete = completeAddressET.getText().toString().trim();
            address_landmark = addressLandmarkET.getText().toString().trim();
            // geting key for selected area
            /*
            for(HashMap<String, String> map: areaData) {
                boolean getKey=false;
                for (Map.Entry<String, String> mapEntry : map.entrySet()) {
                    if(mapEntry.getValue().equals(selected_area)){
                        address_areaId  = mapEntry.getKey();
                        getKey=true;
                        break; }
                }
                if(getKey){
                    break;
                }
            }*/
            if(selected_area==null){
                validationStatus=false;
                error_message.append("Select an area.").append(" ");
            }
            address_areaId=auxiliary.findKeyForGivenValueInArrayListOfHM(areaData,selected_area);
            address_tag = new StringBuilder();
            if (selectedAddressTag == null) {
                validationStatus=false;
                error_message.append("Select or add a tag.").append(" ");
            } else{
                switch (selectedAddressTag) {
                    case HOME:
                        address_tag.append("home");
                        break;
                    case OFFICE:
                        address_tag.append("office");
                        break;
                    case CUSTOM_TAG:
                        if(addOtherTagET.getText().toString().equals("")){
                            addOtherTagET.setError("Add a tag");
                        } else{
                            if(addOtherTagET.getText().toString().length() > auxiliary.CUSTOMTAG_MAXLENGTH){
                                validationStatus=false;
                                error_message.append("Length of tag cannot exceed ").append(auxiliary.CUSTOMTAG_MAXLENGTH).append(". ");
                            } else{
                                address_tag.append(addOtherTagET.getText().toString().trim());
                            }
                        }
                        break;
                }
            }

            if (address_complete.equals("")) {
                validationStatus=false;
                completeAddressET.requestFocus();
                completeAddressET.setError("Address can't be empty!");
            } else if (address_complete.length() > auxiliary.COMPLETEADDRESS_MAXLENGTH) {
                validationStatus=false;
                completeAddressET.requestFocus();
                completeAddressET.setError("Address length exceeded.");
            }
            if (address_landmark.length() > auxiliary.LANDMARK_MAXLENGTH) {
                validationStatus=false;
                addressLandmarkET.requestFocus();
                addressLandmarkET.setError("Landmark length exceeded.");
            }
            if(address_areaId.equals("")){
                validationStatus=false;
                Log.i("Area->","area id is null.");
            }
        } catch (Exception e) {
            validationStatus=false;
            e.printStackTrace();
        }
        Toast.makeText(mContext,error_message.toString().trim(),Toast.LENGTH_LONG).show();
        return validationStatus;
    }

    private void initializeGeographyData(boolean initialize_state_data,
                                         boolean initialize_city_data,
                                         boolean initialize_area_data) {
        if (initialize_state_data) {
            stateData = new ArrayList<HashMap<String, String>>();
            stateData.add(new HashMap<String, String>() {{
                put("0", auxiliary.SPINNER_UNSELECTED_STATE);
            }});
        }
        if (initialize_city_data) {
            cityData = new ArrayList<HashMap<String, String>>();
            cityData.add(new HashMap<String, String>() {{
                put("0", auxiliary.SPINNER_UNSELECTED_CITY);
            }});
        }
        if (initialize_area_data) {
            areaData = new ArrayList<HashMap<String, String>>();
            areaData.add(new HashMap<String, String>() {{
                put("0", auxiliary.SPINNER_UNSELECTED_AREA);
            }});

        }
    }

    private void initializeAddressTagButtons() {
        homeTagLL.setBackground(ContextCompat.getDrawable(mContext, R.drawable.default_border_store_page));
        homeTagTextTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
        officeTagLL.setBackground(ContextCompat.getDrawable(mContext, R.drawable.default_border_store_page));
        officeTagTextTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
    }

    private void addListenersToTagViews() {
        homeTagLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeTagLL.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_store_page));
                homeTagTextTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                officeTagLL.setBackground(ContextCompat.getDrawable(mContext, R.drawable.default_border_store_page));
                officeTagTextTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));

                if (addOtherTagET.hasFocus()) {
                    addOtherTagET.setText("");
                    addOtherTagET.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(addOtherTagET.getWindowToken(), 0);
                    //onBackPressed();
                }

                selectedAddressTag = addressTag.HOME;
            }
        });
        officeTagLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                officeTagLL.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_store_page));
                officeTagTextTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                homeTagLL.setBackground(ContextCompat.getDrawable(mContext, R.drawable.default_border_store_page));
                homeTagTextTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));

                if (addOtherTagET.hasFocus()) {
                    addOtherTagET.setText("");
                    addOtherTagET.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(addOtherTagET.getWindowToken(), 0);
                    //onBackPressed();
                }

                selectedAddressTag = addressTag.OFFICE;
            }
        });

        addOtherTagET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    selectedAddressTag = null;
                } else if (charSequence.length() > 0) {
                    initializeAddressTagButtons();
                    selectedAddressTag = addressTag.CUSTOM_TAG;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void fetchStateData(final String urlWebService) {
        // Fetch list of states.
        class FetchStateData extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>() {
                        {
                            put(auxiliary.PPK_INITIAL_CHECK, auxiliary.PPV_INITIAL_CHECK);
                            put(auxiliary.PPK_REQUESTTYPE, auxiliary.PPV_REQUESTTYPE_ADDRESSSTATEDATAFETCH);
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
                    if (!sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_FAIL) &&
                            !sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_NOT_SET)) {
                        JSONArray jsonArray = new JSONArray(sb.toString().trim());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final JSONObject jsonObject = jsonArray.getJSONObject(i);
                            stateData.add(new HashMap<String, String>() {
                                {
                                    put(jsonObject.getString("state_id"), jsonObject.getString("state_name"));
                                }
                            });
                        }
                    } else {
                        Log.i("PPK", "Check fail in addAddress.java in fetchStateData");
                    }
                } catch (MalformedURLException mue) {
                    Log.i("exception", "MalformedURLException occurred in fetchAreas (addAddress.java)");
                    mue.printStackTrace();
                } catch (IOException io) {
                    Log.i("exception", "IOException occurred in fetchAreas (addAddress.java)");
                    io.printStackTrace();
                } catch (Exception e) {
                    Log.i("exception", "Exception occurred in fetchAreas (addAddress.java)");
                    e.printStackTrace();
                }
                return null;
            }
        }
        FetchStateData fetchStateData = new FetchStateData();
        try {
            fetchStateData.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void fetchCityData(final String urlWebService, final String state_id) {
        // Fetch list of cities based on state id.
        class FetchCityData extends AsyncTask<Void, Void, Void> {
            @SuppressLint("LongLogTag")
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>() {
                        {
                            put(auxiliary.PPK_INITIAL_CHECK, auxiliary.PPV_INITIAL_CHECK);
                            put(auxiliary.PPK_REQUESTTYPE, auxiliary.PPV_REQUESTTYPE_ADDRESSCITYDATAFETCH);
                            put(auxiliary.PPK_STATEID, state_id);
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
                    Log.i("sql (addAddress->fetchCityData)", sb.toString().trim());
                    if (!sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_FAIL) &&
                            !sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_NOT_SET)) {
                        JSONArray jsonArray = new JSONArray(sb.toString().trim());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final JSONObject jsonObject = jsonArray.getJSONObject(i);
                            cityData.add(new HashMap<String, String>() {
                                {
                                    put(jsonObject.getString("city_id"), jsonObject.getString("city_name"));
                                }
                            });
                        }
                    } else {
                        Log.i("PPK", "Check fail in addAddress.java in fetchCityData");
                    }
                } catch (MalformedURLException mue) {
                    Log.i("exception", "MalformedURLException occurred in fetchCityData (addAddress.java)");
                    mue.printStackTrace();
                } catch (IOException io) {
                    Log.i("exception", "IOException occurred in fetchCityData (addAddress.java)");
                    io.printStackTrace();
                } catch (Exception e) {
                    Log.i("exception", "Exception occurred in fetchCityData (addAddress.java)");
                    e.printStackTrace();
                }
                return null;
            }
        }
        FetchCityData fetchCityData = new FetchCityData();
        try {
            fetchCityData.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void fetchAreaData(final String urlWebService,
                               final String state_id,
                               final String city_id,
                               final String state_name,
                               final String city_name) {
        // Fetches list of areas based on ids of state and city
        class FetchAreaData extends AsyncTask<Void, Void, Void> {
            @SuppressLint("LongLogTag")
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
//                    dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>(){
//                        {
//                            put(auxiliary.PPK_INITIAL_CHECK,auxiliary.PPV_INITIAL_CHECK);
//                            put(auxiliary.PPK_REQUESTTYPE,auxiliary.PPV_REQUESTTYPE_ADDRESSAREADATAFETCH);
//                        }
                    //                   }));
                    if (state_name == null && city_name == null) {
                        dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>() {
                            {
                                put(auxiliary.PPK_INITIAL_CHECK, auxiliary.PPV_INITIAL_CHECK);
                                put(auxiliary.PPK_REQUESTTYPE, auxiliary.PPV_REQUESTTYPE_ADDRESSAREADATAFETCH);
                                put(auxiliary.PPK_STATEID, state_id);
                                put(auxiliary.PPK_CITYID, city_id);
                            }
                        }));
                    } else if (state_id == null && city_id == null) {
                        dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>() {
                            {
                                put(auxiliary.PPK_INITIAL_CHECK, auxiliary.PPV_INITIAL_CHECK);
                                put(auxiliary.PPK_REQUESTTYPE, auxiliary.PPV_REQUESTTYPE_ADDRESSAREADATAFETCH);
                                put(auxiliary.PPK_STATENAME, state_name);
                                put(auxiliary.PPK_CITYNAME, city_name);
                            }
                        }));
                    }
                    dos.flush();
                    dos.close();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json);
                    }
                    Log.i("sql (addAddress->fetchAreaData)", sb.toString().trim());
                    if (!sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_FAIL) &&
                            !sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_NOT_SET)) {
                        JSONArray jsonArray = new JSONArray(sb.toString().trim());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final JSONObject jsonObject = jsonArray.getJSONObject(i);
                            areaData.add(new HashMap<String, String>() {
                                {
                                    put(jsonObject.getString("area_id"), jsonObject.getString("area_name"));
                                }
                            });

                            // can't find state and city inside DB
                            Log.i("sql (addAddress->fetchAreaId from DB -> null)", sb.toString().trim());
                        }
                    } else {
                        Log.i("PPK", "Check fail in addAddress.java in fetchAreaData");
                    }
                } catch (MalformedURLException mue) {
                    Log.i("exception", "MalformedURLException occurred in fetchAreas (addAddress.java)");
                    mue.printStackTrace();
                } catch (IOException io) {
                    Log.i("exception", "IOException occurred in fetchAreas (addAddress.java)");
                    io.printStackTrace();
                } catch (Exception e) {
                    Log.i("exception", "Exception occurred in fetchAreas (addAddress.java)");
                    e.printStackTrace();
                }
                return null;
            }
        }
        FetchAreaData fetchAreaData = new FetchAreaData();
        try {
            fetchAreaData.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }




}
