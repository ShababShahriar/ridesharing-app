package petproject.ridesharing;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.IntProperty;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import petproject.ridesharing.models.Booking;

public class AddBookingActivity extends AppCompatActivity {
    private String fromLocation, toLocation, rideType, startTime;
    private int numUsers;

    public final String TAG = getClass().getSimpleName();

    public String[] locations = new String[]{"Banani", "Central Road", "Dhanmondi", "Uttara"};
    EditText setStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);

        Spinner fromLocationSpinner = (Spinner) findViewById(R.id.fromLocationSpinner);
        Spinner toLocationSpinner = (Spinner) findViewById(R.id.toLocationSpinner);
        Spinner rideTypeSpinner = (Spinner) findViewById(R.id.rideTypeSpinner);
        final Spinner maxUsersSpinner = (Spinner) findViewById(R.id.setMaxUsersSpinner);
        Spinner numRegistrationSpinner = (Spinner) findViewById(R.id.numRegSpinner);

        setStartTime = (EditText) findViewById(R.id.addStartTime);

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.locations);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromLocationSpinner.setAdapter(locationAdapter);
        toLocationSpinner.setAdapter(locationAdapter);
        fromLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLocation = locations[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        toLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLocation = locations[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final Integer[] userOptions = new Integer[]{1, 2, 3, 4};
        Log.d("AddBooking", userOptions.length + "");
        ArrayAdapter<Integer> maxUsersAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, userOptions);
        maxUsersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxUsersSpinner.setAdapter(maxUsersAdapter);

        ArrayAdapter<String> rideTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"CNG", "UBER"});
        rideTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rideTypeSpinner.setAdapter(rideTypeAdapter);
        rideTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rideType = (position==0) ? "cng" : "uber";
                if(position==0) {
                    maxUsersSpinner.setSelection(userOptions.length - 2);
                }
                else {
                    maxUsersSpinner.setSelection(userOptions.length - 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<Integer> numRegUsersAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, userOptions);
        numRegUsersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numRegistrationSpinner.setAdapter(numRegUsersAdapter);
        numRegistrationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numUsers = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void onAddBookingClick(View v) {
        if (v.getId() == R.id.addBookingButton) {
            if(fromLocation.equals(toLocation)) {
                Toast.makeText(getApplicationContext(), "Taking a ride for same location?!", Toast.LENGTH_LONG).show();
            }
            else {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                String today = dateFormat.format(cal.getTime());
                final String relatedTopicName = fromLocation.replace(" ", "%20") + "-" + toLocation.replace(" ", "%20") + "-" + today;
                FirebaseMessaging.getInstance().subscribeToTopic(relatedTopicName);

                startTime = setStartTime.getText().toString();
                String timeOfDay = startTime.substring(startTime.length()-2, startTime.length());
                if (timeOfDay.equals("PM")) {
                    String hour = startTime.substring(0, 2);
                    if (!hour.equals("12")) {
                        hour = (Integer.parseInt(hour) + 12) + "";
                        startTime = hour.concat(startTime.substring(2));
                    }
                }

                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                cal = Calendar.getInstance();
                cal.setTime(new Date());
                today = dateFormat.format(cal.getTime());

                startTime = today + " " + startTime.substring(0, startTime.length()-3);

                String endPoint = Utility.BASE_URL + "addbooking/";

                StringRequest strReq = new StringRequest(Request.Method.POST,
                        endPoint, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "response: " + response);

                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.getBoolean("success")) {
                                Toast.makeText(getApplicationContext(), "Booking Added!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddBookingActivity.this, ChatActivity.class);
                                intent.putExtra("bookingID", obj.getInt("booking_id")+"");
                                intent.putExtra("fromLocation", fromLocation);
                                intent.putExtra("toLocation", toLocation);
                                intent.putExtra("startTime", startTime);
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "json parsing error: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                        Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("from_location", fromLocation);
                        params.put("to_location", toLocation);
                        params.put("related_topic_name", relatedTopicName);
                        params.put("num_users", ""+numUsers);
                        params.put("start_time", startTime);
                        params.put("ride_type", rideType);
                        params.put("user_id", "1");

                        Log.d(TAG, "params: " + params.toString());
                        return params;
                    }
                };

                Utility.addToRequestQueue(strReq, getApplicationContext());
            }
        }
    }


}
