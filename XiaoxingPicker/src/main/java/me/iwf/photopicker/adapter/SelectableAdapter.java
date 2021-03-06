package me.iwf.photopicker.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.entity.PhotoDirectory;
import me.iwf.photopicker.event.Selectable;

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> implements Selectable {

  private static final String TAG = SelectableAdapter.class.getSimpleName();

  protected List<PhotoDirectory> photoDirectories;
  protected List<Photo> selectedPhotos;

  protected ArrayList<String> originalPhotos = null;

  public int currentDirectoryIndex = 0;


  public SelectableAdapter() {
    photoDirectories = new ArrayList<>();
    selectedPhotos = new ArrayList<>();
  }



  @Override public boolean isSelected(Photo photo) {
    if (originalPhotos != null && originalPhotos.contains(photo.getPath()) && !selectedPhotos.contains(photo)) {
      selectedPhotos.add(photo);
    }
    return getSelectedPhotos().contains(photo);
  }



  @Override public void toggleSelection(Photo photo) {
    if (selectedPhotos.contains(photo)) {
      selectedPhotos.remove(photo);
      if(originalPhotos!=null &&originalPhotos.contains(photo.getPath())){
        originalPhotos.remove(photo.getPath());
      }
    } else {
      selectedPhotos.add(photo);
    }
  }



  @Override public void clearSelection() {
    selectedPhotos.clear();
  }



  @Override public int getSelectedItemCount() {
    return selectedPhotos.size();
  }


  public void setCurrentDirectoryIndex(int currentDirectoryIndex) {
    this.currentDirectoryIndex = currentDirectoryIndex;
  }


  public List<Photo> getCurrentPhotos() {
    return photoDirectories.get(currentDirectoryIndex).getPhotos();
  }


  public List<String> getCurrentPhotoPaths() {
    List<String> currentPhotoPaths = new ArrayList<>(getCurrentPhotos().size());
    for (Photo photo : getCurrentPhotos()) {
      currentPhotoPaths.add(photo.getPath());
    }
    return currentPhotoPaths;
  }


  public List<Photo> getSelectedPhotos() {
    return selectedPhotos;
  }

}