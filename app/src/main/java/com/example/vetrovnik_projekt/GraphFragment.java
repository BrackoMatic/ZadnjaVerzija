package com.example.vetrovnik_projekt;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.GraphViewXML;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.InputStream;
import java.util.Random;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


public class GraphFragment extends Fragment {
    GraphView graph;

    private LineGraphSeries<DataPoint> series;

    private int lastX = 0;

    public GraphFragment() {
    super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);
        series = new LineGraphSeries<DataPoint>();
        GraphView graph = rootView.findViewById(R.id.graph);
        graph.addSeries(series);
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-1);
        viewport.setMaxY(900);
        viewport.scrollToEnd();

        return rootView;

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }


     public void addEntry(GraphFragment graphFragment,int senzorData) {
         GraphView graph=(GraphView) graphFragment.getView().findViewById(R.id.graph);
//         GraphView graph= view.findViewById(R.id.graph);
        if (graph != null) {
            series = new LineGraphSeries<DataPoint>();
            series.appendData(new DataPoint(lastX++, senzorData / 10d), true, 5);
           // graph.addSeries(series);
        }

     }
}