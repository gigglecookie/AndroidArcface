package com.arcsoft.example.checkin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
/*import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;*/
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
/*

import androidx.appcompat.app.AppCompatActivity;

import com.example.checkin.testapplication.CameraSurfaceView;
*/

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.example.checkin.testapplication.CameraSurfaceView;
import com.arcsoft.example.checkin.testapplication.Registered;
import com.arcsoft.example.domain.User;
import com.arcsoft.example.httpUntil.HttpUtilsUser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.layout.two_line_list_item;

public class IndexActivity extends AppCompatActivity {
    Intent intent = null;
    TextView mainTv;
    String postMessage;
    /*相机*/
    private Button button;
    private CameraSurfaceView mCameraSurfaceView;
    //private RectOnCamera rectOnCamera;
    /*获取员工信息存储*/
    private Button btnSave, btnReset;//保存按钮,撤销按钮

    SharedPreferences preferences = null;
    SharedPreferences.Editor editor = null;

    //重写拍照完成后index页面的返回键
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("点击返回键了");
        //重写返回键返回到首页
        //if (intent.getStringExtra("path") == null) {
        Intent intent = new Intent(IndexActivity.this, MainActivity.class);
        //nv21转化成bi't
        //Bitmap bmp=nv21ToBitmap(nv21s,w,h);
        startActivity(intent);
        //}
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        //拍照后保留edittext内容
        preferences = getPreferences(MODE_PRIVATE);
        editor = preferences.edit();

        /*调用相机*/
    /*    mCameraSurfaceView =  findViewById(R.id.cameraSurfaceView);
        button =  findViewById(R.id.takePic);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraSurfaceView.takePicture();
            }
        });*/


        //添加部门
        //获取界面布局文件的Spinner组件
        Spinner spinner = (Spinner) findViewById(R.id.department);
        String[] arr = {"请选择部门", "技术部", "售后部", "财务部", "销售部", "市场部"};
        //创建ArrayAdapter对象
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(IndexActivity.this, R.layout.department_item, arr);
        spinner.setAdapter(adapter);
        /*获取部门参数*/
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String str = parent.getItemAtPosition(position).toString();
                Toast.makeText(IndexActivity.this, "你点击的是:" + str, 2000).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        //重置  保存
        btnSave = (Button) findViewById(R.id.save);
        btnReset = (Button) findViewById(R.id.reset);

        btnSave.setOnClickListener(new BtnSaveListener());
        btnReset.setOnClickListener(new BtnResetListener());
        //去拍照
        ImageButton b = (ImageButton) findViewById(R.id.camera);
        b.setOnClickListener(new BtnRegListener());
        //时间的线程
        mainTv = findViewById(R.id.time);
        new IndexActivity.TimeThread().start();//启动线程

        //判断intent返回来之后是否有数据  并把照片显示到页面
        intent = getIntent();
        if (intent != null) {
            intent = getIntent();
            String path = intent.getStringExtra("path");
            System.out.println("获取的路径是：：：：：：：：：：：：：：：：：" + path);//获取的路径是/storage/emulated/0/arcface/register/imgsregistered 627
            //图片显示
            ImageView img;
            //SD图片路径
            String filepath = path + ".jpg";
            img = (ImageView) findViewById(R.id.cameraImage);
            File file = new File(filepath);
            System.out.println(file.getName());
            if (file.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(filepath);
                //将图片显示到ImageView中
                img.setImageBitmap(bm);

                //显示保存的值
                EditText userId = findViewById(R.id.id);
                EditText userName = findViewById(R.id.name);
                userId.setText(preferences.getString("id", ""));
                userName.setText(preferences.getString("name", ""));
                //性别显示
                //性别
                RadioGroup sexRadioGroup = findViewById(R.id.sex);
                int selectedId = sexRadioGroup.getCheckedRadioButtonId();
                String sexStr = preferences.getString("sex", "男");
                System.out.println(sexStr);
                if (sexStr.equals("男")) {
                    sexRadioGroup.check(R.id.nan);
                }
                sexRadioGroup.check(R.id.nv);
            }

        }

    }

    /*去拍照*/
    class BtnRegListener implements View.OnClickListener {
        @SuppressLint("WrongConstant")
        public void onClick(View v) {

            //拍照之前查看是否已经输入信息
            System.out.println("点击去拍照了}}}}}}}}}}}}}}}}}}}}");
            EditText userId = findViewById(R.id.id);
            EditText userName = findViewById(R.id.name);
            //性别存储
            RadioGroup sexRadioGroup = findViewById(R.id.sex);
            int selectedId = sexRadioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = findViewById(selectedId);
            String sexStr = (String) radioButton.getText();
            System.out.println(sexStr);
            System.out.println(selectedId);
            if (userId.getText().toString() != null || userName.getText().toString() != null || sexStr != null) {
                editor.putString("id", userId.getText().toString());
                editor.putString("name", userName.getText().toString());
                editor.putString("sex", sexStr);
                editor.commit();
            }
//去拍照
            Intent intent = new Intent(IndexActivity.this, Registered.class);
            //nv21转化成bi't
            //Bitmap bmp=nv21ToBitmap(nv21s,w,h);
            startActivity(intent);

        }
    }


    /*保存*/
    Toast toast = null;

    class BtnSaveListener implements View.OnClickListener {
        @SuppressLint("WrongConstant")
        public void onClick(View v) {
            //获取拍照的图片
            EditText userId = findViewById(R.id.id);
            EditText userName = findViewById(R.id.name);
            RadioGroup sexRadioGroup = findViewById(R.id.sex);
            TextView enteringTime = findViewById(R.id.time);
            int selectedId = sexRadioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = findViewById(selectedId);

            //判断是否拍照
            if (intent.getStringExtra("path") == null) {
                if (toast == null) {
                    System.out.println("请拍照");
                    toast = Toast.makeText(getApplicationContext(), "请拍照", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    LinearLayout layout = (LinearLayout) toast.getView();
                    TextView tv = (TextView) layout.getChildAt(0);
                    tv.setTextSize(40);
                } else {
                    toast.setText("请拍照");
                }
                toast.show();
                return;
            } else {
                //信息填写
                if (userId.length() == 0 || userName.length() == 0) {
                    if (toast == null) {
                        System.out.println("请填写完整信息");
                        toast = Toast.makeText(getApplicationContext(), "请填写完整信息", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        LinearLayout layout = (LinearLayout) toast.getView();
                        TextView tv = (TextView) layout.getChildAt(0);
                        tv.setTextSize(40);
                    } else {
                        toast.setText("请填写完整信息");
                    }
                    toast.show();
                    return;
                }
                //如果信息填写完整
                //获取工号年龄和性别，照片 存储
                String id = userId.getText().toString();
                String name = userName.getText().toString();
                String sex = (String) radioButton.getText();
                System.out.println("id:" + id);
                System.out.println("usersex" + name);
                System.out.println("sex" + sex);
                //获取特征值文件
                String featurefileName = intent.getStringExtra("features");
                System.out.println("人脸的特征值文件名" + featurefileName);
                //对特征值和信息的存储到本地文件
                final User user = new User();
                user.setUserId(id);
                user.setUserName(name);
                user.setUserSex(sex);
                user.setUserFeature(featurefileName);
                //把文件写到流的里面，保存对象
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream os = new ObjectOutputStream(baos);
                    os.writeObject(user);//把对象写到流的里面
                    String output = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
                    baos.close();
                    os.close();
                    SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("userInfo", output);
                    editor.commit();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //获取信息的录入时间
                String entTime = enteringTime.getText().toString();
                java.text.SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                try {
                    Date date = formatter.parse(entTime);
                    System.out.println("系统录入时间是" + entTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //发送请求
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Map<String, String> params = new HashMap<String, String>();
                            //用户id
                            params.put("username", user.getUserId());
                            //用户姓名
                            params.put("name", user.getUserName());
                            URL url = new URL("http://192.168.2.107:8080/user");
                            postMessage = HttpUtilsUser.sendPostMessage(params, "utf-8", url);
                            System.out.println("postMessage->" + postMessage);
                            //String转JSONObject
                            JSONObject result = new JSONObject(postMessage);
                            //获取返回的状态值
                            Integer data = (Integer) result.get("data");
                            if (data != null && data == 1) {
                                System.out.println(data + "::::::::::::注册失败！账号已经被注册");
                                toa("账号已经被注册!");
                                //删除文件
                                Boolean lt = deleteFile(intent.getStringExtra("features"));
                                Boolean lt2 = deleteFile(intent.getStringExtra("path") + ".jpg");
                                System.out.println("人脸照片" + intent.getStringExtra("path") + ".jpg");
                                System.out.println("特征值删除结果：" + lt + "：：：：：：：：图片删除结果：" + lt2);
                                return;
                            } else if (data != null && data == 2) {
                                System.out.println(data + "::::::::::::账号注册成功");
                                toa("注册成功!");
                                try {
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    ObjectOutputStream os = new ObjectOutputStream(baos);
                                    os.writeObject(user);//把对象写到流的里面
                                    String output = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
                                    baos.close();
                                    os.close();
                                    SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("userInfo", output);
                                    editor.commit();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //清除缓存
                                intent.removeExtra("path");


                            } else if (data != 1 && data != 2) {
                                System.out.println(data + "注册失败！位置错误");
                                toa("未知错误注册失败！请重试！");
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                 toa("保存成功!");
                //点击保存后输入框和照片清除页面数据
                userId.setText("");
                userName.setText("");
                //清除缓存
                //intent.removeExtra("path");
                //设置ImageView的图片为空
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pro, null);
                ImageView imageView = findViewById(R.id.cameraImage);
                //setBackgroundDrawable
                imageView.setImageDrawable(drawable);

                intent.setFlags(PendingIntent.FLAG_UPDATE_CURRENT);
            }
        }
    }

    //注册失败删除提取的特征值文件
    public boolean deleteFile(String fpath) {
        Boolean flag = false;
        File file = new File(fpath);
        System.out.println(fpath);
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;

        }
        return flag;
    }

    /*弹窗*/
    public void toa(String saveresult) {
        final String result = saveresult;
        new Thread() {
            public void run() {
                IndexActivity.this.runOnUiThread(new Runnable() {
                    Toast toast = null;

                    public void run() {
                        System.out.println("保存的信息：：：：：" + result);
                        toast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        LinearLayout layout = (LinearLayout) toast.getView();
                        TextView tv = (TextView) layout.getChildAt(0);
                        tv.setTextSize(40);
                        toast.show();
                    }
                });
            }
        }.start();

    }

    /*重置*/
    class BtnResetListener implements View.OnClickListener {
        public void onClick(View v) {
            EditText userId = findViewById(R.id.id);
            EditText userName = findViewById(R.id.name);
            userId.setText("");
            userName.setText("");
            System.out.println("点击重置了");
            System.out.println("点击");
        }
    }

    /*获取系统时间*/
    public class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mainTv.setText(new SimpleDateFormat("YY/MM/dd/HH:mm:ss").format(new Date(System.currentTimeMillis())));
                    break;
            }
            return false;
        }
    });

}
