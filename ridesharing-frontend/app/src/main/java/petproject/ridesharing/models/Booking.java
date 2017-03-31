package petproject.ridesharing.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class Booking implements Serializable {
    private String relatedTopicName, fromLocation, toLocation, rideType, startTime;
    private int numUsers;
    private boolean isFull;


    public Booking(String relatedTopicName, String fromLocation, String toLocation, String rideType, String startTime, int numUsers, boolean isFull) {
        this.relatedTopicName = relatedTopicName;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.rideType = rideType;
        this.startTime = startTime;
        this.numUsers = numUsers;
        this.isFull = isFull;
    }

    public Booking(String relatedTopicName, String fromLocation, String toLocation, String rideType, String startTime, int numUsers) {
        this.relatedTopicName = relatedTopicName;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.rideType = rideType;
        this.startTime = startTime;
        this.numUsers = numUsers;
    }

    public static Booking createBookingFromJSON(JSONObject jsonObject) {
        try {
            return new Booking(
                    jsonObject.getString("related_topic_name"),
                    jsonObject.getString("from_location"),
                    jsonObject.getString("to_location"),
                    jsonObject.getString("ride_type"),
                    jsonObject.getString("start_time"),
                    jsonObject.getInt("num_users"),
                    jsonObject.getBoolean("is_full"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getRelatedTopicName() {
        return relatedTopicName;
    }

    public void setRelatedTopicName(String relatedTopicName) {
        this.relatedTopicName = relatedTopicName;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getRideType() {
        return rideType;
    }

    public void setRideType(String rideType) {
        this.rideType = rideType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    public boolean is_full() {
        return isFull;
    }

    public void setFull(boolean full) {
        this.isFull = full;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "relatedTopicName='" + relatedTopicName + '\'' +
                ", fromLocation='" + fromLocation + '\'' +
                ", toLocation='" + toLocation + '\'' +
                ", rideType='" + rideType + '\'' +
                ", startTime='" + startTime + '\'' +
                ", numUsers=" + numUsers +
                ", isFull=" + isFull +
                '}';
    }
}