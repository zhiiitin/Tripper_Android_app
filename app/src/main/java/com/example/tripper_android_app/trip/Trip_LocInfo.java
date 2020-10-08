package com.example.tripper_android_app.trip;

import java.io.Serializable;

public class Trip_LocInfo implements Serializable {

	private String loc_Id;
	private String trip_Id;
	private String name;
	private String address;
	private String staytime;
	private String memo;

	public Trip_LocInfo(String loc_Id, String trip_Id, String name, String address, String staytime, String memo) {
		super();
		this.loc_Id = loc_Id;
		this.trip_Id = trip_Id;
		this.name = name;
		this.address = address;
		this.staytime = staytime;
		this.memo = memo;
	}

	public String getLoc_Id() {
		return loc_Id;
	}

	public void setLoc_Id(String loc_Id) {
		this.loc_Id = loc_Id;
	}

	public String getTrip_Id() {
		return trip_Id;
	}

	public void setTrip_Id(String trip_Id) {
		this.trip_Id = trip_Id;
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

	public String getStaytime() {
		return staytime;
	}

	public void setStaytime(String staytime) {
		this.staytime = staytime;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
