package com.wd.jsbragedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;

public class MainActivity extends AppCompatActivity {

    /**
     * 给js发送数据并获取反馈结果
     */
    private Button defaultBt;
    /**
     * 给js指定方法发送数据并获取反馈结果
     */
    private Button specialBt;
    private BridgeWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        //给js发送数据并获取反馈结果
        defaultBt = (Button) findViewById(R.id.default_bt);
        //给js指定方法发送数据并获取反馈结果
        specialBt = (Button) findViewById(R.id.special_bt);
        //BridgeWebView
        mWebView = (BridgeWebView) findViewById(R.id.webview);

        OnButtonClick onButtonClick = new OnButtonClick();
        defaultBt.setOnClickListener(onButtonClick);
        specialBt.setOnClickListener(onButtonClick);

        // 加载Assert下的HTML文件
        mWebView.loadUrl("file:////android_asset/jsbrage.html");


        // 注册js可以调用的方法， 一种是默认接收，一种是通过第一个参数指定接收
        //默认接收
        mWebView.setDefaultHandler(new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String msg = "默认接收到js的数据：" + data;
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();

                function.onCallBack("java默认接收数据，并回传数据给js"); //回传数据给js
            }
        });
        //指定接收 submitFromWeb 与js保持一致
        mWebView.registerHandler("callFromWeb", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String msg = "指定接收到js的数据：" + data;
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();

                function.onCallBack("java指定接收数据，并回传数据给js"); //回传数据给js
            }
        });
    }

    class OnButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view == defaultBt) {
                // 给js以默认的方式传数据，并获取返回值
                //默认接收
                mWebView.send("java发送数据给js数据， 对方使用默认接收", new CallBackFunction() {
                    @Override
                    public void onCallBack(String data) { //处理js回传的数据
                        Toast.makeText(MainActivity.this, data, Toast.LENGTH_LONG).show();
                    }
                });
            }
            else if (view == specialBt) {
                // js注册了functionInJs， 然后Java指定它来接收这个数据
                mWebView.callHandler("functionInJs", "发送数据给js指定接收", new CallBackFunction() {
                    @Override
                    public void onCallBack(String data) { //处理js回传的数据
                        Toast.makeText(MainActivity.this, data, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }


    }
}
