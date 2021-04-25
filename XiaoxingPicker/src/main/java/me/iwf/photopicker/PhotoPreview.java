package me.iwf.photopicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import java.util.ArrayList;


public class PhotoPreview {

  public final static int REQUEST_CODE = 666;

  public final static String EXTRA_CURRENT_ITEM = "current_item";
  public final static String EXTRA_PHOTOS       = "photos";
  public final static String EXTRA_SHOW_DELETE  = "show_delete";

  public final static String EXTRA_ACTION       = "action";


  public static PhotoPreviewBuilder builder() {
    return new PhotoPreviewBuilder();
  }


  public static class PhotoPreviewBuilder {
    private Bundle mPreviewOptionsBundle;
    private Intent mPreviewIntent;

    public PhotoPreviewBuilder() {
      mPreviewOptionsBundle = new Bundle();
      mPreviewIntent = new Intent();
    }


    public void start(@NonNull Activity activity, int requestCode) {
      activity.startActivityForResult(getIntent(activity), requestCode);
    }


    public void start(@NonNull Context context, @NonNull android.support.v4.app.Fragment fragment, int requestCode) {
      fragment.startActivityForResult(getIntent(context), requestCode);
    }


    public void start(@NonNull Context context, @NonNull android.support.v4.app.Fragment fragment) {
      fragment.startActivityForResult(getIntent(context), REQUEST_CODE);
    }


    public void start(@NonNull Activity activity) {
      start(activity, REQUEST_CODE);
    }


    public Intent getIntent(@NonNull Context context) {
      mPreviewIntent.setClass(context, PhotoPagerActivity.class);
      mPreviewIntent.putExtras(mPreviewOptionsBundle);
      return mPreviewIntent;
    }

    public PhotoPreviewBuilder setPhotos(ArrayList<String> photoPaths) {
      mPreviewOptionsBundle.putStringArrayList(EXTRA_PHOTOS, photoPaths);
      return this;
    }

    public PhotoPreviewBuilder setAction(int action) {
      mPreviewOptionsBundle.putInt(EXTRA_ACTION, action);
      return this;
    }

    public PhotoPreviewBuilder setCurrentItem(int currentItem) {
      mPreviewOptionsBundle.putInt(EXTRA_CURRENT_ITEM, currentItem);
      return this;
    }

    public PhotoPreviewBuilder setShowDeleteButton(boolean showDeleteButton) {
      mPreviewOptionsBundle.putBoolean(EXTRA_SHOW_DELETE, showDeleteButton);
      return this;
    }
  }
}
