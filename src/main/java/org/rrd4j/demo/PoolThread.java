package org.rrd4j.demo;

import static org.rrd4j.ConsolFun.AVERAGE;
import static org.rrd4j.ConsolFun.MAX;
import static org.rrd4j.ConsolFun.TOTAL;
import static org.rrd4j.DsType.GAUGE;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.rrd4j.core.Util;

public class PoolThread extends Thread {
	
	//datenbank
	static final String FILE = "demo";
	private String rrdPath = Util.getRrd4jDemoPath(FILE + ".rrd");
	
	private RrdDef rrdDef;
	private RrdDb rrdDb;
	Sample sample;
	
	//startwert für datenbank
	//private long start=1;
	private long steps = 10;
	private long start=(System.currentTimeMillis()/1000);
	private long t =start;
	
	private double wert =1;
	
	//mein kram
	boolean stop = true;
	private float[] temps = new float[2];
	
	public void run() {
		while(stop) {
			
			try {
				datenSpeichern();
				System.out.println("------------------\nIn der run() nach dem speichern vorm auslesen\n-------------------");
				datenAuslesen();
				
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		System.out.println("Thread beendet");
	}
	
	public void datenHolen() throws Exception{
		String url = "http://192.168.1.76";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
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
		System.out.println(response.toString());
		
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
		
		t =(System.currentTimeMillis()/1000);
		try {
			sample.setTime(t);
			
	       
	        //wert ist die variable die in die datenbank geschrieben wird 
	        sample.setValue("temp1", wert);
	        sample.setValue("temp2", (wert+1));
	        
	        System.out.println("daten in sample ");
	               
	        sample.update();
	        System.out.println("Sample geupdatet");        
	              
	        }catch (Exception e){
	        	System.out.println(e.getMessage());
	        }
				//t++;
	     wert ++;
	    
	}
	
	public void datenAuslesen() throws IOException{

		// test read-only access!
		System.out.println("File reopen in read-only mode");
		System.out.println("== Last update time was: " + rrdDb.getLastUpdateTime());
		System.out.println("== Last info was: " + rrdDb.getInfo());

		// fetch data
		System.out.println("---------------------------------------------\nDaten aus datenbank\n------------------------------------------");
		FetchRequest request = rrdDb.createFetchRequest(AVERAGE, start, (t));
		System.out.println(t + " Wenn Daten ausgegeben werden ausgegeben");
		System.out.println(request.dump());
		
		
		FetchData fetchData = request.fetchData();
	       
		System.out.println("HIER DIE DATEN!!!!!!\n" + fetchData.toString());
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
        rrdDef.addDatasource("temp1", GAUGE, 300, 0, Double.NaN);
        rrdDef.addDatasource("temp2", GAUGE, 300, 0, Double.NaN);
		
        
        rrdDef.addArchive(AVERAGE, 0.5, 1, 600);
        rrdDef.addArchive(AVERAGE, 0.5, 6, 700);
        rrdDef.addArchive(AVERAGE, 0.5, 24, 775);
        rrdDef.addArchive(AVERAGE, 0.5, 288, 797);
		        
        System.out.println(rrdDef.dump());
		     
        rrdDb = new RrdDb(rrdDef);
        
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
