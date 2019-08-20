package alauncher.cn.measuringinstrument.widget;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import java.util.ArrayList;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.view.adapter.ChooseCodeAdapter;

public class ChooseCodeDialog extends Dialog {

    private final Context mContext;
    private ListView mListView;

    private ChooseCodeAdapter mAdapter;

    public ChooseCodeDialog(Context context) {
        super(context);
        mContext = context;
        initView();
        initListView();
    }

    private void initView() {
        View contentView = View.inflate(mContext, R.layout.content_dialog, null);
        mListView = (ListView) contentView.findViewById(R.id.lv);
        setContentView(contentView);
    }

    private void initListView() {
//        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_expandable_list_item_1);
//        for (int i = 0; i < 10; i++) {
//            stringArrayAdapter.add("item " + i);
//        }

        ArrayList<Integer> _list = new ArrayList<Integer>();
        _list.add(1);
        mAdapter = new ChooseCodeAdapter(_list, mContext);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            return;
        }
        setHeight();
    }

    private void setHeight() {
        Window window = getWindow();
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (window.getDecorView().getHeight() >= (int) (displayMetrics.heightPixels * 0.6)) {
            attributes.height = (int) (displayMetrics.heightPixels * 0.6);
        }
        window.setAttributes(attributes);
    }
}
