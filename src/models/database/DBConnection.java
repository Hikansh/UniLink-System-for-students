package models.database;

import javafx.scene.control.Alert;
import models.*;

import java.sql.*;

public class DBConnection {

    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement pstmt;

    public static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:unilink.sqlite");
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Add event post to DB
    public static void addEventPost(EventPost post) {
        if (! getPostById(post.getID(),"event")) {
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate(String.format("Insert into eventposts (id,title,description,creator_id,status,venue,date,capacity,attendee_count,img) " +
                                "values ('%s','%s','%s','%s','%s','%s','%s',%d,%d,'%s');", post.getID(), post.getTitle(), post.getDescription(), post.getCreator_ID()
                        , post.getStatus(), post.getVenue(), post.getDate(), post.getCapacity(), post.getAttendee_count(), post.getImgName()));
                System.out.println("Data added successfully !");
            } catch (SQLException e) {
                showSQLiteException();
            }
        }else{
            System.out.println("ID: "+ post.getID() +" already exists");
            Alert alert = new Alert(Alert.AlertType.ERROR,"ID: "+ post.getID() +" already exists");
            alert.show();
        }
    }

    // Add Sale post to DB
    public static void addSalePost(SalePost post) {
        if (! getPostById(post.getID(),"sale")) {
            try {
            stmt = connection.createStatement();
            stmt.executeUpdate(String.format("Insert into saleposts (id,title,description,creator_id,status,askingprice,minraise,highestoffer,img) " +
                            "values ('%s','%s','%s','%s','%s',%f,%f,%f,'%s');", post.getID(), post.getTitle(), post.getDescription(), post.getCreator_ID(),
                    post.getStatus(), post.getAskingPrice(), post.getMinRaise(), post.getHighestOffer(),post.getImgName()));
            System.out.println("Data added successfully !");
        } catch (SQLException e) {
                showSQLiteException();
        }
        }
        else{
            System.out.println("ID: "+ post.getID() +" already exists");
            Alert alert = new Alert(Alert.AlertType.ERROR,"ID: "+ post.getID() +" already exists");
            alert.show();
        }
    }

    // Add job post to DB
    public static void addJobPost(JobPost post) {
        if (! getPostById(post.getID(),"event")) {
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate(String.format("Insert into jobposts (id,title,description,creator_id,status,proposedprice,lowestoffer,img) " +
                                "values ('%s','%s','%s','%s','%s',%f,%f,'%s');", post.getID(), post.getTitle(), post.getDescription(), post.getCreator_ID()
                        , post.getStatus(), post.getProposedPrice(), post.getLowestOffer(), post.getImgName()));
                System.out.println("Data added successfully !");
            } catch (SQLException e) {
                showSQLiteException();
            }
        }else{
            System.out.println("ID: "+ post.getID() +" already exists");
            Alert alert = new Alert(Alert.AlertType.ERROR,"ID: "+ post.getID() +" already exists");
            alert.show();
        }
    }

    //Get post by id to check for imports
    private static boolean getPostById(String id, String type){
        String query = "";
        if (type.equalsIgnoreCase("event"))
            query = "select * from eventposts where id = '" + id + "';";
        else if (type.equalsIgnoreCase("sale"))
            query = "select * from eventposts where id = '" + id + "';";
        else if (type.equalsIgnoreCase("job"))
            query = "select * from eventposts where id = '" + id + "';";
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            if (!resultSet.isBeforeFirst()) //NO DATA FOUND
                return false;
        } catch (SQLException e) {
            showSQLiteException();
        }
        return true;
    }

    //Update Event post
    public static void updateEventPost(EventPost post){
        String query = "update eventposts set title = ?, description = ?, status = ?, venue = ?, date = ?, capacity = ?, img = ? where id = ?;";
        try {
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,post.getTitle());
            pstmt.setString(2,post.getDescription());
            pstmt.setString(3,post.getStatus());
            pstmt.setString(4,post.getVenue());
            pstmt.setString(5,post.getDate());
            pstmt.setInt(6,post.getCapacity());
            pstmt.setString(7,post.getImgName());
            pstmt.setString(8,post.getID());
            //Update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            showSQLiteException();
        }
        System.out.println("Updated Event post table !");
    }

    // Delete post
    public static void deletePost(Post post){
        String sql = "";
        if (post instanceof EventPost){
            sql = "delete from eventposts where id = '" + post.getID() + "';";
        }
        else if (post instanceof SalePost){
            sql = "delete from saleposts where id = '" + post.getID() + "';";
        }
        else{
            sql = "delete from jobposts where id = '" + post.getID() + "';";
        }
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            System.out.println("Deleted the post successfully !");
        } catch (SQLException e) {
            showSQLiteException();
        }
    }

    // Update Sale Post
    public static void updateSalePost(SalePost post){
        String query = "update saleposts set title = ?, description = ?, status = ?, askingprice = ?, minraise = ?, img = ? where id = ?;";
        try {
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,post.getTitle());
            pstmt.setString(2,post.getDescription());
            pstmt.setString(3,post.getStatus());
            pstmt.setDouble(4,post.getAskingPrice());
            pstmt.setDouble(5,post.getMinRaise());
            pstmt.setString(6,post.getImgName());
            pstmt.setString(7,post.getID());
            //Update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            showSQLiteException();
        }
        System.out.println("Updated Sale post table !");
    }

    // Update Sale Post
    public static void updateJobPost(JobPost post){
        String query = "update jobposts set title = ?, description = ?, status = ?, proposedprice = ?, img = ? where id = ?;";
        try {
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,post.getTitle());
            pstmt.setString(2,post.getDescription());
            pstmt.setString(3,post.getStatus());
            pstmt.setDouble(4,post.getProposedPrice());
            pstmt.setString(5,post.getImgName());
            pstmt.setString(6,post.getID());
            //Update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            showSQLiteException();
        }
        System.out.println("Updated Job post table !");
    }

    // Update counter variables for posts in counters table
    public static void updateCounter(String counter) {
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate(String.format("Update counters set %s = %d where id = 1;", counter, (getCounter(counter) + 1)));
        } catch (SQLException e) {
            showSQLiteException();
        }
    }

    // Get counter variables for ID generator
    public static int getCounter(String counter) {
        try {
            ResultSet rs = connection.createStatement().executeQuery("select * from counters");
            rs.next();
            if (counter.equalsIgnoreCase("eventcounter"))
                return rs.getInt(2);
            else if (counter.equalsIgnoreCase("salecounter"))
                return rs.getInt(3);
            if (counter.equalsIgnoreCase("jobcounter"))
                return rs.getInt(4);

        } catch (SQLException e) {
            showSQLiteException();
        }
        return 0;
    }

    // Add replies
    public static void addReply(Reply reply) {
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate(String.format("Insert into replies (post_id,replier_id,value) values ('%s','%s',%f);",
                    reply.getPost_ID(), reply.getResponder_ID(), reply.getValue()));
            System.out.println("Reply added successfully !");
        } catch (SQLException e) {
            showSQLiteException();
        }
    }

    // Update offers and attendees in posts after reply
    public static void updatePost(Post post, Reply reply) {
        if (post instanceof EventPost) {
            try {
                stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(String.format("Select attendee_count from eventposts where id = '%s';", post.getID()));
                stmt.executeUpdate(String.format("update eventposts set attendee_count = %d where id = '%s';", (rs.getInt("attendee_count") + 1), post.getID()));
                System.out.println("Updated Attendee count");
            } catch (SQLException e) {
                showSQLiteException();
            }
        } else if (post instanceof SalePost) {
            try {
                stmt = connection.createStatement();
                if (((SalePost) post).getAskingPrice() <= reply.getValue())
                    stmt.executeUpdate(String.format("update saleposts set highestoffer = %f, status = 'CLOSED' where id = '%s';", reply.getValue(), post.getID()));
                else
                    stmt.executeUpdate(String.format("update saleposts set highestoffer = %f where id = '%s';", reply.getValue(), post.getID()));
                System.out.println("Updated Highest Offer");
            } catch (SQLException e) {
                showSQLiteException();
            }
        } else {
            try {
                stmt = connection.createStatement();
                stmt.executeUpdate(String.format("update jobposts set lowestoffer = %f where id = '%s';", reply.getValue(), post.getID()));
                System.out.println("Updated Lowest Offer");
            } catch (SQLException e) {
                showSQLiteException();
            }
        }
    }

    public static String getEventReply(Post post, Reply reply) {
        try {
            ResultSet rs = connection.createStatement().executeQuery(String.format("select * from replies where post_id = '%s';", post.getID()));
            while (rs.next()) {
                if (rs.getString("replier_id").equalsIgnoreCase(reply.getResponder_ID()))
                    return "already_registered";
            }
        } catch (SQLException e) {
            showSQLiteException();
        }
        return "";
    }

    public static ResultSet getEventPosts(){
        try {
            return connection.createStatement().executeQuery("select * from eventposts;");
        } catch (SQLException e) {
            showSQLiteException();
        }
        return null;
    }
    public static ResultSet getSalePosts(){
        try {
            return connection.createStatement().executeQuery("select * from saleposts;");
        } catch (SQLException e) {
            showSQLiteException();
        }
        return null;
    }
    public static ResultSet getJobPosts(){
        try {
            return connection.createStatement().executeQuery("select * from jobposts;");
        } catch (SQLException e) {
            showSQLiteException();
        }
        return null;
    }
    public static ResultSet getAllReplies(){
        try {
            return connection.createStatement().executeQuery("select * from replies;");
        } catch (SQLException e) {
            showSQLiteException();
        }
        return null;
    }

    private static void showSQLiteException(){
        Alert alert = new Alert(Alert.AlertType.ERROR,"Database not responding..\nPlease wait and Try again !");
        alert.setHeaderText("DB connection busy");
        alert.show();
    }
}
