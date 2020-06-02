package utility;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.EventPost;
import models.JobPost;
import models.Post;
import models.SalePost;

public class GUIElementsGenerator {
    // Get image node
    public static Node getImageNode(Post post) {
        Image image;
        if (post.getImgName() != null)
            image = new Image("file:images/"+post.getImgName(),100,100,false,false);
        else
            image = new Image("file:images/default_img.png",100,100,false,false);
        return new ImageView(image);
    }

    public static HBox getHBoxRoot(){
        HBox root = new HBox(10);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(5, 10, 5, 10));
        return root;
    }
    public static VBox getJobItems(JobPost post) {
        VBox jobItems =  new VBox(10);
        jobItems.getChildren().addAll(new Label("Lowest Offer: "+post.getLowestOffer()),new Label("Proposed Price: "+post.getProposedPrice()));
        return jobItems;
    }

    public static VBox getSaleItemsInVBox(SalePost post, String username) {
        VBox saleItems =  new VBox(10);
        if(post.getCreator_ID().equalsIgnoreCase(username))
            saleItems.getChildren().addAll(new Label("Highest Offer: "+post.getHighestOffer()),new Label("Minimum Raise: "+post.getMinRaise()),new Label("Asking Price: "+post.getAskingPrice()));
        else
            saleItems.getChildren().addAll(new Label("Highest Offer: "+post.getHighestOffer()),new Label("Minimum Raise: "+post.getMinRaise()));
        return saleItems;
    }

    public static VBox getEventItemsInVBox(EventPost post) {
        VBox eventItems =  new VBox(10);
        eventItems.getChildren().addAll(new Label("Venue: "+post.getVenue()),new Label("Date: "+post.getDate()),
                new Label("Capacity: "+post.getCapacity()),new Label("Attendee Count: "+ post.getAttendee_count()));
        return eventItems;
    }

    public static VBox getCommonItemsInVBox(Post post){
        VBox commonItems =  new VBox(10);
        commonItems.getChildren().addAll(new Label("ID: "+post.getID()),new Label("Title: "+post.getTitle()),
                new Label("Description: "+post.getDescription()),new Label("Created by: "+ post.getCreator_ID()),
                new Label("Status: "+post.getStatus()));
        return commonItems;
    }


}
