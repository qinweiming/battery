package layout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.novadata.batteryapp.R;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.HashMap;

import adapter.MyItemClickListener;
import adapter.MyListItemAdapter;

public class fragment_main extends Fragment implements MyItemClickListener{

    private View view;
    private Banner banner;

    private RecyclerView Rv;
    private ArrayList<HashMap<String,Object>> listItem;
    private MyListItemAdapter myAdapter;

    //设置图片资源:url或本地资源
    String[] images= new String[] {
            "http://img.zcool.cn/community/01c8dc56e1428e6ac72531cbaa5f2c.jpg",
            "http://img.zcool.cn/community/0166c756e1427432f875520f7cc838.jpg",
            "http://img.zcool.cn/community/018fdb56e1428632f875520f7b67cb.jpg",
            "http://img.zcool.cn/community/01c8dc56e1428e6ac72531cbaa5f2c.jpg",
            "http://img.zcool.cn/community/01fda356640b706ac725b2c8b99b08.jpg",
            "http://img.zcool.cn/community/01fd2756e142716ac72531cbf8bbbf.jpg",
            "http://img.zcool.cn/community/0114a856640b6d32f87545731c076a.jpg"};

    //设置图片标题:自动对应
    /*String[] titles=new String[]{"十大星级品牌联盟，全场2折起",
            "全场2折起",
            "十大星级品牌联盟",
            "嗨购5折不要停",
            "12趁现在",
            "嗨购5折不要停，12.12趁现在",
            "实打实大顶顶顶顶"};*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);
        banner = (Banner) view.findViewById(R.id.banner);
        initItemHead();
        initItemData();
        initView();

        return view;
    }

    private void initItemHead() {
        //设置样式,默认为:Banner.NOT_INDICATOR(不显示指示器和标题)
        //可选样式如下:
        //1. Banner.CIRCLE_INDICATOR    显示圆形指示器
        //2. Banner.NUM_INDICATOR    显示数字指示器
        //3. Banner.NUM_INDICATOR_TITLE    显示数字指示器和标题
        //4. Banner.CIRCLE_INDICATOR_TITLE    显示圆形指示器和标题
        banner.setBannerStyle(Banner.CIRCLE_INDICATOR);

        //设置轮播样式（没有标题默认为右边,有标题时默认左边）
        //可选样式:
        //Banner.LEFT    指示器居左
        //Banner.CENTER    指示器居中
        //Banner.RIGHT    指示器居右
        banner.setIndicatorGravity(Banner.CENTER);

        //设置轮播要显示的标题和图片对应（如果不传默认不显示标题）
        //banner.setBannerTitle(titles);

        //设置是否自动轮播（不设置则默认自动）
        banner.isAutoPlay(true)    ;

        //设置轮播图片间隔时间（不设置默认为2000）
        banner.setDelayTime(4000);
        //设置图片资源:可选图片网址/资源文件，默认用Glide加载,也可自定义图片的加载框架
        //所有设置参数方法都放在此方法之前执行
        //banner.setImages(images);

        //自定义图片加载框架
        banner.setImages(images, new Banner.OnLoadImageListener() {
            @Override
            public void OnLoadImage(ImageView Imageview, Object url) {
//                Toast.makeText(getActivity(),"加载中", Toast.LENGTH_LONG).show();
                Glide.with(getActivity()).load(url).into(Imageview);
//                Toast.makeText(getActivity(), "加载完", Toast.LENGTH_LONG).show();
            }
        });
        //设置点击事件，下标是从1开始
        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {//设置点击事件
            @Override
            public void OnBannerClick(View view, int position) {
                //TODO 定义广告图的点击响应事件
                Toast.makeText(getActivity(), "你点击了：" + position, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void initItemData(){
        listItem = new ArrayList<>();/*在数组中存放数据*/
        for (int i = 0; i < 20; i++) {
            HashMap<String, Object> map = new HashMap<>();
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
        //TODO 定义Item的点击响应事件
        System.out.println("点击了第" + postion + "行");
        Toast.makeText(getActivity(), "点击了第"+postion+"行模组信息", Toast.LENGTH_SHORT).show();
    }

}
