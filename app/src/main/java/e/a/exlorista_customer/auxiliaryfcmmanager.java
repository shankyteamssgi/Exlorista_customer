package e.a.exlorista_customer;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

class auxiliaryfcmmanager {

    static String sendTokenToServer(final String urlWebService,
                                  final String cust_id,
                                  final String token){
        class SendTokenToServer extends AsyncTask<Void,Void,Void> {

            private String sql;

            private SendTokenToServer() {
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
                            put(auxiliary.PPK_REQUESTTYPE,auxiliary.PPV_REQUESTTYPE_FCMTOKENADDORUPDATE);
                            put(auxiliary.PPK_CUSTID,cust_id);
                            put(auxiliary.PPK_FCMTOKEN,token);
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
        SendTokenToServer sendTokenToServer=new SendTokenToServer();
        try {
            sendTokenToServer.execute().get();
        } catch (InterruptedException ignored) {
        } catch (ExecutionException ignored) {
        }
        return sendTokenToServer.getSql();
    }

}
