package ch.buerkigiger.locationfinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


public class LocationHelper extends FragmentActivity  implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	LocationListener {

	// Define a request code to send to Google Play services, this code is returned in Activity.onActivityResult
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL_MS = 1000 * 5;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL_MS = 1000 * 3;
    
    // Required accuracy in meter
    private final static int REQUIRED_ACCURACY_M = 50;

    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;

    private Activity mActivity;
    private EditText mTxtLatitude;
    private EditText mTxtLongitude;
    
    
    public void UpdateLocation(Activity activity, EditText txtLatitude, EditText txtLongitude)
    {
    	mActivity = activity;
    	mTxtLatitude = txtLatitude;
    	mTxtLongitude = txtLongitude;
    	
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
		if (mLocationClient.isConnected()) {
			// stop location updates
			mLocationClient.removeLocationUpdates(this);
			Log.d("LocationFinder", "Postion update stopped");
		}
		mLocationClient.disconnect();
	}
    
    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        if (servicesConnected(mActivity))
		{
			Location myLocation = mLocationClient.getLastLocation();
			if (myLocation != null)
			{
				onLocationChanged(myLocation);
			}
			else
			{
				Utility.displayMessage(R.string.enable_position_service, mActivity);
			}

			// start location updates
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}
    }

    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(mActivity, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
        	// no resolution available
        	Utility.displayMessage(R.string.dialog_message_unknown, mActivity);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
        case CONNECTION_FAILURE_RESOLUTION_REQUEST:
        	// if the result code is Activity.RESULT_OK, try to connect again
            switch (resultCode) {
            case Activity.RESULT_OK :
            	Toast.makeText(mActivity, "Resultion request, try again", Toast.LENGTH_SHORT).show();
            	break;
            default:
            	Toast.makeText(mActivity, "Result code: " + getString(resultCode), Toast.LENGTH_SHORT).show();
            	break;
            } // end of switch (resultCode)
            break;
        default:
        	Toast.makeText(mActivity, "Request code: " + getString(requestCode), Toast.LENGTH_SHORT).show();
        	break;
        } // end of switch (requestCode)
     }
    
    @Override
    public void onLocationChanged(Location location) {
    	
		Double latitude = location.getLatitude();
		Double longitue = location.getLongitude();
		Integer accuracy = ((Float)location.getAccuracy()).intValue();
		
		// add position into given text fields
		mTxtLatitude.setText(latitude.toString());
		mTxtLongitude.setText(longitue.toString());
		
		if (accuracy < REQUIRED_ACCURACY_M) 
		{
			// stop location updates
			mLocationClient.removeLocationUpdates(this);
			
			Toast.makeText(mActivity,
					mActivity.getText(R.string.position_reached) +
					accuracy.toString() + "m", Toast.LENGTH_SHORT).show();
			Log.d("LocationFinder", "Postion reached: " + accuracy.toString());
		}
		else
		{
			Toast.makeText(mActivity,
				mActivity.getText(R.string.position_updated) +
				accuracy.toString() + "m", Toast.LENGTH_SHORT).show();
			Log.d("LocationFinder", "Postion updated: " + accuracy.toString());
		}
    }

    private boolean servicesConnected(Context context) {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("LocationFinder", "Google Play services is available.");

            return true;
        // Google Play services was not available for some reason
        } else {
			// Get the error code
			int errorCode = ConnectionResult.B.getErrorCode();
			
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    errorCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getFragmentManager(), "Location Updates");
            }
            return false;
        }
    }

}
