package entities;

import java.util.ArrayList;

public class Task {
	private String name, description;
	public boolean complete;
	private int id;	
	private ArrayList<String> assignedUsers;

	public Task() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getJSON() {
		String returnString ="{ \"name\":\""+name+"\",\"description\":\""+description
				+"\", \"id\":\""+id+"\",\"complete\":\"" + complete + "\",\"users\":[";
		for(int i = 0; i < assignedUsers.size(); i++) {
			returnString += "\"" + assignedUsers.get(i) + "\"";
			if(i < assignedUsers.size()-1) returnString += ",";
		}
		returnString += "]}";
		return returnString;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ArrayList<String> getAssignedUsers() {
		return assignedUsers;
	}
	public void setAssignedUsers(ArrayList<String> assignedUsers) {
		this.assignedUsers = assignedUsers;
	}
	
}
