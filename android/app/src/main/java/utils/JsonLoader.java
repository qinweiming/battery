package utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonLoader {
    private String jsonFile;
    private String jsonObject;
    private String string_container;
    private List<String> stringList_container;
    private ArrayList<HashMap<String,Object>> hashMapList_container;

    public JsonLoader(String jsonFile) {
        this.jsonFile = jsonFile;
    }

    public String loadJson2container(String jsonObject, String container){
        this.jsonObject = jsonObject;
        string_container = container;
        try {
            //从assets获取json文件
            InputStreamReader isr = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("assets/" + jsonFile));
            //字节流转字符流
            BufferedReader bfr = new BufferedReader(isr);
            String line ;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bfr.readLine())!=null){
                stringBuilder.append(line);
            }//将JSON数据转化为字符串
            JSONObject root = new JSONObject(stringBuilder.toString());
            //根据键名获取键值信息
            string_container = root.getString(jsonObject);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return string_container;
    }

    public List<String> loadJson2container(String jsonObject, List<String> container){
        this.jsonObject = jsonObject;
        stringList_container = container;
        try {
            //从assets获取json文件
            InputStreamReader isr = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("assets/" + jsonFile));
            //字节流转字符流
            BufferedReader bfr = new BufferedReader(isr);
            String line ;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bfr.readLine())!=null){
                stringBuilder.append(line);
            }//将JSON数据转化为字符串
            JSONObject root = new JSONObject(stringBuilder.toString());
            JSONArray array = root.getJSONArray(jsonObject);
            for (int i = 0;i < array.length();i++)
            {
                stringList_container.add(array.getString(i));
                bfr.close();
                isr.close();//依次关闭流
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return stringList_container;
    }

    public ArrayList<HashMap<String,Object>> loadJson2container(String jsonObject, ArrayList<HashMap<String,Object>> container){
        this.jsonObject = jsonObject;
        hashMapList_container = container;
        try {
            //从assets获取json文件
            InputStreamReader isr = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("assets/" + jsonFile));
            //字节流转字符流
            BufferedReader bfr = new BufferedReader(isr);
            String line ;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bfr.readLine())!=null){
                stringBuilder.append(line);
            }//将JSON数据转化为字符串
            JSONObject root = new JSONObject(stringBuilder.toString());
            JSONArray array = root.getJSONArray(jsonObject);
            for (int i = 0;i < array.length();i++)
            {
                JSONObject module = array.getJSONObject(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemTitle", module.getString("title"));
                map.put("ItemText1", "模组编号：" + module.getString("module_num"));
                map.put("ItemText2", "生产信息：" + module.getString("produce_date") + " "+ module.getString("producer"));
                map.put("ItemText3", "流通信息：" + module.getString("latest_logistics_date") + " "+ module.getString("latest_logistics_place"));
                map.put("ItemImage", module.getString("module_image"));
                hashMapList_container.add(map);
                bfr.close();
                isr.close();//依次关闭流
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return hashMapList_container;
    }

}
