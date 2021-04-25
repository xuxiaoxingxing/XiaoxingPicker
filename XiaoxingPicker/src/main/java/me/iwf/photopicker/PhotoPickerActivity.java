package me.iwf.photopicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.hss01248.image.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.event.OnItemCheckListener;
import me.iwf.photopicker.fragment.ImagePagerFragment;
import me.iwf.photopicker.fragment.PhotoPickerFragment;
import me.iwf.photopicker.widget.Titlebar;

import static android.widget.Toast.LENGTH_LONG;
import static me.iwf.photopicker.PhotoPicker.DEFAULT_COLUMN_NUMBER;
import static me.iwf.photopicker.PhotoPicker.DEFAULT_MAX_COUNT;
import static me.iwf.photopicker.PhotoPicker.EXTRA_GRID_COLUMN;
import static me.iwf.photopicker.PhotoPicker.EXTRA_MAX_COUNT;
import static me.iwf.photopicker.PhotoPicker.EXTRA_ORIGINAL_PHOTOS;
import static me.iwf.photopicker.PhotoPicker.EXTRA_PREVIEW_ENABLED;
import static me.iwf.photopicker.PhotoPicker.EXTRA_SHOW_CAMERA;
import static me.iwf.photopicker.PhotoPicker.EXTRA_SHOW_GIF;
import static me.iwf.photopicker.PhotoPicker.KEY_SELECTED_PHOTOS;

public class PhotoPickerActivity extends AppCompatActivity {

  private PhotoPickerFragment pickerFragment;
  private ImagePagerFragment imagePagerFragment;
  //private MenuItem menuDoneItem;

  private int maxCount = DEFAULT_MAX_COUNT;


 // private boolean menuIsInflated = false;

  private boolean showGif = false;
  private int columnNumber = DEFAULT_COLUMN_NUMBER;
  private ArrayList<String> originalPhotos = null;

  private Titlebar titlebar;
  boolean showCamera;
  boolean previewEnabled;
  Intent intent;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    intent = getIntent();
    ImageLoader.getActualLoader().clearMomoryCache();

     showCamera      = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, false);
     showGif         = getIntent().getBooleanExtra(EXTRA_SHOW_GIF, false);
     previewEnabled  = getIntent().getBooleanExtra(EXTRA_PREVIEW_ENABLED, true);

    setShowGif(showGif);

    setContentView(R.layout.__picker_activity_photo_picker);

    titlebar = (Titlebar) findViewById(R.id.titlebar);
    titlebar.init(this);

    fragmentManager = getSupportFragmentManager();


    maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
    columnNumber = getIntent().getIntExtra(EXTRA_GRID_COLUMN, DEFAULT_COLUMN_NUMBER);
    originalPhotos = getIntent().getStringArrayListExtra(EXTRA_ORIGINAL_PHOTOS);

    showPikcerFragment();


    titlebar.getTvRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ArrayList<String> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotoPaths();
        if (photos != null && photos.size() > 0){

          intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, photos);
          setResult(RESULT_OK, intent);
          finish();
        }else {
          Toast.makeText(getApplicationContext(),"還沒有選擇圖片",Toast.LENGTH_SHORT).show();
        }
      }
    });

    setListener();

  }

  private void setListener() {
    pickerFragment.getPhotoGridAdapter().setOnItemCheckListener(new OnItemCheckListener() {
      @Override public boolean OnItemCheck(int position, Photo photo, final boolean isCheck, int selectedItemCount) {

        int total = selectedItemCount + (isCheck ? -1 : 1);

       // menuDoneItem.setEnabled(total > 0);


        if (maxCount <= 1) {
          List<Photo> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
          if (!photos.contains(photo)) {
            photos.clear();
            pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
          }
          return true;
        }

        if (total > maxCount) {
          Toast.makeText(getActivity(), getString(R.string.__picker_over_max_count_tips, maxCount),
              LENGTH_LONG).show();
          return false;
        }
        titlebar.getTvRight().setText(getString(R.string.__picker_done_with_count, total, maxCount));
        return true;
      }
    });
  }



  @Override public void onBackPressed() {
    if (imagePagerFragment != null && imagePagerFragment.isVisible()) {
      imagePagerFragment.runExitAnimation(new Runnable() {
        public void run() {
          if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
          }
        }
      });
      showPikcerFragment();
    } else {
      super.onBackPressed();
    }
  }


  public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
    this.imagePagerFragment = imagePagerFragment;
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.container, this.imagePagerFragment)
        .addToBackStack(null)
        .commit();
  }



  public PhotoPickerActivity getActivity() {
    return this;
  }

  public boolean isShowGif() {
    return showGif;
  }

  public void setShowGif(boolean showGif) {
    this.showGif = showGif;
  }


  FragmentManager fragmentManager;
  FragmentTransaction fragmentTransaction;

  private void showPikcerFragment() {
    fragmentTransaction = fragmentManager.beginTransaction();


        if(pickerFragment==null){
          pickerFragment = PhotoPickerFragment
                  .newInstance(showCamera, showGif, previewEnabled, columnNumber, maxCount, originalPhotos);

        }

        if(!pickerFragment.isAdded()){
          fragmentTransaction.add(R.id.container,pickerFragment);
          //fragmentTransaction.addToBackStack(null);
        }
        else{

          if(imagePagerFragment!=null){
            fragmentTransaction.hide(imagePagerFragment);

          }
          //Log.v("rush_yu", "hh");

          fragmentTransaction.show(pickerFragment);
          //fragmentTransaction.addToBackStack(null);
        }

    fragmentTransaction.commit();
    fragmentManager.executePendingTransactions();
  }




  public void showPagerFragment(List<String> photos, int index, int[] screenLocation,int height,
                          int width){
    fragmentTransaction = fragmentManager.beginTransaction();



        if(imagePagerFragment==null){
          imagePagerFragment = ImagePagerFragment.newInstance(photos, index, screenLocation, height,
                  width);
        }

        if(!imagePagerFragment.isAdded()){
          fragmentTransaction.add(R.id.container,imagePagerFragment);

        }
        else{

          if(pickerFragment!=null){
            fragmentTransaction.hide(pickerFragment);
          }


          imagePagerFragment.update(photos, index, screenLocation, height, width);
          fragmentTransaction.show(imagePagerFragment);
          //Log.v("rush_yu", "hh1");
        }

    fragmentTransaction.commit();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ImageLoader.getActualLoader().clearMomoryCache();

  }
}
