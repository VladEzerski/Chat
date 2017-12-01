package com.ezerski.vladislav.firebasechat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<Message> adapter;
    RelativeLayout rv;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RelativeLayout) findViewById(R.id.relative_layout);
        btn = (Button) findViewById(R.id.btn_send);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.editText);
                FirebaseDatabase.getInstance().getReference().push().
                        setValue(new Message(input.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),
                    SIGN_IN_REQUEST_CODE);
        } else {
            displayChat();
        }
    }

    private void displayChat(){

        ListView listMessage = (ListView) findViewById(R.id.listView);
        adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.item, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, Message model, int position) {

                ((TextView)v.findViewById(R.id.tvMessage)).setText(model.getTextMessage());
                ((TextView)v.findViewById(R.id.tvUser)).setText(model.getAuthor());
                ((TextView)v.findViewById(R.id.tvTime)).setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",model.getTimeMessage()));
            }
        };
        listMessage.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show();
                displayChat();
            } else {
                Toast.makeText(this, "Вход не выполнен", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_signout){
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Выход выполнен", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        return true;
    }
}
