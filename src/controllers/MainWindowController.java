package controllers;

import exceptions.InvalidOfferException;
import exceptions.PostClosedException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.*;
import models.database.DBConnection;
import utility.GUIElementsGenerator;
import utility.InputValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.function.Predicate;

public class MainWindowController implements Initializable {

    @FXML
    private Button logoutButton;
    @FXML
    private Label userNameHeading;
    @FXML
    private Label mainHeading;
    @FXML
    private ListView<Post> postsListView;
    @FXML
    private ComboBox<String> comboBoxCreator;
    @FXML
    private ComboBox<String> comboBoxType;
    @FXML
    private ComboBox<String> comboBoxStatus;

    private ObservableList<Post> posts;
    private FilteredList<Post> filteredList;
    Connection connection;
    private String username;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setComboBoxes();
        posts = FXCollections.observableArrayList();
        this.mainHeading.setText("You're Logged In");
        filteredList = new FilteredList<>(posts, p -> true);    // All posts in starting
    }

    // Set combo box values
    private void setComboBoxes(){
        this.comboBoxCreator.getItems().addAll("My posts","All Posts");
        this.comboBoxStatus.getItems().addAll("All","Open","Closed");
        this.comboBoxType.getItems().addAll("All","Event Posts","Sale Posts","Job Posts");
    }

    // Set listview and customize cells
    private void setListviewCells(){
        postsListView.setCellFactory(param-> new ListCell<Post>(){
            @Override
            protected void updateItem(Post post,boolean empty){
                super.updateItem(post,empty);
                if(empty || post==null) {
                    setText(null);
                    setGraphic(null);
                }
                else{
                    HBox root = GUIElementsGenerator.getHBoxRoot(); //change
                    VBox commonItems = GUIElementsGenerator.getCommonItemsInVBox(post);
                    root.getChildren().add(GUIElementsGenerator.getImageNode(post));
                    if(post instanceof EventPost){
                        VBox eventItems = GUIElementsGenerator.getEventItemsInVBox((EventPost)post);
                        root.getChildren().addAll(new ImageView(),commonItems,eventItems);
                        root.setStyle("-fx-background-color: lightblue;");
                    }
                    else if(post instanceof SalePost){
                        VBox saleItems = GUIElementsGenerator.getSaleItemsInVBox((SalePost)post,username);
                        root.getChildren().addAll(new ImageView(),commonItems,saleItems);
                        root.setStyle("-fx-background-color: lightpink;");
                    }
                    else{
                        VBox jobItems = GUIElementsGenerator.getJobItems((JobPost)post);
                        root.getChildren().addAll(new ImageView(),commonItems,jobItems);
                        root.setStyle("-fx-background-color: lightyellow;");
                    }
                    Region region = new Region();
                    HBox.setHgrow(region, Priority.ALWAYS);
                    root.getChildren().add(region);
                    Button replyBtn = getReplyButton(post);
                    Button detailsBtn = getDetailsButton(post);
                    root.getChildren().add(replyBtn);
                    if (post.getCreator_ID().equalsIgnoreCase(username))
                        root.getChildren().add(detailsBtn);
                    setText(null);
                    setGraphic(root);
                }
            }
        });
    }

    private Button getReplyButton(Post post){
        Button reply = new Button("Reply");
        reply.setStyle("-fx-background-color: #04d11c;-fx-text-fill: #ffffff;-fx-font-size: 1.5em;");
        if(post instanceof EventPost)
            reply.setText("Join");
        if (post.getCreator_ID().equalsIgnoreCase(this.username))
            reply.setDisable(true);
        reply.setOnAction(actionEvent -> {
            this.connection = DBConnection.getConnection();
            if (post instanceof EventPost)
                joinEvent(post);
            else if (post instanceof SalePost)
                salePostReply(post);
            else
                jobPostReply(post);
            refresh();
           //Close conn
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return reply;
    }

    // Replies
    private void jobPostReply(Post post) {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Reply to Job Post");
        textInputDialog.setHeaderText("Input your Offer below");
        Optional<String> result = textInputDialog.showAndWait();
        result.ifPresent(value -> {
            Alert alert = new Alert(AlertType.NONE);
            if (InputValidator.isDouble(value) || InputValidator.isInteger(value)){
                Reply reply = new Reply(post.getID(),this.username,Double.parseDouble(value));
                String joined = post.handleReply(reply);
                if (joined.equalsIgnoreCase("closed")){
                    try {
                        throw new PostClosedException("The post has already been closed !");
                    } catch (PostClosedException ex) {
                        alert = new Alert(Alert.AlertType.ERROR,ex.getMessage());
                        alert.setHeaderText("PostClosedException");
                        alert.show();
                    }
                }
                else if (joined.equalsIgnoreCase("not_accepted")){
                    alert.setAlertType(AlertType.ERROR);
                    alert.setHeaderText("Offer Not accepted !");
                    alert.show();
                }
                else{
                    alert.setAlertType(AlertType.INFORMATION);
                    alert.setHeaderText("Congratulations, The offer is accepted !");
                    alert.show();
                    DBConnection.addReply(reply);
                    DBConnection.updatePost(post,reply);
                }
            }
            else{
                try {
                    throw new InvalidOfferException("Invalid Offer Input !");
                } catch (InvalidOfferException e) {
                    alert.setAlertType(AlertType.ERROR);
                    alert.setHeaderText("InvalidOfferException");
                    alert.setContentText(e.getMessage());
                    alert.show();
                }
            }
        });
    }

    private void salePostReply(Post post) {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Reply to Sale Post");
        textInputDialog.setHeaderText("Input your Offer below");
        Optional<String> result = textInputDialog.showAndWait();
        result.ifPresent(value -> {
            Alert alert = new Alert(AlertType.NONE);
            if (InputValidator.isDouble(value) || InputValidator.isInteger(value)){
                Reply reply = new Reply(post.getID(),this.username,Double.parseDouble(value));
                String joined = post.handleReply(reply);
                if (joined.equalsIgnoreCase("already_sold")){
                    try {
                        throw new PostClosedException("The post has already been closed/Item is sold !");
                    } catch (PostClosedException ex) {
                        alert = new Alert(Alert.AlertType.ERROR,ex.getMessage());
                        alert.setHeaderText("PostClosedException");
                        alert.show();
                    }
                }
                else if (joined.equalsIgnoreCase("not_accepted")){
                    alert.setAlertType(AlertType.ERROR);
                    alert.setHeaderText("Offer Not accepted !");
                    alert.show();
                }
                else if (joined.equalsIgnoreCase("submitted")){
                    alert.setAlertType(AlertType.INFORMATION);
                    alert.setHeaderText("Offer Submitted !");
                    alert.show();
                    DBConnection.addReply(reply);
                    DBConnection.updatePost(post,reply);
                }
                else{
                    alert.setAlertType(AlertType.INFORMATION);
                    alert.setHeaderText("Congratulations, The item is sold to you !");
                    alert.show();
                    DBConnection.addReply(reply);
                    DBConnection.updatePost(post,reply);
                }
            }
            else{
                try {
                    throw new InvalidOfferException("Invalid Offer Input !");
                } catch (InvalidOfferException e) {
                    alert.setAlertType(AlertType.ERROR);
                    alert.setHeaderText("InvalidOfferException");
                    alert.setContentText(e.getMessage());
                    alert.show();
                }}
        });
    }

    private void joinEvent(Post post) {
        Alert alert = new Alert(AlertType.NONE);
        Reply reply = new Reply(post.getID(),this.username,1);
        String joined = post.handleReply(reply);
        String dbCheck = DBConnection.getEventReply(post,reply);
        if (joined.equalsIgnoreCase("already_registered") || dbCheck.equalsIgnoreCase("already_registered")){
            alert.setAlertType(AlertType.ERROR);
            alert.setHeaderText("You're Already registered !");
            alert.show();
        }
        else if (joined.equalsIgnoreCase("closed")){
            try {
                throw new PostClosedException("The post has already been closed !");
            } catch (PostClosedException ex) {
                alert = new Alert(Alert.AlertType.ERROR,ex.getMessage());
                alert.setHeaderText("PostClosedException");
                alert.show();
            }
        }
        else if (joined.equalsIgnoreCase("full")){
            alert.setAlertType(AlertType.ERROR);
            alert.setHeaderText("Sorry, the event is full !");
            alert.show();
        }
        else{
            alert.setAlertType(AlertType.INFORMATION);
            alert.setHeaderText("Joined Successfully !");
            alert.show();
            DBConnection.addReply(reply);
            DBConnection.updatePost(post,reply);
        }
    }

    private Button getDetailsButton(Post post){
        Button details = new Button("More Details");
        details.setStyle("-fx-background-color: #f52500;-fx-text-fill: #ffffff;-fx-font-size: 1.5em;");
        details.setOnAction(actionEvent -> {
            //Switch scene with passing username
            Stage stage;
            stage = (Stage) userNameHeading.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/post-details-window.fxml"));
            Scene scene = null;
            try {
                scene = new Scene((Parent)loader.load());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            stage.setScene(scene);
            PostDetailsWindowController postDetailsWindowController = loader.<PostDetailsWindowController>getController();
            postDetailsWindowController.initData(username,post);
            stage.show();
        });
        return details;
    }

    @FXML
    private void showAddPostWindow(ActionEvent e){
        //Switch scene with passing username
        Stage stage;
        stage = (Stage) userNameHeading.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/add-new-post.fxml"));
        Scene scene = null;
        try {
            scene = new Scene((Parent)loader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        stage.setScene(scene);
        AddNewPost addNewPost = loader.<AddNewPost>getController();
        addNewPost.initData(username);
        stage.show();

    }

    // Initialize username passed from LoginController
    public void initData(String username) {
        this.username = username;
        userNameHeading.setText("Welcome " + username);
    }

    // Load data from database and store it into the observable list
    private void loadAllPostsData(){
        try {
            this.connection = DBConnection.getConnection();
            ResultSet resultSet;
            resultSet = DBConnection.getEventPosts();
            while(resultSet.next())
                this.posts.add(new EventPost(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),resultSet.getString(5),resultSet.getString(6),resultSet.getString(7),resultSet.getInt(8),resultSet.getInt(9),resultSet.getString(10)));
            resultSet = DBConnection.getSalePosts();
            while(resultSet.next())
                this.posts.add(new SalePost(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),resultSet.getString(5),resultSet.getDouble(6),resultSet.getDouble(7),resultSet.getDouble(8),resultSet.getString(9)));
            resultSet = DBConnection.getJobPosts();
            while(resultSet.next())
                this.posts.add(new JobPost(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),resultSet.getString(5),resultSet.getDouble(6),resultSet.getDouble(7),resultSet.getString(8)));
            System.out.println("Data loaded from database successfully : " + posts.size());
            if (posts.size() == 0){
                Alert alert = new Alert(AlertType.WARNING,"No Data in DB");
                alert.setHeaderText("DB empty");
                alert.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        Stage stage;
        stage = (Stage) logoutButton.getScene().getWindow();
        FXMLLoader  loader = new FXMLLoader(getClass().getResource("../views/login-window.fxml"));
        Scene scene = null;
        try {
            scene = new Scene((Parent)loader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        stage.setScene(scene);
        stage.show();
        System.out.println("Logout Successfull");
    }

    @FXML
    private void LoadData(ActionEvent actionEvent){
        refresh();
    }

    public void typeBoxUpdated(ActionEvent actionEvent) {
        Predicate<Post> type = post -> {
            if (comboBoxType.getValue().isEmpty() || comboBoxType.getValue() == null)
                return true;
            if (comboBoxType.getValue().equalsIgnoreCase("all"))
                return true;
            if (comboBoxType.getValue().equalsIgnoreCase("event posts"))
                if (post.getID().contains("EVE")) return true;
            if (comboBoxType.getValue().equalsIgnoreCase("sale posts"))
                if (post.getID().contains("SAL")) return true;
            if (comboBoxType.getValue().equalsIgnoreCase("job posts"))
                if (post.getID().contains("JOB")) return true;
            return false;
        };
        filteredList.setPredicate(type.and(filteredList.getPredicate()));
        postsListView.setItems(filteredList);
    }

    public void creatorBoxUpdated(ActionEvent actionEvent) {
        Predicate<Post> creator = post -> {
            if (comboBoxCreator.getValue().isEmpty() || comboBoxCreator.getValue() == null)
                return true;
            if (comboBoxCreator.getValue().equalsIgnoreCase("all posts"))
                return true;
            if (comboBoxCreator.getValue().equalsIgnoreCase("my posts"))
                if (post.getCreator_ID().equalsIgnoreCase(this.username)) return true;
            return false;
        };
        filteredList.setPredicate(creator.and(filteredList.getPredicate()));
        postsListView.setItems(filteredList);
    }

    public void statusBoxUpdated(ActionEvent actionEvent) {
        Predicate<Post> status = post -> {
            if (comboBoxStatus.getValue().isEmpty() || comboBoxStatus.getValue() == null)
                return true;
            if (comboBoxStatus.getValue().equalsIgnoreCase("all"))
                return true;
            if (comboBoxStatus.getValue().equalsIgnoreCase("open"))
                if (post.getStatus().equalsIgnoreCase("open")) return true;
            if (comboBoxStatus.getValue().equalsIgnoreCase("closed"))
                if (post.getStatus().equalsIgnoreCase("closed")) return true;
            return false;
        };
        filteredList.setPredicate(status.and(filteredList.getPredicate()));
        postsListView.setItems(filteredList);
    }

    private void refresh(){
        comboBoxStatus.setValue("All");
        comboBoxType.setValue("All");
        comboBoxCreator.setValue("All Posts");
        this.posts.clear();
        loadAllPostsData();
        postsListView.setItems(posts);
        setListviewCells();
    }

    @FXML
    private void exportData(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(null);
        if(file != null){
            saveToFile(file);
            Alert alert = new Alert(AlertType.INFORMATION,"Exported All the data successfully !");
            alert.setHeaderText("Export Complete !");
            alert.show();
        }
    }

    private void saveToFile(File file) {
        try{
            FileWriter fileWriter;
            fileWriter = new FileWriter(file);
            ResultSet rs;
            connection = DBConnection.getConnection();
            rs = DBConnection.getEventPosts();
            if (rs != null) {
//                fileWriter.write("Event Posts:\n");
                while (rs.next()) {
                    fileWriter.write(String.format("%s,%s,%s,%s,%s,%s,%s,%d,%d,%s\n", rs.getString("id"), rs.getString("title"), rs.getString("description"), rs.getString("creator_id"), rs.getString("status"), rs.getString("venue"), rs.getString("date"), rs.getInt("capacity"), rs.getInt("attendee_count"), rs.getString("img")));
                }
            }
            rs = DBConnection.getSalePosts();
            if (rs != null) {
//                fileWriter.write("Sale Posts:\n");
                while (rs.next()) {
                    fileWriter.write(String.format("%s,%s,%s,%s,%s,%f,%f,%f,%s\n", rs.getString("id"), rs.getString("title"), rs.getString("description"), rs.getString("creator_id"), rs.getString("status"), rs.getDouble("askingprice"), rs.getDouble("minraise"), rs.getDouble("highestoffer"), rs.getString("img")));
                }
            }
            rs = DBConnection.getJobPosts();
            if (rs != null) {
//                fileWriter.write("Job Posts:\n");
                while (rs.next()) {
                    fileWriter.write(String.format("%s,%s,%s,%s,%s,%f,%f,%s\n", rs.getString("id"), rs.getString("title"), rs.getString("description"), rs.getString("creator_id"), rs.getString("status"), rs.getDouble("proposedprice"), rs.getDouble("lowestoffer"), rs.getString("img")));
                }
            }
            rs = DBConnection.getAllReplies();
            if (rs != null) {
//                fileWriter.write("Replies:\n");
                while (rs.next()) {
                    fileWriter.write(String.format("%s,%s,%f\n", rs.getString("post_id"), rs.getString("replier_id"), rs.getDouble("value")));
                }
            }
            fileWriter.close();
        }catch (IOException | SQLException e){
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void importData(ActionEvent event) {
        connection = DBConnection.getConnection();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);
        if(file != null) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()){
                    String[] items = scanner.nextLine().split(",");
                    if (items.length == 10)
                        DBConnection.addEventPost(new EventPost(items[0],items[1],items[2],items[3],items[4],items[5],items[6],Integer.parseInt(items[7]),Integer.parseInt(items[8]),items[9]));
                    else if (items.length == 9)
                        DBConnection.addSalePost(new SalePost(items[0],items[1],items[2],items[3],items[4],Double.parseDouble(items[5]),Double.parseDouble(items[6]),Double.parseDouble(items[7]),items[8]));
                    else if (items.length == 8)
                        DBConnection.addJobPost(new JobPost(items[0],items[1],items[2],items[3],items[4],Double.parseDouble(items[5]),Double.parseDouble(items[6]),items[7]));
                    else if (items.length == 3)
                        DBConnection.addReply(new Reply(items[0],items[1],Double.parseDouble(items[2])));
                    else
                        System.out.println("Line skipped..");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void devInfoDialogShow(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION,"Developer Name: Hikansh Kapoor\nStudent Number: s3803669");
        alert.setHeaderText("Developer Information");
        alert.show();
    }

    @FXML
    private void quitApplication(ActionEvent event) {
        System.exit(1);
    }
}
