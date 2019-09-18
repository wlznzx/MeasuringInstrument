package alauncher.cn.measuringinstrument.view;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.base.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;

public class SystemManagementActivity extends BaseActivity {

    @BindView(R.id.rv)
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        List<MainInfo> _datas = new ArrayList();
        _datas.add(new MainInfo(R.string.communication, R.drawable.settings_ethernet));
        _datas.add(new MainInfo(R.string.io, R.drawable.settings_input_svideo));
        _datas.add(new MainInfo(R.string.system_backup, R.drawable.archive));
        // _datas.add(new MainInfo(R.string.force_calibration, R.drawable.straighten));
        _datas.add(new MainInfo(R.string.set, R.drawable.settings));
        _datas.add(new MainInfo(R.string.wifi_str, R.drawable.wifi));
        MainLayoutAdapter _adapter = new MainLayoutAdapter(_datas);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        rv.addItemDecoration(new RecyclerItemDecoration(24, 3));
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(_adapter);
    }

    class MainInfo {
        public int strID;
        public int drawableID;

        public MainInfo(int strID, int drawableID) {
            this.strID = strID;
            this.drawableID = drawableID;
        }
    }

    class MainLayoutAdapter extends RecyclerView.Adapter<ViewHolder> {

        List<MainInfo> datas;

        public MainLayoutAdapter(List<MainInfo> pDatas) {
            datas = pDatas;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ViewHolder.createViewHolder(SystemManagementActivity.this, parent, R.layout.main_layout_item);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setImageResource(R.id.main_item_iv, datas.get(position).drawableID);
            holder.setText(R.id.main_item_tv, datas.get(position).strID);
            holder.setOnClickListener(R.id.main_item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            Intent _i = new Intent(SystemManagementActivity.this, CommunicationActivity.class);
                            _i.putExtra("Title", R.string.communication);
                            startActivity(_i);
                            break;
                        case 1:
                            startActivity(new Intent(SystemManagementActivity.this, IOActivity.class).putExtra("Title", R.string.io));
                            break;
                        case 2:
                            startActivity(new Intent(SystemManagementActivity.this, BackupActivity.class).putExtra("Title", R.string.system_backup));
                            break;
                        case 3:
                            startActivity(new Intent(SystemManagementActivity.this, SetActivity.class).putExtra("Title", R.string.set));
                            break;
                        case 4:
                            startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                            break;
                        default:
                            break;
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    public class RecyclerItemDecoration extends RecyclerView.ItemDecoration {
        private int itemSpace;
        private int itemNum;

        /**
         * @param itemSpace item间隔
         * @param itemNum   每行item的个数
         */
        public RecyclerItemDecoration(int itemSpace, int itemNum) {
            this.itemSpace = itemSpace;
            this.itemNum = itemNum;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = itemSpace;
            if (parent.getChildLayoutPosition(view) % itemNum == 0) {  //parent.getChildLayoutPosition(view) 获取view的下标
                outRect.left = 0;
            } else {
                // outRect.left = itemSpace;
            }

        }
    }
}
