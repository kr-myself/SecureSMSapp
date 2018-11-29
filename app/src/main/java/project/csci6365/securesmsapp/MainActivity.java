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
import android.transition.CircularPropagation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.InvalidKeyException;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import android.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static SharedPreferences sharedPreferences;
    private String userid;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    public static List<String> dataSet;             // List of users to display
    public static JSONObject userListJSON;          // List of users and public keys
    public static JSONObject messagesJSON;          // List of users and unread messages
    public static ArrayList<Integer> positions;
    public static MyAdapter adapter;

    @BindView(R.id.welcome) TextView welcome;
    @BindView(R.id.add_user) Button addUser;
    public static RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView = findViewById(R.id.recyclerView);

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
            try {
                onLogin();
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | JSONException | IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        } else {
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivityForResult(i, 100);
        }
    }

    protected void onLogin() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, JSONException {
        // Create the connection with the server

        welcome.setText(getString(R.string.welcome, userid));

        userListJSON = new JSONObject();
        dataSet = new ArrayList<>();
        positions = new ArrayList<>();

        if (sharedPreferences.contains("userList")) {
            try {
                userListJSON = new JSONObject(sharedPreferences.getString("userList", null));
                Iterator<String> keys = userListJSON.keys();
                while (keys.hasNext())
                    dataSet.add(keys.next());
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                Toast.makeText(this, "Unable to retrieve user list", Toast.LENGTH_SHORT).show();
            }
        }

        messagesJSON = new JSONObject();

        if (sharedPreferences.contains("messages")) {
            try {
                messagesJSON = new JSONObject(sharedPreferences.getString("messages", null));
                Iterator<String> senders = messagesJSON.keys();
                while (senders.hasNext()) {
                    String sender = senders.next();
                    for (int i = 0; i < dataSet.size(); i++) {
                        if (dataSet.get(i).equals(sender)) {
                            positions.add(i);
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(this, "Error retrieving unread message", Toast.LENGTH_SHORT).show();
            }
        }

        adapter = new MyAdapter(dataSet, positions);
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
                        editor.putString("userList",userListJSON.toString());
                        editor.apply();

                        dataSet.add(userid_to_add.getText().toString());
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
        // TODO   receive sender, message, and sender's public key

        /*
        String message = Base64.encodeToString("second message".getBytes(), Base64.DEFAULT);
        String sender = "jimothy";
        if (dataSet.contains(sender)) {
            for (int i = 0; i < dataSet.size(); i++) {
                if (dataSet.get(i).equals(sender)) {
                    positions.add(i);
                }
            }
            if (messagesJSON.has(sender)) {
                try {
                    String messagesString = messagesJSON.getString(sender);
                    messagesString = messagesString.replaceAll("([\\[\\]\n\\s])", "");
                    String[] messagesArray = messagesString.split(",");
                    ArrayList<String> messagesArrayList = new ArrayList<>(Arrays.asList(messagesArray));
                    messagesArrayList.add(message);
                    messagesArray = messagesArrayList.toArray(new String[0]);
                    messagesString = Arrays.toString(messagesArray);
                    messagesJSON.put(sender, messagesString);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("messages", messagesJSON.toString());
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                String[] messagesArray = {message};
                String messagesString = Arrays.toString(messagesArray);
                try {
                    messagesJSON.put(sender, messagesString);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("messages", messagesJSON.toString());
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            positions.add(dataSet.size());
            dataSet.add(sender);
            userListJSON.put(sender, "");

            String[] messagesArray = {message};
            String messagesString = Arrays.toString(messagesArray);
            try {
                messagesJSON.put(sender, messagesString);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userList", userListJSON.toString());
                editor.putString("messages", messagesJSON.toString());
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        /* THIS SHOULD RUN AFTER RECEIVING A MESSAGE FROM THE SERVER AND PARSING IT
        String sender = "";
        PublicKey senderPublicKey;
        String encryptedMessage = "";
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedMessageBytes = cipher.doFinal(encryptedMessage.getBytes());
        String decryptedMessage = new String(decryptedMessageBytes);
        String originalMessage = decryptedMessage.substring(0, decryptedMessage.length() - 32);
        String hashMessage = decryptedMessage.substring(decryptedMessage.length() - 32, decryptedMessage.length());

        Hash hasher = new Hash(originalMessage);
        String hash = hasher.getHash();

        if (!hash.equals(hashMessage)) {
            // ALERT BOTH USERS THAT THE MESSAGE WAS TAMPERED WITH.
            // Still save user and public key
        } else {
            saveMessage(originalMessage, sender, publicKey);
        }

        adapter = new MyAdapter(dataSet, positions);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 100) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            userid = data.getStringExtra("userid");
            editor.putString("userid", data.getStringExtra("userid"));
            editor.apply();
            generateRSAKeys();
            try {
                onLogin();
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | JSONException | IllegalBlockSizeException e) {
                e.printStackTrace();
            }
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

    private void saveMessage(String message, String sender, PublicKey senderPublicKey) throws JSONException {
        message = Base64.encodeToString(message.getBytes(), Base64.DEFAULT);
        if (dataSet.contains(sender)) {
            for (int i = 0; i < dataSet.size(); i++) {
                if (dataSet.get(i).equals(sender)) {
                    positions.add(i);
                }
            }
            if (messagesJSON.has(sender)) {
                try {
                    String messagesString = messagesJSON.getString(sender);
                    messagesString = messagesString.replaceAll("([\\[\\]\n\\s])", "");
                    String[] messagesArray = messagesString.split(",");
                    ArrayList<String> messagesArrayList = new ArrayList<>(Arrays.asList(messagesArray));
                    messagesArrayList.add(message);
                    messagesArray = messagesArrayList.toArray(new String[0]);
                    messagesString = Arrays.toString(messagesArray);
                    messagesJSON.put(sender, messagesString);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("messages", messagesJSON.toString());
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                String[] messagesArray = {message};
                String messagesString = Arrays.toString(messagesArray);
                try {
                    messagesJSON.put(sender, messagesString);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("messages", messagesJSON.toString());
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            positions.add(dataSet.size());
            dataSet.add(sender);
            userListJSON.put(sender, Base64.encodeToString(senderPublicKey.getEncoded(), Base64.DEFAULT));

            String[] messagesArray = {message};
            String messagesString = Arrays.toString(messagesArray);
            try {
                messagesJSON.put(sender, messagesString);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userList", userListJSON.toString());
                editor.putString("messages", messagesJSON.toString());
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
