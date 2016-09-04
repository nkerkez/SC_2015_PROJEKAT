package com.soft.nikola.softprojekat;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by DJ David on 9/4/2016.
 */
public class Serialization {


    public Serialization() {
    }

    public ArrayList<Motion> readFromFile(Context context, String fileName)
    {
        ArrayList<Motion> ret = null;
        FileInputStream input;
        try {
            input = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(input);
            ret = (ArrayList<Motion>) ois.readObject();

            ois.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }


        return ret;


    }

    public boolean writeToFile(Context context, String fileName, ArrayList<Motion> motions )
    {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);

            os.writeObject(motions);
            os.close();

            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


    }

    public boolean writeToFile(Context context, String fileName, String motions )
    {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);

            os.writeObject(motions);
            os.flush();
            os.close();

            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


    }


}
