package LabmemFX.xyz;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class Main extends Application {

    private ConectionDanMu cdm;

    public static void main(String[] args) {
//        ConectionDanMu conectionDanMu=new ConectionDanMu();
//        conectionDanMu.setRequest_url("https://api.live.bilibili.com/ajax/msg");
//        conectionDanMu.setRequest_number("271744");
//        conectionDanMu.connection();
//        ObservableList<DanMuData> list= ConectionDanMu.findData(conectionDanMu.getDanMuData());
//
//        list.forEach(new Consumer<DanMuData>() {
//            @Override
//            public void accept(DanMuData danMuData) {
//                System.out.println("姓名："+danMuData.getNickname()+" 弹幕:"+danMuData.getText()+" 时间:"+danMuData.getTimeline());
//            }
//        });
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        cdm = new ConectionDanMu();
        cdm.setRequest_url("https://api.live.bilibili.com/ajax/msg");

        VBox root = new VBox();
        root.setStyle("-fx-background-color: #ffffff");

        HBox hBox = new HBox();
        hBox.setPrefHeight(40);
        hBox.setAlignment(Pos.CENTER);

        Button danmu = new Button("获取弹幕");
        TextField text_roomNum = new TextField();
        text_roomNum.setPromptText("请输入B站直播房间号");
        Button close = new Button("断开连接");

        TableView<DanMuData> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setTableMenuButtonVisible(true);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        TableColumn<DanMuData, String> tc_name = new TableColumn<>("姓名");
        TableColumn<DanMuData, String> tc_text = new TableColumn<>("弹幕");
        TableColumn<DanMuData, String> tc_time = new TableColumn<>("时间");
        tc_name.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DanMuData, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DanMuData, String> param) {
                SimpleStringProperty ssp = new SimpleStringProperty(param.getValue().getNickname());
                return ssp;
            }
        });
        tc_text.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DanMuData, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DanMuData, String> param) {
                SimpleStringProperty ssp = new SimpleStringProperty(param.getValue().getText());
                return ssp;
            }
        });
        tc_time.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DanMuData, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DanMuData, String> param) {
                SimpleStringProperty ssp = new SimpleStringProperty(param.getValue().getTimeline());
                return ssp;
            }
        });

        tableView.getColumns().add(tc_name);
        tableView.getColumns().add(tc_text);
        tableView.getColumns().add(tc_time);


        hBox.getChildren().addAll(danmu, text_roomNum, close);

        root.getChildren().addAll(hBox, tableView);

        Scene scene = new Scene(root);
        primaryStage.setWidth(800);
        primaryStage.setHeight(800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("B站弹幕抓取助手");
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();

        //展示弹幕列表
        ObservableList<DanMuData> alldanmu = FXCollections.observableArrayList();
        int rmI = 0;

        DanMuTask danMuTask = new DanMuTask(cdm);
        danMuTask.valueProperty().addListener(new ChangeListener<ObservableList<DanMuData>>() {

            @Override
            public void changed(ObservableValue<? extends ObservableList<DanMuData>> observable, ObservableList<DanMuData> oldValue, ObservableList<DanMuData> newValue) {
                if (newValue != null) {
                    for (DanMuData danMuData : newValue) {
                        while (alldanmu.size() >= 100)
                            alldanmu.remove(0);
                        alldanmu.add(danMuData);
                    }
                    tableView.setItems(alldanmu);
                    tableView.scrollTo(alldanmu.size() - 1);
                }
            }
        });
        danmu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!text_roomNum.equals("")) {
                    cdm.setRequest_number(text_roomNum.getText());

                    danMuTask.setDelay(Duration.seconds(0));
                    danMuTask.setPeriod(Duration.seconds(1));
                    danMuTask.start();

                    danmu.setDisable(true);
                    text_roomNum.setDisable(true);
                }
            }
        });

        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cdm.disconnect();
                danMuTask.cancel();
                danMuTask.reset();
                danmu.setDisable(false);
                text_roomNum.setDisable(false);
            }
        });
    }

    @Override
    public void stop() throws Exception {
        cdm.disconnect();
        super.stop();
    }
}
