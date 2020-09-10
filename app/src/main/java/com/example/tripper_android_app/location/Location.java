package com.example.tripper_android_app.location;

import java.io.Serializable;


/**
* 類別說明：景點Bean檔
* @author Connor Fan
* @version 建立時間:Sep 2, 2020 12:57:32 PM
* 
*/
public class Location implements Serializable{
	private static final long serialVersionUID = 1L;
	private int logId;
	private String name;
	private String address;
	private String locType;
	private String city;
	private String info;
	private double longitude;
	private double latitude;
	private int createId;
	private int useId;
	private String createDateTime;
	
	public Location(int logId, String name, String address, String locType, String city, String info,
			double longitude, double latitude, int createId, int useId, String createDateTime) {
		super();
		this.logId = logId;
		this.name = name;
		this.address = address;
		this.locType = locType;
		this.city = city;
		this.info = info;
		this.longitude = longitude;
		this.latitude = latitude;
		this.createId = createId;
		this.useId = useId;
		this.createDateTime = createDateTime;
	}
	
	

	public Location(String name, String address, String locType, String city, String info, double longitude,
			double latitude, int createId, int useId, String createDateTime) {
		super();
		this.name = name;
		this.address = address;
		this.locType = locType;
		this.city = city;
		this.info = info;
		this.longitude = longitude;
		this.latitude = latitude;
		this.createId = createId;
		this.useId = useId;
		this.createDateTime = createDateTime;
	}



	public int getLogId() {
		return logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLocType() {
		return locType;
	}

	public void setLocType(String locType) {
		this.locType = locType;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getCreateId() {
		return createId;
	}

	public void setCreateId(int createId) {
		this.createId = createId;
	}

	public int getUseId() {
		return useId;
	}

	public void setUseId(int useId) {
		this.useId = useId;
	}

	public String getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(String createDateTime) {
		this.createDateTime = createDateTime;
	}
	
	
	
	
}
