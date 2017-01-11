package adapter;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchResultAdapter extends SearchHistoryItemAdapter{
    public SearchResultAdapter(Context context, ArrayList<HashMap<String, Object>> listItem) {
        super(context, listItem);
    }

    //TODO 继承 main Tab 页的搜索记录Adapter，增加一些其他信息来适配搜索结果的Item
}
