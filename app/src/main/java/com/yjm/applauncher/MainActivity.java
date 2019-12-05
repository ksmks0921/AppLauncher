package com.yjm.applauncher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yjm.applauncher.utilities.Constants;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button add, cancel, set_background_image, change_password;
    private Intent intent ;
    private static final int SELECTED_PIC = 1;
    private LinearLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        init_ui();
        background = findViewById(R.id.backgroud);



    }

    private void init_ui(){
        setContentView(R.layout.activity_main);


        add = (Button) findViewById(R.id.add);
        cancel =(Button) findViewById(R.id.cancel);
        set_background_image = (Button) findViewById(R.id.add_background);
        change_password = findViewById(R.id.change_password);





        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
//                PackageManager packageManager = getPackageManager();
//                Intent i = new Intent();
//                i.addCategory(Intent.CATEGORY_HOME);
//                i.setAction(Intent.ACTION_MAIN);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.addCategory(Intent.CATEGORY_MONKEY);
//                List<ResolveInfo> queryIntentActivities = packageManager
//                        .queryIntentActivities(i,0);
//
//                ResolveInfo resolveInfo = queryIntentActivities.get(0);
//                String packageName = resolveInfo.activityInfo.packageName;
//                String className = resolveInfo.activityInfo.targetActivity;
//
//                Toast.makeText(getApplicationContext(),packageName, Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(),className, Toast.LENGTH_SHORT).show();
//
//                if(className != null){
//                    Intent res = new Intent();
//
//                    res.setComponent(new ComponentName(packageName,className));
//                    startActivity(res);
//                }
//                else {
//                    finish();
//                }

                resetPreferredLauncherAndOpenChooser(MainActivity.this);

            }
        });

        set_background_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(),"Now is " + isReadStoragePermissionGranted(), Toast.LENGTH_SHORT).show();
                if(isReadStoragePermissionGranted()){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECTED_PIC);
                }
                else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[] {
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            }, 3);
                }

            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangePasword.class);
                startActivity(intent);
            }
        });
    }
    private boolean isMyAppLauncherDefault() {

        PackageManager localPackageManager = getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        String str = localPackageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
        return str.equals(getPackageName());
    }

    //    protected void onActivityResult(int request)
    public static void resetPreferredLauncherAndOpenChooser(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, FakeActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(selector);

        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case SELECTED_PIC:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filepath = cursor.getString(columnIndex);
                    Constants.Background_file_path = filepath;
                    cursor.close();

                }
                break;


            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Tag","Permission is granted1");
                return true;
            } else {

                Log.v("Tag","Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Tag","Permission is granted1");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
//                Log.d("TAG", "External storage2");
//                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
//                    Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
//                    //resume tasks needing this permission
//                    downloadPdfFile();
//                }else{
//                    progress.dismiss();
//                }
                break;

            case 3:
//                Log.d("TAG", "External storage1");
//                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
//                    Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
//                    //resume tasks needing this permission
//                    SharePdfFile();
//                }else{
//                    progress.dismiss();
//                }
                break;
        }
    }
}