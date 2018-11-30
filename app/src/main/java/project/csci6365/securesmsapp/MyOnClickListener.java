package project.csci6365.securesmsapp;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class MyOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        TextView userid1 = view.findViewById(R.id.userid);
        TextView newMesssage = view.findViewById(R.id.new_message);

        Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.dialog);
        TextView userid2 = dialog.findViewById(R.id.userid);
        Button messageBtn = dialog.findViewById(R.id.button1);
        Button readBtn = dialog.findViewById(R.id.button3);
        Button removeBtn = dialog.findViewById(R.id.button2);

        userid2.setText(userid1.getText());

        if (newMesssage.getVisibility() == View.INVISIBLE) {
            readBtn.setEnabled(false);
            readBtn.setBackgroundColor(Color.GRAY);
        }

        messageBtn.setOnClickListener(e -> {
            Dialog dialog2 = new Dialog(view.getContext());
            dialog2.setContentView(R.layout.dialog_message_user);
            TextView title = dialog2.findViewById(R.id.userid);
            title.setText(view.getContext().getString(R.string.messageTo, userid1.getText().toString()));
            EditText messageET = dialog2.findViewById(R.id.message);
            Button send = dialog2.findViewById(R.id.send);

            send.setOnClickListener(f -> {
                String receiver = userid1.getText().toString();
                String message = messageET.getText().toString();

                Hash hasher = new Hash(message);
                String hash = hasher.getHash();

                String fullMessage = message + hash;

                try {
                    String publicKeyString = MainActivity.userListJSON.getString(receiver);
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(Base64.decode(publicKeyString, Base64.DEFAULT)));
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                    byte[] cipherText = cipher.doFinal(fullMessage.getBytes());
                    String cipherTextString = Base64.encodeToString(cipherText, Base64.DEFAULT);
                    Connect cc = new Connect();
                    cc.get_ip();
                    cc.send_message(MainActivity.userid, userid2.getText().toString(), cipherTextString);
                } catch (JSONException | NoSuchAlgorithmException | NoSuchPaddingException |
                        InvalidKeySpecException | InvalidKeyException | BadPaddingException |
                        IllegalBlockSizeException e1) {
                    Toast.makeText(view.getContext(), "Unable to encrypt message", Toast.LENGTH_LONG).show();
                }

                dialog2.dismiss();
            });

            dialog.dismiss();
            dialog2.show();
        });

        readBtn.setOnClickListener(e -> {
            String sender = userid1.getText().toString();
            try {
                String messagesString = MainActivity.messagesJSON.getString(sender);
                messagesString = messagesString.replaceAll("([\\[\\]\n\\s])", "");
                String[] messagesArray = messagesString.split(",");
                ArrayList<String> messagesArrayList = new ArrayList<>(Arrays.asList(messagesArray));

                readMessage(view, messagesArrayList);

                readBtn.setEnabled(false);
                readBtn.setBackgroundColor(Color.GRAY);

                MainActivity.messagesJSON.remove(sender);
                for (int i = 0; i < MainActivity.dataSet.size(); i++) {
                    if (MainActivity.dataSet.get(i).equals(sender)) {
                        System.out.println("POSITION: " + i);
                        MainActivity.positions.removeAll(Collections.singleton(i));
                    }
                }

                SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
                editor.putString("messages", MainActivity.messagesJSON.toString());
                editor.apply();

                MainActivity.adapter = new MyAdapter(MainActivity.dataSet, MainActivity.positions);
                MainActivity.recyclerView.setAdapter(MainActivity.adapter);
                MainActivity.adapter.notifyDataSetChanged();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        });

        removeBtn.setOnClickListener(e -> {
            // Remove user from list
            MainActivity.dataSet.remove(userid1.getText().toString());
            MainActivity.userListJSON.remove(userid1.getText().toString());
            SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
            editor.putString("userList",MainActivity.userListJSON.toString());
            editor.apply();
            MainActivity.adapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void readMessage(View view, ArrayList<String> messagesArrayList) {
        Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.dialog_message);
        TextView messageTextView = dialog.findViewById(R.id.message);
        Button done = dialog.findViewById(R.id.button);

        System.out.println(messagesArrayList.size());
        for (String s : messagesArrayList) {
            System.out.println(s);
            System.out.println(new String(Base64.decode(s, Base64.DEFAULT)));
        }

        String message = messagesArrayList.get(0);
        messagesArrayList.remove(message);
        message = new String(Base64.decode(message, Base64.DEFAULT));
        messageTextView.setText(message);

        System.out.println("Second one: " + messagesArrayList.size());

        done.setOnClickListener(e -> {
            dialog.dismiss();
            if (messagesArrayList.size() != 0) {
                readMessage(view, messagesArrayList);
            }
        });

        dialog.show();
    }
}
