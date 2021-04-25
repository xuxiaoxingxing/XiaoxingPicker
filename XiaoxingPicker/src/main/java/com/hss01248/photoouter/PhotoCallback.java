package com.hss01248.photoouter;

import java.util.List;



public interface  PhotoCallback {
      void onFail(String msg,Throwable r,int requestCode);
      void onSuccessSingle(String originalPath,String compressedPath,int requestCode);
      void onSuccessMulti(List<String> originalPaths,List<String> compressedPaths,int requestCode);
      void onCancel(int requestCode);
}
