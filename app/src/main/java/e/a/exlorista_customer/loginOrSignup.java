package e.a.exlorista_customer;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class loginOrSignup extends AppCompatActivity {

    SignInButton signInGoogleNewwSIB;
    LoginButton signInFacebookNewwLB;
    TextView loginTV,signupTV,orNewwTV;
    EditText phoneNoNewwET;
    LinearLayout continueWithGoogleLL, continueWithFacebookLL, phoneLoginNewwLL;
    Button phoneLoginNewwB;

    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager mCallbackManager;

    private final Context mContext=this;
    //private enum options{LOGIN,SIGNUP};
    private auxiliaryuseraccountmanager.options selectedOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_signup);
        //getSupportActionBar().hide();

        auxiliaryuseraccountmanager.LoggedInWith loggedInWith=auxiliaryuseraccountmanager.userSignedIn(mContext);
        String signInStatus=auxiliaryuseraccountmanager.getSignInStatus(mContext);
        if(signInStatus!=null){
            if(loggedInWith!=null){
                switch (signInStatus){
                    case auxiliaryuseraccountmanager.INVALID_SIGNIN:
                        auxiliaryuseraccountmanager.signOutOGC(mContext,getString(R.string.default_web_client_id),false);
                        break;
                    case auxiliaryuseraccountmanager.VALID_SIGNIN:
                        proceedToPaymentActivity();
                        break;
                }
            } else{
                auxiliaryuseraccountmanager.signOutOGC(mContext,getString(R.string.default_web_client_id),false);
            }
            /*
            if(loggedInWith!=null && signInStatus.equals(auxiliaryuseraccountmanager.VALID_SIGNIN)){
                Intent loginOrSignupToPaymentIntent=new Intent(loginOrSignup.this,payment.class);
                startActivity(loginOrSignupToPaymentIntent);
                finish();
            }*/
        } else{
            Log.i("AUTH","signInStatus was null. User was logged out.");
            auxiliaryuseraccountmanager.signOutOGC(mContext,getString(R.string.default_web_client_id),false);
        }

        loginTV=findViewById(R.id.loginTV);
        signupTV=findViewById(R.id.signupTV);
        orNewwTV=findViewById(R.id.orNewwTV);
        phoneNoNewwET=findViewById(R.id.phoneNoNewwET);
        continueWithGoogleLL=findViewById(R.id.continueWithGoogleLL);
        continueWithFacebookLL=findViewById(R.id.continueWithFacebookLL);
        phoneLoginNewwLL=findViewById(R.id.phoneLoginNewwLL);
        phoneLoginNewwB=findViewById(R.id.phoneLoginNewwB);
        signInGoogleNewwSIB=findViewById(R.id.signInGoogleNewwSIB);
        signInFacebookNewwLB=findViewById(R.id.signInFacebookNewwLB);

        selectedOption=auxiliaryuseraccountmanager.options.LOGIN;
        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedOption=auxiliaryuseraccountmanager.options.LOGIN;
                loginTV.setBackgroundColor(getResources().getColor(R.color.colorActionBarBackground));
                loginTV.setTextColor(getResources().getColor(R.color.colorActionBarTitleText));
                loginTV.setTextSize(18);
                signupTV.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                signupTV.setTextColor(getResources().getColor(R.color.colorProductBrand));
                signupTV.setTextSize(14);
                orNewwTV.setVisibility(View.VISIBLE);
                phoneLoginNewwLL.setVisibility(View.VISIBLE);
            }
        });
        signupTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedOption=auxiliaryuseraccountmanager.options.SIGNUP;
                signupTV.setBackgroundColor(getResources().getColor(R.color.colorActionBarBackground));
                signupTV.setTextColor(getResources().getColor(R.color.colorActionBarTitleText));
                signupTV.setTextSize(18);
                loginTV.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                loginTV.setTextColor(getResources().getColor(R.color.colorProductBrand));
                loginTV.setTextSize(14);
                orNewwTV.setVisibility(View.GONE);
                phoneLoginNewwLL.setVisibility(View.GONE);
            }
        });

        try{
            Bundle extras=getIntent().getExtras();
            if(extras!=null){
                if(extras.containsKey(auxiliary.NAVBUTTON_CLICKED)){
                    String navButton_clicked=extras.getString(auxiliary.NAVBUTTON_CLICKED);
                    switch (navButton_clicked){
                        case auxiliary.NAV_LOGINB:
                            loginTV.performClick();
                            break;
                        case auxiliary.NAV_SIGNUPB:
                            signupTV.performClick();
                            break;
                    }
                }
            }
        } catch (NullPointerException ignored){
        } catch (Exception ignored){
        }

        mAuth=FirebaseAuth.getInstance();
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient=GoogleSignIn.getClient(mContext,gso);
        signInGoogleNewwSIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogle();
            }
        });
        continueWithGoogleLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //signInGoogleNewwSIB.performClick(); //This somehow does not work
                signInGoogle();
            }
        });

        mCallbackManager=CallbackManager.Factory.create();
        signInFacebookNewwLB.setReadPermissions("email","public_profile");
        signInFacebookNewwLB.setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);
        signInFacebookNewwLB.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // change ui
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(mContext,"onError",Toast.LENGTH_LONG).show();
                // change ui
            }
        });
        continueWithFacebookLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInFacebookNewwLB.performClick();
            }
        });
        phoneLoginNewwB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNoEntered=phoneNoNewwET.getText().toString().trim();
                if(phoneNoEntered.length()==10){
                    if(auxiliaryuseraccountmanager.userExistsInDb(auxiliary.SERVER_URL+"/userManagement.php"
                            ,null
                            ,null
                            ,phoneNoEntered
                            ,null)){
                        Intent loginOrSignupToPhoneVerificationIntent=new Intent(loginOrSignup.this,phoneVerification.class);
                        loginOrSignupToPhoneVerificationIntent.putExtra(auxiliary.PHONE_NO,"+91"+phoneNoEntered);
                        loginOrSignupToPhoneVerificationIntent.putExtra(auxiliary.SELECTED_OPTION, auxiliaryuseraccountmanager.options.LOGIN);
                        phoneNoNewwET.setText("");
                        startActivity(loginOrSignupToPhoneVerificationIntent);
                        finish();
                    } else{
                        Toast.makeText(mContext,R.string.phone_does_not_exist,Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, auxiliaryuseraccountmanager.RC_SIGN_IN);
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("FB uid",token.getUserId());
                            // Sign in success, update UI with the signed-in user's information
                            takeRelevantAction(auxiliaryuseraccountmanager.LoggedInWith.FACEBOOK);
                            //proceedToPaymentActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(mContext,"sign in failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // For Facebook
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // For Google
        if (requestCode == auxiliaryuseraccountmanager.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException apie) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(mContext,"Sign in failed (ApiException)",Toast.LENGTH_LONG).show();
            } catch (NullPointerException npe){
                // Google Sign In failed, due to account being null
                Toast.makeText(mContext,"Sign in failed (NullPointerException)",Toast.LENGTH_LONG).show();
            } catch (Exception e){
                // Google Sign In failed, due to some unknown error
                Toast.makeText(mContext,"Sign in failed (Exception)",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            // requestCode != RC_SIGN_IN i.e., 9001
            Toast.makeText(mContext,"request code not 9001",Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            takeRelevantAction(auxiliaryuseraccountmanager.LoggedInWith.GOOGLE);
                            //proceedToPaymentActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(mContext,"sign in failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void takeRelevantAction(auxiliaryuseraccountmanager.LoggedInWith continueWith){
        FirebaseUser user=mAuth.getCurrentUser();
        /* Because phone no cannot be fetched at this moment and will be null
        String userPhoneNumber=user.getPhoneNumber();
        if(userPhoneNumber!=null){
            if(!userPhoneNumber.equals("")){
                if(userPhoneNumber.startsWith("+91")){
                    userPhoneNumber=userPhoneNumber.substring(3);
                }
            }
        }*/
        switch (selectedOption){
            case LOGIN:
                // Remaining : Adding user details (email, displayName, phone) to shared prefs
                //             if user exists in db
                switch (continueWith){
                    case PHONE:
                        if(auxiliaryuseraccountmanager.userExistsInDb(auxiliary.SERVER_URL+"/userManagement.php"
                                ,null
                                ,null
                                ,null/*userPhoneNumber*/
                                ,null)){
                        }
                    case GOOGLE:
                        if(auxiliaryuseraccountmanager.userExistsInDb(auxiliary.SERVER_URL+"/userManagement.php"
                                ,user.getEmail()
                                ,user.getDisplayName()
                                ,null/*userPhoneNumber*/
                                ,null)){
                            // INCOMPLETE : get user's phone number' from database by user's email
                            // as here userPhoneNumber will be null (Google login)
                            //Log.i("AUTH","Inside if");
                            HashMap<String,String> userDetailInDb=auxiliaryuseraccountmanager.fetchUserDetailFromDb(
                                    auxiliary.SERVER_URL+"/userManagement.php"
                                    ,auxiliary.PPK_EMAIL
                                    ,user.getEmail());
                            auxiliaryuseraccountmanager.addUserDetailsToSP(mContext,
                                    userDetailInDb.get(auxiliaryuseraccountmanager.DETAILTYPE_CUSTID),
                                    userDetailInDb.get(auxiliaryuseraccountmanager.DETAILTYPE_EMAIL),
                                    userDetailInDb.get(auxiliaryuseraccountmanager.DETAILTYPE_DISPLAYNAME),
                                    userDetailInDb.get(auxiliaryuseraccountmanager.DETAILTYPE_PHONENUMBER));
                            //Log.i("AUTH","fetched phone : "+((userPhoneNumber==null||userPhoneNumber.equals(""))?"":userPhoneNumber));
                            auxiliaryuseraccountmanager.setSignInStatus(mContext,auxiliaryuseraccountmanager.VALID_SIGNIN);
                            proceedToPaymentActivity();
                        } else{
                            //Log.i("AUTH","Inside else");
                            proceedToPhoneVerificationActivity(auxiliaryuseraccountmanager.options.SIGNUP/*because user does not exist
                            in database and will have to signup*/
                                    ,user.getEmail()
                                    ,user.getDisplayName()
                                    ,null/*userPhoneNumber*/
                                    ,null);
                        }
                        break;
                    case FACEBOOK:
                        if(auxiliaryuseraccountmanager.userExistsInDb(auxiliary.SERVER_URL+"/userManagement.php"
                                ,user.getEmail()
                                ,user.getDisplayName()
                                ,null/*userPhoneNumber*/
                                ,auxiliaryuseraccountmanager.getFacebookUserId())){
                            HashMap<String,String> userDetailInDb=auxiliaryuseraccountmanager.fetchUserDetailFromDb(
                                    auxiliary.SERVER_URL+"/userManagement.php"
                                    ,auxiliary.PPK_FACEBOOKUSERID
                                    ,auxiliaryuseraccountmanager.getFacebookUserId());
                            auxiliaryuseraccountmanager.addUserDetailsToSP(mContext,
                                    userDetailInDb.get(auxiliaryuseraccountmanager.DETAILTYPE_CUSTID),
                                    userDetailInDb.get(auxiliaryuseraccountmanager.DETAILTYPE_EMAIL),
                                    userDetailInDb.get(auxiliaryuseraccountmanager.DETAILTYPE_DISPLAYNAME),
                                    userDetailInDb.get(auxiliaryuseraccountmanager.DETAILTYPE_PHONENUMBER));
                            auxiliaryuseraccountmanager.setSignInStatus(mContext,auxiliaryuseraccountmanager.VALID_SIGNIN);
                            proceedToPaymentActivity();
                        } else{
                            proceedToPhoneVerificationActivity(auxiliaryuseraccountmanager.options.SIGNUP/*because user does not exist
                            in database and will have to signup*/
                                    ,user.getEmail()
                                    ,user.getDisplayName()
                                    ,null/*userPhoneNumber*/
                                    ,auxiliaryuseraccountmanager.getFacebookUserId());
                        }
                        break;
                }
                break;
            case SIGNUP:
                switch (continueWith){
                    case GOOGLE:
                        if(auxiliaryuseraccountmanager.userExistsInDb(auxiliary.SERVER_URL+"/userManagement.php"
                                ,user.getEmail()
                                ,user.getDisplayName()
                                ,null/*userPhoneNumber*/
                                ,null)){
                            auxiliaryuseraccountmanager.signOutOGC(mContext,getString(R.string.default_web_client_id),false);
                            Toast.makeText(mContext,getString(R.string.account_already_exists),Toast.LENGTH_LONG).show();
                        } else{
                            proceedToPhoneVerificationActivity(auxiliaryuseraccountmanager.options.SIGNUP
                                    ,user.getEmail()
                                    ,user.getDisplayName()
                                    ,null/*userPhoneNumber*/
                                    ,null);
                        }
                        break;
                    case FACEBOOK:
                        if(auxiliaryuseraccountmanager.userExistsInDb(auxiliary.SERVER_URL+"/userManagement.php"
                                ,user.getEmail()
                                ,user.getDisplayName()
                                ,null/*userPhoneNumber*/
                                ,Profile.getCurrentProfile().getId())){
                            auxiliaryuseraccountmanager.signOutOGC(mContext,getString(R.string.default_web_client_id),false);
                            Toast.makeText(mContext,getString(R.string.account_already_exists),Toast.LENGTH_LONG).show();
                        } else{
                            proceedToPhoneVerificationActivity(auxiliaryuseraccountmanager.options.SIGNUP
                                    ,user.getEmail()
                                    ,user.getDisplayName()
                                    ,null/*userPhoneNumber*/
                                    ,auxiliaryuseraccountmanager.getFacebookUserId());
                        }
                        break;
                }
                break;
        }
    }

    private void proceedToPaymentActivity(){
        Intent loginOrSignupToPaymentIntent=new Intent(loginOrSignup.this,payment.class);
        startActivity(loginOrSignupToPaymentIntent);
        finish();
    }

    private void proceedToPhoneVerificationActivity(auxiliaryuseraccountmanager.options option,
                                                    String email,
                                                    String displayName,
                                                    String phone,
                                                    String facebookUserId){
        Intent loginOrSignupToPhoneVerificationIntent=new Intent(loginOrSignup.this,phoneVerification.class);
        loginOrSignupToPhoneVerificationIntent.putExtra(auxiliary.SELECTED_OPTION,option);
        loginOrSignupToPhoneVerificationIntent.putExtra(auxiliaryuseraccountmanager.DETAILTYPE_EMAIL,email);
        loginOrSignupToPhoneVerificationIntent.putExtra(auxiliaryuseraccountmanager.DETAILTYPE_DISPLAYNAME,displayName);
        loginOrSignupToPhoneVerificationIntent.putExtra(auxiliaryuseraccountmanager.DETAILTYPE_PHONENUMBER,phone);
        loginOrSignupToPhoneVerificationIntent.putExtra(auxiliaryuseraccountmanager.DETAILTYPE_FACEBOOKUSERID,facebookUserId);
        startActivity(loginOrSignupToPhoneVerificationIntent);
        finish();
    }

}
