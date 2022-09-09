package or.kr.ajouhosp.stroke.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import java.util.Arrays;

import or.kr.ajouhosp.stroke.callback.FacebookLoginCallback;
import or.kr.ajouhosp.stroke.R;

public class LoginActivity extends AppCompatActivity {

    //카카오
    private SessionCallback sessionCallback = new SessionCallback();
    ImageView btn_kakao_login;

    //페이스북
    private LoginButton btn_facebook_login;
    private FacebookLoginCallback mLoginCallback;
    private CallbackManager mCallbackManager;

    //네이버
    LinearLayout ll_naver_login;
    Button btn_logout;
    public static OAuthLogin mOAuthLoginModule;
    Context mContext;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mContext = getApplicationContext();

        //카카오 로그인
        btn_kakao_login = findViewById(R.id.btn_kakao_login);
        session = Session.getCurrentSession();
        session.addCallback(sessionCallback);

        btn_kakao_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
            }
        });

        //카카오톡 로그아웃
/*        btn_custom_login_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagement.getInstance()
                        .requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                                Toast.makeText(LoginActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });*/

        //페이스북 로그인
        mCallbackManager = CallbackManager.Factory.create();
        mLoginCallback = new FacebookLoginCallback();
        btn_facebook_login = (LoginButton) findViewById(R.id.btn_facebook_login);
        btn_facebook_login.setReadPermissions(Arrays.asList("public_profile", "email"));
        btn_facebook_login.registerCallback(mCallbackManager, mLoginCallback);

        //네이버 로그인
        ll_naver_login = findViewById(R.id.ll_naver_login);
        btn_logout = findViewById(R.id.btn_logout);
        ll_naver_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOAuthLoginModule = OAuthLogin.getInstance();
                mOAuthLoginModule.init(
                        LoginActivity.this
                        ,getString(R.string.naver_client_id)
                        ,getString(R.string.naver_client_secret)
                        ,getString(R.string.naver_client_name)
                        //,OAUTH_CALLBACK_INTENT
                        // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
                );

                @SuppressLint("HandlerLeak")
                OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
                    @Override
                    public void run(boolean success) {
                        if (success) {
                            String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                            String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
                            long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);
                            String tokenType = mOAuthLoginModule.getTokenType(mContext);

                            Log.i("LoginData(Naver)","accessToken : "+ accessToken);
                            Log.i("LoginData(Naver)","refreshToken : "+ refreshToken);
                            Log.i("LoginData(Naver)","expiresAt : "+ expiresAt);
                            Log.i("LoginData(Naver)","tokenType : "+ tokenType);
                            Log.d("LoginData(Naver)","Success");
                            redirectPreviewActivity();
                        } else {
                            String errorCode = mOAuthLoginModule
                                    .getLastErrorCode(mContext).getCode();
                            String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                            Toast.makeText(mContext, "errorCode:" + errorCode
                                    + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
                        }
                    };
                };
                mOAuthLoginModule.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);

        // 페이스북 로그인 시
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1){
            Log.d("LoginData(Facebook)","Success");
            redirectPreviewActivity();
        }
    }

    private class SessionCallback implements ISessionCallback {
        public void onSessionOpened() {
            requestMe();
        }
        //로그인 실패
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("SessionCallback ::","onSessionOpenFailed : " + exception.getMessage());
        }

        //사용자 정보 요청
        public void requestMe(){
            UserManagement.getInstance()
                    .me(new MeV2ResponseCallback() {
                        //세션이 닫혀 실패. 재로그인 필요
                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            Log.e("LoginData(Kakao)", "세션이 닫혀 있음: " + errorResult);
                        }
                        @Override
                        public void onFailure(ErrorResult errorResult) {
                            Log.e("LoginData(Kakao)", "사용자 정보 요청 실패: " + errorResult);
                        }
                        @Override
                        public void onSuccess(MeV2Response result) {
                            Log.i("LoginData(Kakao)", "사용자 아이디: " + result.getId());
                            UserAccount kakaoAccount = result.getKakaoAccount();
                            if (kakaoAccount != null) {

                                // 이메일
                                String email = kakaoAccount.getEmail();

                                if (email != null) {
                                    Log.i("LoginData(Kakao)", "email: " + email);

                                } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                                    // 동의 요청 후 이메일 획득 가능
                                    // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.

                                } else {
                                    // 이메일 획득 불가
                                }

                                // 프로필
                                Profile profile = kakaoAccount.getProfile();

                                if (profile != null) {
                                    Log.d("LoginData(Kakao)", "nickname: " + profile.getNickname());
                                    Log.d("LoginData(Kakao)", "profile image: " + profile.getProfileImageUrl());
                                    Log.d("LoginData(Kakao)", "thumbnail image: " + profile.getThumbnailImageUrl());
                                } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                                    // 동의 요청 후 프로필 정보 획득 가능

                                } else {
                                    // 프로필 획득 불가
                                }
                            }else{
                                Log.i("LoginData(Kakao)", "kakaoAccount Null");
                            }
                            Log.d("LoginData(Kakao)","Success");
                            redirectPreviewActivity();
                        };
                    });
                }
    }

    //프리뷰 화면 전환 메소드
    public void redirectPreviewActivity() {
        startActivity(new Intent(this, LivePreviewActivity.class));
        finish();
    }

}