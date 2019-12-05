package com.yjm.applauncher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.yjm.applauncher.Model.AppList;
import com.yjm.applauncher.Model.UStats;
import com.yjm.applauncher.R;
import com.yjm.applauncher.Services.RecentAppBtnService;
import com.yjm.applauncher.passwordDialog;
import com.yjm.applauncher.utilities.Constants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class ScreenActivity extends AppCompatActivity implements passwordDialog.EnterPasswordListener{

    List<AppList> list_app;
    ArrayList<String> applist;
    RecyclerView recyclerView;
    private LinearLayout main_layout;
    private TextView select_launcher, select_launcher_description;
    private RecentAppBtnService r;
    private Boolean flag_item_clicked = false;
    boolean currentFocus;

    // To keep track of activity's foreground/background status
    boolean isPaused;

    Handler collapseNotificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_screen);

//        Intent intent_service= new Intent(this, RecentAppBtnService.class);
//        bindService(intent_service, ScreenActivity.this, Context.BIND_AUTO_CREATE);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_id);
        main_layout = findViewById(R.id.main_layout);
        select_launcher = findViewById(R.id.select_launcher);
        select_launcher_description = findViewById(R.id.select_launcher_description);
        Bitmap bitmap = BitmapFactory.decodeFile(Constants.Background_file_path);
        Drawable drawable = new BitmapDrawable(bitmap);
        main_layout.setBackground(drawable);
        select_launcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_item_clicked = true;

                resetPreferredLauncherAndOpenChooser(ScreenActivity.this);



            }
        });
//        Toast.makeText(getApplicationContext(),"Now is " + isMyAppLauncherDefault(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(),"Flag is " + Constants.flag_setting, Toast.LENGTH_SHORT).show();
        if(Constants.flag_setting == false) {
            Intent intent = new Intent(ScreenActivity.this, MainActivity.class);
            finish();
            startActivity(intent);

        }
        else {



            if(!isMyAppLauncherDefault()){
                select_launcher_description.setVisibility(View.VISIBLE);
                select_launcher.setVisibility(View.VISIBLE);

            }
            else {

                select_launcher_description.setVisibility(View.GONE);
                select_launcher.setVisibility(View.GONE);
            }
            initUI();




//
//            final Handler h = new Handler();
//            h.postDelayed(new Runnable()
//            {
//                private long time = 0;
//
//                @Override
//                public void run()
//                {
//
//                    if(!isMyAppLauncherDefault()){
//
//                    }
//                    else {
////                        Toast.makeText(getApplicationContext(),"Deleted!", Toast.LENGTH_SHORT).show();
//                        View myView = findViewById(R.id.select_launcher);
//                        ViewGroup parent = (ViewGroup) myView.getParent();
//                        parent.removeView(myView);
//                        select_launcher_description.setVisibility(View.GONE);
//                        select_launcher.setVisibility(View.GONE);
//                    }
//
//
//
//                }
//            }, 1000); // 1 second delay (takes millis)



        }


        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


    }

    private boolean isMyAppLauncherDefault() {

        PackageManager localPackageManager = getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        String str = localPackageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
        return str.equals(getPackageName());
    }

    public static void resetPreferredLauncherAndOpenChooser(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, FakeActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(selector);

        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);


    }


    public void initUI(){


        applist = Constants.apps;
//            applist = (ArrayList<String>) getIntent().getSerializableExtra("apps");
        list_app = new ArrayList<>();
        for(int i = 0 ; i < applist.size() ; i ++){
            String package_name = applist.get(i);

            Drawable icon = null;
            try {
                icon = getPackageManager().getApplicationIcon(package_name);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


            final PackageManager pm = getApplicationContext().getPackageManager();
            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo( package_name, 0);
            } catch (final PackageManager.NameNotFoundException e) {
                ai = null;
            }
            final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
            list_app.add(new AppList(applicationName, icon, package_name));


        }


        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, list_app, new RecyclerViewAdapter.OnItemClickListener() {
            @Override public void onItemClick(AppList item) {

                flag_item_clicked = true;

                Intent intent = getPackageManager()
                        .getLaunchIntentForPackage(item.getPackages());

                startActivity(intent);


            }
        });
        if (recyclerView != null){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
            recyclerView.setAdapter(recyclerViewAdapter);

            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                Handler handler = new Handler();

                int numberOfTaps = 0;
                long lastTapTimeMs = 0;
                long touchDownMs = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touchDownMs = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_UP:
                            handler.removeCallbacksAndMessages(null);

                            if ((System.currentTimeMillis() - touchDownMs) > ViewConfiguration.getTapTimeout()) {
                                //it was not a tap

                                numberOfTaps = 0;
                                lastTapTimeMs = 0;
                                break;
                            }

                            if (numberOfTaps > 0
                                    && (System.currentTimeMillis() - lastTapTimeMs) < ViewConfiguration.getDoubleTapTimeout()) {
                                numberOfTaps += 1;
                            } else {

                                numberOfTaps = 1;
                            }

                            lastTapTimeMs = System.currentTimeMillis();

                            if (numberOfTaps == 5) {

                                openDialog();

                            } else if (numberOfTaps == 2) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //handle double tap
                                        Toast.makeText(getApplicationContext(), "double", Toast.LENGTH_SHORT).show();
                                    }
                                }, ViewConfiguration.getDoubleTapTimeout());


                            }
                    }

                    return true;
                }
            });
        }


        if(!isMyAppLauncherDefault()){
            select_launcher_description.setVisibility(View.VISIBLE);
            select_launcher.setVisibility(View.VISIBLE);
        }
        else {

            select_launcher_description.setVisibility(View.GONE);
            select_launcher.setVisibility(View.GONE);
        }


    }


    private void openDialog(){

        passwordDialog password_Dialog = new passwordDialog();
        password_Dialog.show(getSupportFragmentManager(),"Password");

    }

    @Override
    protected void onStart() {
        super.onStart();
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        if(!isMyAppLauncherDefault()){
            select_launcher_description.setVisibility(View.VISIBLE);
            select_launcher.setVisibility(View.VISIBLE);
        }
        else {

            select_launcher_description.setVisibility(View.GONE);
            select_launcher.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        flag_item_clicked = false;
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        if(!isMyAppLauncherDefault()){
            select_launcher_description.setVisibility(View.VISIBLE);
            select_launcher.setVisibility(View.VISIBLE);
        }
        else {

            select_launcher_description.setVisibility(View.GONE);
            select_launcher.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Do nothing or catch the keys you want to block
        return false;
    }

    @Override
    public void applyTexts(String password) {
        if (password.trim().equals(Constants.password)){
            Intent intent = new Intent(ScreenActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(getApplicationContext(),"Wrong password!", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus) {
            currentFocus = hasFocus;
            collapseNow();
//            windowCloseHandler.postDelayed(windowCloserRunnable, 250);
            if(flag_item_clicked == false ){
//                Toast.makeText(getApplicationContext(),"This Phone is locked!" + "\t" + "\t" + "com.android.systemui"+ "\t" + "com.android.systemui.recents.RecentsActivity", Toast.LENGTH_LONG).show();
                ActivityManager activityManager = (ActivityManager) getApplicationContext()
                        .getSystemService(Context.ACTIVITY_SERVICE);

                activityManager.moveTaskToFront(getTaskId(), 0);
            }

//            Context mContext = ScreenActivity.this;
//            if (UStats.getUsageStatsList(this).isEmpty()) {
//                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//                startActivity(intent);
//            }
//
//            if(UStats.printUsageStatus(mContext).equals("com.android.systemui")){
//
//                    if(UStats.getStats(this).equals("com.android.systemui.recents.RecentsActivity")){
//                        if(flag_item_clicked == false ){
//                            Toast.makeText(getApplicationContext(),"This Phone is locked!" + "\t" + "\t" + "com.android.systemui"+ "\t" + "com.android.systemui.recents.RecentsActivity", Toast.LENGTH_LONG).show();
//                            ActivityManager activityManager = (ActivityManager) getApplicationContext()
//                                    .getSystemService(Context.ACTIVITY_SERVICE);
//
//                            activityManager.moveTaskToFront(getTaskId(), 0);
//                        }
//
//                    }
//
//
//
//            }



        }
    }

//    private void toggleRecents() {
//        try{
//            Intent closeRecents = new Intent("com.android.systemui.RecentsComponent");
//            closeRecents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//            ComponentName recents = new ComponentName("com.android.systemui", "com.android.systemui.recents.RecentsActivity");
//            closeRecents.setComponent(recents);
//            this.startActivity(closeRecents);
//        }
//        catch (Exception e){
//
//            Log.d("TAG", "Pkg____)))): " + e + "\t");
//
//        }
//
//    }
//
//    private Handler windowCloseHandler = new Handler();
//    private Runnable windowCloserRunnable = new Runnable() {
//        @Override
//        public void run() {
//
//            ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
//
//            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//
//            if (cn != null && cn.getClassName().equals("com.android.systemui.recent.RecentsActivity")) {
//                toggleRecents();
//            }
//            else {
//                Toast.makeText(ScreenActivity.this, "Now is ___ "+cn.getClassName() , Toast.LENGTH_SHORT).show();
//
//            }
//
//
//        }
//    };

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Activity's been resumed
        isPaused = false;
        if(!isMyAppLauncherDefault()){
            select_launcher_description.setVisibility(View.VISIBLE);
            select_launcher.setVisibility(View.VISIBLE);
        }
        else {

            select_launcher_description.setVisibility(View.GONE);
            select_launcher.setVisibility(View.GONE);
            flag_item_clicked = false;
        }


    }


    public void collapseNow() {

        // Initialize 'collapseNotificationHandler'
        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = new Handler();
        }

        if (!currentFocus && !isPaused) {

            // Post a Runnable with some delay - currently set to 300 ms
            collapseNotificationHandler.postDelayed(new Runnable() {

                class Method {
                }

                @Override
                public void run() {

                    // Use reflection to trigger a method from 'StatusBarManager'

                    @SuppressLint("WrongConstant") Object statusBarService = getSystemService("statusbar");
                    Class<?> statusBarManager = null;

                    try {
                        statusBarManager = Class.forName("android.app.StatusBarManager");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    java.lang.reflect.Method collapseStatusBar = null;

                    try {

                        // Prior to API 17, the method to call is 'collapse()'
                        // API 17 onwards, the method to call is `collapsePanels()`

                        if (Build.VERSION.SDK_INT > 16) {
                            collapseStatusBar = statusBarManager .getMethod("collapsePanels");
                        } else {
                            collapseStatusBar = statusBarManager .getMethod("collapse");
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                    collapseStatusBar.setAccessible(true);

                    try {
                        collapseStatusBar.invoke(statusBarService);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    if (!currentFocus && !isPaused) {
                        collapseNotificationHandler.postDelayed(this, 100L);
                    }

                }
            }, 300L);
        }
    }





}
