package controllers;

import exceptions.InvalidOfferException;
import exceptions.InvalidPostDetailsException;
import exceptions.InvalidUsernameException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.EventPost;
import models.JobPost;
import models.Post;
import models.SalePost;
import models.database.DBConnection;
import utility.IdGenerator;
import utility.InputValidator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddNewPost implements Initializable {

    @FXML
    private TextField proposedPrice;

    @FXML
    private TextField venue;

    @FXML
    private Label capacityLabel;

    @FXML
    private Label fileUploadLabel;

    @FXML
    private Label minRaiseLabel;

    @FXML
    private ComboBox<String> comboBoxPosts;

    @FXML
    private TextField askingPrice;

    @FXML
    private TextArea description;

    @FXML
    private TextField title;

    @FXML
    private TextField minRaise;

    @FXML
    private TextField capacity;

    @FXML
    private Label dateLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private Label propPriceLabel;

    @FXML
    private Button addPostButton;

    @FXML
    private Label askPriceLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;

    @FXML
    private Label venueLabel;

    @FXML
    private DatePicker date;

    private String username;
    private String typeOfPost;
    Connection connection;

    // Common fields of Posts
    private String postTitle;
    private String postDescription;
    private String postCreator;
    private String postStatus;
    private File file = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboBoxPosts.getItems().addAll("Event Post","Sale Post","Job Post");
        toggleSaleVisibility(false);
        toggleJobVisibility(false);
        toggleEventVisibility(false);
        fileUploadLabel.setVisible(false);
        this.errorLabel.setText("Please select a type of post to enable the buttons and respective fields !");
        this.successLabel.setVisible(false);
        this.addPostButton.setDisable(true);
        // Connection for adding posts
        this.connection = DBConnection.getConnection();
    }

    //Toggle visibilities
    private void toggleEventVisibility(boolean toggle){
        date.setVisible(toggle);
        dateLabel.setVisible(toggle);
        capacity.setVisible(toggle);
        capacityLabel.setVisible(toggle);
        venue.setVisible(toggle);
        venueLabel.setVisible(toggle);
    }

    private void toggleSaleVisibility(boolean toggle){
        minRaiseLabel.setVisible(toggle);
        minRaise.setVisible(toggle);
        askPriceLabel.setVisible(toggle);
        askingPrice.setVisible(toggle);
    }

    private void toggleJobVisibility(boolean toggle){
        proposedPrice.setVisible(toggle);
        propPriceLabel.setVisible(toggle);
    }

    // Actions on Combo Box update
    @FXML
    private void comboBoxUpdated(ActionEvent event) {
        this.typeOfPost = this.comboBoxPosts.getValue();
        if(typeOfPost.equalsIgnoreCase("event post")){
            toggleEventVisibility(true);
            toggleJobVisibility(false);
            toggleSaleVisibility(false);
        }
        else if (typeOfPost.equalsIgnoreCase("sale post")){
            toggleEventVisibility(false);
            toggleJobVisibility(false);
            toggleSaleVisibility(true);
        }
        else if (typeOfPost.equalsIgnoreCase("job post")){
            toggleEventVisibility(false);
            toggleJobVisibility(true);
            toggleSaleVisibility(false);
        }
        this.errorLabel.setText("");
        this.addPostButton.setDisable(false);
    }

    // Back to main window
    @FXML
    private void goBackToMainWindow(ActionEvent event) {
        Stage stage;
        stage = (Stage) cancelButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/main-window.fxml"));
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
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Upload image
    @FXML
    private void uploadImage(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().add(imageFilter);
        file = fileChooser.showOpenDialog(null);
        if (file!=null){
            fileUploadLabel.setVisible(true);
            fileUploadLabel.setText("File selected : "+file.getName());
            try {
                Path movefrom = FileSystems.getDefault().getPath(file.getPath());
                Path target = FileSystems.getDefault().getPath("images/"+file.getName());
                Files.copy(movefrom,target, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Moved");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Add new post
    @FXML
    private void addPost(ActionEvent event) {
        try {
            if (typeOfPost.equalsIgnoreCase("event post"))
                addEventPost();
            else if (typeOfPost.equalsIgnoreCase("sale post"))
                addSalePost();
            else if (typeOfPost.equalsIgnoreCase("job post")) {
                addJobPost();
            }
            else
                System.out.println("Something wrong happened with the ComboBox");
        } catch (InvalidPostDetailsException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.setHeaderText("InvalidPostDetailsException");
            alert.show();
        }
    }

    // Add Job post
    private void addJobPost() throws InvalidPostDetailsException {
        successLabel.setVisible(false);
        getCommonFields();
        if (this.proposedPrice.getText().isEmpty()){
            errorLabel.setText("Input fields can not be empty !");
            throw new InvalidPostDetailsException("Input fields can not be empty !");
        }
            else if (InputValidator.isDouble(this.proposedPrice) || InputValidator.isInteger(this.proposedPrice)){
            double proposedPrice = Double.parseDouble(this.proposedPrice.getText());
            String id = IdGenerator.generateNewID("JOB");
            String fileName = "default_img.png";
            if (file!=null)
                fileName = file.getName();
            DBConnection.addJobPost(new JobPost(id, this.postTitle, this.postDescription, this.postCreator, this.postStatus, proposedPrice, 0,fileName));
            errorLabel.setText("");
            successLabel.setVisible(true);
            fileUploadLabel.setVisible(false);
        }
        else{
            errorLabel.setText("Proposed Price must be an integer or a floating Point and greater than 0!");
            throw new InvalidPostDetailsException("Proposed Price must be an integer or a floating Point and greater than 0");
        }
    }

    // Add Sale post
    private void addSalePost() throws InvalidPostDetailsException {
        successLabel.setVisible(false);
        getCommonFields();
        if (this.askingPrice.getText().isEmpty() || this.minRaise.getText().isEmpty()){
            errorLabel.setText("Input fields can not be empty !");
            throw new InvalidPostDetailsException("Input fields can not be empty !");
        }
        else if((InputValidator.isDouble(this.askingPrice) || InputValidator.isInteger(this.askingPrice)) &&
                (InputValidator.isDouble(this.minRaise) || InputValidator.isInteger(this.minRaise))) {
            double askingPrice = Double.parseDouble(this.askingPrice.getText());
            double minRaise = Double.parseDouble(this.minRaise.getText());
            String id = IdGenerator.generateNewID("SAL");
            String fileName = "default_img.png";
            if (file!=null)
                fileName = file.getName();
            DBConnection.addSalePost(new SalePost(id, this.postTitle, this.postDescription, this.postCreator, this.postStatus, askingPrice, minRaise, 0,fileName));
            errorLabel.setText("");
            successLabel.setVisible(true);
            fileUploadLabel.setVisible(false);
        }
        else{
            errorLabel.setText("Last two fields must be a number and greater than 0 !");
            throw new InvalidPostDetailsException("Last two fields must be a number and greater than 0 ");
        }
    }

    // Add event post
    private void addEventPost() throws InvalidPostDetailsException {
        successLabel.setVisible(false);
        getCommonFields();
        if (this.venue.getText().isEmpty() || this.date.getEditor().getText().isEmpty() || this.capacity.getText().isEmpty()){
            errorLabel.setText("Input fields can not be empty !");
            throw new InvalidPostDetailsException("Input fields can not be empty !");
        }
        else if (!InputValidator.isInteger(this.capacity)){
            errorLabel.setText("Capacity must be numeric and greater than 0 !");
            throw new InvalidPostDetailsException("Capacity must be numeric and greater than 0 !");
        }
        else if (!InputValidator.isDateValid(this.date.getEditor().getText())){
            errorLabel.setText("Date format must be : DD/MM/YYYY, Kindly Use the DatePicker !");
            throw new InvalidPostDetailsException("Date format must be : DD/MM/YYYY, Kindly Use the DatePicker !");
        }
        else {
            String venue = this.venue.getText();
            String date = this.date.getEditor().getText();
            int capacity = Integer.parseInt(this.capacity.getText());
            String id = IdGenerator.generateNewID("EVE");
            String fileName = "default_img.png";
            if (file!=null)
                fileName = file.getName();
            DBConnection.addEventPost(new EventPost(id, this.postTitle, this.postDescription, this.postCreator, this.postStatus, venue, date, capacity, 0,fileName));
            errorLabel.setText("");
            successLabel.setVisible(true);
            fileUploadLabel.setVisible(false);
        }
    }

    // Get common fields from textfields
    private void getCommonFields() throws InvalidPostDetailsException {
        if (title.getText().isEmpty() || description.getText().isEmpty()) {
            errorLabel.setText("Input fields can not be empty !");
            throw new InvalidPostDetailsException("Input fields can not be empty !");
        }
        else {
            this.postTitle = title.getText();
            this.postDescription = description.getText();
            this.postStatus = "OPEN";
            this.postCreator = this.username;
        }
    }

    // Initialize data from last scene
    public void initData(String username) {
        this.username = username;
    }
}
