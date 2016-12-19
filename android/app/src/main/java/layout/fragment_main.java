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

import com.novadata.bottomtabdemo.R;

import java.util.ArrayList;
import java.util.HashMap;

import adapter.MyItemClickListener;
import adapter.MyListItemAdapter;

public class fragment_main extends Fragment implements MyItemClickListener{

    private View view;

    private RecyclerView Rv;
    private ArrayList<HashMap<String,Object>> listItem;
    private MyListItemAdapter myAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);

        initData();
        initView();

        return view;
    }

    public void initData(){
        listItem = new ArrayList<HashMap<String, Object>>();/*在数组中存放数据*/
        for (int i = 0; i < 20; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemTitle", "电池模组");
            map.put("ItemText1", "模组编号：11A2FMZABCDEF1212345AB...");
            map.put("ItemText2", "生产信息：2016年8月3日  深圳比克");
            map.put("ItemText3", "流通信息：2016年8月30日  深圳比克4S店");
            map.put("ItemImage",R.drawable.ic_battery);
            listItem.add(map);
        }
    }

    public void initView(){
        //为ListView绑定适配器
        myAdapter = new MyListItemAdapter(getActivity(),listItem);
        myAdapter.setOnItemClickListener(this);

        Rv = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        Rv.setAdapter(myAdapter);
        //使用线性布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        Rv.setLayoutManager(layoutManager);
        Rv.setHasFixedSize(true);
        //Rv.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));//用类设置分割线
        //Rv.addItemDecoration(new DividerItemDecoration(this, R.drawable.list_divider)); //用已有图片设置分割线

        //设置Item之间的间距
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_space);
        Rv.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }

    @Override
    public void onItemClick(View view, int postion) {//点击事件的回调函数
        System.out.println("点击了第" + postion + "行");
        Toast.makeText(getActivity(), "点击了第"+postion+"行模组信息", Toast.LENGTH_SHORT).show();
    }

}
