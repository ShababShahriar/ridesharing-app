package petproject.ridesharing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import petproject.ridesharing.models.ChatMessage;
import petproject.ridesharing.models.User;

//import com.android.volley.NetworkResponse;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;


public class ChatActivity extends AppCompatActivity {

    private String TAG = ChatActivity.class.getSimpleName();

    private String bookingID, fromLocation, toLocation, startTime;
    private RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    private ArrayList<ChatMessage> messageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private EditText inputMessage;
    private Button btnSend;
    private JSONArray messagesJSONArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Subscribing to topic...", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//
//                FirebaseMessaging.getInstance().subscribeToTopic("news2");
//            }
//        });



        Intent intent = getIntent();
        bookingID = intent.getStringExtra("bookingID");
        fromLocation = intent.getStringExtra("fromLocation");
        toLocation = intent.getStringExtra("toLocation");
        startTime = intent.getStringExtra("startTime");
        inputMessage = (EditText) findViewById(R.id.chat_message);

//        if (bookingID == null) {
//            Toast.makeText(getApplicationContext(), "Chat room not found!", Toast.LENGTH_SHORT).show();
//            finish();
//        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();

        // self user id is to identify the message owner
//        String selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();

        mAdapter = new ChatAdapter(this, messageArrayList, "5");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("NewMessage")) {
                    handlePushNotification(intent);
                }
            }
        };

//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendMessage();
//            }
//        });
//
        fetchChatThread("9");
//        handlePushNotification(null);
//        handlePushNotification(null);
//        handlePushNotification(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter("NewMessage"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void fetchChatThread(final String bookingID) {
        String endPoint = String.format(Utility.BASE_URL + ("getAllMessages?booking_id=%1$s"), bookingID);
        Log.d(TAG, endPoint);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);
                    if(obj.getBoolean("success")) {
                        Toast.makeText(getApplicationContext(), "Messages Received!", Toast.LENGTH_SHORT).show();
                        messagesJSONArray = obj.getJSONArray("messages");
                        Log.d(TAG, messagesJSONArray.toString());

                        int n = messagesJSONArray.length(), curIndex=0;

                        if (n==0) {
                            Toast.makeText(getApplicationContext(), "No messages in this conversation yet", Toast.LENGTH_LONG).show();
                        }

                        while (curIndex < n) {
                            JSONObject curObj = messagesJSONArray.getJSONObject(curIndex++);

                            ChatMessage curMessage = ChatMessage.createMessageFromJSON(curObj);
                            messageArrayList.add(curMessage);
                            mAdapter.notifyDataSetChanged();
                        }
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

        Utility.addToRequestQueue(strReq, getApplicationContext());
    }

    private void handlePushNotification(Intent intent) {
        ChatMessage message = (ChatMessage) intent.getSerializableExtra("message");
//        String userID = intent.getStringExtra("from_user");

//        ChatMessage message = new ChatMessage("5", "This is a test", null, new User("5", null, null));

//        if (message != null && bookingID != null) {
            messageArrayList.add(message);
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1) {
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
            }
//        }
    }

    public void onSendMessageClick(View v) {
        if (v.getId() == R.id.btn_send) {
            String endPoint = Utility.BASE_URL + "addmessage/";

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    endPoint, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "response: " + response);

                    try {
                        JSONObject obj = new JSONObject(response);
                        if(obj.getBoolean("success")) {
                            Toast.makeText(getApplicationContext(), "Message Sent!", Toast.LENGTH_SHORT).show();
                            inputMessage.setText("");
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

                    params.put("message_text", inputMessage.getText().toString());
                    params.put("booking_id", "9");
                    params.put("user_id", "1");

                    Log.d(TAG, "params: " + params.toString());
                    return params;
                }
            };

            Utility.addToRequestQueue(strReq, getApplicationContext());
        }
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
