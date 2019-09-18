package final_project.classes;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Deep Shah
 */
public class ShiftTime {

    private final IntegerProperty time =  new SimpleIntegerProperty(this,"time");
    String meridian;

    public ShiftTime(String meridian) {    
        this.meridian=meridian;
        
    }

    public int getTime() {
     return this.time.get();
    }

    public void setTime(int time) {
        this.time.set(time);
    }
    public final IntegerProperty timeProperty(){
        return time;
    }

    public String getMeridian() {
        return meridian;
    }

    public void setMeridian(String meridian) {
        this.meridian = meridian;
    }
}
