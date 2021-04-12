package e.a.exlorista_customer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class TrackableOrderAdapter extends RecyclerView.Adapter<TrackableOrderAdapter.trackableOrderViewHolder>{

    private Context mContext;
    private ArrayList<String> _orderId;
    private ArrayList<String> _placedOn;
    private ArrayList<String> _grandTotal;
    private ArrayList<String> _productCount;

    public class trackableOrderViewHolder extends RecyclerView.ViewHolder{
        TextView orderIdTV;
        TextView placedOnTV;
        TextView grandTotalTV;
        TextView productCountTV;
        Button viewLosB;

        public trackableOrderViewHolder(View itemView) {
            super(itemView);
            orderIdTV=itemView.findViewById(R.id.orderIdTV);
            placedOnTV=itemView.findViewById(R.id.placedOnTV);
            grandTotalTV=itemView.findViewById(R.id.grandTotalTV);
            productCountTV=itemView.findViewById(R.id.productCountTV);
            viewLosB=itemView.findViewById(R.id.viewLosB);
        }
    }

    public TrackableOrderAdapter(Context context,
                                 ArrayList<String> orderId,
                                 ArrayList<String> placedOn,
                                 ArrayList<String> grandTotal,
                                 ArrayList<String> productCount) {
        mContext = context;
        _orderId=orderId;
        _placedOn=placedOn;
        _grandTotal=grandTotal;
        _productCount=productCount;
    }

    @NonNull
    @Override
    public trackableOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.trackableorder, parent, false);
        return new TrackableOrderAdapter.trackableOrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final trackableOrderViewHolder holder, int position) {
        holder.orderIdTV.setText(_orderId.get(holder.getAdapterPosition()));
        holder.placedOnTV.setText(_placedOn.get(holder.getAdapterPosition()));
        holder.grandTotalTV.setText(_grandTotal.get(holder.getAdapterPosition()));
        holder.productCountTV.setText(_productCount.get(holder.getAdapterPosition()));
        holder.viewLosB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent trackOrderToLiveOrderStatusIntent=new Intent(mContext,liveOrderStatus.class);
                trackOrderToLiveOrderStatusIntent.putExtra(auxiliary.ORDER_ID,_orderId.get(holder.getAdapterPosition()));
                mContext.startActivity(trackOrderToLiveOrderStatusIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _orderId.size();
    }


}
