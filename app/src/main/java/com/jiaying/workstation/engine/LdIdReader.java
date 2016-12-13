package com.jiaying.workstation.engine;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.jiaying.workstation.constant.Constants;
import com.jiaying.workstation.entity.IdentityCardEntity;
import com.jiaying.workstation.interfaces.IidReader;
import com.jiaying.workstation.utils.LDAPI;
import com.jiaying.workstation.utils.MyLog;

/**
 * Created by Administrator on 2016/3/9 0009.
 */
public class LdIdReader implements IidReader {
    private static final String TAG = "LdIdReader";
    private OnIdReadCallback onIdReadCallback;
    private OnIdopenCallback onIdopenCallback;
    long ssart = System.currentTimeMillis();
    long ssend = System.currentTimeMillis();
    private boolean isFirstStart = true;
    private long firststart = System.currentTimeMillis();
    private HandlerThread readHandlerThread;
    private Handler cardHandler;
    private boolean readFlag = false;
    private int readStatus = 1;
    private static LdIdReader ldIdReader = null;

    private LDAPI ZAZAPI;

    private LdIdReader(Activity activity) {
        ZAZAPI = new LDAPI(activity, 4, 1);
        readFlag = true;

//**        线程中完成
        readHandlerThread = new HandlerThread("read card thread");
        readHandlerThread.start();
        cardHandler = new Handler(readHandlerThread.getLooper());
//**       线程中完成

//**  主线程中完成
//        readHandlerThread = new HandlerThread("read card thread");
//        readHandlerThread.start();
//        cardHandler = new Handler();
//**  主线程完成
    }

    public synchronized static LdIdReader getInstance(Activity activity) {
        if(ldIdReader==null){
            ldIdReader = new LdIdReader(activity);
        }
        return ldIdReader;
    }

    @Override
    public void open() {
        cardHandler.postDelayed(openTasks, 0);

    }

    private Runnable openTasks = new Runnable() {
        int status;

        @Override
        public void run() {
            // 上电
            int powerOnFlag = ZAZAPI.card_power_on();

            wait1sec();
            if (1 == powerOnFlag) {//上电成功
                // 初始化
                boolean initFlag = ZAZAPI.InitIDCardDevice(null);
                if (initFlag) {//初始化成功
                    status = 1;
                } else {//初始化失败
                    status = 0;
                }
            } else {//上电失败
                ZAZAPI.CloseIDCardDevice(null);
                ZAZAPI.card_power_on();
                status = 0;
            }
            onIdopenCallback.onOpen(status);
        }
    };

    private void wait1sec() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void read() {
        MyLog.e(TAG,"read");
        //开始读取身份证
        readFlag = false;
        isFirstStart = true;
        readStatus = 1;
        cardHandler.removeCallbacks(cardTasks);
        cardHandler.post(cardTasks);
    }

    private Runnable cardTasks = new Runnable() {
        public void run()// 运行该服务执行此函数
        {
            long timecount = 0;
            Log.e(TAG, "readStatus:" + readStatus);
            if (readFlag) {
                return;
            }

            switch (readStatus) {

                case 1:
                    ssart = System.currentTimeMillis();
                    if(isFirstStart){
                        firststart = System.currentTimeMillis();
                        isFirstStart = false;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                    }
                    //进行寻卡操作，找到卡返回true，未找到返回false。
                    if (ZAZAPI.FindIDCard())
                        readStatus++;
                    else {
                        readStatus = 1;
                        ssend = System.currentTimeMillis();
                        timecount = (ssend - firststart);
                        Log.e(TAG,"未刷卡：timecount:" + timecount);
                        if (timecount > Constants.COUNT_DOWN_TIME_20S) {
                            Log.e(TAG, "未刷卡超时");
                            readStatus = 0;
                        }
                    }
                    break;
                case 2:
                    //读卡计时开始
                    readStatus++;
                    break;
                case 3:
                    //寻找到卡后，需要选定身份证卡，如果选定成功则为true，选定失败则为false。
                    if (ZAZAPI.SelectCard())
                        readStatus++;
                    else
                        readStatus = 3;
                    ssend = System.currentTimeMillis();
                    timecount = (ssend - ssart);
                    if (timecount > 5000) {
                        Log.e(TAG, "超时");
                        readStatus = 0;
                    }
                    //读取身份证卡的信息，返回职位IDCard
                    break;
                case 4:
                    LDAPI.idcard = ZAZAPI.readCard();
                    if (LDAPI.idcard != null)
                        readStatus++;
                    else
                        readStatus = 4;
                    ssend = System.currentTimeMillis();
                    timecount = (ssend - ssart);
                    if (timecount > 5000) {
//                    mCallback.onResultInfo(temp, null);
                        readStatus = 0;
                    } else {
                        if (readStatus == 5) {
                            readStatus = 0;
                            sendIdCard();
                        } else {
                            readStatus = 4;
                        }
                    }
                    break;

            }
            if(readStatus!=0){
                //readStatus就不读了
                cardHandler.postDelayed(cardTasks, 500);
            }

        }

    };

    public void sendIdCard() {

        if (LDAPI.idcard != null) {

            //读取到了身份证信息
            IdentityCardEntity card = IdentityCardEntity.getIntance();
            card.setName(LDAPI.idcard.name);
            card.setSex(LDAPI.idcard.sex);
            card.setAddr(LDAPI.idcard.address);
            card.setNation(LDAPI.idcard.nation);
            card.setYear(LDAPI.idcard.birthday.substring(0, 4));
            card.setMonth(LDAPI.idcard.birthday.substring(4, 6));
            card.setDay(LDAPI.idcard.birthday.substring(6, 8));
            card.setIdcardno(LDAPI.idcard.idcardno);
            card.setPhotoBmp(ZAZAPI.getPhotoBmp());
//            card.setType("normal");
            onIdReadCallback.onRead(card);

        }
    }

    @Override
    public int close() {

        readFlag = true;
        cardHandler.removeCallbacks(cardTasks);
        if (ZAZAPI != null) {
            if (ZAZAPI.CloseIDCardDevice(null)) {
                return ZAZAPI.card_power_off();
            } else {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public void setOnIdReadCallback(OnIdReadCallback onIdReadCallback) {
        this.onIdReadCallback = onIdReadCallback;

    }

    @Override
    public void setOnIdOpenCallback(OnIdopenCallback onIdOpenCallback) {
        this.onIdopenCallback = onIdOpenCallback;
    }
}
