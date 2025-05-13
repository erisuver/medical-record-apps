package com.orion.pasienqu_2.globals;


import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;

public class UppercaseTextWatcher implements TextWatcher {

    TextInputEditText editText;


    public UppercaseTextWatcher(TextInputEditText editText) {
        this.editText = editText;


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        editText.removeTextChangedListener(this);

        try {
            String originalString = s.toString();
            String formattedString = originalString.toUpperCase();
            editText.setText(formattedString);
            editText.setSelection(editText.getText().length());

        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }

        editText.addTextChangedListener(this);
//        try
//        {
////            editText.removeTextChangedListener(this);
////            String value = editText.getText().toString();
////
////
////            if (value != null && !value.equals(""))
////            {
////
////                if(value.startsWith(".")){
////                    editText.setText("0.");
////                }
////                if(value.startsWith("0") && !value.startsWith("0.")){
////                    editText.setText("");
////
////                }
////
////
////                String str = editText.getText().toString().replaceAll(",", "");
////                if (!value.equals(""))
////                    editText.setText(getDecimalFormattedString(str));
////                editText.setSelection(editText.getText().toString().length());
////            }
////            editText.addTextChangedListener(this);
////            return;
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//            editText.addTextChangedListener(this);
//        }

    }

}
