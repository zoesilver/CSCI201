package zsilver_csci201_Assignment1c;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
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








public class main {
	public static void main(String[] args) {
		BufferedReader br = null;
		FileReader fr = null;
		String fileName = null;
		int userChoice;
		int toSave;
		StockList stockList = null;
		StockList OriginalstockList = null;


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
			System.out.println("1) Display all public companies\n" + "2) Search for a stock (by ticker)\n"
					+ "3) Search for all stocks on an exchange\n" + "4) Add a new company/stocks\n"
					+ "5) Remove a company\n" + "6) Sort companies\n" + "7) Exit");
			while (true) {
				//https://www.baeldung.com/reading-file-in-java
				Scanner scanner = new Scanner(System.in);
				System.out.println("What do you want to do? ");
				if (scanner.hasNextInt()) {
					userChoice = scanner.nextInt();
					if (userChoice >= 1 && userChoice <= 7) {
						break;
					} else {
						System.out.println("That is not a valid option. ");

					}

				}
				else {
					System.out.println("That is not a valid option. ");
				}

			}
			if (userChoice == 1) {
				List<Stock> stocks = stockList.getData();
				for (int i = 0; i < stocks.size(); i++) {
					//I used the WordUtils library and documentation from Apache Commons
					System.out.println(WordUtils.wrap(stocks.get(i).makeString(),80));

				}

			} else if (userChoice == 2) {
				Scanner scanner3 = new Scanner(System.in);
				boolean noMatch = true;
				while (noMatch == true) {
					System.out.println("What is the ticker of the company you would like to search for? ");
					String userTicker = scanner3.next();
					List<Stock> stocks = stockList.getData();
					for (int i = 0; i < stocks.size(); i++) {
						if (stocks.get(i).getTicker().equalsIgnoreCase(userTicker)) {
							System.out.println(stocks.get(i).getName() + " symbol " + stocks.get(i).getTicker()
									+ " started on " + stocks.get(i).getStartDate() + ", listed on "
									+ stocks.get(i).getExchangeCode());
							noMatch = false;
							break;
						}

					}
					if (noMatch) {
						System.out.println(userTicker + " could not be found.");
					}
				}
			}

			else if (userChoice == 3) {
				Scanner scanner4 = new Scanner(System.in);
				boolean noMatch = true;
				while (noMatch == true) {
					List<Stock> names = new ArrayList<>();
					System.out.println("What Stock Exchange would you like to search for? ");
					String userSE = scanner4.next();
					List<Stock> stocks = stockList.getData();
					for (int i = 0; i < stocks.size(); i++) {
						if (stocks.get(i).getExchangeCode().equalsIgnoreCase(userSE)) {
							names.add(stocks.get(i));
						}

					}
					if (names.size() == 0) {
						System.out.println("No exchange named " + userSE + " found.");
					} else {
						System.out.print(names.get(0).getTicker());
						if (names.size() > 1) {
							for (int i = 1; i < names.size() - 1; i++) {
								System.out.print(", "+names.get(i).getTicker());
							}
							System.out.print(" and " + names.get(names.size() - 1).getTicker());

						}
						System.out.println(" found on the " + userSE);

						noMatch = false;
						break;
					}

				}

			} else if (userChoice == 4) {
				Scanner scanner5 = new Scanner(System.in);
				boolean noMatch = false;

				while (noMatch == false) {
					System.out.println("What is the name of the company you would like to add? ");
					String userName = scanner5.nextLine();
					List<Stock> stocks = stockList.getData();

					boolean entryExists = false;

					for (int i = 0; i < stocks.size(); i++) {
						if (stocks.get(i).getName().equalsIgnoreCase(userName)) {
							System.out.println("There is already an entry for " + userName + ". ");
							entryExists = true;
							break;
						}
					}

					if (!entryExists) {
						Stock newStock = new Stock();

						newStock.setName(userName);

						System.out.println("What is the stock symbol of " + userName + "?");
						String symbol = scanner5.next();
						newStock.setTicker(symbol);
						//chatgpt for the date parsing validation
						while (true) {
							System.out.println("What is the start date of " + userName + "?");
							String date = scanner5.next();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							sdf.setLenient(false); 
							try {
								Date parsedDate = sdf.parse(date);
								newStock.setStartDate(date);
								break;
							} catch (ParseException e) {
								System.out.println("Invalid date format.");											
							}


						}

						while(true) {
							System.out.println("What is the exchange where " + userName + " is listed?");
							String exchange = scanner5.next();
							newStock.setExchangeCode(exchange);
							if ("NYSE".equalsIgnoreCase(newStock.getExchangeCode()) || "NASDAQ".equalsIgnoreCase(newStock.getExchangeCode())) {
								break;
							}
							else {
								System.out.println("Only NASDAQ and NYSE are valid exchange codes.");
							}


						}


						scanner5.nextLine();

						System.out.println("What is the description of " + userName + "?");
						String description = scanner5.nextLine();
						newStock.setDescription(description);

						System.out.println(WordUtils.wrap(newStock.makeString(),80));

						// Add new info into the full stockList
						stocks.add(newStock);
						stockList.setData(stocks);

						noMatch = true;
					}
				}
			}
			else if (userChoice == 5) {
				//print names of all stored companies 
				List<Stock> stocks = stockList.getData();
				for (int i = 0; i < stocks.size(); i++) {
					System.out.println(i+1 + ") "+stocks.get(i).getName());					
				}
				System.out.println("Which company would you like to remove? ");
				Scanner scanner6 = new Scanner(System.in);
				int userChoiceTD = scanner6.nextInt();
				String companyName=""; 
				//add all stocks except the one to be deleted into a new list
				List<Stock> newStocks = new ArrayList<>();
				for (int i = 0; i < stocks.size(); i++) {
					if (i!=userChoiceTD-1) {
						newStocks.add(stocks.get(i));						
					}
					else {
						companyName = stocks.get(i).getName();
					}
				}
				//set old list to new list
				stockList.setData(newStocks);
				System.out.println(companyName+ " is now removed.");





			} else if (userChoice == 6) {
				System.out.println("1) A to Z\n" + "2) Z to A\n" + "How would you like to sort by?");
				Scanner scanner7 = new Scanner(System.in);
				int userSort = scanner7.nextInt();

				List<Stock> preSort = new ArrayList<>(stockList.getData());
				// used chatgpt to understand comparing OpenAI. "ChatGPT." OpenAI, [https://chat.openai.com/]
				if (userSort == 1) {
					preSort.sort(Comparator.comparing(Stock::getName));
					System.out.println("Your companies are now sorted from in alphabetical order (A-Z)");
				} else if (userSort == 2) {
					preSort.sort(Comparator.comparing(Stock::getName).reversed());
					System.out.println("Your companies are now sorted from in reverse alphabetical order (Z-A)");

				} else {
					System.out.println("Please enter 1 or 2.");
					return;
				}

				stockList.setData(preSort);
			}


			else {
				System.out.println("1) Yes\n" + "2) No");
				Scanner scanner2 = new Scanner(System.in);
				while (true) {
					System.out.println("Would you like to save your edits? ");
					if (scanner2.hasNextInt()) {
						toSave = scanner2.nextInt();
						if (toSave == 1 || toSave == 2) {
							if (toSave == 1) {
								// used https://www.tutorialspoint.com/how-to-overwrite-a-line-in-a-txt-file-using-java
								try (FileWriter writer = new FileWriter(fileName)) {
									Gson gson = new GsonBuilder().setPrettyPrinting().create();
									String jsonString = gson.toJson(stockList);
									writer.write(jsonString);
									writer.flush();
								}
								catch (IOException e) {
									System.out.println("Error saving "+fileName);

								}
								System.out.println("Your edits have been saved to " + fileName
										+ " \n Thank you for using my program!");

							}
							else {
								//revert file back to original that has not been modified
								List<Stock> OGstocks = OriginalstockList.getData();
								stockList.setData(OGstocks);
								System.out.println("Your edits have not been saved."
										+ " Thank you for using my program!");


							}

							break;
						} else {
							System.out.println("That is not a valid option. ");
						}
					}

				}
				scanner2.close();

				System.exit(0);

			}


		}

	}
}


