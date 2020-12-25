package alauncher.cn.measuringinstrument.view.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.ViewHolder;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.database.greenDao.db.AuthorityGroupBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.UserDao;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AuthorityUserFragment extends Fragment {

    private Unbinder unbinder;

    @BindView(R.id.rv)
    RecyclerView mRecyclerView;

    public AuthorityUserAdapter mAuthorityUserAdapter;

    public List<User> usingUsers;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.authority_user_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private long authorityGroupID;

    public void setAuthorityGroupID(long pID) {
        authorityGroupID = pID;
    }

    @OnClick(R.id.save_btn)
    public void onSave(View v) {
        // 这个只能出现比当前用户权限低的用户;
        List<User> allUser = new ArrayList<>();
        List<User> _datas = App.getDaoSession().getUserDao().loadAll();
        for (User user : _datas) {
            android.util.Log.d("wlDebug", "user = " + user.toString());
            int limit = App.getDaoSession().getAuthorityGroupBeanDao().queryBuilder()
                    .where(AuthorityGroupBeanDao.Properties.Id.eq(user.getUseAuthorityGroupID())).unique().getLimit();
            if (limit > App.getCurrentAuthorityGroupBean().getLimit()) {
                allUser.add(user);
            }
        }
        String[] _item = new String[allUser.size()];
        boolean[] _selected = new boolean[allUser.size()];
        boolean[] _base = new boolean[allUser.size()];
        for (int i = 0; i < allUser.size(); i++) {
            _item[i] = allUser.get(i).getName();
            android.util.Log.d("alauncher", allUser.get(i).toString());
            android.util.Log.d("alauncher", "authorityGroupID = " + authorityGroupID);
            _selected[i] = allUser.get(i).getUseAuthorityGroupID() == authorityGroupID ? true : false;
            _base[i] = allUser.get(i).getUseAuthorityGroupID() == authorityGroupID ? true : false;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("多选列表");
        builder.setMultiChoiceItems(_item, _selected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // Toast.makeText(getActivity(), _item[which] + isChecked, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Toast.makeText(getActivity(), "确定", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < _selected.length; i++) {
                    if (_selected[i] ^ _base[i]) {
                        User _user = allUser.get(i);
                        if (_selected[i]) {
                            _user.setUseAuthorityGroupID(authorityGroupID);
                        } else {
                            _user.setUseAuthorityGroupID(4);
                        }
                        App.getDaoSession().getUserDao().insertOrReplace(_user);
                    }
                }
                updateUI();
            }
        });
        builder.create().show();
    }

    private void updateUI() {
        usingUsers = App.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.UseAuthorityGroupID.eq(authorityGroupID)).list();
        mAuthorityUserAdapter.setDatas(usingUsers);
        mAuthorityUserAdapter.notifyDataSetChanged();
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
        usingUsers = App.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.UseAuthorityGroupID.eq(authorityGroupID)).list();
        android.util.Log.d("alauncher", "authorityGroupID = " + authorityGroupID);
        android.util.Log.d("alauncher", "usingUsers.size() = " + usingUsers.size());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAuthorityUserAdapter = new AuthorityUserAdapter(usingUsers);
        mRecyclerView.setAdapter(mAuthorityUserAdapter);
        // mAuthorityUserAdapter.notifyDataSetChanged();
    }

    class AuthorityUserAdapter extends RecyclerView.Adapter<ViewHolder> {

        List<User> datas;

        public AuthorityUserAdapter(List<User> pDatas) {
            datas = pDatas;
        }

        public void setDatas(List<User> pDatas) {
            datas = pDatas;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ViewHolder.createViewHolder(getActivity(), parent, R.layout.authority_user_list_item);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            User _bean = datas.get(position);
            holder.setText(R.id.user_account_tv, _bean.getAccout());
            holder.setText(R.id.user_name_tv, _bean.getName());
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

}
