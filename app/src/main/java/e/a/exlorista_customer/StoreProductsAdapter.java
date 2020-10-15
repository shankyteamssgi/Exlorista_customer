package e.a.exlorista_customer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by a on 4/5/2020.
 */

public class StoreProductsAdapter extends
        RecyclerView.Adapter<StoreProductsAdapter.StoreProductsViewHolder> {

    private Context mContext;
    private String mCurrentSubcategory;
    private StoreProductsSelectedAdapterCallback storeProductsSelectedAdapterCallback;

    static class StoreProductsViewHolder extends RecyclerView.ViewHolder{
        ImageView mStoreProductIV;
        TextView mStoreProductBrandTV, mStoreProductNameTV, mStoreProductSizeUnitContainerTV,
                mStoreProductMrpTV, mStoreProductPriceTV, mThisProdCountTV;
        Button mAddProdB, mAddOneMoreProdB, mRemoveOneProdB;
        StoreProductsViewHolder(View itemView) {
            super(itemView);

            mStoreProductIV=itemView.findViewById(R.id.storeProductIV);
            mStoreProductBrandTV=itemView.findViewById(R.id.storeProductBrandTV);
            mStoreProductNameTV=itemView.findViewById(R.id.storeProductNameTV);
            mStoreProductSizeUnitContainerTV=itemView.findViewById(R.id.storeProductSizeUnitContainerTV);
            mStoreProductMrpTV=itemView.findViewById(R.id.storeProductMrpTV);
            mStoreProductPriceTV=itemView.findViewById(R.id.storeProductPriceTV);
            mThisProdCountTV=itemView.findViewById(R.id.thisProdCountTV);
            mAddProdB=itemView.findViewById(R.id.addProdB);
            mAddOneMoreProdB=itemView.findViewById(R.id.addOneMoreProdB);
            mRemoveOneProdB=itemView.findViewById(R.id.removeOneProdB);
        }
    }

    StoreProductsAdapter(Context context, String currentSubcategory) {
        this.mContext = context;
        this.mCurrentSubcategory=currentSubcategory;
        this.storeProductsSelectedAdapterCallback=((StoreProductsSelectedAdapterCallback) mContext);
        storeProductsSelectedAdapterCallback.onMethodCallback_ProceedToCartBottomSheetBehavior();
        //auxiliarycart.clearCart(this.mContext);
    }

    @Override
    public StoreProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.storeproducts, parent, false);
        return new StoreProductsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final StoreProductsViewHolder holder, int position) {
        //[prodbrand_name, proddet_name, prodsizeunitcontainer, prod_mrp, storeprodlist_storeprice]
        final String[] prod_attr_val;
        prod_attr_val=setValsToProdAttrs(holder,position);
        final String prodId=store.mAuxasync.getProdId(holder.getAdapterPosition(),mCurrentSubcategory);
        //final Bitmap prodImage=mAuxasync.getStoreProductImage(holder.getAdapterPosition(),mCurrentSubcategory);
        //final String prodImageString=auxiliary.BitMapToString(prodImage);
        //final String prodImageString=null;
        // prod_attr_val is passed to thisProdCountInCart only for debugging
        final String prodCountInCart=auxiliarycart.thisProdCountInCartOGC(mContext,prodId,store.storeId);
        if(prodCountInCart!=null){
            if(!prodCountInCart.equals("0")){
                //Log.i("CART","product name,SUC: "+prod_attr_val[1]+" "+prod_attr_val[2]+"("+prodCountInCart+")");
                // Change layout
                holder.mAddProdB.setVisibility(View.GONE);
                holder.mAddOneMoreProdB.setVisibility(View.VISIBLE);
                holder.mRemoveOneProdB.setVisibility(View.VISIBLE);
                holder.mThisProdCountTV.setVisibility(View.VISIBLE);
                holder.mThisProdCountTV.setText(prodCountInCart);
            } else if (prodCountInCart.equals("0")){
                //Log.i("CART","product name,SUC: "+prod_attr_val[1]+" "+prod_attr_val[2]+"(0)");
            }
        } else{
            //Log.i("CART","product name,SUC: "+prod_attr_val[1]+" "+prod_attr_val[2]+"(NULL)");
        }

        // Set onClickListeners to Cart Buttons
        holder.mAddProdB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(prod_attr_val!=null){
                    /*
                    auxiliarycart.addProdToCart(mContext,prodId
                            ,store.storeId,prod_attr_val,prodImageString);
                    */
                    auxiliarycart.addProdToCart(mContext,prodId
                            ,store.storeId,prod_attr_val,auxiliary
                                    .BitMapToStringOGC(store.mAuxasync
                                            .getStoreProductImage(holder.getAdapterPosition()
                                                    ,mCurrentSubcategory)));
                    // Change layout
                    holder.mAddProdB.setVisibility(View.GONE);
                    holder.mAddOneMoreProdB.setVisibility(View.VISIBLE);
                    holder.mRemoveOneProdB.setVisibility(View.VISIBLE);
                    holder.mThisProdCountTV.setVisibility(View.VISIBLE);
                    holder.mThisProdCountTV.setText("1");
                    storeProductsSelectedAdapterCallback.onMethodCallback_ProceedToCartBottomSheetBehavior();
                }
                else{
                    //Log.i("CONTROL","prod_attr_val is null");
                }
            }
        });
        holder.mAddOneMoreProdB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String this_prodcount_now=auxiliarycart
                        .incrementProdCountByOneOGC(mContext,prodId);
                // Change layout
                holder.mThisProdCountTV.setText(this_prodcount_now);
            }
        });
        holder.mRemoveOneProdB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String this_prodcount_now=auxiliarycart
                        .decrementProdCountByOneOGC(mContext,prodId);
                // Change layout
                holder.mThisProdCountTV.setText(this_prodcount_now);
                if(this_prodcount_now.equals("0")){
                    holder.mAddProdB.setVisibility(View.VISIBLE);
                    holder.mAddOneMoreProdB.setVisibility(View.GONE);
                    holder.mRemoveOneProdB.setVisibility(View.GONE);
                    holder.mThisProdCountTV.setVisibility(View.GONE);
                    storeProductsSelectedAdapterCallback.onMethodCallback_ProceedToCartBottomSheetBehavior();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return store.mAuxasync.getStoreProductsCountInSubcategory(this.mCurrentSubcategory);
    }

    private String[] setValsToProdAttrs(final StoreProductsViewHolder holder, final int position){
        class SetValsToProdAttrs extends AsyncTask<Void, Void, String[]>{
            private String[] prod_attr_val;
            
            @Override
            protected String[] doInBackground(Void... voids) {

                //[prodbrand_name, proddet_name, prodsizeunitcontainer, prod_mrp, storeprodlist_storeprice]
                prod_attr_val=new String[5];

                // Product Brand -> prodbrand_name
                prod_attr_val[0]=store.mAuxasync.getStoreProductAttrVal
                        (position,"prodbrand_name",mCurrentSubcategory);

                // Product Name -> proddet_name
                prod_attr_val[1]=store.mAuxasync.getStoreProductAttrVal
                        (position,"proddet_name",mCurrentSubcategory);

                // Product size+unit+container -> prod_size, produnit_name, prodcontain_name
                String prod_size=store.mAuxasync.getStoreProductAttrVal
                        (position,"prod_size",mCurrentSubcategory);
                String produnit_name=store.mAuxasync.getStoreProductAttrVal
                        (position,"produnit_name",mCurrentSubcategory);
                String prodcontain_name=store.mAuxasync.getStoreProductAttrVal
                        (position,"prodcontain_name",mCurrentSubcategory);
                prod_attr_val[2]=prod_size + " " + produnit_name + " " + prodcontain_name;

                // Product MRP -> prod_mrp
                prod_attr_val[3]=store.mAuxasync.getStoreProductAttrVal
                        (position,"prod_mrp",mCurrentSubcategory);

                // Product store price -> storeprodlist_storeprice
                prod_attr_val[4]=store.mAuxasync.getStoreProductAttrVal
                        (position,"storeprodlist_storeprice",mCurrentSubcategory);
                return prod_attr_val;
            }

            @Override
            protected void onPostExecute(String[] prod_attr_val) {
                super.onPostExecute(prod_attr_val);
                //Log.i("prodbrand_name",prod_attr_val[0]);
                holder.mStoreProductBrandTV.setText(prod_attr_val[0]);

                //Log.i("CONTROL proddet_name",prod_attr_val[1]);
                holder.mStoreProductNameTV.setText(prod_attr_val[1]);

                //Log.i("size_unit_container",prod_attr_val[2]);
                holder.mStoreProductSizeUnitContainerTV.setText(prod_attr_val[2]);

                //Log.i("prod_mrp",prod_attr_val[3]);
                //holder.mStoreProductMrpTV.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                //holder.mStoreProductMrpTV.setText(prod_attr_val[3]);
                String prod_mrp="MRP: "+prod_attr_val[3];
                holder.mStoreProductMrpTV.setText(prod_mrp, TextView.BufferType.SPANNABLE);
                Spannable spannable=(Spannable) holder.mStoreProductMrpTV.getText();
                spannable.setSpan(new StrikethroughSpan(),5,prod_mrp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                //Log.i("store_price",prod_attr_val[4]);
                holder.mStoreProductPriceTV.setText(prod_attr_val[4]);

                // setting bitmap to imageview
                holder.mStoreProductIV.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.mStoreProductIV.setImageBitmap(store.mAuxasync.getStoreProductImage(position,mCurrentSubcategory));
            }
            private String[] getProdAttrVal(){
                return prod_attr_val;
            }
        }
        SetValsToProdAttrs setValsToProdAttrs=new SetValsToProdAttrs();
        try{
            setValsToProdAttrs.execute().get();
            return setValsToProdAttrs.getProdAttrVal();
        } catch (InterruptedException ie){
            //ie.printStackTrace();
        } catch (ExecutionException ee){
            //ee.printStackTrace();
        }
        return null;
    }

    public interface StoreProductsSelectedAdapterCallback{
        void onMethodCallback_ProceedToCartBottomSheetBehavior();
    }

}
