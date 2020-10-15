package e.a.exlorista_customer;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

public class AddressBookAdapter extends RecyclerView.Adapter<AddressBookAdapter.AddressBookViewHolder>{


    private Context mContext;
    private ArrayList<String> custaddrId;
    private ArrayList<String> custaddrAddress;
    private ArrayList<String> custaddrArea;
    private ArrayList<String> custaddrLandmark;
    private ArrayList<String> custaddrCity;
    private ArrayList<String> custaddrState;
    private ArrayList<String> custaddrLat;
    private ArrayList<String> custaddrLong;
    private ArrayList<String> custaddrTag;
    private ArrayList<String> custaddrVisibility;

    static class AddressBookViewHolder extends RecyclerView.ViewHolder{

        TextView tagAddressTV;
        TextView completeAddressTV;
        Button editAddressB;
        Button deleteAddressB;

        public AddressBookViewHolder(View itemView) {
            super(itemView);
            tagAddressTV=itemView.findViewById(R.id.tagAddressTV);
            completeAddressTV=itemView.findViewById(R.id.completeAddressTV);
            editAddressB=itemView.findViewById(R.id.editAddressB);
            deleteAddressB=itemView.findViewById(R.id.deleteAddressB);
        }
    }

    public AddressBookAdapter(Context context) {
        this.mContext = context;
        this.custaddrId=new ArrayList<String>();
        this.custaddrAddress=new ArrayList<String>();
        this.custaddrArea=new ArrayList<String>();
        this.custaddrLandmark=new ArrayList<String>();
        this.custaddrCity=new ArrayList<String>();
        this.custaddrState=new ArrayList<String>();
        this.custaddrLat=new ArrayList<String>();
        this.custaddrLong=new ArrayList<String>();
        this.custaddrTag=new ArrayList<String>();
        this.custaddrVisibility=new ArrayList<String>();
        this.getAddressBook(auxiliary.SERVER_URL+"/addressBookManager.php");
    }

    @NonNull
    @Override
    public AddressBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.addressbook,parent,false);
        return new AddressBookAdapter.AddressBookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddressBookViewHolder holder, int position) {
        holder.tagAddressTV.setText(custaddrTag.get(holder.getAdapterPosition()));
        holder.completeAddressTV.setText(custaddrAddress.get(holder.getAdapterPosition())+", "+
                custaddrArea.get(holder.getAdapterPosition())+", "+
                custaddrLandmark.get(holder.getAdapterPosition())+", "+
                custaddrCity.get(holder.getAdapterPosition()));

        holder.deleteAddressB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                builder.setTitle("Delete this address?");
                builder.setMessage("Are you sure you want to delete the address ("+custaddrTag.get(holder.getAdapterPosition())+")");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAddress(auxiliary.SERVER_URL+"/addressBookManager.php",custaddrId.get(holder.getAdapterPosition()));
                        custaddrId.remove(holder.getAdapterPosition());
                        custaddrAddress.remove(holder.getAdapterPosition());
                        custaddrArea.remove(holder.getAdapterPosition());
                        custaddrLandmark.remove(holder.getAdapterPosition());
                        custaddrCity.remove(holder.getAdapterPosition());
                        custaddrState.remove(holder.getAdapterPosition());
                        custaddrLat.remove(holder.getAdapterPosition());
                        custaddrLong.remove(holder.getAdapterPosition());
                        custaddrTag.remove(holder.getAdapterPosition());
                        custaddrVisibility.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());

                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        //Log.i("address book adapter",Integer.toString(custaddrId.size()));
        return custaddrId.size();
    }

    private void getAddressBook(final String urlWebService){
        class GetAddressBook extends AsyncTask<Void,Void,Void>{
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
                            put(auxiliary.PPK_REQUESTTYPE,auxiliary.PPV_REQUESTTYPE_ADDRESSBOOKFETCH);
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
                    Log.i("AddressBookAdapter sb",sb.toString().trim());
                    if(!sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_FAIL) &&
                            !sb.toString().trim().equals(auxiliary.PPK_INITIAL_CHECK_NOT_SET)) {
                        //Log.i("CONTROL","PPK pass");
                        JSONArray jsonArray = new JSONArray(sb.toString().trim());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject=jsonArray.getJSONObject(i);
                            custaddrId.add(jsonObject.getString("custaddr_id"));
                            custaddrAddress.add(jsonObject.getString("custaddr_address"));
                            custaddrArea.add(jsonObject.getString("area_name"));
                            custaddrLandmark.add(jsonObject.getString("custaddr_landmark"));
                            custaddrCity.add(jsonObject.getString("city_name"));
                            custaddrState.add(jsonObject.getString("state_name"));
                            custaddrLat.add(jsonObject.getString("custaddr_lat"));
                            custaddrLong.add(jsonObject.getString("custaddr_long"));
                            custaddrTag.add(jsonObject.getString("custaddr_tag"));
                            custaddrVisibility.add(jsonObject.getString("custaddr_visibility"));
                        }
                    } else {
                        Log.i("CONTROL","PPK failed or not set");
                    }
                } catch (JSONException e) {
                    Log.i("exception","JSONException in AddressBookAdapter");
                    e.printStackTrace();
                } catch (MalformedURLException mue){
                    Log.i("exception","MalformedURLException in AddressBookAdapter");
                    mue.printStackTrace();
                } catch (IOException io){
                    Log.i("exception","IOException in AddressBookAdapter");
                    io.printStackTrace();
                } catch (Exception e){
                    Log.i("exception","Exception in AddressBookAdapter");
                    e.printStackTrace();
                }
                return null;
            }
        }
        GetAddressBook getAddressBook=new GetAddressBook();
        try {
            getAddressBook.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void deleteAddress(final String urlWebService, final String custaddr_id){
        class DeleteAddress extends AsyncTask<Void,Void,Void>{
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
                            put(auxiliary.PPK_CUSTADDRID,custaddr_id);
                            put(auxiliary.PPK_REQUESTTYPE,auxiliary.PPV_REQUESTTYPE_ADDRESSDELETE);
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
                } catch (MalformedURLException mue){
                    Log.i("exception","MalformedURLException in AddressBookAdapter");
                    mue.printStackTrace();
                } catch (IOException io){
                    Log.i("exception","IOException in AddressBookAdapter");
                    io.printStackTrace();
                } catch (Exception e){
                    Log.i("exception","Exception in AddressBookAdapter");
                    e.printStackTrace();
                }
                return null;
            }
        }
        DeleteAddress deleteAddress=new DeleteAddress();
        try {
            deleteAddress.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
