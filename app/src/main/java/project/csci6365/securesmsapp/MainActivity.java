package project.csci6365.securesmsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import android.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static SharedPreferences sharedPreferences;
    private String userid;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    public static List<String> dataset;
    public static JSONObject userListJSON;
    public static JSONObject messagesJSON;
    public static MyAdapter adapter;

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
        // Create the connection with the server

        welcome.setText(getString(R.string.welcome, userid));

        userListJSON = new JSONObject();
        dataset = new ArrayList<>();

        if (sharedPreferences.contains("userlist")) {
            try {
                userListJSON = new JSONObject(sharedPreferences.getString("userlist", null));
                Iterator<String> keys = userListJSON.keys();
                while (keys.hasNext())
                    dataset.add(keys.next());
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                Toast.makeText(this, "Unable to retrieve user list", Toast.LENGTH_SHORT).show();
            }
        }

        adapter = new MyAdapter(dataset);
        LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        addUser.setOnClickListener(e -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_add_user);
            EditText userid_to_add = dialog.findViewById(R.id.userid);
            Button addUser = dialog.findViewById(R.id.add_user);

            addUser.setOnClickListener(f -> {
                if (!userid_to_add.getText().toString().equals("")) {
                    String user = userid_to_add.getText().toString();

                    // TODO check with server that user id is legit and receive public key if it is

                    String userPublicKey = "";

                    try {
                        userListJSON.put(user, userPublicKey);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userlist",userListJSON.toString());
                        editor.apply();

                        dataset.add(userid_to_add.getText().toString());
                        adapter.notifyDataSetChanged();
                    } catch (JSONException g) {
                        Toast.makeText(this, "Unable to add user", Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                }
            });

            dialog.show();
        });

        // TODO   listen for messages
        // TODO   receive sender and message

        // If we get message
        String sending_user = "person01";
        String message = "something encrypted";
        int position = -1;
        if (dataset.contains(sending_user)) {
            for (int i = 0; i < dataset.size(); i++) {
                if (dataset.get(i).equals(sending_user)) {
                    position = i;
                    break;
                }
            }
            adapter = new MyAdapter(dataset, position);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            position = dataset.size();
            dataset.add(sending_user);
            try {
                userListJSON.put(sending_user, "user");
            } catch (JSONException e) {
                Toast.makeText(this, "Unable to save user", Toast.LENGTH_LONG).show();
            }
            adapter = new MyAdapter(dataset, position);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        // After decrypting message

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 100) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            userid = data.getStringExtra("userid");
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

            // TODO send server the public key
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(this, "Unable to generate RSA keys", Toast.LENGTH_LONG).show();
        }
    }

    private void updateUserList() {

    }
}
