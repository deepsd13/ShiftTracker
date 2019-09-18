package final_project.classes;

/**
 *
 * @author Deep Shah
 */
public class Shift {

    private Job job;
    private Date date;
    private String day;
    private ShiftTime shiftStartTime;
    private ShiftTime shiftEndTime;
    private int hoursWorked;
    private double totalWages;

    /**
     * NO Parameter constructor
     */
    public Shift() {

    }

    /**
     * 7 parameter constructor
     * 
     * @param job -  job of the shift
     * @param date - date of the shift
     * @param day - day of the shift
     * @param shiftStartTime -  the start time of the shift
     * @param shiftEndTime - the end time of the class
     * @param hoursWorked - total hours worked
     * @param totalWages - wages earned
     */
    public Shift(Job job, Date date, String day, ShiftTime shiftStartTime, ShiftTime shiftEndTime, int hoursWorked, double totalWages) {
        this.job = job;
        this.date = date;
        this.day = day;
        this.shiftStartTime = shiftStartTime;
        this.shiftEndTime = shiftEndTime;
        this.hoursWorked = hoursWorked;
        this.totalWages = totalWages;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ShiftTime getShiftStartTime() {
        return shiftStartTime;
    }

    public void setShiftStartTime(ShiftTime shiftStartTime) {
        this.shiftStartTime = shiftStartTime;
    }

    public ShiftTime getShiftEndTime() {
        return shiftEndTime;
    }

    public void setShiftEndTime(ShiftTime shiftEndTime) {
        this.shiftEndTime = shiftEndTime;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public double getTotalWages() {
        return totalWages;
    }

    public void setTotalWages(double totalWages) {
        this.totalWages = totalWages;
    }
    
    /**
     * 
     * @return a formatted string
     */

    @Override
    public String toString() {
        return date.getMonth() + "," + date.getDay() + " " + date.getYear();

    }
}

