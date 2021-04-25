package me.shaohui.advancedluban;

import android.graphics.Bitmap;
import java.io.File;



class LubanBuilder {

    int maxSize;

    int maxWidth;

    int maxHeight;

    File cacheDir;
    Renameable renameable = new Renameable();

    Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;

    int gear = Luban.THIRD_GEAR;

    LubanBuilder(File cacheDir) {
        this.cacheDir = cacheDir;
    }

}
