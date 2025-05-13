package com.orion.pasienqu_2.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.ZoomImage.ZoomImageActivity;
import com.orion.pasienqu_2.globals.FileUtil;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.models.RecordFileModel;
import com.orion.pasienqu_2.models.RecordFileModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

public class RecordFileAdapter extends RecyclerView.Adapter {
    Context context;
    Activity activity;
    List<RecordFileModel> Datas;
    private final String lokFolder = Environment.getExternalStorageDirectory() + "//Android/Data/com.orionit.app.PasienQu/PasienquTemp/";
//    private List<RecordFileModel> dataFiltered;
    int view;
    private String mode;

    public RecordFileAdapter(Context context, Activity activity, List<RecordFileModel> datas, int view, String mode) {
        this.context = context;
        this.activity = activity;
        Datas = datas;
        this.view = view;
//        this.dataFiltered = new ArrayList<RecordFileModel>();
//        this.dataFiltered.addAll(Datas);
        this.mode = mode;
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public ImageView imgFile;
        public ImageButton btnDelete;

        public ItemHolder(View itemView) {
            super(itemView);
            imgFile = itemView.findViewById(R.id.imgFile);
            tvName = itemView.findViewById(R.id.tvName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new RecordFileAdapter.ItemHolder(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RecordFileModel data = Datas.get(position);
        final RecordFileAdapter.ItemHolder itemHolder = (RecordFileAdapter.ItemHolder) holder;
        String uuid = data.getUuid();

        final String mime_type = data.getMime_type();
        File newFile = FileUtil.saveImage(context, data.getRecord_file());
        final Uri recordFile = Uri.fromFile(newFile);
        if (mime_type.equals("jpg")) {
//            Bitmap image = Global.BitmapFileFromUri(recordFile, context);
//            itemHolder.imgFile.setImageBitmap(image);
//            itemHolder.imgFile.setImageURI(recordFile);
            File imgFile = new  File(JConst.mediaLocationPath +"/"+data.getRecord_file());
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                itemHolder.imgFile.setImageBitmap(myBitmap);
            }

        }else{
            itemHolder.imgFile.setImageResource(R.drawable.ic_baseline_description_24);
        }

        if (mode.equals("add") || mode.equals("edit")) {
            itemHolder.btnDelete.setVisibility(View.VISIBLE);
        }else{
            itemHolder.btnDelete.setVisibility(View.INVISIBLE);
        }

        itemHolder.imgFile.setOnClickListener(view1 -> {
//            if (mime_type.equals("jpg")){
//                try{
//                    Intent i = new Intent(context, ZoomImageActivity.class);
//                    i.putExtra("gambar", Global.getImageAsByteArrayHighRes(itemHolder.imgFile));
//                    context.startActivity(i);
//                }catch (Exception e){
//                    Log.d("Error", "onClick: "+e.toString());
//                }
//            }else{
//                try {

//                    File file = FileUtil.saveImage(context, data.getRecord_file());
//                    File dst = new File(lokFolder, file.getName());
//                    FileUtil.copyFile(file, dst);

                    // Get URI and MIME type of file
//                    Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", dst);
            try {
                    File file = new  File(JConst.mediaLocationPath +"/"+data.getRecord_file());
//                    Uri uri = Uri.fromFile(file);
                    Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                    MimeTypeMap map = MimeTypeMap.getSingleton();
                    String mimeType = map.getMimeTypeFromExtension(mime_type);

                    // Open file with user selected app
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);

                    //cek kompatibel
                    if (Build.VERSION.SDK_INT >= 24) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(uri, mimeType);
                    } else {
                        intent.setDataAndType(uri, mimeType);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }

                    context.startActivity(intent);
                }catch (Exception e) {
                    e.printStackTrace();
                }
//            }
        });

        itemHolder.tvName.setText(data.getFile_name());
        itemHolder.btnDelete.setOnClickListener(view1 -> {
            int newPosition = itemHolder.getAdapterPosition();
            File file = new  File(JConst.mediaLocationPath+"/"+data.getRecord_file());
            if(file.exists()){
                file.delete();
            }
            removeModel(newPosition);

        });

    }


    public void addModel(RecordFileModel Datas) {
        int pos = this.Datas.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
//        this.dataFiltered.add(Datas);
//        notifyItemRangeInserted(pos, dataFiltered.size());
    }

    public void addModels(List<RecordFileModel> Datas) {
        int pos = this.Datas.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
//        this.dataFiltered.addAll(Datas);
//        notifyItemRangeInserted(pos, dataFiltered.size());
    }

    public void removeAllModel() {
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
//        LastPosition = dataFiltered.size();
//        this.dataFiltered.removeAll(dataFiltered);
        notifyItemRangeRemoved(0, LastPosition);
    }

    public void removeModel(int idx) {
        if (Datas.size() > 0){
            this.Datas.remove(idx);
            notifyItemRemoved(idx);
            notifyDataSetChanged();
        }
    }

    public void setMode(String mode){
        this.mode = mode;
        notifyDataSetChanged();
    }
}
