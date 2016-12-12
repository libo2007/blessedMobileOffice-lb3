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
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.jiaying.workstation.R;
import com.jiaying.workstation.activity.plasmacollection.ChangeDeviceResultActivity;
import com.jiaying.workstation.activity.plasmacollection.ManualIdentityCardActivity;
import com.jiaying.workstation.activity.plasmacollection.Res;
import com.jiaying.workstation.activity.plasmacollection.SelectPlasmaMachineResultActivity;
import com.jiaying.workstation.activity.sensor.FingerprintActivity;
import com.jiaying.workstation.activity.sensor.IdentityCardActivity;
import com.jiaying.workstation.adapter.PlasmaMachineSelectAdapter;
import com.jiaying.workstation.app.MobileofficeApp;
import com.jiaying.workstation.constant.IntentExtra;
import com.jiaying.workstation.constant.TypeConstant;
import com.jiaying.workstation.entity.PlasmaMachineEntity;
import com.jiaying.workstation.thread.ObservableZXDCSignalListenerThread;
import com.jiaying.workstation.utils.DealFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 换机
 */
public class ChangeDeviceFragment extends Fragment {
    private Button nurse_login_btn;
    private Button pulp_btn;
    private DealFlag btn_collection_flag;

    private Button btn_idcard_forget;
    private Button btn_idcard_broken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_device, container, false);

        pulp_btn = (Button) view.findViewById(R.id.btn_collection);
        pulp_btn.setOnClickListener(new ClickListener());
        btn_collection_flag = new DealFlag();

        btn_idcard_forget = (Button) view.findViewById(R.id.btn_idcard_forget);
        btn_idcard_forget.setOnClickListener(new ClickListener());
        btn_idcard_broken = (Button) view.findViewById(R.id.btn_idcard_broken);
        btn_idcard_broken.setOnClickListener(new ClickListener());
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        btn_collection_flag.reset();
    }

    //献浆
    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (btn_collection_flag.isFirst()) {
                Intent it = null;
                switch (v.getId()) {
                    case R.id.btn_collection:
                        it = new Intent(getActivity(), IdentityCardActivity.class);
                        it.putExtra("type", "normal");
                        it.putExtra(IntentExtra.EXTRA_TYPE, TypeConstant.TYPE_CHANGE_DEVICE);
                        break;

                    case R.id.btn_idcard_forget:
                        it = new Intent(getActivity(), ManualIdentityCardActivity.class);
                        it.putExtra(IntentExtra.EXTRA_TYPE, TypeConstant.TYPE_CHANGE_DEVICE);
                        it.putExtra("type", "forgot");
                        break;

                    case R.id.btn_idcard_broken:
                        it = new Intent(getActivity(), ManualIdentityCardActivity.class);
                        it.putExtra(IntentExtra.EXTRA_TYPE, TypeConstant.TYPE_CHANGE_DEVICE);
                        it.putExtra("type", "broken");
                        break;

                    default:
                        break;
                }
                if (it != null) {
                    startActivity(it);
                }

            }
        }
    }
}
