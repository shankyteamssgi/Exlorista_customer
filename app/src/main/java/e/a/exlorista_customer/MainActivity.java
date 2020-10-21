package e.a.exlorista_customer;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.acl.Group;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ViewPager imgSliderVP;
    private ImageSliderAdapter imageSliderAdapter;
    private RecyclerView storeDetailsRV;
    private RecyclerView.Adapter storeDetailsRVAdapter;
    private RecyclerView.LayoutManager storeDetailsRVLayoutManager;
    private int currentPage;
    private Timer timer;
    private ActionBarDrawerToggle toggle;

    private auxiliary aux;
    private Context mContext;

    private final int SECONDS_UNTIL_SERVER_UNAVAILABILITY=2;
    private final int SLIDER_DELAY=0;
    private final int SLIDER_PERIOD=10;
    static HashMap<String,Integer> IDHamburgerOption=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        navigationView=findViewById(R.id.nav_view);
        attachOnClickListenersToUserAccountManagementViews();
        currentPage=0;
        aux=new auxiliary();
        aux.setServerAvailability(-1);

        try{
            if(!aux.isInternetAvailable()){
                // Internet not available. However, if there's internet then data plan must be inactive
                LaunchNoInternetActivity(0);
            }
            else{
                // Internet available with active data plan
                Log.i("MainActivity","internet available, checking server availability");
                aux.checkServerAvailability(auxiliary.SERVER_URL+"/fetchHamburgerMenu.php",
                        SECONDS_UNTIL_SERVER_UNAVAILABILITY);
                Log.i("MainActivity","checking done, inside while loop");
                while(true){
                    int serverAvailability=aux.getServerAvailability();
                    if(serverAvailability==1){
                        // server available
                        getHamburgerJSON(auxiliary.SERVER_URL+"/fetchHamburgerMenu.php");
                        loadImageSlider(SLIDER_PERIOD,SLIDER_DELAY);
                        loadStoreDetails();
                        //fetchSetImage(auxiliary.SERVER_URL+"/images/ads/ad1.jpg");
                        //getGalleryImagesPathJSON(auxiliary.SERVER_URL+"/fetchGalleryImages.php");
                        break;
                    }
                    else if(serverAvailability==0){
                        // server unavailable
                        LaunchNoInternetActivity(1);
                        break;
                    }
                }
                Log.i("MainActivity","out of while loop");
                Log.i("MainActivity server url", auxiliary.SERVER_URL);
            }
        }
        catch (InterruptedException ie){
            //Log.i("ERROR", "InterruptedException occurred");
            //ie.printStackTrace();
        }
        catch (IOException io){
            //Log.i("ERROR", "IOException occurred");
            //io.printStackTrace();
        }

        //navigationView=findViewById(R.id.nav_view);
        /*navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer=findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        //Navigation Drawer
        setUpToolbar();
        //Navigation Drawer Close


    }

    //Navigtion Drawer

    private void setUpToolbar() {
        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.menu, null);
        toolbar.setNavigationIcon(d);


    }
    //Navigation Drawer Close

    @Override
    protected void onResume() {
        Log.i("Inside onResume","Code executed");
        manageUserDetailInNavbar();
        handleBottomSheetBehavior();
        super.onResume();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==IDHamburgerOption.get("cart")){
            startActivity(new Intent(this,cart.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==IDHamburgerOption.get("order history")){
            startActivity(new Intent(this,orderHistory.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==IDHamburgerOption.get("address book")){
            startActivity(new Intent(this,addressBook.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==IDHamburgerOption.get("payment methods")){
            startActivity(new Intent(this,paymentMethods.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==IDHamburgerOption.get("wallet")){
            startActivity(new Intent(this,wallet.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==IDHamburgerOption.get("bookmarked stores")){
            startActivity(new Intent(this,bookmarkedStores.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==IDHamburgerOption.get("help and support")){
            startActivity(new Intent(this,helpAndSupport.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(item.getItemId()==IDHamburgerOption.get("about us")){
            startActivity(new Intent(this,aboutUs.class));
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }


    private void getHamburgerJSON(final String urlWebService) {

        class GetHamburgerJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                NavigationView navView = findViewById(R.id.nav_view);
                Menu menu = navView.getMenu();
                try {
                    //Log.i("json", s);
                    JSONArray jsonArray = new JSONArray(s);
                    int hamburgerOptionID = Menu.FIRST;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String optionName = obj.getString("option name");
                        menu.add(R.id.hamburger_menu, hamburgerOptionID, 0, optionName)
                                .setVisible(obj.getInt("visibility") == 1);
                        IDHamburgerOption.put(optionName, hamburgerOptionID);
                        //Log.i(optionName, Integer.toString(hamburgerOptionID));
                        hamburgerOptionID++;
                    }
                    navView.invalidate();
                } catch (JSONException e) {
                    //Log.i("exception","JSONException occurred in GetJSON");
                    //e.printStackTrace();
                } catch (NullPointerException ne) {
                    //App has internet connection
                    //but is unable to establish connection to database server.
                    //Programmatically string 's' will be null and as a result,
                    //NullPointerException will occur
                    //Log.i("exception", "NullPointerException occurred in GetJSON");
                    //ne.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    String json;
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json);
                    }
                    return sb.toString().trim();
                } catch (MalformedURLException mue){
                    //Log.i("exception","MalformedURLException occurred in GetJSON");
                } catch (IOException io){
                    //Log.i("exception","IOException occurred in GetJSON");
                } catch (Exception e){
                    //Log.i("exception","Exception occurred in GetJSON");
                }
                return null;
            }
        }
        GetHamburgerJSON getHamburgerJSON=new GetHamburgerJSON();
        getHamburgerJSON.execute();
    }

    private void getGalleryImagesPathJSON(final String urlWebService){
        class GetGalleryImagesPathJSON extends AsyncTask<Void, Void, String>{

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    //Log.i("json", s);
                    JSONArray jsonArray = new JSONArray(s);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String storeId = obj.getString("store_id");
                        String adspaceId = obj.getString("adspace_id");
                        String dirPath = obj.getString("dirpath_path");
                        String fileExt = obj.getString("fileext_name");
                        //Log.i("IMAGE PATH",dirPath+storeId+"_"+adspaceId+"."+fileExt);
                    }
                } catch (JSONException e) {
                    //Log.i("exception","JSONException occurred in GetGalleryImagesPathJSON");
                    //e.printStackTrace();
                } catch (NullPointerException ne) {
                    //Log.i("exception", "NullPointerException occurred in GetGalleryImagesPathJSON");
                    //ne.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    String json;
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json);
                    }
                    return sb.toString().trim();
                } catch (MalformedURLException mue){
                    //Log.i("exception","MalformedURLException occurred in GetGalleryImagesPathJSON");
                } catch (IOException io){
                    //Log.i("exception","IOException occurred in GetGalleryImagesPathJSON");
                } catch (Exception e){
                    //Log.i("exception","Exception occurred in GetGalleryImagesPathJSON");
                }
                return null;
            }
        }
        GetGalleryImagesPathJSON getGalleryImagesPathJSON=new GetGalleryImagesPathJSON();
        getGalleryImagesPathJSON.execute();
    }

    /*
    private void fetchSetImage(final String imageUrl){
        class FetchSetImage extends AsyncTask<Void, Void, Bitmap> {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                try {
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    return BitmapFactory.decodeStream(input);
                } catch (MalformedURLException mue){
                    Log.i("exception","MalformedURLException occurred in fetchsetImage");
                } catch (Exception e){
                    Log.i("exception","Exception occurred in fetchsetImage");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if(result!=null) {
                    ImageView imageView = findViewById(R.id.testIV);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setImageBitmap(result);
                }
                else{
                    Log.i("bitmap NULL","bitmap is null");
                }
            }
        }
        FetchSetImage fetchSetImage=new FetchSetImage();
        fetchSetImage.execute();
    }
    */


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    private void LaunchNoInternetActivity(int internetStatusCode){
        Intent intent=new Intent(this,NoInternet.class);
        intent.putExtra(auxiliary.INTERNET_STATUS_STRING,internetStatusCode);
        intent.putExtra(auxiliary.CALLING_ACTIVITY_NAME,this.getClass().getSimpleName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void loadImageSlider(int slider_period, int slider_delay){
        imgSliderVP=findViewById(R.id.imgSliderVP);
        imageSliderAdapter=new ImageSliderAdapter(this);
        imgSliderVP.setAdapter(imageSliderAdapter);
        final int slideCount=imageSliderAdapter.getCount();
        final Handler handler=new Handler();
        final Runnable updateImage=new Runnable() {
            @Override
            public void run() {
                if(currentPage==slideCount){
                    currentPage=0;
                }
                imgSliderVP.setCurrentItem(currentPage++,true);
            }
        };
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(updateImage);
            }
        }, slider_delay*1000, slider_period*1000);
    }

    private void loadStoreDetails(){
        storeDetailsRV=findViewById(R.id.storeRV);
        storeDetailsRVLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        storeDetailsRVAdapter=new StoreDetailsAdapter(this);
        storeDetailsRV.setLayoutManager(storeDetailsRVLayoutManager);
        storeDetailsRV.setAdapter(storeDetailsRVAdapter);
    }

    private void addUserDetailToNavbar(String email,String display_name,String phone){
        LinearLayout navBarUserDetailsLL;
        TextView navBarUserDisplayNameTV,navBarUserEmailTV,navBarUserPhoneTV;
        Button navLoginB,navSignupB,navSignoutB;

        View header=navigationView.getHeaderView(0);
        navBarUserDetailsLL=header.findViewById(R.id.navBarUserDetailsLL);
        navBarUserDisplayNameTV=header.findViewById(R.id.navBarUserDisplayNameTV);
        navBarUserEmailTV=header.findViewById(R.id.navBarUserEmailTV);
        navBarUserPhoneTV=header.findViewById(R.id.navBarUserPhoneTV);
        navLoginB=header.findViewById(R.id.navLoginB);
        navSignupB=header.findViewById(R.id.navSignupB);
        navSignoutB=header.findViewById(R.id.navSignoutB);

        navLoginB.setVisibility(View.GONE);
        navSignupB.setVisibility(View.GONE);
        navBarUserDisplayNameTV.setText(display_name);
        navBarUserEmailTV.setText(email);
        navBarUserPhoneTV.setText(phone);
        navBarUserDetailsLL.setVisibility(View.VISIBLE);
        navSignoutB.setVisibility(View.VISIBLE);
    }

    void clearUserDetailFromNavbar(){
        LinearLayout navBarUserDetailsLL;
        Button navLoginB,navSignupB,navSignoutB;

        View header=navigationView.getHeaderView(0);
        navBarUserDetailsLL=header.findViewById(R.id.navBarUserDetailsLL);
        navLoginB=header.findViewById(R.id.navLoginB);
        navSignupB=header.findViewById(R.id.navSignupB);
        navSignoutB=header.findViewById(R.id.navSignoutB);

        navBarUserDetailsLL.setVisibility(View.GONE);
        navSignoutB.setVisibility(View.GONE);
        navLoginB.setVisibility(View.VISIBLE);
        navSignupB.setVisibility(View.VISIBLE);
    }

    void manageUserDetailInNavbar(){
        HashMap<String,String> userDetails_HM=auxiliaryuseraccountmanager.getUserDetailsFromSP(mContext);
        if(userDetails_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_USERDATAVALIDITYFLAG)!=null){
            if(userDetails_HM
                    .get(auxiliaryuseraccountmanager.DETAILTYPE_USERDATAVALIDITYFLAG)
                    .equals(auxiliaryuseraccountmanager.USERDATA_VALID)){
                addUserDetailToNavbar(
                        userDetails_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_EMAIL),
                        userDetails_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_DISPLAYNAME),
                        userDetails_HM.get(auxiliaryuseraccountmanager.DETAILTYPE_PHONENUMBER));
            } else if(userDetails_HM
                    .get(auxiliaryuseraccountmanager.DETAILTYPE_USERDATAVALIDITYFLAG)
                    .equals(auxiliaryuseraccountmanager.USERDATA_INVALID)){
                clearUserDetailFromNavbar();
            }
        }
    }

    void attachOnClickListenersToUserAccountManagementViews(){
        View header=navigationView.getHeaderView(0);
        Button navLoginB=header.findViewById(R.id.navLoginB);
        Button navSignupB=header.findViewById(R.id.navSignupB);
        Button navSignoutB=header.findViewById(R.id.navSignoutB);
        navLoginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MainActivityToLoginOrSignupIntent=new Intent(MainActivity.this,loginOrSignup.class);
                MainActivityToLoginOrSignupIntent.putExtra(auxiliary.NAVBUTTON_CLICKED,auxiliary.NAV_LOGINB);
                startActivity(MainActivityToLoginOrSignupIntent);
            }
        });
        navSignupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MainActivityToLoginOrSignupIntent=new Intent(MainActivity.this,loginOrSignup.class);
                MainActivityToLoginOrSignupIntent.putExtra(auxiliary.NAVBUTTON_CLICKED,auxiliary.NAV_SIGNUPB);
                startActivity(MainActivityToLoginOrSignupIntent);
            }
        });
        navSignoutB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearUserDetailFromNavbar();
                auxiliaryuseraccountmanager.signOutOGC(mContext,getString(R.string.default_web_client_id),false);
            }
        });
    }

    void handleBottomSheetBehavior(){
        NestedScrollView cartSummary_MainActivityNSV=findViewById(R.id.cartSummary_MainActivityNSV);
        TextView cartItemCount_MainActivityTV=findViewById(R.id.cartItemCount_MainActivityTV);
        Button proceedToCart_MainActivityB=findViewById(R.id.proceedToCart_MainActivityB);
        try{
            int cartItemCount=auxiliarycart.diffProdCountInCart(mContext);
            if(cartItemCount>0){
                cartSummary_MainActivityNSV.setVisibility(View.VISIBLE);
                cartItemCount_MainActivityTV.setText(Integer.toString(cartItemCount)+" items");
                if(!proceedToCart_MainActivityB.hasOnClickListeners()){
                    proceedToCart_MainActivityB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent MainActivityToCartIntent=new Intent(MainActivity.this,cart.class);
                            startActivity(MainActivityToCartIntent);
                        }
                    });
                }
            } else{
                cartSummary_MainActivityNSV.setVisibility(View.GONE);
            }
        } catch (Exception e){
            cartSummary_MainActivityNSV.setVisibility(View.GONE);
        }
    }

}
