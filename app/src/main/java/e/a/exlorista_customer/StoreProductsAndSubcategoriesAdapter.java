package e.a.exlorista_customer;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by a on 4/5/2020.
 */

public class StoreProductsAndSubcategoriesAdapter extends
        RecyclerView.Adapter<StoreProductsAndSubcategoriesAdapter.StoreProductsAndSubcategoriesViewHolder> {

    private Context mContext;
    private String mCategorySelected;

    /*
    @Override
    public void onMethodCallback_StoreProductCategoriesAdapter(String categoryClicked) {
        Log.i("CONTROL","inside callback implementation of spasa");
        if(!this.mCategorySelected.toLowerCase().equals(categoryClicked.toLowerCase())){
            this.mCategorySelected=categoryClicked;
            Log.i("CONTROL","category set to new");
            notifyDataSetChanged();
        }
    }
    */


    static class StoreProductsAndSubcategoriesViewHolder extends RecyclerView.ViewHolder{
        TextView mStoreProductSubcategoryTV;
        RecyclerView mStoreProductRV;
        RecyclerView.Adapter mStoreProductRVAdapter;
        RecyclerView.LayoutManager mStoreProductRVLayoutManager;

        StoreProductsAndSubcategoriesViewHolder(View itemView) {
            super(itemView);
            mStoreProductSubcategoryTV=itemView.findViewById(R.id.storeProductSubcategoryTV);
            mStoreProductRV=itemView.findViewById(R.id.storeProductRV);
        }

        void loadStoreProductsAdapter(String currentSubcategory,Context context,auxiliaryasyncs mAuxasync){
            mStoreProductRVLayoutManager=new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            mStoreProductRVAdapter=new StoreProductsAdapter(context,currentSubcategory);
            mStoreProductRV.setLayoutManager(mStoreProductRVLayoutManager);
            mStoreProductRV.setAdapter(mStoreProductRVAdapter);
        }

    }

    StoreProductsAndSubcategoriesAdapter(Context context, String categorySelected) {
        this.mContext = context;
        //this.mAuxasync = auxasync;
        this.mCategorySelected=categorySelected;
        //Log.i("CONTROL","Category selected(constructor) : "+this.mCategorySelected);
    }

    @Override
    public StoreProductsAndSubcategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.storeproductsandsubcategories, parent, false);
        return new StoreProductsAndSubcategoriesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StoreProductsAndSubcategoriesViewHolder holder, int position) {
        //Log.i("CONTROL","subcat OBVH");
        //Log.i("CONTROL","subcat pos: "+Integer.toString(position));
        String current_subcat=store.mAuxasync.getStoreSubcategoryInCategory(position,this.mCategorySelected);
        if(current_subcat!=null){
            //Log.i("CONTROL","Current subcat: "+current_subcat);
            holder.mStoreProductSubcategoryTV.setText(current_subcat);
            holder.loadStoreProductsAdapter(current_subcat,this.mContext,store.mAuxasync);
        } else {
            //Log.i("CONTROL","Current subcat: NULL");
            //Log.i("NullPointerException","getStoreSubcategoryInCategory raised npe");
        }
    }

    @Override
    public int getItemCount() {
        //Log.i("CONTROL","Category selected : "+this.mCategorySelected);
        return store.mAuxasync.getStoreSubcategoriesCountInCategory(this.mCategorySelected);
    }

    void categoryChanged(String new_category){
        this.mCategorySelected=new_category;
        notifyDataSetChanged();
    }

}
