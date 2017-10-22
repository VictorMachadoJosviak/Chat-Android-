package in.co.victor.chatbubblesdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.co.victor.chatbubblesdemo.model.Parameter;
import in.co.victor.chatbubblesdemo.model.User;
import in.co.victor.chatbubblesdemo.widgets.DialogService;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SignInActivity";
    private static final int SIGN_IN = 10;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private SignInButton signInButton;
    private User currentUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DialogService dialogService;
    private EditText txtEmail, txtPassword;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        dialogService = new DialogService(this);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                dialogService.showProgressDialog("Autenticando...");
                if (user != null) {
                    Log.d("AUTH", "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();

                } else {
                    Log.d("AUTH", "onAuthStateChanged:signed_out");
                }
                dialogService.hideProgressDialog();

            }
        };

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        txtEmail = (EditText) findViewById(R.id.txt_login_email);
        txtPassword = (EditText) findViewById(R.id.txt_login_password);

        obterConfiguracoesPadraoLogin();
        AndroidUtilities.showHideKeyboardFocusChangedWithAdView(mAdView,txtEmail,txtPassword);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signiniWithGoogle();
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        AndroidUtilities.dispatchTouchEvent(event,this);
        return super.dispatchTouchEvent(event);
    }

    private void obterConfiguracoesPadraoLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN) {
            GoogleSignInResult resultado = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            boolean deuboa = resultado.isSuccess();
            if (deuboa) {
                GoogleSignInAccount acct = resultado.getSignInAccount();
                obterInfoGoogle(acct);
                Toast.makeText(this, "fucou a bagaca", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void obterInfoGoogle(GoogleSignInAccount acct) {

        currentUser = new User();
        String name = acct.getDisplayName();
        String email = acct.getEmail();
        String id = acct.getId();
        String imageUri = acct.getPhotoUrl().toString();

        currentUser.setName(name);
        currentUser.setEmail(email);
        currentUser.setId(id);
        currentUser.setImageUri(imageUri);

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", currentUser);
        startActivity(i);
        finish();


        // Picasso.with(MainActivity.this).load(urlFoto).resize(100,100).into(ivUsuario);
    }

    public void login(View v) {

        try {

            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                dialogService.showToast("Email ou senha inválidos");

                            }else{
//                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                                startActivity(i);
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                dialogService.showToast("Autenticado");
                            }
                        }
                    });

        } catch (Exception ex) {
            dialogService.hideProgressDialog();
            ex.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    public void signiniWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, SIGN_IN);
    }

    public void navigateToCreateAccount(View view) {
        Intent i = new Intent(this, CreateAccountActivity.class);
        startActivity(i);

    }
}
