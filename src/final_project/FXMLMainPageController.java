package final_project;

import final_project.classes.*;
import static final_project.classes.Job.jobArrayList;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.NumberBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Main page controller class
 * 
 * @author Deep Shah, David Ojesekhoba 
 */
public class FXMLMainPageController implements Initializable {

    //static variable of stage for add job fxml which could be accessed by add job page
    public static Stage addJobStage = new Stage();

    //connecting all the fields from fxml file to the controller
    @FXML
    private Label lblHrsWorked, lblWages, lblPayRate;
    @FXML
    private ComboBox<String> cmbAMPM0, cmbAMPM1, cmbMonth, cmbDay;
    @FXML
    private ComboBox<Job> cmbJT;
    @FXML
    private ComboBox<Integer> cmbDate, cmbYear, cmbStart, cmbEnd;
    @FXML
    private Button btnAddJob, btnAddShift, btnSave, btnCancel, btnExit, btnDel, btnEdit, btnSearch;
    @FXML
    public ListView<Shift> lstShift;

    //Stores all the shifts that user saves
    ShiftList shiftList = new ShiftList();

    //observable list of shift which contains all the shift and is added to the listview
    //of shift
    ObservableList<Shift> obsShiftList = FXCollections.observableArrayList();

    //arraylist of job which will store the jobs that user enters from the add job page
    ArrayList<Job> jobs = new ArrayList<>();

    //NumberBinding which calculate hours worked as the shift time changes
    NumberBinding calHrsWorked;

    //Arraylist of job is added to this observable list 
    ObservableList<Job> jobList = FXCollections.observableArrayList();

    /**
     * The method which is used on search page controller to select the shift
     * which the user selects
     *
     * @param shift- the shift selected by user in the search page
     */
    public void selShift(Shift shift) {
        for (int i = 0; i < obsShiftList.size(); i++) {
            if (obsShiftList.get(i).getDate().getYear() == shift.getDate().getYear()
                    && obsShiftList.get(i).getDate().getDay() == shift.getDate().getDay()
                    && obsShiftList.get(i).getDate().getMonth().equals(shift.getDate().getMonth())) {
                lstShift.getSelectionModel().select(i);
                break;
            }

        }
    }

    /**
     * Locks the fields
     *
     * @param value a boolean value which selects either to disable the fields
     * or not
     */
    public void lockFields(boolean value) {

        cmbJT.setDisable(value);
        cmbAMPM0.setDisable(value);
        cmbAMPM1.setDisable(value);
        cmbDay.setDisable(value);
        cmbDate.setDisable(value);
        cmbMonth.setDisable(value);
        cmbYear.setDisable(value);
        cmbStart.setDisable(value);
        cmbEnd.setDisable(value);

    }

    /**
     * Clears all the fields
     *
     *
     */
    public void clearFields() {
        cmbJT.getSelectionModel().select(0);
        cmbJT.getSelectionModel().clearSelection();
        cmbYear.getSelectionModel().clearSelection();
        cmbAMPM0.getSelectionModel().clearSelection();
        cmbAMPM1.getSelectionModel().clearSelection();
        cmbDay.getSelectionModel().clearSelection();
        cmbDate.getSelectionModel().clearSelection();
        cmbMonth.getSelectionModel().clearSelection();
        cmbStart.getSelectionModel().clearSelection();
        cmbEnd.getSelectionModel().clearSelection();

    }

    /**
     * Sets the default view
     */
    public void setDefaultView() {
        lockFields(true);
        btnAddShift.setDisable(false);
        btnExit.setDisable(false);
        btnSearch.setDisable(false);
        btnSave.setDisable(true);
        btnCancel.setDisable(true);
        btnDel.setDisable(true);
        btnEdit.setDisable(true);
        btnAddJob.setDisable(false);

    }

    /**
     * Methods using the properties of shift time and binding it to calculate
     * hrs, this method is binded to four fields cmbStart,cmbAMPM0,cmbEnd and
     * cmbAMPM0
     */
    public void calculateHrsWorked() {

        if (cmbEnd.getSelectionModel().getSelectedItem() != null && cmbStart.getSelectionModel().getSelectedItem() != null
                && cmbAMPM0.getSelectionModel().getSelectedItem() != null && cmbAMPM1.getSelectionModel().getSelectedItem() != null) {
            ShiftTime shiftST = new ShiftTime(cmbAMPM0.getSelectionModel().getSelectedItem());
            ShiftTime shiftET = new ShiftTime(cmbAMPM1.getSelectionModel().getSelectedItem());
            shiftST.setTime(cmbStart.getSelectionModel().getSelectedItem());
            shiftET.setTime(cmbEnd.getSelectionModel().getSelectedItem());
            String startSel = shiftST.getMeridian();
            String endSel = shiftET.getMeridian();
            int end = shiftET.getTime();
            int start = shiftST.getTime();
            if (startSel != null && endSel != null) {
                if ((startSel.equals("AM") && endSel.equals("PM")) || (startSel.equals("PM") && endSel.equals("AM"))) {
                    calHrsWorked = (shiftET.timeProperty().subtract(shiftST.timeProperty())).add(12);
                } else {
                    if (end < start) {
                        calHrsWorked = (shiftET.timeProperty().subtract(shiftST.timeProperty())).add(24);
                    } else {
                        calHrsWorked = (shiftET.timeProperty().subtract(shiftST.timeProperty()));
                    }
                }
            }

            lblHrsWorked.textProperty().bind(calHrsWorked.asString());
        }

    }

    /**
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //sets the deafult view
        setDefaultView();

        //reading from the file and adding all the shifts added by the user 
        File shifts = new File("shifts.txt");
        try {
            int year = 0, date1, hrsWorked, shiftST, shiftET;
            double wages, payRate;
            final int RECORD = 124;
            RandomAccessFile rafShift = new RandomAccessFile(shifts, "rw");
            long numRecord = rafShift.length() / RECORD;
            rafShift.seek(0);
            for (int i = 0; i < numRecord; i++) {
                String jobTitle = "", meridianST = "", meridianET = "", month = "", day = "";

                for (int c = 0; c < 20; c++) {
                    jobTitle += String.valueOf(rafShift.readChar());
                }
                payRate = rafShift.readDouble();
                year = rafShift.readInt();
                for (int k = 0; k < 10; k++) {
                    month += String.valueOf(rafShift.readChar());
                }
                date1 = rafShift.readInt();
                for (int k = 0; k < 10; k++) {
                    day += String.valueOf(rafShift.readChar());
                }
                shiftST = rafShift.readInt();
                for (int j = 0; j < 2; j++) {
                    meridianST += String.valueOf(rafShift.readChar());
                }
                shiftET = rafShift.readInt();
                for (int l = 0; l < 2; l++) {
                    meridianET += String.valueOf(rafShift.readChar());
                }
                hrsWorked = rafShift.readInt();
                wages = rafShift.readDouble();

                //creating the data fields of shift after reading from file
                Job job = new Job(jobTitle.trim(), payRate);

                Date date = new Date(year, month.trim(), date1);

                ShiftTime st = new ShiftTime(meridianST.trim());
                st.setTime(shiftST);

                ShiftTime et = new ShiftTime(meridianET.trim());
                et.setTime(shiftET);

                //shift that needs to be added to the listview
                Shift shift = new Shift(job, date, day.trim(), st, et, hrsWorked, wages);
                if (!shift.getJob().getJobTitle().equals("")) {
                    //adding the shifts to the observable list
                    obsShiftList.add(shift);
                }
            }
            //adding the observable list to the shift
            lstShift.setItems(obsShiftList);

            //the following determines that if one or more shift have the same job
            //then that  job is not added multiple times
            Job prevJob = new Job("", 0);
            for (int i = 0; i < obsShiftList.size(); i++) {
                Job job = new Job(obsShiftList.get(i).getJob().getJobTitle(),
                        obsShiftList.get(i).getJob().getPayRate());
                if (!(prevJob.getJobTitle().equals(job.getJobTitle()) && prevJob.getPayRate() == job.getPayRate())) {
                    jobArrayList.add(job);
                }
                prevJob = job;
            }
            //adding the jobs to the combo box of jobs
            jobList.setAll(jobArrayList);
            cmbJT.setItems(jobList);
        } catch (IOException ex) {
        }

        //adding items to the combo box of shift start and shift end time
        ObservableList<Integer> obsTimeList = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) {
            obsTimeList.add(i);
        }
        cmbEnd.setItems(obsTimeList);
        cmbStart.setItems(obsTimeList);

        //adding items to the date combobox
        ObservableList<Integer> obsDateList = FXCollections.observableArrayList();
        for (int i = 1; i <= 31; i++) {
            obsDateList.add(i);
        }
        cmbDate.setItems(obsDateList);

        //adding items to the year combobox
        ObservableList<Integer> obsYearList = FXCollections.observableArrayList();
        for (int i = 2000; i <= 2050; i++) {
            obsYearList.add(i);
        }
        cmbYear.setItems(obsYearList);

        //adding items to the meridian combobox of the shift start and end time
        ObservableList<String> obsMeridian = FXCollections.observableArrayList();
        obsMeridian.add("AM");
        obsMeridian.add("PM");

        cmbAMPM0.setItems(obsMeridian);
        cmbAMPM1.setItems(obsMeridian);

        //adding items to the month combobox
        ObservableList<String> obsMonthList = FXCollections.observableArrayList();
        obsMonthList.add("January");
        obsMonthList.add("February");
        obsMonthList.add("March");
        obsMonthList.add("April");
        obsMonthList.add("May");
        obsMonthList.add("June");
        obsMonthList.add("July");
        obsMonthList.add("August");
        obsMonthList.add("September");
        obsMonthList.add("October");
        obsMonthList.add("November");
        obsMonthList.add("December");

        cmbMonth.setItems(obsMonthList);

        //adding items to the day combobox
        ObservableList<String> obsDayList = FXCollections.observableArrayList();
        obsDayList.add("Sunday");
        obsDayList.add("Monday");
        obsDayList.add("Tuesday");
        obsDayList.add("Wednesday");
        obsDayList.add("Thursday");
        obsDayList.add("Friday");
        obsDayList.add("Saturday");

        cmbDay.setItems(obsDayList);

        //binding the shift end time to time property using calulatehrsWorked so that when 
        //this changes the hrs work chnages
        cmbEnd.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable obs) {
                calculateHrsWorked();
            }

        });
        //binding the shift start time to time property using calulatehrsWorked so that when 
        //this changes the hrs work chnages
        cmbStart.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable obs) {
                calculateHrsWorked();
            }

        });
        //binding the shift start time meridian to time property using calulatehrsWorked so that when 
        //this changes the hrs work chnages
        cmbAMPM0.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable obs) {
                calculateHrsWorked();
            }

        });
        //binding the end start time meridian to time property using calulatehrsWorked so that when 
        //this changes the hrs work chnages
        cmbAMPM1.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable obs) {
                calculateHrsWorked();
            }

            /*
            NOTE: ALSO THE HOURS WORKED IS CALCULATED ONLY AFTER ALL THESE FOUR FIELDS
            ARE SELECTED, BUT CHANGING IN ONE WILL CHANGE THE HOURS WORKED!
             */
        });

        //adding invalidation listener to combo box of job title, so whenever it
        //changes the foloowing code executes
        cmbJT.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable obs) {
                if (btnAddShift.isDisabled()) {
                    lockFields(false);
                }
            }

        });

        //adding invalidation listener to label of hours worked, so whenever it
        //changes the wages earned
        lblHrsWorked.textProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable obs) {
                double payrate = Double.parseDouble(lblHrsWorked.getText()) * cmbJT.getSelectionModel().getSelectedItem().getPayRate();
                lblWages.setText(Double.toString(payrate));
                lblPayRate.setText("At a pay rate of $" + cmbJT.getSelectionModel().getSelectedItem().getPayRate());
            }

        });

        //adding invalidation listener to the list view so whenever any item is selected
        //the items fields are displayed in the corresponding fields.
        lstShift.getSelectionModel().selectedItemProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                //making sure that any item is selected
                if (lstShift.getSelectionModel().getSelectedItem() != null) {
                    //locking all the fields so user cannot change it without clicking on edit button
                    lockFields(true);

                    //increasing the opacity of all the fields so it becomes visible 
                    //even though it is disabled
                    cmbYear.setStyle("-fx-opacity: 1");
                    cmbDay.setStyle("-fx-opacity: 1");
                    cmbDate.setStyle("-fx-opacity: 1");
                    cmbMonth.setStyle("-fx-opacity: 1");
                    cmbEnd.setStyle("-fx-opacity: 1");
                    cmbAMPM0.setStyle("-fx-opacity: 1");
                    cmbAMPM1.setStyle("-fx-opacity: 1");
                    cmbStart.setStyle("-fx-opacity: 1");
                    cmbJT.setStyle("-fx-opacity: 1");

                    btnEdit.setDisable(false);
                    btnDel.setDisable(false);

                    //selecting all the items from the combobox to be displayed in
                    //the corresponding fields
                    Shift shift = lstShift.getSelectionModel().getSelectedItem();
                    cmbJT.getSelectionModel().select(shift.getJob());
                    cmbYear.getSelectionModel().select(new Integer(shift.getDate().getYear()));
                    cmbMonth.getSelectionModel().select(shift.getDate().getMonth());
                    cmbDate.getSelectionModel().select(new Integer(shift.getDate().getDay()));
                    cmbDay.getSelectionModel().select(shift.getDay());
                    cmbStart.getSelectionModel().select(new Integer(shift.getShiftStartTime().getTime()));
                    cmbEnd.getSelectionModel().select(new Integer(shift.getShiftEndTime().getTime()));
                    cmbAMPM0.getSelectionModel().select(shift.getShiftStartTime().getMeridian());
                    cmbAMPM1.getSelectionModel().select(shift.getShiftEndTime().getMeridian());
                }
            }
        });

        //anonymous class for add shift button
        btnAddShift.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //disabling the list view so that user cannot select any item 
                //from that when adding a shift
                lstShift.setMouseTransparent(true);
                lstShift.setFocusTraversable(false);
                lstShift.setStyle("-fx-opacity: 0.5");

                lstShift.getSelectionModel().clearSelection();
                cmbJT.setDisable(false);
                if (cmbJT.getSelectionModel().getSelectedItem() != null) {
                    lockFields(false);
                }
                clearFields();

                btnAddJob.setDisable(true);
                btnAddShift.setDisable(true);
                btnSave.setDisable(false);
                btnCancel.setDisable(false);
                btnDel.setDisable(true);
                btnEdit.setDisable(true);
            }
        });

        //anonymous class for save  button
        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //making sure that every field is selected otherwise throwing an alert
                if (cmbJT.getSelectionModel().getSelectedItem() == null
                        || cmbDate.getSelectionModel().getSelectedItem() == null
                        || cmbYear.getSelectionModel().getSelectedItem() == null
                        || cmbMonth.getSelectionModel().getSelectedItem() == null
                        || cmbDay.getSelectionModel().getSelectedItem() == null
                        || cmbStart.getSelectionModel().getSelectedItem() == null
                        || cmbEnd.getSelectionModel().getSelectedItem() == null
                        || cmbAMPM1.getSelectionModel().getSelectedItem() == null
                        || cmbAMPM0.getSelectionModel().getSelectedItem() == null) {

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Data Entry Error");
                    alert.setHeaderText("Some field(s) are not selected");
                    alert.setContentText("Please make sure everything is entered correctly!");
                    alert.showAndWait();

                } else {
                    //if everything is selected ok

                    setDefaultView();
                    //making listview enabled again
                    lstShift.setMouseTransparent(false);
                    lstShift.setFocusTraversable(true);
                    lstShift.setStyle("-fx-opacity: 1");

                    btnAddShift.setDisable(false);

                    //storing all the items selected by user
                    String jobTitle = cmbJT.getSelectionModel().getSelectedItem().getJobTitle();
                    double payRate = cmbJT.getSelectionModel().getSelectedItem().getPayRate();
                    Job job = new Job(jobTitle, payRate);
                    ShiftTime shiftST = new ShiftTime(cmbAMPM0.getSelectionModel().getSelectedItem());
                    shiftST.setTime(cmbStart.getSelectionModel().getSelectedItem());
                    ShiftTime shiftET = new ShiftTime(cmbAMPM1.getSelectionModel().getSelectedItem());
                    shiftET.setTime(cmbEnd.getSelectionModel().getSelectedItem());

                    int year = cmbYear.getSelectionModel().getSelectedItem();
                    int date1 = cmbDate.getSelectionModel().getSelectedItem();
                    String month = cmbMonth.getSelectionModel().getSelectedItem();

                    Date date = new Date(year, month, date1);

                    int hrsWorked = Integer.parseInt(lblHrsWorked.getText());

                    double wagesEarned = Double.parseDouble(lblWages.getText());

                    String day = cmbDay.getSelectionModel().getSelectedItem();

                    //if any shift is selected that means the user is editing the shift,
                    //so first getting the record number of that shift and updating that shift
                    //as per the users selection and then updating it in observable list and adding it to
                    //listview
                    if (lstShift.getSelectionModel().getSelectedItem() != null) {
                        int index = lstShift.getSelectionModel().getSelectedIndex();
                        Shift selShift = lstShift.getSelectionModel().getSelectedItem();
                        int recNum = 0;

                        try {
                            File shifts = new File("shifts.txt");

                            int recYear, recDate1, recHrsWorked = 0, recShiftST, recShiftET;
                            double recWages, recPayRate;
                            final Long RECORD = 124L;
                            RandomAccessFile rafShift = new RandomAccessFile(shifts, "rw");
                            long numRecord = rafShift.length() / RECORD;
                            rafShift.seek(0);
                            //reading from the file
                            for (int i = 1; i <= numRecord; i++) {
                                String recJobTitle = "", recMeridianST = "", recMeridianET = "", recMonth = "", recDay = "";

                                for (int c = 0; c < 20; c++) {
                                    recJobTitle += String.valueOf(rafShift.readChar());
                                }
                                recPayRate = rafShift.readDouble();
                                recYear = rafShift.readInt();
                                for (int k = 0; k < 10; k++) {
                                    recMonth += String.valueOf(rafShift.readChar());
                                }
                                recDate1 = rafShift.readInt();
                                for (int k = 0; k < 10; k++) {
                                    recDay += String.valueOf(rafShift.readChar());
                                }
                                recShiftST = rafShift.readInt();
                                for (int j = 0; j < 2; j++) {
                                    recMeridianST += String.valueOf(rafShift.readChar());
                                }
                                recShiftET = rafShift.readInt();
                                for (int l = 0; l < 2; l++) {
                                    recMeridianET += String.valueOf(rafShift.readChar());
                                }
                                recHrsWorked = rafShift.readInt();
                                recWages = rafShift.readDouble();

                                //after reading storing the values into shift data fields
                                Job recJob = new Job(recJobTitle.trim(), payRate);

                                Date recDate = new Date(recYear, recMonth.trim(), recDate1);
                                ShiftTime recSt = new ShiftTime(recMeridianST.trim());
                                recSt.setTime(recShiftST);
                                ShiftTime recEt = new ShiftTime(recMeridianET.trim());
                                recEt.setTime(recShiftET);

                                //creating shift
                                Shift shift = new Shift(recJob, recDate, recDay.trim(), recSt, recEt, recHrsWorked, recWages);
                                // if the selected shift is same as the shift from the file then that is the records that needs
                                //to be updated
                                if (selShift.getDate().getYear() == shift.getDate().getYear()
                                        && selShift.getDate().getDay() == shift.getDate().getDay()
                                        && selShift.getDate().getMonth().equals(shift.getDate().getMonth())) {
                                    recNum = i;
                                    break;
                                }
                            }
                            //updating the record by writing it to the values user selected
                            rafShift.seek((recNum - 1) * 124);
                            int s = 20 - job.getJobTitle().length();
                            for (int j = 0; j < s; j++) {
                                jobTitle += " ";
                            }
                            rafShift.writeChars(jobTitle);
                            rafShift.writeDouble(payRate);
                            rafShift.writeInt(year);
                            int s1 = 10 - month.length();
                            for (int k = 0; k < s1; k++) {
                                month += " ";
                            }
                            rafShift.writeChars(month);
                            rafShift.writeInt(date1);
                            int s2 = 10 - day.length();
                            for (int l = 0; l < s2; l++) {
                                day += " ";
                            }
                            rafShift.writeChars(day);
                            rafShift.writeInt(shiftST.getTime());
                            rafShift.writeChars(shiftST.getMeridian());
                            rafShift.writeInt(shiftET.getTime());
                            rafShift.writeChars(shiftET.getMeridian());
                            rafShift.writeInt(Integer.parseInt(lblHrsWorked.getText()));
                            rafShift.writeDouble(Double.parseDouble(lblWages.getText()));

                        } catch (IOException e) {
                        }
                        //updating the observable list now
                        Shift selItem = obsShiftList.get(index);

                        selItem.getDate().setYear(cmbYear.getSelectionModel().getSelectedItem());
                        selItem.getDate().setMonth(cmbMonth.getSelectionModel().getSelectedItem());
                        selItem.getDate().setDay(cmbDate.getSelectionModel().getSelectedItem());

                        selItem.setShiftEndTime(shiftST);
                        selItem.setShiftStartTime(shiftET);

                        obsShiftList.add(selItem);
                        obsShiftList.remove(obsShiftList.get(index));
                        shiftList.delete(obsShiftList.get(index));

                        //clearing the selection
                        lstShift.getSelectionModel().clearSelection();

                    } else {
                        // if the no shift is selected from the list view then 
                        //that means user is adding a new shift
                        Shift shift = new Shift(job, date, day, shiftST, shiftET, hrsWorked, wagesEarned);

                        shiftList.add(shift);
                        obsShiftList.add(shift);
                        try {
                            //writing to the file 
                            File shifts = new File("shifts.txt");
                            RandomAccessFile rafShift = new RandomAccessFile(shifts, "rw");
                            rafShift.seek(rafShift.length());
                            int s = 20 - job.getJobTitle().length();
                            for (int j = 0; j < s; j++) {
                                jobTitle += " ";
                            }
                            rafShift.writeChars(jobTitle);
                            rafShift.writeDouble(payRate);
                            rafShift.writeInt(year);
                            int s1 = 10 - month.length();
                            for (int k = 0; k < s1; k++) {
                                month += " ";
                            }
                            rafShift.writeChars(month);
                            rafShift.writeInt(date1);
                            int s2 = 10 - day.length();
                            for (int l = 0; l < s2; l++) {
                                day += " ";
                            }
                            rafShift.writeChars(day);
                            rafShift.writeInt(shiftST.getTime());
                            rafShift.writeChars(shiftST.getMeridian());
                            rafShift.writeInt(shiftET.getTime());
                            rafShift.writeChars(shiftET.getMeridian());
                            rafShift.writeInt(Integer.parseInt(lblHrsWorked.getText()));
                            rafShift.writeDouble(60.0);

                        } catch (IOException e) {
                        }
                    }

                    //finally setting all the shifts to the list view and clearing the fields
                    lstShift.setItems(obsShiftList);
                    clearFields();

                }
            }

        });
        //anonymous class for edit  button
        btnEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btnCancel.setDisable(false);
                btnAddJob.setDisable(true);
                btnAddShift.setDisable(true);
                btnDel.setDisable(true);
                btnSave.setDisable(false);
                lockFields(false);
            }
        });

        //anonymous class for search  button
        btnSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    //loading the search page fxml
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("FXMLSearchPage.fxml"));
                    Parent searchPageRoot = loader.load();
                    Stage searchStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    //setting new scene
                    Scene scene = new Scene(searchPageRoot);

                    //changing the scene
                    searchStage.setScene(scene);
                    searchStage.setTitle("SHIFT TRACKER");
                    searchStage.show();

                } catch (IOException e) {

                }
            }

        });

        //anonymous class for exit button
        btnExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event
            ) {
                //throws a new confirmation alert
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you wish to exit?", ButtonType.YES,
                        ButtonType.NO);
                alert.setTitle("Exit Program");
                alert.setHeaderText(null);
                Optional<ButtonType> result = alert.showAndWait();

                //if user clicks yes then the program exits
                if (result.get() == ButtonType.YES) {
                    System.exit(0); //successully terminates the application
                }
            }

        });

        //anonymous class for cancel button
        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event
            ) {
                //the program thrown a new confirmation alert
                Alert cancelAlert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Do you want to discard your changes?", ButtonType.YES,
                        ButtonType.NO);
                cancelAlert.setTitle("Cancel Program");
                Optional<ButtonType> result = cancelAlert.showAndWait();
                // if user selects yes then then the program goes back to the default mode
                if (result.get() == ButtonType.YES) {
                    setDefaultView();
                    lstShift.setMouseTransparent(false);
                    lstShift.setFocusTraversable(true);
                    lstShift.setStyle("-fx-opacity: 1");
                    clearFields();
                }

            }
        });

        //anonymous class for add job button
        btnAddJob.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    //loading the add job page fxml
                    Parent root = FXMLLoader.load(getClass().getResource("FXMLAddJobPage.fxml"));
                    //setting new scene
                    Scene scene = new Scene(root);
                    //setting scene
                    addJobStage.setScene(scene);
                    //chnaging focus to the add job stage, so that user cannot 
                    //access main page if they are in job stage
                    addJobStage.initModality(Modality.APPLICATION_MODAL);
                    addJobStage.setTitle("SHIFT TRACKER");
                    addJobStage.showAndWait();

                    jobList.setAll(jobArrayList);
                    cmbJT.setItems(jobList);
                } catch (IOException ex) {
                } catch (IllegalStateException ex) {
                    addJobStage.showAndWait();
                    jobList.setAll(jobArrayList);
                    cmbJT.setItems(jobList);
                }

            }
        }
        );

        //anonymous class for del button
        btnDel.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event
            ) {
                //asking for confirmation but making sure that there is some shifts to delete
                if (!obsShiftList.isEmpty()) {
                    Alert cancelAlert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Do you want to delete the shift?", ButtonType.YES,
                            ButtonType.NO);
                    cancelAlert.setTitle("Delete Shift");
                    Optional<ButtonType> result = cancelAlert.showAndWait();
                    // if user selects yes then then the program goes back to the default mode
                    if (result.get() == ButtonType.YES) {
                        Shift selShift = lstShift.getSelectionModel().getSelectedItem();

                        obsShiftList.remove(selShift);
                        lstShift.setItems(obsShiftList);

                        //reading from the file to check which records needs to be delete
                        //if the shift selected matched the record shift that means 
                        //that record needs to be deleted
                        File shifts = new File("shifts.txt");
                        try {
                            int year = 0, date1, hrsWorked, shiftST, shiftET;
                            double wages, payRate;
                            final Long RECORD = 124L;
                            RandomAccessFile rafShift = new RandomAccessFile(shifts, "rw");
                            long numRecord = rafShift.length() / RECORD;
                            rafShift.seek(0);
                            for (int i = 1; i <= numRecord; i++) {
                                String jobTitle = "", meridianST = "", meridianET = "", month = "", day = "";

                                for (int c = 0; c < 20; c++) {
                                    jobTitle += String.valueOf(rafShift.readChar());
                                }
                                payRate = rafShift.readDouble();
                                year = rafShift.readInt();
                                for (int k = 0; k < 10; k++) {
                                    month += String.valueOf(rafShift.readChar());
                                }
                                date1 = rafShift.readInt();
                                for (int k = 0; k < 10; k++) {
                                    day += String.valueOf(rafShift.readChar());
                                }
                                shiftST = rafShift.readInt();
                                for (int j = 0; j < 2; j++) {
                                    meridianST += String.valueOf(rafShift.readChar());
                                }
                                shiftET = rafShift.readInt();
                                for (int l = 0; l < 2; l++) {
                                    meridianET += String.valueOf(rafShift.readChar());
                                }
                                hrsWorked = rafShift.readInt();
                                wages = rafShift.readDouble();

                                Job job = new Job(jobTitle.trim(), payRate);

                                Date date = new Date(year, month.trim(), date1);
                                ShiftTime st = new ShiftTime(meridianST.trim());
                                st.setTime(shiftST);
                                ShiftTime et = new ShiftTime(meridianET.trim());
                                et.setTime(shiftET);

                                Shift shift = new Shift(job, date, day.trim(), st, et, hrsWorked, wages);
                                if (selShift.getDate().getYear() == shift.getDate().getYear()
                                        && selShift.getDate().getDay() == shift.getDate().getDay()
                                        && selShift.getDate().getMonth().equals(shift.getDate().getMonth())) {
                                    Long pos = (i - 1) * RECORD;
                                    rafShift.seek(pos);
                                    //writing zeros to delete the record
                                    for (int k = 0; k < RECORD / 4; k++) {
                                        rafShift.writeInt(0);
                                        pos = pos + 1;
                                    }

                                }
                            }

                        } catch (IOException ex) {
                        }

                    }
                }

            }
        }
        );

    }

}
