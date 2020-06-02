package controllers;

import exceptions.InvalidUsernameException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField usernameTextField;
    @FXML private Label errorLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorLabel.setVisible(false);
    }

    // Login button clicked
    public void loginAction(ActionEvent e){
        String username = usernameTextField.getText().toString();
        if (username.length() < 1){
            errorLabel.setVisible(true);
            try {
                throw new InvalidUsernameException("Empty Username");
            } catch (InvalidUsernameException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR,ex.getMessage());
                alert.setHeaderText("InvalidUsernameException");
                alert.show();
            }
        }else{
            if(username.charAt(0) == 's' || username.charAt(0) == 'S'){
                errorLabel.setVisible(false);
                //Switch scene with passing username
                Stage stage;
                stage = (Stage) usernameTextField.getScene().getWindow();
                FXMLLoader  loader = new FXMLLoader(getClass().getResource("../views/main-window.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene((Parent)loader.load());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                stage.setScene(scene);
                MainWindowController mainWindowController = loader.<MainWindowController>getController();
                mainWindowController.initData(username);
                stage.show();
                System.out.println("Login Successfull");
            }
            else{
                errorLabel.setVisible(true);
                try {
                    throw new InvalidUsernameException("Invalid Username, Username must start with 's'");
                } catch (InvalidUsernameException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR,ex.getMessage());
                    alert.setHeaderText("InvalidUsernameException");
                    alert.show();
                }
            }
        }
    }
}
