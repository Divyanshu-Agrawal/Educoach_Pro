package com.aaptrix.databeans;

import java.io.Serializable;

public class NotificationData implements Serializable {
	
	private String title, message;
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
