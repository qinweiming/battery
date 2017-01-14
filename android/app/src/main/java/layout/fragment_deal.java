package layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.novadata.batteryapp.MainActivity;
import com.novadata.batteryapp.R;
import com.novadata.batteryapp.ScanActivity;

import utils.PhotoSaver;

public class fragment_deal extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {


    private View view;
    private LocalBroadcastManager broadcastManager;
    private LinearLayout default_layout, company_layout, the_4s_layout;
    private TextView start2Scan_Tv, photo_Tv, confirm_Tv;
    private ImageView photo_Iv;
    private RadioGroup rg;
    private Bitmap bitmap;

    int login_status = -1;
    int status_IO;
    static final int IMPORT = 0, EXPORT = 1;
    static final int DEFAULT_STATUS = -1, USER_4S = 1, USER_COMPANY = 0;
    final static int REQUEST_PHOTO = 0, REQUEST_SCAN = 1;
    private static final String PATH = "/sdcard/battery/photos";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        int deal_layout = R.layout.fragment_deal;
        view = inflater.inflate(deal_layout, container, false);

        checkLayout(login_status);
        initCompanyView();
        init4SView();

        return view;
    }

    private void init4SView() {
        photo_Tv = (TextView) view.findViewById(R.id.photo_button);
        confirm_Tv = (TextView) view.findViewById(R.id.confirm_button);
        photo_Iv = (ImageView) view.findViewById(R.id.photo);

        photo_Tv.setOnClickListener(this);
        confirm_Tv.setOnClickListener(this);
    }

    private void initCompanyView() {
        start2Scan_Tv = (TextView) view.findViewById(R.id.scan_button);
        rg = (RadioGroup) view.findViewById(R.id.company_RadioGroup);

        start2Scan_Tv.setOnClickListener(this);
        rg.setOnCheckedChangeListener(this);
    }

    private void checkLayout(int login_status) {
        default_layout = (LinearLayout) view.findViewById(R.id.deal_default_layout);
        company_layout = (LinearLayout) view.findViewById(R.id.deal_company_layout);
        the_4s_layout = (LinearLayout) view.findViewById(R.id.deal_4s_layout);

        switch (login_status){
            case USER_COMPANY:{
                Log.i("Tag", "USER_COMPANY has received");
                default_layout.setVisibility(View.GONE);
                the_4s_layout.setVisibility(View.GONE);
                break;
            }
            case USER_4S:{
                Log.i("Tag", "USER_4S has received");
                default_layout.setVisibility(View.GONE);
                company_layout.setVisibility(View.GONE);
                break;
            }
            default:{
                Log.i("Tag", "DEFAULT_STATUS has received");
                company_layout.setVisibility(View.GONE);
                the_4s_layout.setVisibility(View.GONE);
                break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册广播
        registerReceiver();
    }

    /**
     * 注册广播接收器
     */
    private void registerReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Login_status");
        broadcastManager.registerReceiver(mAdDownLoadReceiver, intentFilter);
    }

    private BroadcastReceiver mAdDownLoadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            login_status = intent.getIntExtra("login_status", DEFAULT_STATUS);
        }
    };
    /**
     * 注销广播
     */
    @Override public void onDetach() {
        super.onDetach();
        broadcastManager.unregisterReceiver(mAdDownLoadReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scan_button://点击扫描二维码按钮
                Intent intent = new Intent(MainActivity.mainActivity, ScanActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("result", "result");
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_SCAN);
                break;
            case R.id.photo_button:
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//实例化Intent对象,使用MediaStore的ACTION_IMAGE_CAPTURE常量调用系统相机
                startActivityForResult(intent2, REQUEST_PHOTO);//开启相机，传入上面的Intent对象
                break;
            case R.id.confirm_button:
                if (bitmap != null ){
                    String photoName = PhotoSaver.createPhotoName();
                    PhotoSaver.savePhoto2SDCard(PATH, photoName, bitmap);
                    Toast.makeText(getActivity(), "照片存储成功！路径为 " + PATH, Toast.LENGTH_SHORT).show();
                    //TODO 还应检查车架号是否输入
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId){
            case R.id.import_radioButton:
                status_IO = IMPORT;
                Log.i("Tag", "IMPORT status has been selected");
                break;
            case R.id.export_radioButton:
                status_IO = EXPORT;
                Log.i("Tag", "EXPORT status has been selected");
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK){
            Bundle bundle=data.getExtras();

            if (requestCode == REQUEST_SCAN){
                String result= bundle.getString("result");//获得扫描的二维码信息
                //TODO 将结果展示
            }
            if (requestCode == REQUEST_PHOTO){
                bitmap=(Bitmap) bundle.get("data");//从附加值中获取返回的图像
                photo_Iv.setImageBitmap(bitmap);//显示缩略图像
            }

        } if(resultCode == getActivity().RESULT_CANCELED) {
            Toast.makeText(getActivity(), "扫描结束", Toast.LENGTH_SHORT).show();
        }
    }


}
