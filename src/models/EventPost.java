package models;

import java.util.*;

public class EventPost extends Post {

	private String venue;
	private String date;
	private int capacity;
	private int attendee_count;
	
	private ArrayList<String> attendees =new ArrayList<String>();
	
	private Scanner sc = new Scanner(System.in);
	
	
	//Constructor
	public EventPost(String iD, String title, String description, String creator_ID, String status,String venue, String date,
			int capacity, int attendee_count,String imgName) {
		super(iD, title, description, creator_ID,status,imgName);
		this.venue = venue;
		this.date = date;
		this.capacity = capacity;
		this.attendee_count = attendee_count;
	}

	public String getVenue() {
		return venue;
	}

	public String getDate() {
		return date;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getAttendee_count() {
		return attendee_count;
	}

	public void updateAttendee_count() {
		if(this.getAttendees().equals(null)) this.attendee_count = 0;
		else this.attendee_count = this.getAttendees().size();
	}
	
	public ArrayList<String> getAttendees() {
		return attendees;
	}

	public void addAttendee(String user) {
		this.attendees.add(user);
	}
	
	@Override
	public String handleReply(Reply reply) {
		if(this.getAttendees().size() != 0 || this.getAttendees() != null) {
			for(String attendee : this.getAttendees()) {
				if(attendee.equalsIgnoreCase(reply.getResponder_ID())) {
					System.out.println("You've already been registered in this event..!");
					return "already_registered";
				}
			}
		}
		if(this.getStatus().equalsIgnoreCase("closed")) {
			System.out.println("The event has been closed by the host..!");
			return "closed";
		}
		if(this.getAttendee_count() >= this.getCapacity()) {
			System.out.println("The event is full..!");
			return "full";
		}
		this.addAttendee(reply.getResponder_ID());
		this.updateAttendee_count();
		return "success";
	}

}
