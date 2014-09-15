import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class textScrapper {

	public static String getData(String url) {
		HttpURLConnection connection =null;
		BufferedReader rd = null;
		StringBuffer sb = null;
		String line = null;
		String jsp = null;
		URL serverAddress = null;

		try {
			serverAddress = new URL(url);
			connection = null;
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);
			connection.setConnectTimeout(2000);
			connection.connect();

		
			rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			sb = new StringBuffer();

			while ((line = rd.readLine()) != null) {
				if (!line.trim().equals("")) {
					sb.append(line + '\n');
				}
			}

			jsp = sb.toString();

			return jsp;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch( NumberFormatException e){
			e.printStackTrace();
			return null;
		}catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			/* close the connection, set all objects to null */
			connection.disconnect();
			rd = null;
			sb = null;
			connection = null;

		}
	}

	public  int findNumber(ArrayList<String> input) {
		StringBuffer s =new StringBuffer();
		for(int i = 0; i < input.get(0).length();i++){
			char char1 =input.get(0).charAt(i);
			if(char1== ' '){
				s.append('%');
				s.append('2');
				s.append('0');
				
			}else{
				s.append(char1);
			}
		}
		String url = "http://www.shopping.com/products?KW="
				+ s.toString();
		Document doc = createDoc(url);
		if(doc == null){
			return 0;
		}
		/* Find the element containing number of results */
		Element content = doc.getElementById("sortFiltersBox");

		if (content == null) {
			return 0;
		}

		Elements num_results = content.getElementsByClass("numTotalResults");
		for (Element el : num_results) {
			/* get the text content of element */
			String text = el.text();
			int start_index = text.indexOf("of ");
			start_index += 3;
			System.out.println(text);
			String number = text.substring(start_index);
			number = number.replace(",", "");
			
			number = number.trim();
			
			
			return Integer.valueOf(number);

		}

		return 0;
	}
	public static Document createDoc(String input){
		String jsp = getData(input).trim();

		if (jsp == null) {
			System.out.println("Failed to retrieve web page.");
			return null;
		}

		Document doc = Jsoup.parse(jsp);
		return doc;
	}
    
	public static void printOutput(String url) {
		Document doc = createDoc(url);
		if(doc == null){
			return ;
		}

		int num = doc.getElementsByClass("gridBox").size();
		int i = 1;
		while(i <= num) {
			
			String price;
			try {
				price = doc
					.getElementById("priceClickableQA" + i)
					.getElementsByClass("productPrice").text();
			} catch (NullPointerException e) {
				price = doc
						.getElementById("priceProductQA" + i)
						.getElementsByClass("productPrice").text();	
			}

			String title = doc.getElementById("nameQA" + i).text();
			String shippingPrice = doc
					.getElementById("quickLookItem-" + i)
					.getElementsByClass("taxShippingArea").text();
			
			if(!shippingPrice.equals("Free Shipping")) {
				String[] temp = shippingPrice.split(" ");
				if (temp.length == 3 && temp[2].equals("shipping")) {
					shippingPrice = temp[1];
				}				
			}
			
			String vendor = doc
					.getElementById("quickLookItem-" + i)
					.getElementsByClass("newMerchantName").text();
			
			System.out.println("No. " + i + " title: " + title);
			System.out.println("The price of the product"+ price);
			if(shippingPrice.equals("Free Shipping")){
				System.out.println("Free Shipping");
			}else{
				System.out.println("Shipping price: " + shippingPrice);
			}
			System.out.println("Vendor: " + vendor);
			System.out.println("//////////////////////////////////");			
			i++;
		}

	}

	public String find(ArrayList<String> input) {
		/* replace any space between keywords by "%20" */
		int m = Integer.valueOf(input.get(1));
		if(m <= 0){
			return "";
		}
		StringBuffer s =new StringBuffer();
		for(int i = 0; i < input.get(0).length();i++){
			char char1 =input.get(0).charAt(i);
			if(char1== ' '){
				s.append('%');
				s.append('2');
				s.append('0');
				
			}else{
				s.append(char1);
			}
		}
		String link = "http://www.shopping.com/products~PG-" + m +"?KW="
				+ input.get(0);
		return link;
	}

	public static void main(String[] args) {
	
		if (args.length <= 0 || args.length > 2) {
			System.out.println("Please input valid parameters.");
			return;
		}

		ArrayList<String> input = new ArrayList<String>();
		input.add(args[0]);
		textScrapper text = new textScrapper();
		
		if (args.length == 1) {
			int num_results = text.findNumber(input);
			System.out.println("Number of results is : " );
			System.out.println( num_results);
			return ;
		}

		
			try {
				input.add(args[1]);
				String page_link = text.find(input);
				if (page_link == null || page_link == "") {
					System.out.println("we can not find this page");
				} else {
					printOutput(page_link);
				}
			
			} catch (NumberFormatException e){
				e.printStackTrace();
				return ;
			}


	}
}

