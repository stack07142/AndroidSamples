package com.android.sample_firebase_auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;

    // View
    private TextView retTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate()");

        // View
        retTextView = (TextView) findViewById(R.id.ret_textview);

        // Firebase 로그인 통합 관리하는 Object 만들기
        mAuth = FirebaseAuth.getInstance();

        // 구글 로그인 옵션 설정 (요청 토큰, 요청 권한.. 등등)
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.id))
                .requestEmail()
                .build();

        // 구글 로그인 API 만들기
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {

                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Log.d(TAG, "Login fail");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.btn_signin);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

                startActivityForResult(signInIntent, RC_SIGN_IN);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "request code = " + requestCode);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                // 로그인 성공 했을때
                GoogleSignInAccount acct = result.getSignInAccount();

                firebaseAuthWithGoogle(acct);
            } else {


            }
        }
    }

    // 구글에서 받는 정보를 파이어베이스 서버로 넘기기
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            String retText = "표시되는 전체 이름 =" + acct.getDisplayName()
                                    + "\n표시되는 이름=" + acct.getGivenName()
                                    + "\n이메일=" + acct.getEmail()
                                    + "\n표시되는 성=" + acct.getFamilyName();

                            retTextView.setText(retText);

                            Log.d(TAG, retText);

                        } else {

                            // 로그인 실패 했을때
                            Log.e(TAG, "로그인 실패");

                            retTextView.setText("로그인 실패");
                        }

                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
