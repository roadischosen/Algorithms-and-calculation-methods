package org.example.roadischosen.lab2;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.Series;

import java.util.Iterator;

public class ExportableGraphView extends GraphView {
    public ExportableGraphView(Context context) {
        super(context);
    }
    public ExportableGraphView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void drawGraphElements(Canvas canvas) {
        this.drawTitle(canvas);
        this.getViewport().drawFirst(canvas);
        this.getGridLabelRenderer().draw(canvas);
        Iterator var2 = this.getSeries().iterator();

        Series s;
        while(var2.hasNext()) {
            s = (Series)var2.next();
            s.draw(this, canvas, false);
        }

        if(this.mSecondScale != null) {
            var2 = this.mSecondScale.getSeries().iterator();

            while(var2.hasNext()) {
                s = (Series)var2.next();
                s.draw(this, canvas, true);
            }
        }

        this.getViewport().draw(canvas);
        this.getLegendRenderer().draw(canvas);
    }
}
