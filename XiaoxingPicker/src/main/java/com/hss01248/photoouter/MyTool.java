package com.hss01248.photoouter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import me.iwf.photopicker.PhotoPickUtils;
import me.iwf.photopicker.PhotoPicker;
import me.shaohui.advancedluban.Luban;

import static com.hss01248.photoouter.PhotoUtil.context;
import static me.shaohui.advancedluban.Luban.compress;


public class MyTool {


    public static void startCamera(Activity activity, BasePhotoBuilder builder) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Uri mDestinationUri = buildUri(builder, true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mDestinationUri);
        activity.startActivityForResult(intent, PhotoUtil.REQUEST_CAMERA);
    }

    private static Uri buildUri(BasePhotoBuilder builder, boolean isCameraOrCrop) {
        File cacheFolder = null;
        if (isCameraOrCrop) {
            cacheFolder = new File(builder.rootDir, "camera");
        } else {
            cacheFolder = new File(builder.compressedDir);

        }

        // File cacheFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "crop");
        if (!cacheFolder.exists()) {
            try {
                boolean result = cacheFolder.mkdir();
                if (PhotoUtil.isDebug)
                    Log.d("uri", "generateUri " + cacheFolder + " result: " + (result ? "succeeded" : "failed"));
            } catch (Exception e) {
                if (PhotoUtil.isDebug)
                    Log.e("uri", "generateUri failed: " + cacheFolder, e);
            }
        }
        String name = String.format("image-%d.jpg", System.currentTimeMillis());
        String filePath = new File(cacheFolder, name).getAbsolutePath();
        if (isCameraOrCrop) {
            builder.filePathToCamera = filePath;
        } else {
            builder.filePathToCrop = filePath;
        }
        Log.e("dd", "new filePath:" + filePath);

        Uri uri;

        if (isCameraOrCrop && Build.VERSION.SDK_INT >= 24) {

            uri = FileProvider.getUriForFile(PhotoUtil.context, "qilin.yougu.com.qilin.fileprovider", new File(cacheFolder, name));
        } else {
            uri = Uri
                    .fromFile(cacheFolder)
                    .buildUpon()
                    .appendPath(name)
                    .build();
        }

        Log.e("uri", uri.toString());

        return uri;
    }

    public static PhotoPicker.PhotoPickerBuilder startPicker() {
        return PhotoPickUtils.startPick();
    }

    public static void startCropActivity(Activity context, Uri sourceUri, BasePhotoBuilder builder) {
        CropConfig config = buildCropConfig(builder);
        Uri mDestinationUri = buildUri(builder, false);
        UCrop uCrop = UCrop.of(sourceUri, mDestinationUri);

        uCrop.withAspectRatio(config.aspectRatioX, config.aspectRatioY);
        uCrop.withMaxResultSize(config.maxWidth, config.maxHeight);

        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.NONE, UCropActivity.NONE);
        options.setCompressionQuality(config.quality);
        // options.setOvalDimmedLayer(config.isOval);
        options.setCircleDimmedLayer(config.isOval);
        options.setShowCropGrid(config.showGridLine);
        options.setHideBottomControls(config.hideBottomControls);
        options.setShowCropFrame(config.showOutLine);
        options.setToolbarColor(config.toolbarColor);
        options.setStatusBarColor(config.statusBarColor);

        uCrop.withOptions(options);

        uCrop.start(context);
    }

    public static CropConfig buildCropConfig(BasePhotoBuilder builder) {
        CropConfig config = new CropConfig();
        config.aspectRatioX = builder.cropX;
        config.aspectRatioY = builder.cropY;
        config.isOval = builder.isOval;
        config.showGridLine = false;
        config.showOutLine = builder.showGridline;
        return config;


    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static void compressPics(final BasePhotoBuilder builder) {
        conpressByBuban(builder);

        // compressByCompressor(builder);


    }

    private static void conpressByBuban(final BasePhotoBuilder builder) {

        final int size = builder.selectedPaths.size();
        final int[] count = {0, 0};


        List<File> files = new ArrayList<>();
        for (String path : builder.selectedPaths) {
            files.add(new File(path));
        }

        Luban luban = Luban.compress(context, files);
        if (builder.maxWidth == 0 || builder.maxHeight == 0) {
            luban.putGear(Luban.THIRD_GEAR);
        } else {
            luban.putGear(Luban.CUSTOM_GEAR)
                    .setMaxHeight(builder.maxHeight)
                    .setMaxWidth(builder.maxWidth);
        }

        File file = new File(builder.compressedDir);
        if (file.isDirectory() || !file.mkdirs()) {
            luban.setCompressCacheDir(file);
        }


        if (builder.renameable != null) {
            luban.setCompressFileRenameMethod(builder.renameable);
        }

        luban.asListObservable()// generate Observable
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        final List<String> compressedFiles = new ArrayList<>();
                        for (File file : files) {
                            compressedFiles.add(file.getAbsolutePath());
                        }
                        builder.callback.onSuccessMulti(builder.selectedPaths, compressedFiles, builder.requestCode);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        builder.callback.onFail(throwable.getMessage(), throwable, builder.requestCode);
                    }
                });


    }

    private static void compressOneByLuban(final BasePhotoBuilder builder, final int size, final int[] count,
                                           final List<String> compressedFiles, final File file) {
        compress(context, file)
                .putGear(Luban.CUSTOM_GEAR)
                .setMaxHeight(builder.maxHeight)
                .setMaxWidth(builder.maxWidth)
                .asObservable()// generate Observable
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        compressedFiles.add(file.getAbsolutePath());
                        if (PhotoUtil.isDebug)
                            Log.e("compressing", file.getAbsolutePath() + "--success!");
                        count[0]++;
                        if (count[0] == size) {
                            if (size == 1) {
                                builder.callback.onSuccessSingle(builder.selectedPaths.get(0), compressedFiles.get(0), builder.requestCode);
                            } else {
                                builder.callback.onSuccessMulti(builder.selectedPaths, compressedFiles, builder.requestCode);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        if (PhotoUtil.isDebug)
                            Log.e("compressing", file.getAbsolutePath() + "--fail!--" + throwable.getMessage());

                        count[1]++;
                        if (count[1] < size * 2) {
                            compressOneByLuban(builder, size, count, compressedFiles, file);
                        } else {
                            builder.callback.onFail("圖片壓縮失敗", new Throwable("圖片壓縮失敗"), builder.requestCode);
                        }
                    }
                });
    }

    public static String getListStr(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : list) {
            stringBuilder.append(str + "\n");
        }
        return stringBuilder.toString();

    }

    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;
    }

    public static class CropConfig {
        public int aspectRatioX = 1;
        public int aspectRatioY = 1;
        public int maxWidth = 1080;
        public int maxHeight = 1920;

        //options
        public int tag;
        public boolean isOval = false;
        public int quality = 80;

        public boolean hideBottomControls = true;
        public boolean showGridLine = true;
        public boolean showOutLine = true;

        public @ColorInt
        int toolbarColor = PhotoUtil.titleBarColor;


        public @ColorInt
        int statusBarColor = PhotoUtil.statusBarColor;


        public void setAspectRation(int x, int y) {
            this.aspectRatioX = x;
            this.aspectRatioY = y;
        }

        public void setMaxSize(int width, int height) {
            this.maxHeight = height;
            this.maxWidth = width;
        }

    }
}
