1.导入依赖
     allprojects {
         repositories {
               google()
               jcenter()
        
               maven { url "https://jitpack.io" }
         }
     }
    
    
    dependencies {
       //依赖
       implementation 'com.github.lzyzsd:jsbridge:1.0.4'  
    }
    
 2.本地Html (在src/assets/jsbrage.html)
 
 <html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>

    <script>
       <!--注册事件监听，初始化-->
       function setupWebViewJavascriptBridge(callback) {
           if (window.WebViewJavascriptBridge) {
               callback(WebViewJavascriptBridge)
           } else {
               document.addEventListener(
                   'WebViewJavascriptBridgeReady'
                   , function() {
                       callback(WebViewJavascriptBridge)
                   },
                   false
               );
           }
       }

       <!--回调函数，接收java发送来的数据-->
       setupWebViewJavascriptBridge(function(bridge) {
           //默认接收
           bridge.init(function(message, responseCallback) {
               document.getElementById("show").innerHTML = '默认接收到Java的数据： ' + message;

               var responseData = 'js默认接收完毕，并回传数据给java';
               responseCallback(responseData); //回传数据给java
           });

           <!--指定接收，参数functionInJs 与java保持一致-->
           bridge.registerHandler("functionInJs", function(data, responseCallback) {
               document.getElementById("show").innerHTML = '指定接收到Java的数据： ' + data;
               var responseData = 'js指定接收完毕，并回传数据给java';
               responseCallback(responseData);  <!--回传数据给java-->
           });
       })

       <!--js传递数据给java-->
       function jsCallJavaDefault() {
           var data = '发送数据给java默认接收';
           window.WebViewJavascriptBridge.send(data, function(responseData) {
                  document.getElementById("show").innerHTML = responseData;
           });
        }

       function jsCallJavaSpec() {
           var data='发送数据给java指定接收';
           <!--指定接收参数 submitFromWeb与java一致, function(responseData)处理java回传的数据-->
           window.WebViewJavascriptBridge.callHandler('callFromWeb',data,function(responseData) {
                  document.getElementById("show").innerHTML = responseData;
           });
       }
    </script>

</head>
<body>
<div>
    <button onClick="jsCallJavaDefault()">js调用java，Java使用默认方式接收</button>
</div>
<br/>
<div>
    <button onClick="jsCallJavaSpec()">js发送数据给java指定其某个方法接收</button>
</div>
<br/>
<div id="show">打印信息</div>
</body>
</html>
 
 
 3.逻辑实现
 
    1>.布局
        <?xml version="1.0" encoding="utf-8"?>
        <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:app="http://schemas.android.com/apk/res-auto"
            xmlns:tools="http://schemas.android.com/tools"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            tools:context=".MainActivity"
            android:orientation="vertical"
            >

            <Button
                android:id="@+id/default_bt"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="给js发送数据并获取反馈结果"
                android:textColor="#000"
                android:textSize="20dp"
                android:gravity="center"
                android:layout_margin="20dp"
                />

            <Button
                android:id="@+id/special_bt"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="给js指定方法发送数据并获取反馈结果"
                android:textColor="#000"
                android:textSize="20dp"
                android:gravity="center"
                android:layout_margin="20dp"
                />

            <com.github.lzyzsd.jsbridge.BridgeWebView
                android:id="@+id/webview"
                android:layout_width="match_parent"
                android:layout_height="300dp" />


        </LinearLayout>

    2>.逻辑
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

   
 
