package e.a.exlorista_customer;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class phoneVerification extends AppCompatActivity {

    private EditText otpOrPhoneInputET;
    private Button proceedVerifyB, signInWithOtherMethodB;
    private TextView resendOtpTV, otpSentMsgTV, otpNotReceivedMsgTV;

    Bundle extras;

    private Context mContext;
    private String phoneNoReceived;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean mVerificationInProgress=false;
    private String mVerificationId;
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        if(savedInstanceState!=null){
            onRestoreInstanceState(savedInstanceState);
        }
        mContext=this;
        
        otpOrPhoneInputET=findViewById(R.id.otpOrPhoneInputET);
        proceedVerifyB=findViewById(R.id.proceedVerifyB);
        signInWithOtherMethodB=findViewById(R.id.signInWithOtherMethodB);
        resendOtpTV=findViewById(R.id.resendOtpTV);
        otpSentMsgTV=findViewById(R.id.otpSentMsgTV);
        otpNotReceivedMsgTV=findViewById(R.id.otpNotReceivedMsgTV);

        mAuth=FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                mVerificationInProgress = false;

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                //updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                mVerificationInProgress = false;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(mContext,"Invalid phone number",Toast.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(mContext,"Quota exceeded",Toast.LENGTH_LONG).show();
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //Log.d(TAG, "onCodeSent:" + verificationId);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                otpSentMsgTV.setVisibility(View.VISIBLE);
                otpNotReceivedMsgTV.setVisibility(View.VISIBLE);
                resendOtpTV.setVisibility(View.VISIBLE);
            }
        };

        resendOtpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otpOrPhoneInputET.setText("");
                resendVerificationCode(phoneNoReceived,mResendToken);
            }
        });
        signInWithOtherMethodB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(auxiliaryuseraccountmanager.userSignedIn(mContext)!=null){
                    auxiliaryuseraccountmanager.signOut(mContext,getString(R.string.default_web_client_id),false);
                }
                Intent phoneVerificationTologinOrSignupIntent=new Intent(phoneVerification.this,loginOrSignup.class);
                startActivity(phoneVerificationTologinOrSignupIntent);
                finish();
            }
        });

        extras=getIntent().getExtras();
        if(extras.containsKey(auxiliary.PHONE_NO)){
            // This case occurs in login with phone
            // display for OTP
            otpOrPhoneInputET.setHint("Enter OTP");
            phoneNoReceived=extras.getString(auxiliary.PHONE_NO);
            proceedVerifyB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String inputted_otp=otpOrPhoneInputET.getText().toString().trim();
                    if(inputted_otp.isEmpty()){
                        otpOrPhoneInputET.setError("Cannot be empty");
                    } else{
                        verifyPhoneNumberWithCode(mVerificationId,inputted_otp);
                    }
                }
            });
            startPhoneNumberVerification(phoneNoReceived);
        } else{
            // display for Phone
            attachOnclickListenersForloginSignupWithGoogleFacebook();
        }
        //startPhoneNumberVerification(phoneNoReceived);
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,              // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,       // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,              // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,       // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("AUTH","signin success");
                            FirebaseUser user = task.getResult().getUser();
                            if(user!=null){
                                auxiliaryuseraccountmanager.options option=(auxiliaryuseraccountmanager.options)extras
                                        .getSerializable(auxiliary.SELECTED_OPTION);
                                String userPhoneNumber=user.getPhoneNumber(); // Only for Google and Facebook
                                if(userPhoneNumber!=null){
                                    if(!userPhoneNumber.equals("")){
                                        if(userPhoneNumber.startsWith("+91")){
                                            userPhoneNumber=userPhoneNumber.substring(3);
                                        }
                                    }
                                }
                                if(option==auxiliaryuseraccountmanager.options.SIGNUP){
                                    // Signup
                                    if(auxiliaryuseraccountmanager.userExistsInDb(
                                            auxiliary.SERVER_URL+"/userManagement.php"
                                            ,null
                                            ,null
                                            ,userPhoneNumber
                                            ,null)){
                                        // Signup : user with this Phone already exists in database
                                        attachOnclickListenersForloginSignupWithGoogleFacebook();
                                        Toast.makeText(mContext,R.string.phone_already_exists,Toast.LENGTH_LONG)
                                                .show();
                                    } else{
                                        // Signup : user with this Phone does not exist in database
                                        /*
                                        Log.i("AUTH","email : "+user.getEmail());
                                        Log.i("AUTH","display name : "+user.getDisplayName());
                                        Log.i("AUTH","phone : "+user.getPhoneNumber());
                                        Log.i("AUTH","fbuid : "+auxiliaryuseraccountmanager.getFacebookUserId());*/
                                        /*
                                        auxiliaryuseraccountmanager.addUserToDb(auxiliary.SERVER_URL+"/userManagement.php"
                                                ,user.getEmail()
                                                ,user.getDisplayName()
                                                ,userPhoneNumber
                                                ,null);*/
                                        String sql=auxiliaryuseraccountmanager.addUserToDbGetSql(
                                                auxiliary.SERVER_URL+"/userManagement.php"
                                                ,extras.getString(auxiliaryuseraccountmanager.DETAILTYPE_EMAIL)
                                                ,extras.getString(auxiliaryuseraccountmanager.DETAILTYPE_DISPLAYNAME)
                                                ,userPhoneNumber
                                                ,extras.getString(auxiliaryuseraccountmanager.DETAILTYPE_FACEBOOKUSERID));
                                        if(sql!=null){
                                            Log.i("AUTH","Query : "+sql);
                                        } else{
                                            Log.i("AUTH","Query : NULL");
                                        }
                                        if(auxiliaryuseraccountmanager.userExistsInDb(
                                                auxiliary.SERVER_URL+"/userManagement.php"
                                                ,extras.getString(auxiliaryuseraccountmanager.DETAILTYPE_EMAIL)
                                                ,extras.getString(auxiliaryuseraccountmanager.DETAILTYPE_DISPLAYNAME)
                                                ,userPhoneNumber
                                                ,extras.getString(auxiliaryuseraccountmanager.DETAILTYPE_FACEBOOKUSERID))){
                                            // SUCCESS : user has been added to database
                                            Log.i("AUTH","User added to Db");
                                            HashMap<String,String> userDetailFromDb_HM=auxiliaryuseraccountmanager.fetchUserDetailFromDb(
                                                    auxiliary.SERVER_URL+"/userManagement.php"
                                                    ,auxiliary.PPK_PHONE
                                                    ,userPhoneNumber);
                                            auxiliaryuseraccountmanager.addUserDetailsToSP(mContext
                                                    ,userDetailFromDb_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_CUSTID)
                                                    ,userDetailFromDb_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_EMAIL)
                                                    ,userDetailFromDb_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_DISPLAYNAME)
                                                    ,userDetailFromDb_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_PHONENUMBER));
                                            /* This was the previous code (above 2 statements added instead of this)
                                            auxiliaryuseraccountmanager.addUserDetailsToSP(mContext
                                                    ,extras.getString(auxiliaryuseraccountmanager.DETAILTYPE_EMAIL)
                                                    ,extras.getString(auxiliaryuseraccountmanager.DETAILTYPE_DISPLAYNAME)
                                                    ,userPhoneNumber);*/
                                            auxiliaryuseraccountmanager.setSignInStatus(mContext,auxiliaryuseraccountmanager.VALID_SIGNIN);
                                            proceedToPaymentActivity();
                                        } else{
                                            // FAILED : user cannot be added to database
                                            Log.i("AUTH","User cannot be added");
                                            auxiliaryuseraccountmanager.setSignInStatus(mContext,auxiliaryuseraccountmanager.INVALID_SIGNIN);
                                            attachOnclickListenersForloginSignupWithGoogleFacebook();
                                            Toast.makeText(mContext,R.string.failed_user_addition_to_db,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } else if(option==auxiliaryuseraccountmanager.options.LOGIN){
                                    // Login
                                    if(extras.containsKey(auxiliary.PHONE_NO)){
                                        // Login : Phone
                                        Log.i("AUTH","User verified via phone");
                                        phoneNoReceived=phoneNoReceived.substring(3);
                                        HashMap<String,String> userDetailFromDb_HM=auxiliaryuseraccountmanager.fetchUserDetailFromDb(
                                                auxiliary.SERVER_URL+"/userManagement.php"
                                                ,auxiliary.PPK_PHONE
                                                ,phoneNoReceived);
                                        auxiliaryuseraccountmanager.addUserDetailsToSP(mContext
                                                ,userDetailFromDb_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_CUSTID)
                                                ,userDetailFromDb_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_EMAIL)
                                                ,userDetailFromDb_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_DISPLAYNAME)
                                                ,userDetailFromDb_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_PHONENUMBER));
                                        auxiliaryuseraccountmanager.setSignInStatus(mContext,auxiliaryuseraccountmanager.VALID_SIGNIN);
                                        proceedToPaymentActivity();
                                    } else{
                                        // Login : Google or Facebook
                                        if(auxiliaryuseraccountmanager.userExistsInDb(
                                                auxiliary.SERVER_URL+"/userManagement.php"
                                                ,null
                                                ,null
                                                ,userPhoneNumber
                                                ,null)){
                                            // Login flow is converted to Sign up flow because
                                            // user with verified email does not exist in database
                                            // Signup : some other user with this Phone already exists in database
                                            attachOnclickListenersForloginSignupWithGoogleFacebook();
                                            Toast.makeText(mContext,R.string.phone_already_exists,Toast.LENGTH_LONG).show();
                                        } else{
                                            String sql=auxiliaryuseraccountmanager.addUserToDbGetSql(
                                                    auxiliary.SERVER_URL+"/userManagement.php"
                                                    ,extras.getString(auxiliaryuseraccountmanager.DETAILTYPE_EMAIL)
                                                    ,extras.getString(auxiliaryuseraccountmanager.DETAILTYPE_DISPLAYNAME)
                                                    ,userPhoneNumber
                                                    ,extras.getString(auxiliaryuseraccountmanager.DETAILTYPE_FACEBOOKUSERID));
                                            if(sql!=null){
                                                Log.i("AUTH","Query : "+sql);
                                            } else{
                                                Log.i("AUTH","Query : NULL");
                                            }
                                            if(auxiliaryuseraccountmanager.userExistsInDb(
                                                    auxiliary.SERVER_URL+"/userManagement.php"
                                                    ,extras.getString(auxiliaryuseraccountmanager.DETAILTYPE_EMAIL)
                                                    ,extras.getString(auxiliaryuseraccountmanager.DETAILTYPE_DISPLAYNAME)
                                                    ,userPhoneNumber
                                                    ,extras.getString(auxiliaryuseraccountmanager.DETAILTYPE_FACEBOOKUSERID))){
                                                // SUCCESS : user has been added to database
                                                Log.i("AUTH","User added to Db");
                                                HashMap<String,String> userDetailFromDb_HM=auxiliaryuseraccountmanager.fetchUserDetailFromDb(
                                                        auxiliary.SERVER_URL+"/userManagement.php"
                                                        ,auxiliary.PPK_PHONE
                                                        ,userPhoneNumber);
                                                auxiliaryuseraccountmanager.addUserDetailsToSP(mContext
                                                        ,userDetailFromDb_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_CUSTID)
                                                        ,userDetailFromDb_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_EMAIL)
                                                        ,userDetailFromDb_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_DISPLAYNAME)
                                                        ,userDetailFromDb_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_PHONENUMBER));
                                                auxiliaryuseraccountmanager.setSignInStatus(mContext,auxiliaryuseraccountmanager.VALID_SIGNIN);
                                                proceedToPaymentActivity();
                                            } else{
                                                // FAILED : user cannot be added to database
                                                Log.i("AUTH","User cannot be added");
                                                auxiliaryuseraccountmanager.setSignInStatus(mContext,auxiliaryuseraccountmanager.INVALID_SIGNIN);
                                                attachOnclickListenersForloginSignupWithGoogleFacebook();
                                                Toast.makeText(mContext,R.string.failed_user_addition_to_db,Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                }
                            } else{
                                Log.i("AUTH","user is null");
                            }
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                // [END_EXCLUDE]
                            }
                        }
                    }
                });
    }

    private void attachOnclickListenersForloginSignupWithGoogleFacebook() {
        otpOrPhoneInputET.setHint("Enter 10 digit Phone No");
        proceedVerifyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputted_Phone=otpOrPhoneInputET.getText().toString().trim();
                if(inputted_Phone.length()!=10){
                    otpOrPhoneInputET.setError("Enter valid Phone No");
                } else{
                    // display for OTP
                    phoneNoReceived="+91"+otpOrPhoneInputET.getText().toString().trim();
                    otpOrPhoneInputET.setText("");
                    otpOrPhoneInputET.setHint("Enter OTP");
                    Log.i("AUTH","Phone no : "+phoneNoReceived);
                    proceedVerifyB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String inputted_Otp=otpOrPhoneInputET.getText().toString().trim();
                            if(inputted_Otp.isEmpty()){
                                otpOrPhoneInputET.setError("Cannot be empty");
                            } else{
                                verifyPhoneNumberWithCode(mVerificationId,inputted_Otp);
                            }
                        }
                    });
                    startPhoneNumberVerification(phoneNoReceived);
                }
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    private void proceedToPaymentActivity() {
        // Implementation imcomplete: check if user is present in database or not
        Intent phoneVerificationToPaymentIntent = new Intent(phoneVerification.this, payment.class);
        startActivity(phoneVerificationToPaymentIntent);
        finish(); // so that user cannot return here by pressing back
    }

    @Override
    public void onBackPressed() {
    }
}