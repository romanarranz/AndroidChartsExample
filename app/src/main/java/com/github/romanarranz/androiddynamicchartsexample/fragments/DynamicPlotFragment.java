package com.github.romanarranz.androiddynamicchartsexample.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.romanarranz.androiddynamicchartsexample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by romanarranzguerrero on 21/8/17.
 */

public class DynamicPlotFragment extends Fragment {

    public static final String DPF_URI = "DPFURI";

    private Context mContext;
    private View mRootView;
    private LineChart mChart;
    private Handler mHandler = new Handler();

    private Runnable mTickUI = new Runnable() {
        @Override
        public void run() {
            // Generar datos
            generateData();

            // Siguiente iteracion de la hebra cada 1s
            mHandler.postDelayed(mTickUI, 1000);
        }
    };

    public DynamicPlotFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getContext();
        mRootView = inflater.inflate(R.layout.fragment_static_plot, container, false);
        mChart = (LineChart) mRootView.findViewById(R.id.chart);

        setupAxis();
        setupHighlight();
        setupData();
        setupSettings();
        setupLegend();

        // pintar
        mChart.invalidate();

        // Lanzar la hebra que rellena de datos el grafico
        mHandler.post(mTickUI);

        return mRootView;
    }

    private void generateData() {
        Float value = new Float(10 * Math.random() * 1f);
        mChart.getLineData().addEntry(new Entry(0f, value), 0);
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    private void setupAxis() {
        // Obtener el eje Y para darle estilos
        YAxis leftAxis = mChart.getAxisLeft();

        // linea  de cota superior superada en VERDE
        LimitLine ll = new LimitLine(340f, "Alto indice de Ganancia");
        ll.setLineColor(Color.GREEN);
        ll.setLineWidth(4f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(12f);

        leftAxis.addLimitLine(ll);

        // Obtener el eje X para darle estilos
        XAxis bottomAxis = mChart.getXAxis();
        bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        bottomAxis.setTextSize(10f);
        bottomAxis.setTextColor(Color.GRAY);
        bottomAxis.setDrawAxisLine(true);
        bottomAxis.setDrawGridLines(false);

        // Vamos a darle un nombre mas descriptivo a los datos del eje X en lugar de un rango entre 0f y 3f
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            final String[] quarters = new String[] { "Q1", "Q2", "Q3", "Q4" };

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }
        };

        bottomAxis.setValueFormatter(formatter);
        bottomAxis.setGranularity(1f);
    }

    private void setupHighlight() {
        // habilitar highlight
        mChart.setHighlightPerTapEnabled(true);
    }

    /**
     * El orden de los valores importa proque MPAndroidChart no ordena los valores
     */
    private void setupData() {
        // lista de empresas
        List<Entry> valsComp1 = new ArrayList<>();
        List<Entry> valsComp2 = new ArrayList<>();

        float acum = 100f;
        float[] quarters = {0f, 1f, 2f, 3f}; // X-axis: cuartos del año correspondientes del primer trimestre al cuarto de las empresas
        for (int i = 0; i<4; i++) {
            valsComp1.add(new Entry(quarters[i%quarters.length], acum));
            valsComp2.add(new Entry(quarters[i%quarters.length], 2*acum));

            acum = acum + 30f;
        }

        LineDataSet setComp1 = new LineDataSet(valsComp1, "Company 1");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT); // indicamos el eje donde se traza el dataset
        setComp1.setColors(new int[]{ R.color.red1, R.color.red2, R.color.red3, R.color.red4 }, mContext);

        LineDataSet setComp2 = new LineDataSet(valsComp2, "Company 2");
        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp2.setColors(new int[]{ R.color.cyan1, R.color.cyan2, R.color.cyan3, R.color.cyan4 }, mContext);

        // Usamos la interfaz ILineDataset
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);

        LineData data = new LineData(dataSets);
        mChart.setData(data);
    }

    private void setupSettings() {
        mChart.setBackgroundColor(Color.BLACK);

        Description desc = new Description();
        desc.setText("Test Chart");
        desc.setTextColor(Color.WHITE);

        mChart.setDescription(desc);
        mChart.setDrawBorders(false);
        mChart.setNoDataText("No hay datos disponbles");

        // disponible para Line, Bar, Candle y BubbleChart
        mChart.setAutoScaleMinMaxEnabled(false);
    }

    private void setupLegend() {
        Legend legend = mChart.getLegend();

        // estilos
        legend.setEnabled(true); // habilita la leyenda de los dataset
        legend.setTextColor(Color.WHITE); // fija el color del texto de las leyendas
        legend.setTextSize(10f);

        // clipping
        legend.setMaxSizePercent(0.85f); // establece el tamaño maximo relativo al grafico en %

        // custom / espacios
        legend.setXEntrySpace(10f); // espacio entre etiquetas de leyenda en el eje X
        legend.setYEntrySpace(5f); // espacio entre etiquetas de leyenda en el eje Y
        legend.setForm(Legend.LegendForm.CIRCLE); // queremos la forma de dibujo del color del dataset sea un circulo
        legend.setFormToTextSpace(5f); // espacio entre el circulo y el texto de la leyenda
    }
}
