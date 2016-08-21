package com.timaimee.retrofitdownfilewithprogress;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.timaimee.retrofitdownfilewithprogress.LogAdapter.CustomAndroidLogAdapter;
import com.timaimee.retrofitdownfilewithprogress.http.HttpDownUtil;
import com.timaimee.retrofitdownfilewithprogress.progress.ProgressListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Subscriber;


public class MainActivity extends ActionBarActivity {
    final static String TAG = MainActivity.class.getSimpleName();
    private TextView textView;
    public final static String dirPath = Environment.getExternalStorageDirectory() + File.separator + "downfile"
            + File.separator;
    private final static String filepath = dirPath + File.separator + "file.apk";
    private final static String URL = "http://shouji.360tpcdn.com/160819/36dd9def9a9b0667adc26bac01a77b12/com.netease.cloudmusic_77.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.progress);
        initLog();
        initFile();
        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downFile();
            }
        });
    }

    private void initLog() {
        Logger.init("Test")       //全局Tag  .init("applicationName")
                .methodCount(0)                 // 方法条数的显示 默认 2
                .hideThreadInfo()               // 线程信息的显示 默认 显示
                .logLevel(LogLevel.FULL)        // 调试/发布 Loglevel.FULL/Loglevel.NONE 默认 Loglevel.FULL
                .methodOffset(0)
                .logAdapter(new CustomAndroidLogAdapter());
    }

    private void initFile() {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdir();
        }
    }


    private void downFile() {

        final ProgressListener progressListener = new ProgressListener() {
            @Override
            public void update(final long bytesRead, final long contentLength, boolean done) {
                Logger.t(TAG).d(" %d%% done\n", (100 * bytesRead) / contentLength);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText((100 * bytesRead) / contentLength + "%" + "(" + bytesRead + "/" + contentLength + ")");
                    }
                });
            }
        };

        final File file = new File(filepath);
        HttpDownUtil.getInstance().down(URL, progressListener, new Subscriber<Response<ResponseBody>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

                for (int i = 0; i < e.getStackTrace().length; i++) {

                    Logger.t(TAG).e("down onNext err=" + e.getStackTrace()[i].toString());
                }
            }

            @Override
            public void onNext(Response<ResponseBody> responseBody) {
                Logger.t(TAG).d("down onNext");
                if (responseBody.isSuccess()) {


                    boolean writeSuccess = writeResponseBodyToDisk(responseBody.body(), file);
                    if (writeSuccess) {
                        Logger.t(TAG).d("writesucess");
                    }
                }
            }
        });

    }


    private boolean writeResponseBodyToDisk(ResponseBody body, File file) {
        try {
            // todo change the file location/name according to your needs

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Logger.t(TAG).d("file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


}

