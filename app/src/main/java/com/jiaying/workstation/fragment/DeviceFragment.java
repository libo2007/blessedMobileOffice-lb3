package com.jiaying.workstation.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.jiaying.workstation.R;
import com.jiaying.workstation.activity.DeviceActivity;
import com.jiaying.workstation.activity.plasmacollection.ChangeDeviceResultActivity;
import com.jiaying.workstation.activity.plasmacollection.Res;
import com.jiaying.workstation.adapter.PlasmaMachineSelectAdapter;
import com.jiaying.workstation.app.MobileofficeApp;
import com.jiaying.workstation.constant.IntentExtra;
import com.jiaying.workstation.entity.PlasmaMachineEntity;
import com.jiaying.workstation.thread.ObservableZXDCSignalListenerThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 设备列表（点击后可以进入操作设备（启用停用等操作））
 */
public class DeviceFragment extends Fragment {
    private static final String TAG = "DeviceFragment";
    private GridView mGridView;
    private List<PlasmaMachineEntity> mList;
    private PlasmaMachineSelectAdapter mAdapter;


    private PlasmaMachineStateHandlerObserver plasmaMachineStateHandlerObserver;
    private ResContext resContext;
    private PlasmaMachineStateRes plasmaMachineStateRes;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridview);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(getActivity(), DeviceActivity.class);
                startActivity(it);
            }
        });
        mList = new ArrayList<PlasmaMachineEntity>();
        mAdapter = new PlasmaMachineSelectAdapter(mList, getActivity());
        mGridView.setAdapter(mAdapter);
        mList.addAll(MobileofficeApp.getPlasmaMachineEntityList());
        mAdapter.notifyDataSetChanged();
        resContext = new ResContext();
        resContext.open();
        plasmaMachineStateHandlerObserver = new PlasmaMachineStateHandlerObserver();
        ObservableZXDCSignalListenerThread.addObserver(plasmaMachineStateHandlerObserver);
        resContext.setCurState(plasmaMachineStateRes);
        return view;
    }



    private class PlasmaMachineStateHandlerObserver extends Handler implements Observer {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            msg.obj = (msg.obj == null) ? (Res.NOTHING) : (msg.obj);
            switch ((Res) msg.obj) {
                case ZXDC_STATE_CHANGE:
                    resContext.handle((Res) msg.obj);
                    break;
            }

        }

        @Override
        public void update(Observable observable, Object data) {

            Message msg = Message.obtain();
            msg.obj = data;
            this.sendMessage(msg);

        }
    }

    private class PlasmaMachineStateRes extends State {

        @Override
        void handle(Res res) {
            switch (res) {
                case ZXDC_STATE_CHANGE:
                    mList.clear();
                    mList.addAll(MobileofficeApp.getPlasmaMachineEntityList());
                    mAdapter.notifyDataSetChanged();
                    resContext.setCurState(plasmaMachineStateRes);
                    break;

            }

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
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(plasmaMachineStateHandlerObserver!=null){
            ObservableZXDCSignalListenerThread.deleteObserver(plasmaMachineStateHandlerObserver);
        }
    }
}

