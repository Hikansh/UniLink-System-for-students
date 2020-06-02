package models;

public class Reply {

	private String post_ID;
	private String responder_ID;
	private double value;

	//Constructor
	public Reply(String post_ID, String responder_ID, double value) {
		this.post_ID = post_ID;
		this.responder_ID = responder_ID;
		this.value = value;
	}
	public Reply(String responder_ID, double value) {
		this.responder_ID = responder_ID;
		this.value = value;
	}

	public String getPost_ID() {
		return post_ID;
	}

	public String getResponder_ID() {
		return responder_ID;
	}

	public double getValue() {
		return value;
	}

}
