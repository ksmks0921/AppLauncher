package com.yjm.applauncher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yjm.applauncher.utilities.Constants;

public class ChangePasword extends AppCompatActivity {

    private EditText old, new_one, new_again;
    private Button change_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pasword);


        old = findViewById(R.id.old_one);
        new_one = findViewById(R.id.new_one);
        new_again = findViewById(R.id.new_again);

        change_save = findViewById(R.id.change_save);

        change_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(old.getText().toString().equals(Constants.password)){
                    if(new_one.getText().toString().equals(new_again.getText().toString())){
//                        Toast.makeText(getApplicationContext(),"Successfully saved new password", Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChangePasword.this);

                        alertDialogBuilder.setTitle("Are you sure to change password?");
                        alertDialogBuilder.setCancelable(true);

                        alertDialogBuilder.setPositiveButton( "OK",
                                new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        SharedPreferences prefs = getSharedPreferences( Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("password", new_one.getText().toString());
                                        editor.commit();

                                        Toast.makeText(getApplicationContext(),"Successfully saved new password", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                } );
                        alertDialogBuilder.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                    else {

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ChangePasword.this);
                        builder1.setMessage("Please confirm your password is correct.");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                }
                else {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(ChangePasword.this);
                    builder2.setMessage("Please confirm your old password is correct.");
                    builder2.setCancelable(true);

                    builder2.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder2.create();
                    alert11.show();
                }
            }
        });

    }
}
