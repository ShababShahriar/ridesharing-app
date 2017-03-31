package petproject.ridesharing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import petproject.ridesharing.models.Booking;


public class ShowBookingsActivity extends AppCompatActivity {

    private String TAG = ShowBookingsActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private BookingItemAdapter mAdapter;
    private ArrayList<Booking> bookingArrayList;
    private JSONArray bookingJSONArray;
    private String fromLocation="", toLocation="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bookings);

//        fromLocation = getIntent().getStringExtra("fromLocation");
//        toLocation = getIntent().getStringExtra("toLocation");

        String endPoint = Utility.BASE_URL + String.format("getActiveBookingList?from_location=%1$s&to_location=%2$s", fromLocation, toLocation);
        Log.d(TAG, endPoint);

        bookingArrayList = new ArrayList<>();
        bookingArrayList.add(null);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);
                    bookingJSONArray = obj.getJSONArray("bookings");
                    Log.d(TAG, bookingJSONArray.toString());

                    int n = bookingJSONArray.length(), curIndex=0;

                    if (n==0) {
                        Toast.makeText(getApplicationContext(), "No active bookings in this route", Toast.LENGTH_LONG).show();
                    }

                    while (curIndex < n) {
                        JSONObject curObj = bookingJSONArray.getJSONObject(curIndex++);

                        Booking curBooking = Booking.createBookingFromJSON(curObj);
                        bookingArrayList.add(curBooking);
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
        });

        //Adding request to request queue
        Utility.addToRequestQueue(strReq, getApplicationContext());


        recyclerView = (RecyclerView) findViewById(R.id.show_bookings_recycler_view);

//        bookingArrayList.add(new Booking("topicname", "Dhanmondi", "Central Rd", "CNG", "11:00 AM", 3));
//        bookingArrayList.add(new Booking("topicname", "Dhanmondi", "Central Rd", "UBER", "11:30 AM", 1));

        // self user id is to identify the message owner
//        String selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();

        mAdapter = new BookingItemAdapter(this, bookingArrayList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new BookingItemAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new BookingItemAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Booking booking = bookingArrayList.get(position);
                if(position==0 || booking.is_full())
                    return;
                Intent intent = new Intent(ShowBookingsActivity.this, ChatActivity.class);
                intent.putExtra("bookingID", booking.getId());
                intent.putExtra("fromLocation", booking.getFromLocation());
                intent.putExtra("toLocation", booking.getToLocation());
                intent.putExtra("startTime", booking.getStartTime());
                startActivity(intent);
            }
        }));

        this.fromLocation = this.fromLocation.equals("") ? "All" : this.fromLocation;
        this.toLocation = this.toLocation.equals("") ? "All" : this.toLocation;
        ((TextView) findViewById(R.id.show_from_location)).setText(this.fromLocation);
        ((TextView) findViewById(R.id.show_to_location)).setText(this.toLocation);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
