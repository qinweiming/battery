package layout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.novadata.batteryapp.R;

import java.util.ArrayList;
import java.util.List;

import adapter.SearchRecordsAdapter;
import utils.RecordsDao;

public class fragment_search extends Fragment implements View.OnClickListener {

    private View view;
    private EditText searchContentEt;
    private SearchRecordsAdapter recordsAdapter;
    private View recordsHistoryView;
    private ListView recordsListLv;
    private TextView clearAllRecordsTv;
    private TextView cancelTv;
    private LinearLayout searchRecordsLl;

    private List<String> searchRecordsList;
    private List<String> tempList;
    private RecordsDao recordsDao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        initView();
        initData();
        bindAdapter();
        initListener();

        return view;
    }

    private void initView() {
        //setHideHeader();
        initRecordsView();

        searchRecordsLl = (LinearLayout) view.findViewById(R.id.search_content_show_ll);
        searchContentEt = (EditText) view.findViewById(R.id.input_search_content_et);

        //添加搜索view
        searchRecordsLl.addView(recordsHistoryView);

    }

    //初始化搜索历史记录View
    private void initRecordsView() {
        recordsHistoryView = LayoutInflater.from(getActivity()).inflate(R.layout.search_records_list_layout, null);
        //显示历史记录lv
        recordsListLv = (ListView) recordsHistoryView.findViewById(R.id.search_records_lv);
        //清除搜索历史记录
        clearAllRecordsTv = (TextView) recordsHistoryView.findViewById(R.id.clear_all_records_tv);
    }


    private void initData() {
        recordsDao = new RecordsDao(getActivity());
        searchRecordsList = new ArrayList<>();
        tempList = new ArrayList<>();
        tempList.addAll(recordsDao.getRecordsList());

        reversedList();
        //第一次进入判断数据库中是否有历史记录，没有则不显示
        checkRecordsSize();
    }


    private void bindAdapter() {
        recordsAdapter = new SearchRecordsAdapter(getActivity(), searchRecordsList);
        recordsListLv.setAdapter(recordsAdapter);
    }

    private void initListener() {
        clearAllRecordsTv.setOnClickListener(this);
        searchContentEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchContentEt.getText().toString().length() > 0) {

                        String record = searchContentEt.getText().toString();

                        //判断数据库中是否存在该记录
                        if (!recordsDao.isHasRecord(record)) {
                            tempList.add(record);
                        }
                        //将搜索记录保存至数据库中
                        recordsDao.addRecords(record);
                        reversedList();
                        checkRecordsSize();
                        recordsAdapter.notifyDataSetChanged();

                        //根据关键词去搜索

                    } else {
                        Toast.makeText(getActivity(), "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        //根据输入的信息去模糊搜索
        searchContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String tempName = searchContentEt.getText().toString();
                tempList.clear();
                tempList.addAll(recordsDao.querySimlarRecord(tempName));
                reversedList();
                checkRecordsSize();
                recordsAdapter.notifyDataSetChanged();
            }
        });

        //历史记录点击事件
        recordsListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //将获取到的字符串传到搜索结果界面
                //searchContentEt.setText(   );
            }
        });
    }

    //当没有匹配的搜索数据的时候不显示历史记录栏
    private void checkRecordsSize(){
        if(searchRecordsList.size() == 0){
            searchRecordsLl.setVisibility(View.GONE);
        }else{
            searchRecordsLl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //清空所有历史数据
            case R.id.clear_all_records_tv:
                tempList.clear();
                reversedList();
                recordsDao.deleteAllRecords();
                recordsAdapter.notifyDataSetChanged();
                searchRecordsLl.setVisibility(View.GONE);
                break;
        }
    }

    //颠倒list顺序，用户输入的信息会从上依次往下显示
    private void reversedList(){
        searchRecordsList.clear();
        for(int i = tempList.size() - 1 ; i >= 0 ; i --){
            searchRecordsList.add(tempList.get(i));
        }
    }

}