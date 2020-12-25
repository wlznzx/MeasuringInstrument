package alauncher.cn.measuringinstrument.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.AuthorityBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.AuthorityBeanDao;
import alauncher.cn.measuringinstrument.utils.DialogUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AuthorityDetailFragment extends Fragment {

    private Unbinder unbinder;

    // private List<AuthorityBean> authorityBeanLists;

    private Map<String, AuthorityBean> authorityBeanLists = new HashMap<>();

    @BindView(R.id.elv)
    public ExpandableListView mExpandableListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.authority_detail_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @OnClick(R.id.save_btn)
    public void onSave(View v) {
        // 统一存储;
        for (Map.Entry<String, AuthorityBean> entry : authorityBeanLists.entrySet()) {
            App.getDaoSession().getAuthorityBeanDao().insertOrReplace(entry.getValue());
            android.util.Log.d("alauncher", "_bean = " + entry.toString());
        }
        DialogUtils.showDialog(getContext(), getResources().getString(R.string.save), getResources().getString(R.string.save_success));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initView() {
        initData();
        DetailAdapter adapter = new DetailAdapter(getContext(), mGroupList, mItemSet);
        mExpandableListView.setAdapter(adapter);
    }

    private long authorityGroupID;

    public void setAuthorityGroupID(long pID) {
        authorityGroupID = pID;
    }

    class DetailAdapter extends BaseExpandableListAdapter {
        private Context mContext;
        private ArrayList<String> mGroup;
        private ArrayList<ArrayList<String>> mItemList;
        private final LayoutInflater mInflater;

        public DetailAdapter(Context context, ArrayList<String> group, ArrayList<ArrayList<String>> itemList) {
            this.mContext = context;
            this.mGroup = group;
            this.mItemList = itemList;
            mInflater = LayoutInflater.from(context);
        }

        //父项的个数
        @Override
        public int getGroupCount() {
            return mGroup.size();
        }

        //某个父项的子项的个数
        @Override
        public int getChildrenCount(int groupPosition) {
            return mItemList.get(groupPosition).size();
        }

        //获得某个父项
        @Override
        public Object getGroup(int groupPosition) {
            return mGroup.get(groupPosition);
        }

        //获得某个子项
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mItemList.get(groupPosition).get(childPosition);
        }

        //父项的Id
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        //子项的id
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        //获取父项的view
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.authority_detail_main_item, parent, false);
            }
            String group = mGroup.get(groupPosition);
            TextView tvGroup = convertView.findViewById(R.id.authority_tv);
            tvGroup.setText(group);
            Switch sw = convertView.findViewById(R.id.authority_enable_sw);
            sw.setFocusable(false);
            AuthorityBean _authorityBean = authorityBeanLists.get(groupPosition);
            if (_authorityBean == null) {
                sw.setChecked(true);
            } else {
                sw.setChecked(_authorityBean.getAuthorized());
            }
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    AuthorityBean _bean = App.getDaoSession().getAuthorityBeanDao().queryBuilder()
                            .where(AuthorityBeanDao.Properties.Id.eq(groupPosition)
                                    , AuthorityBeanDao.Properties.GroupID.eq(App.getCurrentAuthorityGroupBean().getId())).unique();
                    android.util.Log.d("wlDebug", "groupPosition = " + groupPosition
                            + " isChecked = " + isChecked + "_bean.getAuthorized() = " + _bean.getAuthorized());
                    if (!_bean.getAuthorized()) {
                        DialogUtils.showDialog(getActivity()
                                , getResources().getString(R.string.authorization_failed_title),
                                getResources().getString(R.string.authorization_failed_msg));
                        buttonView.setChecked(false);
                        return;
                    }
//                    android.util.Log.d("alauncher", "groupPosition = " + groupPosition);
                    authorityBeanLists.get("" + groupPosition).setAuthorized(isChecked);
                }
            });
            return convertView;
        }

        //获取子项的view
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final String child = mItemList.get(groupPosition).get(childPosition);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.authority_detail_child_item, parent, false);
            }
            TextView tvChild = convertView.findViewById(R.id.authority_tv);
            Switch sw = convertView.findViewById(R.id.authority_enable_sw);
            sw.setFocusable(false);
            AuthorityBean _authorityBean = authorityBeanLists.get(groupPosition + "_" + childPosition);
            if (_authorityBean == null) {
                sw.setChecked(true);
            } else {
                sw.setChecked(_authorityBean.getAuthorized());
            }
            /*
            tvChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AuthorityBean _bean = App.getDaoSession().getAuthorityBeanDao().queryBuilder()
                            .where(AuthorityBeanDao.Properties.Id.eq(groupPosition + "_" + childPosition)
                                    , AuthorityBeanDao.Properties.GroupID.eq(App.getCurrentAuthorityGroupBean().getId())).unique();
                    if (_bean != null && !_bean.getAuthorized()) {
                        DialogUtils.showDialog(getActivity()
                                , getResources().getString(R.string.authorization_failed_title),
                                getResources().getString(R.string.authorization_failed_msg));
                        sw.setChecked(false);
                        return;
                    }
                    _authorityBean.setAuthorized(i);
                    // Toast.makeText(mContext, child, Toast.LENGTH_SHORT).show();
                }
            });
             */
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    AuthorityBean _bean = App.getDaoSession().getAuthorityBeanDao().queryBuilder()
                            .where(AuthorityBeanDao.Properties.Id.eq(groupPosition + "_" + childPosition)
                                    , AuthorityBeanDao.Properties.GroupID.eq(App.getCurrentAuthorityGroupBean().getId())).unique();
                    if (_bean != null && !_bean.getAuthorized()) {
                        DialogUtils.showDialog(getActivity()
                                , getResources().getString(R.string.authorization_failed_title),
                                getResources().getString(R.string.authorization_failed_msg));
                        buttonView.setChecked(false);
                        return;
                    }
                    _authorityBean.setAuthorized(isChecked);
                }
            });
            tvChild.setText(child);
            return convertView;
        }

        //子项是否可选中,如果要设置子项的点击事件,需要返回true
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private ArrayList<String> mGroupList;
    //item数据
    private ArrayList<ArrayList<String>> mItemSet;

    private void initData() {
        //
        // authorityBeanLists = new ArrayList<>();
        mGroupList = new ArrayList<>();
        mItemSet = new ArrayList<>();

        try {
            JSONArray data = new JSONArray(getFromAssets(getContext(), "authority.json"));
            for (int i = 0; i < data.length(); i++) {
                JSONObject _object = data.getJSONObject(i);
                String id = _object.getString("id");
                String name = _object.getString("name");
                android.util.Log.d("alauncher", "name = " + name);
                mGroupList.add(name);
                JSONArray _secondLevelArr = _object.getJSONArray("second_level");
                //
                ArrayList<String> _list = new ArrayList<>();
                for (int j = 0; j < _secondLevelArr.length(); j++) {
                    JSONObject _secondObject = _secondLevelArr.getJSONObject(j);
                    String _secondID = _secondObject.getString("id");
                    String _secondName = _secondObject.getString("name");
                    _list.add(_secondName);
                    AuthorityBean _bean = App.getDaoSession().getAuthorityBeanDao().queryBuilder().where(AuthorityBeanDao.Properties.GroupID.eq(authorityGroupID)
                            , AuthorityBeanDao.Properties.Id.eq(_secondID)).unique();
                    if (_bean == null) {
                        _bean = new AuthorityBean();
                        _bean.setGroupID(authorityGroupID);
                        _bean.setId(_secondID);
                        _bean.setAuthorized(true);
                    }
                    authorityBeanLists.put(_bean.getId(), _bean);
                    android.util.Log.d("alauncher", "_secondID = " + _secondID);
                    android.util.Log.d("alauncher", "_secondName = " + _secondName);
                }
                mItemSet.add(_list);
                // Base Authority.
                AuthorityBean _bean = App.getDaoSession().getAuthorityBeanDao().queryBuilder().where(AuthorityBeanDao.Properties.GroupID.eq(authorityGroupID)
                        , AuthorityBeanDao.Properties.Id.eq(id)).unique();
                if (_bean == null) {
                    _bean = new AuthorityBean();
                    _bean.setGroupID(authorityGroupID);
                    _bean.setId(id);
                    _bean.setAuthorized(true);
                }
                android.util.Log.d("alauncher", "fristID = " + _bean.getId());
                authorityBeanLists.put(_bean.getId(), _bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private static String getFromAssets(Context context, String fileName) {
        String result = "";
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            int lenght = in.available();
            byte[] buffer = new byte[lenght];
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
