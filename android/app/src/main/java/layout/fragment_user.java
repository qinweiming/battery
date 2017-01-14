package layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.novadata.batteryapp.R;

public class fragment_user extends Fragment implements RadioGroup.OnCheckedChangeListener {

    View view;
    private RadioGroup rg;

    static final int DEFAULT_STATUS = -1, USER_4S = 1, USER_COMPANY = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_user, container, false);
        rg = (RadioGroup) view.findViewById(R.id.login_status_rg);
        rg.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Intent intent = new Intent("Login_status");
        switch (checkedId){
            case R.id.default_radioButton:
                intent.putExtra("login_status", DEFAULT_STATUS);
                Log.i("Tag","DEFAULT_STATUS");
                break;
            case R.id.user_company_radioButton:
                intent.putExtra("login_status", USER_COMPANY);
                Log.i("Tag", "USER_COMPANY");
                break;
            case R.id.user_4s_radioButton:
                intent.putExtra("login_status", USER_4S);
                Log.i("Tag", "USER_4S");
                break;
        }
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
}
