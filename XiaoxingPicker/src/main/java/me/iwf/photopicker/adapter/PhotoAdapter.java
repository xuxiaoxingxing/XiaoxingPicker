package me.iwf.photopicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hss01248.image.ImageLoader;
import com.hss01248.photoouter.PhotoCallback;
import com.hss01248.photoouter.PhotoUtil;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPickUtils;
import me.iwf.photopicker.PhotoPreview;
import me.iwf.photopicker.R;
import me.iwf.photopicker.utils.BottomDialogUtil;
import me.iwf.photopicker.utils.ProxyTools;
import me.iwf.photopicker.widget.MultiPickResultView;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private ArrayList<String> photoPaths;
    private LayoutInflater inflater;

    private Context mContext;
    private final static int COL_NUMBER_DEFAULT = 3;

    public void setAction(@MultiPickResultView.MultiPicAction int action) {
        this.action = action;
    }

    private int action;

    private int imageSize;
    private int columnNumber = COL_NUMBER_DEFAULT;

    public PhotoAdapter(Context mContext, ArrayList<String> photoPaths) {
        this.photoPaths = photoPaths;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        padding = dip2Px(8);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        imageSize = widthPixels / columnNumber / 3;

    }

    public void add(ArrayList<String> photoPaths) {
        if (photoPaths != null && photoPaths.size() > 0) {
            this.photoPaths.addAll(photoPaths);
            notifyDataSetChanged();
        }

    }

    public void refresh(List<String> photoPaths) {
        this.photoPaths.clear();
        if (photoPaths != null && photoPaths.size() > 0) {
            this.photoPaths.addAll(photoPaths);
        }
        notifyDataSetChanged();
    }

    public void refresh1(List<String> photoPaths) {
        if (photoPaths != null && photoPaths.size() > 0) {
            this.photoPaths.addAll(photoPaths);
        }
        notifyDataSetChanged();
    }


    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // View itemView = inflater.inflate(R.layout.__picker_item_photo, parent, false);
        PhotoViewHolder viewHolder = PhotoPickUtils.holderGenerator.newGridViewHolder2(mContext);
        //parent.addView(viewHolder.itemView);
        return viewHolder;
    }

    public int dip2Px(int dip) {
        // px/dip = density;
        float density = mContext.getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + .5f);
        return px;
    }

    int padding;

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

        if (action == MultiPickResultView.ACTION_SELECT) {
            // RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.ivPhoto.getLayoutParams();

            holder.ivPhoto.setPadding(padding, padding, padding, padding);


            if (position == getItemCount() - 1) {
                //ImageLoader.with(mContext).widthHeight()
                ImageLoader.with(null)
                        .res(R.drawable.icon_pic_default)
                        //.placeHolder(R.drawable.icon_pic_default,true)
                        .into(holder.ivPhoto);
                holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (photoPaths != null && photoPaths.size() == 9) {
                            Toast.makeText(mContext, "已選了9張圖片", Toast.LENGTH_SHORT).show();
                        } else {
//              PhotoPickUtils.startPick().setSelected(photoPaths).start((Activity) mContext,77);
                            chooseXiang();
                        }
                    }
                });

                holder.deleteBtn.setVisibility(View.GONE);

            } else {
                String str = photoPaths.get(position);
                Log.e("file", str);

                ImageLoader.with(null)
                        .file(str)
                        .widthHeight(imageSize, imageSize)
                        .placeHolder(R.drawable.__picker_default_weixin, true)
                        .into(holder.ivPhoto);


                holder.deleteBtn.setVisibility(View.VISIBLE);
                holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoPaths.remove(position);
                        notifyDataSetChanged();
                    }
                });

                holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PhotoPreview.builder()
                                .setPhotos(photoPaths)
                                .setAction(action)
                                .setCurrentItem(position)
                                .start((Activity) mContext);
                    }
                });
            }
        } else if (action == MultiPickResultView.ACTION_ONLY_SHOW) {
            //Uri uri = Uri.fromFile(new File(photoPaths.get(position)));
            //Uri uri = Uri.parse(photoPaths.get(position));
            Log.e("pic", photoPaths.get(position));
            ImageLoader.with(null)
                    .file(photoPaths.get(position))
                    .widthHeight(imageSize, imageSize)
                    .placeHolder(R.drawable.__picker_default_weixin, true)
                    .into(holder.ivPhoto);


            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PhotoPreview.builder()
                            .setPhotos(photoPaths)
                            .setAction(action)
                            .setCurrentItem(position)
                            .start((Activity) mContext);
                }
            });
        }


    }


    @Override
    public int getItemCount() {
        return action == MultiPickResultView.ACTION_SELECT ? photoPaths.size() + 1 : photoPaths.size();
    }


    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivPhoto;
        public View vSelected;
        public View cover;
        public View deleteBtn;

        public PhotoViewHolder(ViewGroup itemView) {
            super(itemView);


        }

        public void assiginView() {

        }
    }

    BottomDialogUtil mBottomDialogUtilShare;

    private void chooseXiang() {

        final List<String> strings = new ArrayList<>();
        strings.add("取自相機");
        strings.add("取自相簿");


        LinearLayout root = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.sales_client_bottom_dialog_pai_zhao, null);
        Button btn_xiang_ce = root.findViewById(R.id.btn_xiang_ce);
        Button ben_xiang_ji = root.findViewById(R.id.ben_xiang_ji);
        Button btn_qu_xiao = root.findViewById(R.id.btn_qu_xiao);

        btn_qu_xiao.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (mBottomDialogUtilShare != null)
                    mBottomDialogUtilShare.dismissDiaolog();

            }
        });
        ben_xiang_ji.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (mBottomDialogUtilShare != null)
                    mBottomDialogUtilShare.dismissDiaolog();


                PhotoUtil.cropAvatar(true)
                        .start((Activity) mContext, 23, ProxyTools.getShowMethodInfoProxy(new PhotoCallback() {
                            @Override
                            public void onFail(String msg, Throwable r, int requestCode) {
                            }

                            @Override
                            public void onSuccessSingle(String originalPath, String compressedPath, int requestCode) {

                                List<String> path = new ArrayList<String>();
                                path.add(compressedPath);
                                refresh1(path);
                            }

                            @Override
                            public void onSuccessMulti(List<String> originalPaths, List<String> compressedPaths, int requestCode) {
                            }

                            @Override
                            public void onCancel(int requestCode) {
                            }
                        }));
            }
        });
        btn_xiang_ce.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (mBottomDialogUtilShare != null)
                    mBottomDialogUtilShare.dismissDiaolog();

                PhotoUtil.begin()
                        .setNeedCropWhenOne(true)
                        .setNeedCompress(true)
                        .setMaxSelectCount(6)
                        .setCropMuskOval()
                        .setSelectGif()

                        .start((Activity) mContext, 33, ProxyTools.getShowMethodInfoProxy(new PhotoCallback() {
                            @Override
                            public void onFail(String msg, Throwable r, int requestCode) {
                            }

                            @Override
                            public void onSuccessSingle(String originalPath, String compressedPath, int requestCode) {
                            }

                            @Override
                            public void onSuccessMulti(List<String> originalPaths, List<String> compressedPaths, int requestCode) {
                                refresh1(originalPaths);
                            }

                            @Override
                            public void onCancel(int requestCode) {
                            }
                        }));
            }
        });

        mBottomDialogUtilShare = new

                BottomDialogUtil(mContext, root);

        mBottomDialogUtilShare.showDiaolog();

//        StyledDialog.buildBottomItemDialog(strings, "cancle", new MyItemDialogListener() {
//            @Override
//            public void onItemClick(CharSequence text, int position) {
//                switch (position) {
//                    case 0:
//
//                        PhotoUtil.cropAvatar(true)
//                                .start((Activity) mContext, 23, ProxyTools.getShowMethodInfoProxy(new PhotoCallback() {
//                                    @Override
//                                    public void onFail(String msg, Throwable r, int requestCode) {
//                                    }
//
//                                    @Override
//                                    public void onSuccessSingle(String originalPath, String compressedPath, int requestCode) {
//
//                                        List<String> path = new ArrayList<String>();
//                                        path.add(compressedPath);
//                                        refresh1(path);
//                                    }
//
//                                    @Override
//                                    public void onSuccessMulti(List<String> originalPaths, List<String> compressedPaths, int requestCode) {
//                                    }
//
//                                    @Override
//                                    public void onCancel(int requestCode) {
//                                    }
//                                }));
//
//                        break;
//                    case 1:
//
//                        PhotoUtil.begin()
//                                .setNeedCropWhenOne(true)
//                                .setNeedCompress(true)
//                                .setMaxSelectCount(6)
//                                .setCropMuskOval()
//                                .setSelectGif()
//
//                                .start((Activity) mContext, 33, ProxyTools.getShowMethodInfoProxy(new PhotoCallback() {
//                                    @Override
//                                    public void onFail(String msg, Throwable r, int requestCode) {
//                                    }
//
//                                    @Override
//                                    public void onSuccessSingle(String originalPath, String compressedPath, int requestCode) {
//                                    }
//
//                                    @Override
//                                    public void onSuccessMulti(List<String> originalPaths, List<String> compressedPaths, int requestCode) {
//                                        refresh1(originalPaths);
//                                    }
//
//                                    @Override
//                                    public void onCancel(int requestCode) {
//                                    }
//                                }));
//                        break;
//                }
//            }
//
//            @Override
//            public void onBottomBtnClick() {
//            }
//        }).show();
    }

}
