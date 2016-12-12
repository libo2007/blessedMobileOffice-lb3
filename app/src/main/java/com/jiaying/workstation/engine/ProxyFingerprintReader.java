package com.jiaying.workstation.engine;

import com.jiaying.workstation.interfaces.IfingerprintReader;
import com.jiaying.workstation.utils.MyLog;

/**
 * 作者：lenovo on 2016/3/4 13:27
 * 邮箱：353510746@qq.com
 * 功能：指纹代理
 */
public class ProxyFingerprintReader implements IfingerprintReader {
    private static final String TAG = "ProxyFingerprintReader";
    private IfingerprintReader ifingerprintReader;
    private static ProxyFingerprintReader proxyFingerprintReader = null;

    private ProxyFingerprintReader(IfingerprintReader ifingerprintReader) {
        this.ifingerprintReader = ifingerprintReader;
    }

    public synchronized static ProxyFingerprintReader getInstance(IfingerprintReader ifingerprintReader) {
        if(proxyFingerprintReader==null){
            MyLog.e(TAG,"proxyFingerprintReader==null");
            proxyFingerprintReader = new ProxyFingerprintReader(ifingerprintReader);
        }else {
            MyLog.e(TAG,"proxyFingerprintReader!=null");
        }
        return proxyFingerprintReader;
    }

    @Override
    public void open() {
        this.ifingerprintReader.open();
    }

    @Override
    public void read() {
        this.ifingerprintReader.read();
    }

    @Override
    public int close() {
        return this.ifingerprintReader.close();
    }

    @Override
    public void setOnFingerprintReadCallback(OnFingerprintReadCallback onFingerprintReadCallback) {
        this.ifingerprintReader.setOnFingerprintReadCallback(onFingerprintReadCallback);
    }

    @Override
    public void setOnFingerprintOpenCallback(OnFingerprintOpenCallback onFingerprintOpenCallback) {
        this.ifingerprintReader.setOnFingerprintOpenCallback(onFingerprintOpenCallback);
    }
}
