package project.csci6365.securesmsapp;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MyOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        TextView userid1 = view.findViewById(R.id.userid);

        Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.dialog);
        TextView userid2 = dialog.findViewById(R.id.userid);
        Button messageBtn = dialog.findViewById(R.id.button1);
        Button removeBtn = dialog.findViewById(R.id.button2);

        userid2.setText(userid1.getText());

        messageBtn.setOnClickListener(e -> {
            Dialog dialog2 = new Dialog(view.getContext());
            dialog2.setContentView(R.layout.dialog_message_user);
            EditText message = dialog2.findViewById(R.id.message);
            Button send = dialog2.findViewById(R.id.send);

            send.setOnClickListener(f -> {
                // Send the message to the server
                dialog2.dismiss();
            });

            dialog.dismiss();
            dialog2.show();
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
