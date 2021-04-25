package me.shaohui.advancedluban;

import java.io.File;
import java.util.List;



public interface OnMultiCompressListener {


    void onStart();


    void onSuccess(List<File> fileList);


    void onError(Throwable e);


}
