package alauncher.cn.measuringinstrument;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.base.ViewHolder;
import alauncher.cn.measuringinstrument.view.ParameterManagementActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class MainActivity extends BaseActivity {

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
        _datas.add(new MainInfo(R.string.measuring, R.drawable.equalizer_24px));
        _datas.add(new MainInfo(R.string.data_query, R.drawable.find_in_page_24px));
        _datas.add(new MainInfo(R.string.parameter_management, R.drawable.functions_24px));
        _datas.add(new MainInfo(R.string.calibration, R.drawable.straighten_24px));
        _datas.add(new MainInfo(R.string.user_management, R.drawable.account_box_24px));
        _datas.add(new MainInfo(R.string.program_management, R.drawable.code_24px));
        _datas.add(new MainInfo(R.string.system_management, R.drawable.phonelink_setup_24px));
        _datas.add(new MainInfo(R.string.store, R.drawable.archive_24px));
        _datas.add(new MainInfo(R.string.spc_analysis, R.drawable.show_chart_24px));
        _datas.add(new MainInfo(R.string.statistical_report, R.drawable.assignment_24px));
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
            return ViewHolder.createViewHolder(MainActivity.this, parent, R.layout.main_layout_item);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setImageResource(R.id.main_item_iv, datas.get(position).drawableID);
            holder.setText(R.id.main_item_tv, datas.get(position).strID);
            holder.setOnClickListener(R.id.main_item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openActivty(ParameterManagementActivity.class);
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
