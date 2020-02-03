package com.example.aaveg2020.login;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import com.example.aaveg2020.MainActivity;
import com.example.aaveg2020.R;
import com.example.aaveg2020.UserUtils;
import com.example.aaveg2020.api.AavegApi;


import static com.example.aaveg2020.UserUtils.APIToken;

public class LoginActivity extends AppCompatActivity implements ILoginView, View.OnClickListener {
    View child, trans;
    FrameLayout item;
    private EditText editUser;
    private EditText editPass;
    private Button btnLogin;
    private ILoginPresenter loginPresenter;
    private ProgressBar progressBar;

    ImageView hostelLogo, aaveglogo;
    ImageView ground;

    TextView loginBanner;
    Animation moveRight;
    TextView madeWith;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        item = findViewById(R.id.hostel_chooser);
        child = getLayoutInflater()
                .inflate(R.layout.loginview, item, false);
        item.addView(child);
        moveRight = AnimationUtils.loadAnimation(this, R.anim.move_right);

        editUser = this.findViewById(R.id.et_login_username);
        editPass = this.findViewById(R.id.et_login_password);
        btnLogin = this.findViewById(R.id.btn_login_login);
        progressBar = this.findViewById(R.id.progress_login);
        btnLogin.setOnClickListener(this);
        loginPresenter = new LoginPresenterCompl(this);
        loginPresenter.setProgressBarVisiblity(View.INVISIBLE);
        loginBanner = findViewById(R.id.loginBanner);
        loginBanner.setBackgroundResource(R.drawable.cardbanner);
        madeWith = findViewById(R.id.tv_made_with);

        madeWith.setClickable(true);
        madeWith.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<p>Made with ♥ by <a href=\"https://delta.nitt.edu\" target=\"_blank\">DeltaForce</a> and Aaveg Design Team </p>";
        madeWith.setText(Html.fromHtml(text));
        loginBanner.startAnimation(moveRight);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_login_login:
                loginPresenter.setProgressBarVisiblity(View.VISIBLE);
                btnLogin.setEnabled(false);
                loginPresenter.doLogin(editUser.getText().toString(), editPass.getText().toString());
                break;
        }
    }

    @Override
    public void onClearText() {
        editUser.setText("");
        editPass.setText("");
    }

    @Override
    public void onLoginResult(int code, String message) {
        loginPresenter.setProgressBarVisiblity(View.INVISIBLE);
        btnLogin.setEnabled(true);
        if (code == 200) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            loginPresenter.hasHostel(APIToken);
        } else {
            Toast.makeText(this, "Login Fail, code = " + code + " " + message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSetProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    @Override
    public void setHostel() {
        item.removeView(child);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.hostel_chooser, new ChooseHostel());
        ft.commit();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void goToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
