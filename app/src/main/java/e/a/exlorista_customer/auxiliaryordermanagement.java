package e.a.exlorista_customer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

class auxiliaryordermanagement {

    static String placeOrder(final String urlWebService,
                    final String cust_id,
                    final String store_id,
                    final String delivery_type,
                    final String prod_ids,
                    final String prod_counts,
                    final String prod_mrps,
                    final String prod_storePrices,
                    final String order_total,
                    final String order_deliveryCharge,
                    final String order_grandTotal,
                    final String order_diffProd,
                    final String custaddr_id,
                    final String paym_id){
        class PlaceOrder extends AsyncTask<Void,Void,Void>{
            String sql;
            private PlaceOrder() {
                this.sql = null;
            }
            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                    dos.writeBytes(auxiliary.postParamsToString(new HashMap<String, String>() {
                        {
                            put(auxiliary.PPK_INITIAL_CHECK,auxiliary.PPV_INITIAL_CHECK);
                            put(auxiliary.PPK_REQUESTTYPE,auxiliary.PPV_REQUESTTYPE_ORDERPLACEMENT);
                            put(auxiliary.PPK_CUSTID,cust_id);
                            put(auxiliary.PPK_STOREID,store_id);
                            put(auxiliary.PPK_DELIVERYTYPE,delivery_type);
                            put(auxiliary.PPK_PRODIDS,prod_ids);
                            put(auxiliary.PPK_PRODCOUNTS,prod_counts);
                            put(auxiliary.PPK_PRODMRPS,prod_mrps);
                            put(auxiliary.PPK_PRODSTOREPRICES,prod_storePrices);
                            put(auxiliary.PPK_ORDERTOTAL,order_total);
                            put(auxiliary.PPK_ORDERDELIVERYCHARGE,order_deliveryCharge);
                            put(auxiliary.PPK_ORDERGRANDTOTAL,order_grandTotal);
                            put(auxiliary.PPK_ORDERDIFFPROD,order_diffProd);
                            put(auxiliary.PPK_CUSTADDRID,custaddr_id);
                            put(auxiliary.PPK_PAYMID,paym_id==null?"":paym_id);
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
                    this.sql=sb.toString().trim();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            private String getSql(){
                return this.sql;
            }
        }
        PlaceOrder placeOrder=new PlaceOrder();
        try {
            placeOrder.execute().get();
        } catch (InterruptedException ignored) {
        } catch (ExecutionException ignored) {
        }
        return placeOrder.getSql();
    }

    static String placeOrderWrapper(Context context,String payment_id){
        ArrayList<String> prodIdsInCart=auxiliarycart.prodIdsInCart(context);
        ArrayList<String> prodCountsInCart=new ArrayList<String>();
        ArrayList<String> prodMrpsInCart=new ArrayList<String>();
        ArrayList<String> prodStorepricesInCart=new ArrayList<String>();
        String storeId=auxiliarycart.getStoreIdInCart(context);
        for(String i : prodIdsInCart){
            prodCountsInCart.add(auxiliarycart.thisProdCountInCartOGC(context,i,storeId));
            prodMrpsInCart.add(auxiliarycart.getCartProdInternalHashMapValueOGC(context,i,auxiliarycart.IHMKEY_PRODMRP));
            prodStorepricesInCart.add(auxiliarycart.getCartProdInternalHashMapValueOGC(context,i,auxiliarycart.IHMKEY_PRODSTOREPRICE));
        }

        int cart_total=auxiliarycart.getCartTotalAmount(context,prodIdsInCart);
        int cart_deliveryCharges=auxiliarycart.getCartDeliveryCharges(cart_total);
        int cart_grandTotal=cart_total+cart_deliveryCharges;
        String user_id=auxiliaryuseraccountmanager.getUserIdFromSP(context);
        String store_id=auxiliarycart.getStoreIdInCartOGC(context);
        String prod_ids=auxiliary.arrayListToStr(prodIdsInCart,"_");
        String prod_counts=auxiliary.arrayListToStr(prodCountsInCart,"_");
        int diff_prod=auxiliarycart.diffProdCountInCart(context);
        String prod_mrps=auxiliary.arrayListToStr(prodMrpsInCart,"_");
        String prod_storePrices=auxiliary.arrayListToStr(prodStorepricesInCart,"_");

        Log.i("AOM user_id",user_id);
        Log.i("AOM store_id",store_id);
        Log.i("AOM prod_ids",prod_ids);
        Log.i("AOM prod_counts",prod_counts);
        Log.i("AOM diff_prod",Integer.toString(diff_prod));
        Log.i("AOM prod_mrps",prod_mrps);
        Log.i("AOM prod_storePrices",prod_storePrices);
        Log.i("AOM cart_total",Integer.toString(cart_total));
        Log.i("AOM delivery_charge",Integer.toString(cart_deliveryCharges));
        Log.i("AOM cart_grandTotal",Integer.toString(cart_grandTotal));
        Log.i("AOM custaddr_id",auxiliary.DUMMYVAL_CUSTADDRID);
        Log.i("AOM payment_id",payment_id==null?"null":payment_id);

        String server_msg1=auxiliaryordermanagement.placeOrder(auxiliary.SERVER_URL+"/orderManagement.php"
                ,user_id
                ,store_id
                ,auxiliary.PPV_DELIVERYTYPE_TAKEAWAY
                ,prod_ids
                ,prod_counts
                ,prod_mrps
                ,prod_storePrices
                ,Integer.toString(cart_total)
                ,Integer.toString(cart_deliveryCharges)
                ,Integer.toString(cart_grandTotal)
                ,Integer.toString(diff_prod)
                ,auxiliary.DUMMYVAL_CUSTADDRID
                ,payment_id);

        /*
        String server_msg=auxiliaryordermanagement.placeOrder(auxiliary.SERVER_URL+"/orderManagement.php"
                ,auxiliaryuseraccountmanager.getUserIdFromSP(context)
                ,auxiliarycart.getStoreIdInCartOGC(context)
                ,auxiliary.arrayListToStr(prodIdsInCart,"_")
                ,auxiliary.arrayListToStr(prodCountsInCart,"_")
                ,auxiliary.arrayListToStr(prodMrpsInCart,"_")
                ,auxiliary.arrayListToStr(prodStorepricesInCart,"_")
                ,Integer.toString(cart_total)
                ,Integer.toString(cart_deliveryCharges)
                ,Integer.toString(cart_grandTotal)
                ,auxiliary.DUMMYVAL_CUSTADDRID
                ,payment_id);
         */

        //Log.i("server_msg1",server_msg1);

        return server_msg1;
    }

}
