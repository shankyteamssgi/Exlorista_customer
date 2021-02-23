package e.a.exlorista_customer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class setLocation extends AppCompatActivity {

    ImageView setLocationIV;
    TextView setLocationMsgTV;
    Button useCurrentLocationB;
    Button setLocationManuallyB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);

        setLocationIV=findViewById(R.id.setLocationIV);
        setLocationMsgTV=findViewById(R.id.setLocationMsgTV);
        useCurrentLocationB=findViewById(R.id.useCurrentLocationB);
        setLocationManuallyB=findViewById(R.id.setLocationManuallyB);

    }
}