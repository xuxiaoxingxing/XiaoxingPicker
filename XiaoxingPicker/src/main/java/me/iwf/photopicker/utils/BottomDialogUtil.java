package me.iwf.photopicker.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import me.iwf.photopicker.R;


/**
 * @author 小星 QQ:753940262
 * @class describe 底部弹出框
 * @time
 */
public class BottomDialogUtil {

    private Context mContext;
    private Dialog mDialog;

    public BottomDialogUtil(Context context, LinearLayout root) {

        this.mContext = context;
        initDialog(root);
    }

    public BottomDialogUtil(Context context, LinearLayout root, int gravity) {

        this.mContext = context;
        initCenterDialog(root, gravity);
    }


    /**
     * @class describe 初始化    LinearLayout root = (LinearLayout) LayoutInflater.from(mContext).inflate(layout_bottom_dialog, null);
     * @author 小星 QQ:753940262
     * @time 2018/6/22 0022 14:23
     */
    private View initDialog(LinearLayout root) {

        mDialog = new Dialog(mContext, R.style.BottomDialog);

        //初始化视图
//        root.findViewById(R.id.btn_choose_img).setOnClickListener(context);
//        root.findViewById(R.id.btn_open_camera).setOnClickListener(context);
//        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCameraDialog.dismiss();
//            }
//        });
        mDialog.setContentView(root);
        mDialog.setCanceledOnTouchOutside(false);
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) mContext.getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);

        return root;
    }

    private View initCenterDialog(LinearLayout root, int gravity) {

        mDialog = new Dialog(mContext, R.style.BottomDialog);

        //初始化视图
//        root.findViewById(R.id.btn_choose_img).setOnClickListener(context);
//        root.findViewById(R.id.btn_open_camera).setOnClickListener(context);
//        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCameraDialog.dismiss();
//            }
//        });
        mDialog.setContentView(root);
        Window dialogWindow = mDialog.getWindow();
//        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setGravity(gravity);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) mContext.getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);

        return root;
    }

    /**
     * @class describe 隐藏dialog
     * @author 小星 QQ:753940262
     * @time 2018/6/22 0022 14:23
     */
    public void dismissDiaolog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    /**
     * @class describe 显示dialog
     * @author 小星 QQ:753940262
     * @time 2018/6/22 0022 14:27
     */
    public void showDiaolog() {
        if (mDialog != null) {
            mDialog.show();
        }
    }
}
