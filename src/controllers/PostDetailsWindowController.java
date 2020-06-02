package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.*;
import models.database.DBConnection;
import utility.InputValidator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class PostDetailsWindowController implements Initializable {

    @FXML
    private Label proposedLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;

    @FXML
    private TextField date;

    @FXML
    private TextField proposedPrice;

    @FXML
    private TextField venue;

    @FXML
    private Button upload;

    @FXML
    private TextField askingPrice;

    @FXML
    private TextField highestOffer;

    @FXML
    private Button save;

    @FXML
    private TextArea description;

    @FXML
    private TextField lowestOffer;

    @FXML
    private TextField title;

    @FXML
    private Button delete;

    @FXML
    private TextField minRaise;

    @FXML
    private TextField capacity;

    @FXML
    private TextField attendee_count;

    @FXML
    private Button goBack;

    @FXML
    private TextField id;

    @FXML
    private Button close;

    @FXML
    private TextField status;

    @FXML
    private ImageView image;

    @FXML
    private Label highestLabel;

    @FXML
    private Label lowestLabel;

    @FXML
    private Label askinglabel;

    @FXML
    private Label minLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label attendeeLabel;

    @FXML
    private Label venueLabel;

    @FXML
    private Label capacityLabel;

    @FXML
    private TableColumn<Reply, String> col_id;

    @FXML
    private TableView<Reply> replyTable;

    @FXML
    private TableColumn<Reply, Double> col_reply;

    private Post post;
    private String username;
    private Connection connection;
    private File file = null;
    private ObservableList<Reply> replies = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        id.setDisable(true);
        highestOffer.setDisable(true);
        lowestOffer.setDisable(true);
        attendee_count.setDisable(true);
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
        status.setDisable(true);
        this.connection = DBConnection.getConnection();
    }

    // upload image
    @FXML
    private void uploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().add(imageFilter);
        file = fileChooser.showOpenDialog(null);
        if (file!=null){
            Image img = new Image(file.toURI().toString());
            image.setImage(img);
            try {
                Path movefrom = FileSystems.getDefault().getPath(file.getPath());
                Path target = FileSystems.getDefault().getPath("images/"+file.getName());
                Files.copy(movefrom,target, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Moved");
                System.out.println("Moved !");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // close post
    @FXML
    private void closePost(ActionEvent event) {
        successLabel.setVisible(false);
        errorLabel.setVisible(false);
        this.status.setText("CLOSED");
        this.status.setDisable(true);
        this.close.setDisable(true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION,"The post has been closed now !\nPress Save to save changes in DB");
        alert.show();
    }

    // delete post
    @FXML
    private void deletePost(ActionEvent event) {
        successLabel.setVisible(false);
        errorLabel.setVisible(false);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete the post with ID : " + post.getID() + " ?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Delete Post?");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            DBConnection.deletePost(post);
            goBack(event);
        }
        else if (alert.getResult() == ButtonType.NO) {
            System.out.println("Not deleted !");
        }
    }

    // save post
    @FXML
    private void savePost(ActionEvent event) {
        successLabel.setVisible(false);
        if (title.getText().isEmpty() || description.getText().isEmpty() || status.getText().isEmpty()){
            errorLabel.setText("Input fields Empty !");
            errorLabel.setVisible(true);
        }
        else{
            if (post instanceof EventPost) {
                updateEventPost((EventPost) post);
            }
            else if (post instanceof SalePost) {
                updateSalePost((SalePost) post);
            }
            else if (post instanceof JobPost) {
                updateJobPost((JobPost) post);
            }
        }
    }

    private void updateJobPost(JobPost post) {
        if (this.proposedPrice.getText().isEmpty()){
            errorLabel.setText("Input fields Empty !");
            errorLabel.setVisible(true);
        }
        else if (InputValidator.isDouble(this.proposedPrice) || InputValidator.isInteger(this.proposedPrice)) {
            String fileName = post.getImgName();
            if (file!=null)
                fileName = file.getName();
            DBConnection.updateJobPost(new JobPost(id.getText(), title.getText(), description.getText(), this.username, status.getText()
                    , Double.parseDouble(proposedPrice.getText()), Double.parseDouble(lowestOffer.getText()),fileName));
            successLabel.setVisible(true);
            successLabel.setText("Post Updated Successfully !");
            errorLabel.setVisible(false);
        }
        else{
            errorLabel.setText("Proposed price must be number !");
            errorLabel.setVisible(true);
        }
    }

    private void updateSalePost(SalePost post) {
        if (this.askingPrice.getText().isEmpty() || this.minRaise.getText().isEmpty()){
            errorLabel.setText("Input fields Empty !");
            errorLabel.setVisible(true);
        }
        else if((InputValidator.isDouble(this.askingPrice) || InputValidator.isInteger(this.askingPrice)) &&
                (InputValidator.isDouble(this.minRaise) || InputValidator.isInteger(this.minRaise))) {
            String fileName = post.getImgName();
            if (file!=null)
                fileName = file.getName();
            DBConnection.updateSalePost(new SalePost(id.getText(), title.getText(), description.getText(), this.username, status.getText()
                    , Double.parseDouble(askingPrice.getText()), Double.parseDouble(minRaise.getText()), Double.parseDouble(highestOffer.getText()),fileName));
            successLabel.setVisible(true);
            successLabel.setText("Post Updated Successfully !");
            errorLabel.setVisible(false);
        }
        else{
            errorLabel.setText("Last two fields must be number !");
            errorLabel.setVisible(true);
        }
    }

    private void updateEventPost(EventPost post) {
        if (this.venue.getText().isEmpty() || this.date.getText().isEmpty() || this.capacity.getText().isEmpty()) {
            errorLabel.setText("Input fields Empty !");
            errorLabel.setVisible(true);
        }
        else if (!InputValidator.isInteger(this.capacity)) {
            errorLabel.setText("Capacity must be number !");
            errorLabel.setVisible(true);
        }
        else if (!InputValidator.isDateValid(this.date.getText())){
            errorLabel.setText("Invalid Date !");
            errorLabel.setVisible(true);
        }
        else {
            String fileName = post.getImgName();
            if (file!=null)
                fileName = file.getName();
            DBConnection.updateEventPost(new EventPost(id.getText(), title.getText(), description.getText(), this.username, status.getText()
                    , venue.getText(), date.getText(), Integer.parseInt(capacity.getText()), Integer.parseInt(attendee_count.getText()),fileName));
            successLabel.setVisible(true);
            successLabel.setText("Post Updated Successfully !");
            errorLabel.setVisible(false);
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        Stage stage;
        stage = (Stage) date.getScene().getWindow();
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

    public void initData(String username,Post post){
        this.username = username;
        this.post = post;
        setTableView(post);
        loadItems(post);
        if (replies.size() > 0){
            disableEditing();
        }
    }

    private void disableEditing() {
        title.setDisable(true);
        description.setDisable(true);
        venue.setDisable(true);
        capacity.setDisable(true);
        date.setDisable(true);
        askingPrice.setDisable(true);
        minRaise.setDisable(true);
        proposedPrice.setDisable(true);
        upload.setDisable(true);
    }

    private void loadItems(Post post){
        if (post.getStatus().equalsIgnoreCase("closed"))
            close.setDisable(true);
        if (post instanceof EventPost){
            fetchEventPostDetails((EventPost) post);
            disableSaleItems();
            disableJobItems();
        }
        else if (post instanceof SalePost){
            fetchSalePostDetails((SalePost) post);
            disableEventItems();
            disableJobItems();
        }
        else{
            fetchJobPostDetails((JobPost) post);
            disableEventItems();
            disableSaleItems();
        }
    }

    private void disableEventItems() {
        date.setVisible(false);
        venue.setVisible(false);
        capacity.setVisible(false);
        attendee_count.setVisible(false);
        dateLabel.setVisible(false);
        venueLabel.setVisible(false);
        capacityLabel.setVisible(false);
        attendeeLabel.setVisible(false);
    }

    private void disableSaleItems() {
        askingPrice.setVisible(false);
        highestOffer.setVisible(false);
        minRaise.setVisible(false);
        askinglabel.setVisible(false);
        minLabel.setVisible(false);
        highestLabel.setVisible(false);
    }

    private void disableJobItems() {
        proposedPrice.setVisible(false);
        lowestOffer.setVisible(false);
        lowestLabel.setVisible(false);
        proposedLabel.setVisible(false);
    }

    private void fetchEventPostDetails(EventPost post){
        try {
            ResultSet rs = connection.createStatement().executeQuery("select * from eventposts where id = '"+post.getID()+"';");
            id.setText(rs.getString("id"));
            title.setText(rs.getString("title"));
            description.setText(rs.getString("description"));
            status.setText(rs.getString("status"));
            venue.setText(rs.getString("venue"));
            date.setText(rs.getString("date"));
            capacity.setText(""+rs.getInt("capacity"));
            attendee_count.setText(""+rs.getInt("attendee_count"));
            image.setImage(new Image("file:images/"+rs.getString("img")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchSalePostDetails(SalePost post){
        try {
            ResultSet rs = connection.createStatement().executeQuery("select * from saleposts where id = '"+post.getID()+"';");
            id.setText(rs.getString("id"));
            title.setText(rs.getString("title"));
            description.setText(rs.getString("description"));
            status.setText(rs.getString("status"));
            askingPrice.setText(String.valueOf(rs.getDouble("askingprice")));
            minRaise.setText(String.valueOf(rs.getDouble("minraise")));
            highestOffer.setText(String.valueOf(rs.getDouble("highestoffer")));
            image.setImage(new Image("file:images/"+rs.getString("img")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchJobPostDetails(JobPost post){
        try {
            ResultSet rs = connection.createStatement().executeQuery("select * from jobposts where id = '"+post.getID()+"';");
            id.setText(rs.getString("id"));
            title.setText(rs.getString("title"));
            description.setText(rs.getString("description"));
            status.setText(rs.getString("status"));
            proposedPrice.setText(String.valueOf(rs.getDouble("proposedprice")));
            lowestOffer.setText(String.valueOf(rs.getDouble("lowestoffer")));
            image.setImage(new Image("file:images/"+rs.getString("img")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setTableView(Post post){
        col_id.setCellValueFactory(new PropertyValueFactory<>("responder_ID"));
        col_reply.setCellValueFactory(new PropertyValueFactory<>("value"));
        try {
            ResultSet rs = null;
            if(post instanceof SalePost){
                rs = connection.createStatement().executeQuery("select * from replies where post_id = '"+ post.getID()+"' order by value DESC;");
            }
            else{
                rs = connection.createStatement().executeQuery("select * from replies where post_id = '"+ post.getID()+"' order by value;");
            }
                while (rs.next()){
                replies.add(new Reply(rs.getString("replier_id"),rs.getDouble("value")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        replyTable.setItems(replies);
    }
}
