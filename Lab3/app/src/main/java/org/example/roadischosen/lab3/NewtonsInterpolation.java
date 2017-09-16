package org.example.roadischosen.lab3;


import android.util.Log;

public class NewtonsInterpolation {

    private double[] coefficients;
    private int degree;
    private double[] points;
    private double[] values;
    private double step;

    public NewtonsInterpolation(int degree, double[] points, double[] values) {

        this.degree = degree;
        this.points = new double[degree+2];
        System.arraycopy(points, 0, this.points, 0, this.points.length);
        this.values = new double[degree+2];
        System.arraycopy(values, 0, this.values, 0, this.values.length);

        step = points[1] - points[0];
        coefficients = new double[degree+1];
        coefficients[0] = values[1] - values[0];
        double c = 1;
        for (int i = 1; i <= degree; i++) {
            c *= i;
            coefficients[i] = finiteDifference(i, 1) - coefficients[i - 1];
            coefficients[i - 1] /= c;
        }
        coefficients[degree] /= (c * (degree + 1));
    }

    private double finiteDifference(int degree, int index) {
        double c = degree % 2 == 0 ? 1 : -1;
        double fd = c * values[index];
        for (int j = 1; j <= degree; j++) {
            c *= -(degree-j+1.0)/j;
            fd += c * values[index+j];
        }
        return fd;
    }

    public double value(double point, int degree) {

        double result = values[0];
        double production = 1;
        for (int i = 0; i < degree; i++) {
            production *= (point - points[i]) / step;
            result += production * coefficients[i];
        }
        return result;
    }

}
