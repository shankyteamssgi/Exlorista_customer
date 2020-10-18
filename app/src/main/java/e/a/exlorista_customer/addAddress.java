package e.a.exlorista_customer;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class addAddress extends AppCompatActivity {

    private EditText completeAddressET,addressLandmarkET,addOtherTagET;
    private Spinner stateSpinner,citySpinner,areaSpinner;
    private Button homeTagB,officeTagB,saveAddressB;
    private LinearLayout cityChoiceLL,stateChoiceLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        setTitle("Add address");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        completeAddressET=findViewById(R.id.completeAddressET);
        addressLandmarkET=findViewById(R.id.addressLandmarkET);
        addOtherTagET=findViewById(R.id.addOtherTagET);
        stateSpinner=findViewById(R.id.stateSpinner);
        citySpinner=findViewById(R.id.citySpinner);
        areaSpinner=findViewById(R.id.areaSpinner);
        homeTagB=findViewById(R.id.homeTagB);
        officeTagB=findViewById(R.id.officeTagB);
        saveAddressB=findViewById(R.id.saveAddressB);
        cityChoiceLL=findViewById(R.id.cityChoiceLL);
        stateChoiceLL=findViewById(R.id.stateChoiceLL);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
