package com.orion.pasienqu_2.adapter.more;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.android.material.textfield.TextInputEditText;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.adapter.BillingInputAdapter;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.models.BillingItemModel;
import com.orion.pasienqu_2.models.BillingItemModel;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class BillingTemplateItemAdapter extends RecyclerView.Adapter {
    Context context;
    List<BillingItemModel> Datas;
//    private List<BillingItemModel> dataFiltered;
    int view;

    public BillingTemplateItemAdapter(Context context, List<BillingItemModel> datas, int view) {
        this.context = context;
        Datas = datas;
        this.view = view;
        this.Datas = Datas;
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder  {
        public TextInputEditText txtLabel;
        public ImageButton btnDelete;

        public ItemHolder(View itemView) {
            super(itemView);
            txtLabel = (TextInputEditText) itemView.findViewById(R.id.txtLabel);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new BillingTemplateItemAdapter.ItemHolder(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position >= Datas.size()){
            return;
        }
        final BillingItemModel data = Datas.get(position);
        final BillingTemplateItemAdapter.ItemHolder itemHolder = (BillingTemplateItemAdapter.ItemHolder) holder;
        String uuid = data.getUuid();
        itemHolder.txtLabel.setText(data.getName());


        int newPosition = itemHolder.getAdapterPosition();
        itemHolder.btnDelete.setOnClickListener(view1 -> {
            BillingTemplateItemAdapter.this.removeModel(newPosition);

        });

        itemHolder.txtLabel.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                int newPosition = itemHolder.getAdapterPosition();
                Datas.get(newPosition).setName(itemHolder.txtLabel.getText().toString());
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        if (position == getItemCount()-1) {
            new Handler().postDelayed(new Runnable() {

                public void run() {
                    itemHolder.txtLabel.requestFocus();
                    itemHolder.txtLabel.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0f, 0f, 0));
                    itemHolder.txtLabel.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0f, 0f, 0));
                }
            }, 200);
        }
//        itemHolder.txtLabel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    Global.hideSoftKeyboard((Activity) context);
//                } else {
//                    Global.showKeyboard((Activity) context);
//                }
//            }
//        });


    }

    public void addModel(BillingItemModel Datas) {
        int pos = this.Datas.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
//        this.dataFiltered.add(Datas);
//        notifyItemRangeInserted(pos, dataFiltered.size());
    }

    public void addModels(List<BillingItemModel> Datas) {
        int pos = this.Datas.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
//        this.dataFiltered.addAll(Datas);
//        notifyItemRangeInserted(pos, dataFiltered.size());
    }

    public void removeAllModel(){
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
//        LastPosition = dataFiltered.size();
//        this.dataFiltered.removeAll(dataFiltered);
        notifyItemRangeRemoved(0, LastPosition);
    }

//    public void removeModel(int idx) {
//        if (Datas.size() > 0 && idx != -1){
//            this.Datas.remove(idx);
//            notifyItemRemoved(idx);
//        }
////        if (dataFiltered.size() > 0){
////            this.dataFiltered.remove(idx);
////            notifyItemRemoved(idx);
////        }
//    }
    public void removeModel(int idx) {
        if (Datas.size() > 0 && Datas.size() > idx){
            this.Datas.remove(idx);
            notifyItemRemoved(idx);
            notifyDataSetChanged();
        }
    }


}

