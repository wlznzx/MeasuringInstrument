package alauncher.cn.measuringinstrument.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.base.ViewHolder;
import alauncher.cn.measuringinstrument.bean.AuthorityGroupBean;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.database.greenDao.db.AuthorityGroupBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.UserDao;
import alauncher.cn.measuringinstrument.view.activity_view.DataUpdateInterface;
import alauncher.cn.measuringinstrument.widget.AuthorityGroupEditDialog;
import butterknife.BindView;
import butterknife.OnClick;


public class AuthorityManagementActivity extends BaseOActivity implements DataUpdateInterface {

    @BindView(R.id.rv)
    SwipeRecyclerView rv;

    AuthorityAdapter mAuthorityAdapter;

    List<AuthorityGroupBean> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_authority);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initView() {
//        rv.setSwipeMenuCreator(swipeMenuCreator);
//        rv.setOnItemMenuClickListener(mMenuItemClickListener);
        android.util.Log.d("wlDebug", "App.getCurrentAuthorityGroupBean() = " + App.getCurrentAuthorityGroupBean().getLimit());
        initDate();
//        mDatas = App.getDaoSession().getAuthorityGroupBeanDao().loadAll();
        mAuthorityAdapter = new AuthorityAdapter(mDatas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AuthorityManagementActivity.this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(mAuthorityAdapter);
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
            int swipe_size = getResources().getDimensionPixelSize(R.dimen.swipe_size);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;

            // 添加左侧的，如果不添加，则左侧不会出现菜单。
            {
                SwipeMenuItem addItem = new SwipeMenuItem(AuthorityManagementActivity.this).setBackground(R.drawable.delete_24px)
                        .setImage(R.drawable.add_circle)
                        .setWidth(swipe_size)
                        .setHeight(swipe_size);
                swipeLeftMenu.addMenuItem(addItem); // 添加菜单到左侧。
                SwipeMenuItem editItem = new SwipeMenuItem(AuthorityManagementActivity.this)
                        .setImage(R.drawable.edit)
                        .setWidth(swipe_size)
                        .setHeight(swipe_size);
                swipeLeftMenu.addMenuItem(editItem); // 添加菜单到左侧。
                // swipeLeftMenu.addMenuItem(closeItem); // 添加菜单到左侧。
            }

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            /* */
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(AuthorityManagementActivity.this).setBackground(R.drawable.delete_24px)
                        .setImage(R.drawable.delete)
                        .setWidth(swipe_size)
                        .setHeight(swipe_size);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。

            }
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private OnItemMenuClickListener mMenuItemClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, final int position) {
            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(AuthorityManagementActivity.this);
            normalDialog.setIcon(R.drawable.delete);
            normalDialog.setTitle("删除用户");
            normalDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AuthorityGroupBean _bean = mDatas.get(position);
                            if (_bean != null) {
                                App.getDaoSession().getAuthorityGroupBeanDao().delete(_bean);
                                // 在该权限组被删除的同时，属于该权限的用户的权限组的用户所属权限组切换为默认.
                                List<User> _users = App.getDaoSession().getUserDao()
                                        .queryBuilder().where(UserDao.Properties.UseAuthorityGroupID.eq(_bean.getId())).list();
                                for (User user : _users) {
                                    user.setUseAuthorityGroupID(4);
                                    App.getDaoSession().getUserDao().insertOrReplace(user);
                                }
                                updateUI();
                            }
                        }
                    });
            normalDialog.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            // 显示
            normalDialog.show();
            /*
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
//                Toast.makeText(AccoutManagementActivity.this, "list第" + position + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT)
//                        .show();


            } else if (direction == SwipeRecyclerView.LEFT_DIRECTION) {
                if (menuPosition == 0) {
                    doShowEditDialog(null);
                } else if (menuPosition == 1) {
//                    doShowEditUserDialog(mDatas.get(position));
                }

            }
             */
        }
    };

    private void initDate() {
        mDatas = App.getDaoSession().getAuthorityGroupBeanDao().queryBuilder()
                .where(AuthorityGroupBeanDao.Properties.Limit.gt(App.getCurrentAuthorityGroupBean().getLimit()),
                        AuthorityGroupBeanDao.Properties.Limit.lt(11)).list();
    }

    private void updateUI() {
        initDate();
        mAuthorityAdapter.setDatas(mDatas);
        mAuthorityAdapter.notifyDataSetChanged();
    }


    @Override
    public void dataUpdate() {
        updateUI();
    }


    class AuthorityAdapter extends RecyclerView.Adapter<ViewHolder> {

        List<AuthorityGroupBean> datas;

        public AuthorityAdapter(List<AuthorityGroupBean> pDatas) {
            datas = pDatas;
        }

        public void setDatas(List<AuthorityGroupBean> pDatas) {
            datas = pDatas;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ViewHolder.createViewHolder(AuthorityManagementActivity.this, parent, R.layout.authority_list_item);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//            holder.setText(R.id.accout_tv, "" + datas.get(position).accout);
//            holder.setText(R.id.accout_id_tv, "" + datas.get(position).id);
//            holder.setText(R.id.accout_name_tv, datas.get(position).name);
//            holder.setText(R.id.accout_status_tv, "" + mActivity.getResources().getStringArray(R.array.position)[datas.get(position).getStatus()]);
//            holder.setText(R.id.accout_email_tv, "" + datas.get(position).email);
            final AuthorityGroupBean bean = mDatas.get(position);
            android.util.Log.d("alauncher", "bean = " + bean.toString());
            holder.setText(R.id.authority_group_tv, bean.getName());
            holder.setText(R.id.describe_tv, bean.getDescribe());
            holder.setText(R.id.remarks_tv, bean.getRemarks());
            holder.setOnClickListener(R.id.authority_item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doShowEditDialog(bean);
                }
            });

            holder.setOnLongClickListener(R.id.authority_item, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(AuthorityManagementActivity.this);
                    normalDialog.setIcon(R.drawable.delete_24_icon);
                    normalDialog.setTitle("删除权限组");
//                normalDialog.setMessage("确认删除 " + mDatas.get(position).getAccout() + " 用户吗？");
                    normalDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AuthorityGroupBean _bean = mDatas.get(position);
                                    if (_bean != null) {
                                        App.getDaoSession().getAuthorityGroupBeanDao().delete(_bean);
                                        // 在该权限组被删除的同时，属于该权限的用户的权限组的用户所属权限组切换为默认.
                                        List<User> _users = App.getDaoSession().getUserDao()
                                                .queryBuilder().where(UserDao.Properties.UseAuthorityGroupID.eq(_bean.getId())).list();
                                        for (User user : _users) {
                                            user.setUseAuthorityGroupID(4);
                                            App.getDaoSession().getUserDao().insertOrReplace(user);
                                        }
                                        updateUI();
                                    }
                                }
                            });
                    normalDialog.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    // 显示
                    normalDialog.show();
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    @OnClick(R.id.add_user_btn)
    public void onClick() {
        doShowEditDialog(null);
    }


    private void doShowEditDialog(AuthorityGroupBean bean) {
        AuthorityGroupEditDialog _dialog = new AuthorityGroupEditDialog(AuthorityManagementActivity.this, bean);
        _dialog.setDataUpdateInterface(this);
        _dialog.show();
    }

}
