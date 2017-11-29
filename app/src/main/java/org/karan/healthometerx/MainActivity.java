package org.karan.healthometerx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,SensorEventListener, StepListener  {

    private TextView textView;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Steps: ";
    public long numSteps;
    private Button BtnStart, BtnStop, BtnReset;
    private TextView TvSteps,CalorieView, currentWaterValue, currentCaffeineValue, waterQuantity, caffeineQuantity;
    public int waterGlasses,caffeineCups, currentWaterQuantity, currentCaffeineQuantity;
    public float Calories;
    public ImageButton addWater, addCaffeine;
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TvSteps = (TextView) findViewById(R.id.tv_steps);
        CalorieView = (TextView) findViewById(R.id.CalorieView);
        BtnStart = (Button) findViewById(R.id.btn_start);
        BtnStop = (Button) findViewById(R.id.btn_stop);
        BtnReset = (Button) findViewById(R.id.btn_reset);
        addWater = (ImageButton) findViewById(R.id.addWater);
        addCaffeine = (ImageButton) findViewById(R.id.addCaffeine);
        currentWaterValue = (TextView) findViewById(R.id.currentWaterValue);
        currentCaffeineValue = (TextView) findViewById(R.id.currentCaffeineValue);
        waterQuantity = (TextView) findViewById(R.id.waterQuantity);
        caffeineQuantity = (TextView) findViewById(R.id.caffeineQuantity);


        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        waterGlasses=pref.getInt("waterGlasses", 0);
        currentWaterQuantity=pref.getInt("currentWaterQuantity", 0);
        currentWaterValue.setText(""+waterGlasses);
        waterQuantity.setText(currentWaterQuantity+" ml");

        caffeineCups=pref.getInt("caffeineCups", 0);
        currentCaffeineQuantity=pref.getInt("currentCaffeineQuantity", 0);
        currentCaffeineValue.setText(""+caffeineCups);
        caffeineQuantity.setText(currentCaffeineQuantity +" mg");

        numSteps=pref.getLong("numSteps", 0);
        Calories=pref.getFloat("Calories",0);


        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        BtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                BtnStart.setVisibility(View.INVISIBLE);
                BtnReset.setVisibility(View.INVISIBLE);
                BtnStop.setVisibility(View.VISIBLE);
            }
        });


        BtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                TvSteps.setText("Paused");
                sensorManager.unregisterListener(MainActivity.this);
                BtnStop.setVisibility(View.INVISIBLE);
                BtnStart.setVisibility(View.VISIBLE);
                BtnReset.setVisibility(View.VISIBLE);
            }
        });

        BtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                TvSteps.setText("Let's Start!");
                CalorieView.setText("");
                sensorManager.unregisterListener(MainActivity.this);
                numSteps=0;
                Calories=0;
                editor.putLong("numSteps", 0);
                editor.putFloat("Calories", 0);
                editor.apply();
                BtnReset.setVisibility(View.INVISIBLE);
                BtnStop.setVisibility(View.INVISIBLE);
                BtnStart.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Records cleared", Toast.LENGTH_SHORT).show();
            }
        });

        addWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                waterGlasses++;
                currentWaterQuantity=currentWaterQuantity+250;
                editor.putInt("waterGlasses", waterGlasses);
                editor.putInt("currentWaterQuantity", currentWaterQuantity);
                editor.apply();
                currentWaterValue.setText(""+waterGlasses);
                waterQuantity.setText(currentWaterQuantity+" ml");
            }
        });

        addCaffeine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                caffeineCups++;
                currentCaffeineQuantity=currentCaffeineQuantity+80;
                editor.putInt("caffeineCups", caffeineCups);
                editor.putInt("currentCaffeineQuantity", currentCaffeineQuantity);
                editor.apply();
                currentCaffeineValue.setText(""+caffeineCups);
                caffeineQuantity.setText(currentCaffeineQuantity +" mg");
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Action_Settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent i = new Intent(this,HealthNews.class);
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // code for home;
        } else if (id == R.id.nav_gallery) {
            // code for health_news
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++; //Stores value of steps
        Calories = numSteps/(float)20; //Stores calories, algorithm by "Shape Up America!"
        TvSteps.setText(TEXT_NUM_STEPS + numSteps);
        CalorieView.setText("Calories Burnt: "+ Float.toString(Calories)+" cal");
        editor.putLong("numSteps", numSteps);
        editor.putFloat("Calories", Calories);
        editor.apply();

    }
}