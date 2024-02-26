package zsilver_csci201_Assignment2;

//import com.opencsv.bean.CsvBindByPosition;


public class Schedule {
	private int StartTime;
	
	private String companyName;
	
	private int Quant;
	
   private int Price;
   
   public Schedule() {
   }
	
	 public int getStartTime() {
	        return StartTime;
	    }

	    public void setStartTime(int startTime) {
	        this.StartTime = startTime;
	    }

	    public String getCompanyName() {
	        return companyName;
	    }

	    public void setCompanyName(String companyName) {
	        this.companyName = companyName;
	    }

	    public int getQuant() {
	        return Quant;
	    }

	    public void setQuant(int quant) {
	        this.Quant = quant;
	    }

	    public int getPrice() {
	        return Price;
	    }

	    public void setPrice(int price) {
	        this.Price = price;
	    }
}
