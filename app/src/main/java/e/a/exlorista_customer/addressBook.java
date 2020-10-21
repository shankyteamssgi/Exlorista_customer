package e.a.exlorista_customer;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

public class addressBook extends AppCompatActivity {

    Button addNewAddressB;
    RecyclerView addressListRV;
    RecyclerView.LayoutManager addressListRVLayoutManager;
    RecyclerView.Adapter addressListRVAdapter;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);

        /*setTitle("Address book");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        mContext=this;

        addNewAddressB=findViewById(R.id.addNewAddressB);
        addressListRV=findViewById(R.id.addressListRV);
        addressListRVLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        addressListRVAdapter=new AddressBookAdapter(this);
        addressListRV.setLayoutManager(addressListRVLayoutManager);
        addressListRV.setAdapter(addressListRVAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
