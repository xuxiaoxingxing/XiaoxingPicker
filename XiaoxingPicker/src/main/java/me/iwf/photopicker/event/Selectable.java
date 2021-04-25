package me.iwf.photopicker.event;

import me.iwf.photopicker.entity.Photo;


public interface Selectable {



  boolean isSelected(Photo photo);


  void toggleSelection(Photo photo);


  void clearSelection();


  int getSelectedItemCount();

}
