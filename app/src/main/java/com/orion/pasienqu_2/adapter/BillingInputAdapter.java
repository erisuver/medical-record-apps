package com.orion.pasienqu_2.adapter;

import android.content.Context;
import android.icu.text.DecimalFormat;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.models.BillingItemModel;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class BillingInputAdapter extends RecyclerView.Adapter {
    public boolean isReqFocus= true;
    Context context;
    List<BillingItemModel> Datas;
//    private List<BillingItemModel> dataFiltered;
    int view;
    private Runnable runnableOnAmountChange;
    public boolean isDetail = false;
    private long textcount = 0;
    private long newtextcount = 0;

    public BillingInputAdapter(Context context, List<BillingItemModel> datas, int view, Runnable runnableOnAmountChange) {
        this.context = context;
        Datas = datas;
        this.view = view;
//        this.dataFiltered = new ArrayList<BillingItemModel>();
//        this.dataFiltered.addAll(Datas);
        this.runnableOnAmountChange = runnableOnAmountChange;
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        public TextInputEditText txtLabel;
        public TextInputEditText txtAmount;
        public ImageButton btnDelete;

        public ItemHolder(View itemView) {
            super(itemView);
            txtLabel = (TextInputEditText) itemView.findViewById(R.id.txtLabel);
            txtAmount = (TextInputEditText) itemView.findViewById(R.id.txtAmount);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new BillingInputAdapter.ItemHolder(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= Datas.size()){
            return;
        }
        final BillingItemModel data = Datas.get(position);
        final BillingInputAdapter.ItemHolder itemHolder = (BillingInputAdapter.ItemHolder) holder;
        String uuid = data.getUuid();

        itemHolder.txtLabel.setText(data.getName());
        itemHolder.txtAmount.setText(Global.FloatToStrFmt(data.getAmount()));

        int newPosition = itemHolder.getAdapterPosition();

        itemHolder.btnDelete.setOnClickListener(view1 -> {
            removeModel(newPosition);
            runnableOnAmountChange.run();
        });


        itemHolder.txtLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Datas.get(newPosition).setName(itemHolder.txtLabel.getText().toString());
            }
        });


        itemHolder.txtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = charSequence.toString();
                if (input.equals("")) {
                    itemHolder.txtAmount.setText("0");
                    itemHolder.txtAmount.selectAll();
                }

                textcount = input.length();
                if (textcount != newtextcount) {
                    try {
                        String newPrice = Global.FloatToStrFmt(Global.StrFmtToFloat(input));
                        itemHolder.txtAmount.removeTextChangedListener(this); //To Prevent from Infinite Loop
                        if (newPrice.length() < 11) {
                            itemHolder.txtAmount.setText(newPrice);
                        } else {
                            newPrice = "99.999.999";
                            itemHolder.txtAmount.setText(newPrice);
                        }
                        itemHolder.txtAmount.setSelection(newPrice.length()); //Move Cursor to end of String
                        itemHolder.txtAmount.addTextChangedListener(this);
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                }
                runnableOnAmountChange.run();

                newtextcount = textcount;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Datas.get(newPosition).setAmount(Global.StrFmtToFloat(itemHolder.txtAmount.getText().toString()));
                runnableOnAmountChange.run();
            }
        });


        if (isDetail){
            itemHolder.txtLabel.setEnabled(false);
            itemHolder.txtAmount.setEnabled(false);
            itemHolder.btnDelete.setVisibility(View.GONE);
        }

        if(isReqFocus) {
            if (position == getItemCount()-1) {
                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        itemHolder.txtLabel.requestFocus();
                        itemHolder.txtLabel.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0f, 0f, 0));
                        itemHolder.txtLabel.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0f, 0f, 0));
                    }
                }, 200);
            }
        }

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

    public void removeModel(int idx) {
        if (Datas.size() > 0 && Datas.size() > idx){
            this.Datas.remove(idx);
            notifyItemRemoved(idx);
            notifyDataSetChanged();
        }
    }

}