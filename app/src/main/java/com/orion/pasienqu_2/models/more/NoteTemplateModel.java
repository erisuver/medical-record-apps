package com.orion.pasienqu_2.models.more;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.JConst;

public class NoteTemplateModel {
    private int id;
    private String uuid;
    private String name;
    private String category;
    private String template;
    private int companyId;
    private String mode;

    public NoteTemplateModel(){
        this.id = 0;
        this.uuid = "";
        this.name = "";
        this.category = "";
        this.template = "";
        this.companyId = 0;
    }

    public NoteTemplateModel(int id, String uuid, String name, String category, String template, int companyId) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.category = category;
        this.template = template;
        this.companyId = companyId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String CategoryText(){
        if (this.category.equals(JConst.value_category_patient_remarks)) {
            return "Patient Remarks";
        } else if (this.category.equals(JConst.value_category_anamnesa)) {
            return "Anamnesa";
        } else if (this.category.equals(JConst.value_category_diagnosa)) {
            return "Diagnosa";
        } else if (this.category.equals(JConst.value_category_physical_exam)) {
            return "Physical Exam";
        } else if (this.category.equals(JConst.value_category_therapy)) {
            return "Therapy";
        } else {
            return "Others";
        }
    }

}
