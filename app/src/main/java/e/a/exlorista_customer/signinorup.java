package e.a.exlorista_customer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
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

import org.w3c.dom.Text;

public class signinorup extends AppCompatActivity {
    // TO BE DELETED

    EditText phoneNoET;
    Button phoneLoginB;
    SignInButton signInGoogleSIB;
    LoginButton signInFacebookLB;
    CallbackManager callbackManager;
    //Context mContext;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    private final Context mContext=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinorup);
      //  getSupportActionBar().hide();
        //mContext=this;
        mCallbackManager=CallbackManager.Factory.create();
        auxiliaryuseraccountmanager.LoggedInWith loggedInWith=userSignedIn();

        if(loggedInWith!=null){
            Intent signinorupToPaymentIntent=new Intent(signinorup.this,payment.class);
            startActivity(signinorupToPaymentIntent);
            finish();
        }

        phoneNoET=findViewById(R.id.phoneNoET);
        phoneLoginB=findViewById(R.id.phoneLoginB);
        signInGoogleSIB=findViewById(R.id.signInGoogleSIB);
        signInFacebookLB=findViewById(R.id.signInFacebookLB);
        signInFacebookLB.setReadPermissions("email","public_profile");
        signInFacebookLB.setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);
        signInFacebookLB.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
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

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient=GoogleSignIn.getClient(mContext,gso);
        mAuth=FirebaseAuth.getInstance();
        signInGoogleSIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogle();
            }
        });
        phoneLoginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNoEntered=phoneNoET.getText().toString().trim();
                if(phoneNoEntered.length()==10){
                    Intent signinorupTophoneVerificationIntent=new Intent(signinorup.this,phoneVerification.class);
                    signinorupTophoneVerificationIntent.putExtra(auxiliary.PHONE_NO,"+91"+phoneNoEntered);
                    phoneNoET.setText("");
                    startActivity(signinorupTophoneVerificationIntent);
                    finish();
                } else{
                    phoneNoET.setError("Valid number is required");
                    phoneNoET.requestFocus();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
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

    private auxiliaryuseraccountmanager.LoggedInWith userSignedIn(){
        /* check if user is logged in any way
        either through Google, Facebook, or Phone.
        If user logged in -> return corresponding member of enum LoggedInWith
        else -> return null
        */
        // 1) Check for Firebase (i.e., user is signed in either with Phone or Google)
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            return auxiliaryuseraccountmanager.LoggedInWith.GOOGLE;
        }
        // 2) Check for Facebook (i.e., user is signed in with Facebook)
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null && !accessToken.isExpired()){
            return auxiliaryuseraccountmanager.LoggedInWith.FACEBOOK;
        }
        return null;
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, auxiliaryuseraccountmanager.RC_SIGN_IN);
    }

    private void signOut(auxiliaryuseraccountmanager.LoggedInWith loggedInWith) {
        // Firebase sign out
        mAuth.signOut();

        if(loggedInWith==auxiliaryuseraccountmanager.LoggedInWith.GOOGLE){
            // Google sign out
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
        }
        else if(loggedInWith==auxiliaryuseraccountmanager.LoggedInWith.FACEBOOK){
            // Facebook sign out
            LoginManager.getInstance().logOut();
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
                            //auxiliaryuseraccountmanager.saveUserGoogleSignInClientToSP(mContext,mGoogleSignInClient);
                            Intent signinorupToPaymentIntent=new Intent(signinorup.this,payment.class);
                            startActivity(signinorupToPaymentIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(mContext,"sign in failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent signinorupToPaymentIntent=new Intent(signinorup.this,payment.class);
                            startActivity(signinorupToPaymentIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(mContext,"sign in failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
