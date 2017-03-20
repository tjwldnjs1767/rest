package rest.rest.correctPostureNoti.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import android.media.FaceDetector;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import rest.rest.R;
import rest.rest.notification.activity.MakeNotificationActivity;
import rest.rest.correctPostureNoti.activity.PostureNotiActivity;

public class FaceDetectionService extends Service {

    private final String TAG = "rest";
    private final Handler handler = new Handler();
    private String imageStoredPath = null;
    private String imageName;
    private SurfaceHolder holder;
    private Camera camera;
    private Parameters parameters;
    private SurfaceView surfaceView;

    static String imagePath;
    Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

        PostureNotiActivity notiControl = new PostureNotiActivity(this);
        startForeground(1, notiControl.getNoti());

        int cameraId = findFrontSideCamera();

        camera = Camera.open(cameraId);

        surfaceView = new SurfaceView(getApplicationContext());

        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    camera.setPreviewDisplay(surfaceView.getHolder());
                    parameters = camera.getParameters();

                    camera.setParameters(parameters);
                    camera.startPreview();
                    camera.takePicture(null, null, call);

                } catch (IOException e) {
                    Log.d(TAG, "error1 " + e.getMessage());
                }

                holder = surfaceView.getHolder();
                holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

                handler.postDelayed(this, 15000);
            }
        };

        handler.postDelayed(runnable, 100);


        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        camera.release();
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    Camera.PictureCallback call = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            FileOutputStream fos;
            File pictureFile = getOutputMediaFile();

            if (pictureFile == null) {
                return;
            }

            imageStoredPath = pictureFile.getAbsolutePath();


            try {
                fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

                imageName = pictureFile.getName();
                Toast.makeText(getApplicationContext(), "Picture saved: " + imageName, Toast.LENGTH_LONG).show();


                detectFace();
                deleteImageFile();

            } catch (FileNotFoundException e) {
                Log.e(TAG, "error2 " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "error3 " + e.getMessage());
            }
        }
    };

    private void detectFace() {

        File imageFile = new File(imageStoredPath);
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        Bitmap bitmap;
        int max = 5, width, height, faceCnt;

        bfo.inPreferredConfig = Bitmap.Config.RGB_565;
        bfo.inScaled = false;
        bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bfo);


        width = bitmap.getWidth();
        height = bitmap.getHeight();
        FaceDetector fd = new FaceDetector(width, height, max);
        FaceDetector.Face[] faces = new android.media.FaceDetector.Face[max];
        faceCnt = fd.findFaces(bitmap, faces);

        for (int i = 0; i < faceCnt; i++) {
            Log.d("TAG", "eyesDistance = " + Float.toString(faces[i].eyesDistance()));
            if (faces[i].eyesDistance() > 46) // TODO: 2017-02-07 눈 사이 거리 
                makeNotification();
            else
                Toast.makeText(getApplicationContext(), "양호합니다", Toast.LENGTH_LONG).show();
        }

        }

    private int findFrontSideCamera() {

        int cameraId = -1;
        int numOfCameras = Camera.getNumberOfCameras();

        for (int i = 0; i < numOfCameras; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);

            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = 1;
                break;
            }
        }

        return cameraId;
    }

    private static File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "CameraBasicImages");
        File mediaFile;
        String timeStamp;

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs())
                return null;
        }

        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        imagePath = mediaStorageDir.getPath();

        return mediaFile;
    }

    private void deleteImageFile() {
        try {
            File file = new File(imagePath);
            File[] fileList = file.listFiles();

            for (int i = 0; i < fileList.length; i++) {
                String fileName = fileList[i].getName();
                if (fileName.equals(imageName))
                    fileList[i].delete();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "파일 삭제 실패 ", Toast.LENGTH_SHORT).show();
        }
    }

    public void makeNotification() {

        Resources res = getResources();

        Intent notificationIntent = new Intent();
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cn = new ComponentName(this, MakeNotificationActivity.class);
        notificationIntent.setComponent(cn);
        notificationIntent.putExtra("notificationId", 9999);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle("화면과 멀리 떨어지세요!")
                .setContentText("가까운 화면 응시는 시력저하를 발생시킵니다.")
                .setTicker("멀리떨어지세요!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1234, builder.build());
    }
}