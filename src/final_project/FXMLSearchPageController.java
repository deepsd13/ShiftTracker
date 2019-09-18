package final_project;

import final_project.classes.Date;
import final_project.classes.Job;
import final_project.classes.Shift;
import final_project.classes.ShiftTime;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * Search page Controller class
 *
 * @author Deep Shah
 */
public class FXMLSearchPageController implements Initializable {

    //connecting all the fields from fxml file to the controller
    @FXML
    private Button btnSelect, btnCancel, btnSearch;
    @FXML
    private ListView<Shift> lstSearch;
    @FXML
    private ComboBox<Integer> cmbDate, cmbYear;
    @FXML
    private ComboBox<String> cmbMonth;
    @FXML
    private CheckBox chkYear, chkDate, chkMonth;
    @FXML
    private Label lblResult;
    ObservableList<Shift> obsSearchList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //adding items to combobox of date
        ObservableList<Integer> obsDateList = FXCollections.observableArrayList();
        for (int i = 1; i <= 31; i++) {
            obsDateList.add(i);
        }
        cmbDate.setItems(obsDateList);

        //adding items to combobox of year
        ObservableList<Integer> obsYearList = FXCollections.observableArrayList();
        for (int i = 2000; i <= 2050; i++) {
            obsYearList.add(i);
        }
        cmbYear.setItems(obsYearList);

        //adding items to combobox of month
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

        //disabling the comboboxes
        cmbYear.setDisable(true);
        cmbMonth.setDisable(true);
        cmbDate.setDisable(true);

        //adding listener to checkbox of year so when it is selected it 
        //combobox year becomes enabled and false other wise
        chkYear.selectedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (chkYear.isSelected()) {
                    cmbYear.setDisable(false);
                } else {
                    cmbYear.setDisable(true);

                }
            }
        });
        //adding listener to checkbox of month so when it is selected it 
        //combobox month becomes enabled and false other wise
        chkMonth.selectedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (chkMonth.isSelected()) {
                    cmbMonth.setDisable(false);
                } else {
                    cmbMonth.setDisable(true);
                }
            }
        });
        //adding listener to checkbox of date so when it is selected it 
        //combobox date becomes enabled and false other wise
        chkDate.selectedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (chkDate.isSelected()) {
                    cmbDate.setDisable(false);
                } else {
                    cmbDate.setDisable(true);

                }
            }
        });

        //anonymous class for cancel button
        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //the program thrown a new confirmation alert
                Alert cancelAlert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Do you want to discard your changes?", ButtonType.YES,
                        ButtonType.NO);
                cancelAlert.setTitle("Cancel Program");
                cancelAlert.setHeaderText(null);
                Optional<ButtonType> result = cancelAlert.showAndWait();
                // if user selects yes then then the program goes back to the default mode
                if (result.get() == ButtonType.YES) {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("FXMLMainPage.fxml"));
                    try {
                        Parent searchPageRoot = loader.load();
                        Node source = (Node) event.getSource();
                        Stage stage = (Stage) source.getScene().getWindow();
                        Scene scene = new Scene(searchPageRoot);
                        stage.setScene(scene);
                    } catch (IOException ex) {
                    }

                }

            }
        });
        //anonymous class for search button
        btnSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //making sure somthing is selected before searching or else showing
                //error alert
                if (cmbYear.getSelectionModel().getSelectedItem() != null
                        || cmbDate.getSelectionModel().getSelectedItem() != null
                        || cmbMonth.getSelectionModel().getSelectedItem() != null) {

                    lblResult.setText("");
                    obsSearchList.clear();
                    boolean found = false;
                    File shifts = new File("shifts.txt");
                    int error = 0;
                    try {
                        //searching the shift by reading from the file
                        int year, date1, hrsWorked, shiftST, shiftET;
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

                            Job job = new Job(jobTitle.trim(), payRate);
                            Date date = new Date(year, month.trim(), date1);
                            ShiftTime st = new ShiftTime(meridianST.trim());
                            ShiftTime et = new ShiftTime(meridianET.trim());
                            st.setTime(shiftST);
                            et.setTime(shiftET);

                            Shift shift = new Shift(job, date, day.trim(), st, et, hrsWorked, wages);
                            int selDate, selYear;
                            String selMonth;
                            try {

                                //if all the checkbox are selected then checking if sel items
                                //is equal to the record if yes then adding it to the observable lisr
                                if (chkYear.isSelected() && chkMonth.isSelected() && chkDate.isSelected()) {
                                    selYear = cmbYear.getSelectionModel().getSelectedItem();
                                    selMonth = cmbMonth.getSelectionModel().getSelectedItem();
                                    selDate = cmbDate.getSelectionModel().getSelectedItem();

                                    if (selYear == year && selMonth.equals(month.trim()) && selDate == date1) {
                                        obsSearchList.add(shift);
                                        found = true;
                                    }
                                    //if month and year are selected then checking if sel items
                                    //is equal to the record if yes then adding it to the observable lisr
                                } else if (chkYear.isSelected() && chkMonth.isSelected()) {
                                    selYear = cmbYear.getSelectionModel().getSelectedItem();
                                    selMonth = cmbMonth.getSelectionModel().getSelectedItem();

                                    if (selYear == year && selMonth.equals(month.trim())) {
                                        obsSearchList.add(shift);
                                        found = true;
                                    }
                                    //if year and date are selected then checking if sel items
                                    //is equal to the record if yes then adding it to the observable lisr
                                } else if (chkYear.isSelected() && chkDate.isSelected()) {
                                    selYear = cmbYear.getSelectionModel().getSelectedItem();
                                    selDate = cmbDate.getSelectionModel().getSelectedItem();

                                    if (selYear == year && selDate == date1) {
                                        obsSearchList.add(shift);
                                        found = true;
                                    }
                                    //if month and date are selected then checking if sel items
                                    //is equal to the record if yes then adding it to the observable lisr
                                } else if (chkMonth.isSelected() && chkDate.isSelected()) {
                                    selMonth = cmbMonth.getSelectionModel().getSelectedItem();
                                    selDate = cmbDate.getSelectionModel().getSelectedItem();

                                    if (selMonth.equals(month.trim()) && selDate == date1) {
                                        obsSearchList.add(shift);
                                        found = true;
                                    }

                                    //if only month is selected then checking if sel items
                                    //is equal to the record if yes then adding it to the observable lisr
                                } else if (chkMonth.isSelected()) {
                                    selMonth = cmbMonth.getSelectionModel().getSelectedItem();

                                    if (selMonth.equals(month.trim())) {
                                        obsSearchList.add(shift);
                                        found = true;
                                    }
                                    //if only date is selected then checking if sel items
                                    //is equal to the record if yes then adding it to the observable lisr
                                } else if (chkYear.isSelected()) {
                                    selYear = cmbYear.getSelectionModel().getSelectedItem();

                                    if (selYear == year) {
                                        obsSearchList.add(shift);
                                        found = true;
                                    }
                                    //if date is selected selected then checking if sel items
                                    //is equal to the record if yes then adding it to the observable lisr
                                } else if (chkDate.isSelected()) {
                                    selDate = cmbDate.getSelectionModel().getSelectedItem();

                                    if (selDate == date1) {
                                        obsSearchList.add(shift);
                                        found = true;
                                    }
                                }
                            } catch (NullPointerException ex) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Data Entry Error");
                                alert.setHeaderText("No items selected to search");
                                alert.setContentText("Please select something to search");
                                alert.showAndWait();
                                error++;
                                break;
                            }
                        }
                        if (!found && error==0) {
                            //if record is not found
                            lblResult.setText("No records found.Try again!");
                        }
                        lstSearch.setItems(obsSearchList);

                    } catch (IOException ex) {
                    }

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Data Entry Error");
                    alert.setHeaderText("No items selected to search");
                    alert.setContentText("Please select something to search");
                    alert.showAndWait();
                }
            }

        });

        //anonymous class fot select button
        btnSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //getting the current stage
                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();

                Shift shift = lstSearch.getSelectionModel().getSelectedItem();

                try {
                    //if no shift is selected then showing alert error
                    if (shift == null) {

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Data Entry Error");
                        alert.setHeaderText("Shift not selected!");
                        alert.setContentText("Please select a shift!");
                        alert.showAndWait();
                    } else {
                        //loading main page fxml
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("FXMLMainPage.fxml"));
                        Parent searchPageRoot;
                        searchPageRoot = loader.load();

                        //changing the scene back to main page with the selected shifts properties
                        //displayed in the fields
                        Scene scene = new Scene(searchPageRoot);
                        stage.setScene(scene);

                        FXMLMainPageController select = loader.getController();
                        select.selShift(shift);
                    }
                } catch (IOException ex) {
                }

            }
        });

    }
}
