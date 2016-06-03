package joao.splitride.app.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Segments")
public class Segment extends ParseObject implements Parcelable{

    public static final Parcelable.Creator<Segment> CREATOR = new Parcelable.Creator<Segment>() {
        public Segment createFromParcel(Parcel in) {

            Segment segment = new Segment();

            segment.setName(in.readString());
            segment.setCost(in.readDouble());
            segment.setDistance(in.readInt());
            segment.setCalendarID(in.readString());

            return segment;
        }

        public Segment[] newArray(int size) {
            return new Segment[size];

        }
    };

	public Segment(){

	}

    public String getName() {
        return getString("Name");
    }

	public void setName(String name){
		put("Name", name);
	}

    public int getDistance() {
        return getInt("Distance");
    }

	public void setDistance(int distance){
		put("Distance", distance);
	}

    public double getCost() {
        return getDouble("Cost");
    }

    public void setCost(double cost){
		put("Cost", cost);
	}

    public String getCalendarID() {
        return getString("calendarID");
    }

    public void setCalendarID(String id) {
        put("calendarID", id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeDouble(getCost());
        dest.writeInt(getDistance());
        dest.writeString(getCalendarID());

    }
}
