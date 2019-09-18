

package final_project.classes;

/**
 *The date class 
 * 
 * @author Deep Shah, David Ojesekhoba 
 */
public class Date {

	private int year;
	private String month;
	private int day;

	public String getMonth() {
		return month;
	}

	/**
	 * 
	 * @param month 
	 */
	public void setMonth(String month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	/**
	 * 
	 * @param year
	 */
	public void setYear(int year) {
		this.year = year;
	}

	public int getDay() {
		return day;
	}

	/**
	 * 
	 * @param day
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	

	public Date(int year, String month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
	}


}
