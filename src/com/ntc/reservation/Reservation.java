package com.ntc.reservation;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Scanner;

public class Reservation {

	private ArrayList<Row> layout = new ArrayList<Row>();
	private int totalAvailableSeats;
	
	public int getTotalAvailableSeats() {
		return totalAvailableSeats;
	}

	public void setTotalAvailableSeats(int totalAvailableSeats) {
		this.totalAvailableSeats = totalAvailableSeats;
	}

	//
	private ArrayList<int[]> reservations = new ArrayList<int[]>();
	
	public static void main(String[] args){
		try{
			Scanner scanner = new Scanner(System.in);
	
			Reservation reservation = new Reservation();
			ArrayList<Row> rows = reservation.fillLayout(scanner);
			
			while(scanner.hasNextLine()){
				String input = scanner.nextLine();
				if(input.length() > 1){
					String[] requestParts = input.split(" ");
					int requestedSeats = Integer.parseInt(requestParts[1]);
					if(requestedSeats > reservation.getTotalAvailableSeats()){
						System.out.println(requestParts[0] + " Sorry, we can't handle your party.");
					}
					else if(reservation.canAccomodate(rows, requestedSeats)){
						reservation.allocateSection(reservation, rows, requestParts);
					}
					else{
						System.out.println(requestParts[0] + " Call to split party");
					}
				}
			}
		}
		catch(Exception ex){
			System.out.println("There is problem reading file. Please check the data in file.");
		}
	}
	
	public void allocateSection(Reservation reservation, ArrayList<Row> rows, String[] requestParts){
		int prevAvailableRowIndex = -1;
		int prevAvailableSecIndex = -1;
		boolean isAllotted = false;
		String name = requestParts[0];
		int requestedSeats = Integer.parseInt(requestParts[1]);
		for(int i=0; i< rows.size(); i++){
			ArrayList<Section> sections = rows.get(i).getSections();
			for(int j=0; j< sections.size(); j++){
				Section section = sections.get(j);
				if(requestedSeats == section.getSeatsAvailable()){
					System.out.println(name + " Row "+ (i+1) + " Section " + (j+1));
					section.updateSeatsAvailable(requestedSeats);
					prevAvailableRowIndex = -1;
					prevAvailableSecIndex = -1;
					isAllotted = true;
					break;
				}
				else if(requestedSeats < sections.get(j).getSeatsAvailable()){
					prevAvailableRowIndex = i;
					prevAvailableSecIndex = j;
					break;
				}
			}
			if( prevAvailableRowIndex != -1 && prevAvailableSecIndex != -1){
				ArrayList<Section> nextRowSections = rows.get(prevAvailableRowIndex+1).getSections();
				for(int j=0; j< nextRowSections.size(); j++){
					Section section = nextRowSections.get(j);
					if(requestedSeats == section.getSeatsAvailable()){
						System.out.println(name + " Row "+ (prevAvailableRowIndex+2) + " Section " + (j+1));
						section.updateSeatsAvailable(requestedSeats);
						prevAvailableRowIndex = -1;
						prevAvailableSecIndex = -1;
						isAllotted = true;
						break;
					}
				}
				if(!isAllotted){
					ArrayList<Section> prevRowSections = rows.get(prevAvailableRowIndex).getSections();
					if(reservation.allocatePrevMatch(prevRowSections, requestedSeats, name, prevAvailableRowIndex, prevAvailableSecIndex)){
						isAllotted = true;
						break;
					}
				}
			}
			if(isAllotted){
				break;
			}
		}
	}
	
	public boolean allocatePrevMatch(ArrayList<Section> prevRowSections, int requestedSeats, String name, int prevAvailableRowIndex, int prevAvailableSecIndex){
		for(int j=0; j< prevRowSections.size(); j++){
			if(requestedSeats < prevRowSections.get(j).getSeatsAvailable()){
				Section section = prevRowSections.get(j);
				System.out.println(name + " Row "+ (prevAvailableRowIndex+1) + " Section " + (prevAvailableSecIndex+1));
				section.updateSeatsAvailable(requestedSeats);
				prevAvailableRowIndex = -1;
				prevAvailableSecIndex = -1;
				return true;
			}
		}
		return false;
	}
	
	public boolean canAccomodate(ArrayList<Row> rows, int requestedSeats){
		for(int i=0; i< rows.size(); i++){
			ArrayList<Section> sections = rows.get(i).getSections();
			for(int j=0; j< sections.size(); j++){
				if(requestedSeats <= sections.get(j).getSeatsAvailable()){
					return true;
				}
			}
		}
		return false;
	}
	
	public ArrayList<Row> fillLayout(Scanner scanner){
		ArrayList<Row> rows = new ArrayList<Row>();
		try{
		int totalAvailabe = 0;
		while(scanner.hasNextLine()){
			String input = scanner.nextLine();
			Row row = new Row();
			if(input.length() <= 1){
				break;
			}
			String[] sectionSeats = input.split(" ");
			ArrayList<Section> sections = new ArrayList<Section>();
			for(int i=0; i<sectionSeats.length; i++){
				
				Section section = new Section();
				int seats = Integer.parseInt(sectionSeats[i]);
				totalAvailabe += seats;
				section.setSeatsAvailable(seats);
				sections.add(section);
			}
			row.setSections(sections);
			rows.add(row);	
		}
		this.setTotalAvailableSeats(totalAvailabe);
		}catch(Exception ex){
			throw ex;
		}
		return rows;
	}
}
