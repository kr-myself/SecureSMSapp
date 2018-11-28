package project.csci6365.securesmsapp;

import android.app.Dialog;
import android.content.SharedPreferences;
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
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class MyOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        TextView userid1 = view.findViewById(R.id.userid);

        Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.dialog);
        TextView userid2 = dialog.findViewById(R.id.userid);
        Button messageBtn = dialog.findViewById(R.id.button1);
        Button readBtn = dialog.findViewById(R.id.new_message);
        Button removeBtn = dialog.findViewById(R.id.button2);

        userid2.setText(userid1.getText());

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

                    // TODO send cipherText to the server

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

        });

        removeBtn.setOnClickListener(e -> {
            // Remove user from list
            MainActivity.dataset.remove(userid1.getText().toString());
            MainActivity.userListJSON.remove(userid1.getText().toString());
            SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
            editor.putString("userlist",MainActivity.userListJSON.toString());
            editor.apply();
            MainActivity.adapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        dialog.show();
    }
}
