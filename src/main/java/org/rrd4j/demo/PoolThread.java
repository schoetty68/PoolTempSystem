package org.rrd4j.demo;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import static org.rrd4j.ConsolFun.AVERAGE;
import static org.rrd4j.DsType.GAUGE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.rrd4j.core.Util;

public class PoolThread extends Observable implements Runnable {
	
	//datenbank
	static final String FILE = "demo";
	private String rrdPath = Util.getRrd4jDemoPath(FILE + ".rrd");
	
	private RrdDef rrdDef;
	private RrdDb rrdDb;
	Sample sample;
	
	//startwert für datenbank
	private long steps = 400;
	private long start=(System.currentTimeMillis());
	private long t =start;
	
	private double wert =1;
	
	//mein kram
	boolean stop = true;
	private float[] temps = new float[2];
	
	public void run() {
		while(stop) {
			
			try {
				datenSpeichern();
				datenAuslesen();
				
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		System.out.println("Thread beendet");
	}
	JSONObject object;
	public void datenHolen() throws Exception{
		String url = "http://schoetty68.no-ip.biz";
		URL obj1 = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj1.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		//add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(
		new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		//print in String
		//System.out.println(response.toString());

	      JSONParser parser = new JSONParser();
	      String s = response.toString();
			
	      
	         Object obj = parser.parse(s);
			object = (JSONObject) obj;	
	         System.out.println("The 1st element of array");
	         System.out.println(object.get("VorlaufTemperatur"));
	         System.out.println();

	        // JSONObject obj2 = (JSONObject)array.get(1);
	         System.out.println("Field \"1\"");
	         System.out.println(object.get("RueckTemperatur"));    

	        /* s = "{}";
	         obj = parser.parse(s);
	         System.out.println(obj);

	         s = "[5,]";
	         obj = parser.parse(s);
	         System.out.println(obj);

	         s = "[5,,2]";
	         obj = parser.parse(s);
	         System.out.println(obj);
	      */
	}
	
	public void stopen() {
		stop = false;
		try {
			rrdDb.close();
			System.out.println("Datenbank geschlossen");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public float[] getWerte() {
		return temps;
	}

	
	public void datenSpeichern() throws IOException{
		
		t =(System.currentTimeMillis());
		try {
			sample.setTime(t);
			
	       
	        //wert ist die variable die in die datenbank geschrieben wird 
	        sample.setValue("vorlauf", wert);
	        sample.setValue("ruecklauf", (wert+1));
	               
	        sample.update();
	        System.out.println("Daten im Sample");        
	              
	        }catch (Exception e){
	        	System.out.println(e.getMessage());
	        }
				
	     wert ++;
	    
	}
	
	public void datenAuslesen() throws IOException{

		System.out.println("Last update time was: " + rrdDb.getLastUpdateTime());
		System.out.println("Last info was: " + rrdDb.getInfo());

		// fetch data
		System.out.println("---------------------------------------------\nDaten aus datenbank\n------------------------------------------");
		FetchRequest request = rrdDb.createFetchRequest(AVERAGE, (start -600000), t);
		System.out.println(t + " Wenn Daten ausgegeben werden");
		System.out.println(request.dump());
		
		
		FetchData fetchData = request.fetchData();
	    long times [] = fetchData.getTimestamps();
	    double values [][] = fetchData.getValues();
	    
	    int count = 0;
	    long millis;
	    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");  
	    
	   for(long time: times) {
	    	millis = time;
	    	
	    	Date resultdate = new Date(millis);
	    	System.out.println(sdf.format(resultdate) + ": "+ values[0][count] + "	" + values[1][count]);
	    	count ++;
	    }
		//System.out.println("HIER DIE DATEN!!!!!!\n" + fetchData.toString());
		System.out.println("== Fetch completed");
	}
	    

	//nur einmal am anfang
	public void dBErstellen ()throws IOException {
		//Brauch ich das?
		//System.setProperty("java.awt.headless","true");
		
		System.out.println("Datenbank erstellen");
		        	        
        // creation
        System.out.println("== Creating RRD file " + rrdPath);
		        
        //start-1 ist der Startwert und 1 die schrittgröße
        
        rrdDef = new RrdDef(rrdPath, start -1 , steps);
        rrdDef.setVersion(2);
        rrdDef.addDatasource("vorlauf", GAUGE, 300, 0, Double.NaN);
        rrdDef.addDatasource("ruecklauf", GAUGE, 300, 0, Double.NaN);
		
        
        rrdDef.addArchive(AVERAGE, 0.5, 1, 600);
        rrdDef.addArchive(AVERAGE, 0.5, 6, 700);
        rrdDef.addArchive(AVERAGE, 0.5, 24, 775);
        rrdDef.addArchive(AVERAGE, 0.5, 288, 797);
		        
        System.out.println(rrdDef.dump());
		     
        rrdDb = new RrdDb(rrdPath, false);
      //  rrdDb = new RrdDb(rrdDef);
        
        try {
        	System.out.println("== RRD file created.");
	        if (rrdDb.getRrdDef().equals(rrdDef)) {
	        	System.out.println("Checking RRD file structure... OK");
		    } 
	        else {
	        	System.out.println("Invalid RRD file created. This is a serious bug, bailing out");
	        	return;
	        }
        }
        catch (Exception e){
        	System.out.println(e.getMessage());
        }
		//um daten in die datenbank zu schreiben       
		sample = rrdDb.createSample();      
		}
}
