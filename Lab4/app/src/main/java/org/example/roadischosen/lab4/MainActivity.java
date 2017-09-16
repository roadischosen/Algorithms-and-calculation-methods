package org.example.roadischosen.lab4;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MainActivity extends AppCompatActivity {
    private Handler handler;
    private GraphView graph;
    private LineGraphSeries<DataPoint> curve;
    private Thread thread;
    private double a;
    private double b;
    private double eps;
    private final int MESSAGE_POINT = 0;
    private final int MESSAGE_VALUE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String equation = getString(R.string.equation);
                String value = (String) ((TextView) findViewById(R.id.result)).getText();
                String text = getString(R.string.sms, equation, value, a, b, eps);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(share);
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_VALUE:
                        ((TextView) findViewById(R.id.result)).setText(getString(R.string.x_eq, (double) msg.obj));
                        break;
                    case MESSAGE_POINT:
                        curve.appendData((DataPoint) msg.obj, false, 200);
                        break;
                }
            }
        };
    }

    public void okButtonClick(final View view) {
        try {
            a = Double.parseDouble(((EditText) findViewById(R.id.input_a)).getText().toString());
            b = Double.parseDouble(((EditText) findViewById(R.id.input_b)).getText().toString());
            eps = Double.parseDouble(((EditText) findViewById(R.id.input_eps)).getText().toString());
        } catch (NumberFormatException nfe) {
            ((TextView) findViewById(R.id.result)).setText(getString(R.string.error));
            return;
        }

        if (thread == null || !thread.isAlive()) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    double left = isolateRoot(a, b, 0.001);
                    sendValue(findIsolatedRoot(left, left + 0.001, eps));
                    configureGraph();
                    makeGraph();
                }
            });
            thread.start();
        }
    }

    private void configureGraph() {
        curve = new LineGraphSeries<>();
        curve.setColor(Color.RED);
        curve.setTitle(getString(R.string.equation));
        curve.setDrawDataPoints(false);
        curve.setThickness(3);

        graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();
        graph.addSeries(curve);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setFixedPosition(10, 10);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(a);
        graph.getViewport().setMaxX(b);
    }

    private void makeGraph() {
        for (double x = a; x <= b; x += 0.1) {
            sendPoint(x, f(x));
        }
    }

    private void sendPoint(double x, double y) {
        Message msg = handler.obtainMessage();
        msg.obj = new DataPoint(x, y);
        msg.what = MESSAGE_POINT;
        handler.sendMessage(msg);
    }

    private void sendValue(double x) {
        Message msg = handler.obtainMessage();
        msg.obj = x;
        msg.what = MESSAGE_VALUE;
        handler.sendMessage(msg);
    }

    public static double findIsolatedRoot(double a, double b, double eps) {
        if (Math.signum(d2f(a) * f(a)) == 1.0) {
            b = a;
        }
        double x = b;
        do {
            b = x;
            x = b - f(b) / df(b);
        } while (Math.abs(x - b) > eps);
        return x;
    }

    public static double f(double x) {
        return Math.pow(x, 3) - x - 3;
    }

    public static double df(double x) {
        return 3 * Math.pow(x, 2) - 1;
    }

    public static double d2f(double x) {
        return 6 * x;
    }

    public static double isolateRoot(double a, double b, double step) {
        double right = a;
        for (double left = a + step; left <= b; left += step) {
            if (Math.signum(f(right) * f(left)) == 1.0) {
                right = left;
            } else {
                break;
            }
        }
        return right;
    }
}
