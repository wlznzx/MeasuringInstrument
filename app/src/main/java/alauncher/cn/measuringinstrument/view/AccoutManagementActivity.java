package alauncher.cn.measuringinstrument.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.MainActivity;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.base.ViewHolder;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.database.greenDao.db.UserDao;
import alauncher.cn.measuringinstrument.widget.UserEditDialog;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;


public class AccoutManagementActivity extends BaseActivity implements UserEditDialog.UIInterface {

    @BindView(R.id.rv)
    SwipeRecyclerView rv;

    UserDao mUserDao;

    List<User> mDatas;
    AccoutAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_accout);
    }

    @Override
    protected void initView() {
        rv.setSwipeMenuCreator(swipeMenuCreator);
        rv.setOnItemMenuClickListener(mMenuItemClickListener);

        mUserDao = App.getDaoSession().getUserDao();


        mDatas = mUserDao.loadAll();
        _adapter = new AccoutAdapter(mDatas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AccoutManagementActivity.this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(_adapter);
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
                SwipeMenuItem addItem = new SwipeMenuItem(AccoutManagementActivity.this).setBackground(R.drawable.delete_24px)
                        .setImage(R.drawable.add_circle)
                        .setWidth(swipe_size)
                        .setHeight(swipe_size);
                swipeLeftMenu.addMenuItem(addItem); // 添加菜单到左侧。
                SwipeMenuItem editItem = new SwipeMenuItem(AccoutManagementActivity.this)
                        .setImage(R.drawable.edit)
                        .setWidth(swipe_size)
                        .setHeight(swipe_size);
                swipeLeftMenu.addMenuItem(editItem); // 添加菜单到左侧。
                // swipeLeftMenu.addMenuItem(closeItem); // 添加菜单到左侧。
            }

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            /* */
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(AccoutManagementActivity.this).setBackground(R.drawable.delete_24px)
                        .setImage(R.drawable.delete)
                        .setWidth(swipe_size)
                        .setHeight(swipe_size);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
                /*
                SwipeMenuItem addItem = new SwipeMenuItem(AccoutManagementActivity.this).setBackground(R.drawable.delete_24px)
                        .setText("添加")
                        .setTextColor(Color.WHITE)
                        .setWidth(swipe_size)
                        .setHeight(swipe_size);
                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
                */
            }
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private OnItemMenuClickListener mMenuItemClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
//                Toast.makeText(AccoutManagementActivity.this, "list第" + position + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT)
//                        .show();

                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(AccoutManagementActivity.this);
                normalDialog.setIcon(R.drawable.delete);
                normalDialog.setTitle("删除用户");
                normalDialog.setMessage("确认删除 " + mDatas.get(position).getAccout() + " 用户吗？");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                                if (mDatas.get(position).getAccout().equals("admin")) {
                                    Toast.makeText(AccoutManagementActivity.this, "管理员账号不能删除.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                mUserDao.delete(mDatas.get(position));
                                updateUI();
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
            } else if (direction == SwipeRecyclerView.LEFT_DIRECTION) {
                if (menuPosition == 0) {
                    doShowAddUserDialog();
                } else if (menuPosition == 1) {
                    doShowEditUserDialog(mDatas.get(position));
                }

            }
        }
    };

    private void updateUI() {
        mDatas = mUserDao.loadAll();
        _adapter.setDatas(mDatas);
        _adapter.notifyDataSetChanged();
    }

    @Override
    public void upDateUserUI() {
        updateUI();
    }


    class AccoutAdapter extends RecyclerView.Adapter<ViewHolder> {

        List<User> datas;

        public AccoutAdapter(List<User> pDatas) {
            datas = pDatas;
        }

        public void setDatas(List<User> pDatas) {
            datas = pDatas;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ViewHolder.createViewHolder(AccoutManagementActivity.this, parent, R.layout.accout_list_item);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (datas.get(position).getAccout().equals(App.handlerAccout))
                holder.setImageResource(R.id.accout_item_radio, R.drawable.radio_checked);
            holder.setText(R.id.accout_tv, "" + datas.get(position).accout);
            holder.setText(R.id.accout_id_tv, "" + datas.get(position).id);
            holder.setText(R.id.accout_name_tv, datas.get(position).name);
            holder.setText(R.id.accout_status_tv, "" + mActivity.getResources().getStringArray(R.array.position)[datas.get(position).getStatus()]);
            holder.setText(R.id.accout_email_tv, "" + datas.get(position).email);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    @OnClick(R.id.add_user_btn)
    public void onClick() {
        doShowAddUserDialog();
    }


    private void doShowAddUserDialog() {
        UserEditDialog _dialog = new UserEditDialog(AccoutManagementActivity.this);
        _dialog.setmUIInterface(this);
        _dialog.show();
    }

    private void doShowEditUserDialog(User user) {
        UserEditDialog _dialog = new UserEditDialog(AccoutManagementActivity.this);
        _dialog.setmUIInterface(this);
        _dialog.show();
        _dialog.goEditMode(user);
    }

}
