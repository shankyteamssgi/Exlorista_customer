package e.a.exlorista_customer;

import android.app.ActionBar;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class orderDetail extends AppCompatActivity {

    private Context mContext;
    private Bundle extras;
    private String orderId,orderStatus,orderTotal,orderDeliveryCharge,orderGrandTotal,orderPaymMethod,
            orderDate,orderTime,orderDeliveryAddress;
    private ArrayList<String> orderProdName,orderProdSize,orderProdUnit,orderProdContainer,orderProdCount,orderProdStatus,
            orderProdAvailabilityStatus,orderProdMrp,orderProdStorePrice;
    private String orderStoreName,orderStoreArea;

    private TextView storeNameOrderDetailTV,storeAddressOrderDetailTV,totalAmountOrderDetailTV,
            discountAmountOrderDetailTV,deliveryAmountOrderDetailTV,grandTotalAmountOrderDetailTV,
            orderIdOrderDetailTV,orderStatusOrderDetailTV,orderedOnOrderDetailTV,paymentOrderDetailTV,
            deliveryAddressOrderDetailTV;
    private LinearLayout itemsListOrderDetailLL,discountOrderDetailLL;
    private TextView itemsInfoTextOrderDetailTV,totalTextOrderDetailTV,discountTextOrderDetailTV,deliveryTextOrderDetailTV,
            grandTotalTextOrderDetailTV,orderInfoTextOrderDetailTV,orderIdTextOrderDetailTV,orderStatusTextOrderDetailTV,
            moreInfoTextOrderDetailTV,orderedOnTextOrderDetailTV,paymentTextOrderDetailTV,deliveryAddressTextOrderDetailTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        /* setTitle("Order history");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        mContext=this;

        storeNameOrderDetailTV=findViewById(R.id.storeNameOrderDetailTV);
        storeAddressOrderDetailTV=findViewById(R.id.storeAddressOrderDetailTV);
        totalAmountOrderDetailTV=findViewById(R.id.totalAmountOrderDetailTV);
        discountAmountOrderDetailTV=findViewById(R.id.discountAmountOrderDetailTV);
        deliveryAmountOrderDetailTV=findViewById(R.id.deliveryAmountOrderDetailTV);
        grandTotalAmountOrderDetailTV=findViewById(R.id.grandTotalAmountOrderDetailTV);
        orderIdOrderDetailTV=findViewById(R.id.orderIdOrderDetailTV);
        orderStatusOrderDetailTV=findViewById(R.id.orderStatusOrderDetailTV);
        orderedOnOrderDetailTV=findViewById(R.id.orderedOnOrderDetailTV);
        paymentOrderDetailTV=findViewById(R.id.paymentOrderDetailTV);
        deliveryAddressOrderDetailTV=findViewById(R.id.deliveryAddressOrderDetailTV);

        itemsListOrderDetailLL=findViewById(R.id.itemsListOrderDetailLL);
        discountOrderDetailLL=findViewById(R.id.discountOrderDetailLL);

        try{
            extras=getIntent().getExtras();
            if(extras!=null){
                orderId=extras.getString(auxiliary.ORDER_ID);
                orderStatus=extras.getString(auxiliary.ORDER_STATUS);
                orderTotal=extras.getString(auxiliary.ORDER_TOTAL);
                orderDeliveryCharge=extras.getString(auxiliary.ORDER_DELIVERYCHARGE);
                orderGrandTotal=extras.getString(auxiliary.ORDER_GRANDTOTAL);
                orderPaymMethod=extras.getString(auxiliary.ORDER_PAYMMETHOD);
                orderDate=extras.getString(auxiliary.ORDER_DATE);
                orderTime=extras.getString(auxiliary.ORDER_TIME);
                orderDeliveryAddress=extras.getString(auxiliary.ORDER_DELIVERYADDRESS);
                orderProdName=extras.getStringArrayList(auxiliary.ORDER_PRODNAME);
                orderProdSize=extras.getStringArrayList(auxiliary.ORDER_PRODSIZE);
                orderProdUnit=extras.getStringArrayList(auxiliary.ORDER_PRODUNIT);
                orderProdContainer=extras.getStringArrayList(auxiliary.ORDER_PRODCONTAINER);
                orderProdCount=extras.getStringArrayList(auxiliary.ORDER_PRODCOUNT);
                orderProdStatus=extras.getStringArrayList(auxiliary.ORDER_PRODSTATUS);
                orderProdAvailabilityStatus=extras.getStringArrayList(auxiliary.ORDER_PRODAVAILABILITYSTATUS);
                orderProdMrp=extras.getStringArrayList(auxiliary.ORDER_PRODMRP);
                orderProdStorePrice=extras.getStringArrayList(auxiliary.ORDER_PRODSTOREPRICE);
                orderStoreName=extras.getString(auxiliary.ORDER_STORENAME);
                orderStoreArea=extras.getString(auxiliary.ORDER_STOREAREA);

                discountOrderDetailLL.setVisibility(View.GONE);

                storeNameOrderDetailTV.setText(orderStoreName);
                storeAddressOrderDetailTV.setText(orderStoreArea);
                setHeadingForItemsList();
                generateItemsList(orderProdName
                        ,orderProdSize
                        ,orderProdUnit
                        ,orderProdContainer
                        ,orderProdCount
                        ,orderProdStatus
                        ,orderProdAvailabilityStatus
                        ,orderProdMrp
                        ,orderProdStorePrice);
                totalAmountOrderDetailTV.setText(orderTotal);
                discountAmountOrderDetailTV.setVisibility(View.GONE);
                discountAmountOrderDetailTV.setText("a");
                deliveryAmountOrderDetailTV.setText(orderDeliveryCharge);
                grandTotalAmountOrderDetailTV.setText(orderGrandTotal);
                orderedOnOrderDetailTV.setText(orderTime+","+orderDate);
                paymentOrderDetailTV.setText(orderPaymMethod);
                deliveryAddressOrderDetailTV.setText(orderDeliveryAddress);
                orderIdOrderDetailTV.setText(orderId);
                orderStatusOrderDetailTV.setText(orderStatus);
            } else{
                Log.i("orderDetail.java","extras is null");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void generateItemsList(ArrayList<String> prod_name,
                                   ArrayList<String> prod_size,
                                   ArrayList<String> prod_unit,
                                   ArrayList<String> prod_container,
                                   ArrayList<String> prod_count,
                                   ArrayList<String> prod_status,
                                   ArrayList<String> prod_availabilityStatus,
                                   ArrayList<String> prod_mrp,
                                   ArrayList<String> prod_storePrice){
        int diffProdTypes=prod_name.size();
        for(int i=0;i<diffProdTypes;i++){
            String item_particular=prod_name.get(i)+" "+prod_size.get(i)+" "+prod_unit.get(i)+" "+prod_container.get(i)+" x "+prod_count.get(i);
            String item_mrp=prod_mrp.get(i);
            String item_storePrice=prod_storePrice.get(i);
            Log.i("orderDetail","prod_count : "+prod_count.get(i));
            Log.i("orderDetail","prod_storePrice : "+prod_storePrice.get(i));
            Log.i("orderDetail","prod_availabilityStatus : "+prod_availabilityStatus.get(i));
            String item_total=Integer.toString(Integer.parseInt(prod_count.get(i))*Integer.parseInt(prod_storePrice.get(i)));
            LinearLayout itemRowOrderDetailLL=new LinearLayout(mContext);
            itemRowOrderDetailLL.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );
            itemRowOrderDetailLL.setOrientation(LinearLayout.HORIZONTAL);
            TextView itemParticularOrderDetailTV,itemMrpOrderDetailTV,itemStorePriceOrderDetailTV,itemTotalOrderDetailTV;
            itemParticularOrderDetailTV=new TextView(mContext);
            itemMrpOrderDetailTV=new TextView(mContext);
            itemStorePriceOrderDetailTV=new TextView(mContext);
            itemTotalOrderDetailTV=new TextView(mContext);
            itemParticularOrderDetailTV.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT,0.4f
                    )
            );
            itemMrpOrderDetailTV.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            0,LinearLayout.LayoutParams.WRAP_CONTENT,0.2f
                    )
            );
            itemStorePriceOrderDetailTV.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            0,LinearLayout.LayoutParams.WRAP_CONTENT,0.2f
                    )
            );
            itemTotalOrderDetailTV.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT,0.2f
                    )
            );
            itemParticularOrderDetailTV.setText(item_particular);
            itemMrpOrderDetailTV.setText(item_mrp);
            itemStorePriceOrderDetailTV.setText(item_storePrice);
            itemTotalOrderDetailTV.setText(item_total);
            itemRowOrderDetailLL.addView(itemParticularOrderDetailTV);
            itemRowOrderDetailLL.addView(itemMrpOrderDetailTV);
            itemRowOrderDetailLL.addView(itemStorePriceOrderDetailTV);
            itemRowOrderDetailLL.addView(itemTotalOrderDetailTV);
            itemsListOrderDetailLL.addView(itemRowOrderDetailLL);
        }
    }

    private void setHeadingForItemsList(){
        LinearLayout itemsListHeadingLL=new LinearLayout(mContext);
        itemsListHeadingLL.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );
        TextView itemParticularHeadingTV,itemMrpHeadingTV,itemStorePriceTV,itemTotalHeadingTV;
        itemParticularHeadingTV=new TextView(mContext);
        itemMrpHeadingTV=new TextView(mContext);
        itemStorePriceTV=new TextView(mContext);
        itemTotalHeadingTV=new TextView(mContext);
        itemParticularHeadingTV.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT,0.4f
                )
        );
        itemMrpHeadingTV.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT,0.2f
                )
        );
        itemStorePriceTV.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT,0.2f
                )
        );
        itemTotalHeadingTV.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT,0.2f
                )
        );
        itemParticularHeadingTV.setText(auxiliary.HEADING_ITEMPARTICULAR);
        itemMrpHeadingTV.setText(auxiliary.HEADING_ITEMMRP);
        itemStorePriceTV.setText(auxiliary.HEADING_ITEMSTOREPRICE);
        itemTotalHeadingTV.setText(auxiliary.HEADING_ITEMTOTAL);
        itemsListHeadingLL.addView(itemParticularHeadingTV);
        itemsListHeadingLL.addView(itemMrpHeadingTV);
        itemsListHeadingLL.addView(itemStorePriceTV);
        itemsListHeadingLL.addView(itemTotalHeadingTV);
        itemsListOrderDetailLL.addView(itemsListHeadingLL);
    }
}