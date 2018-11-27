package project.csci6365.securesmsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.List;
import android.util.Base64;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String userid;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    @BindView(R.id.welcome) TextView welcome;
    @BindView(R.id.add_user) Button addUser;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("userid")) {
            userid = sharedPreferences.getString("userid", null);
            if (!sharedPreferences.contains("publicKey") || !sharedPreferences.contains("privateKey")) {
                generateRSAKeys();
            } else {
                String publicKeyString = sharedPreferences.getString("publicKey", null);
                String privateKeyString = sharedPreferences.getString("privateKey", null);
                try {
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    publicKey = kf.generatePublic(new X509EncodedKeySpec(Base64.decode(publicKeyString, Base64.DEFAULT)));
                    privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(privateKeyString, Base64.DEFAULT)));
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    // Should disable option to send messages
                    Toast.makeText(this, "RSA Encryption unavailable.", Toast.LENGTH_LONG).show();
                }
            }
            onLogin();
        } else {
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivityForResult(i, 100);
        }
    }

    protected void onLogin() {
        welcome.setText(getString(R.string.welcome, userid));

        List<String> dataset = new ArrayList<>();
        dataset.add("Archie");
        dataset.add("Beto");

        MyAdapter adapter = new MyAdapter(dataset);
        LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        addUser.setOnClickListener(e -> {
            // Input box to type name of user...
            // A layout like dialog_messsage_user.xml must be created for this as well
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 100) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userid", data.getStringExtra("userid"));
            editor.apply();
            generateRSAKeys();
            onLogin();
        }
    }

    private void generateRSAKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("publicKey", Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT));
            editor.putString("privateKey", Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT));
            editor.apply();
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(this, "Unable to generate RSA keys", Toast.LENGTH_LONG).show();
        }

    }
}
