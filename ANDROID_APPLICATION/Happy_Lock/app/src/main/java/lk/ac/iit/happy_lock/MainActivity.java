package lk.ac.iit.happy_lock;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mapzen.speakerbox.Speakerbox;
import com.nightonke.blurlockview.BlurLockView;
import com.nightonke.blurlockview.Directions.HideType;
import com.nightonke.blurlockview.Directions.ShowType;
import com.nightonke.blurlockview.Eases.EaseType;
import com.nightonke.blurlockview.Password;
import com.pusher.pushnotifications.PushNotifications;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        BlurLockView.OnPasswordInputListener,
        BlurLockView.OnLeftButtonClickListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private Menu menu;

    Handler handler = new Handler();
    Handler handlerIcon = new Handler();

    // private static int CODE_AUTHENTICATION_VERIFICATION = 241;
    int checkOnline = 1;
    String passCode = "1234";
    boolean isLoginSuccessfully = false;
    BlurLockView blurLockView;
    private ImageView imageView1;

    Speakerbox speakerbox;

    BottomNavigationViewEx navigation ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isOnline();


        PushNotifications.start(getApplicationContext(), "98f86d7e-a81e-4974-bc64-31e4530bcdea");
        PushNotifications.subscribe("hello");



        handler.postDelayed(updateStatus, 0);

        blurLockView = findViewById(R.id.blurlockview);
        imageView1 = findViewById(R.id.imageBack);

        navigation = findViewById(R.id.bottomNavigation);
        navigation.enableAnimation(true);
        navigation.enableShiftingMode(false);
        navigation.enableItemShiftingMode(false);

        getPassCode();

        // Set the view that need to be blurred
        blurLockView.setBlurredView(imageView1);

        // Set the password
        blurLockView.setCorrectPassword(passCode);

        blurLockView.setTitle("ENTER PASS CODE");
        blurLockView.setLeftButton("");
        blurLockView.setRightButton("");
        blurLockView.setTypeface(getTypeface());
        blurLockView.setType(getPasswordType(), false);

        blurLockView.setOnLeftButtonClickListener(this);
        blurLockView.setOnPasswordInputListener(this);
        blurLockView.setBlurRadius(1);
        //blurLockView.setOverlayColor(Color.parseColor("#35A753"));

        speakerbox = new Speakerbox(getApplication());
        speakerbox.play("enter pass code.");

//        // Authentication - pattern or password or pin or fingerprint
//        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//        if (km.isKeyguardSecure()) {
//
//            Intent i = km.createConfirmDeviceCredentialIntent("Happy Lock Authentication", "Draw your pattern lock");
//            startActivityForResult(i, CODE_AUTHENTICATION_VERIFICATION);
//        } else {
//            Toast.makeText(this, "No any security setup done by user(pattern or password or pin or fingerprint", Toast.LENGTH_LONG).show();
//            // System.exit(0);
//        }

//        if(isLoginSuccessfully){
//            loadActivity();
//        }else {
//
//        }
    }

    protected void loadActivity() {
        //loading the default fragment
        loadFragment(new HomeActivity());

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationViewEx.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeActivity();
                        break;

                    case R.id.action_door_lock:
                        fragment = new DoorControlFragment();
                        break;

                    case R.id.action_light:
                        fragment = new BulbsControlFragment();
                        break;

                    case R.id.action_location:
                        fragment = new LoctionFragment();
                        break;
                    case R.id.action_door_history:
                        fragment = new DoorHistoryFragment();
                        break;
                }

                return loadFragment(fragment);
            }


        });

    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .commit();
            return true;
        }
        return false;

    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        // MenuInflater mainMenu = new MenuInflater(this);
        //  mainMenu.inflate(R.menu.mainmenu, menu)
        getMenuInflater().inflate(R.menu.mainmenu, menu);

        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.home_menu:
                fragment = new HomeActivity();
                break;
            case R.id.voice_menu:
                fragment = new HomeActivity();
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                // launch settings activity
               // startActivity(new Intent(MainActivity.this, SettingsPrefActivity.class));
                //fragment = new HomeActivity();
                break;
            case R.id.about_menu:
                fragment = new AboutFragment();
                break;
            case R.id.exit_menu:
                System.exit(0);
                break;
        }
        loadFragment(fragment);
        return true;
    }

    private Runnable updateStatus = new Runnable() {
        @Override
        public void run() {
            try {
                checkHappyLockIsOnline();
            } catch (Exception e) {

            }
            //   handler.postDelayed(this, 000);
        }
    };

    void checkHappyLockIsOnline() {
        DatabaseReference myRef = database.getReference("ONLINE");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                checkOnline = dataSnapshot.getValue(Integer.class);

                if (menu != null) {
                    if (checkOnline == 1) {
                        MenuItem item = menu.findItem(R.id.onlineStatus);
                        if (item != null) {
                            item.setIcon(R.drawable.online_icon);
                        }
                        handlerIcon.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                DatabaseReference myRef = database.getReference("ONLINE");
                                myRef.setValue(0);
                            }
                        }, 1000);

                    } else {
                        MenuItem item = menu.findItem(R.id.onlineStatus);
                        if (item != null) {
                            item.setIcon(R.drawable.offline_icon);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void checkNetworkConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection and try again");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isOnline();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if (isConnected) {
            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
            return true;
        } else {
            checkNetworkConnection();
            Toast.makeText(this, "Not Connected", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    void getPassCode() {
        DatabaseReference myRef = database.getReference("PASS_CODE");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                passCode = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onClick() {

    }

    private Password getPasswordType() {
        if ("PASSWORD_NUMBER".equals(getIntent().getStringExtra("PASSWORD_TYPE")))
            return Password.NUMBER;
        else if ("PASSWORD_NUMBER".equals(getIntent().getStringExtra("PASSWORD_TYPE")))
            return Password.TEXT;
        return Password.NUMBER;
    }

    private Typeface getTypeface() {
        if ("SAN".equals(getIntent().getStringExtra("TYPEFACE")))
            return Typeface.createFromAsset(getAssets(), "fonts/San Francisco Regular.ttf");
        else if ("DEFAULT".equals(getIntent().getStringExtra("TYPEFACE")))
            return Typeface.DEFAULT;
        return Typeface.DEFAULT;
    }

    @Override
    public void correct(String inputPassword) {
        loadActivity();
        isLoginSuccessfully = true;
        //Toast.makeText(this, "Happy Lock Authentication", Toast.LENGTH_LONG).show();
        blurLockView.hide(
                getIntent().getIntExtra("HIDE_DURATION", 1000),
                getHideType(getIntent().getIntExtra("HIDE_DIRECTION", 2)),
                getEaseType(getIntent().getIntExtra("HIDE_EASE_TYPE", 30)));
        speakerbox.play("Welcome to happy lock.");
    }

    @Override
    public void incorrect(String inputPassword) {
        blurLockView.show(
                getIntent().getIntExtra("SHOW_DURATION", 400),
                getShowType(getIntent().getIntExtra("SHOW_DIRECTION", 4)),
                getEaseType(getIntent().getIntExtra("SHOW_EASE_TYPE", 30)));
        Toast.makeText(this, "Please enter correct pass code.", Toast.LENGTH_LONG).show();
        speakerbox.play("Wrong pass code.");
    }

    @Override
    public void input(String inputPassword) {

    }

    private ShowType getShowType(int p) {
        ShowType showType = ShowType.FROM_TOP_TO_BOTTOM;
        switch (p) {
            case 0:
                showType = ShowType.FROM_TOP_TO_BOTTOM;
                break;
            case 1:
                showType = ShowType.FROM_RIGHT_TO_LEFT;
                break;
            case 2:
                showType = ShowType.FROM_BOTTOM_TO_TOP;
                break;
            case 3:
                showType = ShowType.FROM_LEFT_TO_RIGHT;
                break;
            case 4:
                showType = ShowType.FADE_IN;
                break;
        }
        return showType;
    }

    private HideType getHideType(int p) {
        HideType hideType = HideType.FROM_TOP_TO_BOTTOM;
        switch (p) {
            case 0:
                hideType = HideType.FROM_TOP_TO_BOTTOM;
                break;
            case 1:
                hideType = HideType.FROM_RIGHT_TO_LEFT;
                break;
            case 2:
                hideType = HideType.FROM_BOTTOM_TO_TOP;
                break;
            case 3:
                hideType = HideType.FROM_LEFT_TO_RIGHT;
                break;
            case 4:
                hideType = HideType.FADE_OUT;
                break;
        }
        return hideType;
    }

    private EaseType getEaseType(int p) {
        EaseType easeType = EaseType.Linear;
        switch (p) {
            case 0:
                easeType = EaseType.EaseInSine;
                break;
            case 1:
                easeType = EaseType.EaseOutSine;
                break;
            case 2:
                easeType = EaseType.EaseInOutSine;
                break;
            case 3:
                easeType = EaseType.EaseInQuad;
                break;
            case 4:
                easeType = EaseType.EaseOutQuad;
                break;
            case 5:
                easeType = EaseType.EaseInOutQuad;
                break;
            case 6:
                easeType = EaseType.EaseInCubic;
                break;
            case 7:
                easeType = EaseType.EaseOutCubic;
                break;
            case 8:
                easeType = EaseType.EaseInOutCubic;
                break;
            case 9:
                easeType = EaseType.EaseInQuart;
                break;
            case 10:
                easeType = EaseType.EaseOutQuart;
                break;
            case 11:
                easeType = EaseType.EaseInOutQuart;
                break;
            case 12:
                easeType = EaseType.EaseInQuint;
                break;
            case 13:
                easeType = EaseType.EaseOutQuint;
                break;
            case 14:
                easeType = EaseType.EaseInOutQuint;
                break;
            case 15:
                easeType = EaseType.EaseInExpo;
                break;
            case 16:
                easeType = EaseType.EaseOutExpo;
                break;
            case 17:
                easeType = EaseType.EaseInOutExpo;
                break;
            case 18:
                easeType = EaseType.EaseInCirc;
                break;
            case 19:
                easeType = EaseType.EaseOutCirc;
                break;
            case 20:
                easeType = EaseType.EaseInOutCirc;
                break;
            case 21:
                easeType = EaseType.EaseInBack;
                break;
            case 22:
                easeType = EaseType.EaseOutBack;
                break;
            case 23:
                easeType = EaseType.EaseInOutBack;
                break;
            case 24:
                easeType = EaseType.EaseInElastic;
                break;
            case 25:
                easeType = EaseType.EaseOutElastic;
                break;
            case 26:
                easeType = EaseType.EaseInOutElastic;
                break;
            case 27:
                easeType = EaseType.EaseInBounce;
                break;
            case 28:
                easeType = EaseType.EaseOutBounce;
                break;
            case 29:
                easeType = EaseType.EaseInOutBounce;
                break;
            case 30:
                easeType = EaseType.Linear;
                break;
        }
        return easeType;
    }
}
