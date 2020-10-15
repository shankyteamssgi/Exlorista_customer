package e.a.exlorista_customer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by a on 3/28/2020.
 */

public class StoreProductCategoriesAdapter extends
        RecyclerView.Adapter<StoreProductCategoriesAdapter.StoreProductCategoriesViewHolder> {

    //private auxiliaryasyncs mAuxasync;
    private storeProductCategoriesAdapterCallback mCallBackChangeText;
    private int mPositionCurrentlySelected;
    private Context mContext;
    //private boolean clickPerformed;

    static class StoreProductCategoriesViewHolder extends RecyclerView.ViewHolder{
        Button mStoreProductCategoryB;

        StoreProductCategoriesViewHolder(View itemView) {
            super(itemView);
            mStoreProductCategoryB=itemView.findViewById(R.id.storeProductCategoryB);
        }
    }

    StoreProductCategoriesAdapter(Context contextStore) {
        this.mContext = contextStore;
        //this.mAuxasync=auxasync;
        this.mPositionCurrentlySelected=-1;
        //this.clickPerformed=false;
        try{
            this.mCallBackChangeText=((storeProductCategoriesAdapterCallback)contextStore);
        } catch (ClassCastException cce){
            //cce.printStackTrace();
        }
    }

    @Override
    public StoreProductCategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.i("CONTROL","inside onCreateViewHolder of StoreProductCategoriesAdapter");
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.storeproductcategories, parent, false);
        return new StoreProductCategoriesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final StoreProductCategoriesViewHolder holder, final int position) {
        //Log.i("CONTROL", "inside onBindViewHolder of StoreProductCategoriesAdapter");
        //Log.i("pos,adapterpos",Integer.toString(position)+","+Integer.toString(holder.getAdapterPosition()));
        holder.mStoreProductCategoryB.setText(store.mAuxasync.getStoreProductCategory(position));
        holder.mStoreProductCategoryB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.i("pos,adapterpos(onclk)",Integer.toString(position)+","+Integer.toString(holder.getAdapterPosition()));
                mPositionCurrentlySelected=holder.getAdapterPosition();
                notifyDataSetChanged();
                mCallBackChangeText
                        .onMethodCallback_StoreProductCategoriesAdapter(holder.mStoreProductCategoryB.getText().toString());
            }
        });

        // Color change logic
        if(mPositionCurrentlySelected==holder.getAdapterPosition()){
            // this is the selected item : change to desired color
            holder.mStoreProductCategoryB.setTextColor(ContextCompat.getColor(this.mContext,R.color.colorActionBarBackground));
        } else{
            // this item is not selected : reset to default color
            holder.mStoreProductCategoryB.setTextColor(ContextCompat.getColor(this.mContext,R.color.colorBodyText));
        }
    }

    @Override
    public int getItemCount() {return store.mAuxasync.getStoreProductCategoryCount();}

    public interface storeProductCategoriesAdapterCallback{
        void onMethodCallback_StoreProductCategoriesAdapter(String categoryClicked);
    }

}
