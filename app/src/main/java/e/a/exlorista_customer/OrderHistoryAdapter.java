package e.a.exlorista_customer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.concurrent.ExecutionException;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>{

    private Context mContext;
    private ArrayList<String> orderId;
    private ArrayList<String> orderStoreName;
    private ArrayList<String> orderStoreArea;
    private ArrayList<String> orderStatus;
    private ArrayList<String> paymMethod;
    private ArrayList<String> custaddrAddress;
    private ArrayList<String> custaddrArea;
    private ArrayList<String> custaddrLandmark;
    private ArrayList<String> custaddrCity;
    private ArrayList<ArrayList<String>> productName;
    private ArrayList<ArrayList<String>> productSize;
    private ArrayList<ArrayList<String>> productUnit;
    private ArrayList<ArrayList<String>> productContainer;
    private ArrayList<ArrayList<String>> productCount;
    private ArrayList<ArrayList<String>> productStatus;
    private ArrayList<ArrayList<String>> productAvailabilityStatus;
    private ArrayList<ArrayList<String>> productMrp;
    private ArrayList<ArrayList<String>> productStoreprice;
    private ArrayList<String> orderDate;
    private ArrayList<String> orderTime;
    private ArrayList<String> orderTotal;
    private ArrayList<String> orderDeliveryCharge;
    private ArrayList<String> orderGrandTotal;
    private ArrayList<String> orderRating;

    static class OrderHistoryViewHolder extends RecyclerView.ViewHolder{
        TextView mStoreNameOrderHistoryTV;
        TextView mStoreAddressOrderHistoryTV;
        TextView mOrderStatusOrderHistoryTV;
        //LinearLayout mItemsListOrderHistoryLL;
        TextView mItemsListOrderHistoryTV;
        TextView mOrderDateTimeOrderHistoryTV;
        TextView mOrderGrandTotalOrderHistoryTV;
        Button mOrderActionOrderHistoryB;

        public OrderHistoryViewHolder(View itemView) {
            super(itemView);
            mStoreNameOrderHistoryTV=itemView.findViewById(R.id.storeNameOrderHistoryTV);
            mStoreAddressOrderHistoryTV=itemView.findViewById(R.id.storeAddressOrderHistoryTV);
            mOrderStatusOrderHistoryTV=itemView.findViewById(R.id.orderStatusOrderHistoryTV);
            //mItemsListOrderHistoryLL=itemView.findViewById(R.id.itemsListOrderHistoryLL);
            mItemsListOrderHistoryTV=itemView.findViewById(R.id.itemsListOrderHistoryTV);
            mOrderDateTimeOrderHistoryTV=itemView.findViewById(R.id.orderDateTimeOrderHistoryTV);
            mOrderGrandTotalOrderHistoryTV=itemView.findViewById(R.id.orderGrandTotalOrderHistoryTV);
            mOrderActionOrderHistoryB=itemView.findViewById(R.id.orderActionOrderHistoryB);
        }
    }

    public OrderHistoryAdapter(Context context) {
        this.mContext = context;
        this.orderId=new ArrayList<String>();
        this.orderStoreName=new ArrayList<String>();
        this.orderStoreArea=new ArrayList<String>();
        this.orderStatus=new ArrayList<String>();
        this.paymMethod=new ArrayList<String>();
        this.custaddrAddress=new ArrayList<String>();
        this.custaddrArea=new ArrayList<String>();
        this.custaddrLandmark=new ArrayList<String>();
        this.custaddrCity=new ArrayList<String>();
        this.productName=new ArrayList<ArrayList<String>>();
        this.productSize=new ArrayList<ArrayList<String>>();
        this.productUnit=new ArrayList<ArrayList<String>>();
        this.productContainer=new ArrayList<ArrayList<String>>();
        this.productCount=new ArrayList<ArrayList<String>>();
        this.productStatus=new ArrayList<ArrayList<String>>();
        this.productAvailabilityStatus=new ArrayList<ArrayList<String>>();
        this.productMrp=new ArrayList<ArrayList<String>>();
        this.productStoreprice=new ArrayList<ArrayList<String>>();
        this.orderDate=new ArrayList<String>();
        this.orderTime=new ArrayList<String>();
        this.orderTotal=new ArrayList<String>();
        this.orderDeliveryCharge=new ArrayList<String>();
        this.orderGrandTotal=new ArrayList<String>();
        this.orderRating=new ArrayList<String>();
        this.getOrderHistory(auxiliary.SERVER_URL+"/fetchOrderHistory.php");
    }

    @NonNull
    @Override
    public OrderHistoryAdapter.OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.orderhistory,parent,false);
        return new OrderHistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryAdapter.OrderHistoryViewHolder holder, int position) {
        holder.mStoreNameOrderHistoryTV.setText(orderStoreName.get(holder.getAdapterPosition()));
        holder.mStoreAddressOrderHistoryTV.setText(orderStoreArea.get(holder.getAdapterPosition()));
        holder.mOrderStatusOrderHistoryTV.setText(orderStatus.get(holder.getAdapterPosition()));
        holder.mItemsListOrderHistoryTV.setText(Integer.toString(productCount.get(holder.getAdapterPosition()).size())+" items");
        holder.mOrderDateTimeOrderHistoryTV.setText(orderTime.get(holder.getAdapterPosition())+" "+orderDate.get(holder.getAdapterPosition()));
        holder.mOrderGrandTotalOrderHistoryTV.setText(orderGrandTotal.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return orderId.size();
    }

    private void getOrderHistory(final String urlWebService){
        class GetOrderHistory extends AsyncTask<Void,Void,Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos=new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>(){
                        {
                            put(auxiliary.PPK_INITIAL_CHECK,auxiliary.PPV_INITIAL_CHECK);
                            put(auxiliary.PPK_CUSTID,auxiliary.DUMMYVAL_CUSTID);
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
                            JSONObject jsonObject=jsonArray.getJSONObject(i);
                            orderId.add(jsonObject.getString("_order_id"));
                            orderStoreName.add(jsonObject.getString("store_name"));
                            orderStoreArea.add(jsonObject.getString("store_area"));
                            orderStatus.add(jsonObject.getString("orderstat_name"));
                            orderDate.add(jsonObject.getString("_order_date"));
                            orderTime.add(jsonObject.getString("_order_time"));
                            orderGrandTotal.add(jsonObject.getString("_order_grandTotal"));
                            paymMethod.add(jsonObject.getString("paym_method"));
                            custaddrAddress.add(jsonObject.getString("custaddr_address"));
                            custaddrArea.add(jsonObject.getString("customeraddress_area"));
                            custaddrLandmark.add(jsonObject.getString("custaddr_landmark"));
                            custaddrCity.add(jsonObject.getString("city_name"));
                            productName.add(new ArrayList<String>());
                            productSize.add(new ArrayList<String>());
                            productUnit.add(new ArrayList<String>());
                            productContainer.add(new ArrayList<String>());
                            productCount.add(new ArrayList<String>());
                            productStatus.add(new ArrayList<String>());
                            productAvailabilityStatus.add(new ArrayList<String>());
                            productMrp.add(new ArrayList<String>());
                            productStoreprice.add(new ArrayList<String>());
                            int prod_count_diff=jsonObject.getJSONArray("proddet_name").length();
                            for(int j=0;j<prod_count_diff;j++){
                                productName.get(i).add(jsonObject.getJSONArray("proddet_name").getString(j));
                                productSize.get(i).add(jsonObject.getJSONArray("proddet_name").getString(j));
                                productUnit.get(i).add(jsonObject.getJSONArray("proddet_name").getString(j));
                                productContainer.get(i).add(jsonObject.getJSONArray("proddet_name").getString(j));
                                productCount.get(i).add(jsonObject.getJSONArray("proddet_name").getString(j));
                                productStatus.get(i).add(jsonObject.getJSONArray("proddet_name").getString(j));
                                productAvailabilityStatus.get(i).add(jsonObject.getJSONArray("proddet_name").getString(j));
                                productMrp.get(i).add(jsonObject.getJSONArray("proddet_name").getString(j));
                                productStoreprice.get(i).add(jsonObject.getJSONArray("proddet_name").getString(j));
                            }
                        }
                        Log.i("order id arraylist size",Integer.toString(orderId.size()));
                    } else {
                        Log.i("CONTROL","PPK failed or not set");
                    }
                } catch (JSONException e) {
                    Log.i("exception","JSONException in OrderHistoryAdapter");
                    e.printStackTrace();
                } catch (MalformedURLException mue){
                    Log.i("exception","MalformedURLException in OrderHistoryAdapter");
                    mue.printStackTrace();
                } catch (IOException io){
                    Log.i("exception","IOException in OrderHistoryAdapter");
                    io.printStackTrace();
                } catch (Exception e){
                    Log.i("exception","Exception in OrderHistoryAdapter");
                    e.printStackTrace();
                }
                return null;
            }
        }
        GetOrderHistory getOrderHistory=new GetOrderHistory();
        try {
            getOrderHistory.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
