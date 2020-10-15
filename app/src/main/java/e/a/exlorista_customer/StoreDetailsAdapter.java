package e.a.exlorista_customer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by a on 1/13/2020.
 */

public class StoreDetailsAdapter extends RecyclerView.Adapter<StoreDetailsAdapter.StoreDetailViewHolder> {

    private Context mContext;
    private ArrayList<String> storeId;
    private ArrayList<String> storeName;
    private ArrayList<String> storeAddress;
    private ArrayList<String[]> storeTiming;
    private ArrayList<String> storeImgPath;
    private ArrayList<Bitmap> storeImg;
    private auxiliary aux;

    static class StoreDetailViewHolder extends RecyclerView.ViewHolder {

        TextView mStoreNameTV;
        TextView mStoreAddressTV;
        TextView mStoreTimingTV;
        ImageView mStoreImgIV;
        ConstraintLayout mStoreDetailCL;

        StoreDetailViewHolder(View itemView) {
            super(itemView);
            mStoreNameTV=itemView.findViewById(R.id.storeNameTV);
            mStoreAddressTV=itemView.findViewById(R.id.storeAddressTV);
            mStoreTimingTV=itemView.findViewById(R.id.storeTimingTV);
            mStoreImgIV=itemView.findViewById(R.id.storeImgIV);
            mStoreDetailCL=itemView.findViewById(R.id.storeDetailsCL);
        }
    }

    StoreDetailsAdapter(Context context) {
        this.mContext=context;
        this.storeId=new ArrayList<>();
        this.storeName=new ArrayList<>();
        this.storeAddress=new ArrayList<>();
        this.storeTiming=new ArrayList<>();
        this.storeImgPath=new ArrayList<>();
        this.storeImg=new ArrayList<>();
        this.aux=new auxiliary();
        this.getStoreDetails(auxiliary.SERVER_URL+"/fetchStoreDetails.php");
    }

    @Override
    public StoreDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.storedetails, parent, false);
        return new StoreDetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final StoreDetailViewHolder holder, final int position) {
        holder.mStoreNameTV.setText(storeName.get(holder.getAdapterPosition()));
        holder.mStoreAddressTV.setText(storeAddress.get(holder.getAdapterPosition()));
        Log.i("AIOOB exception",Integer.toString(currentDayIntCode()));
        Log.i("position",Integer.toString(holder.getAdapterPosition()));
        holder.mStoreTimingTV.setText(storeTiming.get(holder.getAdapterPosition())[currentDayIntCode()]);
        holder.mStoreImgIV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.mStoreImgIV.setImageBitmap(storeImg.get(holder.getAdapterPosition()));
        holder.mStoreDetailCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StoreDetailsAdapter.this.storeClicked(storeId.get(holder.getAdapterPosition()),
                        storeName.get(holder.getAdapterPosition()),
                        storeAddress.get(holder.getAdapterPosition()),
                        storeTiming.get(holder.getAdapterPosition()),
                        storeImgPath.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return storeImg.size();
    }

    private void getStoreDetails(final String urlWebService){
        class GetStoreDetails extends AsyncTask<Void, Void, Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    //Log.i("CONTROL", "inside doInBackground of GetGalleryImages");
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos=new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(aux.postParamsToString(new HashMap<String, String>(){
                        {
                            put(auxiliary.PPK_INITIAL_CHECK,auxiliary.PPV_INITIAL_CHECK);
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
                    if(!sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_FAIL) &&
                            !sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_NOT_SET)) {
                        //Log.i("CONTROL","PPK pass");
                        JSONArray jsonArray = new JSONArray(sb.toString().trim());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            storeId.add(obj.getString("store_id"));
                            String dirPath = obj.getString("dirpath_path");
                            String fileExt = obj.getString("fileext_name");
                            String filePath = dirPath + obj.getString("store_id") + "." + fileExt;
                            storeImgPath.add(filePath);
                            //Log.i("IMAGE PATH", filePath);
                            url = new URL(auxiliary.SERVER_URL + filePath);
                            con = (HttpURLConnection) url.openConnection();
                            con.setDoInput(true);
                            con.connect();
                            InputStream input = con.getInputStream();
                            storeImg.add(BitmapFactory.decodeStream(input));
                            storeName.add(obj.getString("store_name"));
                            storeAddress.add(obj.getString("area_name"));
                            storeTiming.add(getStoreTimingStringArray(obj));
                            /* Previous code when there was only one open and close timings
                            storeTiming.add(obj.getString("store_openTime")
                                    +" - "
                                    +obj.getString("store_closeTime"));*/
                            //Log.i("CONTROL", "store details set");
                        }
                    } else {
                        //Log.i("CONTROL","PPK failed or not set");
                    }
                } catch (JSONException e) {
                    //Log.i("exception","JSONException occurred in GetStoreDetails");
                    //e.printStackTrace();
                } catch (MalformedURLException mue){
                    //Log.i("exception","MalformedURLException occurred in GetStoreDetails");
                    //mue.printStackTrace();
                } catch (IOException io){
                    //Log.i("exception","IOException occurred in GetStoreDetails");
                    //io.printStackTrace();
                } catch (Exception e){
                    //Log.i("exception","Exception occurred in GetStoreDetails");
                    //e.printStackTrace();
                }
                return null;
            }
        }
        GetStoreDetails getStoreDetails=new GetStoreDetails();
        try{
            getStoreDetails.execute().get();
        } catch (InterruptedException ie){
            //Log.i("exception","InterruptionException occurred in GetStoreDetails");
            //ie.printStackTrace();
        } catch (ExecutionException ee){
            //Log.i("execution","ExecutionException occurred in GetStoreDetails");
            //ee.printStackTrace();
        }
    }

    private void storeClicked(String store_id,
                              String store_name,
                              String store_address,
                              String[] store_timing,
                              String store_imgPath){
        Intent intent=new Intent(this.mContext,store.class);
        intent.putExtra(auxiliary.STORE_ID,store_id);
        intent.putExtra(auxiliary.STORE_NAME,store_name);
        intent.putExtra(auxiliary.STORE_ADDRESS,store_address);
        intent.putExtra(auxiliary.STORE_TIMING,store_timing);
        intent.putExtra(auxiliary.STORE_IMGPATH,store_imgPath);
        this.mContext.startActivity(intent);
    }

    private String[] getStoreTimingStringArray(JSONObject obj){
        String[] storeTimingArr=new String[7];
        try{
            if(!obj.getString("store_openTimeMon").equalsIgnoreCase("null")
                    && !obj.getString("store_closeTimeMon").equalsIgnoreCase("null")){
                storeTimingArr[auxiliary.MON_INT]=obj.getString("store_openTimeMon")
                        +" - "
                        +obj.getString("store_closeTimeMon");
            } else{
                storeTimingArr[auxiliary.MON_INT]="Closed";
            }
            if(!obj.getString("store_openTimeTue").equalsIgnoreCase("null")
                    && !obj.getString("store_closeTimeTue").equalsIgnoreCase("null")){
                storeTimingArr[auxiliary.TUE_INT]=obj.getString("store_openTimeTue")
                        +" - "
                        +obj.getString("store_closeTimeTue");
            } else{
                storeTimingArr[auxiliary.TUE_INT]="Closed";
            }
            if(!obj.getString("store_openTimeWed").equalsIgnoreCase("null")
                    && !obj.getString("store_closeTimeWed").equalsIgnoreCase("null")){
                storeTimingArr[auxiliary.WED_INT]=obj.getString("store_openTimeWed")
                        +" - "
                        +obj.getString("store_closeTimeWed");
            } else{
                storeTimingArr[auxiliary.WED_INT]="Closed";
            }
            if(!obj.getString("store_openTimeThu").equalsIgnoreCase("null")
                    && !obj.getString("store_closeTimeThu").equalsIgnoreCase("null")){
                storeTimingArr[auxiliary.THU_INT]=obj.getString("store_openTimeThu")
                        +" - "
                        +obj.getString("store_closeTimeThu");
            } else{
                storeTimingArr[auxiliary.THU_INT]="Closed";
            }
            if(!obj.getString("store_openTimeFri").equalsIgnoreCase("null")
                    && !obj.getString("store_closeTimeFri").equalsIgnoreCase("null")){
                storeTimingArr[auxiliary.FRI_INT]=obj.getString("store_openTimeFri")
                        +" - "
                        +obj.getString("store_closeTimeFri");
            } else{
                storeTimingArr[auxiliary.FRI_INT]="Closed";
            }
            if(!obj.getString("store_openTimeSat").equalsIgnoreCase("null")
                    && !obj.getString("store_closeTimeSat").equalsIgnoreCase("null")){
                storeTimingArr[auxiliary.SAT_INT]=obj.getString("store_openTimeSat")
                        +" - "
                        +obj.getString("store_closeTimeSat");
            } else{
                storeTimingArr[auxiliary.SAT_INT]="Closed";
            }
            if(!obj.getString("store_openTimeSun").equalsIgnoreCase("null")
                    && !obj.getString("store_closeTimeSun").equalsIgnoreCase("null")){
                storeTimingArr[auxiliary.SUN_INT]=obj.getString("store_openTimeSun")
                        +" - "
                        +obj.getString("store_closeTimeSun");
            } else{
                storeTimingArr[auxiliary.SUN_INT]="Closed";
            }
        } catch (JSONException je){
            je.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return storeTimingArr;
    }

    private int currentDayIntCode(){
        /* java.util.Calendar has days of week indexed from 1 (Sunday) to 7 (Saturday).
        *  The days of week in auxiliary.java is indexed from 0 (Monday) to 6 (Sunday).
        *  Therefore, Calendar's days of week index is to be mapped to auxiliary.java days of week index.
        *  This method does the aforementioned mapping by
        *  fetching the index of current day from java.util.Calendar and returning days of week index as in auxiliary.java*/
        return ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2)%7+7)%7;
    }

}
