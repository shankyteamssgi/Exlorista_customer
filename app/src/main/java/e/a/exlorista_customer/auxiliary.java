package e.a.exlorista_customer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

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

    // server response Strings
    static String NULL="null";

    //ProgressDialog message
    final static String PROGRESS_DIALOG_MESSAGE="Please wait";

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
    static String DUMMYVAL_CUSTADDRID="1";

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
    static String PPK_ORDERDIFFPROD="orderDiffProd";
    static String PPK_CUSTADDRID="custaddrId";
    static String PPK_PAYMID="paymId";
    static String PPV_REQUESTTYPE_ORDERPLACEMENT="10";
    static String PPV_REQUESTTYPE_FETCHTRACKABLEORDERS="11";

    // auxiliaryfcmmanager.java (usage in liveOrderStatus)
    static String PPK_FCMTOKEN="fcmToken";
    static String FCM_RECEIVEDMESSAGE="fcm_receivedMessage";
    static String PPV_REQUESTTYPE_FCMTOKENADDORUPDATE="20";

    // liveOrderStatus.java
    static String DEFAULT_TOPIC="defaultTopic";
    static String PPK_ORDERSTATUS="orderStatus";
    static String PPV_ORDERSTATUS_PLACED="orderStatusPlaced";
    static String PPV_ORDERSTATUS_ACCEPTED="orderStatusAccepted";
    static String PPV_ORDERSTATUS_CANCELLED="orderStatusCancelled";
    static String PPV_ORDERSTATUS_PENDING="orderStatusPending";
    static String PPV_ORDERSTATUS_CONFIRMED="orderStatusConfirmed";
    static String PPV_ORDERSTATUS_PACKAGED="orderStatusPackaged";
    static String PPV_ORDERSTATUS_HANDOVER="orderStatusHandover";

    // auxiliaryaddressbook.java
    static String PPV_REQUESTTYPE_ADDRESSBOOKFETCH="51";
    static String PPV_REQUESTTYPE_ADDRESSDELETE="52";
    static String PPV_REQUESTTYPE_ADDRESSEDIT="53";

    // addressBook.java
    static String ADDR_STATE="addrState";
    static String ADDR_CITY="addrCity";
    static String ADDR_LAT="addrLat";
    static String ADDR_LONG="addrLong";

    // addAddress.java
    static String PPK_ADDRLAT="addrLat";
    static String PPK_ADDRLONG="addrLong";
    static String PPK_STATENAME="stateName";
    static String PPK_CITYNAME="cityName";
    static String PPK_STATEID="stateId";
    static String PPK_CITYID="cityId";
    static String PPK_AREAID="areaId";
    static String PPK_ADDRCOMPLETE="addrComplete";
    static String PPK_ADDRLANDMARK="addrLandmark";
    static String PPK_ADDRTAG="addrTag";
    static String PPV_REQUESTTYPE_ADDRESSAREADATAFETCH="54";
    static String PPV_REQUESTTYPE_ADDRESSCITYDATAFETCH="55";
    static String PPV_REQUESTTYPE_ADDRESSSTATEDATAFETCH="56";
    static String PPV_REQUESTTYPE_ADDRESSADD="57";
    static String PPV_REQUESTTYPE_STATEANDCITYIDFETCH="58";
    static String SPINNER_UNSELECTED_STATE="Select state";
    static String SPINNER_UNSELECTED_CITY="Select city";
    static String SPINNER_UNSELECTED_AREA="Select area";
    static int COMPLETEADDRESS_MAXLENGTH=255;
    static int LANDMARK_MAXLENGTH=50;
    static int CUSTOMTAG_MAXLENGTH=30;

    // (orderHistoryAdapter -> orderDetail) | payment
    static String ORDER_ID="orderId";
    static String ORDER_STATUS="orderStatus";
    static String ORDER_TOTAL="orderTotal";
    static String ORDER_DELIVERYCHARGE="orderDeliveryCharge";
    static String ORDER_GRANDTOTAL="orderGrandTotal";
    static String ORDER_PAYMMETHOD="orderPaymentMethod";
    static String ORDER_DATE="orderDate";
    static String ORDER_TIME="orderTime";
    static String ORDER_DELIVERYADDRESS="orderDeliveryAddress";
    static String ORDER_PRODNAME="orderProdName";
    static String ORDER_PRODSIZE="orderProdSize";
    static String ORDER_PRODUNIT="orderProdUnit";
    static String ORDER_PRODCONTAINER="orderProdContainer";
    static String ORDER_PRODCOUNT="orderProdCount";
    static String ORDER_PRODSTATUS="orderProdStatus";
    static String ORDER_PRODAVAILABILITYSTATUS="orderProdAvailabilityStatus";
    static String ORDER_PRODMRP="orderProdMrp";
    static String ORDER_PRODSTOREPRICE="orderProdStorePrice";
    static String ORDER_STORENAME="orderStoreName";
    static String ORDER_STOREAREA="orderStoreArea";

    // orderDetail.java
    static String HEADING_ITEMPARTICULAR="particular";
    static String HEADING_ITEMMRP="mrp";
    static String HEADING_ITEMSTOREPRICE="store price";
    static String HEADING_ITEMTOTAL="item total";

    // deliveryType.java
    final static String DELIVERYTYPE="deliverytype";
    final static String DELIVERYTYPEID="deliverytype_id";
    final static String DELIVERYSLOTID="delivery_slotid";
    final static String DELIVERYDATETIME="delivery_datetime";
    final static String PPK_DELIVERYTYPEID="deliveryTypeId";

    // deliveryType + payment + auxiliaryordermanagement.java
    final static String PPK_DELIVERYTYPE="deliveryType";
    final static String PPV_DELIVERYTYPE_INSTANT="deliveryType_instant";
    final static String PPV_DELIVERYTYPE_SLOTTED="deliveryType_slotted";
    final static String PPV_DELIVERYTYPE_SCHEDULED="deliveryType_scheduled";
    final static String PPV_DELIVERYTYPE_TAKEAWAY="deliveryType_takeaway";
    // General
    final static String PPK_ORDERID="orderId";

    // payment
    final static String PPK_PAYMENTID="paymentId";
    final static String PPK_PAYMENTMETHODID="paymentMethodId";
    final static String PPK_PAYMENTSTATUSID="paymentStatusId";
    final static String PPV_REQUESTTYPE_PAYMENTRECORDCREATE="81";
    final static String PPV_REQUESTTYPE_PAYMENTRECORDASSIGNORDERID="82";

    // MainActivity.java -> loginOrSignup.java
    final static String NAVBUTTON_CLICKED="navButton_clicked";
    final static String NAV_LOGINB="nav_loginButton";
    final static String NAV_SIGNUPB="nav_signupButton";

    //intent (loginOrSignup.java -> phoneVerification.java)
    static String SELECTED_OPTION="selectedOption";
    static String CONTINUE_WITH="continueWith";

    // Volley test
    static String PPK_VOLLEYTEST="volleyTest";

    // General use
    static String FROM_ACTIVITY="fromActivity";

    // Request validation from client
    // PPK -> POST parameter keys
    // PPV -> POST parameter values
    // 1) INITIAL_CHECK
    static String PPK_INITIAL_CHECK="PPK_INITIAL_CHECK";
    static String PPK_INITIAL_CHECK_NOT_SET="PPK_INITIAL_CHECK not set";
    static String PPK_INITIAL_CHECK_FAIL="PPK_INITIAL_CHECK failed";
    static String PPV_INITIAL_CHECK="EXPLORISTA";

    private Context mContext;

    static boolean isInternetAvailable() throws InterruptedException, IOException {
        String command="ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor()==0;
    }

    static int checkServerAvailability(final String urlWebService, final int seconds_Until_Server_Unavailability){

        class CheckServerAvailability extends AsyncTask<Void, Void, Void> {

            int server_availability;

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
                        //auxiliary.this.setServerAvailability(1);
                        server_availability=1;
                        //Log.i("server available here","true");
                    }
                } catch (MalformedURLException mue){
                    //Log.i("exception","MalformedURLException occurred in CheckServerAvailability");
                    //auxiliary.this.setServerAvailability(0);
                    server_availability=0;
                } catch (SocketTimeoutException ste){
                    //Log.i("exception","SocketTimeOutException occurred in CheckServerAvailability");
                    //auxiliary.this.setServerAvailability(0);
                    server_availability=0;
                } catch (IOException io){
                    //Log.i("exception","IOException occurred in CheckServerAvailability");
                    //auxiliary.this.setServerAvailability(0);
                    server_availability=0;
                } catch (Exception e){
                    //Log.i("exception","Exception occurred in CheckServerAvailability");
                    //auxiliary.this.setServerAvailability(0);
                    server_availability=0;
                }
                return null;
            }
        }
        CheckServerAvailability csa=new CheckServerAvailability();
        try {
            csa.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return csa.server_availability;
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

    static ArrayList<String> arrayListOfHMtoArrayListOfHMVals(ArrayList<HashMap<String,String>> ah){
        // Utility in addAddress.java
        // Takes : ArrayList<HashMap<String,String>>
        // Returns : ArrayList of values of HashMap
        // For eg, ah = ArrayList<HashMap1, HashMap2, HashMap3...HashMapi............HashMapn>, Where, HashMapi only has one (key,value) pair
        // passing ah to this method would return ah_return = ArrayList<HashMap1_value, HashMap2_value, HashMap3_value.......>
        ArrayList<String> ah_return=new ArrayList<String>();
        for(int i=0;i<ah.size();i++){
            //ah_return.add((new ArrayList<>(ah.get(i).values())).get(0));
            //ah_return.add(ah.get(i).values().iterator().toString());
            ah_return.add(ah.get(i).values().iterator().next());
        }
        return ah_return;
    }

    static String findKeyForGivenValueInArrayListOfHM(ArrayList<HashMap<String,String>> ah,String value){
        for(int i=0;i<ah.size();i++){
            if(ah.get(i).values().iterator().next().equals(value)){
                return ah.get(i).keySet().iterator().next();
            }
        }
        return null;
    }

    static ArrayList<String> strToArrayList(String str,String delimiter){
        return new ArrayList<String>(Arrays.asList(str.split(delimiter)));
    }

    static boolean gpsEnabled(Context context){
        boolean gps_enabled=false;
        LocationManager locationManager=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try{
            if(locationManager!=null){
                gps_enabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } else{
                Log.i("LOCATION","locationManager is null");
            }
        } catch (Exception e){
            Log.i("LOCATION","Exception occurred");
            e.printStackTrace();
        }
        return gps_enabled;
    }

}
