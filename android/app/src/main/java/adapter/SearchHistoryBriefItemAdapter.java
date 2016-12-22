package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novadata.batteryapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchHistoryBriefItemAdapter extends RecyclerView.Adapter{

    private LayoutInflater inflater;
    private ArrayList<HashMap<String, Object>> listItem;
    private MyItemClickListener myItemClickListener;

    public SearchHistoryBriefItemAdapter(Context context, ArrayList<HashMap<String, Object>> listItem) {
        inflater = LayoutInflater.from(context);
        this.listItem = listItem;
    }//构造函数，传入数据

    //定义RecyclerView的ViewHolder
    class Holder extends RecyclerView.ViewHolder {

        private TextView briefSearch_text;

        public Holder(View itemView) {
            super(itemView);

            briefSearch_text = (TextView) itemView.findViewById(R.id.briefSearch_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myItemClickListener != null)
                        myItemClickListener.onItemClick(v, getPosition());
                }

            }//监听到点击就回调MainActivity的onItemClick函数
            );
        }

        public TextView getBriefSearch_text() {
            return briefSearch_text;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_list_cell, parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof Holder) {
            String title, moduleNum;
            title = (String) listItem.get(position).get("ItemTitle");
            moduleNum = (String) listItem.get(position).get("ItemText1");
            moduleNum = moduleNum.replace("模组编号：","");
            ((Holder) holder).briefSearch_text.setText(title + "：" + moduleNum);
        }
    }//在这里绑定数据到ViewHolder里面

    @Override
    public int getItemCount() {
        return listItem.size();
    }//返回Item数目

    public void setOnItemClickListener(MyItemClickListener listener){
        myItemClickListener = listener;
    }//绑定MainActivity传进来的点击监听器

}
