package org.example.roadischosen.lab3;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;

public class MainActivity extends AppCompatActivity {
    private static int n;
    private static double a;
    private static double b;
    private static NewtonsInterpolation ni;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }


    public static double f(double x) {
        return exp(-x+sin(x));
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment retFragment = null;
            switch (position) {
                case 0:
                    retFragment = new InputFragment();
                    break;
                case 1:
                    retFragment = new GraphFragment();
                    break;
            }
            return retFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.data_title);
                case 1:
                    return getString(R.string.graphs_title);
            }
            return null;
        }
    }

    public static class InputFragment extends Fragment {

        public InputFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.input_fragment, container, false);
            rootView.findViewById(R.id.calculate_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            calcButtonClick(rootView);
                        }
                    });
            return rootView;
        }

        public void calcButtonClick(View view) {
            n = Integer.parseInt(((EditText) view.findViewById(R.id.type_sampling)).getText().toString());
            a = Double.parseDouble(((EditText) view.findViewById(R.id.type_from)).getText().toString());
            b = Double.parseDouble(((EditText) view.findViewById(R.id.type_to)).getText().toString());
            double[] xs = new double[n+2];
            double[] ys = new double[n+2];
            double step = (b - a) / n;
            double x = a;
            for (int i = 0; i < n+2; i++) {
                xs[i] = x;
                ys[i] = f(x);
                x += step;
            }

            ni = new NewtonsInterpolation(n, xs, ys);
            x = Double.parseDouble(((EditText) view.findViewById(R.id.type_x)).getText().toString());
            double y = ni.value(x, n);
            double err = f(x) - y;
            ((TextView) view.findViewById(R.id.res_y)).setText(getString(R.string.y_eq, y));
            ((TextView) view.findViewById(R.id.res_error)).setText(getString(R.string.err_eq, err));

        }
    }

    public static class GraphFragment extends Fragment {
        private static Thread thread;
        Handler handler;
        static final int UPDATE_REAL  = 0;
        static final int UPDATE_INTERPOL = 1;
        static final int UPDATE_ERRORS = 2;

        private static GraphView func_graph;
        private static GraphView err_graph;
        private static LineGraphSeries<DataPoint> real;
        private static LineGraphSeries<DataPoint> interpolation;
        private static LineGraphSeries<DataPoint> errors;

        public GraphFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.graph_fragment, container, false);
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    DataPoint dp;
                    switch (msg.what) {
                        case UPDATE_REAL:
                            dp = (DataPoint) msg.obj;
                            real.appendData(dp, false, 100);
                            break;
                        case UPDATE_INTERPOL:
                            dp = (DataPoint) msg.obj;
                            interpolation.appendData(dp, false, 100);
                            break;
                        case UPDATE_ERRORS:
                            dp = (DataPoint) msg.obj;
                            errors.appendData(dp, false, 100);
                            break;
                    }
                }
            };

            return rootView;
        }

        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser && (thread == null || !thread.isAlive())) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (ni != null) {
                            configureGraphs();
                            buildGraphs();
                        }
                    }
                });
                thread.start();
            }
        }

        private void buildGraphs() {
            double y_real;
            double y_interp;
            for (double x = a; x <= b; x += 0.1) {
                y_real = f(x);
                y_interp = ni.value(x, n);
                sendPoint(x, y_real, UPDATE_REAL);
                sendPoint(x, y_interp, UPDATE_INTERPOL);
                sendPoint(x, y_real - y_interp, UPDATE_ERRORS);
            }
        }

        private void configureGraphs() {
            real = new LineGraphSeries<>();
            interpolation = new LineGraphSeries<>();
            errors = new LineGraphSeries<>();
            real.setColor(Color.BLACK);
            interpolation.setColor(Color.RED);
            real.setTitle(getString(R.string.real_title));
            interpolation.setTitle(getString(R.string.interp_title));
            errors.setTitle(getString(R.string.errors_title));
            real.setDrawDataPoints(false);
            interpolation.setDrawDataPoints(false);
            errors.setDrawDataPoints(false);
            real.setThickness(3);
            interpolation.setThickness(3);
            errors.setThickness(3);

            View view = getView();
            func_graph = (GraphView) view.findViewById(R.id.func_graph);
            err_graph = (GraphView) view.findViewById(R.id.err_graph);
            err_graph.removeAllSeries();
            err_graph.addSeries(errors);
            err_graph.getLegendRenderer().setVisible(true);
            err_graph.getLegendRenderer().setFixedPosition(10, 10);
            err_graph.getViewport().setXAxisBoundsManual(true);
            err_graph.getViewport().setMinX(a);
            err_graph.getViewport().setMaxX(b);
            func_graph.removeAllSeries();
            func_graph.addSeries(real);
            func_graph.addSeries(interpolation);
            func_graph.getLegendRenderer().setVisible(true);
            func_graph.getLegendRenderer().setFixedPosition(10, 10);
            func_graph.getViewport().setXAxisBoundsManual(true);
            func_graph.getViewport().setYAxisBoundsManual(true);
            func_graph.getViewport().setMinX(a);
            func_graph.getViewport().setMaxX(b);
            func_graph.getViewport().setMinY(min(f(a), f(b)));
            func_graph.getViewport().setMaxY(max(f(a), f(b)));
            err_graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    String format = "%1$.2e";
                    if (isValueX) {
                        // show normal x values
                        return super.formatLabel(value, isValueX);
                    } else {
                        return String.format(format, value);
                    }
                }
            });
        }

        private void sendPoint(double x, double y, int message) {
            Message msg = handler.obtainMessage();
            msg.obj = new DataPoint(x, y);
            msg.what = message;
            handler.sendMessage(msg);
        }
    }
}


