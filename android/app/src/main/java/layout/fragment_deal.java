package layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.novadata.batteryapp.R;

public class fragment_deal extends Fragment {


    View view;
    LocalBroadcastManager broadcastManager;
    LinearLayout default_layout;
    CardView company_layout,the_4s_layout;

    int login_status = -1;
    static final int DEFAULT_STATUS = -1, USER_4S = 1, USER_COMPANY = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        int deal_layout = R.layout.fragment_deal;
        view = inflater.inflate(deal_layout, container, false);

        default_layout = (LinearLayout) view.findViewById(R.id.deal_default_layout);
        company_layout = (CardView) view.findViewById(R.id.deal_company_layout);
        the_4s_layout = (CardView) view.findViewById(R.id.deal_4s_layout);

        switch (login_status){
            case USER_COMPANY:{
                Log.i("Tag", "USER_COMPANY has received");
                default_layout.setVisibility(View.GONE);
                the_4s_layout.setVisibility(View.GONE);
                break;
            }
            case USER_4S:{
                Log.i("Tag", "USER_4S has received");
                default_layout.setVisibility(View.GONE);
                company_layout.setVisibility(View.GONE);
                break;
            }
            default:{
                Log.i("Tag", "DEFAULT_STATUS has received");
                company_layout.setVisibility(View.GONE);
                the_4s_layout.setVisibility(View.GONE);
                break;
            }
        }

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册广播
        registerReceiver();
    }

    /**
     * 注册广播接收器
     */
    private void registerReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Login_status");
        broadcastManager.registerReceiver(mAdDownLoadReceiver, intentFilter);
    }

    private BroadcastReceiver mAdDownLoadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            login_status = intent.getIntExtra("login_status", DEFAULT_STATUS);
        }
    };
    /**
     * 注销广播
     */
    @Override public void onDetach() {
        super.onDetach();
        broadcastManager.unregisterReceiver(mAdDownLoadReceiver);
    }

}
