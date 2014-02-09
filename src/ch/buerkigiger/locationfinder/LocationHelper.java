package ch.buerkigiger.locationfinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class LocationHelper implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	LocationListener {

    // interface for updating the location, the receiver must implement this method
    interface LocationReceiver
    {
        public void updateLocation(Location location);
    }
    
    // receiver of location updates
    private LocationReceiver mLocationReceiver;
    // the activity context of the receiver
    private Activity mActivity;
    
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL_MS = 1000 * 5;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL_MS = 1000 * 3;
    
    // Maximum waiting time for update position in milliseconds
    private static final long TIMEOUT_UPDATE_MS = 1000 * 30;
    
    // Required accuracy in meter
    private final static int REQUIRED_ACCURACY_M = 50;
    
    private long mCountTimoutUpdate = 0;

    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    
    
    public void startLocationUpdates(LocationReceiver locationReceiver, Activity activity)
    {
    	mActivity = activity;
    	mLocationReceiver = locationReceiver;
    	
    	if (mLocationClient == null)
    	{
    		mLocationClient = new LocationClient(mActivity, this, this);
    	}
    	
    	mLocationClient.connect();
    	
    	if (mLocationRequest == null)
    	{
			// Create the LocationRequest object
	        mLocationRequest = LocationRequest.create();
	        // Use high accuracy
	        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	        // Set the update interval
	        mLocationRequest.setInterval(UPDATE_INTERVAL_MS);
	        // Set the fastest update interval
	        mLocationRequest.setFastestInterval(FASTEST_INTERVAL_MS);
    	}
    }
    
	public void stopLocationUpdates() {
		// If the client is connected
		if (mLocationClient.isConnected())
		{
			// stop location updates
			mLocationClient.removeLocationUpdates(this);
			Log.d("LocationFinder", "Postion update stopped");
		}
		mLocationClient.disconnect();
	}

    @Override
    public void onConnected(Bundle dataBundle) {
        if (servicesConnected(mActivity))
		{
			// start location updates
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
            // reset counter, which counts up to max number of location updates
            mCountTimoutUpdate = 0;
		}
    }

    @Override
    public void onDisconnected() {
        // Display the connection status
        Utility.displayMessage(R.string.enable_position_service, mActivity);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    	// no Google service available
    	Utility.displayMessage(R.string.dialog_message_unknown, mActivity);
    }
    
    @Override
    public void onLocationChanged(Location location) {
        
        // if timeout reached, get lastLocation
        if (mCountTimoutUpdate > TIMEOUT_UPDATE_MS)
        {
            location = mLocationClient.getLastLocation();
        }
        
        if (location != null)
        {
            mLocationReceiver.updateLocation(location);

            // get accuracy
    		Integer accuracy = ((Float)location.getAccuracy()).intValue();
    		
    		if (accuracy < REQUIRED_ACCURACY_M) 
    		{
    			// required accuracy reached, stop location updates
    			mLocationClient.removeLocationUpdates(this);
    			
    			Toast.makeText(mActivity,
    					mActivity.getText(R.string.position_reached) +
    					accuracy.toString() + "m", Toast.LENGTH_SHORT).show();
    			Log.d("LocationFinder", "Postion reached: " + accuracy.toString());
    		}
    		else if (mCountTimoutUpdate > TIMEOUT_UPDATE_MS)
    		{
    		    // timeout reached, stop location updates
                mLocationClient.removeLocationUpdates(this);
                
                Utility.displayMessage(R.string.position_not_reached, mActivity);
                Log.d("LocationFinder", "Postion timout: " + accuracy.toString());
    		}
    		else
    		{
    		    mCountTimoutUpdate += UPDATE_INTERVAL_MS;
    		    
    			Toast.makeText(mActivity,
    				mActivity.getText(R.string.position_updated) +
    				accuracy.toString() + "m", Toast.LENGTH_SHORT).show();
    			Log.d("LocationFinder", "Postion updated: " + accuracy.toString());
    		}
        }
        else
        {
            Utility.displayMessage(R.string.enable_position_service, mActivity);
        }
    }

    private boolean servicesConnected(Context context) {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        
        if (ConnectionResult.SUCCESS == resultCode)
        {
            // In debug mode, log the status
            Log.d("LocationFinder", "Google Play services is available.");
            return true;
        }
        else
        {
            Log.d("LocationFinder", "Error: Google Play services was not available");
            return false;
        }
    }

}
