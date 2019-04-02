package LabmemFX.xyz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lty
 * Created in 22:57 2019/4/2
 */
public class ConectionDanMu {

    //连接地址
    private String request_url = "";

    //房间号
    private String request_number = "";

    private HttpURLConnection conn;

    private static SetList<String> danmuSet = new SetList<>();

    public ConectionDanMu() {
    }

    public String getRequest_url() {
        return request_url;
    }

    public void setRequest_url(String request_url) {
        this.request_url = request_url;
    }

    public String getRequest_number() {
        return request_number;
    }

    public void setRequest_number(String request_number) {
        this.request_number = request_number;
    }


    public boolean connection() {
        try {
            URL url = new URL(request_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public String getDanMuData() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            String post_value1 = "roomid=" + URLEncoder.encode(request_number, "UTF-8");
            String post_value2 = "&csrf_token=" + URLEncoder.encode("", "UTF-8");
            String post_value3 = "&csrf=" + URLEncoder.encode("", "UTF-8");
            String post_value4 = "&visit_id=" + URLEncoder.encode("", "UTF-8");

            String post_value = post_value1 + post_value2 + post_value3 + post_value4;

            writer.write(post_value);
            writer.flush();
            writer.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String rl = "";
            StringBuffer sb = new StringBuffer();
            while ((rl = reader.readLine()) != null) {
                sb.append(rl);
            }
            reader.close();

            return sb.toString();

        } catch (Exception e) {
            System.out.println("获取数据错误");
            e.printStackTrace();
            return null;
        }
    }

    public static ObservableList<DanMuData> findData(String data) {

        String repData = data.replaceAll("vip", "\r\n");
        String pat = "text(.*)isadmin";
        Pattern pattern = Pattern.compile(pat);
        Matcher matcher = pattern.matcher(repData);
        ObservableList<DanMuData> list = FXCollections.observableArrayList();
        while (matcher.find()) {
            DanMuData dmd = new DanMuData();
            String temp_data = matcher.group();
            //----------------------------------拆分内容
            String pat_text = "text(.*)nickname";
            Pattern pattern1_text = Pattern.compile(pat_text);
            Matcher matcher_text = pattern1_text.matcher(temp_data);

            while (matcher_text.find()) {
                String temp_str = matcher_text.group();
                String text = temp_str.substring(7, temp_str.length() - 11);
                dmd.setText(text);
            }
            //----------------------------------拆分昵称
            String pat_nickname = "nickname(.*)uname_color";
            Pattern pattern_nickname = Pattern.compile(pat_nickname);
            Matcher matcher_nickname = pattern_nickname.matcher(temp_data);
            while (matcher_nickname.find()) {
                String temp_str = matcher_nickname.group();
                String name = temp_str.substring(11, temp_str.length() - 14);
                dmd.setNickname(name);
            }
            //----------------------------------拆分时间
            String pat_timeline = "timeline(.*)isadmin";
            Pattern pattern_timeline = Pattern.compile(pat_timeline);
            Matcher matcher_timeline = pattern_timeline.matcher(temp_data);
            while (matcher_timeline.find()) {
                String temp_str = matcher_timeline.group();
                String time = temp_str.substring(11, temp_str.length() - 10);
                dmd.setTimeline(time);
            }
            if (!danmuSet.contains(dmd.getNickname() + dmd.getText() + dmd.getTimeline())) {
                while (danmuSet.size() >= 1000)
                    danmuSet.remove(0);
                danmuSet.add(dmd.getNickname() + dmd.getText() + dmd.getTimeline());
                list.add(dmd);
            }
        }
        return list;
    }

    public void disconnect() {
        if (conn != null) {
            conn.disconnect();
        }
    }

}
