package com.orion.pasienqu_2.globals;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.icu.util.ULocale;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.applandeo.materialcalendarview.CalendarUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.home.home;
import com.orion.pasienqu_2.activities.more.google_drive.BackupRestoreDrive;
import com.orion.pasienqu_2.activities.more.renew_subscriotion.RenewSubscriptionActivity;
import com.orion.pasienqu_2.data_table.RecordFileTable;
import com.orion.pasienqu_2.data_table.RecordTable;
import com.orion.pasienqu_2.models.LoginCompanyModel;
import com.orion.pasienqu_2.models.RecordDiagnosaModel;
import com.orion.pasienqu_2.models.RecordFileModel;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.orion.pasienqu_2.JApplication.fmt;

public class Global {
    public static String getTahun(long date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        String thn = format.format(date);
        return thn;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }


    public static float StrFmtToFloat(String input) {
//
//        final NumberFormat formatter = NumberFormat.getInstance();

        try {
            Number n = fmt.parse(input);
            return n.floatValue();
        } catch (ParseException e) {
            return 0;
        }
    }

    public static float StrFmtToFloatInput(String input) {
        String koma = fmt.format(0.1);

        if (koma.contains(",")) {
            if (!input.contains(",")) {
                input = input.replace(".", ",");
            }
        }
        try {
            Number n = fmt.parse(input);
            return n.floatValue();
        } catch (ParseException e) {
            return 0;
        }
    }


    public static String FloatToStrFmt(double input) {
        return fmt.format(input);
    }

    public static String StrToStrFloatFmt(String input) {
        double i = Global.StrFmtToFloat(input);
        return fmt.format(i);
    }

    public static String FloatToStrFmt(double input, boolean withRpSign) {
        if (withRpSign)
            return "Rp. " + fmt.format(input);
        else
            return fmt.format(input);

    }


    public static final String tag_json_obj = "json_obj_req";

    public static String getBulanTahun(long date) {
        SimpleDateFormat format = new SimpleDateFormat("MMM yyyy");
        String bln = format.format(date);
        return bln;
    }


    public static String getBulan(long date) {
        SimpleDateFormat format = new SimpleDateFormat("MM");
        String bln = format.format(date);
        return bln;
    }

    public static String getHari(long date) {
        SimpleDateFormat format = new SimpleDateFormat("dd");
        String hr = format.format(date);
        return hr;
    }

    public static String getJam(long date) {
        SimpleDateFormat format = new SimpleDateFormat("HH");
        String HH = format.format(date);
        return HH;
    }

    public static String getMenit(long date) {
        SimpleDateFormat format = new SimpleDateFormat("mm");
        String mm = format.format(date);
        return mm;
    }

    public static String getDetik(long date) {
        SimpleDateFormat format = new SimpleDateFormat("ss");
        String ss = format.format(date);
        return ss;
    }

    public static String getDateFormated(long date) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }

    public static String getDateFormated(long date, String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }

    public static long addDay(long date, int countDay) {
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        calender.add(Calendar.DAY_OF_YEAR, countDay);
        return calender.getTimeInMillis();
    }

    public static long serverNowLong(int minusDay) {
        //DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.DATE, -minusDay);
        return calender.getTimeInMillis();
    }

    public static long convertDateGMT(long date) {
        Calendar calender = Calendar.getInstance();
        TimeZone mTimeZone = calender.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        long gmt = TimeUnit.MILLISECONDS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
        date = date + gmt;
        return date;
    }

    public static String getDateFormatedList(long date) {
        DateFormat formatter = new SimpleDateFormat("dd/MM");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }

    public static String getDateTimeFormated(long date) {
        if (date == 0) {
            return "";
        }
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }

    public static String getTimeFormated(long date) {
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }


    public static String getDateTimeFormatedList(long date) {
        if (date == 0) {
            return "";
        }
        DateFormat formatter = new SimpleDateFormat("dd/MM  HH:mm");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }


    public static String getDateTimeFormatedList(long date, String defZero) {
        if (date == 0) {
            return defZero;
        }
        return getDateTimeFormatedList(date);
    }


    public static String getDateTimeFormated(long date, String defZero) {
        if (date == 0) {
            return defZero;
        }
        return getDateTimeFormated(date);
    }

    public static String getTglFmt(long date, String Format) {
        DateFormat formatter = new SimpleDateFormat(Format);
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }


    public static String getDateFormatedNonDay(long date) {
        DateFormat formatter = new SimpleDateFormat("MM-yyyy");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }

    public static String getDateFormatedBulanTahun(long date) {
        DateFormat formatter = new SimpleDateFormat("MMMM yyyy");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }

    public static String getDateFormatedOdoo(long date) {
        if (date == 0)
            date = serverNowLong();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calender = Calendar.getInstance();
        TimeZone mTimeZone = calender.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        long gmttozero = TimeUnit.MILLISECONDS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
        date = date - gmttozero;
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }


    public static String getDateTimeFormatedOdoo(long date) {

        Calendar calender = Calendar.getInstance();
        TimeZone mTimeZone = calender.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        long gmttozero = TimeUnit.MILLISECONDS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
        date = date - gmttozero;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
//        Calendar calender = Calendar.getInstance();
//        TimeZone timeZone = TimeZone.getTimeZone("UTC");
//        Calendar calender = Calendar.getInstance(TimeZone.getTimeZone("GMT-7"));
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }

    public static String serverNowFormated() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calender = Calendar.getInstance();
        return formatter.format(calender.getTimeInMillis());
    }

    public static String serverNowFormated(long date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    public static String serverNowFormatedWithTime() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calender = Calendar.getInstance();
        TimeZone mTimeZone = calender.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        long gmttozero = TimeUnit.MILLISECONDS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
        long date = calender.getTimeInMillis();
        date = date - gmttozero;

        return formatter.format(date);
    }

    public static String serverNowTimeFormated() {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Calendar calender = Calendar.getInstance();
        return formatter.format(calender.getTimeInMillis());
    }

    public static String getStartDateFormatOdoo(long date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime()) + "-01";
    }

    public static String getEndDateFormatMySql(long date, int endMonth) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime()) + "-" + String.valueOf(endMonth);
    }

    public static String getDayFromNow(long startDate, int day) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(startDate);
        calender.add(Calendar.DATE, day);
        return formatter.format(calender.getTime());
    }

    public static String getDayFromNowFormated(long startDate, int day, String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(startDate);
        calender.add(Calendar.DATE, day);
        return formatter.format(calender.getTime());
    }

    public static String getMinutes(long date) {
        Calendar now = Calendar.getInstance();
        int dayNow = now.get(Calendar.DAY_OF_MONTH);
        int hourNow = now.get(Calendar.HOUR_OF_DAY);
        int minuteNow = now.get(Calendar.MINUTE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        int setMinutes = ((day * 24 * 60) + (hour * 60) + minute) - ((dayNow * 24 * 60) + (hourNow * 60) + minuteNow);
        return String.valueOf(setMinutes + 1);
    }

    public static long serverNowStartOfTheMonthLong() {
        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.DAY_OF_MONTH, 1);
        calender.set(Calendar.HOUR_OF_DAY, 0);
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);
        calender.set(Calendar.MILLISECOND, 0);
        return calender.getTimeInMillis();
    }


    public static String serverNowFormatedBulanTahun() {
        DateFormat formatter = new SimpleDateFormat("MMMM yyyy");
        Calendar calender = Calendar.getInstance();
        return formatter.format(calender.getTimeInMillis());
    }

    public static long serverNowLong() {
        //DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calender = Calendar.getInstance();
        return calender.getTimeInMillis();
    }

    public static long serverNowWithoutTimeLong() {
        //DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.HOUR_OF_DAY, 0);
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);
        calender.set(Calendar.MILLISECOND, 0);
        return calender.getTimeInMillis();
    }

    public static long DateNowGMT() {
        long date = serverNowWithoutTimeLong();
        Calendar calender = Calendar.getInstance();
        TimeZone mTimeZone = calender.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        long gmt = TimeUnit.MILLISECONDS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
        date = date + gmt;
        return date;
    }

    public static long getMillisDateGMT(String input) {
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        Date d = null;
        try {
            d = f.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = 0;
        if (d != null) {
            Calendar calender = Calendar.getInstance();
            TimeZone mTimeZone = calender.getTimeZone();
            int mGMTOffset = mTimeZone.getRawOffset();
            long gmt = TimeUnit.MILLISECONDS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
            date = d.getTime();
            date = date + gmt;
            return date;
        }
        return 0;
    }

    public static long Addminute(long date, int countMinute) {
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        calender.add(Calendar.MINUTE, countMinute);
        return calender.getTimeInMillis();
    }

    public static long MinusMinute(long date, int countMinute) {
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        calender.add(Calendar.MINUTE, -countMinute);  //(-) minus sebelum tanggal janjian
        return calender.getTimeInMillis();
    }


    public static long serverNowLongPlus(int plusDay) {
        //DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.DATE, plusDay);
        return calender.getTimeInMillis();
    }

    public static String serverNowFormated4Ekspor() {
        DateFormat formatter = new SimpleDateFormat("ddMMyyyy_HHmm");
        Calendar calender = Calendar.getInstance();
        return formatter.format(calender.getTimeInMillis());
    }

    public static String serverNow() {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Calendar calender = Calendar.getInstance();
        return formatter.format(calender.getTime());
    }

    public static String serverNowWithoutTime() {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.HOUR, 0);
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);
        return formatter.format(calender.getTime());
    }


    public static String serverNowWithTime() {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Calendar calender = Calendar.getInstance();
        return formatter.format(calender.getTime());
    }

    public static String serverNow4Nomor() {
        DateFormat formatter = new SimpleDateFormat("MMyy");

        Calendar calender = Calendar.getInstance();
        return formatter.format(calender.getTime());
    }

    public static long EndOfTheMonthLong(Long date) {
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        calender.set(Calendar.DAY_OF_MONTH, calender.getActualMaximum(Calendar.DAY_OF_MONTH));
        calender.set(Calendar.HOUR_OF_DAY, calender.getActualMaximum(Calendar.HOUR_OF_DAY));
        calender.set(Calendar.MINUTE, calender.getActualMaximum(Calendar.MINUTE));
        calender.set(Calendar.SECOND, calender.getActualMaximum(Calendar.SECOND));
        calender.set(Calendar.MILLISECOND, calender.getActualMaximum(Calendar.MILLISECOND));
        return calender.getTimeInMillis();
    }


    public static long getMillisDate(String input) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        Date d = null;
        try {
            d = f.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = 0;
        if (d != null) {
            date = d.getTime();
            return date;
        }
        return 0;
    }

    public static long getMillisDateStrip(String input) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");


        Date d = null;
        try {
            DateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            Date date = parser.parse(input);
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String output = formatter.format(date);

            d = f.parse(output);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = 0;
        if (d != null) {
            date = d.getTime();
            return date;
        }
        return 0;
    }

    public static long getMillisDateTime(String input) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date d = null;
        try {
            d = f.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = 0;
        if (d != null) {
            date = d.getTime();
            return date;
        }
        return 0;
    }

    public static long getMillisDateFmt(String input, String fmt) {
        SimpleDateFormat f = new SimpleDateFormat(fmt);
        Date d = null;
        try {
            d = f.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = 0;
        if (d != null) {
            date = d.getTime();
            return date;
        }
        return 0;
    }

    public static long getMillisDateTimeZone(String input) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date d = null;
        try {
            d = f.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = 0;
        if (d != null) {
            date = d.getTime();
            Calendar calender = Calendar.getInstance();
            TimeZone mTimeZone = calender.getTimeZone();
            int mGMTOffset = mTimeZone.getRawOffset();
            long gmttozero = TimeUnit.MILLISECONDS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
            date = date + gmttozero;
            return date;
        }
        return 0;
    }


    public static long getMillisDateFmtFromOdoo(String input, String fmt) {
        SimpleDateFormat f = new SimpleDateFormat(fmt);
        Date d = null;
        try {
            d = f.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = 0;
        if (d != null) {
            date = d.getTime();
            Calendar calender = Calendar.getInstance();
            TimeZone mTimeZone = calender.getTimeZone();
            int mGMTOffset = mTimeZone.getRawOffset();
            long gmttozero = TimeUnit.MILLISECONDS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
            date = date + gmttozero;
            return date;
        }
        return 0;
    }

    public static long getMillisDateTimeFromOdoo(String input) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Date d = null;
        try {
            DateFormat parser = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            Date date = parser.parse(input);
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String output = formatter.format(date);

            d = f.parse(output);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = 0;
        if (d != null) {
            date = d.getTime();
            Calendar calender = Calendar.getInstance();
            TimeZone mTimeZone = calender.getTimeZone();
            int mGMTOffset = mTimeZone.getRawOffset();
            long gmttozero = TimeUnit.MILLISECONDS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
            date = date + gmttozero;
            return date;
        }
        return 0;
    }

    public static long getMillisDateFromOdoo(String input) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        Date d = null;
        try {
            d = f.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = 0;
        if (d != null) {
            date = d.getTime();
            Calendar calender = Calendar.getInstance();
            TimeZone mTimeZone = calender.getTimeZone();
            int mGMTOffset = mTimeZone.getRawOffset();
            long gmttozero = TimeUnit.MILLISECONDS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
            date = date + gmttozero;
            return date;
        }
        return 0;
    }

    public static void inform(Context context, String pesan, String title) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(pesan)
                .setCancelable(true)
                .setPositiveButton("Ok", null)
                .show();
    }

    public static boolean Confirm(Context context, String message) {
        final boolean[] answer = new boolean[3];
        answer[1] = false;
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Confirmation");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                answer[0] = true;
                answer[1] = true;
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                answer[0] = false;
                answer[1] = true;
            }
        });
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
        while (answer[1] = true) {
            answer[2] = true;
        }
        return answer[0];
    }

    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (activity != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (NullPointerException e) {

        }
    }

    public static double Round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static void dtpClick(Activity activity, TextInputEditText textTemp, View view, Boolean formatBulan, Runnable dissmis) {
        setLanguage(activity);
        hideSoftKeyboard(activity, view);
        if (textTemp.getText().toString().equals("")) {
            textTemp.setText(Global.serverNow());
        }
        int mYear = 0;
        int mMonth = 0;
        int mDay = 0;
        if (formatBulan) {
            Long tgl = Global.getMillisDateFmt(textTemp.getText().toString(), "MMM yyyy");
            mYear = (Integer.parseInt(Global.getTahun(tgl)));
            mMonth = (Integer.parseInt(Global.getBulan(tgl))) - 1;
            mDay = 1;
        } else {
            Long tgl = Global.getMillisDate(textTemp.getText().toString());
            mYear = (Integer.parseInt(Global.getTahun(tgl)));
            mMonth = (Integer.parseInt(Global.getBulan(tgl))) - 1;
            mDay = (Integer.parseInt(Global.getHari(tgl)));
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                (view1, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    SimpleDateFormat format = new SimpleDateFormat("MMyy");
                    if (formatBulan) {
                        format = new SimpleDateFormat("MMM yyyy");

                    } else {
                        format = new SimpleDateFormat("dd/MM/yyyy");
                    }
                    textTemp.setText(format.format(calendar.getTime()));

                }, mYear, mMonth, mDay);
        datePickerDialog.setOnDismissListener(dialogInterface -> {
            dissmis.run();
        });
        datePickerDialog.show();
    }

    public static void dtpClickDisableFutureDate(Activity activity, TextInputEditText textTemp, View view, Boolean formatBulan, Runnable dissmis) {
        hideSoftKeyboard(activity, view);
        setLanguage(activity);

        if (textTemp.getText().toString().equals("")) {
            textTemp.setText(Global.serverNow());
        }
        int mYear = 0;
        int mMonth = 0;
        int mDay = 0;
        if (formatBulan) {
            Long tgl = Global.getMillisDateFmt(textTemp.getText().toString(), "MMM yyyy");
            mYear = (Integer.parseInt(Global.getTahun(tgl)));
            mMonth = (Integer.parseInt(Global.getBulan(tgl))) - 1;
            mDay = 1;
        } else {
            Long tgl = Global.getMillisDate(textTemp.getText().toString());
            mYear = (Integer.parseInt(Global.getTahun(tgl)));
            mMonth = (Integer.parseInt(Global.getBulan(tgl))) - 1;
            mDay = (Integer.parseInt(Global.getHari(tgl)));
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                (view1, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();

                    calendar.set(year, monthOfYear, dayOfMonth);
                    SimpleDateFormat format = new SimpleDateFormat("MMyy");
                    if (formatBulan) {
                        format = new SimpleDateFormat("MMM yyyy");

                    } else {
                        format = new SimpleDateFormat("dd/MM/yyyy");
                    }
                    textTemp.setText(format.format(calendar.getTime()));

                }, mYear, mMonth, mDay);
        datePickerDialog.setOnDismissListener(dialogInterface -> {
            dissmis.run();
        });
        datePickerDialog.getDatePicker().setMaxDate(serverNowLong());  //disable past date
        datePickerDialog.show();
    }

    public static void datePickerClick(Activity activity, TextInputEditText textTemp, View view) {
        hideSoftKeyboard(activity, view);
        if (textTemp.getText().toString().equals("")) {
            textTemp.setText(Global.serverNow());
        }
        int mYear = 0;
        int mMonth = 0;
        int mDay = 0;

        Long tgl = Global.getMillisDate(textTemp.getText().toString());
        mYear = (Integer.parseInt(Global.getTahun(tgl)));
        mMonth = (Integer.parseInt(Global.getBulan(tgl))) - 1;
        mDay = (Integer.parseInt(Global.getHari(tgl)));


        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                (view1, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    SimpleDateFormat format = new SimpleDateFormat("MMyy");
                    format = new SimpleDateFormat("dd/MM/yyyy");
                    textTemp.setText(format.format(calendar.getTime()));

                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(serverNowLong());  //disable future date
        datePickerDialog.show();
    }

    public static void datePickerEdittextClick(Activity activity, EditText textTemp, View view, Runnable runnable) {
        hideSoftKeyboard(activity, view);
        if (textTemp.getText().toString().equals("")) {
            textTemp.setText(Global.serverNow());
        }
        int mYear = 0;
        int mMonth = 0;
        int mDay = 0;

        Long tgl = Global.getMillisDate(textTemp.getText().toString());
        mYear = (Integer.parseInt(Global.getTahun(tgl)));
        mMonth = (Integer.parseInt(Global.getBulan(tgl))) - 1;
        mDay = (Integer.parseInt(Global.getHari(tgl)));


        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                (view1, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    SimpleDateFormat format = new SimpleDateFormat("MMyy");
                    format = new SimpleDateFormat("dd/MM/yyyy");
                    textTemp.setText(format.format(calendar.getTime()));
                    runnable.run();

                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(serverNowLong());  //disable future date
        datePickerDialog.show();
    }


    public static void dtpTimeClick(Activity activity, TextInputEditText textTemp, View view) {
        hideSoftKeyboard(activity, view);
        setLanguage(activity);
        if (textTemp.getText().toString().equals("")) {
            textTemp.setText(Global.serverNowWithTime());
        }
        int mYear = 0;
        int mMonth = 0;
        int mDay = 0;

        Long tgl = Global.getMillisDateTime(textTemp.getText().toString());
        mYear = (Integer.parseInt(Global.getTahun(tgl)));
        mMonth = (Integer.parseInt(Global.getBulan(tgl))) - 1;
        mDay = (Integer.parseInt(Global.getHari(tgl)));
        final int mHour = (Integer.parseInt(Global.getJam(tgl)));
        final int mMinute = (Integer.parseInt(Global.getMenit(tgl)));

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                (view1, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth, mHour, mMinute);
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    textTemp.setText(format.format(calendar.getTime()));
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        datePickerDialog.setOnDismissListener(dialogInterface -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(activity, (timePicker, hour, minute) -> {
                Long tempTgl = Global.getMillisDateTime(textTemp.getText().toString());
                int tempYear = (Integer.parseInt(Global.getTahun(tempTgl)));
                int tempMonth = (Integer.parseInt(Global.getBulan(tempTgl))) - 1;
                int tempDay = (Integer.parseInt(Global.getHari(tempTgl)));

                Calendar calendar = Calendar.getInstance();
                calendar.set(tempYear, tempMonth, tempDay, hour, minute);
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                textTemp.setText(format.format(calendar.getTime()));
            }, mHour, mMinute, false);
            timePickerDialog.show();
        });
    }

    public static void dtpTimeClickTextview(Activity activity, TextView textTemp, View view, Runnable runDismiss) {
        hideSoftKeyboard(activity, view);
        setLanguage(activity);
        int mYear = 0;
        int mMonth = 0;
        int mDay = 0;
        String txtDefault = textTemp.getText().toString();

        Long tgl = Global.getMillisDateTime(serverNowWithTime());
        mYear = (Integer.parseInt(Global.getTahun(tgl)));
        mMonth = (Integer.parseInt(Global.getBulan(tgl))) - 1;
        mDay = (Integer.parseInt(Global.getHari(tgl)));
        final int mHour = (Integer.parseInt(Global.getJam(tgl)));
        final int mMinute = (Integer.parseInt(Global.getMenit(tgl)));

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                (view1, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth, mHour, mMinute);
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    textTemp.setText(format.format(calendar.getTime()));
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(serverNowLong());  //disable past date
        datePickerDialog.show();
        datePickerDialog.setOnDismissListener(dialogInterface -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(activity, (timePicker, hour, minute) -> {
                Long tempTgl = Global.getMillisDateTime(textTemp.getText().toString());
                int tempYear = (Integer.parseInt(Global.getTahun(tempTgl)));
                int tempMonth = (Integer.parseInt(Global.getBulan(tempTgl))) - 1;
                int tempDay = (Integer.parseInt(Global.getHari(tempTgl)));

                Calendar calendar = Calendar.getInstance();
                calendar.set(tempYear, tempMonth, tempDay, hour, minute);
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                textTemp.setText(format.format(calendar.getTime()));
            }, mHour, mMinute, true);
            timePickerDialog.show();
            timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    runDismiss.run();
                }
            });

            timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    textTemp.setText("");
                }
            });
        });
    }

    public static void dtpTimeClickDisablePastDate(Activity activity, TextInputEditText textTemp, View view) {
        hideSoftKeyboard(activity, view);
        setLocale(activity);
//        setLanguage(activity);
//        SharedPreferences sharedPreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
//        String language = sharedPreferences.getString("language", "en");
//        if(language.equals("in")) {
//            Locale locale = new Locale("in");
//            Locale.setDefault(locale);
//        }else{
//            Locale locale = new Locale("en");
//            Locale.setDefault(locale);
//        }

        if (textTemp.getText().toString().equals("")) {
            textTemp.setText(Global.serverNowWithTime());
        }
        int mYear = 0;
        int mMonth = 0;
        int mDay = 0;

        Long tgl = Global.getMillisDateTime(textTemp.getText().toString());
        mYear = (Integer.parseInt(Global.getTahun(tgl)));
        mMonth = (Integer.parseInt(Global.getBulan(tgl))) - 1;
        mDay = (Integer.parseInt(Global.getHari(tgl)));
        final int mHour = (Integer.parseInt(Global.getJam(tgl)));
        final int mMinute = (Integer.parseInt(Global.getMenit(tgl)));

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                (view1, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth, mHour, mMinute);
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    textTemp.setText(format.format(calendar.getTime()));
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(serverNowLong());  //disable past date
        datePickerDialog.show();
        datePickerDialog.setOnDismissListener(dialogInterface -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(activity, (timePicker, hour, minute) -> {
                Long tempTgl = Global.getMillisDateTime(textTemp.getText().toString());
                int tempYear = (Integer.parseInt(Global.getTahun(tempTgl)));
                int tempMonth = (Integer.parseInt(Global.getBulan(tempTgl))) - 1;
                int tempDay = (Integer.parseInt(Global.getHari(tempTgl)));

                Calendar calendar = Calendar.getInstance();
                calendar.set(tempYear, tempMonth, tempDay, hour, minute);
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                textTemp.setText(format.format(calendar.getTime()));
            }, mHour, mMinute, true);
            timePickerDialog.show();
        });
    }

    public static void dtpTimeClick(Activity activity, TextInputEditText textTemp, View view, final Runnable func) {
        hideSoftKeyboard(activity, view);
        setLanguage(activity);
        if (textTemp.getText().toString().equals("")) {
            textTemp.setText(Global.serverNowWithTime());
        }
        int mYear = 0;
        int mMonth = 0;
        int mDay = 0;

        Long tgl = Global.getMillisDateTime(textTemp.getText().toString());
        mYear = (Integer.parseInt(Global.getTahun(tgl)));
        mMonth = (Integer.parseInt(Global.getBulan(tgl))) - 1;
        mDay = (Integer.parseInt(Global.getHari(tgl)));
        final int mHour = (Integer.parseInt(Global.getJam(tgl)));
        final int mMinute = (Integer.parseInt(Global.getMenit(tgl)));

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                (view1, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth, mHour, mMinute);
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    textTemp.setText(format.format(calendar.getTime()));
                    func.run();
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        datePickerDialog.setOnDismissListener(dialogInterface -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(activity, (timePicker, hour, minute) -> {
                Long tempTgl = Global.getMillisDateTime(textTemp.getText().toString());
                int tempYear = (Integer.parseInt(Global.getTahun(tempTgl)));
                int tempMonth = (Integer.parseInt(Global.getBulan(tempTgl))) - 1;
                int tempDay = (Integer.parseInt(Global.getHari(tempTgl)));

                Calendar calendar = Calendar.getInstance();
                calendar.set(tempYear, tempMonth, tempDay, hour, minute);
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                textTemp.setText(format.format(calendar.getTime()));
                func.run();
            }, mHour, mMinute, false);
            timePickerDialog.show();
        });
    }

    public static void setLocale(Activity activity) {
        SharedPreferences sharedPreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "en");
        if (language.equals("in")) {
            Locale locale = new Locale("in");
            Locale.setDefault(locale);
            Configuration config = activity.getResources().getConfiguration();
            config.locale = locale;
            activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
        } else {
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            Configuration config = activity.getResources().getConfiguration();
            config.locale = locale;
            activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
        }
        ;
    }

//    public static String getImageAsByteArray(ImageView imgView) {
//        BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable();
//        Bitmap bitmap = drawable.getBitmap();
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
//        byte[] byteArrayImage = outputStream.toByteArray();
//        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
//        return encodedImage;
//    }

    public static String getImageAsByteArray(ImageView imgView) {
        BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        double widthTemp = (double) ((double) height / (double) 750);
        widthTemp = (double) width / (double) (widthTemp);
        int widthHasil = (int) widthTemp;
        Bitmap b;
        if (height > 500) {
            b = scaleBitmap(bitmap, widthHasil, 750);
        } else {
            b = bitmap;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] byteArrayImage = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        Log.d("TAG", "getImageAsByteArray: " + encodedImage);
        return encodedImage;
    }

    public static String getImageAsByteArrayHighRes(ImageView imgView) {
        BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
//
//        int height = bitmap.getHeight();
//        int width = bitmap.getWidth();
//        double widthTemp = (double) ((double) height / (double) 1024);
//        widthTemp = (double) width / (double) (widthTemp);
//        int widthHasil = (int) widthTemp;
//        Bitmap b;
//        if (height > 640) {
//            b = scaleBitmap(bitmap, widthHasil, 1024);
//        }else{
//            b = bitmap;
//        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        byte[] byteArrayImage = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        Log.d("TAG", "getImageAsByteArray: " + encodedImage);
        return encodedImage;
    }


    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }


    public static String getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        byte[] byteArrayImage = byteBuffer.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return encodedImage;
    }

    public static Bitmap BitmapFileFromUri(Uri fileUri, Context context) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(fileUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        double widthTemp = (double) ((double) height / (double) 1024);
        widthTemp = (double) width / (double) (widthTemp);
        int widthHasil = (int) widthTemp;
        Bitmap bm;
        if (height > 640) {
            bm = scaleBitmap(bitmap, widthHasil, 1024);
        } else {
            bm = bitmap;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 70, out);
        Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

//        try {
//            String dir = Environment.getExternalStorageDirectory().toString() + "/DCIM/1.jpg";
//            //            String dir = Environment.getExternalStorageDirectory().toString() + "/DCIM/dbbackup.db";
//            java.io.FileOutputStream fos = new FileOutputStream(new java.io.File(dir));
//            out.writeTo(fos);
//        }catch (IOException e){
//
//        }

        return decoded;
    }

    public static String getStringFile(File f) {
        InputStream inputStream = null;
        String encodedFile = "", lastVal;
        try {
            inputStream = new FileInputStream(f.getAbsolutePath());

            byte[] buffer = new byte[10240];//specify the size to allow
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();
            encodedFile = output.toString();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
    }

    public static File compressBitmapFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public static File handleRotateCompressedBitmapFile(File file, Uri selectedImage, Context context) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            selectedBitmap = rotateImageIfRequired(context, selectedBitmap, selectedImage);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getTimeStringFromDouble(double time) {
        double value = 61200000;//karena jika 0 defaultnya jam  7, jadi biar jadi jam 00.00 ditambah ini
        value = value + (time * 3600000);
        long valueTime = (new Double(value)).longValue();
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(valueTime);
        return formatter.format(calender.getTime());
    }

    public static void insert_after_login(Context context, Application appContext, String userId) {
//        LoginTable loginTable = ((JApplication) getApplicationContext()).loginTable;
//        LoginTable loginTable = ((JApplication) appContext).loginTable;
//
//        loginTable.deleteAll();
//        loginTable.deleteAll();
//        LoginModel loginModel = new LoginModel();
//        loginModel.setContract_id(10);
//        loginModel.setCust_name("Universal");
//        loginModel.setDriver_id(5);
//        loginModel.setUser_id(userId);
//        loginModel.setVehicle_id(3);
//        loginModel.setContract_no("201001");
//        loginModel.setEnd_date(Global.serverNowLong());
//        loginModel.setStart_date(Global.serverNowLong());
//        loginTable.insert(loginModel);
//
//        WorkingTimeTable workingTimeTable = new WorkingTimeTable(context);
//        workingTimeTable.deleteAll();
//        WorkingTimeModel workingTimeModel = new WorkingTimeModel();
//        workingTimeModel.setName("Senin");
//        workingTimeModel.setHour_from(8);
//        workingTimeModel.setHour_to(17);
//        workingTimeTable.insert(workingTimeModel);
//
//
//        workingTimeModel.setName("Selasa");
//        workingTimeModel.setHour_from(8);
//        workingTimeModel.setHour_to(17);
//        workingTimeTable.insert(workingTimeModel);
//
//
//        workingTimeModel.setName("Rabu");
//        workingTimeModel.setHour_from(8);
//        workingTimeModel.setHour_to(17);
//        workingTimeTable.insert(workingTimeModel);
//
//        workingTimeModel.setName("Kamis");
//        workingTimeModel.setHour_from(8);
//        workingTimeModel.setHour_to(17);
//        workingTimeTable.insert(workingTimeModel);
//
//        workingTimeModel.setName("Jumat");
//        workingTimeModel.setHour_from(8);
//        workingTimeModel.setHour_to(17);
//        workingTimeTable.insert(workingTimeModel);
//
//        workingTimeModel.setName("Sabtu");
//        workingTimeModel.setHour_from(8);
//        workingTimeModel.setHour_to(12);
//        workingTimeTable.insert(workingTimeModel);
//
//
//        loginModel.setWorking_times(workingTimeTable.getRecords());
//        JApplication.getInstance().setDtLoginGlobal(loginModel);

//
//        DailyTripCostCenterTable dailyTripCostCenterTable = ((JApplication) appContext).dailyTripCostCenterTable;
//        dailyTripCostCenterTable.deleteAll();
//        DailyTripCostCenterModel dailyTripCostCenterModel = new DailyTripCostCenterModel();
//        dailyTripCostCenterModel.setContract_id(1);
//        dailyTripCostCenterModel.setId(1);
//        dailyTripCostCenterModel.setName("Direksi");
//        dailyTripCostCenterModel.setPin("123");
//        dailyTripCostCenterTable.insert(dailyTripCostCenterModel);
//
//        dailyTripCostCenterModel.setContract_id(1);
//        dailyTripCostCenterModel.setId(2);
//        dailyTripCostCenterModel.setName("Manajemen");
//        dailyTripCostCenterModel.setPin("123");
//        dailyTripCostCenterTable.insert(dailyTripCostCenterModel);
//
//        dailyTripCostCenterModel.setContract_id(1);
//        dailyTripCostCenterModel.setId(3);
//        dailyTripCostCenterModel.setName("Marketing");
//        dailyTripCostCenterModel.setPin("123");
//        dailyTripCostCenterTable.insert(dailyTripCostCenterModel);


//        ProductTable ProductTable = ((JApplication) appContext).productTable;
//        ProductTable.deleteAll();
////        Global.sinkBarang(appContext);
//        ProductModel ProductModel = new ProductModel();
//        ProductModel.setId(1);
//        ProductModel.setName("Fuel");
//        ProductTable.insert(ProductModel);
//
//        ProductModel.setId(2);
//        ProductModel.setName("Other Trip Expense");
//        ProductTable.insert(ProductModel);
//
//        ProductModel.setId(3);
//        ProductModel.setName("Parking");
//        ProductTable.insert(ProductModel);
//
//        ProductModel.setId(4);
//        ProductModel.setName("Toll");
//        ProductTable.insert(ProductModel);
    }


    public static int getCount(SQLiteDatabase db, String tableName, String where) {
        String sql = "SELECT count(*) as jml FROM " + tableName;
        if (!where.equals("")) {
            sql = sql + " where " + where;
        }
        int result = 0;
        Cursor cr = db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()) {
            result = cr.getInt(cr.getColumnIndexOrThrow("jml"));
        }
        return result;
    }

    public static ProgressDialog showProgressDialog(Context appContext, Activity activity) {
        ProgressDialog loading = new ProgressDialog(activity);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.setMessage(appContext.getString(R.string.loading));
        loading.show();
        return loading;
    }


    public static boolean CheckConnectionOdoo(Context context) {
        boolean connected = true;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        if (connected) {
            Global.isInternetAvailable();
        }
        return connected;
    }

    public static boolean CheckConnectionInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }


    public static boolean isInternetAvailable() {
        try {
            String command = "ping -c 1 " + JConst.HOST_SERVER_WITHOUT_HTTP;
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        } catch (InterruptedException e) {
            // Log error
        } catch (IOException e) {
        }
        return false;
    }

    public static void setEnabledTextInputEditText(TextInputEditText txtInput, boolean enabled) {
        txtInput.setEnabled(enabled);
        txtInput.setSelectAllOnFocus(enabled);
        txtInput.setFocusable(enabled);
        txtInput.setFocusableInTouchMode(enabled);
        if (!enabled) {
            txtInput.setSelected(false);
        }
    }

    public static void setEnabledAutoCompleteText(AutoCompleteTextView txtInput, boolean enabled) {
//        txtInput.setEnabled(enabled);
        txtInput.setSelectAllOnFocus(enabled);
        txtInput.setFocusable(enabled);
        txtInput.setFocusableInTouchMode(enabled);
        if (!enabled) {
            txtInput.setSelected(false);
        }
    }

    public static void setEnabledClickText(TextInputEditText txtInput, boolean enabled) {
        txtInput.setSelectAllOnFocus(enabled);
        txtInput.setFocusable(enabled);
        txtInput.setFocusableInTouchMode(enabled);
        if (!enabled) {
            txtInput.setSelected(false);
        }
    }

    public static void setEnabledClickAutoCompleteText(AutoCompleteTextView txtInput, boolean enabled) {
        txtInput.setSelectAllOnFocus(enabled);
        txtInput.setFocusable(enabled);
        txtInput.setFocusableInTouchMode(enabled);
        if (!enabled) {
            txtInput.setSelected(false);
        }
    }


    public static void setEnabledAutoCompleteTextView(AutoCompleteTextView txtInput, boolean enabled) {
        txtInput.setEnabled(enabled);
        txtInput.setSelectAllOnFocus(enabled);
        txtInput.setFocusable(enabled);
        if (!enabled) {
            txtInput.setSelected(false);
        }
    }

    public static void dtpClickAge(Activity activity, TextInputEditText textTemp, TextInputEditText txtAge, TextInputEditText txtMonth, View view) {
        hideSoftKeyboard(activity, view);
        if (textTemp.getText().toString().equals("")) {
            textTemp.setText(Global.serverNowFormated());
        }
        int mYear = 0;
        int mMonth = 0;
        int mDay = 0;

        Long tgl = Global.getMillisDate(textTemp.getText().toString());
        mYear = (Integer.parseInt(Global.getTahun(tgl)));
        mMonth = (Integer.parseInt(Global.getBulan(tgl))) - 1;
        mDay = (Integer.parseInt(Global.getHari(tgl)));

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                (view1, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    SimpleDateFormat format = new SimpleDateFormat("MMyy");
                    format = new SimpleDateFormat("dd/MM/yyyy");
                    textTemp.setText(format.format(calendar.getTime()));
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Long tempTgl = Global.getMillisDateTime(textTemp.getText().toString());
                int tempYear = (Integer.parseInt(Global.getTahun(tempTgl)));
                int tempMonth = (Integer.parseInt(Global.getBulan(tempTgl)));
                int tempDay = (Integer.parseInt(Global.getHari(tempTgl)));
                txtAge.setText(String.valueOf(tempYear));
                txtMonth.setText(String.valueOf(tempMonth));

            }
        });
    }

    public static void getBirthofDate(int addyear, int addmonth, TextInputEditText dateBirth, String defaultDate) {
        Long LtglNow;
        LtglNow = Global.serverNowLong();


        int month, year, day;
        month = Integer.parseInt(Global.getBulan(LtglNow)) - addmonth;
        year = Integer.parseInt(Global.getTahun(LtglNow)) - addyear;
        day = Integer.parseInt(Global.getHari(LtglNow));

        if (month <= 0) {
            month = month + 12;
            year = year - 1;
        }
        day = Integer.parseInt(Global.getHari(LtglNow));
        String dayStr = String.valueOf(day);
        String monthStr = String.valueOf(month);
        if (day < 10) {
            dayStr = "0" + day;
        }
        if (month < 10) {
            monthStr = "0" + month;
        }
        String date = dayStr + "/" + monthStr + "/" + year;

//        if (addmonth == 0 && addyear == 0){
//            if (defaultDate.equals("")){
//                dateBirth.setText(serverNow());
//            }else {
//                dateBirth.setText(defaultDate);
//            }
//            return;
//        }
        dateBirth.setText(date);
    }

    public static int getUmur(Date dateNow, Date dateBirth, boolean isAge) {

        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(dateBirth);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(dateNow);

        int diffYear = (endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR));
        int diffMonth = (endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH));
        int diffDay = endCalendar.get(Calendar.DAY_OF_MONTH) - startCalendar.get(Calendar.DAY_OF_MONTH);

        Log.w("bulan diffYear", String.valueOf(diffYear));
        Log.w("bulan diffMonth", String.valueOf(diffMonth));
        Log.w("bulan diffDay", String.valueOf(diffDay));
        if ((diffDay < 0)) {
            if (diffMonth == 0) {
                diffYear -= 1;
                diffMonth = 11;
            } else {
                diffMonth -= 1;
            }
        }

        if ((diffMonth < 0)) {
            Log.w("bulan awal", String.valueOf(diffMonth));
            diffYear -= 1;
            diffMonth += 12;
            Log.w("bulan", String.valueOf(diffMonth));
            Log.w("tahun", String.valueOf(diffYear));
        }
        //String hasil = diffYear + " Thn " + diffMonth + " Bln";
        if (isAge) {
            return diffYear;
        } else {
            return diffMonth;
        }
    }

    public static int getAgeMonth(long date, boolean age, boolean month) {
        int result = 0;

        long LDateNow = Global.serverNowLong();
        long LDateBirth = date;

        Date dateBirth, dateNow;
        dateBirth = new Date(Integer.parseInt(Global.getTahun(LDateBirth)), Integer.parseInt(Global.getBulan(LDateBirth)),
                Integer.parseInt(Global.getHari(LDateBirth)));
        dateNow = new Date(Integer.parseInt(Global.getTahun(LDateNow)), Integer.parseInt(Global.getBulan(LDateNow)),
                Integer.parseInt(Global.getHari(LDateNow)));

        if (age && !month) {  //dapetin umur aja
            result = Global.getUmur(dateNow, dateBirth, true);
        } else if (!age && month) { //dapetin bulan aja
            result = Global.getUmur(dateNow, dateBirth, false);
        } else { //dapetin semua
            result = Global.getUmur(dateNow, dateBirth, true);
            result = Global.getUmur(dateNow, dateBirth, false);
        }

        return result;
    }


    //method to convert your text to image
    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }


    public static Drawable getCircleDrawableWithText(Context context, String string) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.circle_date);
        Drawable text = CalendarUtils.getDrawableText(context, string, null, android.R.color.white, 12);

        Drawable[] layers = {background, text};
        return new LayerDrawable(layers);
    }

    public static int generateNewPin() {
        int pin = new Random().nextInt(900000) + 100000;
        return pin;
    }

    public static boolean isBeforeToday(long date) {
        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);

        Date prev = new Date(date);
        return prev.before(today);
    }

    public static String getNameLoading(Context context, String model) {
        String nameLoading = "";
        if (model.equals(JConst.gender_model_text)) {
            nameLoading = context.getString(R.string.gender);
        } else if (model.equals(JConst.work_location_model_text)) {
            nameLoading = context.getString(R.string.work_location);
        } else if (model.equals(JConst.note_template_model_text)) {
            nameLoading = context.getString(R.string.note_templates);
        } else if (model.equals(JConst.billing_template_model_text)) {
            nameLoading = context.getString(R.string.billing_templates);
        } else if (model.equals(JConst.patient_model_text)) {
            nameLoading = context.getString(R.string.patient);
        } else if (model.equals(JConst.appointment_model_text)) {
            nameLoading = context.getString(R.string.appointment);
        } else if (model.equals(JConst.medical_record_text)) {
            nameLoading = context.getString(R.string.medical_records);
        } else if (model.equals(JConst.medical_record_diagnosa_model_text)) {
            nameLoading = context.getString(R.string.medical_records);
        } else if (model.equals(JConst.medical_record_model_file_text)) {
            nameLoading = context.getString(R.string.medical_records);
        } else if (model.equals(JConst.billing_model_text)) {
            nameLoading = context.getString(R.string.title_billing);
        } else if (model.equals(JConst.billing_items_model_text)) {
            nameLoading = context.getString(R.string.title_billing);
        } else if (model.equals(JConst.user_model_text)) {
            nameLoading = context.getString(R.string.sub_accounts);
        }

        return nameLoading;
    }

    public static boolean ReadOnlyMode() {
        LoginCompanyModel loginCompanyModel = JApplication.getInstance().loginCompanyModel;
        long activePeriod = Global.getMillisDateFmt(loginCompanyModel.getGrace_period_date(), "yyyy-MM-dd");
        long expiredDate = Global.getMillisDateFmt(loginCompanyModel.getExpired_date(), "yyyy-MM-dd");
        boolean readOnly = false;

        if (serverNowLong() > activePeriod && serverNowLong() < expiredDate) {
            readOnly = true;
        }

        return readOnly;
    }

    public static boolean isExpired() {
        LoginCompanyModel loginCompanyModel = JApplication.getInstance().loginCompanyModel;
        long expiredDate = Global.getMillisDateFmt(loginCompanyModel.getExpired_date(), "yyyy-MM-dd");
        boolean expired = false;

        if (serverNowLong() > expiredDate) {
            expired = true;
        }
        return expired;
    }


    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    public static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());


        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public static void setLanguage(Context context) {
        SharedPreferences sharedPreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "");
        if (!language.equals("")) {
            if (language.equals("in")) {
                Locale locale = new Locale("in");
                Configuration config = context.getResources().getConfiguration();
                config.locale = locale;
                context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            } else {
                Locale locale = new Locale("en");
                Configuration config = context.getResources().getConfiguration();
                config.locale = locale;
                context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            }
        } else {
            Locale locale = new Locale("");
            Configuration config = context.getResources().getConfiguration();
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
    }

    public static void getLanguage(Context context) {
        SharedPreferences sharedPreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "");
        if (!language.equals("")) {
            if (language.equals("in")) {
                Locale locale = new Locale("in");
                Configuration config = context.getResources().getConfiguration();
                config.locale = locale;
                context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            } else {
                Locale locale = new Locale("en");
                Configuration config = context.getResources().getConfiguration();
                config.locale = locale;
                context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            }
        }
    }

    public static String StartOfTheMonth(int customMonth) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.MONTH, customMonth);
        calender.set(Calendar.DAY_OF_MONTH, 1);
        calender.set(Calendar.HOUR_OF_DAY, 0);
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);
        calender.set(Calendar.MILLISECOND, 0);
        return formatter.format(calender.getTime());
    }

    public static String EndOfTheMonth(int customMonth) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.MONTH, customMonth);
        calender.set(Calendar.DAY_OF_MONTH, calender.getActualMaximum(Calendar.DAY_OF_MONTH));
        calender.set(Calendar.HOUR_OF_DAY, calender.getActualMaximum(Calendar.HOUR_OF_DAY));
        calender.set(Calendar.MINUTE, calender.getActualMaximum(Calendar.MINUTE));
        calender.set(Calendar.SECOND, calender.getActualMaximum(Calendar.SECOND));
        calender.set(Calendar.MILLISECOND, calender.getActualMaximum(Calendar.MILLISECOND));
        return formatter.format(calender.getTime());
    }

    public static long StartOfTheMonthLong(int customMonth) {
        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.MONTH, customMonth);
        calender.set(Calendar.DAY_OF_MONTH, 1);
        calender.set(Calendar.HOUR_OF_DAY, 0);
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);
        calender.set(Calendar.MILLISECOND, 0);
        return calender.getTimeInMillis();
    }

    public static long EndOfTheMonthLong(int customMonth) {
        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.MONTH, customMonth);
        calender.set(Calendar.DAY_OF_MONTH, calender.getActualMaximum(Calendar.DAY_OF_MONTH));
        calender.set(Calendar.HOUR_OF_DAY, calender.getActualMaximum(Calendar.HOUR_OF_DAY));
        calender.set(Calendar.MINUTE, calender.getActualMaximum(Calendar.MINUTE));
        calender.set(Calendar.SECOND, calender.getActualMaximum(Calendar.SECOND));
        calender.set(Calendar.MILLISECOND, calender.getActualMaximum(Calendar.MILLISECOND));
        return calender.getTimeInMillis();
    }

    public static boolean contains(List<RecordDiagnosaModel> list, String codeIcd) {
        for (RecordDiagnosaModel model : list) {
            if (model.getIcd_code().equals(codeIcd)) {
                return true;
            }
        }
        return false;
    }

    public static SharedPreferences getEncryptSharedPreference(Context context) {
        String masterKeyAlias = null;
        SharedPreferences sharedPreferences = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            sharedPreferences = EncryptedSharedPreferences.create(
                    "login_company",
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sharedPreferences;
    }

    public static String getPathFromUri(Context context, Uri uri) {
        String result = null;
        String[] proj = null;
//        if (isFromCamera){
//            return Environment.getExternalStorageDirectory() + "/Android/Data/"+context.getPackageName()+ "/files/"+ Environment.DIRECTORY_PICTURES;
//        }else if (isFromGalery) {
//            proj = new String[]{MediaStore.Images.Media.DATA};
//        }else{
        proj = new String[]{MediaStore.Images.Media.DOCUMENT_ID};
//        }
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    public static void setLastDownloadToSharedPref(Context context, long last_download) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("masterDevice", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("last_download", last_download);
        editor.apply();
    }

    public static int getMaxProgress(Context context, long last_backup) {
        RecordTable recordTable = new RecordTable(context);
        RecordFileTable recordFileTable = new RecordFileTable(context);

        int maxProgres = 0;
        int maxPres = recordTable.getCountPrescriptionFile(last_backup);
        int maxFile = recordFileTable.getCountFile(last_backup);
        maxProgres = maxPres + maxFile;

        return maxProgres;
    }

    public static void clearDatabase() {
        String tableName = "";
        List<String> listTable = ListValue.list_table_pasienqu();
        for (int i = 0; i < listTable.size(); i++) {
            tableName = listTable.get(i);
            JApplication.getInstance().db.delete(tableName, null, null);
        }

        //generate default
        JApplication.getInstance().db.execSQL("INSERT INTO pasienqu_work_location (id, name, active, uuid) values (1, 'Default Location', 'true', '" + UUID.randomUUID().toString() + "')");
        JApplication.getInstance().db.execSQL("INSERT INTO backup_drive  (id, last_backup_data, last_backup_media) values (1, 0, 0)");
    }

    public static int getMaxDayOfMonth(int month, int year) {
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(Global.getMillisDate("01/" + month + "/" + year));
        calender.getActualMaximum(Calendar.DAY_OF_MONTH);
        return calender.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static String setTrialDate(int trialDay) {
        String result = "";
        result = getDateFormated(addDay(serverNowWithoutTimeLong(), trialDay), "yyyy-MM-dd");
        return result;
    }

    public static ProgressDialog createProgresSpinner(Activity activity, String message) {
        ProgressDialog progress = new ProgressDialog(activity);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.setMessage(message);

        return progress;
    }

    public static String getlastPatientId() {
        String maxId = JApplication.getInstance().patientTable.getMaxPatientId();
        int autoId = Integer.valueOf(maxId) + 1;
        //cek apakah auto id sudah ada / samaan di database
        int count = Global.getCount(JApplication.getInstance().db, "pasienqu_patient", " patient_id = '" + String.format("%07d", autoId) + "'");
        if (count > 0) {
            autoId += 1;
        }
        return String.format("%07d", autoId);
    }

    public static void infoDialog(Context context, String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(title);
        if (!message.isEmpty()) {
            dialog.setMessage(message);
        }
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void infoDialogLink(Context context, String title, SpannableString message) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(title);
        dialog.setCancelable(false);
        dialog.setMessage(message); // Set pesan sebagai SpannableString

        // Atur MovementMethod agar teks dapat diklik
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                dialog.dismiss();
            }
        });
        dialog.show();

        TextView textView = dialog.findViewById(android.R.id.message);
        if (textView != null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public static void infoDialogRun(Context context, String title, String message, Runnable runnable) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                dialog.dismiss();
                runnable.run();
            }
        });
        dialog.show();
    }

    public static int getViewHeight(View view) {
        WindowManager wm =
                (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int deviceWidth;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            deviceWidth = size.x;
        } else {
            deviceWidth = display.getWidth();
        }

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredHeight(); //        view.getMeasuredWidth();
    }

    public static void showErrorMessage(Context context, String error) {
        if (error.contains("d.a.c.t")) {
            Toast.makeText(context, context.getString(R.string.error_internal) + Global.serverNow(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getString(R.string.error_connection_timeout), Toast.LENGTH_LONG).show();
        }
    }

    public static int convertVersiToInt(String versiName) {
        String[] tipeVersi = versiName.split(Pattern.quote("."));
        String major = tipeVersi[0];
        String minor = tipeVersi[1];
        int versi = (Integer.parseInt(major) * 10000) +
                (Integer.parseInt(minor) * 100);
        return versi;
    }

    public static byte[] getBytes2(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        byte[] byteArrayImage = byteBuffer.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return byteArrayImage;
    }

    public static void dismissProgressDialog(Activity activity, ProgressDialog pDialog) {
        if (!activity.isFinishing() && pDialog != null) {
            pDialog.dismiss();
        }
    }

    public static String getDateFormatSatuSehat(long dateMillis) {
        // Buat objek Date dari milidetik
        Date date = new Date(dateMillis);

        // Buat objek SimpleDateFormat
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Set zona waktu ke UTC
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Format tanggal ke string
        String formattedDate = sdf.format(date)+"T00:00:00+00:00";
        return formattedDate;
    }

    public static double calculateFileSize(String filePath) {
        // Create a File object with the file path
        File file = new File(filePath);

        // Check if the file exists
        if (file.exists()) {
            // Get the size of the file in bytes
            long fileSize = file.length();

            // Convert file size to MB and format it to one decimal place
            double fileSizeMB = fileSize / (1024.0 * 1024.0); // Convert bytes to MB
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setDecimalSeparator('.');
            DecimalFormat df = new DecimalFormat("#.#", decimalFormatSymbols);
            return Double.parseDouble(df.format(fileSizeMB));
        } else {
            System.out.println("File not found!");
            return 0.0;
        }
    }

    public static double calculateFolderSize(String folderPath) {
        File folder = new File(folderPath);
        double totalSize = 0;

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        totalSize += file.length();
                    }
                }
            }

            // Convert total size to MB and format it to one decimal place
            double totalSizeMB = totalSize / (1024.0 * 1024.0); // Convert bytes to MB
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setDecimalSeparator('.');
            DecimalFormat df = new DecimalFormat("#.#", decimalFormatSymbols);
            return Double.parseDouble(df.format(totalSizeMB));
        } else {
            System.out.println("Folder not found or is not a directory!");
            return 0.0;
        }
    }
}
