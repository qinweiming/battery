package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.novadata.batteryapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchHistoryItemAdapter extends RecyclerView.Adapter {
    private LayoutInflater inflater;
    private ArrayList<HashMap<String, Object>> listItem;
    private MyItemClickListener myItemClickListener;
    private String imageUrl;
    private Bitmap bitmap;
    private ImageView imageView;

    public SearchHistoryItemAdapter(Context context, ArrayList<HashMap<String, Object>> listItem) {
        inflater = LayoutInflater.from(context);
        this.listItem = listItem;
    }//构造函数，传入数据

    //定义RecyclerView的ViewHolder
    class Holder extends RecyclerView.ViewHolder {

        private TextView Title, Text1, Text2,Text3;
        private ImageView ima;

        public Holder(View itemView) {
            super(itemView);

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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cell, parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof Holder) {
            ((Holder) holder).Title.setText((String) listItem.get(position).get("ItemTitle"));
            ((Holder) holder).Text1.setText((String) listItem.get(position).get("ItemText1"));
            ((Holder) holder).Text2.setText((String) listItem.get(position).get("ItemText2"));
            ((Holder) holder).Text3.setText((String) listItem.get(position).get("ItemText3"));
            imageUrl = (String) listItem.get(position).get("ItemImage");
            imageView = ((Holder) holder).ima;
            Thread imageViewHandler = new Thread(new NetImageHandler());
            imageViewHandler.start();
        }
    }//在这里绑定数据到ViewHolder里面

    class NetImageHandler implements Runnable {
        @Override
        public void run() {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                //发送消息，通知UI组件显示图片
                handler.sendEmptyMessage(0);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                imageView.setImageBitmap(bitmap);
            }
        }
    };

    public static Bitmap getHttpBitmap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            assert myFileUrl != null;
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setConnectTimeout(0);
            conn.setDoInput(true);
            //conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }//返回Item数目

    public void setOnItemClickListener(MyItemClickListener listener){
        myItemClickListener = listener;
    }//绑定MainActivity传进来的点击监听器

}
