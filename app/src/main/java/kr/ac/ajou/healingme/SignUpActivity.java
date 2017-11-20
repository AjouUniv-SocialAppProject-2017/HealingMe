package kr.ac.ajou.healingme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignUpActivity extends AppCompatActivity {

    private EditText name;
    private EditText id;
    private EditText email;
    private EditText password;
    private EditText repassword;
    private TextInputLayout name_layout;
    private TextInputLayout id_layout;
    private TextInputLayout email_layout;
    private TextInputLayout password_layout;
    private TextInputLayout repassword_layout;

    private Button signupButton;
    private Button cancelButton;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        name = (EditText) findViewById(R.id.signup_user_name_edit);
        id = (EditText) findViewById(R.id.signup_user_id_edit);
        email = (EditText) findViewById(R.id.signup_email_edit);
        password = (EditText) findViewById(R.id.signup_password_edit);
        repassword = (EditText) findViewById(R.id.signup_password_check_edit);
        name_layout = (TextInputLayout) findViewById(R.id.signup_user_name_edit_layout);
        id_layout = (TextInputLayout) findViewById(R.id.signup_user_id_edit_layout);
        email_layout = (TextInputLayout) findViewById(R.id.signup_email_edit_layout);
        password_layout = (TextInputLayout) findViewById(R.id.signup_password_edit_layout);
        repassword_layout = (TextInputLayout) findViewById(R.id.signup_password_check_edit_layout);
        signupButton = (Button) findViewById(R.id.signup_button);
        cancelButton = (Button) findViewById(R.id.signup_cancel_button);
        progressBar = (ProgressBar) findViewById(R.id.signup_progress_bar);

        //이메일 형식 확인
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                    email_layout.setError("올바른 이메일 형식이 아닙니다.");
                } else {
                    email_layout.setErrorEnabled(false);
                }
            }
        });

        //비밀번호 6자리 이상 입력 확인
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password.length() < 6) {
                    password_layout.setError("비밀번호는 6자리 이상 입력해주세요!");
                } else {
                    password_layout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //비밀번호 일치 확인
        repassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (password.getText().toString().equals(repassword.getText().toString())) {
//                    checkImage.setImageResource(R.drawable.same);
                    repassword_layout.setErrorEnabled(false);
                    repassword_layout.setHint("비밀번호 일치");
                } else {
//                    checkImage.setImageResource(R.drawable.different);
                    repassword_layout.setError("비밀번호 불일치");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //입력창
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 이메일 입력 확인
                if (email.getText().toString().length() == 0) {
                    Toast.makeText(SignUpActivity.this, "Email을 입력하세요!", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }

                // 비밀번호 입력 확인
                if (password.getText().toString().length() == 0) {
                    Toast.makeText(SignUpActivity.this, "비밀번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    return;
                }

                // 비밀번호 확인 입력 확인
                if (repassword.getText().toString().length() == 0) {
                    Toast.makeText(SignUpActivity.this, "비밀번호 확인을 입력하세요!", Toast.LENGTH_SHORT).show();
                    repassword.requestFocus();
                    return;
                }

                // 비밀번호 일치 확인
                if (!password.getText().toString().equals(repassword.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "비밀번호가 일치하지 않습니다!", Toast.LENGTH_SHORT).show();
                    password.setText("");
                    repassword.setText("");
                    password.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                    UserModel userModel = new UserModel();
                                    userModel.saveUser(name.getText().toString(), id.getText().toString(), 0);
                                } else {
                                    Toast.makeText(SignUpActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                                }
//                                Toast.makeText(SignUpActivity.this, "회원가입 성공여부:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                if(!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent result = new Intent(SignUpActivity.this, LoginActivity.class);
                                    result.putExtra("email", email.getText().toString());
                                    result.putExtra("password", password.getText().toString());
                                    // 자신을 호출한 Activity로 데이터를 보낸다.
                                    setResult(RESULT_OK, result);
                                    startActivity(result);
                                    finish();
                                }
                            }
                        });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent goback = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(goback);
            }
        });


    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        progressBar.setVisibility(View.GONE);
//    }
}
