package global;

//import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import java.awt.Rectangle;

public class SDOGeometry {
	public enum SDOGeomType {RECTANGLE, TRIANGLE, CIRCLE, POLYGON};
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
    
    	 /*This constructor takes as input an array of doubles, 
         where the first specifies the type of shape we have, and
         the rest are our coordinates necessary for defining the shape */
	 public SDOGeometry(double[] inputArray){
		 this.shapeType = (int) inputArray[0];
		 
		 int lastCoordinate;	//Determines where the last coordinate value is in the array
		 
		 //Determines what shape our input is, so we know how many points to expect
		 switch (this.shapeType) {
			case RECTANGLE:
				lastCoordinate = 9;	//Rectangles have 4 XY coordinates +1 for type
				break;
			
			case TRIANGLE:
				lastCoordinate = 7;	//Triangles have 3 XY coordinates +1 for type
				break;
			
			case CIRCLE:
				lastCoordinate = 5;	//Circles have 2 XY coordinates +1 for type
				break;
			
			case POLYGON:
				lastCoordinate = 9;	//Polynomials use entire array
				break;
			
			default:
				lastCoordinate = 9;
			break;
		 }
		 
		 this.coords = new double[lastCoordinate - 1];	//Creates a new array in coords of the appropriate length
		 
		 for(int i=1; i<coords.length; i++){	//Copy the input array over to our coordinate array
			 coords[i] = inputArray[i+1];
		 }
		 
	 }//end constructor
    
    
    //Take the geometry shape and converts it into an array of doubles
	 public double[] convertToDoubleArray(){
		 double[] output = new double[9];	//new array of the maximum possible length needed
		 output[0] = (double)shapeType;		//the first element of the array indicates type
		 for(int i=0; i<coords.length; i++){	//the rest is a copy of our coordinate array
			output[i+1] = coords[i];
		 }
		 for(int i=coords.length; i<9; i++){	//any array values not needed in our representation are instead given the POSITIVE_INFINITY value
			 output[i+1] = Double.POSITIVE_INFINITY;
		 }
		 
		 return output;
	 }
	 
	 public double area() {

		 switch (this.shapeType) {
			 case RECTANGLE:
				 double area = Math.abs((coords[2]-coords[0]) * (coords[5]-coords[1]));
				 return area;
			 case TRIANGLE:
				 return area = Math.abs((coords[0]*(coords[3]-coords[5])) + (coords[2]*(coords[5]-coords[1])) + (coords[2]*(coords[5]-coords[1])));
			 case CIRCLE:
				 double radius = (coords[0] - coords[2]);
				 return area = Math.abs((radius * radius * Math.PI));
			 case POLYGON:
				 return area = 0;
			 default:
				 return area = 0;
		 }
	 }
	 
	 //Determines the distance between this shape and another, returns the distance (-1 if error)
	 public double Distance(SDOGeometry X)
	 {
	        double distance;
			
			//Find out which distance method is required to be called
			switch (this.shapeType) {
			 //This shape is a rectangle, but we need to determine what the other shape is
			 case RECTANGLE:
				switch (X.shapeType) {
					case RECTANGLE:	//This is rectangle, other is rectangle
						distance = rectangleRectangleDistance(this, X);
						break;
					case TRIANGLE:	//This is rectangle, other is triangle
						distance = rectangleTriangleDistance(this, X);
						break;
					case CIRCLE:	//This is rectangle, other is circle
						distance = rectangleCircleDistance(this, X);
						break;
					case POLYGON:	//This is rectangle, other is Polygon
						distance = rectanglePolygonDistance(this, X);
						break;
					default
						distance = -1;
						break;
				}
				 break;//End cases where this object is rectangle
				 
			//Begin cases where this is a triangle, now we determine what the other shape is	 
			 case TRIANGLE:
				switch (X.shapeType) {
					case RECTANGLE:	//This is triangle, other is rectangle
						distance = rectangleTriangleDistance(X, this);
						break;
					case TRIANGLE:	//This is triangle, other is triangle
						distance = triangleTriangleDistance(this, X);
						break;
					case CIRCLE:	//This is triangle, other is circle
						distance = triangleCircleDistance(this, X);
						break;
					case POLYGON:	//This is triangle, other is Polygon
						distance = trianglePolygonDistance(this, X);
						break;
					default
						distance = -1;
						break;
				}				 
				 break; //End cases where this is a Triangle
			
			//Begin cases where this is a circle, now we determine what the other shape is
			 case CIRCLE:
				switch (X.shapeType) {
					case RECTANGLE:	//This is Circle, other is rectangle
						distance = rectangleCircleDistance(X, this);
						break;
					case TRIANGLE:	//This is Circle, other is triangle
						distance = triangleCircleDistance(X, this);
						break;
					case CIRCLE:	//This is Circle, other is circle
						distance = circleCircleDistance(this, X);
						break;
					case POLYGON:	//This is Circle, other is Polygon
						distance = circlePolygonDistance(this, X);
						break;
					default
						distance = -1;					
						break;
				}				 
				 break;
				 
			//Begin cases where this is a polygon, now we determine what the other shape is
			 case POLYGON:
				switch (X.shapeType) {
					case RECTANGLE:	//This is polygon, other is rectangle
						distance = rectanglePolygonDistance(X, this);
						break;
					case TRIANGLE:	//This is polygon, other is triangle
						distance = trianglePolygonDistance(X, this);
						break;
					case CIRCLE:	//This is polygon, other is circle
						distance = circlePolygonDistance(X, this);
						break;
					case POLYGON:	//This is polygon, other is Polygon
						distance = polygonPolygonDistance(this, X);
						break;
					default
					
					
						break;
				}
				 break;
			//If the shapetype is out of bounds, return -1 as distance to signify error	 
			 default
				 mindistance = -1;
				 break;
		 }
         
         return distance;
			
			/*
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
			
			*/
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
    
    	 /*Determines the distance between two rectangles
	   Param X: a rectangle SDOGeometry object
	   Param Y: a rectangle SDOGeometry object */
	 private double rectangleRectangleDistance(SDOGeometry X, SDOGeometry Y){
		 double minDistance = Double.POSITIVE_INFINITY;	//minimum distance between two points on the shapes
		 double currentDistance;	//distance between two points in question
		 
		 for(int i=0 ; i<4 ; i++){
			 for(int j=0 ; j<4 ; j++){
				 currentDistance = dist(X.coords[i*2], X.coords[i*2+1], Y.coords[j*2], Y.coords[j*2+1]); //Calculate the distance between the two points
				 if(currentDistance < minDistance){ //And if it's the smallest one we've found yet
					 minDistance = currentDistance;	//save it for future comparisons
				 }
			 }//end inner for loop
		 }//end outer for loop
		 
		 if(minDistance = Double.POSITIVE_INFINITY){ //Check for errors
			 midDistance = -1;
		 }
		 
		 return minDistance;
	 }
	 
	 /*Returns the distance between a rectangle and a triangle, passed in that order
	   Param X: a rectangle SDOGeometry object
	   Param Y: a triangle SDOGeometry object */
	 private double rectangleTriangleDistance(SDOGeometry X, SDOGeometry Y){
		 double minDistance = Double.POSITIVE_INFINITY;	//minimum distance between two points on the shapes
		 double currentDistance;	//distance between two points in question
		 
		 for(int i=0 ; i<4 ; i++){
			 for(int j=0 ; j<3 ; j++){
				 currentDistance = dist(X.coords[i*2], X.coords[i*2+1], Y.coords[j*2], Y.coords[j*2+1]); //Calculate the distance between the two points
				 if(currentDistance < minDistance){ //And if it's the smallest one we've found yet
					 minDistance = currentDistance;	//save it for future comparisons
				 }
			 }//end inner for loop
		 }//end outer for loop
		 
		 if(minDistance = Double.POSITIVE_INFINITY){ //Check for errors
			 midDistance = -1;
		 }
		 
		 return minDistance;
	 }
	 
	 private double rectangleCircleDistance(SDOGeometry X, SDOGeometry Y){
		 double minDistance = Double.POSITIVE_INFINITY;	//minimum distance between two points on the shapes
		 double currentDistance;	//distance between two points in question
		 
		 for(int i=0 ; i<4 ; i++){
			 
		 }
		 
		 if(minDistance = Double.POSITIVE_INFINITY){ //Check for errors
			 midDistance = -1;
		 }
		 
		 return minDistance;
	 }
	 
	 private double rectanglePolygonDistance(SDOGeometry X, SDOGeometry Y){
		 return -1;
	 }
	 
	 /*Returns the distance between a rectangle and a triangle, passed in that order
	   Param X: a rectangle SDOGeometry object
	   Param Y: a triangle SDOGeometry object */
	 private double triangleTriangleDistance(SDOGeometry X, SDOGeometry Y){
		 double minDistance = Double.POSITIVE_INFINITY;	//minimum distance between two points on the shapes
		 double currentDistance;	//distance between two points in question
		 
		 for(int i=0 ; i<3 ; i++){
			 for(int j=0 ; j<3 ; j++){
				 currentDistance = dist(X.coords[i*2], X.coords[i*2+1], Y.coords[j*2], Y.coords[j*2+1]); //Calculate the distance between the two points
				 if(currentDistance < minDistance){ //And if it's the smallest one we've found yet
					 minDistance = currentDistance;	//save it for future comparisons
				 }
			 }//end inner for loop
		 }//end outer for loop
		 
		 if(minDistance = Double.POSITIVE_INFINITY){ //Check for errors
			 midDistance = -1;
		 }
		 
		 return minDistance;
	 }
	 
	 private double triangleCircleDistance(SDOGeometry X, SDOGeometry Y){
		 return -1;
	 }
	 
	 private double trianglePolygonDistance(SDOGeometry X, SDOGeometry Y){
		 return -1;
	 }
	 
	 private double circleCircleDistance(SDOGeometry X, SDOGeometry Y){
		 return -1;
	 }
	 
	 private double circlePolygonDistance(SDOGeometry X, SDOGeometry Y){
		 return -1;
	 }
	 
	 private double polygonPolygonDistance(SDOGeometry X, SDOGeometry Y){
		 return -1;
	 }
}
