package e.a.exlorista_customer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class NoInternet extends AppCompatActivity {

    Bundle extras;
    int internet_status_code; // 0: no internet connection , 1: connection to database failure

    TextView noInternetTV;
    Button retryButton;
    String callingActivityClassName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        setTitle("An error occurred");
        extras=getIntent().getExtras();

        noInternetTV=findViewById(R.id.noInternet);
        retryButton=findViewById(R.id.retryConnect);

        callingActivityClassName=extras.getString(auxiliary.CALLING_ACTIVITY_NAME);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (callingActivityClassName.equals("MainActivity")) {
                        //Log.i("CONTROL","inside if");
                    }
                    else{
                        //Log.i("CONTROL","inside else");
                    }
                    reloadCallingActivity(callingActivityClassName);
                    finish();
                } catch (NullPointerException ne){
                    //Log.i("exception","NullPointerException -> NoInternet.java in onClick");
                    //ne.printStackTrace();
                } catch (Exception e){
                    //Log.i("exception","Exception -> NoInternet.java"+NoInternet.this.getPackageName());
                    //e.printStackTrace();
                }
            }
        });

        try{
            internet_status_code=extras.getInt(auxiliary.INTERNET_STATUS_STRING);
        } catch (NullPointerException ne){
            //Log.i("ERROR","NullPointer occurred while fetching internet_status_code");
            //ne.printStackTrace();
        }
        if(internet_status_code==0){
            noInternetTV.setText("No Internet. Please check your internet connection");
        }
        else{
            noInternetTV.setText("Unable to establish connection to our server");
        }
        //Log.i(auxiliary.CALLING_ACTIVITY_NAME,extras.getString(auxiliary.CALLING_ACTIVITY_NAME));
    }

    private void reloadCallingActivity(String callingActivityClassName, int... intentFlags){
        Intent intent=new Intent();
        intent.setClassName(NoInternet.this,
                NoInternet.this.getPackageName()+"."+callingActivityClassName);
        for(int i=0;i<intentFlags.length;i++){
            intent.addFlags(i);
        }
        NoInternet.this.startActivity(intent);
    }

}
