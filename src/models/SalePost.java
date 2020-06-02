package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class SalePost extends Post{

	private double askingPrice; 	//Not visible to users
	private double highestOffer;
	private double minRaise;
	
	private Map<String,Double> offersHistory = new HashMap<String,Double>();
	private Scanner sc = new Scanner(System.in);
	
	//Constructor
	public SalePost(String iD, String title, String description, String creator_ID, String status, double askingPrice, double minRaise,
			double highestOffer,String imgName) {
		super(iD, title, description, creator_ID, status,imgName);
		this.askingPrice = askingPrice;
		this.highestOffer = highestOffer;
		this.minRaise = minRaise;
	}
	
	
	@Override
	public String handleReply(Reply reply) {
		if(this.getStatus().equalsIgnoreCase("closed")) {
			System.out.println("The item has been sold and the post is closed now !");
			return "already_sold";
		}
		if(reply.getValue() < (this.minRaise+this.getHighestOffer())) {
			System.out.println("Offer not accepted !");
			return "not_accepted";
		}
		if(reply.getValue() < this.getAskingPrice()) {
			System.out.println("Offer submitted !\nHowever your offer is below the asking price set by owner..\nThe item is still on sale");
			this.setHighestOffer(reply.getValue());
			this.offersHistory.put(reply.getResponder_ID(), reply.getValue());
			return "submitted";
		}
		if(reply.getValue() >= this.getAskingPrice()) {
			this.setHighestOffer(reply.getValue());
			this.offersHistory.put(reply.getResponder_ID(), reply.getValue());
			this.setStatus("CLOSED");
			LinkedHashMap<String, Double> reverseSortedMap = new LinkedHashMap<>();
			this.offersHistory.entrySet()
			    .stream()
			    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) 
			    .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
			this.offersHistory = reverseSortedMap;
			return "sold";
		}
		return "error";
	}

	public double getAskingPrice() {
		return askingPrice;
	}

	public double getHighestOffer() {
		return highestOffer;
	}

	public void setHighestOffer(double highestOffer) {
		this.highestOffer = highestOffer;
	}

	public double getMinRaise() {
		return minRaise;
	}

}
