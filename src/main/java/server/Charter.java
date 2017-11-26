package server;

import client.ClientState;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.util.*;

public class Charter extends ApplicationFrame {
    private List<ClientState> states;
    private boolean needsRedraw = false;
    private boolean rendered = false;

    public Charter(String title){
        super(title);
        this.states = new ArrayList<>();
    }

    public Charter(String title, List<ClientState> states){
        super(title);
        this.states = states;
    }

    public void addClientState(ClientState state){
        this.states.add(state);

        if(needsRedraw){
            refreshChart();
        }

        if(rendered){
            needsRedraw = true;
        }
    }

    private XYDataset generateDataset(){
        Map<UUID, TimeSeries> timeSeriesMap = new HashMap<>();

        for(ClientState state: states){
            timeSeriesMap.putIfAbsent(state.getClientUuid(), new TimeSeries(state.getClientUuid().toString()));
            TimeSeries timeSeries = timeSeriesMap.get(state.getClientUuid());

            timeSeries.add(new FixedMillisecond(Date.from(state.getTimestamp())), state.getPpm());
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();

        for(TimeSeries timeSeries: timeSeriesMap.values()){
            dataset.addSeries(timeSeries);
        }

        return dataset;
    }

    private JFreeChart createChart(){
        JFreeChart chart = ChartFactory.createTimeSeriesChart(getTitle(),
                "Date",
                "PPM",
                generateDataset());

        return chart;
    }

    private JPanel createPanel(){
        JFreeChart chart = createChart();

        ChartPanel panel = new ChartPanel(chart, false);
        return panel;
    }

    private void refreshChart(){
        ChartPanel panel = (ChartPanel) getContentPane();
        panel.getChart().getXYPlot().setDataset(generateDataset());
        panel.removeAll();
        panel.repaint();
        System.out.println("Refresh done");
        needsRedraw = false;
    }

    public ApplicationFrame render(){
        this.needsRedraw = true;
        this.rendered = true;
        JPanel panel = createPanel();
        setContentPane(panel);
        return this;
    }
}
