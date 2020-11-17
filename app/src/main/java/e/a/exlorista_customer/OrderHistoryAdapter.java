package e.a.exlorista_customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder> {

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
    private String mOrderDate, mOrderTime;


    static class OrderHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView mStoreNameOrderHistoryTV;
        TextView mStoreAddressOrderHistoryTV;
        TextView mOrderStatusOrderHistoryTV;
        //LinearLayout mItemsListOrderHistoryLL;
        TextView mItemsListOrderHistoryTV;
        TextView mOrderDateTimeOrderHistoryTV;
        TextView mOrderGrandTotalOrderHistoryTV;
        Button mOrderActionOrderHistoryB;

        ConstraintLayout mOrderHistoryCL;

        public OrderHistoryViewHolder(View itemView) {
            super(itemView);
            mStoreNameOrderHistoryTV = itemView.findViewById(R.id.storeNameOrderHistoryTV);
            mStoreAddressOrderHistoryTV = itemView.findViewById(R.id.storeAddressOrderHistoryTV);
            mOrderStatusOrderHistoryTV = itemView.findViewById(R.id.orderStatusOrderHistoryTV);
            //mItemsListOrderHistoryLL=itemView.findViewById(R.id.itemsListOrderHistoryLL);
            mItemsListOrderHistoryTV = itemView.findViewById(R.id.itemsListOrderHistoryTV);
            mOrderDateTimeOrderHistoryTV = itemView.findViewById(R.id.orderDateTimeOrderHistoryTV);
            mOrderGrandTotalOrderHistoryTV = itemView.findViewById(R.id.orderGrandTotalOrderHistoryTV);
            mOrderActionOrderHistoryB = itemView.findViewById(R.id.orderActionOrderHistoryB);

            mOrderHistoryCL=itemView.findViewById(R.id.orderHistoryCL);
        }
    }

    public OrderHistoryAdapter(Context context) {
        this.mContext = context;
        this.orderId = new ArrayList<String>();
        this.orderStoreName = new ArrayList<String>();
        this.orderStoreArea = new ArrayList<String>();
        this.orderStatus = new ArrayList<String>();
        this.paymMethod = new ArrayList<String>();
        this.custaddrAddress = new ArrayList<String>();
        this.custaddrArea = new ArrayList<String>();
        this.custaddrLandmark = new ArrayList<String>();
        this.custaddrCity = new ArrayList<String>();
        this.productName = new ArrayList<ArrayList<String>>();
        this.productSize = new ArrayList<ArrayList<String>>();
        this.productUnit = new ArrayList<ArrayList<String>>();
        this.productContainer = new ArrayList<ArrayList<String>>();
        this.productCount = new ArrayList<ArrayList<String>>();
        this.productStatus = new ArrayList<ArrayList<String>>();
        this.productAvailabilityStatus = new ArrayList<ArrayList<String>>();
        this.productMrp = new ArrayList<ArrayList<String>>();
        this.productStoreprice = new ArrayList<ArrayList<String>>();
        this.orderDate = new ArrayList<String>();
        this.orderTime = new ArrayList<String>();
        this.orderTotal = new ArrayList<String>();
        this.orderDeliveryCharge = new ArrayList<String>();
        this.orderGrandTotal = new ArrayList<String>();
        this.orderRating = new ArrayList<String>();
        this.getOrderHistory(auxiliary.SERVER_URL + "/fetchOrderHistory.php");


    }


    private void customDateTimeFormat( String order_time) {

        Date date ;

        try {
            SimpleDateFormat df = new SimpleDateFormat("hh:mm");
            date = df.parse(order_time);
            mOrderTime = new SimpleDateFormat("hh:mm a").format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        orderTime.add(mOrderTime);

    }
    @SuppressLint("SimpleDateFormat")
    public void customDate(String order_date){
       Date date=null;
        try {
            // get Date
            java.util.Locale locale = java.util.Locale.US;
            SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-ddd",locale);

            format.setTimeZone(TimeZone.getDefault());
             date =format.parse(order_date);

            format  = new SimpleDateFormat("MMMM dd, yyyy");
            mOrderDate = format.format(date);


        }catch (Exception e){
            e.printStackTrace();
        }
        orderDate.add(mOrderDate);

    }

    @NonNull
    @Override
    public OrderHistoryAdapter.OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderhistory, parent, false);
        return new OrderHistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderHistoryAdapter.OrderHistoryViewHolder holder, int position) {
        holder.mStoreNameOrderHistoryTV.setText(orderStoreName.get(holder.getAdapterPosition()));
        holder.mStoreAddressOrderHistoryTV.setText(orderStoreArea.get(holder.getAdapterPosition()));
        holder.mOrderStatusOrderHistoryTV.setText(orderStatus.get(holder.getAdapterPosition()));
        holder.mItemsListOrderHistoryTV.setText(Integer.toString(productCount.get(holder.getAdapterPosition()).size()) + " items");
        holder.mOrderDateTimeOrderHistoryTV.setText(orderTime.get(holder.getAdapterPosition()) + " " + orderDate.get(holder.getAdapterPosition()));
        holder.mOrderGrandTotalOrderHistoryTV.setText(orderGrandTotal.get(holder.getAdapterPosition()));
        holder.mOrderHistoryCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderHistoryAdapter.this.orderHistoryClicked(orderId.get(holder.getAdapterPosition())
                        ,orderStatus.get(holder.getAdapterPosition())
                        ,orderTotal.get(holder.getAdapterPosition())
                        ,orderDeliveryCharge.get(holder.getAdapterPosition())
                        ,orderGrandTotal.get(holder.getAdapterPosition())
                        ,paymMethod.get(holder.getAdapterPosition())
                        ,orderDate.get(holder.getAdapterPosition())
                        ,orderTime.get(holder.getAdapterPosition())
                        ,custaddrAddress.get(holder.getAdapterPosition())+", "
                                +custaddrLandmark.get(holder.getAdapterPosition())+", "
                                +custaddrArea.get(holder.getAdapterPosition())+", "
                                +custaddrCity.get(holder.getAdapterPosition())
                        ,productName.get(holder.getAdapterPosition())
                        ,productSize.get(holder.getAdapterPosition())
                        ,productUnit.get(holder.getAdapterPosition())
                        ,productContainer.get(holder.getAdapterPosition())
                        ,productCount.get(holder.getAdapterPosition())
                        ,productStatus.get(holder.getAdapterPosition())
                        ,productAvailabilityStatus.get(holder.getAdapterPosition())
                        ,productMrp.get(holder.getAdapterPosition())
                        ,productStoreprice.get(holder.getAdapterPosition())
                        ,orderStoreName.get(holder.getAdapterPosition())
                        ,orderStoreArea.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderId.size();
    }

    private void getOrderHistory(final String urlWebService) {
        class GetOrderHistory extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>() {
                        {
                            put(auxiliary.PPK_INITIAL_CHECK, auxiliary.PPV_INITIAL_CHECK);
                            put(auxiliary.PPK_CUSTID, auxiliary.DUMMYVAL_CUSTID);
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
                        //Log.i("CONTROL","PPK pass");
                        JSONArray jsonArray = new JSONArray(sb.toString().trim());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            orderId.add(jsonObject.getString("_order_id"));
                            orderStoreName.add(jsonObject.getString("store_name"));
                            orderStoreArea.add(jsonObject.getString("store_area"));
                            orderStatus.add(jsonObject.getString("orderstat_name"));
//                            orderDate.add(jsonObject.getString("_order_date"));
//                            orderTime.add(jsonObject.getString("_order_time"));
                            orderTotal.add(jsonObject.getString("_order_total"));
                            orderDeliveryCharge.add(jsonObject.getString("_order_deliveryCharge"));
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
                            int prod_count_diff = jsonObject.getJSONArray("proddet_name").length();
                            for (int j = 0; j < prod_count_diff; j++) {
                                productName.get(i).add(jsonObject.getJSONArray("proddet_name").getString(j));
                                productSize.get(i).add(jsonObject.getJSONArray("prod_size").getString(j));
                                productUnit.get(i).add(jsonObject.getJSONArray("produnit_name").getString(j));
                                productContainer.get(i).add(jsonObject.getJSONArray("prodcontain_name").getString(j));
                                productCount.get(i).add(jsonObject.getJSONArray("orderproddet_count").getString(j));
                                productStatus.get(i).add(jsonObject.getJSONArray("prodstat_name").getString(j));
                                productAvailabilityStatus.get(i).add(jsonObject.getJSONArray("prodavailstat_stat").getString(j));
                                productMrp.get(i).add(jsonObject.getJSONArray("orderproddet_mrp").getString(j));
                                productStoreprice.get(i).add(jsonObject.getJSONArray("orderproddet_storeprice").getString(j));
                            }
                            customDateTimeFormat( jsonObject.getString("_order_time"));
                            customDate(jsonObject.getString("_order_date"));
                        }
                        Log.i("order id arraylist size", Integer.toString(orderId.size()));
                    } else {
                        Log.i("CONTROL", "PPK failed or not set");
                    }
                } catch (JSONException e) {
                    Log.i("exception", "JSONException in OrderHistoryAdapter");
                    e.printStackTrace();
                } catch (MalformedURLException mue) {
                    Log.i("exception", "MalformedURLException in OrderHistoryAdapter");
                    mue.printStackTrace();
                } catch (IOException io) {
                    Log.i("exception", "IOException in OrderHistoryAdapter");
                    io.printStackTrace();
                } catch (Exception e) {
                    Log.i("exception", "Exception in OrderHistoryAdapter");
                    e.printStackTrace();
                }
                return null;
            }
        }
        GetOrderHistory getOrderHistory = new GetOrderHistory();
        try {
            getOrderHistory.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void orderHistoryClicked(String order_id,
                                     String order_status,
                                     String order_total,
                                     String order_deliveryCharge,
                                     String order_grandTotal,
                                     String order_paymMethod,
                                     String order_date,
                                     String order_time,
                                     String order_deliveryAddress,
                                     ArrayList<String> order_prodName,
                                     ArrayList<String> order_prodSize,
                                     ArrayList<String> order_prodUnit,
                                     ArrayList<String> order_prodContainer,
                                     ArrayList<String> order_prodCount,
                                     ArrayList<String> order_prodStatus,
                                     ArrayList<String> order_prodAvailabilityStatus,
                                     ArrayList<String> order_prodMrp,
                                     ArrayList<String> order_prodStorePrice,
                                     String order_storeName,
                                     String order_storeArea){
        Intent orderHistoryAdapterToOrderDetailIntent=new Intent(this.mContext,orderDetail.class);
        orderHistoryAdapterToOrderDetailIntent.putExtra(auxiliary.ORDER_ID,order_id);
        orderHistoryAdapterToOrderDetailIntent.putExtra(auxiliary.ORDER_STATUS,order_status);
        orderHistoryAdapterToOrderDetailIntent.putExtra(auxiliary.ORDER_TOTAL,order_total);
        orderHistoryAdapterToOrderDetailIntent.putExtra(auxiliary.ORDER_DELIVERYCHARGE,order_deliveryCharge);
        orderHistoryAdapterToOrderDetailIntent.putExtra(auxiliary.ORDER_GRANDTOTAL,order_grandTotal);
        orderHistoryAdapterToOrderDetailIntent.putExtra(auxiliary.ORDER_PAYMMETHOD,order_paymMethod);
        orderHistoryAdapterToOrderDetailIntent.putExtra(auxiliary.ORDER_DATE,order_date);
        orderHistoryAdapterToOrderDetailIntent.putExtra(auxiliary.ORDER_TIME,order_time);
        orderHistoryAdapterToOrderDetailIntent.putExtra(auxiliary.ORDER_DELIVERYADDRESS,order_deliveryAddress);
        orderHistoryAdapterToOrderDetailIntent.putStringArrayListExtra(auxiliary.ORDER_PRODNAME,order_prodName);
        orderHistoryAdapterToOrderDetailIntent.putStringArrayListExtra(auxiliary.ORDER_PRODSIZE,order_prodSize);
        orderHistoryAdapterToOrderDetailIntent.putStringArrayListExtra(auxiliary.ORDER_PRODSIZE,order_prodSize);
        orderHistoryAdapterToOrderDetailIntent.putStringArrayListExtra(auxiliary.ORDER_PRODUNIT,order_prodUnit);
        orderHistoryAdapterToOrderDetailIntent.putStringArrayListExtra(auxiliary.ORDER_PRODCONTAINER,order_prodContainer);
        orderHistoryAdapterToOrderDetailIntent.putStringArrayListExtra(auxiliary.ORDER_PRODCOUNT,order_prodCount);
        orderHistoryAdapterToOrderDetailIntent.putStringArrayListExtra(auxiliary.ORDER_PRODSTATUS,order_prodStatus);
        orderHistoryAdapterToOrderDetailIntent.putStringArrayListExtra(auxiliary.ORDER_PRODAVAILABILITYSTATUS,order_prodAvailabilityStatus);
        orderHistoryAdapterToOrderDetailIntent.putStringArrayListExtra(auxiliary.ORDER_PRODMRP,order_prodMrp);
        orderHistoryAdapterToOrderDetailIntent.putStringArrayListExtra(auxiliary.ORDER_PRODSTOREPRICE,order_prodStorePrice);
        orderHistoryAdapterToOrderDetailIntent.putExtra(auxiliary.ORDER_STORENAME,order_storeName);
        orderHistoryAdapterToOrderDetailIntent.putExtra(auxiliary.ORDER_STOREAREA,order_storeArea);
        this.mContext.startActivity(orderHistoryAdapterToOrderDetailIntent);
    }
}
