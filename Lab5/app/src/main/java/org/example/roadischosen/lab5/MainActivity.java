package org.example.roadischosen.lab5;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private double[][] a = {{10, -3, 2},
                            {3, 6, 0  },
                            {2, -5, 6 }};
    private double[]   b =  {5, 7, 9};
    private double eps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TableLayout input_a = (TableLayout) findViewById(R.id.input_a);
        TableRow input_b = (TableRow) ((TableLayout) findViewById(R.id.input_b)).getChildAt(1);
        TableRow tr;
        for (int i = 0; i < 3; i++) {
            tr = (TableRow) input_a.getChildAt(i+1);
            for (int j = 0; j < 3; j++) {
                ((TextView) tr.getChildAt(j+1)).setText(Double.toString(a[i][j]));
            }
            ((TextView) input_b.getChildAt(i+1)).setText(Double.toString(b[i]));
        }
    }

    public void okButtonClick(View view) {
        TableLayout input_a = (TableLayout) findViewById(R.id.input_a);
        TableRow input_b = (TableRow) ((TableLayout) findViewById(R.id.input_b)).getChildAt(1);
        TableRow tr;
        try {
            for (int i = 0; i < 3; i++) {
                tr = (TableRow) input_a.getChildAt(i + 1);
                for (int j = 0; j < 3; j++)
                    a[i][j] = Double.parseDouble(((TextView) tr.getChildAt(j + 1)).getText().toString());
                b[i] = Double.parseDouble(((TextView) input_b.getChildAt(i + 1)).getText().toString());
            }
            eps = Double.parseDouble(((TextView) findViewById(R.id.input_eps)).getText().toString());
        } catch (NumberFormatException e) {
            ((TextView) findViewById(R.id.msg)).setText(getString(R.string.wrong_number));
            return;
        }
        normalize(a, b);
        if (checkSolvableJakobi(a)) {
            double[] current_x;
            double[] next_x = {1, 1, 1};
            ///*
            do {
                current_x = next_x;
                next_x = iteration(a, b, current_x);
            } while(isNotCalculatedEnought(current_x, next_x, eps));//*/

            tr = (TableRow) ((TableLayout) findViewById(R.id.output_x)).getChildAt(1);
            for (int i = 0; i < 3; i++) {
                ((TextView) tr.getChildAt(i+1)).setText(Double.toString(next_x[i]));
            }
        } else {
            ((TextView) findViewById(R.id.msg)).setText(getString(R.string.wrong_equations));
        }

    }

    public static void normalize(double[][] coeffs, double[] constant) {
        int size = coeffs.length;
        double diag;
        for (int i = 0; i < size; i++) {
            diag = coeffs[i][i];
            constant[i] /= diag;
            diag = -diag;
            for (int j = 0; j < size; j++) {
                coeffs[i][j] /= diag;
            }
            coeffs[i][i] = 0;
        }
    }

    public static boolean checkSolvableJakobi(final double[][] coeffs) {
        int size = coeffs.length;
        int verticalSum;
        int horizontalSum;
        for (int i = 0; i < size; i++) {
            verticalSum   = 0;
            horizontalSum = 0;
            for (int j = 0; j < size; j++) {
                horizontalSum += coeffs[i][j];
                verticalSum   += coeffs[j][i];
            }
            if (Math.abs(horizontalSum) >= 1 || Math.abs(verticalSum) >= 1)
                return false;
        }
        return true;
    }

    public static double[] iteration(final double[][] coeffs, final double[] constant, final double[] x) {
        int size = x.length;
        double[] res = new double[size];
        double sum;
        for (int row = 0; row < size; row++) {
            sum = 0;
            for (int col = 0; col < size; col++) {
                sum += coeffs[row][col] * x[col];
            }
            res[row] = sum + constant[row];
        }
        return res;
    }

    public static boolean isNotCalculatedEnought(final double[] old, final double[] nw, final double eps) {
        int size = old.length;
        for (int i = 0; i < size; i++) {
            if (Math.abs(nw[i] - old[i]) < eps)
                return false;
        }
        return true;
    }
}
