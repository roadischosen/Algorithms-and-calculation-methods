package org.example.lab1;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.Math;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

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
            switch (position) {
                case 0:
                    return new LinAlgoFragment();
                case 1:
                    return new BranchAlgoFragment();
                case 2:
                    return new CyclicAlgoFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.linear);
                case 1:
                    return getString(R.string.branching);
                case 2:
                    return getString(R.string.cyclic);
            }
            return null;
        }
    }


    public static class LinAlgoFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView =  inflater.inflate(R.layout.fragment_linear_algorithm, container, false);
            rootView.findViewById(R.id.calculate_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            handleInput(rootView);
                        }
                    });
            return rootView;
        }

        private void handleInput(View view) {
            TextView textView = (TextView) view.findViewById(R.id.y);
            try {
                double a = Double.parseDouble(((EditText) view.findViewById(R.id.a)).getText().toString());
                double b = Double.parseDouble(((EditText) view.findViewById(R.id.b)).getText().toString());
                double c = Double.parseDouble(((EditText) view.findViewById(R.id.c)).getText().toString());

                textView.setText(getString(R.string.y_eq, calc(a, b, c)));
            } catch (NumberFormatException e) {
                textView.setText("");
            }
        }

        private double calc(double a, double b, double c) {
            return Math.cbrt(5 + c * Math.sqrt(b + 5 * Math.sqrt(a)));
        }
    }

    public static class BranchAlgoFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView =  inflater.inflate(R.layout.fragment_branching_algorithm, container, false);
            rootView.findViewById(R.id.calculate_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (handleInput(rootView)) {
                                FragmentActivity fa = getActivity();
                                InputMethodManager inputManager = (InputMethodManager) fa.getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputManager.hideSoftInputFromWindow((null == fa.getCurrentFocus()) ? null : fa.getCurrentFocus().getWindowToken(),
                                                                     InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                        }
                    });
            return rootView;
        }

        private boolean handleInput(View view) {
            TextView textView = (TextView) view.findViewById(R.id.y);
            try {
                double k = Double.parseDouble(((EditText) view.findViewById(R.id.k)).getText().toString());
                double d = Double.parseDouble(((EditText) view.findViewById(R.id.d)).getText().toString());
                textView.setText(getString(R.string.y_eq, calc(k, d)));
                return true;
            } catch (NumberFormatException e) {
                textView.setText("");
                return false;
            }
        }

        private double calc(double k, double d) {
            if (k > 10)
                return Math.sqrt(k * Math.abs(d) + d * Math.abs(k));
            else
                return Math.pow(k + d, 2);
        }

    }

    public static class CyclicAlgoFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView =  inflater.inflate(R.layout.fragment_cyclic_algorithm, container, false);
            rootView.findViewById(R.id.calculate_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (handleInput(rootView)) {
                                FragmentActivity fa = getActivity();
                                InputMethodManager inputManager = (InputMethodManager) fa.getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputManager.hideSoftInputFromWindow((null == fa.getCurrentFocus()) ? null : fa.getCurrentFocus().getWindowToken(),
                                                                     InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                        }
                    });
            return rootView;
        }

        private boolean handleInput(View view) {
            TextView textView = (TextView) view.findViewById(R.id.y);
            try {
                Integer n = Integer.parseInt(((EditText) view.findViewById(R.id.n)).getText().toString());
                textView.setText(getString(R.string.y_eq, calc(n)));
                return true;
            } catch (NumberFormatException e) {
                textView.setText("");
                return false;
            }
        }

        private double calc(int n) {
            if (n == 0)
                return 0.0;
            double[] a = new double[n+1];
            double[] b = new double[n+1];

            for (int i = 0; i <= n; i++) {
                a[i] = ThreadLocalRandom.current().nextDouble(-10, 11);
                b[i] = ThreadLocalRandom.current().nextDouble(-10, 11);
            }

            double sum  = 0.0;
            double mult = 1.0;
            for (int i = 0; i < n; i++) {
                sum += a[i+1] * b[i];
                mult *= (a[i] + b[i+1]);
            }

            return sum + mult;
        }
    }
}
