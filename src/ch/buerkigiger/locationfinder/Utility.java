package ch.buerkigiger.locationfinder;

import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;

public class Utility
{

    public static double getDoubleValue(EditText textField)
    {
        String strValue = textField.getText().toString();
        if (strValue.isEmpty() || strValue.isEmpty())
        {
            return Double.MAX_VALUE;
        }
        return Double.parseDouble(strValue);
    }

    public static boolean isInRange(double value, double minValue, double maxValue)
    {
        return value >= minValue && value <= maxValue;
    }

    public static boolean isNetworkConnected(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        if (network != null)
        {
            return network.isAvailable();
        }
        return false;
    }

    public static void displayMessage(int messageId, Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(messageId);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                // user cancelled the dialog
            }
        });
        // display message
        builder.create().show();
    }

    public static String getAddress(double latitude, double longitude, Context context)
    {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try
        {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty())
            {
                Address address = addresses.get(0);
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++)
                {
                    sb.append(address.getAddressLine(i));
                    sb.append("\r\n");
                }
                return sb.toString();
            }
        } catch (Exception e)
        {
        }
        return null;
    }

    public static String getLocation(String locationName, Context context)
    {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try
        {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (!addresses.isEmpty())
            {
                Address address = addresses.get(0);
                StringBuffer sb = new StringBuffer();
                sb.append(context.getText(R.string.text_latitude) + ": ");
                sb.append(Double.toString(address.getLatitude()));
                sb.append("\r\n");
                sb.append(context.getText(R.string.text_longitude) + ": ");
                sb.append(Double.toString(address.getLongitude()));
                sb.append("\r\n\r\n");
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++)
                {
                    sb.append(address.getAddressLine(i));
                    sb.append("\r\n");
                }
                return sb.toString();
            }
        } catch (Exception e)
        {
        }
        return null;
    }
}
