package global;

//import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import java.awt.Rectangle;

public class SDOGeometry {

	public SDOGeomType shapeType;
	public double[] coords;
	
	 public SDOGeometry(SDOGeomType shapeType, double[] coords)
	 {		
	        this.shapeType = shapeType;
									 // shapeType = 0, Rectangle
									 // shapeType = 1, Triangle
									 // shapeType = 2, Circle
									 // shapeType = 2, Polygon
	        this.coords = coords; // Coordinates of Shape
	 }
	 
	 public double area() 

		 switch (this.shapeType) {
			 case RECTANGLE:
				 double area = Math.abs(coords[2] - coords[0]) * Math.abs(coords[1] - coords[3]);
				 return area;
				 break;
			 case TRIANGLE:
				 return area = Math.abs((coords[0]*(coords[3]-coords[5])) + (coords[2]*(coords[5]-coords[1])) + (coords[2]*(coords[5]-coords[1])));
				 break;
			 case CIRCLE:
				 double radius = (coord[0] - coord[1]);
				 return area = Math.abs((radius * radius * Math.PI));
				 break;
			 case POLYGON:
				 return area = 0;
				 break;
			 default
				 return area = 0;
				 break;
		 }
	 }
	 
	 public double Distance(SDOGeometry X)
	 {
	        double recA[] = new double[8];
	        double recB[] = new double[8];
	        double distance[] = new double[16];
	        int count=0;

	        recA[0]=coords[0];
	        recA[1]=coords[1];
	        recA[2]=coords[2];
	        recA[3]=coords[3];
	        recA[4]=coords[0];
	        recA[5]=coords[3];
	        recA[6]=coords[2];
	        recA[7]=coords[1];

	        recB[0]=X.coords[0];
	        recB[1]=X.coords[1];
	        recB[2]=X.coords[2];
	        recB[3]=X.coords[3];
	        recB[4]=X.coords[0];
	        recB[5]=X.coords[3];
	        recB[6]=X.coords[2];
	        recB[7]=X.coords[1];

	        for (int i=0;i<8;i=i+2) 
	        {
	            for(int j=0;j<8;j=j+2) 
	            {
	                distance[count]=dist(recA[i],recA[i+1],recB[j],recB[j+1]);
	                count++;
	            }
	        }
	        int p1=0;
	        double minDist=distance[p1];
	        for (p1=0;p1<16;p1++) 
	        {
	            if (distance[p1]<minDist)
	                minDist=distance[p1];
	        }
	        System.out.println("Minimum distance= "+minDist);       
	        return minDist;
	    }
	 
	 private double dist(double x1, double y1, double x2, double y2) 
	 {
		 double dist = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
		 return dist;
	 }
	 
	 public SDOGeometry intersection (SDOGeometry X)
	 {
		 return null;
	 }
}
