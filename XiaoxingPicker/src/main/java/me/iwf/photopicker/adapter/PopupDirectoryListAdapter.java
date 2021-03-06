package me.iwf.photopicker.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hss01248.image.ImageLoader;
import com.hss01248.image.config.GlobalConfig;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPickUtils;
import me.iwf.photopicker.R;
import me.iwf.photopicker.entity.PhotoDirectory;


public class PopupDirectoryListAdapter extends BaseAdapter {


  private List<PhotoDirectory> directories = new ArrayList<>();
  //private RequestManager glide;

  public PopupDirectoryListAdapter( List<PhotoDirectory> directories) {
    this.directories = directories;
    //this.glide = glide;
  }


  @Override public int getCount() {
    return directories.size();
  }


  @Override public PhotoDirectory getItem(int position) {
    return directories.get(position);
  }


  @Override public long getItemId(int position) {
    return directories.get(position).hashCode();
  }


  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {

      holder = PhotoPickUtils.holderGenerator.newDirViewHolder(GlobalConfig.context);
      convertView= holder.rootView;
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    holder.bindData(directories.get(position));

    return convertView;
  }

  public static class ViewHolder {

    public ImageView ivCover;
    public TextView tvName;
    public TextView tvCount;
    public View rootView;

    public ViewHolder(View rootView) {
      this.rootView = rootView;

    }

    public void assignView(){

    }

    public void bindData(PhotoDirectory directory) {
      ImageLoader.with(null).file(directory.getCoverPath())
          .widthHeight(100,100)
          .into(ivCover);
      tvName.setText(directory.getName());
      tvCount.setText(tvCount.getContext().getString(R.string.__picker_image_count, directory.getPhotos().size()));
    }
  }

}
