package client_PAMSRV;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/******************************************************************
 * 		Erstellt ein "zoombares" panel auf dem die daten des lidars angezeigt werden
 * 
 ******************************************************************/
public class PAMSRV_view_lidarpanel extends JPanel {
	private PAMSRV_model model;
	
	private volatile int xValue;
	private volatile int yValue;
	private Timer t;
	private volatile double zoomFactor = 0.5;
	private double targetX;
	private double targetY;
	private int targetAngle;
	private int targetDistance;
	
	public PAMSRV_view_lidarpanel(PAMSRV_model model) {
		this.model = model;
		
		this.setBackground(Color.LIGHT_GRAY);
		this.setBorder(new TitledBorder(new EtchedBorder(), "Lidar"));
		
		t = new Timer(25, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PAMSRV_view_lidarpanel.this.repaint();
			}
		});
		t.start();
		
		MouseWheelZoom mwz = new MouseWheelZoom();
		this.addMouseWheelListener(mwz);
		model.debugPrint("LidarPanel gestartet");
	}//End: Konstruktor
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (model.isLidarStarted()) {
			g.setColor(Color.RED);
			
			while (model.lidar_hasNextAngle()) {
				int key = model.lidar_nextAngle();
				double angle = (double) (key/10);
				if ((key >= 0) && (key <=3600)) {
					double distance = (double) (model.lidar_getDistance(key)/10);
					xValue = (int) (((getWidth()/2) + (distance * Math.cos(angle) * zoomFactor)) );
					//Y eventuell *-1 weil unterschiedliche Koordinatenrichtungen
					yValue = (int) (((getHeight()/2) + (distance * Math.sin(angle) * zoomFactor)) );
					g.fillOval(xValue, yValue, 6, 6);
				}
			}//End: while iterator has next
		}
	}//End: paintComponent()
	
	public void zoomIn() {
		this.zoomFactor += 0.1;
	}
	public void zoomOut() {
		this.zoomFactor -= 0.1;
	}
	
	class MouseWheelZoom implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent mwe) {
			int notches = mwe.getWheelRotation();
			if (notches < 0) {
				zoomIn();
			} else {
				zoomOut();
			}
		}
	}//End: INNER class MouseWheelZoom
	
	class PointGetter implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent me) {
			targetX = ( me.getX() - (getWidth()/2) / zoomFactor) ;
			targetY = ( (me.getY() - (getHeight()/2))*(-1) / zoomFactor );
			targetDistance = (int) Math.sqrt((targetX*targetX)+(targetY*targetY));
			targetAngle = (int) Math.atan(targetX/targetY);
			model.move_setTargetAngle(targetAngle);
			model.move_setTargetDistance(targetDistance);
			model.log_addNewLogEntry("New Target:\t\tAngle: " + targetAngle + "\tDistance: " + targetDistance);
		}
		@Override
		public void mousePressed(MouseEvent me) {	}
		@Override
		public void mouseEntered(MouseEvent me) {	}
		@Override
		public void mouseExited(MouseEvent me) {	}
		@Override
		public void mouseReleased(MouseEvent me) {	}
	}//End: INNER class PointGetter
	
}//End: class LidarPanel
