package com.company.asenradov.assignment1;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import java.util.Calendar;

public class BoundedService extends Service implements SensorEventListener {

    private final int DELAY = 100;
    private SensorManager sensorManager_;
    private Sensor accelerometer_;

    // number of seconds the activity will be written to the app (2 minutes)
    private final int twoMinutesInSeconds = 20;

    // counters for the positions
    private double a, b, c = 0;

    private String activity, time = " ";

    private String time1, activity1 = " ";
    private String time2, activity2 = " ";
    private String time3, activity3 = " ";
    private String time4, activity4 = " ";
    private String time5, activity5 = " ";
    private String time6, activity6 = " ";
    private String time7, activity7 = " ";
    private String time8, activity8 = " ";
    private String time9, activity9 = " ";
    private String time10, activity10 = " ";

    // makes a new calender object and gets the current date/time
    private Calendar calendar = Calendar.getInstance();
    private int hour;
    private int minutes;
    private int elapsedSeconds = 0;
    private int seconds;
    private int tmp;

    private boolean bool, initialTimeBool = true;

    public BoundedService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        BoundedService getService(){
            return BoundedService.this;
        }
    }

    public int getTwoMinutesInSeconds(){
        return twoMinutesInSeconds;
    }

    public int getElapsedSeconds(){
        return elapsedSeconds;
    }

    public String getTime1(){
        return time1;
    }
    public String getActivity1(){
        return activity1;
    }

    public String getTime2(){
        return time2;
    }
    public String getActivity2(){
        return activity2;
    }

    public String getTime3(){
        return time3;
    }
    public String getActivity3(){
        return activity3;
    }

    public String getTime4(){
        return time4;
    }
    public String getActivity4(){
        return activity4;
    }

    public String getTime5(){
        return time5;
    }
    public String getActivity5(){
        return activity5;
    }

    public String getTime6(){
        return time6;
    }
    public String getActivity6(){
        return activity6;
    }

    public String getTime7(){
        return time7;
    }
    public String getActivity7(){
        return activity7;
    }

    public String getTime8(){
        return time8;
    }
    public String getActivity8(){
        return activity8;
    }

    public String getTime9(){
        return time9;
    }
    public String getActivity9(){
        return activity9;
    }

    public String getTime10(){
        return time10;
    }
    public String getActivity10(){
        return activity10;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        sensorManager_ = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer_ = sensorManager_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager_.registerListener(this, accelerometer_, SensorManager.SENSOR_DELAY_NORMAL, DELAY);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // rms value
        double rms = 0;

        // values for the accelerometer data
        double accelX = 0;
        double accelY = 0;
        double accelZ = 0;

        String AM_PM = " ";

        Sensor mySensor = event.sensor;
        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelX = event.values[0];
            accelY = event.values[1];
            accelZ = event.values[2];

            rms = Math.sqrt((accelX * accelX + accelY * accelY + accelZ * accelZ) / 3);

            // this will determine if the user is lying down
            if (rms > 5.55 && rms < 5.75 && accelY > -1 && accelY < 1){
                activity = "Lying Down";
            }
            // this will determine if the user is sitting
            else if (rms > 5.45 && rms < 5.54 && accelY > 7 && accelY < 10){
                activity = "Sitting";
            }
            // this will determine if the user walking/running
            else if (rms > 6 && accelY > 5){
                activity = "Walking";
            }
            else {

            }

            // updates the current date/time
            calendar = Calendar.getInstance();

            // gets the hour/minutes/seconds from the current time
            seconds = calendar.get(Calendar.SECOND);
            minutes = calendar.get(Calendar.MINUTE);
            hour = calendar.get(Calendar.HOUR);

            // determine if time is AM or PM
            if(calendar.get(Calendar.AM_PM) == 0){
                AM_PM = " AM";
            }
            else {
                AM_PM = " PM";
            }

            if(initialTimeBool) {
                formatTime(AM_PM);
            }

            // these if statements will ensure that the user position counter will collect data
            // every second, not every time the sensor changed
            if (bool){
                bool = false;
                tmp = seconds;
                counter();
            }
            if (seconds != tmp){
                tmp = seconds;
                counter();

                // this needs to be placed here so that the if statement in main activity receives
                // the correct value of elapsedSeconds
                if(elapsedSeconds == twoMinutesInSeconds){
                    elapsedSeconds = 0;
                }

                // adds a second to the elapsed seconds since the last activity update, or
                // the start of the program
                elapsedSeconds += 1;
            }

            // every 2 minutes (120 seconds), call the function that will write the activity
            // (determined by the algorithm) to the app
            // this will not be called while the time has not advanced by 2 minutes
            if (elapsedSeconds == twoMinutesInSeconds) {

                // this is to let mainActivity know that it should write the current activity
                // to the file on external storage
                writeActivityToApp(AM_PM);
            }
        }
    }

    // this function will run every second, and determine which position the user is in
    // it counts the number of times each position was recorded
    public void counter(){

        if(activity.equals("Lying Down")){
            a += 1;
        }
        else if(activity.equals("Sitting")){
            b += 1;
        }
        else if(activity.equals("Walking")){
            c += 1;
        }
        else{

        }
    }

    // function that determines which activity to write, and writes it to the app
    public void writeActivityToApp(String AM_PM){

        // the following if statements use the counts of each recorded user position for the last
        //  2 minutes
        // Whichever position has the highest count is made the official position for the 2 minutes

        // this determines if the activity for the 2 minute period was lying down
        if(a > b && a > c){
            setTimesAndActivities();
            formatTime(AM_PM);
            activity1 = "Lying Down";
        }
        // this determines if the activity for the 2 minute period was sitting
        else if(b > a && b > c){
            setTimesAndActivities();
            formatTime(AM_PM);
            activity1 = "Sitting";
        }
        // this determines if the activity for the 2 minute period was walking/running
        else if(c > a && c > b){
            setTimesAndActivities();
            formatTime(AM_PM);
            activity1 = "Walking";
        }
        else{
        }

        // resets the counters
        a = 0;
        b = 0;
        c = 0;
    }

    // get the time1
    // deals with some formatting issues
    public void formatTime(String AM_PM){

        String minutesFormat = " ";

        // add a 0 in front of minutes if it's less than 10
        if(minutes < 10){
            minutesFormat = String.format("%02d", minutes);
        }
        else{
            minutesFormat = String.valueOf(minutes);
        }

        if(initialTimeBool){
            time = hour + ":" + minutesFormat + AM_PM;
            initialTimeBool = false;
        }
        else{
            time1 = time + " - " + hour + ":" + minutesFormat + AM_PM;
            time = hour + ":" + minutesFormat + AM_PM;
        }
    }

    // this function updates the main screen on the app
    public void setTimesAndActivities(){
        time10 = time9;
        activity10 = activity9;

        time9 = time8;
        activity9 = activity8;

        time8 = time7;
        activity8 = activity7;

        time7 = time6;
        activity7 = activity6;

        time6 = time5;
        activity6 = activity5;

        time5 = time4;
        activity5 = activity4;

        time4 = time3;
        activity4 = activity3;

        time3 = time2;
        activity3 = activity2;

        time2 = time1;
        activity2 = activity1;
    }

    // this method is called in MainActivity()
    // it is used to reset the value of everything
    // this is need because the service is started as soon as it is created in MainActivity,
    //  therefore all the data start getting collected as soon the app starts up
    // this method is called when the start button is clicked
    public void setBlank(){

        elapsedSeconds = 0;

        time1 = " ";
        activity1 = " ";
        time2 = " ";
        activity2 = " ";
        time3 = " ";
        activity3 = " ";
        time4 = " ";
        activity4 = " ";
        time5 = " ";
        activity5 = " ";
        time6 = " ";
        activity6 = " ";
        time7 = " ";
        activity7 = " ";
        time8 = " ";
        activity8 = " ";
        time9 = " ";
        activity9 = " ";
        time10 = " ";
        activity10 = " ";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

