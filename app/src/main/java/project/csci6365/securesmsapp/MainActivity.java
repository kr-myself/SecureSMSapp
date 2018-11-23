package project.csci6365.securesmsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import android.util.Base64;

import javax.crypto.Cipher;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private final String mypref = "mypref";
    private PublicKey publicKey;
    private PrivateKey privateKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText inputBox = findViewById(R.id.inputText);
        final TextView textView = findViewById(R.id.textView);
        final TextView textView2 = findViewById(R.id.textView2);
        final TextView textView3 = findViewById(R.id.textView3);

        sharedPreferences = getSharedPreferences(mypref, Context.MODE_PRIVATE);
        if (!sharedPreferences.contains("publicKey") && !sharedPreferences.contains("privateKey")) {
            Toast.makeText(this, "No keys found", Toast.LENGTH_LONG).show();
            try {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(1024);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                PrivateKey privateKey = keyPair.getPrivate();
                PublicKey publicKey = keyPair.getPublic();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("publicKey", Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT));
                editor.putString("privateKey", Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT));
                editor.apply();
            } catch (NoSuchAlgorithmException e) {
                Toast.makeText(this, "Unable to generate RSA keys", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Keys found", Toast.LENGTH_LONG).show();
            String publicKeyString = sharedPreferences.getString("publicKey", null);
            String privateKeyString = sharedPreferences.getString("privateKey", null);

            try {
                KeyFactory kf = KeyFactory.getInstance("RSA");
                publicKey = kf.generatePublic(new X509EncodedKeySpec(Base64.decode(publicKeyString, Base64.DEFAULT)));
                privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(privateKeyString, Base64.DEFAULT)));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                Toast.makeText(this, "RSA Encryption unavailable.", Toast.LENGTH_LONG).show();
            }
        }

        inputBox.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (!inputBox.getText().toString().equals("")) {
                    String message = inputBox.getText().toString();
                    Hash hasher = new Hash(inputBox.getText().toString());
                    textView.setText(hasher.getHash());

                    try {
                        Cipher cipher = Cipher.getInstance("RSA");
                        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                        byte[] cipherText = cipher.doFinal(message.getBytes());
                        String cipherTextString = "";
                        for (byte b: cipherText)
                            cipherTextString += Byte.toString(b);
                        textView2.setText(cipherTextString);

                        cipher.init(Cipher.DECRYPT_MODE, privateKey);
                        String plainText = new String(cipher.doFinal(cipherText));
                        textView3.setText(plainText);
                    } catch (Exception e) {
                        Toast.makeText(this, "RSA Encryption unavailable.", Toast.LENGTH_LONG).show();
                    }
                }
            }
            return false;
        });
    }
}
