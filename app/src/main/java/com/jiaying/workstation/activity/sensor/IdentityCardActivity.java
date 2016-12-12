package com.jiaying.workstation.activity.sensor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiaying.workstation.R;
import com.jiaying.workstation.activity.BaseActivity;
import com.jiaying.workstation.activity.MainActivity;
import com.jiaying.workstation.activity.plasmacollection.ShowDonorInfoActivity;
import com.jiaying.workstation.constant.IntentExtra;
import com.jiaying.workstation.constant.TypeConstant;
import com.jiaying.workstation.engine.LdIdReader;
import com.jiaying.workstation.engine.ProxyIdReader;
import com.jiaying.workstation.entity.IdentityCardEntity;
import com.jiaying.workstation.interfaces.IidReader;
import com.jiaying.workstation.interfaces.OnCountDownTimerFinishCallback;
import com.jiaying.workstation.utils.CountDownTimerUtil;
import com.jiaying.workstation.utils.MyLog;
import com.jiaying.workstation.utils.SetTopView;

/*
身份证模块
 */
public class IdentityCardActivity extends BaseActivity implements IidReader.OnIdReadCallback, IidReader.OnIdopenCallback ,OnCountDownTimerFinishCallback {
    private static final String TAG = "IdentityCardActivity";
    private TextView result_txt;
    private TextView state_txt;
    private ImageView photo_image;
    private String donorName;
    private Bitmap avtar;
    private String idCardNO;
    private ProxyIdReader proxyIdReader;
    private CountDownTimerUtil countDownTimerUtil;
    private IidReader iidReader;
    public static TextToSpeech mTts;
    private int source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initVariables() {

        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();

        source = getIntent().getIntExtra(IntentExtra.EXTRA_TYPE, 0);

        //  身份证读取预备
        iidReader = LdIdReader.getInstance(this);
        proxyIdReader = ProxyIdReader.getInstance(iidReader);

        //  指纹读卡器读取到身份证后才会调用该回调函数
        proxyIdReader.setOnIdReadCallback(this);
        proxyIdReader.setOnIdOpenCallback(this);
        proxyIdReader.open();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_identity_card);
        new SetTopView(this, R.string.title_activity_identity, true);
        result_txt = (TextView) findViewById(R.id.result_txt);
        photo_image = (ImageView) findViewById(R.id.photo_image);
        //开始倒计时
//        countDownTimerUtil = CountDownTimerUtil.getInstance(result_txt, this);
//        countDownTimerUtil.start();
        startTimer();
    }

    @Override
    public void loadData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
//        proxyIdReader.open();
    }

    private void showOpenResult(int status) {
        if (status == 1) {
            Toast.makeText(this, "打开证件设备：成功",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "打开证件设备：失败",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRead(IdentityCardEntity identityCardEntity) {

        if (identityCardEntity != null) {
            IdentityCardActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    countDownTimerUtil.cancel();
                    finishTimer();
                }
            });

            donorName = identityCardEntity.getName();
            avtar = identityCardEntity.getPhotoBmp();
            idCardNO = identityCardEntity.getIdcardno();

            //认证通过后跳到指纹界面
            new Handler().postDelayed(new runnable(), 10);

        } else {
            MyLog.e(TAG, "card is null");
        }
    }

    @Override
    public void onOpen(int status) {
        showOpenResult(status);
        //  打开身份证读卡器
        if (1 == status) {
            //开始尝试读取身份证信息
            proxyIdReader.read();
        } else {
            proxyIdReader.close();
//            this.finish();
        }
    }


    private class runnable implements Runnable {
        @Override
        public void run() {
            switch (source) {
                case TypeConstant.TYPE_REG:
//                    goToFaceCollection();
                    goToShowDonorInfo();
                    break;
                case TypeConstant.TYPE_BLOODPLASMACOLLECTION:
                    goToShowDonorInfo();
                    break;
            }
        }
    }

    private void goToShowDonorInfo() {
        Intent it;
        it = new Intent(IdentityCardActivity.this, ShowDonorInfoActivity.class);

        it.putExtra(IntentExtra.EXTRA_TYPE, source);
        it.putExtra("donorName", donorName);
        it.putExtra("avatar", avtar);
        it.putExtra("idCardNO", idCardNO);

        startActivity(it);
//        finish();
    }

    private void goToFaceCollection() {
        Intent it;
        it = new Intent(IdentityCardActivity.this, FaceCollectionActivity.class);
        it.putExtra(IntentExtra.EXTRA_TYPE, source);
        startActivity(it);
//        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        proxyIdReader.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        proxyIdReader.close();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MyLog.e(TAG,"onNewIntent=" + this.toString());
        startTimer();
        photo_image.setImageResource(R.mipmap.identity_card);
        proxyIdReader.read();
    }
    private void startTimer(){
        countDownTimerUtil = CountDownTimerUtil.getInstance(result_txt, this);
        countDownTimerUtil.setOnCountDownTimerFinishCallback(this);
        countDownTimerUtil.start();
    }
    private void finishTimer(){
        if (countDownTimerUtil != null) {
            countDownTimerUtil.cancel();
            countDownTimerUtil = null;
        }
    }

    @Override
    public void onFinish() {
        dealBackClickEvent();
    }
    /**
     * 处理返回按钮
     */
    private void dealBackClickEvent() {
        finishTimer();
        startActivity(new Intent(this, MainActivity.class));
    }
}

