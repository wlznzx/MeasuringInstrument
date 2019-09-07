package alauncher.cn.measuringinstrument.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import alauncher.cn.measuringinstrument.R;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ForceCalibrationFragment extends Fragment {

    private Unbinder unbinder;
    private String bookPx;
    private boolean resumed = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            resumed = savedInstanceState.getBoolean("resumed");
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_force_calibration, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (resumed) {
            resumed = false;
            stopBookShelfRefreshAnim();
        }
    }

    @Override
    public void onPause() {
        resumed = true;
        super.onPause();
    }

    private void stopBookShelfRefreshAnim() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @SuppressLint("DefaultLocale")
    private void upSelectCount() {
    }

}
