package alauncher.cn.measuringinstrument.view.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import alauncher.cn.measuringinstrument.R;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HelpFragment extends Fragment {

    private Unbinder unbinder;

    public static final String FILE_NAME = "instruction_book.pdf";

    public static final String file_path = "/mnt/sdcard/instruction_book.pdf";

//    @BindView(R.id.pdfView)
//    public PdfRendererView pdfView;

    private LayoutInflater mInflater;
    private ParcelFileDescriptor mDescriptor;
    private PdfRenderer mRenderer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.help_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.instruction_book_tv)
    public void goInstructionBook() {
        try {
            startActivity(getPdfFileIntent(file_path));
        } catch (ActivityNotFoundException e) {

        }
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(file_path);
                if (!file.exists()) {
                    //复制文件到本地存储
                    InputStream asset = null;
                    try {
                        asset = getContext().getAssets().open(FILE_NAME);
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];

                        int size;
                        while ((size = asset.read(buffer)) != -1) {
                            fos.write(buffer, 0, size);
                        }

                        asset.close();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                /*
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                 */
            }
        }).start();
    }


    //android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }
}
