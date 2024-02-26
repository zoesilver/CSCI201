package zsilver_csci201_Assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.text.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.util.concurrent.*; 
import java.util.concurrent.Semaphore;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.*;





public class main {
	public static void main(String[] args) {
		BufferedReader br = null;
		FileReader fr = null;
		String fileName = null;
		int userChoice;
		int toSave;
		StockList stockList = null;
		StockList OriginalstockList = null;
		List<Schedule> scheduleList = new ArrayList<>();
		int balance = 0;
		final Object balanceLock = new Object();
		SharedBalance sharedAc = new SharedBalance();



		while (true) {
			try {
				BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("What is the name of the company file? ");
				try{
					fileName = consoleReader.readLine();
				}
				catch (IOException ioe) {
					System.out.println("The file "+fileName+" is not formatted properly.");

				}
				if (fileName == null) {
					continue;
				}
				try {
					br = new BufferedReader(new FileReader(fileName));
				} catch (FileNotFoundException e) {
					System.out.println("The file " + fileName + " could not be found.");
					continue; 
				}

				try {
					String temp = "";
					File file = new File(fileName);
					Scanner sc = new Scanner(file);

					while (sc.hasNextLine()) {
						temp += sc.nextLine();

					}
					//https://howtodoinjava.com/gson/gson-jsonparser/
					Gson gson = new Gson();					

					try {
						stockList = gson.fromJson(temp, StockList.class);
					}
					catch (JsonSyntaxException e) {
						System.out.println("The file "+fileName+" is not formatted properly as JSON.");

					}
					List<Stock> stocks = stockList.getData();
					boolean goodF = true;
					//for every object, make sure all data points are filled in and exchange code is only NYSE or NASDAQ
					for (int i = 0; i < stocks.size(); i++) {						
						if (stocks.get(i).getName().isEmpty() ||
								stocks.get(i).getStartDate().isEmpty() ||
								stocks.get(i).getTicker().isEmpty() ||
								stocks.get(i).getExchangeCode().isEmpty() ||
								stocks.get(i).getDescription().isEmpty() ||
								!(stocks.get(i).getExchangeCode().equalsIgnoreCase("NASDAQ") || stocks.get(i).getExchangeCode().equalsIgnoreCase("NYSE"))) {
							System.out.println("The file " + fileName + " is not formatted properly.");
							goodF = false;
							break;
						}

					}
					//if file has issues with data, ask for a new file
					if (!goodF) {
						continue;
					}
					//validate date format
					else {

						OriginalstockList = gson.fromJson(temp, StockList.class);
						//I used chatGPT for date validation. OpenAI. "ChatGPT." OpenAI, [https://chat.openai.com/]
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						sdf.setLenient(false); 
						boolean allDatesValid = true;

						for (int i = 0; i < stocks.size(); i++) {
							try {
								// Parse each stock's start date
								Date parsedDate = sdf.parse(stocks.get(i).getStartDate());
							} catch (ParseException e) {
								// Handle invalid date format
								System.out.println("The file " + fileName + " is not formatted properly (date).");
								allDatesValid = false;
								break; 
							}
						}

						if (allDatesValid) {
							System.out.println("The file " + fileName + " has been properly read.");
							break; // Exit the outer loop if all dates are valid
						}

					}

				} catch (FileNotFoundException fnfe) {
					System.out.println("The file " + fileName + " could not be found. ");

				}  finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException ioe) {
							System.out.print(ioe.getMessage());
						}
					}
				}
			}
			finally {

			}

		}




		while (true) {
			try {

				BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("What is the name of the file containing the schedule information? ");
				try{
					fileName = consoleReader.readLine();
				}
				catch (IOException ioe) {
					System.out.println("1The file "+fileName+" is not formatted properly.");

				}
				if (fileName == null) {
					continue;
				}
				try {
					br = new BufferedReader(new FileReader(fileName));
				} catch (FileNotFoundException e) {
					System.out.println("The file " + fileName + " could not be found.");
					continue; 
				}

				try {

					String temp = "";
					File file = new File(fileName);
					Scanner sc = new Scanner(file);
					//https://www.javatpoint.com/how-to-read-csv-file-in-java
					sc.useDelimiter(",");
					String splitBy = ",";
					Boolean goodF = true; 
					while(sc.hasNext()) {
						temp = sc.nextLine();

						String[] tempArrayStrings = temp.split(splitBy);
						if (tempArrayStrings.length!=4) {
							System.out.println("2The file " + fileName + " is not formatted properly.");
							goodF = false;
							break;
						}
						Schedule scheduleInstance = new Schedule();
						try {
							int start = Integer.parseInt(tempArrayStrings[0]);
							scheduleInstance.setStartTime(start);
						} catch (NumberFormatException e) {
							System.out.println("3The file " + fileName + " is not formatted properly.");
							goodF = false;

						}

						scheduleInstance.setCompanyName(tempArrayStrings[1]);
						try {
							int quant = Integer.parseInt(tempArrayStrings[2]);
							scheduleInstance.setQuant(quant);
						} catch (NumberFormatException e) {
							System.out.println("4The file " + fileName + " is not formatted properly.");
							goodF = false;

						}
						try {
							int price = Integer.parseInt(tempArrayStrings[3]);
							scheduleInstance.setPrice(price);
						} catch (NumberFormatException e) {
							System.out.println("5The file " + fileName + " is not formatted properly.");
							goodF = false;

						}
						List<Stock> stocks = stockList.getData();
						Boolean valid = false;
						for (int i =0; i<stocks.size();i++) {



							if (stocks.get(i).getTicker().equals(scheduleInstance.getCompanyName().toUpperCase())) {

								valid = true;
								break;
							}
						}
						if (valid) {
							scheduleList.add(scheduleInstance);

						}
						else {
							System.out.println("The file " + fileName + " is not formatted properly.");
							goodF = false;

							break;
						}

					}
					if (goodF) {
						System.out.println("The file " + fileName + " has been properly read.");
						sc.close();
					}
					else {
						continue;
					}




				} catch (FileNotFoundException fnfe) {
					System.out.println("The file " + fileName + " could not be found. ");

				}  finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException ioe) {
							System.out.print(ioe.getMessage());
						}
					}
				}
			}
			finally {

			}



			while(true) {

				BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("What is the initial balance? ");
				try{
					String inputString = consoleReader.readLine();
					balance = Integer.parseInt(inputString);
					sharedAc.setBalance(balance);
				} catch (NumberFormatException e) {
					System.out.println("The input is not formatted properly.");

				}

				catch (IOException ioe) {
					System.out.println("Could not read input");

				}
				System.out.println("Starting execution of program...");
				System.out.println("Initial balance: "+balance);


				break;
			}
			while (true) {
				//create individual semaphore for every company
				List<Stock> stocks = stockList.getData();
				SemaphoreMap semaphoreMap = new SemaphoreMap();

				for (int i = 0;i<stocks.size();i++) {
					semaphoreMap.createSemaphore(stocks.get(i).getTicker(), Integer.parseInt(stocks.get(i).getBroker()));


				}
				int operations = scheduleList.size();
				long startTime = System.currentTimeMillis();
				int completedThreads = 0;
				MyThread[] threadsList = new MyThread[operations];

				for (int i = 0; i < operations; i++) {
					threadsList[i] = new MyThread(scheduleList.get(i).getCompanyName(), scheduleList.get(i).getQuant(), scheduleList.get(i).getPrice(), scheduleList.get(i).getStartTime(), sharedAc, balanceLock, startTime);
					threadsList[i].start();
				}
				while (completedThreads < operations) {
					//used ChatGPT
					synchronized (balanceLock) {
						for (MyThread mt : threadsList) {
							if (mt.isCompleted() && !mt.isAccountedFor()) {
								balance += mt.getBalance();
								mt.setAccountedFor(); 
								completedThreads++; 

							}
						}
					}
				}
				System.out.println("All trades completed!");
				break;

			}

		break;

	}


}

}




