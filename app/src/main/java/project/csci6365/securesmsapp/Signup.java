package project.csci6365.securesmsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Signup extends AppCompatActivity {
    @BindView(R.id.input_userid) EditText _useridText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(v -> signup());

        _loginLink.setOnClickListener(v -> {
            // Finish the registration screen and return to the Login activity
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
    }

    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Signup.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        MainActivity.generateRSAKeys();
        // TODO Logging in needs to display user id and store in SharedPreferences

        String userid = _useridText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        Connect cc = new Connect();
        cc.get_ip();
        if(!cc.user_exists(userid)){
            cc.insert_user(userid, password, Base64.encodeToString(MainActivity.publicKey.getEncoded(), Base64.DEFAULT));
        } else {
            progressDialog.dismiss();
            _useridText.setError("User ID already exists");
            onSignupFailed();
            return;
        }
        cc.close_sockets();

        new android.os.Handler().postDelayed(
                () -> {
                    onSignupSuccess();
                    progressDialog.dismiss();
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        Intent i = new Intent();
        i.putExtra("userid", _useridText.getText().toString());
        System.out.println("SIGNUP SUCCESS: " + _useridText.getText().toString());
        setResult(RESULT_OK, i);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String userid = _useridText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (userid.isEmpty() || userid.length() < 4 || userid.length() > 10) {
            _useridText.setError("Must be 4-10 characters");
            valid = false;
        } else {
            _useridText.setError(null);
        }

        if (password.isEmpty() || password.length() < 8) {
            _passwordText.setError("Password must have more than 8 characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 8 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Passwords do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
}
