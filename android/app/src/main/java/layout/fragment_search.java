package layout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.novadata.batteryapp.R;

import java.util.ArrayList;
import java.util.HashMap;

import adapter.MyItemClickListener;
import adapter.SearchHistoryBriefItemAdapter;
import utils.JsonLoader;

public class fragment_search extends Fragment implements MyItemClickListener{

    private View view;
    private RecyclerView Rv;
    private ArrayList<HashMap<String,Object>> listItem = new ArrayList<HashMap<String,Object>>();
    private SearchHistoryBriefItemAdapter shbItemAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        //读取json数据
        JsonLoader jsonLoader = new JsonLoader("db.json");
        listItem = jsonLoader.loadJson2container("search_history_item", listItem);
        initView();
        return view;
    }

    public void initView(){
    //配置列表视图的适配器
    shbItemAdapter = new SearchHistoryBriefItemAdapter(getActivity(),listItem);
    shbItemAdapter.setOnItemClickListener(this);

    Rv = (RecyclerView) view.findViewById(R.id.search_history_list);
    Rv.setAdapter(shbItemAdapter);
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    Rv.setLayoutManager(layoutManager);
    Rv.setHasFixedSize(true);
}

    @Override
    public void onItemClick(View view, int postion) {//����¼��Ļص�����
        //TODO设置Item的点击事件
        Toast.makeText(getActivity(), "点击了第" + postion + "条历史记录", Toast.LENGTH_SHORT).show();
    }

}