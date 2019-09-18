package final_project.classes;

import java.util.ArrayList;

/**
 *The Job class
 * 
 * @author Deep Shah, David Ojesekhoba 
 */
public class Job {

  private String jobTitle;
  private double payRate;
  public static ArrayList<Job> jobArrayList = new ArrayList<>();
/**
 * A two parameter constructor
 * 
 * @param jobTitle  the title of the job 
 * @param payRate   the payrate of the job
 */
  public Job(String jobTitle, double payRate) {
    this.jobTitle = jobTitle;
    this.payRate = payRate;
  }

 
  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  public double getPayRate() {
    return payRate;
  }

  public void setPayRate(double payRate) {
    this.payRate = payRate;
  }
  
  /**
   * 
   * @return formatted string
   */
  @Override
  public String toString(){
    return getJobTitle();
  }
}