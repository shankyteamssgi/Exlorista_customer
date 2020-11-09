package e.a.exlorista_customer;

import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;

public class cart extends AppCompatActivity implements CartProductsAdapter.CartProductsAdapterCallback{

    ArrayList<String> prodIdsInCart;
    String storeIdInCart;
    Button clearCartB, cartCheckout_CartB;
    LinearLayout cartShopDetailsLL,cartDeliveryDetailsLL,cartDeliveryChargesLL,cartTotalLL;
    TextView cartItemsHeadingTV,cartEmtpyTV,cartItemCount_CartTV,cartGrandTotalAmount_CartTV,cartTotalTV,cartDeliveryChargesTV;
    NestedScrollView cartSummary_CartNSV;
    RecyclerView cartStoreProductRV;
    RecyclerView.Adapter cartStoreProductRVAdapter;
    RecyclerView.LayoutManager cartStoreProductRVLayoutManager;

    auxiliary aux;

    int cart_total, cart_deliveryCharge, cart_grandTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("CART","Inside onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

       /* setTitle("Cart");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        clearCartB=findViewById(R.id.clearCartB);
        cartSummary_CartNSV=findViewById(R.id.cartSummary_CartNSV);
        cartSummary_CartNSV.setVisibility(View.GONE);
        clearCartB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auxiliarycart.clearCartOGC(getApplicationContext());
                cartSummary_CartNSV.setVisibility(View.GONE);
                reloadActivity();
            }
        });
        cartCheckout_CartB=findViewById(R.id.cartCheckout_CartB);
        cartShopDetailsLL=findViewById(R.id.cartShopDetailsLL);
        cartDeliveryDetailsLL=findViewById(R.id.cartDeliveryDetailsLL);
        cartDeliveryChargesLL=findViewById(R.id.cartDeliveryChargesLL);
        cartTotalLL=findViewById(R.id.cartTotalLL);
        cartItemsHeadingTV=findViewById(R.id.cartItemsHeadingTV);
        cartEmtpyTV=findViewById(R.id.cartEmptyTV);
        cartItemCount_CartTV=findViewById(R.id.cartItemCount_CartTV);
        cartTotalTV=findViewById(R.id.cartTotalTV);
        cartDeliveryChargesTV=findViewById(R.id.cartDeliveryChargesTV);
        cartGrandTotalAmount_CartTV=findViewById(R.id.cartGrandTotalAmount_CartTV);
        cartStoreProductRV=findViewById(R.id.cartStoreProductRV);
        prodIdsInCart=auxiliarycart.prodIdsInCartOGC(this);
        if(prodIdsInCart!=null){

            if(prodIdsInCart.size()==0){
                cartEmtpyTV.setVisibility(View.VISIBLE);
                clearCartB.setVisibility(View.GONE);
                cartDeliveryChargesLL.setVisibility(View.GONE);
                cartTotalLL.setVisibility(View.GONE);
            } else{
                cartEmtpyTV.setVisibility(View.GONE);
                clearCartB.setVisibility(View.VISIBLE);
                cartDeliveryChargesLL.setVisibility(View.VISIBLE);
                cartTotalLL.setVisibility(View.VISIBLE);
                cartCheckout_CartB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // start login/signup activity
                        /* (remove comment after payment has been implemented)
                        Intent cartToLoginOrSignupIntent=new Intent(cart.this,loginOrSignup.class);
                        cart.this.startActivity(cartToLoginOrSignupIntent);*/
                        // The below bypass is for testing purposes
                        // as long as payment has not been implemented
                        Intent bypassToLiveOrderStatus=new Intent(cart.this,liveOrderStatus.class);
                        bypassToLiveOrderStatus.putExtra(auxiliary.STORE_ID,storeIdInCart);
                        bypassToLiveOrderStatus.putExtra(auxiliary.DUMMYKEY_CUSTID,auxiliary.DUMMYVAL_CUSTID);
                        bypassToLiveOrderStatus.putStringArrayListExtra(auxiliary.PRODIDS_INCART,prodIdsInCart);
                        bypassToLiveOrderStatus.putExtra(auxiliary.CART_TOTAL,Integer.toString(cart_total));
                        bypassToLiveOrderStatus.putExtra(auxiliary.CART_DELIVERYCHARGE,Integer.toString(cart_deliveryCharge));
                        bypassToLiveOrderStatus.putExtra(auxiliary.CART_GRANDTOTAL,Integer.toString(cart_grandTotal));
                        startActivity(bypassToLiveOrderStatus);
                    }
                });
            }
            cartShopDetailsLL.setVisibility(View.GONE);
            cartDeliveryDetailsLL.setVisibility(View.GONE);
            cartItemsHeadingTV.setVisibility(View.GONE);
            storeIdInCart=auxiliarycart.getStoreIdInCartOGC(getApplicationContext());
            cartStoreProductRVLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
            cartStoreProductRVAdapter=new CartProductsAdapter(this,prodIdsInCart,storeIdInCart);
            cartStoreProductRV.setLayoutManager(cartStoreProductRVLayoutManager);
            cartStoreProductRV.setAdapter(cartStoreProductRVAdapter);
        } else{
            cartShopDetailsLL.setVisibility(View.GONE);
            cartDeliveryDetailsLL.setVisibility(View.GONE);
            cartItemsHeadingTV.setVisibility(View.GONE);
            cartEmtpyTV.setVisibility(View.VISIBLE);
            clearCartB.setVisibility(View.GONE);
            cartDeliveryChargesLL.setVisibility(View.GONE);
            cartTotalLL.setVisibility(View.GONE);
        }
        Log.i("CART","onCreate ends");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void reloadActivity(){
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    public void onMethodCallback_CartProductsAdapter() {
        reloadActivity();
    }

    @Override
    public void onMethodCallback_CheckoutBottomSheetBehavior(int cartItemCount, int cartItemTotalAmount, int cartDeliveryCharges) {
        if(cartItemCount>0){

            cart_total=cartItemTotalAmount;
            cart_deliveryCharge=cartDeliveryCharges;
            cart_grandTotal=cart_total+cart_deliveryCharge;

            cartSummary_CartNSV.setVisibility(View.VISIBLE);
            cartItemCount_CartTV.setText(Integer.toString(cartItemCount)+" items");
            cartTotalTV.setText(Integer.toString(cart_total));
            cartDeliveryChargesTV.setText(Integer.toString(cart_deliveryCharge));
            cartGrandTotalAmount_CartTV.setText(Integer.toString(cart_grandTotal));
        } else{
            cartSummary_CartNSV.setVisibility(View.GONE);
            cartDeliveryChargesLL.setVisibility(View.GONE);
            cartTotalLL.setVisibility(View.GONE);
        }
    }


}
