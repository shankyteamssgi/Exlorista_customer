package e.a.exlorista_customer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Looper;
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

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import e.a.exlorista_customer.ProgressDialog.progressDialog;

public class addressBook extends AppCompatActivity {

    enum userPermissionResponse {LOCATION_NOT_ENABLED, PERMISSION_DENIED, PERMISSION_DENIED_WITH_NEVERASKAGAIN}

    // Location
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    LocationRequest locationRequest;

    Button addNewAddressB;
    RecyclerView addressListRV;
    RecyclerView.LayoutManager addressListRVLayoutManager;
    RecyclerView.Adapter addressListRVAdapter;

    Context mContext;
    LocationManager locationManager;
    progressDialog progressDailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);

        /*setTitle("Address book");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        mContext = this;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // initilize Dailog
        progressDailog = new progressDialog(this);

        addNewAddressB = findViewById(R.id.addNewAddressB);
        addressListRV = findViewById(R.id.addressListRV);
        addressListRVLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        addressListRVAdapter = new AddressBookAdapter(this);
        addressListRV.setLayoutManager(addressListRVLayoutManager);
        addressListRV.setAdapter(addressListRVAdapter);

        setLocationRequest();
        addNewAddressB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (auxiliary.gpsEnabled(mContext)) {
                    //Log.i("LOCATION","Gps is enabled");
                    // Call below code only when location access is granted and lat, long is fetched
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.i("LOCATION", "Permission already granted");
                        proceedToAddAddress();
                    } else {
                        Log.i("LOCATION", "Permission not granted. Requesting permission.");
                        ActivityCompat.requestPermissions(addressBook.this
                                , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                                , REQUEST_LOCATION_PERMISSION_CODE);
                    }
                } else {
                    Log.i("LOCATION", "Gps is disabled.");
                    setLocationSettings();
                }
            }
        });
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
                        proceedToAddAddress();
                    } else {
                        ActivityCompat.requestPermissions(addressBook.this
                                , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                                , REQUEST_LOCATION_PERMISSION_CODE);
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
                            proceedToAddAddress();
                        } else {
                            ActivityCompat.requestPermissions(addressBook.this
                                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                                    , REQUEST_LOCATION_PERMISSION_CODE);
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
                                        , REQUEST_LOCATION_PERMISSION_CODE);
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
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Log.i("LOCATION", "Permission denied");
                msgExplanation(userPermissionResponse.PERMISSION_DENIED);
            } else {
                if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    proceedToAddAddress();
                } else {
                    Log.i("LOCATION", "set to never ask again");
                    msgExplanation(userPermissionResponse.PERMISSION_DENIED_WITH_NEVERASKAGAIN);
                }
            }
        }
    }

    private void proceedToAddAddress() {
        try {
            progressDailog.startLoading();
            LocationServices.getFusedLocationProviderClient(addressBook.this)
                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(addressBook.this).removeLocationUpdates(this);
                            if (locationResult != null) {
                                if (locationResult.getLocations().size() > 0) {
                                    Intent addressBookToAddAddressIntent = new Intent(addressBook.this, addAddress.class);
                                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                                    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                                    List<Address> addresses = null;
                                    double curr_lat, curr_long;
                                    curr_lat = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                    curr_long = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                                    // We don't know when curr_lat and curr_long will be initialized. Therefore,
                                    // we add an inifite loop which checks whether curr_lat or curr_long are still set to
                                    // their default, i.e., 0.0 or not.
                                    // Them moment both curr_lat and curr_long become a value not equal to their default, i.e, 0.0
                                    // then we know that now they have been initialized.
                                    while(curr_lat==0.0 || curr_long==0.0){};
                                    try {
                                        addresses = geocoder.getFromLocation(curr_lat,curr_long,REQUEST_LOCATION_PERMISSION_CODE);
                                    } catch (IOException ioe) {
                                        ioe.printStackTrace();
                                    }
                                    String curr_state,curr_city;
                                    if(addresses!=null){
                                        curr_state = addresses.get(0).getAdminArea();
                                        curr_city = addresses.get(0).getLocality();
                                    } else{
                                        //Log.i("LOCATION","addresses is null");
                                        return;
                                    }
                                    /*
                                    Log.i("curr_state", curr_state);
                                    Log.i("curr_city", curr_city);
                                    Log.i("curr_lat", Double.toString(curr_lat));
                                    Log.i("curr_long", Double.toString(curr_long));*/
                                    addressBookToAddAddressIntent.putExtra(auxiliary.ADDR_STATE, curr_state);
                                    addressBookToAddAddressIntent.putExtra(auxiliary.ADDR_CITY, curr_city);
                                    addressBookToAddAddressIntent.putExtra(auxiliary.ADDR_LAT
                                            , Double.toString(curr_lat));
                                    addressBookToAddAddressIntent.putExtra(auxiliary.ADDR_LONG
                                            , Double.toString(curr_long));
                                    progressDailog.stopLoading();
                                    startActivity(addressBookToAddAddressIntent);
                                } else {
                                    Log.i("LOCATION", "locationResult.getLocations().size() not greater than 0");
                                }
                            } else {
                                Log.i("LOCATION", "locationResult is null");
                            }
                        }
                    }, Looper.getMainLooper());
        } catch (NullPointerException npe){
            npe.printStackTrace();
        } catch (SecurityException se){
            se.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
