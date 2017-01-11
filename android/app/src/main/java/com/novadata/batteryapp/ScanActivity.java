package com.novadata.batteryapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class ScanActivity extends AppCompatActivity implements QRCodeView.Delegate {
    private static final String TAG = ScanActivity.class.getSimpleName();

    private QRCodeView mQRCodeView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        this.setTitle("扫描二维码");

        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        mQRCodeView.showScanRect();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        vibrate();
        Bundle bundle=this.getIntent().getExtras();
        bundle.putString("result", result);
        this.getIntent().putExtras(bundle);
        ScanActivity.this.setResult(RESULT_OK, this.getIntent());
        ScanActivity.this.finish();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_spot://开始扫描
                mQRCodeView.startSpot();
                break;
            case R.id.stop_spot://停止扫描
                mQRCodeView.stopSpot();
                break;
            case R.id.start_spot_showrect://显示扫描框并开始扫描
                mQRCodeView.startSpotAndShowRect();
                break;
            case R.id.stop_spot_hiddenrect://隐藏扫描框并停止扫描
                mQRCodeView.stopSpotAndHiddenRect();
                break;
            case R.id.show_rect://显示扫描框
                mQRCodeView.showScanRect();
                break;
            case R.id.hidden_rect://隐藏扫描框
                mQRCodeView.hiddenScanRect();
                break;
            case R.id.start_preview://开始预览，即保持画面不定格
                mQRCodeView.startCamera();
                break;
            case R.id.stop_preview://停止预览，即将画面定格
                mQRCodeView.stopCamera();
                break;
            case R.id.open_flashlight://开启闪光灯
                mQRCodeView.openFlashlight();
                break;
            case R.id.close_flashlight://关闭闪光灯
                mQRCodeView.closeFlashlight();
                break;
            case R.id.scan_barcode://扫描条形码
                mQRCodeView.changeToScanBarcodeStyle();
                break;
            case R.id.scan_qrcode://扫描二维码
                mQRCodeView.changeToScanQRCodeStyle();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mQRCodeView.showScanRect();
    }


}
