package com.aaptrix.databeans;

import java.io.Serializable;

public class GradeData implements Serializable {
	
	String name, marks;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getMarks() {
		return marks;
	}
	
	public void setMarks(String marks) {
		this.marks = marks;
	}
}
