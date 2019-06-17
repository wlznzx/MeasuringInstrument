package alauncher.cn.measuringinstrument.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

import alauncher.cn.measuringinstrument.R;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
