package org.example.roadischosen.lab2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    Handler handler;
    static final int UPDATE_STAT  = 0;
    static final int UPDATE_GRAPH = 1;
    static int[] array;
    static int[] element_counts = new int[] {1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000};
    private LineGraphSeries<DataPoint> theoretical;
    private LineGraphSeries<DataPoint> practical;
    ExportableGraphView graph;
    Thread thread;
    static {
        System.loadLibrary("time_check");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_STAT:
                        ((TextView) findViewById(R.id.status_text)).setText((String) msg.obj);
                        break;
                    case UPDATE_GRAPH:
                        DataPoint current = (DataPoint) msg.obj;
                        double x = current.getX();
                        double y = x * x / 200.;
                        Log.d("TEST", Double.toString(x));
                        theoretical.appendData(new DataPoint(x, y), false, 100);
                        practical.appendData(new DataPoint(x, current.getY()), false, 100);
                        graph.getViewport().setMaxX(x);
                        graph.getViewport().setMaxY(y);
                        break;
                }
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareGraph();
            }
        });
    }

    private void shareGraph() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/png");
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/graph.png"));
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(share);
    }


    public void buttonsClick(final View view) {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    configureGraph();
                    processArrays();
                    saveGraph();
                }
            });
            thread.start();
        }
    }

    private void configureGraph() {
        theoretical = new LineGraphSeries<>();
        practical = new LineGraphSeries<>();
        theoretical.setColor(Color.GREEN);
        theoretical.setTitle(getString(R.string.theoretical));
        practical.setTitle(getString(R.string.practical));
        practical.setColor(Color.RED);
        theoretical.setDrawDataPoints(true);
        theoretical.setDataPointsRadius(8);
        practical.setDrawDataPoints(true);
        practical.setDataPointsRadius(8);
        theoretical.setThickness(3);
        graph = (ExportableGraphView) findViewById(R.id.graph);
        graph.removeAllSeries();
        graph.addSeries(practical);
        graph.addSeries(theoretical);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setFixedPosition(10, 10);
    }

    private void saveGraph() {
        Bitmap bitmap = Bitmap.createBitmap(graph.getWidth(), graph.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        graph.draw(canvas);

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File picFile = new File(Environment.getExternalStorageDirectory() + "/graph.png");

            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                picFile.createNewFile();
                FileOutputStream picOut = new FileOutputStream(picFile);
                picOut.write(bytes.toByteArray());
                picOut.flush();
                picOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processArrays() {
        for (int i = 0; i < 10; i++) {
            int count = element_counts[i];
            array = new int[count];
            sendMessage(getString(R.string.generating, count));
            for (int j = 0; j < count; j++)
                array[j] = ThreadLocalRandom.current().nextInt(-100, 100);
            sendMessage(getString(R.string.sorting, count));
            sendPoint(count, ArraySort(array));
        }
        sendMessage(getString(R.string.done));
    }

    private void sendMessage(String str) {
        Message msg = handler.obtainMessage();
        msg.obj = str;
        msg.what = UPDATE_STAT;
        handler.sendMessage(msg);
    }

    private void sendPoint(double x, double y) {
        Message msg = handler.obtainMessage();
        msg.obj = new DataPoint(x, y);
        msg.what = UPDATE_GRAPH;
        handler.sendMessage(msg);
    }

    public native long ArraySort(int[] arr);
}
