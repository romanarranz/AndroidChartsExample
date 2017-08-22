package com.github.romanarranz.androiddynamicchartsexample.fragments;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.github.romanarranz.androiddynamicchartsexample.helpers.MapBundler;
import com.github.romanarranz.androiddynamicchartsexample.helpers.MyMarkerView;

import java.util.Map;
import java.util.TreeMap;

import icepick.Icepick;
import icepick.State;

/**
 * Created by romanarranzguerrero on 21/8/17.
 */

public class DynamicPlotFragment extends Fragment {

    public static final String DPF_URI = "DPFURI";
    private static final String LOG_TAG = DynamicPlotFragment.class.getSimpleName();

    private View mRootView;
    private Handler mHandler = new Handler();
    private Typeface mTfLight;
    private long m5fps = 1000 / 5, mStopTime = 1000 * 25; // parar la hebra a los 25s

    private LineChart mChart;

    @State(MapBundler.class) Map<Float, Float> mDatasetCosP3PI;
    @State(MapBundler.class) Map<Float, Float> mDatasetCosL3PI;
    @State long mCurrentTime;

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

        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        } else {
            mDatasetCosL3PI = new TreeMap<>();
            mDatasetCosP3PI = new TreeMap<>();
            mCurrentTime = 0;
        }

        // 1. Opciones del grafico
        setupSettings();

        // 2. Gestos del usuario
        setupGestures();

        // 3. Marcadores
        setupMarker(R.layout.custom_marker_view);

        // 4. Ejes X e Y
        setupAxis();

        // 5. Rellenar los datos si se guardaron con el estado y sino poner datos vacios
        LineData data = new LineData();
        mChart.setData(data);

        boolean update = false;
        if (mDatasetCosL3PI.size() > 0) {
            update = true;
            Log.i(LOG_TAG, "tenia cosas el cos(3x-2π");
            int datasetIndex = 0;
            addCosL3PiDataset();
            for (Map.Entry<Float, Float> entry : mDatasetCosL3PI.entrySet()) {
                Float x = entry.getKey();
                Float y = entry.getValue();
                data.addEntry(new Entry(x, y), datasetIndex);
            }
        }

        if (mDatasetCosP3PI.size() > 0) {
            update = true;
            Log.i(LOG_TAG, "tenia cosas el cos(3x+3π");
            int datasetIndex = 1;
            addCosP3PiDataset();
            for (Map.Entry<Float, Float> entry : mDatasetCosP3PI.entrySet()) {
                Float x = entry.getKey();
                Float y = entry.getValue();
                data.addEntry(new Entry(x, y), datasetIndex);
            }
        }

        // 6. Animaciones
        mChart.animateX(3000);

        // 7. Leyenda
        setupLegend();

        // 8. Pintar
        mChart.invalidate();

        if (update) {
            updateChart(6);
        }

        // 9. Lanzar la hebra de actualizacion de la UI
        mHandler.post(mTickUI);

        return mRootView;
    }

    /**
     * Cuando el dispositivo rota guardamos el estado que tenia el fragment para continuar cuando vuelva a iniciarse
     *
     * @param outState bundle de salida con los datos guardados
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentTime > 0) {
            /*
            Parcelable mapParcel1 = Parcels.wrap(new TreeMap<Float, Float>());
            Parcelable mapParcel2 = Parcels.wrap(new TreeMap<Float, Float>());
            outState.putParcelable("cosl3pi_dataset", mapParcelable);
            */
            Icepick.saveInstanceState(this, outState);
        }
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
     * Metodo para añadir el dataset de la funcion cos(3x-2π)
     */
    private void addCosL3PiDataset() {
        LineData data = mChart.getData();

        if (data!= null) {
            int datasetIndex = 0;
            ILineDataSet set = data.getDataSetByIndex(datasetIndex);

            if (set == null) {
                LineDataSet d = new LineDataSet(null, "cos(3x-2π)");
                d.enableDashedLine(10f, 5f, 0f);
                d.enableDashedHighlightLine(10f, 5f, 0f);
                d.setAxisDependency(YAxis.AxisDependency.LEFT);
                d.setColor(Color.YELLOW);
                d.setCircleColor(Color.CYAN);
                d.setHighLightColor(Color.rgb(244, 117, 117));
                d.setLineWidth(1f);
                d.setCircleRadius(3f);
                d.setDrawCircleHole(false);
                d.setValueTextSize(9f);
                d.setDrawFilled(true);
                d.setFormLineWidth(1f);
                d.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                d.setFormSize(15.f);
                d.setDrawFilled(false);
                d.setDrawValues(false);

                set = d;
                data.addDataSet(set);
            }
        }
    }

    /**
     * Metodo para añadir un nuevo valor a la funcion cos(3x-2π)
     *
     * @param x el nuevo valor
     */
    private void addCosL3PiEntry(float x) {
        LineData data = mChart.getData();

        if (data!= null) {
            int datasetIndex = 0;

            if (data.getDataSetByIndex(datasetIndex) == null) {
                addCosL3PiDataset();
            }

            float y = (float) Math.cos(3*x - 2*Math.PI);
            mDatasetCosL3PI.put(x, y);
            data.addEntry(new Entry(x, y), datasetIndex);
        }
    }

    /**
     * Metodo para añadir el dataset de la funcion cos(3x+3π)
     */
    private void addCosP3PiDataset() {
        LineData data = mChart.getData();

        if (data != null) {
            int datasetIndex = 1;
            ILineDataSet set = data.getDataSetByIndex(datasetIndex);

            if (set == null) {
                LineDataSet d = new LineDataSet(null, "cos(3x+3π)");
                d.setAxisDependency(YAxis.AxisDependency.LEFT);
                d.setColor(Color.GRAY);
                d.setCircleColor(Color.MAGENTA);
                d.setHighLightColor(Color.rgb(244, 117, 117));
                d.setLineWidth(1f);
                d.setCircleRadius(3f);
                d.setDrawCircleHole(false);
                d.setValueTextSize(9f);
                d.setDrawFilled(true);
                d.setFormLineWidth(1f);
                d.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                d.setFormSize(15.f);
                d.setDrawFilled(false);
                d.setDrawValues(false);

                set = d;
                data.addDataSet(set);
            }
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

            if (data.getDataSetByIndex(datasetIndex) == null) {
                addCosP3PiDataset();
            }

            float y = (float) Math.cos(3*x + 3*Math.PI);
            mDatasetCosP3PI.put(x, y);
            data.addEntry(new Entry(x, y), datasetIndex);
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
