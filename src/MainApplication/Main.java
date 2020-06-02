package MainApplication;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.database.DBConnection;

import java.sql.Connection;

public class Main extends Application {

    private Connection connection;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../views/login-window.fxml"));
        primaryStage.setTitle("UniLink");
        primaryStage.setScene(new Scene(root, 1183, 707));
        primaryStage.show();
        connect();
    }

//DB Connection
    private void connect(){
        this.connection = DBConnection.getConnection();
        if(this.connection == null){
            System.out.println("Error while connecting the database !");
            System.exit(1);
        }
        else
            System.out.println("Database Connected !");
    }

    public static void main(String[] args) {
        launch(args);
    }

}
