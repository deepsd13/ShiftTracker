/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package final_project;

import static final_project.FXMLMainPageController.addJobStage;
import final_project.classes.Job;
import static final_project.classes.Job.jobArrayList;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


/**
 * Add page controller class
 * 
 * @author Deep Shah, David Ojesekhoba 
 */
public class FXMLAddJobPageController implements Initializable {

    @FXML
    Button btnSave, btnCancel;
    @FXML
    TextField txtJobTitle, txtPayRate;
    @FXML
    ComboBox cmbJT;
    @FXML
    ComboBox cmbDemo;
    @FXML
    Label lblPayRate;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (txtJobTitle.getText().isEmpty() || txtPayRate.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Data Entry Error");
                    alert.setHeaderText("Missing something!");
                    alert.setContentText("Please make sure every field is entered");
                    alert.showAndWait();

                } else {
                    boolean error = false;
                    double payRate = 0;
                    String jobTitle = txtJobTitle.getText();
                    try {
                        payRate = Double.parseDouble(txtPayRate.getText());

                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Data Entry Error");
                        alert.setHeaderText("Invalid Value Entered");
                        alert.setContentText("Please enter a digit in pay rate");
                        alert.showAndWait();
                        error = true;
                    }
                    if (!error) {
                        jobArrayList.add(new Job(jobTitle, payRate));
                        addJobStage.close();
                    }
                }
            }

        });

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
                    addJobStage.close();
                }
            }

        });

    }
}
