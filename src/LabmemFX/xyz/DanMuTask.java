package LabmemFX.xyz;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Lty
 * Created in 1:14 2019/4/3
 */
public class DanMuTask extends ScheduledService {

    private ConectionDanMu cdm;

    public DanMuTask(ConectionDanMu cdm) {
        this.cdm = cdm;
    }

    @Override
    protected Task<ObservableList<DanMuData>> createTask() {
        Task<ObservableList<DanMuData>> task = new Task<ObservableList<DanMuData>>() {
            @Override
            protected ObservableList<DanMuData> call() throws Exception {
                cdm.connection();
                String data = cdm.getDanMuData();
                ObservableList<DanMuData> list = ConectionDanMu.findData(data);
                return list;
            }
        };
        return task;
    }
}
