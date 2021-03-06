package e.a.exlorista_customer;

import android.os.AsyncTask;
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
                    final String prod_ids,
                    final String prod_counts,
                    final String prod_mrps,
                    final String prod_storePrices,
                    final String order_total,
                    final String order_deliveryCharge,
                    final String order_grandTotal,
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
                            put(auxiliary.PPK_PRODIDS,prod_ids);
                            put(auxiliary.PPK_PRODCOUNTS,prod_counts);
                            put(auxiliary.PPK_PRODMRPS,prod_mrps);
                            put(auxiliary.PPK_PRODSTOREPRICES,prod_storePrices);
                            put(auxiliary.PPK_ORDERTOTAL,order_total);
                            put(auxiliary.PPK_ORDERDELIVERYCHARGE,order_deliveryCharge);
                            put(auxiliary.PPK_ORDERGRANDTOTAL,order_grandTotal);
                            put(auxiliary.PPK_CUSTADDRID,custaddr_id);
                            put(auxiliary.PPK_PAYMID,paym_id);
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

}
