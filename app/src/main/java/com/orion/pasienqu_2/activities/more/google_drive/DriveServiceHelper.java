package com.orion.pasienqu_2.activities.more.google_drive;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Pair;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.globals.FileUtil;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private static final Executor mExecutor = Executors.newSingleThreadExecutor();
    private static Drive mDriveService;
    private DriveClient mDriveClient;
    public static String fileId;
    private static SharedPreferences sharedPreferences = JApplication.getInstance().getSharedPreferences("masterDevice", Context.MODE_PRIVATE);
    public String fileIdDownload;
    public static String fileIdFolder = sharedPreferences.getString("folder_id", "");

    public DriveServiceHelper(Drive mDriveService) {
        this.mDriveService = mDriveService;
    }

    public Drive getDriveServiceHelper() {
        return mDriveService;
    }

    private final String DATABASE_PATH = JApplication.getInstance().lokasi_db;

    public static Task<String> createFileMedia(String filePath, String fileName, String mime) {
        return Tasks.call(mExecutor, () -> {

            //Skip file jika sudah pernah di DriveUtil
            FileList result = mDriveService.files().list().execute();
            for (File file : result.getFiles()) {
                Log.d(" file: \n", file.getName() + file.getId());
                if (file.getName().equals(fileName)) {
                    return null;
                }
            }

            File fileMetaData = new File();
            fileMetaData.setName(fileName);
            fileMetaData.setParents(Collections.singletonList(fileIdFolder));
            java.io.File file = new java.io.File(filePath);
            MimeTypeMap map = MimeTypeMap.getSingleton();
            String mimeType = map.getMimeTypeFromExtension(mime);

            FileContent mediaContent = new FileContent(mimeType, file);

            File myFile = null;
            DateTime currentTime = new DateTime(Global.serverNowLong());
            try {
                myFile = mDriveService.files().create(fileMetaData, mediaContent).execute()
                        .setCreatedTime(currentTime)
                        .setModifiedTime(currentTime);

            } catch (Exception e) {
                e.printStackTrace();
            }
            fileId = myFile.getId();
            return myFile.getId();
        });
    }

    public static Task<String> createFileDB(String filePath, String fileName, String mime) {
        return Tasks.call(mExecutor, () -> {
            String fileIDTempDb = "";

            //cek apakah database sudah ada jika iya nanti di delete file
            FileList result = mDriveService.files().list().execute();
            for (File file : result.getFiles()) {
                Log.d(" file: \n", file.getName() + file.getId());
                if (file.getName().equals(fileName)) {
//                    mDriveService.files().delete(file.getId()).execute();  //delete file
                    fileIDTempDb = file.getId(); //store id db temp
                }
            }

            File fileMetaData = new File();
            fileMetaData.setName(fileName);
            fileMetaData.setParents(Collections.singletonList(fileIdFolder));
            java.io.File file = new java.io.File(filePath);
            MimeTypeMap map = MimeTypeMap.getSingleton();
            String mimeType = map.getMimeTypeFromExtension(mime);

            FileContent mediaContent = new FileContent(mimeType, file);

            File myFile = null;
            DateTime currentTime = new DateTime(Global.serverNowLong());
            try {
                myFile = mDriveService.files().create(fileMetaData, mediaContent).execute()
                    .setCreatedTime(currentTime)
                    .setModifiedTime(currentTime);
                if (!fileIDTempDb.equals("")){
                    mDriveService.files().delete(fileIDTempDb).execute();  //delete file backup db sebelumnya
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            fileId = myFile.getId();
            return myFile.getId();
        });
    }

    public static Task<String> createFileDBTemp(String filePath, String fileName, String mime) {
        return Tasks.call(mExecutor, () -> {

            File fileMetaData = new File();
            fileMetaData.setName(fileName);
            fileMetaData.setParents(Collections.singletonList(fileIdFolder));
            java.io.File file = new java.io.File(filePath);
            MimeTypeMap map = MimeTypeMap.getSingleton();
            String mimeType = map.getMimeTypeFromExtension(mime);

            FileContent mediaContent = new FileContent(mimeType, file);

            File myFile = null;
            DateTime currentTime = new DateTime(Global.serverNowLong());
            try {
                myFile = mDriveService.files().create(fileMetaData, mediaContent).execute()
                        .setCreatedTime(currentTime)
                        .setModifiedTime(currentTime);

            } catch (Exception e) {
                e.printStackTrace();
            }
            fileId = myFile.getId();
            return myFile.getId();
        });
    }

    public static Task<String> createFolder(String folderName, Context context) {
        return Tasks.call(mExecutor, () -> {
            File fileMetadata = new File();
            fileMetadata.setName(folderName);
            fileMetadata.setMimeType("application/vnd.google-apps.folder");

            FileList result = mDriveService.files().list().execute();
            //cek jika baru pertama kali menggunakan fitur maka newfolder + generete filefolder id
            if (result.getFiles().isEmpty()) {
                File folder = mDriveService.files().create(fileMetadata)
                        .setFields("id")
                        .execute();
                fileIdFolder = folder.getId();
            //jika sudah pernah buat folder maka dapetin id nya
            }else {
                for (File file : result.getFiles()) {
                    if (file.getName().equals(folderName)) {
                        fileIdFolder = file.getId();
                    }
                }
            }
            //update ke share preference
            SharedPreferences.Editor editor = context.getSharedPreferences("masterDevice", Context.MODE_PRIVATE).edit();
            editor.putString("folder_id", fileIdFolder);
            editor.apply();
            return fileIdFolder;
        });
    }

    public String getFileId() {
        return fileId;
    }

    public Task<Void> listFiles(String fileName, boolean isDabaseFile) {
        return Tasks.call(mExecutor, () -> {
            Log.d("mulai", "gagal");
            try {
                String file_db = "";
                FileList result = mDriveService.files().list().execute();
                for (File file : result.getFiles()) {
                    Log.d("Found file: \n", file.getName() + file.getId());
                    if (file.getName().equals(fileName)) {
                        file_db = file.getId();
                        downloadFile(isDabaseFile, file.getId(), file.getName());
                    }
                }
//                if (file_db.equals("")){
//                    Toast.makeText(JApplication.getInstance().getApplicationContext(), "No such file or directory", Toast.LENGTH_SHORT).show();
//                    return null;
//                }

            } catch (IOException e) {
                Log.d("Gagal 1", "gagal");
            } catch (Exception e) {
                Log.d("Gagal 1", "gagal");
            }
            return null;
        });
    }

    public Task<Void> downloadFile(boolean isDatabaseFile, String fileId, String fileName) {
        return Tasks.call(mExecutor, () -> {
            OutputStream outputStream = new ByteArrayOutputStream();
            mDriveService.files().get(fileId)
                    .executeMediaAndDownloadTo(outputStream);

            String dir = "";
            if (isDatabaseFile) {
                dir = DATABASE_PATH;
            } else {
                java.io.File folder = new java.io.File(JConst.mediaLocationPath);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                dir = JConst.mediaLocationPath + fileName;
            }
            java.io.FileOutputStream fos = new FileOutputStream(new java.io.File(dir));
            ByteArrayOutputStream baos = ((ByteArrayOutputStream) outputStream);
            baos.writeTo(fos);
            baos.writeTo(fos);
            return null;
        });
    }

    public Task<Void> downloadDB(String fileName) {
        return Tasks.call(mExecutor, () -> {
            String fileDbId = "";
            try {
                FileList result =
                        mDriveService.files().list().execute();
                for (File file : result.getFiles()) {
                    Log.d("Found file: \n", file.getName() + file.getId());
                    if (file.getName().equals(fileName)) {
                        fileDbId = file.getId();
                    }

                }

            } catch (IOException e) {
                Log.d("Gagal 1", "gagal");
            }

            OutputStream outputStream = new ByteArrayOutputStream();
//            try {
                mDriveService.files().get(fileDbId)
                        .executeMediaAndDownloadTo(outputStream);
//            }catch (IOException e) {
//                e.printStackTrace();
//            }

            String dir = DATABASE_PATH;
            java.io.FileOutputStream fos = new FileOutputStream(new java.io.File(dir));
            ByteArrayOutputStream baos = ((ByteArrayOutputStream) outputStream);
//            baos.writeTo(fos);
            baos.writeTo(fos);
            return null;
        });
    }

    public Task<Void> checkLastModified(String fileName) {
        return Tasks.call(mExecutor, () -> {
            JApplication.lastModifiedBackup = 0;
            try {
                FileList result = mDriveService.files().list()
                        .setFields("files(id, name, modifiedTime, mimeType, size)")
                        .execute();
                for (File file : result.getFiles()) {
                    Log.d("Found file: \n", file.getName() + file.getId());
                    if (file.getName().equals(fileName)) {
                        JApplication.lastModifiedBackup = file.getModifiedTime().getValue();
                    }
                }

            } catch (Exception e) {
                Log.d("Gagal 1", "gagal");
            }
            return null;
        });
    }

}
