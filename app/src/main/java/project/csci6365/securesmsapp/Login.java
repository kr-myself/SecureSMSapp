package project.csci6365.securesmsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Login extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_userid) EditText _useridText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(v -> login());

        _signupLink.setOnClickListener(v -> {
            // Start the Signup activity
            Intent intent = new Intent(getApplicationContext(), Signup.class);
            startActivityForResult(intent, REQUEST_SIGNUP);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Login.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String userid = _useridText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                () -> {
                    // On complete call either onLoginSuccess or onLoginFailed
                    onLoginSuccess(userid);
                    // onLoginFailed();
                    progressDialog.dismiss();
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(String userid) {
        _loginButton.setEnabled(true);
        Intent i = new Intent();
        i.putExtra("userid", userid);
        setResult(100, i);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String userid = _useridText.getText().toString();
        String password = _passwordText.getText().toString();

        if (userid.isEmpty()) {
            _useridText.setError("Field required");
            valid = false;
        } else {
            _useridText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("Field required");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
