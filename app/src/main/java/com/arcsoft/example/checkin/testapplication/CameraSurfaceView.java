package com.arcsoft.example.checkin.testapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.Image;
import android.os.Environment;
/*import android.support.v4.app.ActivityCompat;*/
import android.util.AttributeSet;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.arcsoft.example.checkin.MainActivity;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/*import androidx.core.app.ActivityCompat;

import com.arcsoft.example.checkin.MainActivity;
import com.example.checkin.MainActivity;*/

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback {


     private static final String TAG = "CameraSurfaceView";
public static List<String> imagelist=new ArrayList<>();
    private Context mContext;
    private SurfaceHolder holder;
    private Camera mCamera;

    private int mScreenWidth;
    private int mScreenHeight;
    private CameraTopRectView topView;

    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getScreenMetrix(context);
        topView = new CameraTopRectView(context,attrs);
        initView();
    }
    //拿到手机屏幕大小
    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        /*设置相机预览区域*/
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels/2;

    }

    private void initView() {
        holder = getHolder();//获得surfaceHolder引用
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        if (mCamera == null) {

            mCamera = Camera.open();//开启相机

            //后置
           /* try {
                mCamera.setPreviewDisplay(holder);//摄像头画面显示在Surface上
            } catch (IOException e) {
                e.printStackTrace();
            }*/
           //前置
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();
            for(int camIdx = 0;camIdx<cameraCount;camIdx++){
                Camera.getCameraInfo(camIdx,cameraInfo);
                if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                    mCamera = Camera.open(camIdx);



                    try {
                        mCamera.setPreviewDisplay(holder);//摄像头画面显示在Surface上
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");

        setCameraParams(mCamera, mScreenWidth, mScreenHeight);
        mCamera.startPreview();
//        mCamera.takePicture(null, null, jpeg);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        mCamera.stopPreview();//停止预览
        mCamera.release();//释放相机资源
        mCamera = null;
        holder = null;
    }

    @Override
    public void onAutoFocus(boolean success, Camera Camera) {
        if (success) {
            Log.i(TAG, "onAutoFocus success="+success);
            System.out.println(success);
        }
    }
    private void setCameraParams(Camera camera, int width, int height) {
        Log.i(TAG,"setCameraParams  width="+width+"  height="+height);
        Camera.Parameters parameters = mCamera.getParameters();
        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizeList) {
            Log.i(TAG, "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
        if (null == picSize) {
            Log.i(TAG, "null == picSize");
            picSize = parameters.getPictureSize();
        }
        Log.i(TAG, "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
        // 根据选出的PictureSize重新设置SurfaceView大小
        float w = picSize.width;
        float h = picSize.height;
        parameters.setPictureSize(picSize.width,picSize.height);
        this.setLayoutParams(new FrameLayout.LayoutParams((int) (height*(h/w)), height));

        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

        for (Camera.Size size : previewSizeList) {
            Log.i(TAG, "previewSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
        if (null != preSize) {
            Log.i(TAG, "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }

        mCamera.cancelAutoFocus();//自动对焦。
        mCamera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        mCamera.setParameters(parameters);

    }

    /**
     * 从列表中选取合适的分辨率
     * 默认w:h = 4:3
     * <p>注意：这里的w对应屏幕的height
     *            h对应屏幕的width<p/>
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Log.i(TAG, "screenRatio=" + screenRatio);
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }

        if (null == result) {
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 4f / 3) {// 默认w:h = 4:3
                    result = size;
                    break;
                }
            }
        }
        return result;
    }

    // 拍照瞬间调用
    public Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            Log.i(TAG,"shutter");
            System.out.println("执行了吗+1");
        }
    };

    // 获得没有压缩过的图片数据
    public Camera.PictureCallback raw = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {
            Log.i(TAG, "raw");
            System.out.println("执行了吗+2");
        }
    };

    //创建jpeg图片回调数据对象
    public final Camera.PictureCallback jpeg = new Camera.PictureCallback() {

        private Bitmap bitmap;

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {


            topView.draw(new Canvas());

            BufferedOutputStream bos = null;
            Bitmap bm = null;
            if (data != null) {

            }

            try {
                // 获得图片
                bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                System.out.println("原始图片的宽度：：：：：：：：：：：："+ bm.getWidth());
                System.out.println("原始图片的宽度：：：：：：：：：：：：："+bm.getWidth());
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    System.out.println("System.currentTimeMillis()" + System.currentTimeMillis());
                    String filePath = "/storage/emulated/0/Mob/" + System.currentTimeMillis() + ".jpg";//照片保存路径
                    System.out.println("图片的存储路径：" + filePath);
//                    //图片存储前旋转
                    Matrix m = new Matrix();
                    int height = bm.getHeight();
                    int width = bm.getWidth();
                    m.setRotate(270);
                    //旋转后的图片
                    bitmap = Bitmap.createBitmap(bm, 0, 0, width, height, m, true);
                    System.out.println("旋转后的图片bitmap宽度：：：：：：：：：：：：：："+bitmap.getWidth());
                    System.out.println("旋转后的图片bitmap高度：：：：：：：：：：：：：："+bitmap.getHeight());
                    /*Bitmap格式的图片转换为base图片*/
                    String imageBase= bitmapToBase64(bitmap);

                    //测试在文件里面获取一张图片
                    String path = "/storage/emulated/0/Mob/";
                    String names = "";
                        File f = new File(path);
                        if (f.isDirectory())
                        {
                            File[] fList = f.listFiles();
                            for (int j = 0; j < fList.length; j++) {
                                File file = fList[j]; if (file.isFile()) {
                                    names = file.getName();
                                    System.out.println("usersex:::::::::::::::"+names);
                                    if(names.equals("12.jpg")){

                                        FaceFeature faceFeature=new FaceFeature();
                                        FaceInfo faceInfo=new FaceInfo();
                                        // 原始图像
                                        Bitmap originalBitmap = BitmapFactory.decodeFile("/storage/emulated/0/Mob/"+names);
                                        System.out.println();
                                        // 获取宽高符合要求的图像
                                        Bitmap bitmaps = ArcSoftImageUtil.getAlignedBitmap(originalBitmap, true);
                                        // 为图像数据分配内存
                                        byte[] bgr24={0};
                                        bgr24 = ArcSoftImageUtil.createImageData(bitmaps.getWidth(), bitmaps.getHeight(), ArcSoftImageFormat.BGR24);
                                        System.out.println("12.jpg W:::"+bitmaps.getWidth());
                                        System.out.println("12.jpg H:::"+bitmaps.getHeight());
                                        // 图像格式转换
                                        int transformCode = ArcSoftImageUtil.bitmapToImageData(bitmaps, bgr24, ArcSoftImageFormat.BGR24);
                                        System.out.println("transformCode::::::"+transformCode);
                                        System.out.println("bgr24length:::"+bgr24.length);
                                        System.out.println("长度"+bitmaps.getHeight());
                                        System.out.println("宽度"+bitmaps.getWidth());
                                        // 首先进行人脸检测
                                        FaceEngine faceEngine=new FaceEngine();
                                        List<FaceInfo> faceInfoList = new ArrayList<>();
                                        int codes = faceEngine.detectFaces(bgr24, bitmaps.getWidth(), bitmaps.getHeight(), FaceEngine.CP_PAF_BGR24, faceInfoList);
                                        System.out.println("人脸检测code：：：：：：：：："+codes);

                                        //特征值提取
                                      int code = extractFaceFeature(bgr24,bitmaps.getWidth(),bitmaps.getHeight(),FaceEngine.CP_PAF_BGR24,faceInfo,faceFeature);
                                        System.out.println("检测码是：：：：：：：：："+code);
                                    }
                                }
                            }
                        }
                                    //特征值提取
                    /*
                    *  for (int i = 0; i < files.length; i++)
                                        {
                                            byte[] bgr24={0};
                                            if(files[i].getName().replaceAll("(.jpg|.jpeg)+","").length() != files[i].getName().length())
                                            {
                                                System.out.println(files[i].getName());
                                                String imagename=files[i].getName();
                                                // 原始图像
                                                Bitmap originalBitmap = BitmapFactory.decodeFile("/storage/emulated/0/Mob/"+imagename);
                                                // 获取宽高符合要求的图像
                                                Bitmap bitmaps = ArcSoftImageUtil.getAlignedBitmap(originalBitmap, true);
                                                // 为图像数据分配内存
                                                bgr24 = ArcSoftImageUtil.createImageData(bitmaps.getWidth(), bitmaps.getHeight(), ArcSoftImageFormat.BGR24);
                                                // 图像格式转换
                                                int transformCode = ArcSoftImageUtil.bitmapToImageData(bitmaps, bgr24, ArcSoftImageFormat.BGR24);
                                                System.out.println("bgr24length:::"+bgr24.length);
                                                System.out.println("长度"+height);
                                                System.out.println("宽度"+width);
                                            }
                                            extractFaceFeature(bgr24,bitmap.getWidth(),bitmap.getHeight(),FaceEngine.CP_PAF_BGR24,faceInfo,faceFeature);
                                        }
                    *
                    *
                    *
                    * */
                    imagelist.add(imageBase);
                   // System.out.println("图片集合："+imagelist);
                   // System.out.println("图片的base64的值是"+imageBase);
                   // System.out.println("bitmap的值是:" + bitmap);
                    System.out.println("执行了吗+3");
                    File file = new File(filePath);
                    //获取照片存储的权限
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((MainActivity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                    if (!file.exists()) {
                        /*  if(false){*/
                        System.out.println(file.exists());
                        System.out.println(file.getName());
                        file.createNewFile();
                    }

                    bos = new BufferedOutputStream(new FileOutputStream(file));

//                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
//                            data.length);
                    Bitmap sizeBitmap = Bitmap.createScaledBitmap(bitmap,
                            topView.getViewWidth(), topView.getViewHeight(), true);
                    bm = Bitmap.createBitmap(sizeBitmap, topView.getRectLeft(),
                            topView.getRectTop(),
                            topView.getRectRight() - topView.getRectLeft(),
                            topView.getRectBottom() - topView.getRectTop());// 截取


                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中

                }/**/ else {
                    Toast.makeText(mContext, "没有检测到内存卡", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    bos.flush();//输出
                    bos.close();//关闭
                    bm.recycle();// 回收bitmap空间
                    mCamera.stopPreview();// 关闭预览
                    mCamera.startPreview();// 开启预览
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    /*bitmap格式转换为base64存储*/
    @SuppressLint("WrongThread")
    public String bitmapToBase64(Bitmap bitmap) {
        // 要返回的字符串
        String reslut = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                //todo 压缩只对保存有效果bitmap还是原来的大小
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                baos.flush();
                baos.close();
                // 转换为字节数组
                byte[] byteArray = baos.toByteArray();
                // 转换为字符串
                reslut = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return reslut;

    }




    public void takePicture(){
        //设置参数,并拍照
        setCameraParams(mCamera, mScreenWidth, mScreenHeight);
        // 当调用camera.takePiture方法后，camera关闭了预览，这时需要调用startPreview()来重新开启预览
        mCamera.takePicture(null,null, jpeg);
    }

//    public void setAutoFocus(){
//        mCamera.autoFocus(this);
//    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }




//3.7.6.1 extractFaceFeature（传入分离的图像信息数据方法）

    int extractFaceFeature(
            byte[] data,
            int width,
            int height,
            int format,
            FaceInfo faceInfo,
            FaceFeature feature
    ){
        System.out.println("进入人脸特征值提取");
// 首先进行人脸检测
        List<FaceInfo> faceInfoList = new ArrayList<>();
        FaceEngine faceEngine =new FaceEngine();
        int code = faceEngine.detectFaces(data, width, height, FaceEngine.CP_PAF_NV21, faceInfoList);
        int extractCode = 0;
// 这里取检测到的第一张人脸进行特征提取，也可以对人脸大小进行排序，取最大人脸检测，可根据实际应用场景进行选择。如需提取所有人脸的人脸特征数据，循环处理即可
        if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
            FaceFeature faceFeature = new FaceFeature();
            // 在FaceFeature的二进制数组中保存获取到的人脸特征数据
             extractCode = faceEngine.extractFaceFeature(data, width, height,FaceEngine.CP_PAF_BGR24, faceInfoList.get(0), faceFeature);
            if (extractCode == ErrorInfo.MOK){
                Log.i(TAG, "extract face feature success");
                System.out.println("人脸特征值检测成功");
            }else{
                Log.i(TAG, "extract face feature failed, code is : " + extractCode);
                System.out.println("人脸特征值检测失败");
            }
        }else {
            Log.i(TAG, "no face detected, code is : " + code);
        }
        return extractCode;

    }

}

