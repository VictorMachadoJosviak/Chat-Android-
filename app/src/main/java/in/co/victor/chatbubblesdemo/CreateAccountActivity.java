package in.co.victor.chatbubblesdemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.co.victor.chatbubblesdemo.widgets.DialogService;

import static com.google.android.gms.internal.zzs.TAG;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText txtName,txtEmail,txtPassword;
    private DialogService dialogService;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getSupportActionBar().hide();
        dialogService = new DialogService(this);
        mAuth = FirebaseAuth.getInstance();

        initializeSetup();

        AndroidUtilities.showHideKeyboardFocusChangedWithAdView(mAdView,txtEmail,txtPassword,txtName);

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


    }

    private void initializeSetup() {
        mAdView = (AdView) findViewById(R.id.adView);
        txtName = (EditText)findViewById(R.id.txt_name);
        txtEmail = (EditText)findViewById(R.id.txt_email);
        txtPassword = (EditText) findViewById(R.id.txt_password);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        AndroidUtilities.dispatchTouchEvent(event,this);
        return super.dispatchTouchEvent(event);
    }

    public void createAccount(View view) {

        String name = txtName.getText().toString();
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        dialogService.showProgressDialog("Autenticando...");

        try {
            if (!Stringi.isNullOrEmpty(email) && !Stringi.isNullOrEmpty(password)) {

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Log.d("AUTH", "Login Efetuado com sucesso!!!");
                                    Intent i = new Intent(CreateAccountActivity.this,MainActivity.class);
                                    startActivity(i);
                                    finish();
                                    dialogService.showToast("conta criada com sucesso");

                                } else {
                                    Log.w("AUTH", "Falha ao efetuar o Login: ", task.getException());

                                    dialogService.showToast("Usuario já cadastrado");
                                }

                                dialogService.hideProgressDialog();

                            }
                        });

            }
        }catch (Exception ex){
            ex.printStackTrace();
            dialogService.hideProgressDialog();
        }
    }

    public void entrar(View view)
    {
        finish();
    }
}
