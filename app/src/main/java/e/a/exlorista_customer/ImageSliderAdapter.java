package e.a.exlorista_customer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Created by a on 1/6/2020.
 */

public class ImageSliderAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<Bitmap> mBitmaps;
    private LayoutInflater layoutInflater;
    private auxiliary aux;

    ImageSliderAdapter(Context context){
        this.mContext=context;
        this.aux=new auxiliary();
        this.mBitmaps=new ArrayList<>();
        this.getGalleryImages(auxiliary.SERVER_URL+"/fetchGalleryImages.php");
    }

    @Override
    public int getCount() {
        //Log.i("bitmap arraylist size",Integer.toString(mBitmaps.size()));
        return mBitmaps.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.imageslider,null);
        ImageView imageView=view.findViewById(R.id.imgSliderIV);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(mBitmaps.get(position));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        ViewPager viewPager=(ViewPager) container;
        viewPager.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager=(ViewPager) container;
        View view=(View) object;
        viewPager.removeView(view);
    }

    private void getGalleryImages(final String urlWebService){
        class GetGalleryImages extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    //Log.i("CONTROL", "inside doInBackground of GetGalleryImages");
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    //con.setRequestProperty("Accept-Charset", "UTF-8");
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
                            String storeId = obj.getString("store_id");
                            String adspaceId = obj.getString("adspace_id");
                            String dirPath = obj.getString("dirpath_path");
                            String fileExt = obj.getString("fileext_name");
                            //Log.i("IMAGE PATH", dirPath + storeId + "_" + adspaceId + "." + fileExt);
                            url = new URL(auxiliary.SERVER_URL + dirPath + storeId + "_" + adspaceId + "." + fileExt);
                            con = (HttpURLConnection) url.openConnection();
                            con.setDoInput(true);
                            con.connect();
                            InputStream input = con.getInputStream();
                            mBitmaps.add(BitmapFactory.decodeStream(input));
                            //Log.i("CONTROL", "bitmaps set");
                        }
                    } else {
                        //Log.i("CONTROL","PPK failed or not set");
                    }
                } catch (JSONException e) {
                    //Log.i("exception","JSONException occurred in GetGalleryImagesPathJSON");
                    //e.printStackTrace();
                } catch (MalformedURLException mue){
                    //Log.i("exception","MalformedURLException occurred in GetGalleryImagesPathJSON");
                    //mue.printStackTrace();
                } catch (IOException io){
                    //Log.i("exception","IOException occurred in GetGalleryImagesPathJSON");
                    //io.printStackTrace();
                } catch (Exception e){
                    //Log.i("exception","Exception occurred in GetGalleryImagesPathJSON");
                    //e.printStackTrace();
                }
                return null;
            }
        }
        GetGalleryImages getGalleryImages=new GetGalleryImages();
        try{
            getGalleryImages.execute().get();
        } catch (InterruptedException ie){
            //Log.i("exception","InterruptionException occurred");
            //ie.printStackTrace();
        } catch (ExecutionException ee){
            //Log.i("execution","ExecutionException occurred");
            //ee.printStackTrace();
        }

    }

}
