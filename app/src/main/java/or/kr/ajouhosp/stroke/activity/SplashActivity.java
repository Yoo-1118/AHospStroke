package or.kr.ajouhosp.stroke.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import or.kr.ajouhosp.stroke.R;

/**************************************************************************************************
 * title : splash  화면
 *
 * description :
 *
 *
 * @author sky3098@pineone.com
 * @since 2020-03-30
 **************************************************************************************************/
public class SplashActivity extends AppCompatActivity {

    //==============================================================================================
    //  Constants
    //==============================================================================================
    private static final String TAG = SplashActivity.class.getSimpleName();


    //==============================================================================================
    //  interface
    //==============================================================================================


    //==============================================================================================
    //  Fields
    //==============================================================================================

    //==============================================================================================
    //  Methods
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}
