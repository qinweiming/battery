package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.novadata.bottomtabdemo.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MyListItemAdapter extends RecyclerView.Adapter {
    private LayoutInflater inflater;
    private ArrayList<HashMap<String, Object>> listItem;
    private MyItemClickListener myItemClickListener;
    //建立枚举 2个item 类型
    public enum ITEM_TYPE {
        ITEM1,//list_head
        ITEM2 //list_item
    }

    public MyListItemAdapter(Context context, ArrayList<HashMap<String, Object>> listItem) {
        inflater = LayoutInflater.from(context);
        this.listItem = listItem;
    }//构造函数，传入数据

    //定义List_Head的Viewholder，List_Head包含广告轮播图和搜索框
    private class HeadViewHolder extends RecyclerView.ViewHolder {


        public HeadViewHolder(View inflate) {
            super(inflate);
           //TODO
        }

    }

    //定义List_Item的Viewholder
    class Viewholder extends RecyclerView.ViewHolder  {
        private TextView Title, Text1, Text2,Text3;
        private ImageView ima;

        public Viewholder(View root) {
            super(root);
            Title = (TextView) root.findViewById(R.id.Itemtitle);
            Text1 = (TextView) root.findViewById(R.id.Itemtext1);
            Text2 = (TextView) root.findViewById(R.id.Itemtext2);
            Text3 = (TextView) root.findViewById(R.id.Itemtext3);
            ima = (ImageView) root.findViewById(R.id.ItemImage);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myItemClickListener != null)
                        myItemClickListener .onItemClick(v,getPosition());
                }

            }//监听到点击就回调MainActivity的onItemClick函数
            );

        }

        public TextView getTitle() {
            return Title;
        }

        public TextView getText1() {
            return Text1;
        }

        public TextView getText2() {
            return Text2;
        }

        public TextView getText3() {
            return Text3;
        }

        public ImageView getIma() {
            return ima;
        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //加载Item View的时候根据不同TYPE加载不同的布局
        if (viewType == ITEM_TYPE.ITEM1.ordinal()) {
            return new HeadViewHolder(inflater.inflate(R.layout.list_head, parent, false));
        } else {
            return new Viewholder(inflater.inflate(R.layout.list_cell, null));
        }
    }

    //设置ITEM类型，可以自由发挥，这里设置item position为0时显示list_head，后面的显示list_item
    @Override
    public int getItemViewType(int position) {
        //Enum类提供了一个ordinal()方法，返回枚举类型的序数，这里ITEM_TYPE.ITEM1.ordinal()代表0， ITEM_TYPE.ITEM2.ordinal()代表1
       // return position == 0 ? ITEM_TYPE.ITEM1.ordinal() : ITEM_TYPE.ITEM2.ordinal();
        //TODO
        return  ITEM_TYPE.ITEM2.ordinal();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //TODO
        //分情况进行数据绑定
//        if (position == 0) {
//            HeadViewHolder head_vh = (HeadViewHolder) holder;
//
//        } else {
//
//        }

        Viewholder vh = (Viewholder) holder;
        vh.Title.setText((String) listItem.get(position).get("ItemTitle"));
        vh.Text1.setText((String) listItem.get(position).get("ItemText1"));
        vh.Text2.setText((String) listItem.get(position).get("ItemText2"));
        vh.Text3.setText((String) listItem.get(position).get("ItemText3"));
        vh.ima.setImageResource((Integer) listItem.get(position).get("ItemImage"));
    }//在这里绑定数据到ViewHolder里面

    @Override
    public int getItemCount() {
        return listItem.size();
    }//返回Item数目

    public void setOnItemClickListener(MyItemClickListener listener){
        myItemClickListener = listener;
    }//绑定MainActivity传进来的点击监听器

}
