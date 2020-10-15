package e.a.exlorista_customer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by a on 4/4/2020.
 */

class auxiliaryasyncs {

    private ArrayList<String> storeProductCategories;
    private HashMap<String,ArrayList<String>> storeProductSubcategories;
    private HashMap<String,HashMap<String,ArrayList<String>>> storeProductDetail;
    private HashMap<String,ArrayList<String[]>> storeProductImgNamingInfo;
    private HashMap<String,ArrayList<Bitmap>> storeProductImages;
    private auxiliary aux;
    private String mStoreId;

    private final static String[] prod_attrs={
            "prod_id",
            "prodbrand_name",
            "proddet_name",
            "prod_size",
            "produnit_name",
            "prodcontain_name",
            "prod_mrp",
            "storeprodlist_storeprice"
    };

    auxiliaryasyncs(String mStoreId) {
        //Log.i("CONTROL","inside constructor of auxiliaryasyncs");
        this.mStoreId=mStoreId;
        this.storeProductCategories=new ArrayList<>();
        this.storeProductSubcategories=new HashMap<String, ArrayList<String>>();
        this.storeProductDetail=new HashMap<String, HashMap<String, ArrayList<String>>>();
        this.storeProductImgNamingInfo=new HashMap<String, ArrayList<String[]>>();
        this.storeProductImages=new HashMap<String, ArrayList<Bitmap>>();
        this.initializeStoreProductDetail();
        aux=new auxiliary();
        this.fetchStoreAssortment(auxiliary.SERVER_URL+"/fetchStoreAssortment.php",this.mStoreId);
        this.fetchStoreProductImages();
        this.printTest_DEBUG();
        this.storeProductCategories.add(0,"All");
        //Log.i("CONTROL","outside fetchStoreAssortment");
    }

    private void fetchStoreAssortment(final String urlWebService, final String mStoreId){
        class FetchStoreAssortment extends AsyncTask<Void, Void, Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                //Log.i("CONTROL","doInBackground of fetchStoreAssortment");
                try{
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(aux.postParamsToString(new HashMap<String, String>() {
                        {
                            put(auxiliary.PPK_INITIAL_CHECK, auxiliary.PPV_INITIAL_CHECK);
                            put(auxiliary.STORE_ID, mStoreId);
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
                    if (!sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_FAIL) &&
                            !sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_NOT_SET)) {
                        JSONObject jsonObject=new JSONObject(sb.toString().trim());

                        // 1) category
                        Iterator<String> category_jsonObj=jsonObject.getJSONObject("prodcat_name").keys();
                        while (category_jsonObj.hasNext()){
                            String current_category=category_jsonObj.next();
                            storeProductCategories.add(current_category);
                        }

                        // 2) subcategory
                        JSONObject subcategory_jsonObj=jsonObject.getJSONObject("prodsubcat_name");
                        for(String category : storeProductCategories){
                            JSONArray subcategory_jsonArr=subcategory_jsonObj.getJSONArray(category);
                            HashSet<String> unique_subcats=new HashSet<>();
                            for(int j=0;j<subcategory_jsonArr.length();j++){
                                unique_subcats.add(subcategory_jsonArr.getString(j));
                            }
                            storeProductSubcategories.put(category,new ArrayList<String>(unique_subcats));
                        }

                        // 3) other remaining attrs such as prodbrand_name, proddet_name, prod_mrp, etc
                        // except dirpath_path
                        for(String prod_attr: auxiliaryasyncs.prod_attrs){
                            //Log.i("prod_attr",prod_attr);

                            for(String category : storeProductCategories){
                                //Log.i("category",category);

                                //HashSet<String> subcategories=storeProductSubcategories.get(category);
                                ArrayList<String> subcategories=storeProductSubcategories.get(category);

                                for(String subcategory : subcategories){
                                    //Log.i("subcategory",subcategory);
                                    storeProductDetail.get(prod_attr).put(subcategory,new ArrayList<String>());
                                    JSONArray attr_jsonArr=jsonObject.getJSONObject(prod_attr).getJSONArray(subcategory);
                                    for(int l=0;l<attr_jsonArr.length();l++){
                                        //Log.i("l",Integer.toString(l));
                                        storeProductDetail.get(prod_attr).get(subcategory).add(attr_jsonArr.getString(l));
                                    }
                                }
                            }
                        }

                        // 4) product image naming details
                        for(String category : storeProductCategories){

                            ArrayList<String> subcategories=storeProductSubcategories.get(category);

                            for(String subcategory : subcategories){

                                JSONArray imgNamingDetail_jsonArr=jsonObject.getJSONObject("dirpath_path").getJSONArray(subcategory);
                                storeProductImgNamingInfo.put(subcategory,new ArrayList<String[]>());

                                for(int i=0;i<imgNamingDetail_jsonArr.length();i++){

                                    JSONArray imgNamingDetail_jsonArr2=imgNamingDetail_jsonArr.getJSONArray(i);
                                    storeProductImgNamingInfo.get(subcategory).add(new String[5]);
                                    for(int j=0;j<5;j++){
                                        storeProductImgNamingInfo.get(subcategory).get(i)[j]=imgNamingDetail_jsonArr2.getString(j);
                                    }
                                }
                            }
                        }

                        // 5) product image fetch


                    } else {
                        //Log.i("CONTROL","PPK failed or not set");
                    }
                } catch (MalformedURLException mue){
                    //mue.printStackTrace();
                } catch (JSONException je){
                    //je.printStackTrace();
                } catch (IOException io){
                    //io.printStackTrace();
                } catch (Exception e){
                    //e.printStackTrace();
                }
                return null;
            }
        }
        FetchStoreAssortment fetchStoreAssortment=new FetchStoreAssortment();
        try {
            fetchStoreAssortment.execute().get();
        } catch (InterruptedException ie) {
            //ie.printStackTrace();
        } catch (ExecutionException ee) {
            //ee.printStackTrace();
        }

    }

    private void fetchStoreProductImages(){
        class FetchStoreProductImages extends AsyncTask<Void, Void, Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    for(Map.Entry<String,ArrayList<String[]>> pair : storeProductImgNamingInfo.entrySet()){
                        String subcat=pair.getKey();
                        storeProductImages.put(subcat,new ArrayList<Bitmap>());
                        ArrayList<String[]> subcat_images=pair.getValue();
                        for(String[] img_info : subcat_images){
                            String img_filename="cat["+img_info[1]+"]_subcat["+img_info[2]+
                                    "]_brand["+img_info[3]+"]_proddet["+img_info[4]+"].jpg";
                            String filepath=img_info[0]+img_filename;
                            URL url=new URL(auxiliary.SERVER_URL+filepath);
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            con = (HttpURLConnection) url.openConnection();
                            con.setDoInput(true);
                            con.connect();
                            InputStream input = con.getInputStream();
                            storeProductImages.get(subcat).add(BitmapFactory.decodeStream(input));
                        }
                    }
                } catch (MalformedURLException mue){
                    //mue.printStackTrace();
                } catch (IOException io){
                    //io.printStackTrace();
                } catch (Exception e){
                    //e.printStackTrace();
                }
                return null;
            }
        }
        FetchStoreProductImages fetchStoreProductImages=new FetchStoreProductImages();
        try {
            fetchStoreProductImages.execute().get();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } catch (ExecutionException e) {
            //e.printStackTrace();
        }
    }

    String getStoreProductCategory(int position){

        // utility in StoreProductCategoriesAdapter.java

        return this.storeProductCategories.get(position);
    }

    int getStoreProductCategoryCount(){

        // utility in StoreProductCategoriesAdapter.java

        return this.storeProductCategories.size();
    }

    int getStoreProductsCountInSubcategory(String subcategory){

        // utility in StoreProductsAdapter.java

        return this.storeProductDetail.get("prodbrand_name").get(subcategory).size();
    }

    String getStoreProductAttrVal(int position, String product_attr, String subcategory){

        // utility in StoreProductsAdapter.java

        return this.storeProductDetail.get(product_attr).get(subcategory).get(position);
    }

    String getProdId(int position, String subcategory){

        // utility in StoreProductsAdapter.java

        return this.getStoreProductAttrVal(position,"prod_id",subcategory);
    }


    Bitmap getStoreProductImage(int position, String subcategory){
        return this.storeProductImages.get(subcategory).get(position);
    }

    int getStoreSubcategoriesCountInCategory(String category){

        // utility in StoreProductsAndSubcategoriesAdapter.java

        if(category.toLowerCase().equals("all".toLowerCase())){
            // any prod_attr can be placed instead of prodbrand_name
            // as any of the prod_attr will have equal number of items
            //Log.i("prod subcat count in "+category,Integer.toString(this.storeProductDetail.get("prodbrand_name").size()));
            return this.storeProductDetail.get("prodbrand_name").size();
        } else {
            //Log.i("prod subcat count in "+category,Integer.toString(this.storeProductSubcategories.get(category).size()));
            return this.storeProductSubcategories.get(category).size();
        }
    }

    String getStoreSubcategoryInCategory(int position, String category){

        // utility in StoreProductsAndSubcategoriesAdapter.java

        //Log.i("CONTROL","cat: "+category);
        if(category.toLowerCase().equals("all".toLowerCase())) {
            //Log.i("prod cat size",Integer.toString(this.storeProductCategories.size()));
            int i;
            try{
                for (i=1;i<this.storeProductCategories.size();i++) {
                    int subcatSize=this.storeProductSubcategories.get(this.storeProductCategories.get(i)).size();
                    if (position>=subcatSize) {
                        position-=subcatSize;
                    } else {
                        break;
                    }
                }
                //Log.i("CONTROL","subcat: "+this.storeProductSubcategories.get(this.storeProductCategories.get(i)).get(position));
                return this.storeProductSubcategories.get(this.storeProductCategories.get(i)).get(position);
            } catch (NullPointerException ne){
                //ne.printStackTrace();
            } catch (Exception e){
                //e.printStackTrace();
            }
        } else {
            //Log.i("CONTROL","subcat: "+this.storeProductSubcategories.get(category).get(position));
            return this.storeProductSubcategories.get(category).get(position);
        }
        return null;
    }

    private void initializeStoreProductDetail(){
        for (String i : auxiliaryasyncs.prod_attrs) {
            this.storeProductDetail.put(i, new HashMap<String, ArrayList<String>>());
        }
    }

    private void printTest_DEBUG(){
        /*
        StringBuilder output=new StringBuilder();
        for (String outer_key : auxiliaryasyncs.prod_attrs){
            Set<String> inner_key_set=this.storeProductDetail.get(outer_key).keySet();
            //Log.i("inner_key_set size",Integer.toString(inner_key_set.size()));
            for (String inner_key : inner_key_set){
                ArrayList<String> inner_val_arr=this.storeProductDetail.get(outer_key).get(inner_key);
                //Log.i("inner_val_arr size",Integer.toString(inner_val_arr.size()));
                for (String inner_val : inner_val_arr){
                    String temp=outer_key+"->"+inner_key+"->"+inner_val+"\n";
                    output.append(temp);
                    //Log.i("element",outer_key+"->"+inner_key+"->"+inner_val); this invokes chatty(identical lines)
                }
            }
        }
        Log.i("output element",output.toString().trim());
        */
        int count=0;
        for(String cat : this.storeProductSubcategories.keySet()){
            count+=this.storeProductSubcategories.get(cat).size();
            for(int i=0;i<this.storeProductSubcategories.get(cat).size();i++){
                //Log.i("CONTROL","subcat : "+this.storeProductSubcategories.get(cat).get(i));
            }
        }
        //Log.i("CONTROL","subcat fetched : "+Integer.toString(count));
    }

}
