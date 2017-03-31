package petproject.ridesharing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import petproject.ridesharing.models.Booking;

public class BookingItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String TAG = BookingItemAdapter.class.getSimpleName();

    private String userId, fromLocation, toLocation;
    private int FULL = 100;

    private Context mContext;
    private ArrayList<Booking> bookingArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView startTime, numUsers, rideType;

        public ViewHolder(View view) {
            super(view);
            startTime = (TextView) itemView.findViewById(R.id.start_time);
            numUsers = (TextView) itemView.findViewById(R.id.numUsers);
            rideType = (TextView) itemView.findViewById(R.id.ride_type);
        }
    }


    public BookingItemAdapter(Context mContext, ArrayList<Booking> bookingArrayList) {
        this.mContext = mContext;
        this.bookingArrayList = bookingArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the chat message
        // left or right
        if (viewType != FULL) {
        // self message
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_item, parent, false);
        }
        else {
//            // others message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.booking_item_disabled, parent, false);
        }

        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        Booking booking = bookingArrayList.get(position);
        if (position==0)
            return position;
        else return booking.is_full() ? FULL : position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (position==0)
            return;

        Booking booking = bookingArrayList.get(position);
        ((ViewHolder) holder).startTime.setText(getTimestamp(booking.getStartTime()));
        ((ViewHolder) holder).numUsers.setText(booking.getNumUsers() + "/" + (booking.getRideType().equals("cng") ? "3" : "4"));
        ((ViewHolder) holder).rideType.setText(booking.getRideType().toUpperCase());

//        String timestamp = getTimestamp(message.getCreatedAt());
//        String timestamp = "Just now";

//        if (booking.getUser().getName() != null)
//            timestamp = booking.getUser().getName() + ", " + timestamp;

//        ((ViewHolder) holder).timestamp.setText(timestamp);
    }

    @Override
    public int getItemCount() {
        return bookingArrayList.size();
    }

//    public static String getTimestamp(String dateStr) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String timestamp = "";
//
//        today = today.length() < 2 ? "0" + today : today;
//
//        try {
//            Date date = format.parse(dateStr);
//            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
//            String dateToday = todayFormat.format(date);
//            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
//            String date1 = format.format(date);
//            timestamp = date1.toString();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return timestamp;
//    }

    public static String getTimestamp(String dbString) {

        int hr = Integer.parseInt("" + dbString.charAt(11) + dbString.charAt(12));
        String min = "" +  dbString.charAt(14) + dbString.charAt(15);
        String timeOfDay;
        String timeOfUpdate = "";

        try
        {
            if(hr>12) {
                hr = hr%12;
                timeOfDay = "PM";
            }
            else if(hr == 12) timeOfDay = "PM";
            else timeOfDay = "AM";

            if(hr == 0) hr = 12;

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            String today = dateFormat.format(cal.getTime());
            String date = dbString.substring(0, 10);

            if (today.equals(date)) {
                date = " Today";
            }
            else {
                cal.add(Calendar.DAY_OF_MONTH, -1);
                String yesterday = dateFormat.format((cal.getTime()));
                Log.d("yesterday", yesterday);
                if (yesterday.equals(date)) {
                    date = " Yesterday";
                } else {
                    String monthString = new DateFormatSymbols().getMonths()[Integer.parseInt(date.substring(5,7))-1];
                    Log.d("monthString", monthString);

                    date = " " + monthString.substring(0,3) + " " + date.substring(8,10);
                }
            }

//            timeOfUpdate = hr + ":" + min + timeOfDay + date;
            timeOfUpdate = hr + ":" + min + " " + timeOfDay;
        }
        catch (Exception e)
        {
            timeOfUpdate = dbString;
        }

        return timeOfUpdate;
    }

    public interface ClickListener {
        void onClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private BookingItemAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final BookingItemAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
