package joao.splitride.app.entities;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Joao on 06-12-2015.
 */
@ParseClassName("ComposedRoutes")
public class ComposedRoute extends ParseObject{

    public ComposedRoute(){

    }

    public String getRouteId() {
        return this.getString("RouteID");
    }

    public void setRouteId(String routeId) {
        this.put("RouteID", routeId);
    }

    public String getSegmentId() {
        return this.getString("SegmentID");
    }

    public void setSegmentId(String segmentId) {
        this.put("SegmentID", segmentId);
    }

    public String getCalendarID() {
        return this.getString("calendarID");
    }

    public void setCalendarID(String id) {
        this.put("calendarID", id);
    }
}
