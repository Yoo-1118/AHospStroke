
package or.kr.ajouhosp.stroke.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import or.kr.ajouhosp.stroke.CustomDialogBasic;
import or.kr.ajouhosp.stroke.LauncherFragment;
import or.kr.ajouhosp.stroke.PermissionFragment;
import or.kr.ajouhosp.stroke.R;

/**************************************************************************************************
 * title : 메인화면
 *
 * description :
 *
 *
 **************************************************************************************************/

public class MainActivity extends AppCompatActivity {
    //==============================================================================================
    //  Constants
    //==============================================================================================
    private static final String TAG = or.kr.ajouhosp.stroke.activity.MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    //==============================================================================================
    //  interface
    //==============================================================================================


    //==============================================================================================
    //  Fields
    //==============================================================================================
    private static CustomDialogBasic customDialog;
    private FragmentManager fManager;
    private FragmentTransaction fTransaction;
    private Context context;
    //==============================================================================================
    //  Methods
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getHashKey();

        setContentView(R.layout.main_activity);
        context = getApplicationContext();
        fManager = getSupportFragmentManager();
        fTransaction = fManager.beginTransaction();

        //초기 Fragment화면 지정
        fTransaction.add(R.id.frameLayout, LauncherFragment.newInstance()).commitAllowingStateLoss();
    }

    //Fragment 전환 메소드
    public void replaceFragment(){
        Log.d("enter replaceFragment","replaceFragment 진입");
        fManager = getSupportFragmentManager();
        fTransaction = fManager.beginTransaction();
        fTransaction.replace(R.id.frameLayout,PermissionFragment.newInstance()).commit();
    }

    //권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS: {
                int length = permissions.length;
                //권한 하나라도 모자라면 다이얼로그
                for (int i = 0; i < length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        customDialog = new CustomDialogBasic(this, "권한을 허용해야 합니다", "닫기", "설정", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //앱 설정 화면으로 이동
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                        customDialog.show();
                        return;
                    }else{
                        //모두 허가되면 로그인화면으로
                        Intent intent = new Intent(context,LoginActivity.class);
                        startActivity(intent);
                    }
                }
                return;
            }
            default:
                return;
        }
    }

    /*해시키 확인*/
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }
}
