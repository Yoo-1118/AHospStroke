package or.kr.ajouhosp.stroke;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialogBasic extends Dialog {

    private String mDialog_basic_content_text;
    private String mDialog_basic_left_btn_title;
    private String mDialog_basic_right_btn_title;
    private TextView mDialog_basic_content;
    private Button mDialog_basic_left_btn;
    private Button mDialog_basic_right_btn;
    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    public CustomDialogBasic(Context context,
                             String dialog_basic_content_text,
                             String dialog_basic_left_btn_title,
                             String dialog_basic_right_btn_title,
                             View.OnClickListener leftListener,
                             View.OnClickListener rightListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mDialog_basic_content_text = dialog_basic_content_text;
        this.mLeftClickListener = leftListener;
        this.mDialog_basic_left_btn_title = dialog_basic_left_btn_title;
        this.mRightClickListener = rightListener;
        this.mDialog_basic_right_btn_title = dialog_basic_right_btn_title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // 다이얼로그 제외 화면 흐리게
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.custom_dialog_basic);

        mDialog_basic_content = (TextView)findViewById(R.id.dialog_basic_content);
        mDialog_basic_left_btn =  (Button)findViewById(R.id.dialog_basic_left_btn);
        mDialog_basic_right_btn = (Button)findViewById(R.id.dialog_basic_right_btn);

        // 타이틀, 왼쪽버튼제목, 오른쪽버튼제목 셋팅
        mDialog_basic_content.setText(mDialog_basic_content_text);
        mDialog_basic_left_btn.setText(mDialog_basic_left_btn_title);
        mDialog_basic_right_btn.setText(mDialog_basic_right_btn_title);

        // 클릭 이벤트 셋팅
        if(TextUtils.isEmpty(mDialog_basic_left_btn_title)) {
            mDialog_basic_left_btn.setVisibility(View.GONE);
        } else {
            mDialog_basic_left_btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    dismiss();
                    if(mLeftClickListener != null) {
                        mLeftClickListener.onClick(v);
                    }
                }
            });
        }

        mDialog_basic_right_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
                if(mRightClickListener != null) {
                    mRightClickListener.onClick(v);
                }
            }
        });
    }
}