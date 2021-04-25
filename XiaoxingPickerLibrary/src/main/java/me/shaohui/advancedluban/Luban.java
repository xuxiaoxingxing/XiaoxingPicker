
package me.shaohui.advancedluban;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.IntDef;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class Luban {

    public static final int FIRST_GEAR = 1;

    public static final int THIRD_GEAR = 3;

    public static final int CUSTOM_GEAR = 4;

    private static final String TAG = "Luban";

    private static String DEFAULT_DISK_CACHE_DIR = "luban_disk_cache";

    private File mFile;

    private List<File> mFileList;

    private LubanBuilder mBuilder;

    private Luban(File cacheDir) {
        mBuilder = new LubanBuilder(cacheDir);
    }

    public static Luban compress(Context context, File file) {
        Luban luban = new Luban(Luban.getPhotoCacheDir(context));
        luban.mFile = file;
        luban.mFileList = Collections.singletonList(file);
        return luban;
    }

    public static Luban compress(Context context, List<File> files) {
        Luban luban = new Luban(Luban.getPhotoCacheDir(context));
        luban.mFileList = files;
        luban.mFile = files.get(0);
        return luban;
    }


    public Luban putGear(@GEAR int gear) {
        mBuilder.gear = gear;
        return this;
    }
    public Luban setCompressCacheDir(File cacheDir) {
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        File file = new File(cacheDir,".nomedia");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mBuilder.cacheDir = cacheDir;
        return this;
    }
    public Luban setCompressFileRenameMethod(Renameable renameable) {
        mBuilder.renameable = renameable;
        return this;
    }


    public Luban setCompressFormat(Bitmap.CompressFormat compressFormat) {
        mBuilder.compressFormat = compressFormat;
        return this;
    }


    public Luban setMaxSize(int size) {
        mBuilder.maxSize = size;
        return this;
    }


    public Luban setMaxWidth(int width) {
        mBuilder.maxWidth = width;
        return this;
    }


    public Luban setMaxHeight(int height) {
        mBuilder.maxHeight = height;
        return this;
    }


    public void launch(final OnCompressListener listener) {
        asObservable().observeOn(AndroidSchedulers.mainThread()).doOnSubscribe(
                new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        listener.onStart();
                    }
                })
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        listener.onSuccess(file);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        listener.onError(throwable);
                    }
                });
    }


    public void launch(final OnMultiCompressListener listener) {
        asListObservable().observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        listener.onStart();
                    }
                })
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        listener.onSuccess(files);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        listener.onError(throwable);
                    }
                });
    }


    public Observable<File> asObservable() {
        LubanCompresser compresser = new LubanCompresser(mBuilder);
        return compresser.singleAction(mFile);
    }


    public Observable<List<File>> asListObservable() {
        LubanCompresser compresser = new LubanCompresser(mBuilder);
        return compresser.multiAction(mFileList);
    }

    // Utils


    private static File getPhotoCacheDir(Context context) {
        return getPhotoCacheDir(context, Luban.DEFAULT_DISK_CACHE_DIR);
    }


    private static File getPhotoCacheDir(Context context, String cacheName) {
        File cacheDir = context.getCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
                // File wasn't able to create a directory, or the result exists but not a directory
                return null;
            }
            return result;
        }
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, "default disk cache dir is null");
        }
        return null;
    }


    public Luban clearCache() {
        if (mBuilder.cacheDir.exists()) {
            deleteFile(mBuilder.cacheDir);
        }
        return this;
    }


    private void deleteFile(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File file : fileOrDirectory.listFiles()) {
                deleteFile(file);
            }
        }
        fileOrDirectory.delete();
    }

    @IntDef({FIRST_GEAR, THIRD_GEAR, CUSTOM_GEAR})
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @Documented
    @Inherited
    @interface GEAR {

    }
}