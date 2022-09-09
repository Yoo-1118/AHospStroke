package or.kr.ajouhosp.stroke;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class PermissionFragment extends Fragment {
    private Fragment mFragment;
    private Button mExitBtn;
    private Button mNextBtn;
    private Context mContext;
    private View mViewPage;
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragment = this;
        mContext = getContext();
    }

    public static PermissionFragment newInstance(){
        return new PermissionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mViewPage = inflater.inflate(R.layout.fragment_permission, container, false);
        
        //앱 종료 버튼
        mExitBtn = mViewPage.findViewById(R.id.exitBtn);
        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext != null) {
                    ((Activity) mContext).finishAndRemoveTask();
                    ((Activity) mContext).moveTaskToBack(true);
                    ((Activity) mContext).finish();
                    ((Activity) mContext).finishAffinity();
                }
                android.os.Process.killProcess(android.os.Process.myPid());
            }});
        
        //권한 허용 버튼
        mNextBtn = (Button) mViewPage.findViewById(R.id.nextBtn);
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
        return mViewPage;
    }

    //권한 확인
    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
        };

        //요청
        ActivityCompat.requestPermissions((Activity) mContext,permissions,REQUEST_CODE_PERMISSIONS);
    }

}
