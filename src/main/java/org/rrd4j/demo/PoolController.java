package org.rrd4j.demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class PoolController{
	
	public PoolController(PoolView view, final PoolThread thread) {
		
		try {
			thread.dBErstellen();
			thread.datenAuslesen();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		thread.start();
		
		//thread beenden
		view.getStopBtn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.stopen();
			}
		});
	}
	
	
	public static void main(String[] args) {
		PoolThread pt = new PoolThread();
		PoolView pv = new PoolView(pt);
		PoolController controller = new PoolController(pv, pt);
		
	}
}
