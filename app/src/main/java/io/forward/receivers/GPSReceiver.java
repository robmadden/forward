package io.forward.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import io.forward.Plotter;
import io.forward.R;
import io.forward.services.GPSService;

public class GPSReceiver extends BroadcastReceiver {
    private static final float METERS_TO_KNOTS = 1.94384f;
    private static final String DELIMS = ":.";

    private Activity activity;
    private TextView latTextField;
    private TextView lonTextField;
    private TextView speedKnots;
    private String lat;
    private String lon;
    private String latDegrees;
    private String lonDegrees;
    private float mps;
    private float knots;
    private Plotter plotter;

    public GPSReceiver() { }

    public GPSReceiver(Activity activity, Plotter plotter) {
        this.activity = activity;
        this.latTextField = (TextView) activity.findViewById(R.id.latitude);
        this.lonTextField = (TextView) activity.findViewById(R.id.longitude);
        this.speedKnots = (TextView) activity.findViewById(R.id.speed_in_knots);
        this.plotter = plotter;
    }

    public float getSpeedInKnots() {
        return this.knots;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        latDegrees = intent.getStringExtra(GPSService.INTENT_EXTRA_LAT_DEGREES);
        lonDegrees = intent.getStringExtra(GPSService.INTENT_EXTRA_LON_DEGREES);
        lat = intent.getStringExtra(GPSService.INTENT_EXTRA_LAT_MINUTES);
        lon = intent.getStringExtra(GPSService.INTENT_EXTRA_LON_MINUTES);
        mps = intent.getFloatExtra(GPSService.INTENT_EXTRA_SPEED, 0.0f);

        if (lat != null && lon != null) {
            Log.d("GPS: ", lat + " " + lon);

            if (this.latTextField != null && lat != null) {
                this.latTextField.setText(latDegrees + "° N");
            }

            if (this.lonTextField != null && lon != null) {
                this.lonTextField.setText(lonDegrees + "° W");
            }

            if (this.speedKnots != null) {
                knots = mps * METERS_TO_KNOTS;
                this.speedKnots.setText(String.valueOf(knots));
            }

            if (latDegrees != null && lonDegrees != null) {
                plotter.updateGPS(Float.parseFloat(latDegrees), -Float.parseFloat(lonDegrees));
            }
        }
    }

    private String formatCoordinate(String coordinate) {
        String[] parts = coordinate.split(":");
        String minutesSecondsStr = parts[1];
        String[] minutesSeconds = minutesSecondsStr.split("\\.");
        return parts[0] + "°" + minutesSeconds[0] + "." + minutesSeconds[1] + "' ";
    }
}
