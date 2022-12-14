package or.kr.ajouhosp.stroke;

import android.app.Application;
import android.content.Context;


/**************************************************************************************************
 * title : 앱의 Application 객체
 *
 * description :
 *
 *
 * @author sky3098@pineone.com
 * @since 2020-03-30
 **************************************************************************************************/
public class AppApplication extends Application {

    //==============================================================================================
    //  Constants
    //==============================================================================================


    //==============================================================================================
    //  interface
    //==============================================================================================


    //==============================================================================================
    //  Fields
    //==============================================================================================
    Context mContext;

    //==============================================================================================
    //  Methods
    //==============================================================================================
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
