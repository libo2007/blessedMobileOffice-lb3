package com.jiaying.workstation.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.softfan.dataCenter.DataCenterClientService;
import android.softfan.dataCenter.task.DataCenterTaskCmd;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jiaying.workstation.R;
import com.jiaying.workstation.activity.plasmacollection.Res;
import com.jiaying.workstation.constant.Constants;
import com.jiaying.workstation.constant.IntentExtra;
import com.jiaying.workstation.entity.IdentityCardEntity;
import com.jiaying.workstation.entity.PlasmaMachineEntity;
import com.jiaying.workstation.thread.ObservableZXDCSignalListenerThread;
import com.jiaying.workstation.utils.BitmapUtils;
import com.jiaying.workstation.utils.CountDownTimerUtil;
import com.jiaying.workstation.utils.MyLog;
import com.jiaying.workstation.utils.SetTopView;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * 设备详情（启用，停用操作等）
 */
public class DeviceActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "DeviceActivity";
    private Button btn_start;
    private Button btn_stop;

    private ProgressDialog allocDevDialog = null;

    //    private AlertDialog.Builder failAllocDialogBuilder, succAllocDialogBuilder;
    private AlertDialog failAllocDialog = null;
    private AlertDialog succAllocDialog = null;
    private PlasmaMachineEntity plasmaMachineEntity;
    private ResponseHandler responseHandler;
    private ResContext resContext;
    //    private NullRes nullRes;
    private StartRes startRes;
    private  StopRes stopRes;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initVariables() {
        plasmaMachineEntity = (PlasmaMachineEntity) getIntent().getSerializableExtra(IntentExtra.EXTRA_PLASMAMACHINE);
        resContext = new ResContext();
        resContext.open();
        responseHandler = new ResponseHandler();
        ObservableZXDCSignalListenerThread.addObserver(responseHandler);
        //收到信息的各种状态
//        nullRes = new NullRes();
//        resContext.setCurState(nullRes);
        startRes =new StartRes();
        stopRes = new StopRes();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_device);
        new SetTopView(this, R.string.device_info, true);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(this);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_stop:
                //停用设备
                resContext.setCurState(stopRes);
                startDev();
                break;
            case R.id.btn_start:
                //启用设备
                resContext.setCurState(startRes);
                break;
            default:
                break;
        }
    }
    //启用
    private void startDev() {
        DataCenterClientService clientService = ObservableZXDCSignalListenerThread.getClientService();
        if (clientService != null) {
            DataCenterTaskCmd retcmd = new DataCenterTaskCmd();
            enable_device(retcmd, plasmaMachineEntity.getLocationID());
            clientService.getApDataCenter().addSendCmd(retcmd);
            showProgress("设备启用中", "请等待");
        } else {
            MyLog.e(TAG, "clientService==null");
        }
    }
    //启用
    private void stopDev() {
        DataCenterClientService clientService = ObservableZXDCSignalListenerThread.getClientService();
        if (clientService != null) {
            DataCenterTaskCmd retcmd = new DataCenterTaskCmd();

            disable_device(retcmd, plasmaMachineEntity.getLocationID());

            clientService.getApDataCenter().addSendCmd(retcmd);
            showProgress("设备停用中", "请等待");
        } else {
            MyLog.e(TAG, "clientService==null");
        }
    }
    private void disable_device(DataCenterTaskCmd retcmd, String locationId) {
        //       retcmd.setSelfNotify(this);
        retcmd.setCmd("disable_device");
        retcmd.setHasResponse(true);
        retcmd.setLevel(2);
        HashMap<String, Object> values = new HashMap<>();
        values.put("locationId", locationId);
        retcmd.setValues(values);
    }
    private void enable_device(DataCenterTaskCmd retcmd, String locationId) {
        //       retcmd.setSelfNotify(this);
        retcmd.setCmd("enable_device");
        retcmd.setHasResponse(true);
        retcmd.setLevel(2);
        HashMap<String, Object> values = new HashMap<>();
        values.put("locationId", locationId);
        retcmd.setValues(values);
    }
    /*分配设备超时后的对话框*/
    private void showFailDialog(String title, String msg) {

        if (isFinishing()) {
            return;
        }
        //AlertDialog.Builder normalDialog=new AlertDialog.Builder(getApplicationContext());
        AlertDialog.Builder failAllocDialogBuilder = new AlertDialog.Builder(this);
        failAllocDialogBuilder.setIcon(R.mipmap.ic_launcher);
        failAllocDialogBuilder.setTitle(title);
        failAllocDialogBuilder.setMessage(msg);


        failAllocDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //让失败对话框消失
                dialog.dismiss();
            }
        });
        failAllocDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dealBackClickEvent();
                Log.e("showFailDialog", "放弃");
            }
        });
        if (failAllocDialog == null) {
            failAllocDialog = failAllocDialogBuilder.create();
        }
        failAllocDialog.setCancelable(false);
        failAllocDialog.setCanceledOnTouchOutside(false);
        failAllocDialog.show();
    }

    /*分配设备成功后的对话框*/
    private void showSuccesslDialog(String title, String msg) {
        if (isFinishing()) {
            return;
        }
        //AlertDialog.Builder normalDialog=new AlertDialog.Builder(getApplicationContext());
        AlertDialog.Builder succAllocDialogBuilder = new AlertDialog.Builder(this);
        succAllocDialogBuilder.setIcon(R.mipmap.ic_launcher);
        succAllocDialogBuilder.setTitle(title);
        succAllocDialogBuilder.setMessage(msg);

        succAllocDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dealBackClickEvent();
            }
        });

        if (succAllocDialog == null) {
            succAllocDialog = succAllocDialogBuilder.create();
        }

        succAllocDialog.setCanceledOnTouchOutside(false);
        succAllocDialog.setCancelable(false);
        succAllocDialog.show();
    }

    private void showProgress(String title, String msg) {
        if (isFinishing()) {
            return;
        }
        if (allocDevDialog == null) {
            allocDevDialog = new ProgressDialog(this);
        }
        allocDevDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        allocDevDialog.setTitle(title);
        allocDevDialog.setMessage(msg);
        allocDevDialog.setIcon(R.mipmap.ic_launcher);
        allocDevDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dealBackClickEvent();
            }
        });

//        设置点击进度对话框外的区域对话框不消失
        allocDevDialog.setCanceledOnTouchOutside(false);
        allocDevDialog.setIndeterminate(false);
        allocDevDialog.setCancelable(false);
        allocDevDialog.show();
    }

    private class ResponseHandler extends Handler implements Observer {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //有些消息的msg.obj是null需要处理
            msg.obj = (msg.obj == null) ? (Res.NOTHING) : (msg.obj);
            switch ((Res) msg.obj) {
                case ALOC_SERVERRES_PASS:
                    resContext.handle((Res) msg.obj);
                    break;

                case ALOC_SERVERRES_NOT_PASS:
                    resContext.handle((Res) msg.obj);
                    break;

//                case ALOC_TABLETRES:
//                    resContext.handle((Res) msg.obj);
//                    break;
//
//                case ALOC_ZXDCRES:
//                    resContext.handle((Res) msg.obj);
//                    break;
            }

        }

        @Override
        public void update(Observable observable, Object data) {

            Message msg = Message.obtain();
            msg.obj = data;
            this.sendMessage(msg);

        }
    }

    private class ResContext {
        private State state;

        private Boolean isOpen = true;

        public synchronized void open() {
            this.isOpen = true;
        }

        public synchronized void close() {
            this.isOpen = false;
        }

        public void setCurState(State state) {
            this.state = state;
        }

        private synchronized void handle(Res res) {
            if (isOpen) {
                state.handle(res);
            }
        }
    }

    private abstract class State {
        abstract void handle(Res res);
    }



    private class StartRes extends State {

        @Override
        void handle(Res res) {
            switch (res) {

                case ALOC_SERVERRES_PASS:

                    showSuccesslDialog("设备启用结果", "启用成功");
                    disAllocDevDialog();
                    break;
                case ALOC_SERVERRES_NOT_PASS:
                    showFailDialog("设备启用结果", "启用失败");
                    disAllocDevDialog();
                    break;

            }
        }
    }

    private class StopRes extends State {

        @Override
        void handle(Res res) {
            switch (res) {

                case ALOC_SERVERRES_PASS:

                    showSuccesslDialog("设备停用结果", "停用成功");
                    disAllocDevDialog();
                    break;
                case ALOC_SERVERRES_NOT_PASS:
                    showFailDialog("设备停用结果", "停用失败");
                    disAllocDevDialog();
                    break;

            }
        }
    }

    //关闭分配中对话框
    private void disAllocDevDialog() {
        if (!isFinishing()) {
            if (allocDevDialog != null) {
                allocDevDialog.dismiss();
                allocDevDialog = null;
            }
        }
    }

    /**
     * 处理返回按钮
     */
    private void dealBackClickEvent() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
