package e.a.exlorista_customer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

public class addressBook extends AppCompatActivity implements LocationListener {


    enum userPermissionResponse {LOCATION_NOT_ENABLED, PERMISSION_DENIED, PERMISSION_DENIED_WITH_NEVERASKAGAIN}

    ;
    // Location
    private static final int LOCATION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    LocationRequest locationRequest;

    Button addNewAddressB;
    RecyclerView addressListRV;
    RecyclerView.LayoutManager addressListRVLayoutManager;
    RecyclerView.Adapter addressListRVAdapter;

    Context mContext;
    LocationManager locationManager;
    Location location;
    private Double longitude;
    private Double latitude;
    private String current_State;
    private String current_City;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);

        /*setTitle("Address book");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);




        mContext = this;

        addNewAddressB = findViewById(R.id.addNewAddressB);
        addressListRV = findViewById(R.id.addressListRV);

        addNewAddressB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent addressBookToAddAddressIntent = new Intent(addressBook.this, addAddress.class);
                addressBookToAddAddressIntent.putExtra("current_State",current_State);
                addressBookToAddAddressIntent.putExtra("current_City",current_City);
                startActivity(addressBookToAddAddressIntent);


            }
        });

        addressListRVLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        addressListRVAdapter = new AddressBookAdapter(this);
        addressListRV.setLayoutManager(addressListRVLayoutManager);
        addressListRV.setAdapter(addressListRVAdapter);
        setLocationRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (gpsEnabled(mContext)) {
            //Log.i("LOCATION","Gps is enabled");
            // Call below code only when location access is granted and lat, long is fetched
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.i("LOCATION", "Permission already granted");

                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                onLocationChanged(location);
                findAddress();


            } else {
                Log.i("LOCATION", "Permission not granted. Requesting permission.");
                ActivityCompat.requestPermissions(addressBook.this
                        , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        , REQUEST_CODE_LOCATION_PERMISSION);
            }
        } else {
            Log.i("LOCATION", "Gps is disabled.");
            setLocationSettings();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setLocationRequest() {
        if (locationRequest == null) {
            Log.i("LOCATION", "locationRequest was null");
            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
        } else {
            Log.i("LOCATION", "locationRequest was already set");
        }
    }

    private void setLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        Task<LocationSettingsResponse> result = LocationServices
                .getSettingsClient(this)
                .checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //getCurrentLocation();
//                        if(!auxiliaryuseraccountmanager
//                                .userWithThisPhoneExistsInDb(auxiliary.SERVER_URL+"/store_userManagement.php"
//                                        ,store_ownerPhone)){
//                            proceedToOtpVerification(auxiliaryuseraccountmanager.actionType.REGISTER);
//                        } else{
//                            Toast.makeText(mContext,"user already exists",Toast.LENGTH_LONG).show();
//                        }


                    } else {
                        ActivityCompat.requestPermissions(addressBook.this
                                , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                                , REQUEST_CODE_LOCATION_PERMISSION);
                    }
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        addressBook.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                                Log.i("LOCATION", "SendIntentException");
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                                Log.i("LOCATION", "ClassCastException");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            Log.i("LOCATION", "Settings change unavailable");
                            break;
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            //getCurrentLocation();
//                            if(!auxiliaryuseraccountmanager
//                                    .userWithThisPhoneExistsInDb(auxiliary.SERVER_URL+"/store_userManagement.php"
//                                            ,store_ownerPhone)){
//                                proceedToOtpVerification(auxiliaryuseraccountmanager.actionType.REGISTER);
//                            } else{
//                                Toast.makeText(mContext,"user already exists",Toast.LENGTH_LONG).show();
//                            }
                        } else {
                            ActivityCompat.requestPermissions(addressBook.this
                                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                                    , REQUEST_CODE_LOCATION_PERMISSION);
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        // Display an informative message here as to why is fetching location required.
                        Log.i("LOCATION", "result cancelled");
                        msgExplanation(userPermissionResponse.LOCATION_NOT_ENABLED);
                        break;
                    default:
                        Log.i("LOCATION", "default branch entered");
                        break;
                }
                break;
        }
    }

    private void msgExplanation(userPermissionResponse permission_response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        switch (permission_response) {
            case LOCATION_NOT_ENABLED:
                builder.setTitle("Location not enabled!")
                        .setMessage("Location should be enabled to locate your store on the map")
                        .setCancelable(false)
                        .setPositiveButton("Enable location", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setLocationSettings();
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                break;
            case PERMISSION_DENIED:
                builder.setTitle("Permission denied!")
                        .setMessage("This permission is required to locate your store on the map. Give location permission ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(addressBook.this
                                        , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                                        , REQUEST_CODE_LOCATION_PERMISSION);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                break;
            case PERMISSION_DENIED_WITH_NEVERASKAGAIN:
                builder.setTitle("Permission denied with never ask again!")
                        .setMessage("This permission is required to locate your store on the map. Go to Settings -> App permissions")
                        .setCancelable(false)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                break;
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*
        if(requestCode==REQUEST_CODE_LOCATION_PERMISSION && grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
                proceedToOtpVerification(auxiliaryuseraccountmanager.actionType.REGISTER);
            } else{
                Toast.makeText(mContext,"Permission denied!",Toast.LENGTH_LONG).show();
            }
        }*/


        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Log.i("LOCATION", "Permission denied");
                msgExplanation(userPermissionResponse.PERMISSION_DENIED);
            } else {
                if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    //getCurrentLocation();
//                    if (!auxiliaryuseraccountmanager
//                            .userWithThisPhoneExistsInDb(auxiliary.SERVER_URL + "/store_userManagement.php"
//                                    , store_ownerPhone)) {
//                        proceedToOtpVerification(auxiliaryuseraccountmanager.actionType.REGISTER);
//                    } else {
//                        Toast.makeText(mContext, "user already exists", Toast.LENGTH_LONG).show();
//                    }
                } else {
                    Log.i("LOCATION", "set to never ask again");
                    msgExplanation(userPermissionResponse.PERMISSION_DENIED_WITH_NEVERASKAGAIN);
                }
            }
        }
    }

    static boolean gpsEnabled(Context context) {
        boolean gps_enabled = false;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            if (locationManager != null) {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } else {
                Log.i("LOCATION", "locationManager is null");
            }
        } catch (Exception e) {
            Log.i("LOCATION", "Exception occurred");
            e.printStackTrace();
        }
        return gps_enabled;
    }


    @Override
    public void onLocationChanged(final Location location) {
        if(location!=null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }


    }

    private void findAddress() {

        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, LOCATION_REQUEST_CODE);
             current_State = addresses.get(0).getAdminArea();
             current_City = addresses.get(0).getLocality();





        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {



    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
