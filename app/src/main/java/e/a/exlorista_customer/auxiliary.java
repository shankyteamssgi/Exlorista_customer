package e.a.exlorista_customer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by a on 12/26/2019.
 */

class auxiliary{

    //Internet availability, and server url
    static String INTERNET_STATUS_STRING="internetAvailabilityStatus";
    static String CALLING_ACTIVITY_NAME="parentActivityName";
    //static String SERVER_URL="http://192.168.43.137";
    //static String SERVER_URL="http://192.168.43.78";
    //static String SERVER_URL="http://ec2-3-16-188-152.us-east-2.compute.amazonaws.com/app";
    static String SERVER_URL="http://3.16.188.152/app";

    // StoreDetailsAdapter.java
    final static int MON_INT=0;
    final static int TUE_INT=1;
    final static int WED_INT=2;
    final static int THU_INT=3;
    final static int FRI_INT=4;
    final static int SAT_INT=5;
    final static int SUN_INT=6;

    //otp timeout
    final static int OTP_TIMEOUT=60;

    //intent (StoreDetailsAdapter.java-> store.java)
    //Also,
    //storeId (StoreProductCategoriesAdapter.java -> fetchStoreProductCategories.php)
    static String STORE_IMGPATH="storeImgPath";
    static String STORE_ID="storeId"; // also used at other places
    static String STORE_NAME="storeName";
    static String STORE_ADDRESS="storeAddress";
    static String STORE_TIMING="store_timing";

    //intent (cart.java -> liveOrderStatus.java)
    static String PRODIDS_INCART="prodids_incart"; // STORE_ID is used for this intent
    static String CART_TOTAL="cart_total";
    static String CART_DELIVERYCHARGE="cart_deliveryCharge";
    static String CART_GRANDTOTAL="cart_grandTotal";

    //liveOrderStatus.java
    /* DUMMYVAL means dummy value. This exists because a certain implementation is incomplete.
    *  Here, DUMMYVAL_CUSTID exists because the method to fetch customer id from database does not exist.
    *  Therefore, until that method is implemented, this DUMMYVAL_CUSTID will be used.*/
    static String DUMMYKEY_CUSTID="cust_id";
    static String DUMMYVAL_CUSTID="1";

    //intent (signinorup.java -> phoneVerification.java)
    static String PHONE_NO="phoneNo";

    // used in both auxiliaryuseraccountmanagement.java & auxiliaryordermanagement.java
    static String PPK_REQUESTTYPE="requestType";

    // auxiliaryuseraccountmanagement.java (loginOrSignup.java)
    static String PPV_REQUESTTYPE_USEREXISTENCECHECK="0";
    static String PPV_REQUESTTYPE_USERADDITION="1";
    static String PPV_REQUESTTYPE_USERDETAILFETCH="2";
    static String PPK_EMAIL="email";
    static String PPK_DISPLAYNAME="displayName";
    static String PPK_PHONE="phone";
    static String PPK_FACEBOOKUSERID="facebookUserId";

    // auxiliaryordermanagement.java (usage in liveOrderStatus.java)
    static String PPK_CUSTID="custId";
    static String PPK_STOREID="storeId";
    static String PPK_PRODIDS="prodIds";
    static String PPK_PRODCOUNTS="prodCounts";
    static String PPK_PRODMRPS="prodMrps";
    static String PPK_PRODSTOREPRICES="prodStoreprices";
    static String PPK_ORDERTOTAL="orderTotal";
    static String PPK_ORDERDELIVERYCHARGE="orderDeliveryCharge";
    static String PPK_ORDERGRANDTOTAL="orderGrandTotal";
    static String PPK_CUSTADDRID="custaddrId";
    static String PPK_PAYMID="paymId";
    static String PPV_REQUESTTYPE_ORDERPLACEMENT="10";

    // auxiliaryfcmmanager.java (usage in liveOrderStatus)
    static String PPK_FCMTOKEN="fcmToken";
    static String FCM_RECEIVEDMESSAGE="fcm_receivedMessage";
    static String PPV_REQUESTTYPE_FCMTOKENADDORUPDATE="20";

    // liveOrderStatus.java
    static String DEFAULT_TOPIC="defaultTopic";

    // auxiliaryaddressbook.java
    static String PPV_REQUESTTYPE_ADDRESSBOOKFETCH="51";
    static String PPV_REQUESTTYPE_ADDRESSDELETE="52";
    static String PPV_REQUESTTYPE_ADDRESSEDIT="53";

    // MainActivity.java -> loginOrSignup.java
    final static String NAVBUTTON_CLICKED="navButton_clicked";
    final static String NAV_LOGINB="nav_loginButton";
    final static String NAV_SIGNUPB="nav_signupButton";

    //intent (loginOrSignup.java -> phoneVerification.java)
    static String SELECTED_OPTION="selectedOption";
    static String CONTINUE_WITH="continueWith";

    // Request validation from client
    // PPK -> POST parameter keys
    // PPV -> POST parameter values
    // 1) INITIAL_CHECK
    static String PPK_INITIAL_CHECK="PPK_INITIAL_CHECK";
    static String PPK_INITIAL_CHECK_NOT_SET="PPK_INITIAL_CHECK not set";
    static String PPK_INITIAL_CHECK_FAIL="PPK_INITIAL_CHECK failed";
    static String PPV_INITIAL_CHECK="EXPLORISTA";

    private int serverAvailability;
    private Context mContext;

    boolean isInternetAvailable() throws InterruptedException, IOException {
        String command="ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor()==0;
    }

    void setServerAvailability(int serverAvailability){
        this.serverAvailability=serverAvailability;
    }

    int getServerAvailability(){
        return this.serverAvailability;
    }

    void setDefaultServerAvailability(){
        this.serverAvailability=-1;
    }

    void checkServerAvailability(final String urlWebService, final int seconds_Until_Server_Unavailability){

        class CheckServerAvailability extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    URL url = new URL(urlWebService);
                    //Log.i("control","url executed");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    //Log.i("control","con executed");
                    con.setConnectTimeout(seconds_Until_Server_Unavailability*1000);
                    //Log.i("control","connection timeout set");
                    int response_code=con.getResponseCode();
                    //Log.i("control","response_code is "+Integer.toString(response_code));
                    if(response_code==200){
                        auxiliary.this.setServerAvailability(1);
                        //Log.i("server available here","true");
                    }
                } catch (MalformedURLException mue){
                    //Log.i("exception","MalformedURLException occurred in CheckServerAvailability");
                    auxiliary.this.setServerAvailability(0);
                } catch (SocketTimeoutException ste){
                    //Log.i("exception","SocketTimeOutException occurred in CheckServerAvailability");
                    auxiliary.this.setServerAvailability(0);
                } catch (IOException io){
                    //Log.i("exception","IOException occurred in CheckServerAvailability");
                    auxiliary.this.setServerAvailability(0);
                } catch (Exception e){
                    //Log.i("exception","Exception occurred in CheckServerAvailability");
                    auxiliary.this.setServerAvailability(0);
                }
                return null;
            }
        }
        CheckServerAvailability csa=new CheckServerAvailability();
        csa.execute();
    }

    static String postParamsToString(HashMap<String,String> params){
        /*
        Takes POST parameters in a HashMap<String, String>
        and returns POST string to be passed to OutputStream of HttpURLConnection
        */
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            try {
                if (i != 0){
                    sb.append("&");
                }
                sb.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                //e.printStackTrace();
            }
            i++;
        }
        return sb.toString().trim();
    }

    static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte [] b=baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    static String BitMapToStringOGC(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    static String arrayListToStr(ArrayList<String> arrayListStr, String delimiter){
        StringBuilder sb=new StringBuilder();
        sb.append(arrayListStr.get(0));
        for(int i=1;i<arrayListStr.size();i++) {
            sb.append(delimiter).append(arrayListStr.get(i));
        }
        return sb.toString().trim();
    }

}
