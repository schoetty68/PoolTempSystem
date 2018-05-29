package org.rrd4j.demo;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.SwingConstants;
import javax.swing.JButton;

public class PoolView extends JFrame{

	private JPanel contentPane;

	private JLabel einlaufTemp = new JLabel("");
	private JLabel auslaufTemp = new JLabel("");
	private JButton stopBtn = new JButton("Stop");
	
	private PoolThread pt;
	
	public PoolView(PoolThread pt) {
		
		this.pt = pt;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel balkenLinks = new JPanel();
		balkenLinks.setBackground(Color.CYAN);
		contentPane.add(balkenLinks, BorderLayout.WEST);
		
		JPanel balkenRechts = new JPanel();
		balkenRechts.setBackground(Color.CYAN);
		contentPane.add(balkenRechts, BorderLayout.EAST);
		
		JPanel balkenUnten = new JPanel();
		balkenUnten.setBackground(Color.CYAN);
		contentPane.add(balkenUnten, BorderLayout.SOUTH);
		
		balkenUnten.add(stopBtn);
		
		JPanel balkenOben = new JPanel();
		balkenOben.setBackground(Color.CYAN);
		contentPane.add(balkenOben, BorderLayout.NORTH);
		
		JLabel titel = new JLabel("PoolTempSystem");
		titel.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		balkenOben.add(titel);
		
		JPanel mitte = new JPanel();
		contentPane.add(mitte, BorderLayout.CENTER);
		mitte.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblEinlaufTemp = new JLabel("Einlauf Temperatur:");
		lblEinlaufTemp.setHorizontalAlignment(SwingConstants.CENTER);
		mitte.add(lblEinlaufTemp);
		
		mitte.add(einlaufTemp);
		
		JLabel lblAuslaufTemp = new JLabel("Auslauf Temperatur:");
		lblAuslaufTemp.setHorizontalAlignment(SwingConstants.CENTER);
		mitte.add(lblAuslaufTemp);
		
		mitte.add(auslaufTemp);
		
		setVisible(true);
	}
	
	
	public JButton getStopBtn() {
		return stopBtn;
	}

}
