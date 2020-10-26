package e.a.exlorista_customer;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class orderHistory extends AppCompatActivity {

    private RecyclerView orderHistoryRV;
    private RecyclerView.Adapter orderHistoryRVAdapter;
    private RecyclerView.LayoutManager orderHistoryRVLayoutManager;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

       /* setTitle("Order history");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        mContext=this;

        orderHistoryRV=findViewById(R.id.orderHistoryRV);
        orderHistoryRVLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        orderHistoryRVAdapter=new OrderHistoryAdapter(this);
        orderHistoryRV.setLayoutManager(orderHistoryRVLayoutManager);
        orderHistoryRV.setAdapter(orderHistoryRVAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
