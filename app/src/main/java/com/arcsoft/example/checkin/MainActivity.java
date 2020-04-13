package com.arcsoft.example.checkin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.renderscript.Script;
/*import android.support.v7.app.AppCompatActivity;*/
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*import androidx.appcompat.app.AppCompatActivity;*/

import androidx.appcompat.app.AppCompatActivity;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.activity.ChooseFunctionActivity;
import com.arcsoft.arcfacedemo.activity.RegisterAndRecognizeActivity;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    //button1 是员工信息信息录入
    //button2 是打卡
    private Button button1,button2;//
    Toast toast=null;
    //系统时间
    TextView mainTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }*/

        button2 = (Button)findViewById(R.id.btn2);
        button1 = (Button)findViewById(R.id.btn1);

        button1.setOnClickListener(new ButtonListener1());
        button2.setOnClickListener(new ButtonListener2());

        //时间的线程

        mainTv = findViewById(R.id.maintime);
        new MainActivity.TimeThread().start();//启动线程


    }

//信息录入页面
    class ButtonListener1 implements View.OnClickListener {
        public void onClick(View v){



           final EditText inputServer = new EditText(MainActivity.this);
           //设置下划线不显示   0完全透明  255不透明
           // inputServer.setBackgroundColor(Color.argb(1,79,79,79));
           // inputServer.setPadding(20,0,0,0);
                inputServer.setTextSize(30);
            //设置为密码类型
            inputServer.setTypeface(Typeface.DEFAULT);
            inputServer.setTransformationMethod(new PasswordTransformationMethod());
            //明文显示密码
            //edittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            //隐藏密码
            inputServer.setTransformationMethod(PasswordTransformationMethod.getInstance());
            final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AlertDialogCustom));

            //重置密码弹窗
            builder.setNeutralButton("重置密码", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println("进入修改密码弹框");
                    LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                    final View textEntryView = factory.inflate(R.layout.dialog, null);

                    final EditText code = (EditText) textEntryView.findViewById(R.id.editTextName);
                    final EditText repassword = (EditText) textEntryView.findViewById(R.id.editTextNum);
                    final AlertDialog.Builder ad1 = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AlertDialogCustom));
                   // ad1.setTitle("重置密码");
                    ad1.setIcon(android.R.drawable.ic_dialog_info);
                    ad1.setView(textEntryView);
                    ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {
                            Log.i("重置密码", code.getText().toString());
                            if("123".equals(code.getText().toString())){
                               //获取信密码的值,存储到本地
                                String repwd=repassword.getText().toString();
                                //判断密码是否正确
                                SharedPreferences sharedPreferences;
                                SharedPreferences.Editor editor;
                                //创建一个新的sh保存数据
                                SharedPreferences sPreferences=getSharedPreferences("config", MODE_PRIVATE);
                                //创建一个新的sh的edit来写数据
                                editor = sPreferences.edit();
                                editor.putString("pwd",repwd);
                                editor.commit();
                                System.out.println("editor::::::::::::"+editor);

                               //Toast.makeText(getApplicationContext(),"修改成功", Toast.LENGTH_SHORT).show();
                                Toast toast= Toast.makeText(getApplicationContext(),"修改成功", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout layout = (LinearLayout) toast.getView();
                                TextView tv = (TextView) layout.getChildAt(0);
                                tv.setTextSize(40);
                                toast.show();


                                System.out.println("安全码："+code.getText().toString());
                                System.out.println("新密码："+repassword.getText().toString());
                                //关闭窗口
                                try {
                                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    field.set(dialog, true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();

                            }
                                else {
                                if (toast==null){
                                    System.out.println("安全码错误！");
                                    toast = Toast.makeText(getApplicationContext(), "安全码错误！", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    LinearLayout layout = (LinearLayout) toast.getView();
                                    TextView tv = (TextView) layout.getChildAt(0);
                                    tv.setTextSize(40);
                                }else {
                                    toast.setText("安全码错误！");
                                }
                                toast.show();
                                try {
                                    //不关闭窗口
                                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    field.set(dialog, false);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {
                            //关闭窗口
                            try {
                                Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                field.setAccessible(true);
                                field.set(dialog, true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();

                        }
                    });
                    ad1.show();// 显示对话框

                }
            }).setTitle("请输入密码").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


            //密码输入弹窗
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String text = inputServer.getText().toString();
                    System.out.println(text);
                    //判断密码是否正确
                    SharedPreferences model=getSharedPreferences("config", MODE_PRIVATE);
                    String pwd=model.getString("pwd","");
                    System.out.println("从文件获取的密码是：：：：：：：：：：：：："+pwd);

                    if (pwd.equals("")){

                        if("111".equals(text)) {
                            // Toast.makeText(getApplicationContext(),"输入成功",Toast.LENGTH_SHORT).show();
                            System.out.println("进入录入信息");
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, IndexActivity.class);
                            MainActivity.this.startActivity(intent);
                        } else{
                            Toast toast= Toast.makeText(getApplicationContext(),"输入错误", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            LinearLayout layout = (LinearLayout) toast.getView();
                            TextView tv = (TextView) layout.getChildAt(0);
                            tv.setTextSize(40);
                            toast.show();
                        }

                    }
                    else {
                        if (pwd.equals(text)){
                            // Toast.makeText(getApplicationContext(),"输入成功",Toast.LENGTH_SHORT).show();
                            System.out.println("进入录入信息");
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, IndexActivity.class);
                            MainActivity.this.startActivity(intent);
                        }else{
                            Toast toast= Toast.makeText(getApplicationContext(),"输入错误", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            LinearLayout layout = (LinearLayout) toast.getView();
                            TextView tv = (TextView) layout.getChildAt(0);
                            tv.setTextSize(40);
                            toast.show();
                        }

                            // return;

                    }

                }
            });

            builder.show();

        }
    }
    //打卡页面
    class ButtonListener2 implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent();
            //RegisterAndRecognizeActivity
            //ChooseFunctionActivity.class
            intent.setClass(MainActivity.this, RegisterAndRecognizeActivity.class);
             MainActivity.this.startActivity(intent);
        }

    }

    /*获取系统时间*/
    public  class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do{
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (true);
        }
    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    mainTv.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())));
                    break;
            }
            return false;
        }
    });



}
