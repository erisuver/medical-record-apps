package com.orion.pasienqu_2.globals;


import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;

public class NumberTextWatcherForThousand implements TextWatcher {

    TextInputEditText editText;
    Runnable runnable;


    public NumberTextWatcherForThousand(TextInputEditText editText, Runnable runnable) {
        this.editText = editText;
        this.runnable = runnable;

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        editText.removeTextChangedListener(this);
        try {
            String input = s.toString();
            String newPrice = Global.FloatToStrFmt(Global.StrFmtToFloat(input));
//            editText.removeTextChangedListener(this); //To Prevent from Infinite Loop
            if (newPrice.length() < 11) {
                editText.setText(newPrice);
            }else {
                newPrice = "99.999.999";
                editText.setText(newPrice);
            }
            editText.setSelection(newPrice.length()); //Move Cursor to end of String
            editText.addTextChangedListener(this);
            runnable.run();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {



//        try {
//            String originalString = s.toString();
//            String formattedString = Global.FloatToStrFmt(Global.StrFmtToFloat(originalString));
//            editText.setText(formattedString);
//            editText.setSelection(editText.getText().length());
//
//        } catch (NumberFormatException nfe) {
//            nfe.printStackTrace();
//        }
//
//        editText.addTextChangedListener(this);
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
