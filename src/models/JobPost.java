package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class JobPost extends Post{

	private double proposedPrice;
	private double lowestOffer;
	
	private HashMap<String,Double> offersHistory = new HashMap<String,Double>();

	//Constructor
	public JobPost(String iD, String title, String description, String creator_ID, String status, double proposedPrice,
			double lowestOffer,String imgName) {
		super(iD, title, description, creator_ID,status,imgName);
		this.setProposedPrice(proposedPrice);
		this.setLowestOffer(lowestOffer);
	}

	@Override
	public String handleReply(Reply reply) {
		if(this.getStatus().equalsIgnoreCase("closed")) {
			System.out.println("The post is closed..!");
			return "closed";
		}
		if(this.getProposedPrice() < reply.getValue() || (this.getLowestOffer() <= reply.getValue() && this.getLowestOffer()!=0)) {
			System.out.println("Offer not accepted !");
			return "not_accepted";
		}
		this.setLowestOffer(reply.getValue());
		this.offersHistory.put(reply.getResponder_ID(), reply.getValue());
		System.out.println("Offer accepted..!");
		LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
		this.offersHistory.entrySet()
		    .stream()
		    .sorted(Map.Entry.comparingByValue())
		    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
		 
		return "accepted";
	}

	public double getProposedPrice() {
		return proposedPrice;
	}

	public void setProposedPrice(double proposedPrice) {
		this.proposedPrice = proposedPrice;
	}

	public double getLowestOffer() {
		return lowestOffer;
	}

	public void setLowestOffer(double lowestOffer) {
		this.lowestOffer = lowestOffer;
	}

}
