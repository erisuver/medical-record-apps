package com.orion.pasienqu_2.activities.calendar;

import static com.orion.pasienqu_2.globals.Global.MinusMinute;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.home.home;
import com.orion.pasienqu_2.data_table.ReminderTable;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.models.ReminderModel;

import java.util.ArrayList;
import java.util.Random;

public class NotificationUtil {
    private static Context context;

    public NotificationUtil(Context context) {
        this.context = context;
    }

    public static void scheduleNotification(Notification notification, long appoin, int valueReminder, String mCustomValueReminder) {
        ReminderTable reminderTable = new ReminderTable(context);

        //init
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                .setData(Uri.parse("package:"+context.getPackageName()));

//        int notifId = reminderTable.getMaxId();
        Random random = new Random();
        int notifId = random.nextInt(9999 - 1000) + 1000;
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, notifId);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification);

        //fixed errror in android 12
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(context,
                    notifId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        }else {
            pendingIntent = PendingIntent.getBroadcast(context,
                    notifId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        //set alarm
        long appointment= 0;
        if (!mCustomValueReminder.equals("")){
            appointment = Global.getMillisDateTime(mCustomValueReminder);
        }else {
            appointment = MinusMinute(appoin, valueReminder);
        }
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, appointment, pendingIntent);

    }

    public static Notification getNotification(String patientName, String location, String date) {

        //init
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_001");

        Random random = new Random();
        int notifId = random.nextInt(9999 - 1000) + 1000;
        String notif = String.valueOf(notifId);

        String contentText = context.getString(R.string.appointment_with) + patientName;
        Intent intent = new Intent(context, home.class); //buka activity saat klik di notif
        intent.putExtra("patient_name",patientName);
        intent.putExtra("work_location",location);
        intent.putExtra("appointment_date",date);
        intent.putExtra("notif_id", notif);

        //fixed errror in android 12
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(context,
                    notifId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        }else {
            pendingIntent = PendingIntent.getActivity(context,
                    notifId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        builder.setContentTitle(context.getString(R.string.pasienqu_reminder));
        builder.setContentText(contentText);
        builder.setSmallIcon(R.drawable.pasienqu_notification_icon);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setContentIntent(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "notify_001";
            NotificationChannel channel = new NotificationChannel(channelId, "Notifikasi Appointment", NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        return builder.build();
    }

    public static void runNotification(){
        //init
        ReminderTable reminderTable = new ReminderTable(JApplication.getInstance());
        ArrayList<ReminderModel> listReminder = reminderTable.getRecords(Global.serverNowLong());
        Notification notification;

        for (int i=0; i<listReminder.size(); i++){
            notification = getNotification(listReminder.get(i).getPatient_name(), listReminder.get(i).getLocation(), listReminder.get(i).getAppointment());
            scheduleNotification(notification, listReminder.get(i).getDate_reminder(), listReminder.get(i).getValue_reminder(), listReminder.get(i).getCustom_reminder());
        }


    }



    public static void saveToReminderTable(long appoinDate, int valueReminder,  String mCustomValueReminder, String patientName, String location, String date){
        ReminderTable reminderTable = new ReminderTable(context);
        ReminderModel reminderModel = new ReminderModel();
        reminderModel.setValue_reminder(valueReminder);
        reminderModel.setDate_reminder(appoinDate);
        reminderModel.setCustom_reminder(mCustomValueReminder);
        reminderModel.setPatient_name(patientName);
        reminderModel.setLocation(location);
        reminderModel.setAppointment(date);

        reminderTable.insert(reminderModel);
    }
}
