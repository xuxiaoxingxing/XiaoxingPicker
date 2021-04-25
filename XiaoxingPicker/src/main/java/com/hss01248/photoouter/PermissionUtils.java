package com.hss01248.photoouter;

import android.Manifest;
import android.os.Build;

import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.List;


public class PermissionUtils {



    private static void askPermission(final PermissionListener listener,String... permission){
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            Acp.getInstance(PhotoUtil.context).request(new AcpOptions.Builder()
                            .setPermissions(permission)
//                .setDeniedMessage()
//                .setDeniedCloseBtn()
//                .setDeniedSettingBtn()
//                .setRationalMessage()
//                .setRationalBtn()
                            .build(),
                    new AcpListener() {
                        @Override
                        public void onGranted() {
                            listener.onGranted();
                        }

                        @Override
                        public void onDenied(List<String> permissions) {
                            listener.onDenied(permissions);

                        }
                    });
        } else {
            // Pre-Marshmallow
            listener.onGranted();
        }
    }


    public static void askCalendar(PermissionListener listener){
        askPermission(listener, Manifest.permission.READ_CALENDAR);
    }


    public static void askCamera(PermissionListener listener){
        askPermission(listener, Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }



    public static void askExternalStorage(PermissionListener listener){
        askPermission(listener, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    public static void askPhone(PermissionListener listener){
        askPermission(listener, Manifest.permission.READ_PHONE_STATE);
    }
    public static void askCallPhone(PermissionListener listener){
        askPermission(listener, Manifest.permission.CALL_PHONE);
    }


    public static void askSms(PermissionListener listener){
        askPermission(listener, Manifest.permission.SEND_SMS);
    }





    public static void askLocationInfo(PermissionListener listener){
        askPermission(listener, Manifest.permission.ACCESS_COARSE_LOCATION);
    }



    public static void askRecord(PermissionListener listener){
        askPermission(listener, Manifest.permission.RECORD_AUDIO);
    }



    public static void askSensors(PermissionListener listener){
        askPermission(listener, Manifest.permission.BODY_SENSORS);
    }


    public static void askContacts(PermissionListener listener){
        askPermission(listener, Manifest.permission.READ_CONTACTS);
    }

    public interface  PermissionListener{
        void onGranted();
        void onDenied(List<String> permissions);

    }


}
