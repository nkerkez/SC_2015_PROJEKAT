package com.soft.nikola.softprojekat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.renderscript.Matrix2f;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockDialogInterface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    String strX = "";
    String strZ = "";
    private Camera cam = null;
    private TextView rezultat;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Button button;
    private boolean start;
    private ArrayList<Motion> motions;
    private Serialization s;
    private ArrayList<com.soft.nikola.softprojekat.Point> vrednosti;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vrednosti = new ArrayList<>();

        rezultat = (TextView) findViewById(R.id.rez);
        s = new Serialization();

        this.readAllMotions();


        button = (Button) findViewById(R.id.buttonStart);

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stop();
                }
                return true;
            }
        });

        start = false;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (null == (mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)))
            finish();


        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void readAllMotions() {
        motions = s.readFromFile(this, "motions.ser");

        if (motions == null) {
            motions = new ArrayList<Motion>();
        }
    }

    private boolean start() {
        start = true;
        button.setText("START");
        return true;
    }

    private void deleteAll() {
        strZ = "";
        strX = "";
        vrednosti.clear();
    }

    private boolean stop() {

        start = false;
        this.readAllMotions();
        Double[] cene = new Double[motions.size()];

        this.getPointsFromString();
     //   motions.add(new Motion("TROUGAODESNO",vrednosti,0));

        for (int i = 0; i < motions.size(); i++) {

            cene[i] = dtw(motions.get(i).getPoints(), vrednosti);
            motions.get(i).setCena(cene[i]);


        }

        double minCena = (double) Collections.min(Arrays.asList(cene));

        Motion m = this.getByCena(minCena);
        executeAction(m.getMotionName());
        /*
        if (m != null) {
            rezultat.setText(m.getMotionName() + minCena);

        } else {
            rezultat.setText("greska");
        }
        // KORISTIO KAD SAM PRAVIO PRIMERE POKRETA
        if(s.writeToFile(this, "motions.ser", motions)){
            button.setText("sacuvano");
        }
        else
        {
            button.setText("nijessacuvano");
        }
*/
        deleteAll();


        return false;
    }
    // IZVRSAVANJE AKCIJA U ZAVISNOSTI OD POKRETA
    private void executeAction(String s) {


        if (s.equals("KRUGLEVO")) {
            Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 0);
        }
        else if(s.equals("KRUGDESNO")){

            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);

        }
        else if(s.equals("TROUGAODESNO")){
            Intent i = new Intent();
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_APP_CALCULATOR);
            startActivity(i);
        }
        else if(s.equals("TROUGAOLEVO")){
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, 1);
        }

        else if(s.equals("KVADRATLEVO")){

            try {
                if (getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA_FLASH)) {
                     cam = Camera.open();
                    Camera.Parameters p = cam.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    cam.setParameters(p);
                    cam.startPreview();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "Exception flashLightOn()",
                        Toast.LENGTH_SHORT).show();
            }

        }
        else if(s.equals("KVADRATDESNO")){
            try {
                if (getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA_FLASH)) {
                    cam.stopPreview();
                    cam.release();
                    cam = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "Exception flashLightOff",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private Motion getByCena(double cena) {

        for (Motion m : motions) {
            if (m.cena == cena) {
                return m;
            }
        }
        return null;
    }

    private void getPointsFromString() {

        String[] partsX = strX.split(",");
        String[] partsZ = strZ.split(",");


        for (int i = 0; i < partsX.length; i++) {

            vrednosti.add(new com.soft.nikola.softprojekat.Point(Double.parseDouble(partsX[i]), Double.parseDouble(partsZ[i])));

        }

    }
    //D(i,j)=Dist(i,j)+MIN(D(i-1,j),D(i,j-1),D(i-1,j-1)) dynamic time warping alg
    private double dtw(ArrayList<com.soft.nikola.softprojekat.Point> p1, ArrayList<com.soft.nikola.softprojekat.Point> p2) {
        double[][] ret = new double[p1.size()][p2.size()];
        for (int i = 0; i < p1.size(); i++) {
            for (int j = 0; j < p2.size(); j++) {
                if (i == 0 && j == 0)
                    ret[0][0] = dist(p1.get(0), p2.get(0));
                else if (i == 0)
                    ret[0][j] = dist(p1.get(0), p2.get(j)) + ret[0][j - 1];
                else if (j == 0)
                    ret[i][0] = dist(p1.get(i), p2.get(0)) + ret[i - 1][0];
                else
                    ret[i][j] = dist(p1.get(i), p2.get(j)) + Math.min(ret[i - 1][j - 1], Math.min(ret[i][j - 1], ret[i - 1][j]));
            }

        }
        return ret[p1.size() - 1][p2.size() - 1];

    }

    private double dist(com.soft.nikola.softprojekat.Point p1, com.soft.nikola.softprojekat.Point p2) {

        return Math.sqrt(
                Math.abs(
                        Math.pow(p2.getX() - p1.getX(), 2) +
                                Math.pow(p2.getY() - p1.getY(), 2))
        );
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double[] gravity = new double[2];
        final double alpha = 0.8;
        if (start) {


            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[2];

            strX += String.valueOf(event.values[0] - gravity[0]) + ',';
            strZ += String.valueOf(event.values[2] - gravity[1]) + ',';

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.soft.nikola.softprojekat/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.soft.nikola.softprojekat/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
