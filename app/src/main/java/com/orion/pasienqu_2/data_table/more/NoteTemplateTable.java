package com.orion.pasienqu_2.data_table.more;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.models.SyncInfoModel;
import com.orion.pasienqu_2.models.more.NoteTemplateModel;

import java.util.ArrayList;
import java.util.UUID;

public class NoteTemplateTable {
    private SQLiteDatabase db;
    private ArrayList<NoteTemplateModel> records;
    private Context context;
    private String vcategory;
    private boolean archived = false;
    private String valueSort = "";

    public NoteTemplateTable(Context context) {
        this.db = db;
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<NoteTemplateModel>();
        this.context = context;
        vcategory = "";
        Global.setLanguage(context);  // handle bug bahasa pada inform
    }

    public ContentValues setValues (NoteTemplateModel noteTemplateModel, boolean isSave) {
        ContentValues cv = new ContentValues();
        cv.put("uuid", noteTemplateModel.getUuid());
        cv.put("name", noteTemplateModel.getName());
        cv.put("category", noteTemplateModel.getCategory());
        cv.put("template", noteTemplateModel.getTemplate());
        if (isSave){
            cv.put("active", "true");
        }

        return cv;
    }

    public boolean insert(NoteTemplateModel noteTemplateModel, boolean isSinc) {
        if (!validate(noteTemplateModel)) {
            return false;
        }
        ContentValues cv = this.setValues(noteTemplateModel, true);
        this.db.insert("pasienqu_note_template", null, cv);
        this.reloadList();

        if (isSinc) {
            Gson gson = new Gson();
            String json = gson.toJson(noteTemplateModel);
            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.note.template", json.getBytes(), "create", noteTemplateModel.getUuid());
            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        }
        return true;
    }

    public boolean update(NoteTemplateModel noteTemplateModel){
        if (!validate(noteTemplateModel)) {
            return false;
        }
        ContentValues cv = this.setValues(noteTemplateModel, false);
        this.db.update("pasienqu_note_template", cv, "id = " + noteTemplateModel.getId(), null);
        this.reloadList();
        Gson gson = new Gson();
        String json = gson.toJson(noteTemplateModel);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.note.template", json.getBytes(), "write", noteTemplateModel.getUuid());
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);

        return true;
    }

    public void deleteAll() {
        this.db.delete("pasienqu_note_template", null, null);
        this.reloadList();
    }

    public void delete(String uuid){
        this.db.delete("pasienqu_note_template",  "uuid = '" + uuid + "'", null);
        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_note_template", null);

        NoteTemplateModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new NoteTemplateModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getString(cr.getColumnIndexOrThrow("category")),
                        cr.getString(cr.getColumnIndexOrThrow("template")),
                        cr.getInt(cr.getColumnIndexOrThrow("company_id"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }

    }

    private boolean validate(NoteTemplateModel noteTemplateModel){
        Global.setLanguage(context);  // handle bug bahasa pada inform
        int count;
        if (noteTemplateModel.getName().equals("") || noteTemplateModel.getTemplate().equals("")){
            Toast.makeText(context, context.getString(R.string.field_must_be_fill), Toast.LENGTH_LONG).show();
            return false;
        }

        count = Global.getCount(db, "pasienqu_note_template"," name = '" + noteTemplateModel.getName() + "' and category = '"+noteTemplateModel.getCategory()+"' and uuid <> '"+noteTemplateModel.getUuid()+"'");
        if (count > 0) {
            Toast.makeText(context, context.getString(R.string.name_must_be_unique), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public ArrayList<NoteTemplateModel> getRecords() {
        return this.records;
    }


    public ArrayList<NoteTemplateModel> getRecordByCategory(String category ){
        if (this.records.isEmpty()) {
            vcategory = category;
            reloadListCategory();
        }else{
            vcategory = category;
        }
        return this.records;
    }

    private void reloadListCategory(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_note_template where category = '"+vcategory+"'"+addFilter()+addSorting(), null);

        NoteTemplateModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new NoteTemplateModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getString(cr.getColumnIndexOrThrow("category")),
                        cr.getString(cr.getColumnIndexOrThrow("template")),
                        cr.getInt(cr.getColumnIndexOrThrow("company_id"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        //jika data tidak ada --> buat data buat isi field kosong di list
        }else {
            String uuid = UUID.randomUUID().toString();
            tempData = new NoteTemplateModel();
            tempData.setUuid(uuid);
            this.records.add(tempData);
        }
    }


    public NoteTemplateModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public NoteTemplateModel getDataById(long id){
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_note_template where id = "+id, null);

        NoteTemplateModel tempData;
        if (cr != null && cr.moveToFirst()){
                tempData = new NoteTemplateModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getString(cr.getColumnIndexOrThrow("category")),
                        cr.getString(cr.getColumnIndexOrThrow("template")),
                        cr.getInt(cr.getColumnIndexOrThrow("company_id"))
                );
            return tempData;
        }else{
            return null;
        }
    }

    public NoteTemplateModel getDataByUuid(String uuid){
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_note_template where uuid = '"+uuid+"'", null);

        NoteTemplateModel tempData;
        if (cr != null && cr.moveToFirst()){
            tempData = new NoteTemplateModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getString(cr.getColumnIndexOrThrow("uuid")),
                    cr.getString(cr.getColumnIndexOrThrow("name")),
                    cr.getString(cr.getColumnIndexOrThrow("category")),
                    cr.getString(cr.getColumnIndexOrThrow("template")),
                    cr.getInt(cr.getColumnIndexOrThrow("company_id"))
            );
            return tempData;
        }else{
            return null;
        }
    }


    private String addFilter(){
        String filter = "";

        if (archived){
            filter += " and active = 'false'";
        }else{
            filter += " and active = 'true'";
        }
        return filter;
    }

    public void setFilter( boolean archived){
        this.archived = archived;
    }

    public void setSorting(String valueSort) {
        this.valueSort = valueSort;
    }

    private String addSorting(){
        String sortby = "";
        if (valueSort.equals(JConst.value_desc)){
            sortby = " order by name desc ";
        }
        else if (valueSort.equals(JConst.value_asc)){
            sortby = " order by name asc ";
        }
        return sortby;
    }
}
