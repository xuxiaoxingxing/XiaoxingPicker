package com.hss01248.photoouter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;


import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPickUtils;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import me.iwf.photopicker.R;
import me.iwf.photopicker.utils.Initer;



public class PhotoUtil {

    public static final int REQUEST_CAMERA = 0x03;
    public static final int REQUEST_CROP = UCrop.REQUEST_CROP;
    public static final int REQUEST_PICK = PhotoPicker.REQUEST_CODE;
    public static final int REQUEST_PREVIEW = PhotoPreview.REQUEST_CODE;

    private static BasePhotoBuilder builder;
    public static Context context;
    public static @ColorInt int titleBarColor;
    public static @ColorInt int statusBarColor;
    public static boolean isDebug ;

    public static void init(Context context, Initer initer){
        init(context,initer, R.color.colorPrimaryDark,R.color.colorPrimary);
        isDebug = MyTool.isApkDebugable(context);
    }



    private static void init(Context context1, Initer initer,@ColorRes int statusBarColorRes, @ColorRes int titleBarColorRes){
        context = context1;
        statusBarColor = context1.getResources().getColor(statusBarColorRes);
        titleBarColor = context1.getResources().getColor(titleBarColorRes);
        PhotoPickUtils.init(context1,initer);

    }






    public static BasePhotoBuilder  cropAvatar(boolean fromCamera){
         builder = new BasePhotoBuilder();
        builder.isOval = true;
        builder.showGridline = false;
        builder.setFromCamera(fromCamera)
                .setMaxSelectCount(1)
                .setCompressMax(512,512)
                .setNeedCropWhenOne(true);
        return builder;

    }

    public static  BasePhotoBuilder multiSelect(){
        return multiSelect(9);
    }

    public static  BasePhotoBuilder multiSelect(int maxCount){
        builder = new BasePhotoBuilder();
        builder.setMaxSelectCount(maxCount);
        return builder;
    }
    public static  BasePhotoBuilder begin(){
        builder = new BasePhotoBuilder();
        return builder;
    }










    public static void onActivityResult(Activity context,int requestCode, int resultCode, Intent data ){
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK) {

            } else if (requestCode == REQUEST_CROP) {

                Uri finalUri = UCrop.getOutput(data);
                if(finalUri == null && !new File(builder.filePathToCrop).exists()){
                    builder.callback.onFail("圖片裁剪失敗:"+builder.filePathToCrop,new Throwable("圖片裁剪失敗1"),builder.requestCode);
                }else {
                    //builder.callback.onSuccessSingle(builder.selectedPaths.get(0),MyTool.getRealFilePath(context,finalUri));//builder.filePathToCrop
                    builder.callback.onSuccessSingle("啦啦啦成功啦",builder.filePathToCrop,builder.requestCode);
                }

            } else if (requestCode == REQUEST_CAMERA) {
                //startCropActivity(context, uri);
                String path = builder.filePathToCamera;
                if(!new File(path).exists()){
                    builder.callback.onFail("拍照失敗:"+builder.filePathToCamera,new Throwable("拍照失敗:"+builder.filePathToCamera),builder.requestCode);
                    return;
                }


                if((!builder.needCropWhenOne) && (!builder.needCompress)){
                    builder.callback.onSuccessSingle(path,path,builder.requestCode);
                }else if(builder.needCropWhenOne){

                    MyTool.startCropActivity(context,Uri.fromFile(new File(path)),builder);

                }else if(builder.needCompress) {

                    MyTool.compressPics(builder);
                }
            }else {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                if (photos == null || photos.size()==0){
                    builder.callback.onFail("未選擇圖片",new Throwable("未選擇圖片"),requestCode);
                    return;
                }

                builder.selectedPaths = photos;



                if(builder.maxSelectCount ==1 && photos.size() ==1){
                    String path = photos.get(0);
                    if(builder.needCropWhenOne){

                        MyTool.startCropActivity(context,Uri.fromFile(new File(path)),builder);

                    }else if(builder.needCompress) {

                        MyTool.compressPics(builder);
                    }else {
                        builder.callback.onSuccessSingle(path,path,requestCode);
                    }
                }else {
                    if(!builder.needCompress){
                        builder.callback.onSuccessMulti(photos,photos,requestCode);
                        return;
                    }


                    MyTool.compressPics(builder);
                }
            }
        }else if (resultCode == UCrop.RESULT_ERROR) {
            builder.callback.onFail("圖片裁剪失敗",new Throwable("圖片裁剪失敗"),requestCode);
        }else if (resultCode == Activity.RESULT_CANCELED) {
            builder.callback.onCancel(requestCode);
        }
    }









}
