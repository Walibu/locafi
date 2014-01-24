package ch.buerkigiger.locationfinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


public class MyLocation extends FragmentActivity  implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	LocationListener {

	// Define a request code to send to Google Play services, this code is returned in Activity.onActivityResult
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    
    private LocationClient mLocationClient;
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
				Double latitude = myLocation.getLatitude();
				Double longitue = myLocation.getLongitude();
				Integer accuracy = ((Float)myLocation.getAccuracy()).intValue();
				
				// add position into given text fields
				mTxtLatitude.setText(latitude.toString());
				mTxtLongitude.setText(longitue.toString());
				
				Toast.makeText(mActivity,
					mActivity.getText(R.string.position_updated) +
					accuracy.toString() + "m", Toast.LENGTH_SHORT).show();
			}
			else
			{
				displayError(mActivity.getText(R.string.enable_position_service));
			}
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
        	displayError("Unknown error: " + connectionResult.getErrorCode());
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
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude()) + "," +
                Double.toString(location.getAccuracy());
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }


	private void displayError(CharSequence message)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setMessage(message);
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// user cancelled the dialog
			}
		});
		// display message
		builder.create().show();
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
			int errorCode = ConnectionResult.jZ.getErrorCode();  // with new google-play-service_lib
			//int errorCode = ConnectionResult.B.getErrorCode(); // with Hsr google-play-service_lib
			
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

