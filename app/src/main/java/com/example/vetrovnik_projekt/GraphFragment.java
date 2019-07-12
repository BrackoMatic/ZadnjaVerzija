package com.example.vetrovnik_projekt;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class GraphFragment extends Fragment {

    private LineGraphSeries<DataPoint> seriesSensor, seriesReference;

    private double lastX = 0;
    private Boolean flag = false;

    public GraphFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        seriesSensor = new LineGraphSeries<>();
        seriesSensor.setColor(Color.GREEN);
        seriesReference = new LineGraphSeries<>();
        seriesSensor.setColor(Color.BLUE);
        SeekBar referenca = rootView.findViewById(R.id.referenca);
        GraphView graph = rootView.findViewById(R.id.graph);

        graph.addSeries(seriesSensor);
        graph.addSeries(seriesReference);

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(900);
        viewport.setScalable(true);
        viewport.scrollToEnd();

        Switch turnOnOffswitch = rootView.findViewById(R.id.turnOnOffswitch);
        Switch OnOffPlot = rootView.findViewById(R.id.OnOffPlot);

        referenca.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int tempRef = i;
                ((serijski_terminal) getActivity()).posredujNaActivity("R" + tempRef);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override

            public void onStopTrackingTouch(SeekBar seekBar) {
//                double ref = seekBar.getProgress();
//                AddReference(ref);
            }
        });

        OnOffPlot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    OnOffPlot.setText("Plot Off");
                    flag = true;
                } else {
                    OnOffPlot.setText("Plot On");
                    flag = false;
                }
            }
        });
        turnOnOffswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ((serijski_terminal) getActivity()).posredujNaActivity("V1");
                    turnOnOffswitch.setText("Turn Off");
                } else {
                    ((serijski_terminal) getActivity()).posredujNaActivity("V0");
                    turnOnOffswitch.setText("Turn On");
                }
            }
        });
        return rootView;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

//    public void AddReference(double reference) {
//        GraphView graph = getView().findViewById(R.id.graph);
//        if (graph != null) {
//
//            seriesReference.appendData(new DataPoint(lastX++, reference), true, 5);
//            graph.addSeries(seriesReference);
//
//        }
//
//    }

    public void addSensormeasurement(double senzorData) {
        GraphView graph = getView().findViewById(R.id.graph);
        if (flag) {
            graph.setVisibility(View.VISIBLE);
            if (graph != null) {
                seriesSensor.appendData(new DataPoint(lastX++, senzorData), true, 5);
                graph.addSeries(seriesSensor);
            }
        } else {
            graph.setVisibility(View.INVISIBLE);
        }
    }
}

