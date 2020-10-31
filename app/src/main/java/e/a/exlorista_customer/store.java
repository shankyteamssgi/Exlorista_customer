package e.a.exlorista_customer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import e.a.exlorista_customer.ProgressDialog.progressDialog;

public class store extends AppCompatActivity
        implements StoreProductCategoriesAdapter.storeProductCategoriesAdapterCallback
        ,StoreProductsAdapter.StoreProductsSelectedAdapterCallback{

    progressDialog progressDialog;
    Bundle extras;
    static String storeId, storeName, storeAddress; // this is needed to be displayed in cart as well
    String[] storeTiming;
    private boolean storeTimingVisible;
    String storeImgPath;
    TextView storeNameStorePageTV, storeAddressStorePageTV, storeTimingTextTV, storeTimingMonStorePageTV,
            storeTimingTueStorePageTV, storeTimingWedStorePageTV, storeTimingThuStorePageTV, storeTimingFriStorePageTV,
            storeTimingSatStorePageTV, storeTimingSunStorePageTV ;
    ImageView storeImgStorePageIV, storeTimingViewIV;
    TextView storeProductCategoryAndSubcategoryTV, cartItemCount_StoreTV;
    LinearLayout storeTimingShowHideLL;
    NestedScrollView cartSummary_StoreNSV;
    Button proceedToCartB;
    //static auxiliaryasyncs auxasync;
    static auxiliaryasyncs mAuxasync;
    private RecyclerView storeProductCategoriesRV, storeProductsAndSubcategoriesRV;
    private RecyclerView.Adapter storeProductCategoriesRVAdapter;
    private StoreProductsAndSubcategoriesAdapter storeProductsAndSubcategoriesRVAdapter;
    private RecyclerView.LayoutManager storeProductCategoriesRVLayoutManager, storeProductsAndSubcategoriesRVLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        //StatusBarTransparent
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        //StatusBarTransparent Close

        /*setTitle("store");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().hide();*/

        //Log.i("CONTROL","inside store.java");
        //initilize the dialog
        progressDialog = new progressDialog(this);


        storeTimingVisible=false;
        storeNameStorePageTV=findViewById(R.id.storeNameStorePageTV);
        storeAddressStorePageTV=findViewById(R.id.storeAddressStorePageTV);
        storeTimingTextTV=findViewById(R.id.storeTimingTextTV);
        storeTimingMonStorePageTV=findViewById(R.id.storeTimingMonStorePageTV);
        storeTimingTueStorePageTV=findViewById(R.id.storeTimingTueStorePageTV);
        storeTimingWedStorePageTV=findViewById(R.id.storeTimingWedStorePageTV);
        storeTimingThuStorePageTV=findViewById(R.id.storeTimingThuStorePageTV);
        storeTimingFriStorePageTV=findViewById(R.id.storeTimingFriStorePageTV);
        storeTimingSatStorePageTV=findViewById(R.id.storeTimingSatStorePageTV);
        storeTimingSunStorePageTV=findViewById(R.id.storeTimingSunStorePageTV);
        storeTimingShowHideLL=findViewById(R.id.storeTimingShowHideLL);
        storeTimingViewIV=findViewById(R.id.storeTimingViewIV);
        storeImgStorePageIV=findViewById(R.id.storeImgStorePageIV);
        storeProductCategoryAndSubcategoryTV=findViewById(R.id.storeProductCategoryAndSubcategoryTV);
        cartSummary_StoreNSV=findViewById(R.id.cartSummary_StoreNSV);
        cartItemCount_StoreTV=findViewById(R.id.cartItemCount_StoreTV);
        proceedToCartB=findViewById(R.id.proceedToCartB);

        extras=getIntent().getExtras();
        try{

            storeId=extras.getString(auxiliary.STORE_ID);
            storeName=extras.getString(auxiliary.STORE_NAME);
            storeAddress=extras.getString(auxiliary.STORE_ADDRESS);
            storeTiming=extras.getStringArray(auxiliary.STORE_TIMING);
            storeImgPath=extras.getString(auxiliary.STORE_IMGPATH);
            getStoreImage(auxiliary.SERVER_URL+storeImgPath,storeImgStorePageIV);
            storeNameStorePageTV.setText(storeName);
            storeAddressStorePageTV.setText(storeAddress);
            setStoreTimings();
            storeTimingShowHideLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(store.this,"inside onclick",Toast.LENGTH_SHORT).show();
                    if(!storeTimingVisible){
                        // store timings are hidden
                        //Log.i("STORETIMING","store timings were hidden");
                        //Toast.makeText(store.this,"store timings were hidden",Toast.LENGTH_SHORT).show();
                        storeTimingViewIV.setImageResource(R.drawable.uparrow);
                        storeTimingTextTV.setText("Hide store timings");
                        storeTimingMonStorePageTV.setVisibility(View.VISIBLE);
                        storeTimingTueStorePageTV.setVisibility(View.VISIBLE);
                        storeTimingWedStorePageTV.setVisibility(View.VISIBLE);
                        storeTimingThuStorePageTV.setVisibility(View.VISIBLE);
                        storeTimingFriStorePageTV.setVisibility(View.VISIBLE);
                        storeTimingSatStorePageTV.setVisibility(View.VISIBLE);
                        storeTimingSunStorePageTV.setVisibility(View.VISIBLE);
                        storeTimingVisible=true;
                    } else{
                        // store timings are visible
                        Log.i("STORETIMING","store timings were visible");
                        storeTimingViewIV.setImageResource(R.drawable.downarrow);
                        storeTimingTextTV.setText("View store timings");
                        storeTimingMonStorePageTV.setVisibility(View.GONE);
                        storeTimingTueStorePageTV.setVisibility(View.GONE);
                        storeTimingWedStorePageTV.setVisibility(View.GONE);
                        storeTimingThuStorePageTV.setVisibility(View.GONE);
                        storeTimingFriStorePageTV.setVisibility(View.GONE);
                        storeTimingSatStorePageTV.setVisibility(View.GONE);
                        storeTimingSunStorePageTV.setVisibility(View.GONE);
                        storeTimingVisible=false;
                    }
                }
            });
            //store.auxasync=new auxiliaryasyncs(storeId);
            store.mAuxasync=new auxiliaryasyncs(storeId);
            loadStoreProductCategories();
            loadStoreProductsAndSubcategories("all");
            if(!proceedToCartB.hasOnClickListeners()){
                proceedToCartB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent storeToCartIntent=new Intent(store.this,cart.class);
                        store.this.startActivity(storeToCartIntent);
                    }
                });
            }
        } catch (NullPointerException ne){
            //Log.i("exception","NullPointerException occurred (store.java at extras.getString)");
            //ne.printStackTrace();
        } catch (Exception e){
            //Log.i("exception","exception occurred (store.java at extras.getString)");
            //e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        storeProductsAndSubcategoriesRVAdapter.notifyDataSetChanged();
        onMethodCallback_ProceedToCartBottomSheetBehavior();
        super.onResume();
    }

    private void getStoreImage(final String urlWebService, final ImageView storeImageView){
        class GetStoreImage extends AsyncTask<Void, Void, Bitmap>{

            private String url;
            private ImageView imageView;

            private GetStoreImage(String url, ImageView imageView){
                this.url=url;
                this.imageView=imageView;
            }

            @Override
            protected Bitmap doInBackground(Void... voids) {
                try{
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoInput(true);
                    con.connect();
                    InputStream input = con.getInputStream();
                    return BitmapFactory.decodeStream(input);
                } catch (MalformedURLException mue){
                    //Log.i("exception","MalformedURLException occurred in getStoreImage");
                    //mue.printStackTrace();
                } catch (IOException io){
                    //Log.i("exception","IOException occurred in getStoreImage");
                    //io.printStackTrace();
                } catch (Exception e){
                    //Log.i("exception","Exception occurred in getStoreImage");
                    //e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageBitmap(result);
            }
        }
        GetStoreImage getStoreImage=new GetStoreImage(urlWebService,storeImageView);
        getStoreImage.execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setStoreTimings(){
        storeTimingMonStorePageTV.setText("Mon -> "+storeTiming[auxiliary.MON_INT]);
        storeTimingTueStorePageTV.setText("Tue -> "+storeTiming[auxiliary.TUE_INT]);
        storeTimingWedStorePageTV.setText("Wed -> "+storeTiming[auxiliary.WED_INT]);
        storeTimingThuStorePageTV.setText("Thu -> "+storeTiming[auxiliary.THU_INT]);
        storeTimingFriStorePageTV.setText("Fri -> "+storeTiming[auxiliary.FRI_INT]);
        storeTimingSatStorePageTV.setText("Sat -> "+storeTiming[auxiliary.SAT_INT]);
        storeTimingSunStorePageTV.setText("Sun -> "+storeTiming[auxiliary.SUN_INT]);
    }

    private void loadStoreProductCategories(){
        storeProductCategoriesRV=findViewById(R.id.storeProductCategoryRV);
        storeProductCategoriesRVLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //storeProductCategoriesRVAdapter=new StoreProductCategoriesAdapter(this, auxasync);
        storeProductCategoriesRVAdapter=new StoreProductCategoriesAdapter(this);
        storeProductCategoriesRV.setLayoutManager(storeProductCategoriesRVLayoutManager);
        storeProductCategoriesRV.setAdapter(storeProductCategoriesRVAdapter);
    }

    private void loadStoreProductsAndSubcategories(String categorySelected){
        storeProductsAndSubcategoriesRV=findViewById(R.id.storeProductAndSubcategoryRV);
        storeProductsAndSubcategoriesRVLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //storeProductsAndSubcategoriesRVAdapter=new StoreProductsAndSubcategoriesAdapter(this,auxasync,categorySelected);
        storeProductsAndSubcategoriesRVAdapter=new StoreProductsAndSubcategoriesAdapter(this,categorySelected);
        storeProductsAndSubcategoriesRV.setLayoutManager(storeProductsAndSubcategoriesRVLayoutManager);
        storeProductsAndSubcategoriesRV.setAdapter(storeProductsAndSubcategoriesRVAdapter);
    }

    @Override
    public void onMethodCallback_StoreProductCategoriesAdapter(String categoryClicked) {
        //Log.i("CONTROL","inside callback implementation of store.java");
        //Log.i("CONTROL","category clicked = "+categoryClicked);
        if(!this.storeProductCategoryAndSubcategoryTV.getText().toString().toLowerCase().equals(categoryClicked.toLowerCase())){
            this.storeProductCategoryAndSubcategoryTV.setText(categoryClicked);
            this.storeProductsAndSubcategoriesRVAdapter.categoryChanged(categoryClicked);
        }
    }

    @Override
    public void onMethodCallback_ProceedToCartBottomSheetBehavior() {
        //Log.i("CONTROL","inside proceedToCart Bottom sheet");
        String SP_storeId=auxiliarycart.getStoreIdInCart(this);
        if(store.storeId.equals(SP_storeId)){
            int cartItemCount=auxiliarycart.diffProdCountInCart(this);
            if(cartItemCount>0){
                cartSummary_StoreNSV.setVisibility(View.VISIBLE);
                cartItemCount_StoreTV.setText(Integer.toString(cartItemCount)+" items");
            }
            else{
                cartSummary_StoreNSV.setVisibility(View.GONE);
            }
        } else{
            cartSummary_StoreNSV.setVisibility(View.GONE);
        }
    }


}
