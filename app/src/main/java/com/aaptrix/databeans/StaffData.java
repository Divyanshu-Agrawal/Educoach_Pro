package com.aaptrix.databeans;

import java.io.Serializable;

public class StaffData implements Serializable {
	
	private String id;
	private String name;
	private String image;
	private String leaveStatus;
	private String attenStatus;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String type;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	
	public String getLeaveStatus() {
		return leaveStatus;
	}
	
	public void setLeaveStatus(String leaveStatus) {
		this.leaveStatus = leaveStatus;
	}
	
	public String getAttenStatus() {
		return attenStatus;
	}
	
	public void setAttenStatus(String attenStatus) {
		this.attenStatus = attenStatus;
	}
}
