package com.github.romanarranz.androiddynamicchartsexample.helpers;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.romanarranz.androiddynamicchartsexample.R;

/**
 * Created by romanarranzguerrero on 22/8/17.
 */

public class MyMarkerView extends MarkerView {

    private TextView mContent;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        mContent = (TextView) findViewById(R.id.marker_content);
    }

    /**
     * Callback que se llama cada vez que el MarkerView se redibuja, puede ser utilizado para
     * actualizar el contenido
     *
     * @param e
     * @param highlight
     */
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        mContent.setText("" + Utils.formatNumber(e.getY(), 0, true));

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
