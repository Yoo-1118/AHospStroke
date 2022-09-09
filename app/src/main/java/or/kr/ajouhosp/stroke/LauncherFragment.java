package or.kr.ajouhosp.stroke;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import or.kr.ajouhosp.stroke.activity.MainActivity;

public class LauncherFragment extends Fragment {
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private View viewPage;
    private ImageView anim_vr;
    private ImageView anim_logo;
    private Context mContext;
    private FragmentManager fManager;
    private FragmentTransaction fTransaction;
    private PermissionFragment permissionFragment;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //다음 Fragment
        mContext = getContext();
        fManager = getActivity().getSupportFragmentManager();
        fTransaction = fManager.beginTransaction();
        permissionFragment = new PermissionFragment();
    }

    public static LauncherFragment newInstance(){
        return new LauncherFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //레이아웃 설정 및 스플래쉬 실행
        viewPage = inflater.inflate(R.layout.initial_launcher, container, false);
        getActivity().getWindow().setFormat(PixelFormat.RGBA_8888);
        anim_vr = viewPage.findViewById(R.id.anim_vr);
        AnimationDrawable vrAnim = (AnimationDrawable) anim_vr.getDrawable();
        vrAnim.start();
        anim_logo = viewPage.findViewById(R.id.anim_logo);
        AnimationDrawable logoAnim = (AnimationDrawable) anim_logo.getDrawable();
        logoAnim.start();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //2초 뒤에 권한 체크 프래그먼트로 이동
                ((MainActivity)getActivity()).replaceFragment();
            }
        }, 2000 );
        return viewPage;
    }
}
