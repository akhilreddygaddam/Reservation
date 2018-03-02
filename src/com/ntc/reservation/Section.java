package com.ntc.reservation;

public class Section {
	private int seatsAvailable;
	
	public int getSeatsAvailable() {
		return seatsAvailable;
	}
	public void setSeatsAvailable(int seatsAvailable) {
		this.seatsAvailable = seatsAvailable;
	} 
	
	public void updateSeatsAvailable(int seatsAllotted){
		this.seatsAvailable -= seatsAllotted;
	}
}
