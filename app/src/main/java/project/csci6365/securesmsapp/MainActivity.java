package project.csci6365.securesmsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText inputBox = findViewById(R.id.inputText);
        final TextView textView = findViewById(R.id.textView);
        inputBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (!inputBox.getText().toString().equals("")) {
                        Hash hasher = new Hash(inputBox.getText().toString());
                        textView.setText(hasher.getHash());
                    }
                }
                return false;
            }
        });
    }
}
