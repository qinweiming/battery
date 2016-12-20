package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.novadata.batteryapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MyListItemAdapter extends RecyclerView.Adapter {
    private View mHeaderView;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String, Object>> listItem;
    private MyItemClickListener myItemClickListener;

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    public MyListItemAdapter(Context context, ArrayList<HashMap<String, Object>> listItem) {
        inflater = LayoutInflater.from(context);
        this.listItem = listItem;
    }//构造函数，传入数据

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    //定义RecyclerView的ViewHolder
    class Holder extends RecyclerView.ViewHolder {

        private TextView Title, Text1, Text2,Text3;
        private ImageView ima;

        public Holder(View itemView) {
            super(itemView);

            if(itemView == mHeaderView) return;

            Title = (TextView) itemView.findViewById(R.id.Itemtitle);
            Text1 = (TextView) itemView.findViewById(R.id.Itemtext1);
            Text2 = (TextView) itemView.findViewById(R.id.Itemtext2);
            Text3 = (TextView) itemView.findViewById(R.id.Itemtext3);
            ima = (ImageView) itemView.findViewById(R.id.ItemImage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myItemClickListener != null)
                        myItemClickListener.onItemClick(v, getPosition());
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

    //设置ITEM类型
    @Override
    public int getItemViewType(int position) {
        if(mHeaderView == null) return TYPE_NORMAL;
        if(position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    //加载Item View的时候根据不同TYPE加载不同的布局
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER) return new Holder(mHeaderView);
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cell, parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //分情况进行数据绑定
        if(getItemViewType(position) == TYPE_HEADER){
            return;
        }

        final int pos = getRealPosition(holder);
        if(holder instanceof Holder) {
            ((Holder) holder).Title.setText((String) listItem.get(pos).get("ItemTitle"));
            ((Holder) holder).Text1.setText((String) listItem.get(pos).get("ItemText1"));
            ((Holder) holder).Text2.setText((String) listItem.get(pos).get("ItemText2"));
            ((Holder) holder).Text3.setText((String) listItem.get(pos).get("ItemText3"));
            ((Holder) holder).ima.setImageResource((Integer) listItem.get(pos).get("ItemImage"));
            /*if(mListener == null) return;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(pos, data);
                }
            });*/
        }
    }//在这里绑定数据到ViewHolder里面

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? listItem.size() : listItem.size() + 1;
    }//返回Item数目

    public void setOnItemClickListener(MyItemClickListener listener){
        myItemClickListener = listener;
    }//绑定MainActivity传进来的点击监听器

}
