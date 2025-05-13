package com.orion.pasienqu_2.adapter.more;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.home.home;
import com.orion.pasienqu_2.models.CountryModel;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.logging.Handler;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CountryAdapter extends RecyclerView.Adapter{
    Context context;
    List<CountryModel>Datas;
    private List<CountryModel>dataFiltered;
    int view;

    public CountryAdapter(Context context, List<CountryModel> datas, int view) {
        this.context = context;
        Datas = datas;
        dataFiltered = new ArrayList<CountryModel>();
        this.dataFiltered.addAll(Datas);
        this.view = view;
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder  {
        public TextView tvName;
        public ImageView imgFlag, imgChecked;
        public CardView crdView;

        public ItemHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            imgFlag = (ImageView) itemView.findViewById(R.id.imgFlag);
            imgChecked = (ImageView) itemView.findViewById(R.id.imgChecked);
            crdView = (CardView) itemView.findViewById(R.id.crdView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new ItemHolder(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CountryModel data = dataFiltered.get(position);
        final CountryAdapter.ItemHolder itemHolder = (CountryAdapter.ItemHolder) holder;
        final int id = data.getId();
        final SharedPreferences sharedpreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);

        itemHolder.tvName.setText(data.getName());
        itemHolder.imgFlag.setImageResource(data.getFlag());
        itemHolder.imgChecked.setImageResource(data.getChecked());

//        if(id == 2 && sharedpreferences.getString("language", "").equals("in")) {
//            itemHolder.imgChecked.setImageResource(data.getChecked());
//        }else  {
//            itemHolder.imgChecked.setImageResource(data.getChecked());
//        }


        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog loading = new ProgressDialog(context);
                loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                loading.setCancelable(false);
                loading.setMessage("Loading...");
                loading.show();


                //set bahasa sesuai yg dipilih user
                SharedPreferences.Editor editor = sharedpreferences.edit();
                if(id == 2){
                    editor.putString("language", "in");
                    editor.commit();
                    editor.apply();
                    JApplication.getInstance().setLoginInformationBySharedPreferences();

                }else {
                    editor.putString("language", "us");
                    editor.commit();
                    editor.apply();
                    JApplication.getInstance().setLoginInformationBySharedPreferences();
                }


                //fungsi apply bahasa yg telah di set oleh user
                SharedPreferences sharedPreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
                //bahasa
                String language = sharedPreferences.getString("language", "");
                if(!language.equals("")) {
                    if (language.equals("in")) {
                        Locale locale = new Locale("in");
                        Configuration config = context.getResources().getConfiguration();
                        config.locale = locale;
                        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
                    }else{
                        Locale locale = new Locale("en");
                        Configuration config = context.getResources().getConfiguration();
                        config.locale = locale;
                        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
                    }
                }else{
                    Locale locale =  new Locale("");
                    Configuration config = context.getResources().getConfiguration();
                    config.locale = locale;
                    context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
                }

                //delay loading
                new CountDownTimer(1000, 500) {
                    public void onTick(long millisUntilFinished) {
                        // You don't need anything here
                    }

                    public void onFinish() {
                        loading.dismiss();

                        //balik ke  home
                        ((Activity)context).finish();
                        context.startActivity(new Intent(context, home.class));
                    }
                }.start();

            }
        });
    }



    public void addModel(CountryModel Datas) {
        int pos = this.dataFiltered.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
        this.dataFiltered.add(Datas);
        notifyItemRangeInserted(pos, dataFiltered.size());
    }

    public void addModels(List<CountryModel> Datas) {
        int pos = this.dataFiltered.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltered.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltered.size());
    }

    public void removeAllModel(){
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        LastPosition = dataFiltered.size();
        this.dataFiltered.removeAll(dataFiltered);
        notifyItemRangeRemoved(0, LastPosition);
    }
}
