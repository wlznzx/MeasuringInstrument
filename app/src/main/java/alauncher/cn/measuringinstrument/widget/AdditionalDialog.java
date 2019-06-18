package alauncher.cn.measuringinstrument.widget;

import android.content.Context;
import android.os.Bundle;

import alauncher.cn.measuringinstrument.R;
import androidx.appcompat.app.AlertDialog;
import butterknife.ButterKnife;

public class AdditionalDialog extends AlertDialog {

    private Context mContext;

    public AdditionalDialog(Context context) {
        super(context);
        mContext = context;
    }

    public AdditionalDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additional_dialog_layout);
        // View view = LayoutInflater.from(mContext).inflate(R.layout.additional_dialog_layout, null);
        // setView(view);
        ButterKnife.bind(this);
    }

    public interface CodeInterface {
        void onCodeSet(int m, String code);
    }

}
