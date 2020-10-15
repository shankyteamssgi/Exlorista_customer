package e.a.exlorista_customer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

class auxiliaryuseraccountmanager {

    private static final String SPFILE_USERACCOUNTDATA="spfile_userAccountData";
    private static final String SPKEY_CUSTID="spkey_custid";
    private static final String SPKEY_EMAIL="spkey_email";
    private static final String SPKEY_DISPLAYNAME="spkey_displayName";
    private static final String SPKEY_PHONE="spkey_phone";
    private static final String SPKEY_FACEBOOKUSERID="spkey_facebookUserId";

    private static final String SPKEY_USERDATAVALIDITYFLAG="spkey_userDataValidityFlag";
    static final String USERDATA_INVALID="0";
    static final String USERDATA_VALID="1";

    static final int RC_SIGN_IN=9001;
    enum LoggedInWith{PHONE,GOOGLE,FACEBOOK};
    enum options{LOGIN,SIGNUP};

    static final String DETAILTYPE_CUSTID="detailtype_custid";
    static final String DETAILTYPE_EMAIL="detailtype_email";
    static final String DETAILTYPE_DISPLAYNAME="detailtype_displayName";
    static final String DETAILTYPE_PHONENUMBER="detailtype_phoneNumber";
    static final String DETAILTYPE_FACEBOOKUSERID="detailtype_facebookUserId";
    static final String DETAILTYPE_USERDATAVALIDITYFLAG="detailtype_userDataValidityFlag";

    private static final String SPFILE_SIGNINSTATUS="spfile_signinstatus";
    private static final String SPKEY_SIGNINSTATUS="spkey_signinstatus";

    static final String VALID_SIGNIN="1";
    static final String INVALID_SIGNIN="-1";

    // Database column names
    static final String CUST_ID="cust_id";
    static final String CUST_EMAIL="cust_email";
    static final String CUST_PHONE="cust_phone";
    static final String CUST_DISPLAYNAME="cust_displayName";

    static void addUserDetailsToSP(Context context,
                                   String custId,
                                   String email,
                                   String displayName,
                                   String phone){
        SharedPreferences userDetailSP=context.getSharedPreferences(SPFILE_USERACCOUNTDATA,Context.MODE_PRIVATE);
        userDetailSP.edit()
                .putString(SPKEY_CUSTID,custId)
                .putString(SPKEY_EMAIL,email)
                .putString(SPKEY_DISPLAYNAME,displayName)
                .putString(SPKEY_PHONE,phone)
                .putString(SPKEY_USERDATAVALIDITYFLAG,USERDATA_VALID)
                .apply();
    }

    static HashMap<String,String> getUserDetailsFromSP(Context context){
        SharedPreferences userDetailSP=context.getSharedPreferences(SPFILE_USERACCOUNTDATA,Context.MODE_PRIVATE);
        HashMap<String,String> userDetails_HM=new HashMap<String,String>();
        userDetails_HM.put(auxiliaryuseraccountmanager.DETAILTYPE_CUSTID,userDetailSP.getString(SPKEY_CUSTID,null));
        userDetails_HM.put(auxiliaryuseraccountmanager.DETAILTYPE_EMAIL,userDetailSP.getString(SPKEY_EMAIL,null));
        userDetails_HM.put(auxiliaryuseraccountmanager.DETAILTYPE_DISPLAYNAME,userDetailSP.getString(SPKEY_DISPLAYNAME,null));
        userDetails_HM.put(auxiliaryuseraccountmanager.DETAILTYPE_PHONENUMBER,userDetailSP.getString(SPKEY_PHONE,null));
        //userDetails_HM.put(auxiliaryuseraccountmanager.DETAILTYPE_FACEBOOKUSERID,userDetailSP.getString(SPKEY_FACEBOOKUSERID,null));
        userDetails_HM.put(auxiliaryuseraccountmanager.DETAILTYPE_USERDATAVALIDITYFLAG
                ,userDetailSP.getString(SPKEY_USERDATAVALIDITYFLAG,null));
        return userDetails_HM;
    }

    private static void clearUserDetailFromSP(Context context){
        SharedPreferences userDetailSP=context.getSharedPreferences(SPFILE_USERACCOUNTDATA,Context.MODE_PRIVATE);
        userDetailSP.edit()
                .putString(SPKEY_EMAIL,null)
                .putString(SPKEY_DISPLAYNAME,null)
                .putString(SPKEY_PHONE,null)
                .putString(SPKEY_FACEBOOKUSERID,null)
                .putString(SPKEY_USERDATAVALIDITYFLAG,USERDATA_INVALID)
                .apply();
    }

    static auxiliaryuseraccountmanager.LoggedInWith userSignedIn(Context context){
        /* check if user is logged in any way
        either through Google, Facebook, or Phone.
        If user logged in -> return corresponding member of enum LoggedInWith
        else -> return null
        */
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            // 1) Check for Facebook (i.e., user is signed in with Facebook)
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if(accessToken != null && !accessToken.isExpired()){
                Log.i("SIGNED IN: ","Facebook");
                return auxiliaryuseraccountmanager.LoggedInWith.FACEBOOK;
            }
            // 2) Check for Google (i.e., user is signed in with Google)
            if(GoogleSignIn.getLastSignedInAccount(context)!=null){
                Log.i("SIGNED IN: ","Google");
                return auxiliaryuseraccountmanager.LoggedInWith.GOOGLE;
            }
            // Otherwise logged in with Phone
            Log.i("SIGNED IN: ","Phone");
            return auxiliaryuseraccountmanager.LoggedInWith.PHONE;
        }
        return null;
    }

    static void signOut(Context context, String default_web_client_id, boolean finish_flag){
        auxiliaryuseraccountmanager.setSignInStatus(context,auxiliaryuseraccountmanager.INVALID_SIGNIN);
        try{
            FirebaseAuth mAuth=FirebaseAuth.getInstance();
            FirebaseUser user=mAuth.getCurrentUser();
            GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(default_web_client_id)
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient=GoogleSignIn.getClient(context,gso);
            mAuth.signOut();
        /*
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
            }
        });*/
            mGoogleSignInClient.signOut();
        /*
        if(AccessToken.getCurrentAccessToken()!=null){
            AccessToken accessToken=AccessToken.getCurrentAccessToken();
            new GraphRequest(accessToken
                    ,"/"+accessToken.getUserId()+"/permissions/"
                    ,null
                    , HttpMethod.DELETE
                    ,new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                }
            }).executeAsync();
        }*/
            LoginManager.getInstance().logOut();
        } catch (Exception ignored){
        }
        if(finish_flag){
            ((Activity)context).finish();
        }
    }

    static void signOutOGC(Context context, String default_web_client_id, boolean finish_flag){
        auxiliaryuseraccountmanager.setSignInStatus(context,auxiliaryuseraccountmanager.INVALID_SIGNIN);
        auxiliaryuseraccountmanager.clearUserDetailFromSP(context);
        try{
            FirebaseAuth.getInstance().signOut(); // Firebase
            GoogleSignIn.getClient(context
                    ,new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(default_web_client_id)
                            .requestEmail()
                            .build()).signOut(); // Google
            LoginManager.getInstance().logOut(); // Facebook
        } catch (Exception ignored){
        }
        if(finish_flag){
            ((Activity)context).finish();
        }
    }

    static String[] getUserDetails(Context context, String detail_type){
        if(auxiliaryuseraccountmanager.userSignedIn(context)!=null){
            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
            return new String[] {user.getEmail(),user.getPhoneNumber(),user.getDisplayName()};
        }
        return null;
    }

    static String getSignInStatus(Context context){
        SharedPreferences signInStatusSP=context
                .getSharedPreferences(auxiliaryuseraccountmanager.SPFILE_SIGNINSTATUS,Context.MODE_PRIVATE);
        return signInStatusSP.getString(auxiliaryuseraccountmanager.SPKEY_SIGNINSTATUS,null);
    }

    static void setSignInStatus(Context context, String signin_status){
        // signin_status
        // INVALID_SIGNIN (0) : invalid sign in
        // VALID_SIGNIN (1) : valid sign in
        if(!signin_status.equals(auxiliaryuseraccountmanager.VALID_SIGNIN)
                && !signin_status.equals(auxiliaryuseraccountmanager.INVALID_SIGNIN)){
            signin_status=auxiliaryuseraccountmanager.INVALID_SIGNIN;
        }
        SharedPreferences signInStatusSP=context
                .getSharedPreferences(auxiliaryuseraccountmanager.SPFILE_SIGNINSTATUS,Context.MODE_PRIVATE);
        signInStatusSP.edit()
                .putString(auxiliaryuseraccountmanager.SPKEY_SIGNINSTATUS,signin_status)
                .apply();
    }

    static String getFacebookUserId(){
        try{
            return Profile.getCurrentProfile().getId();
        } catch (NullPointerException npe){
            Log.i("AUTH","NPE occurred in getFacebookUserId");
            //npe.printStackTrace();
        } catch (Exception e){
            Log.i("AUTH","Exception occurred in getFacebookUserId");
            //e.printStackTrace();
        }
        return null;
    }

    static boolean userExistsInDb(final String urlWebService,
                                   final String email,
                                   final String displayName,
                                   final String phone,
                                   final String facebookUserId){
        // utility in both login & signup
        class UserAlreadyExists extends AsyncTask<Void, Void, Void> {
            private boolean userExists;

            private UserAlreadyExists() {
                this.userExists=false;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>() {
                        {
                            put(auxiliary.PPK_INITIAL_CHECK,auxiliary.PPV_INITIAL_CHECK);
                            put(auxiliary.PPK_REQUESTTYPE,auxiliary.PPV_REQUESTTYPE_USEREXISTENCECHECK);
                            put(auxiliary.PPK_EMAIL, email==null?"":email);
                            put(auxiliary.PPK_DISPLAYNAME, displayName==null?"":displayName);
                            put(auxiliary.PPK_PHONE, phone==null?"":phone);
                            put(auxiliary.PPK_FACEBOOKUSERID, facebookUserId==null?"":facebookUserId);
                            //Log.i("AUTH","email : "+email);
                            //Log.i("AUTH","displayName : "+displayName);
                            //Log.i("AUTH","phone : "+phone);
                            //Log.i("AUTH","facebookUserId : "+facebookUserId);
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
                    if (!sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_FAIL) &&
                            !sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_NOT_SET)){
                        Log.i("AUTH","PPK pass");
                        Log.i("AUTH","JSON : "+sb.toString().trim());
                        JSONArray jsonArray=new JSONArray(sb.toString().trim());
                        JSONObject obj=(jsonArray).getJSONObject(0);
                        this.userExists=obj.getString("COUNT(*)").equals("1");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            private boolean getResult(){
                return this.userExists;
            }
        }
        UserAlreadyExists userAlreadyExists=new UserAlreadyExists();
        try {
            userAlreadyExists.execute().get();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (ExecutionException ee) {
            ee.printStackTrace();
        }
        return userAlreadyExists.getResult();
    }

    static String addUserToDbGetSql(final String urlWebService,
                            final String email,
                            final String displayName,
                            final String phone,
                            final String facebookUserId){
        // utility in both login & signup
        class AddUserToDbGetSql extends AsyncTask<Void,Void,Void>{

            private String sql;

            private AddUserToDbGetSql() {
                this.sql = null;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>() {
                        {
                            put(auxiliary.PPK_INITIAL_CHECK,auxiliary.PPV_INITIAL_CHECK);
                            put(auxiliary.PPK_REQUESTTYPE,auxiliary.PPV_REQUESTTYPE_USERADDITION);
                            put(auxiliary.PPK_EMAIL, email==null?"":email);
                            put(auxiliary.PPK_DISPLAYNAME, displayName==null?"":displayName);
                            put(auxiliary.PPK_PHONE, phone==null?"":phone);
                            put(auxiliary.PPK_FACEBOOKUSERID, facebookUserId==null?"":facebookUserId);
                            //Log.i("AUTH","email : "+email);
                            //Log.i("AUTH","displayName : "+displayName);
                            //Log.i("AUTH","phone : "+phone);
                            //Log.i("AUTH","facebookUserId : "+facebookUserId);
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
                    this.sql=sb.toString().trim();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            private String getSql(){
                return this.sql;
            }
        }
        AddUserToDbGetSql addUserToDbGetSql=new AddUserToDbGetSql();
        try {
            addUserToDbGetSql.execute().get();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (ExecutionException ee) {
            ee.printStackTrace();
        }
        return addUserToDbGetSql.getSql();
    }

    static void addUserToDb(final String urlWebService,
                             final String email,
                             final String displayName,
                             final String phone,
                             final String facebookUserId){
        class AddUserToDb extends AsyncTask<Void,Void,Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>() {
                        {
                            put(auxiliary.PPK_INITIAL_CHECK,auxiliary.PPV_INITIAL_CHECK);
                            put(auxiliary.PPK_REQUESTTYPE,auxiliary.PPV_REQUESTTYPE_USERADDITION);
                            put(auxiliary.PPK_EMAIL, email==null?"":email);
                            put(auxiliary.PPK_DISPLAYNAME, displayName==null?"":displayName);
                            put(auxiliary.PPK_PHONE, phone==null?"":phone);
                            put(auxiliary.PPK_FACEBOOKUSERID, facebookUserId==null?"":facebookUserId);
                            //Log.i("AUTH","email : "+email);
                            //Log.i("AUTH","displayName : "+displayName);
                            //Log.i("AUTH","phone : "+phone);
                            //Log.i("AUTH","facebookUserId : "+facebookUserId);
                        }
                    }));
                    dos.flush();
                    dos.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        }
        AddUserToDb addUserToDb=new AddUserToDb();
        try {
            addUserToDb.execute().get();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (ExecutionException ee) {
            ee.printStackTrace();
        }
    }

    static HashMap<String,String> fetchUserDetailFromDb(final String urlWebService, final String detail_type, final String detail){
        // utility in Login
        class FetchUserDetailFromDb extends AsyncTask<Void,Void,Void>{

            private HashMap<String,String> userDetailFromDb;

            private FetchUserDetailFromDb() {
                this.userDetailFromDb=new HashMap<String,String>();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>() {
                        {
                            put(auxiliary.PPK_INITIAL_CHECK,auxiliary.PPV_INITIAL_CHECK);
                            put(auxiliary.PPK_REQUESTTYPE,auxiliary.PPV_REQUESTTYPE_USERDETAILFETCH);
                            put(detail_type,detail);
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
                    if (!sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_FAIL) &&
                            !sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_NOT_SET)){
                        JSONArray jsonArray=new JSONArray(sb.toString().trim());
                        JSONObject jsonObject=(jsonArray).getJSONObject(0);
                        if(jsonObject.has(auxiliaryuseraccountmanager.CUST_ID)){
                            String cust_id=jsonObject.getString(auxiliaryuseraccountmanager.CUST_ID);
                            if(!cust_id.equalsIgnoreCase("null")){
                                userDetailFromDb.put(auxiliaryuseraccountmanager.DETAILTYPE_CUSTID
                                        ,cust_id);
                            }
                        }
                        if(jsonObject.has(auxiliaryuseraccountmanager.CUST_EMAIL)){
                            String cust_email=jsonObject.getString(auxiliaryuseraccountmanager.CUST_EMAIL);
                            if(!cust_email.equalsIgnoreCase("null")){
                                userDetailFromDb.put(auxiliaryuseraccountmanager.DETAILTYPE_EMAIL
                                        ,cust_email);
                            }
                        }
                        if(jsonObject.has(auxiliaryuseraccountmanager.CUST_PHONE)){
                            String cust_phone=jsonObject.getString(auxiliaryuseraccountmanager.CUST_PHONE);
                            if(!cust_phone.equalsIgnoreCase("null")){
                                userDetailFromDb.put(auxiliaryuseraccountmanager.DETAILTYPE_PHONENUMBER
                                        ,cust_phone);
                            }
                        }
                        if(jsonObject.has(auxiliaryuseraccountmanager.CUST_DISPLAYNAME)){
                            String cust_displayName=jsonObject.getString(auxiliaryuseraccountmanager.CUST_DISPLAYNAME);
                            if(!cust_displayName.equalsIgnoreCase("null")){
                                userDetailFromDb.put(auxiliaryuseraccountmanager.DETAILTYPE_DISPLAYNAME
                                        ,cust_displayName);
                            }
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            private HashMap<String,String> getUserDetailFromDb_HM(){
                return this.userDetailFromDb;
            }
        }

        FetchUserDetailFromDb fetchUserDetailFromDb=new FetchUserDetailFromDb();
        try {
            fetchUserDetailFromDb.execute().get();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (ExecutionException ee) {
            ee.printStackTrace();
        }
        return fetchUserDetailFromDb.getUserDetailFromDb_HM();
    }

}
