package com.soft.nikola.softprojekat;

import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.renderscript.Matrix2f;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockDialogInterface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    String strX = "";
    String strZ = "";
    private TextView rezultat;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Button button;
    private boolean start;
    private ArrayList<Motion> motions;
    private Serialization s;
    private ArrayList<com.soft.nikola.softprojekat.Point> vrednosti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vrednosti = new ArrayList<>();

        rezultat = (TextView)findViewById(R.id.rez);
        s = new Serialization();

        motions = s.readFromFile(this, "motions.ser");

        if(motions == null){
            motions = new ArrayList<Motion>();
        }



        button = (Button)findViewById(R.id.buttonStart);

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    start = start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    start = stop();
                }
                return true;
            }
        });

        start = false;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if( null == (mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)))
            finish();


        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI );


    }


    private boolean start(){

        return true;
    }

    private boolean stop(){


        Double[] cene = new Double[motions.size()];
        button.setEnabled(false);
        this.getPointsFromString();
       // motions.add(new Motion("kvadratdesno",vrednosti,0));

        for ( int i = 0 ; i < motions.size(); i++ )
        {

            cene[i] =  dtw(motions.get(i).getPoints(),vrednosti);
            motions.get(i).setCena(cene[i]);



        }

        double minCena = (double) Collections.min(Arrays.asList(cene));

        Motion m = this.getByCena(minCena);
        if(  m != null){
            rezultat.setText(m.getMotionName()+minCena);

        }
        else
        {
            rezultat.setText("greska");
        }
        /*
        if(s.writeToFile(this, "motions.ser", motions)){
            button.setText("sacuvano");
        }
        else
        {
            button.setText("nijessacuvano");
        }
        */



       return false;
    }

    private Motion getByCena(double cena){

        for(Motion m : motions)
        {
            if(m.cena == cena) {
                return m;
            }
        }
        return  null;
    }
    private void  getPointsFromString(){

        String[] partsX = strX.split(",");
        String[] partsZ = strZ.split(",");


        for ( int i = 0; i < partsX.length ; i++ ){
            vrednosti.add(new com.soft.nikola.softprojekat.Point( Double.parseDouble(partsX[i]),Double.parseDouble(partsZ[i])));

        }

    }

    private double dtw(ArrayList<com.soft.nikola.softprojekat.Point> p1, ArrayList<com.soft.nikola.softprojekat.Point> p2){
        double[][] ret = new double[p1.size()][p2.size()];
        for(int i = 0 ; i < p1.size(); i++)
        {
            for(int j = 0; j < p2.size(); j++)
            {
                if( i == 0 && j == 0)
                    ret[0][0] = dist(p1.get(0), p2.get(0));
                else if( i == 0)
                    ret[0][j] = dist(p1.get(0), p2.get(j)) + ret[0][j-1];
                else if( j == 0)
                    ret[i][0] = dist(p1.get(i), p2.get(0)) + ret[i-1][0];
                else
                    ret[i][j] = dist(p1.get(i), p2.get(j)) + Math.min( ret[i-1][j-1], Math.min( ret[i][j-1], ret[i-1][j]));
            }

        }
        return ret[p1.size()-1][p2.size()-1];

    }

    private  double dist(com.soft.nikola.softprojekat.Point p1, com.soft.nikola.softprojekat.Point p2){

        return Math.sqrt(
                Math.abs(
                        Math.pow(p2.getX()-p1.getX(),2) +
                        Math.pow(p2.getY()-p1.getY(), 2))
        );
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
            if ( start ){
               strX+=String.valueOf(event.values[0])+',';
                strZ+= String.valueOf(event.values[2])+',';

            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
