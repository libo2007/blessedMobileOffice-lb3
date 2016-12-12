package com.jiaying.workstation.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.jiaying.workstation.R;
import com.jiaying.workstation.app.MobileofficeApp;
import com.jiaying.workstation.db.DataPreference;
import com.jiaying.workstation.fragment.BloodPlasmaCollectionFragment;
import com.jiaying.workstation.fragment.ChangeDeviceFragment;
import com.jiaying.workstation.fragment.DeviceFragment;
import com.jiaying.workstation.fragment.DispatchFragment;
import com.jiaying.workstation.fragment.PhysicalExamFragment;
import com.jiaying.workstation.fragment.RegisterFragment;
import com.jiaying.workstation.fragment.SearchFragment;

/**
 * 主界面包括（建档，登记，体检，采浆，调度四大部分；以及一个查询）
 */
public class MainActivity extends BaseActivity {
    private FragmentManager fragmentManager;

    private RadioGroup tabGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new BloodPlasmaCollectionFragment()).commit();

        (findViewById(R.id.btn_1)).setVisibility(View.GONE);
        (findViewById(R.id.btn_2)).setVisibility(View.GONE);
        (findViewById(R.id.btn_3)).setVisibility(View.GONE);
        (findViewById(R.id.btn_4)).setVisibility(View.VISIBLE);
        (findViewById(R.id.btn_5)).setVisibility(View.GONE);
        (findViewById(R.id.btn_6)).setVisibility(View.GONE);
//        (findViewById(R.id.btn_6)).setVisibility(View.VISIBLE);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);

        tabGroup = (RadioGroup) findViewById(R.id.group);
        tabGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_1:

                        break;
                    case R.id.btn_2:
                        //登记
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new RegisterFragment()).commit();
                        break;
                    case R.id.btn_3:
                        //体检
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new PhysicalExamFragment()).commit();
                        break;
                    case R.id.btn_4:
                        //采集血浆
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new BloodPlasmaCollectionFragment()).commit();
                        break;
                    case R.id.btn_5:
                        //调度
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new DispatchFragment()).commit();
                        break;
                    case R.id.btn_6:
                        //查询
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
                        break;
                    case R.id.btn_7:
                        //换机
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new ChangeDeviceFragment()).commit();
                        break;
                    case R.id.btn_8:
                        //设备（包括启用和停用）
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new DeviceFragment()).commit();
                        break;
                }
            }
        });
    }

    @Override
    public void loadData() {
    }

    @Override
    public void initVariables() {
    }

    //退出
    private void loginOut() {
        MobileofficeApp.clearPlasmaMachineEntityList();
        DataPreference preference = new DataPreference(MainActivity.this);
        preference.writeStr("nurse_id", "wrong");
        preference.commit();
    }

}
