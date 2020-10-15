package e.a.exlorista_customer;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by a on 4/27/2020.
 */

public class CartProductsAdapter extends RecyclerView.Adapter<CartProductsAdapter.CartProductsViewHolder>{
    private Context mContext;
    private ArrayList<String> mProdIdsInCart;
    private String mStoreIdInCart;
    private CartProductsAdapterCallback cartProductsAdapterCallback;

    static class CartProductsViewHolder extends RecyclerView.ViewHolder{
        ImageView mCartProductIV;
        TextView mCartProductBrandTV, mCartProductNameTV, mCartProductSizeUnitContainerTV,
                mCartProductMrpTV, mCartProductPriceTV, mCartThisProdCountTV, mCartThisItemTotalCostTV;
        Button mCartAddOneMoreProdB, mCartRemoveOneProdB;

        CartProductsViewHolder(View itemView) {
            super(itemView);
            mCartProductIV=itemView.findViewById(R.id.cartProductIV);
            mCartProductBrandTV=itemView.findViewById(R.id.cartProductBrandTV);
            mCartProductNameTV=itemView.findViewById(R.id.cartProductNameTV);
            mCartProductSizeUnitContainerTV=itemView.findViewById(R.id.cartProductSizeUnitContainerTV);
            mCartProductMrpTV=itemView.findViewById(R.id.cartProductMrpTV);
            mCartProductPriceTV=itemView.findViewById(R.id.cartProductPriceTV);
            mCartAddOneMoreProdB=itemView.findViewById(R.id.cartAddOneMoreProdB);
            mCartRemoveOneProdB=itemView.findViewById(R.id.cartRemoveOneProdB);
            mCartThisProdCountTV=itemView.findViewById(R.id.cartThisProdCountIV);
            mCartThisItemTotalCostTV=itemView.findViewById(R.id.cartThisItemTotalCostTV);
        }
    }

    CartProductsAdapter(Context context, ArrayList<String> prodIdsInCart, String storeIdInCart) {
        //Log.i("CART PRODUCTSADAPTER","Inside constructor");
        this.mContext = context;
        this.mProdIdsInCart=prodIdsInCart;
        this.mStoreIdInCart=storeIdInCart;
        this.cartProductsAdapterCallback=((CartProductsAdapterCallback) mContext);
        int cartTotalAmount=auxiliarycart.getCartTotalAmount(mContext,mProdIdsInCart);
        int cartDeliveryCharges=auxiliarycart.getCartDeliveryCharges(cartTotalAmount);
        cartProductsAdapterCallback.onMethodCallback_CheckoutBottomSheetBehavior(mProdIdsInCart.size()
                ,cartTotalAmount,cartDeliveryCharges);
        //Log.i("CART PRODUCTSADAPTER","constructor ends");
    }

    @Override
    public CartProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.cartproducts, parent, false);
        return new CartProductsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CartProductsViewHolder holder, final int position) {
        //Log.i("CART PRODUCTSADAPTER","Inside OBVH");
        final String prod_id=mProdIdsInCart.get(holder.getAdapterPosition());
        final String[] IHMK_vals=auxiliarycart.getCartProdInternalHashMapAllValsOGC(mContext,prod_id);

        /*
        Bitmap prod_image_Bitmap=auxiliary.StringToBitMap(IHMK_vals[0]);
        String prodbrand_name=IHMK_vals[1];
        String proddet_name=IHMK_vals[2];
        String prodsizeunitcontainer=IHMK_vals[3];
        String prod_mrp_S=IHMK_vals[4];
        String prod_storeprice_S=IHMK_vals[5];
        String this_prod_count=IHMK_vals[6];

        holder.mCartProductIV.setImageBitmap(prod_image_Bitmap);
        holder.mCartProductBrandTV.setText(prodbrand_name);
        holder.mCartProductNameTV.setText(proddet_name);
        holder.mCartProductSizeUnitContainerTV.setText(prodsizeunitcontainer);
        holder.mCartProductMrpTV.setText(prod_mrp_S);
        holder.mCartProductPriceTV.setText(prod_storeprice_S);
        holder.mCartAddOneMoreProdB.setVisibility(View.VISIBLE);
        holder.mCartRemoveOneProdB.setVisibility(View.VISIBLE);
        holder.mCartThisProdCountTV.setVisibility(View.VISIBLE);
        holder.mCartThisProdCountTV.setText(this_prod_count);
        */

        holder.mCartProductIV.setImageBitmap(auxiliary.StringToBitMap(IHMK_vals[0]));
        holder.mCartProductBrandTV.setText(IHMK_vals[1]);
        holder.mCartProductNameTV.setText(IHMK_vals[2]);
        holder.mCartProductSizeUnitContainerTV.setText(IHMK_vals[3]);
        holder.mCartProductMrpTV.setText(IHMK_vals[4]);
        holder.mCartProductPriceTV.setText(IHMK_vals[5]);
        holder.mCartThisProdCountTV.setText(IHMK_vals[6]);
        String thisItemTotalCost_String=String.format("Rs %s",Integer.toString(
                Integer.parseInt(IHMK_vals[6])*Integer.parseInt(IHMK_vals[5].matches("\\d+")?IHMK_vals[5]:IHMK_vals[5].substring(3))
        ));
        holder.mCartThisItemTotalCostTV.setText(thisItemTotalCost_String);


        holder.mCartAddOneMoreProdB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String this_prodcount_now=auxiliarycart.incrementProdCountByOneOGC(mContext,prod_id);
                holder.mCartThisProdCountTV.setText(this_prodcount_now);
                String thisItemTotalCost_String=String.format("Rs %s",Integer.toString(
                        Integer.parseInt(this_prodcount_now)*Integer.parseInt(IHMK_vals[5].matches("\\d+")?IHMK_vals[5]:IHMK_vals[5].substring(3))
                ));
                holder.mCartThisItemTotalCostTV.setText(thisItemTotalCost_String);
                int cartTotalAmount=auxiliarycart.getCartTotalAmount(mContext,mProdIdsInCart);
                int cartDeliveryCharges=auxiliarycart.getCartDeliveryCharges(cartTotalAmount);
                cartProductsAdapterCallback.onMethodCallback_CheckoutBottomSheetBehavior(mProdIdsInCart.size(),
                        cartTotalAmount,cartDeliveryCharges);
            }
        });
        holder.mCartRemoveOneProdB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String this_prodcount_now=auxiliarycart.decrementProdCountByOneOGC(mContext,prod_id);
                if(this_prodcount_now.equals("0")){
                    notifyItemRemovedModified(holder.getAdapterPosition());
                    int cartTotalAmount=auxiliarycart.getCartTotalAmount(mContext,mProdIdsInCart);
                    int cartDeliveryCharges=auxiliarycart.getCartDeliveryCharges(cartTotalAmount);
                    cartProductsAdapterCallback.onMethodCallback_CheckoutBottomSheetBehavior(mProdIdsInCart.size(),
                            cartTotalAmount,cartDeliveryCharges);
                } else{
                    holder.mCartThisProdCountTV.setText(this_prodcount_now);
                    String thisItemTotalCost_String=String.format("Rs %s",Integer.toString(
                            Integer.parseInt(this_prodcount_now)*Integer.parseInt(IHMK_vals[5].matches("\\d+")?IHMK_vals[5]:IHMK_vals[5].substring(3))
                    ));
                    holder.mCartThisItemTotalCostTV.setText(thisItemTotalCost_String);
                    int cartTotalAmount=auxiliarycart.getCartTotalAmount(mContext,mProdIdsInCart);
                    int cartDeliveryCharges=auxiliarycart.getCartDeliveryCharges(cartTotalAmount);
                    cartProductsAdapterCallback.onMethodCallback_CheckoutBottomSheetBehavior(mProdIdsInCart.size(),
                            cartTotalAmount,cartDeliveryCharges);
                }
            }
        });
        //Log.i("CART PRODUCTSADAPTER","OBVH ends");
    }

    @Override
    public int getItemCount() {
        return mProdIdsInCart.size();
    }

    private void notifyItemRemovedModified(int pos_removed){
        //Log.i("CART","mProdIdsInCart size: "+Integer.toString(mProdIdsInCart.size()));
        //Log.i("CART","mProdIdsInCart index: "+Integer.toString(pos_removed));
        if(pos_removed!=-1){
            // -1 ? -> "-1" could never logically be the value of pos_removed
            // -1 occurs when you click at the recyclerview item while it is being removed through animation
            mProdIdsInCart.remove(pos_removed);
            notifyItemRemoved(pos_removed);
            //notifyItemRangeChanged(pos_removed,mProdIdsInCart.size());
            if(mProdIdsInCart.size()==0){
                cartProductsAdapterCallback.onMethodCallback_CartProductsAdapter();
            }
        }
    }

    public interface CartProductsAdapterCallback{
        void onMethodCallback_CartProductsAdapter();
        void onMethodCallback_CheckoutBottomSheetBehavior(int cartItemCount, int cartItemTotalAmount, int cartDeliveryCharges);
    }
}
