package cc.foxtail.healingchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private RelativeLayout layout;
    private ProgressBar progressBar;
    private EditText emailEdit;
    private EditText passwordEdit;
    private Button loginButton;
    private Button signupButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

//        if (auth.getCurrentUser() != null) {
//            startActivity(new Intent(LoginActivity.this, FolderActivity.class));
//            finish();
//        }

        layout = (RelativeLayout) findViewById(R.id.layout);
        progressBar = (ProgressBar) findViewById(R.id.signin_progress_bar);
        emailEdit = (EditText) findViewById(R.id.signin_email_edit);
        passwordEdit = (EditText) findViewById(R.id.signin_password_edit);
        loginButton = (Button) findViewById(R.id.signin_button);
        signupButton = (Button) findViewById(R.id.want_signup_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                String email = emailEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    hideProgressBar();
                    Toast.makeText(getApplicationContext(), "이메일을 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    hideProgressBar();
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }


                UserModel userModel = new UserModel();
                userModel.login(email, password, new OnLoginCompleteListener() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onFail() {
                        Toast.makeText(LoginActivity.this, "로그인 실패, 아이디와 비밀번호를 확인하세요", Toast.LENGTH_LONG).show();
                        hideProgressBar();
                    }
                });
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, 1000);
            }
        });
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        loginButton.setEnabled(true);
    }


}









