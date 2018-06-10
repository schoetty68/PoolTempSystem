package org.rrd4j.demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class PoolController{
	
	public PoolController(PoolView view, final PoolThread thread) {
		Thread run = new Thread(thread);
		/*try {
			thread.datenHolen();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		try {
			thread.dBErstellen();
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		run.start();
		
		//thread beenden
		view.getStopBtn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.stopen();
			}
		});
		thread.addObserver(view);
	}
	
	
	public static void main(String[] args) {
		PoolThread pt = new PoolThread();
		PoolView pv = new PoolView(pt);
		PoolController controller = new PoolController(pv, pt);
		
	}
}
