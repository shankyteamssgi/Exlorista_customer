package e.a.exlorista_customer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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

import java.util.HashMap;

public class payment extends AppCompatActivity {

    Context mContext;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mContext=this;
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        //FacebookSdk.sdkInitialize(getApplicationContext());
        try{
            HashMap<String,String> userDetailHM=auxiliaryuseraccountmanager.getUserDetailsFromSP(mContext);
            Log.i("SIGNED IN","email: "+user.getEmail());
            Log.i("SIGNED IN","display name: "+user.getDisplayName());
            Log.i("SIGNED IN","phone number: "+user.getPhoneNumber());
            //Log.i("SIGNED IN", "FB id: "+Profile.getCurrentProfile().getId());
            Log.i("INFO FROM SP","email: "+userDetailHM.get(auxiliaryuseraccountmanager.DETAILTYPE_EMAIL));
            Log.i("INFO FROM SP","display name: "+userDetailHM.get(auxiliaryuseraccountmanager.DETAILTYPE_DISPLAYNAME));
            Log.i("INFO FROM SP","phone number: "+userDetailHM.get(auxiliaryuseraccountmanager.DETAILTYPE_PHONENUMBER));
        } catch (NullPointerException npe){
            Log.i("SIGNED IN","NPE occurred");
            npe.printStackTrace();
        } catch (Exception e){
            Log.i("SIGNED IN","Exception occurred");
            e.printStackTrace();
        }
        Button signOutB;
        signOutB=findViewById(R.id.signOutB);
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(mContext,gso);
        signOutB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mAuth.signOut();
                /*
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });*/
                //mGoogleSignInClient.signOut();
                /*
                if(AccessToken.getCurrentAccessToken()!=null){
                    AccessToken accessToken=AccessToken.getCurrentAccessToken();
                    new GraphRequest(accessToken
                            ,"/"+accessToken.getUserId()+"/permissions/"
                            ,null
                            ,HttpMethod.DELETE
                            ,new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse graphResponse) {
                        }
                    }).executeAsync();
                }*/
                //LoginManager.getInstance().logOut();
                //finish();
                //clearUserDetailFromNavbar();
                auxiliaryuseraccountmanager.signOutOGC(mContext, getString(R.string.default_web_client_id),true);
            }
        });
    }

    private void clearUserDetailFromNavbar(){
        LinearLayout navBarUserDetailsLL;
        Button navLoginB,navSignupB,navSignoutB;

        navBarUserDetailsLL=findViewById(R.id.navBarUserDetailsLL);
        navLoginB=findViewById(R.id.navLoginB);
        navSignupB=findViewById(R.id.navSignupB);
        navSignoutB=findViewById(R.id.navSignoutB);
        navBarUserDetailsLL.setVisibility(View.GONE);
        navSignoutB.setVisibility(View.GONE);
        navLoginB.setVisibility(View.VISIBLE);
        navSignupB.setVisibility(View.VISIBLE);
    }

}
