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

    public void setRouteId(String routeId){

        this.put("RouteID", routeId);
    }

    public void setSegmentId(String segmentId){

        this.put("SegmentID", segmentId);
    }

    public String getRouteId(){
        return this.getString("RouteID");
    }

    public String getSegmentId(){

        return this.getString("SegmentID");
    }
}
