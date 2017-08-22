package com.github.romanarranz.androiddynamicchartsexample.fragments;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.romanarranz.androiddynamicchartsexample.R;
import com.github.romanarranz.androiddynamicchartsexample.helpers.MyMarkerView;

/**
 * Created by romanarranzguerrero on 21/8/17.
 */

public class DynamicPlotFragment extends Fragment {

    public static final String DPF_URI = "DPFURI";

    private View mRootView;
    private LineChart mChart;
    private Handler mHandler = new Handler();
    private Typeface mTfLight;

    private long m5fps = 1000 / 5, mCurrentTime = 0, mStopTime = 1000 * 25; // parar la hebra a los 25s

    private Runnable mTickUI = new Runnable() {
        @Override
        public void run() {
            // Generar datos
            generateData(1, 0.25f);
            updateChart(6);

            if (mCurrentTime < mStopTime) {

                mCurrentTime = mCurrentTime + m5fps;

                // Siguiente iteracion de la hebra cada 5fps
                mHandler.postDelayed(mTickUI, m5fps);
            }
        }
    };

    public DynamicPlotFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_static_plot, container, false);
        mChart = (LineChart) mRootView.findViewById(R.id.chart);
        mTfLight = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Light.ttf");

        // 1. Opciones del grafico
        setupSettings();

        // 2. Gestos del usuario
        setupGestures();

        // 3. Marcadores
        setupMarker(R.layout.custom_marker_view);

        // 4. Ejes X e Y
        setupAxis();

        // 5. Datos vacios
        /*
        ArrayList<Entry> values = new ArrayList();
        for (float i = 0f; i<4f; i += 0.25f) {
            values.add(new Entry(i, (float) Math.sin(i)));
        }

        LineDataSet sinDataset = new LineDataSet(values, "sin(x)");

        ArrayList<ILineDataSet> dataSets = new ArrayList();
        dataSets.add(sinDataset);

        // crear el objeto data con todos los datasets
        LineData data = new LineData(dataSets);*/
        LineData data = new LineData();
        mChart.setData(data);

        // 6. Animaciones
        mChart.animateX(3000);

        // 7. Leyenda
        setupLegend();

        // 8. Pintar
        mChart.invalidate();

        // 9. Lanzar la hebra de actualizacion de la UI
        mHandler.post(mTickUI);

        return mRootView;
    }

    /**
     * Metodo para generar un nuevo valor de Y dado un X
     */
    private void generateData(int steps, float increment) {

        Float nextX;

        for (int i = 0; i<steps; i++) {

            if (mChart.getLineData().getEntryCount() == 0) {
                nextX = 0f;
            } else {
                nextX = mChart.getLineData().getXMax() + increment;
            }

            addCosL3PiEntry(nextX);
            addCosP3PiEntry(nextX);
        }
    }

    /**
     * Metodo para establecer los ajustes del grafico
     */
    private void setupSettings() {
        // ajustes del grafico
        mChart.setDrawGridBackground(false);
        mChart.setBackgroundColor(Color.BLACK);
        Description desc = new Description();
        desc.setText("Test Chart");
        desc.setTextColor(Color.WHITE);

        mChart.setDescription(desc);
        mChart.setDrawBorders(false);
        mChart.setNoDataText("No hay datos disponbles");

    }

    /**
     * Metodo para establecer las opciones de gestos del usuario
     */
    private void setupGestures() {
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
    }

    /**
     * Definir el marker que se muestra cuando el usuario selecciona un dato
     *
     * @param layoutId es el layout del marcador
     */
    private void setupMarker(int layoutId) {
        MyMarkerView mv = new MyMarkerView(getContext(), layoutId);
        mv.setChartView(mChart); // para controlar los limites
        mChart.setMarker(mv);

    }

    /**
     * Metodo para establecer las opciones en los ejes y su disposicion
     */
    private void setupAxis() {
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setTypeface(mTfLight);

        LimitLine ll1 = new LimitLine(1f, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(-1f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(2f);
        leftAxis.setAxisMinimum(-2f);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(true);
        leftAxis.setTextSize(10f);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setTypeface(mTfLight);
        mChart.getAxisRight().setEnabled(false); // deshabilita el eje y de la derecha

        // limitar que las lineas sean dibujadas detras de los datos y no por encima
        leftAxis.setDrawLimitLinesBehindData(true);
    }

    /**
     * Metodo para establecer las opciones de la leyenda de los datasets
     */
    private void setupLegend() {
        Legend l = mChart.getLegend();
        l.setTextColor(Color.WHITE);
        l.setTextSize(10f);
        l.setMaxSizePercent(0.25f); // establece el tamaño maximo relativo al grafico en %
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormToTextSpace(5f);
        l.setTypeface(mTfLight);
    }

    /**
     * Metodo para añadir una nueva de la funcion cos(3x-2π) al grafico
     *
     * @param x el nuevo valor
     */
    private void addCosL3PiEntry(float x) {
        LineData data = mChart.getData();

        if (data != null) {
            int sinDatasetIndex = 0;
            ILineDataSet set = data.getDataSetByIndex(sinDatasetIndex);

            if (set == null) {
                LineDataSet sinDataset = new LineDataSet(null, "cos(3x-2π)");
                sinDataset.enableDashedLine(10f, 5f, 0f);
                sinDataset.enableDashedHighlightLine(10f, 5f, 0f);
                sinDataset.setAxisDependency(YAxis.AxisDependency.LEFT);
                sinDataset.setColor(Color.YELLOW);
                sinDataset.setCircleColor(Color.CYAN);
                sinDataset.setHighLightColor(Color.rgb(244, 117, 117));
                sinDataset.setLineWidth(1f);
                sinDataset.setCircleRadius(3f);
                sinDataset.setDrawCircleHole(false);
                sinDataset.setValueTextSize(9f);
                sinDataset.setDrawFilled(true);
                sinDataset.setFormLineWidth(1f);
                sinDataset.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                sinDataset.setFormSize(15.f);
                sinDataset.setDrawFilled(false);
                sinDataset.setDrawValues(false);

                set = sinDataset;
                data.addDataSet(set);
            }

            data.addEntry(new Entry(x, (float) Math.cos(3*x - 2*Math.PI)), sinDatasetIndex);
        }
    }

    /**
     * Metodo para añadir una nueva de la funcion cos(3x+3π) al grafico
     *
     * @param x el nuevo valor
     */
    private void addCosP3PiEntry(float x) {
        LineData data = mChart.getData();

        if (data != null) {
            int datasetIndex = 1;
            ILineDataSet set = data.getDataSetByIndex(datasetIndex);

            if (set == null) {
                LineDataSet dataset = new LineDataSet(null, "cos(3x+3π)");
                dataset.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataset.setColor(Color.GRAY);
                dataset.setCircleColor(Color.MAGENTA);
                dataset.setHighLightColor(Color.rgb(244, 117, 117));
                dataset.setLineWidth(1f);
                dataset.setCircleRadius(3f);
                dataset.setDrawCircleHole(false);
                dataset.setValueTextSize(9f);
                dataset.setDrawFilled(true);
                dataset.setFormLineWidth(1f);
                dataset.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                dataset.setFormSize(15.f);
                dataset.setDrawFilled(false);
                dataset.setDrawValues(false);

                set = dataset;
                data.addDataSet(set);
            }

            data.addEntry(new Entry(x, (float) Math.cos(3*x + 3*Math.PI)), datasetIndex);
        }
    }

    /**
     * Metodo para notificar los cambios al grafico y moverlo si fuera necesario, dejando visibles X entradas
     *
     * @param visibleXRange numero de entradas visibles
     */
    private void updateChart(int visibleXRange) {
        LineData data = mChart.getData();

        if (data != null) {
            data.notifyDataChanged();

            // indicarle al grafico que han cambiado los datos
            mChart.notifyDataSetChanged();

            // limitar el numero de entradas visibles a 20
            mChart.setVisibleXRangeMaximum(visibleXRange);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // mover el grafico a la ultima entrada
            mChart.moveViewToX(data.getEntryCount());

            // esto automaticamente refresca el grafico porque internamente llama a invalidate()
            // mChart.moveViewTo(data.getXValCount()-7, 55f, AxisDependency.LEFT);
        }
    }
}
