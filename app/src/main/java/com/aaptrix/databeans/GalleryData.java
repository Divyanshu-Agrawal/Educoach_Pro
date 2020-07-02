package com.aaptrix.databeans;

import java.io.Serializable;

public class GalleryData implements Serializable {
	
	private String[] images;
	private String title, id;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String[] getImages() {
		return images;
	}
	
	public void setImages(String[] images) {
		this.images = images;
	}
}
