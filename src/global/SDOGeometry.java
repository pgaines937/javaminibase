package global;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.util.GeometricShapeFactory;

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
		 int temp = (int) inputArray[0];
		 this.shapeType = SDOGeometry.SDOGeomType.values()[temp];
		 
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
		 output[0] = shapeType.ordinal();		//the first element of the array indicates type
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
	 
	 public Coordinate[] intersection (SDOGeometry X)
	 {
		 double tempArray [] = this.convertToDoubleArray(); // need to get rid of the first element, and the positive infinities
		 int iterator = 1;
		 int iterator2 = 0;
		 double radius = 0;
		 double height = 0;
		 double width = 0;
		 double centerX = 0;
		 double centerY = 0;
		 Geometry shape1 = null;
		 Geometry shape2 = null;
		 GeometricShapeFactory gsf = new GeometricShapeFactory();
		 GeometryFactory fact = new GeometryFactory();
		 
		 // Convert current object to JTS Geometry object
		 SDOGeometry.SDOGeomType tempShapeType = SDOGeometry.SDOGeomType.values()[(int) tempArray[0]];
		 switch (tempShapeType) { // shapeType
			 case RECTANGLE:	//This is rectangle, other is rectangle
				 width = tempArray[1] - tempArray[3];
				 height = tempArray[5] - tempArray[1];
				 centerX = width / 2;
				 centerY = height / 2;
				 gsf.setWidth(width);
				 gsf.setHeight(height);
				 gsf.setNumPoints(4);
				 gsf.setBase(new Coordinate(centerX,centerY));
				 shape1 = gsf.createRectangle();
				 break;
			 case TRIANGLE:	//This is rectangle, other is triangle
				 Coordinate[] p = new Coordinate[] { new Coordinate(tempArray[1], tempArray[2]), 
						 new Coordinate(tempArray[3], tempArray[4]), new Coordinate(tempArray[5], tempArray[6]) };
				 shape1 = fact.createPolygon(p);
				 break;
			 case CIRCLE:	//This is rectangle, other is circle
				 radius = tempArray[1] - tempArray[3];
				 centerX = tempArray[1];
				 centerY = tempArray[2];
				 gsf.setSize(radius);
				 gsf.setNumPoints(4);
				 gsf.setBase(new Coordinate(centerX,centerY));
				 shape1 = gsf.createCircle();
				 break;
			 case POLYGON:	//This is rectangle, other is Polygon
				 break;
			 default:
				 break;
		 }

		 // Convert passed in Geometry object to JTS Geometry object
		 SDOGeometry.SDOGeomType xShapeType = SDOGeometry.SDOGeomType.values()[(int) X.coords[0]];
		 switch (xShapeType) { // shapeType
			 case RECTANGLE:	//This is rectangle, other is rectangle
				 width = X.coords[1] - X.coords[3];
				 height = X.coords[5] - X.coords[1];
				 centerX = width / 2;
				 centerY = height / 2;
				 gsf.setWidth(width);
				 gsf.setHeight(height);
				 gsf.setNumPoints(4);
				 gsf.setBase(new Coordinate(centerX,centerY));
				 shape2 = gsf.createRectangle();
				 break;
			 case TRIANGLE:	//This is rectangle, other is triangle
				 Coordinate[] p = new Coordinate[] { new Coordinate(X.coords[1], X.coords[2]),
						 new Coordinate(X.coords[3], X.coords[4]), new Coordinate(X.coords[5], X.coords[6]) };
				 shape2 = fact.createPolygon(p);
				 break;
			 case CIRCLE:	//This is rectangle, other is circle
				 radius = tempArray[1] - tempArray[3];
				 centerX = tempArray[1];
				 centerY = tempArray[2];
				 gsf.setSize(radius);
				 gsf.setNumPoints(4);
				 gsf.setBase(new Coordinate(centerX,centerY));
				 shape2 = gsf.createCircle();
				 break;
			 case POLYGON:	//This is rectangle, other is Polygon
				 break;
			 default:
				 break;
		 }

		 Geometry newShape = shape1.intersection(shape2);
		 Coordinate[] newCoordinates = newShape.getCoordinates();

		 return newCoordinates;
	 }
    
    	 /*Determines the distance between two rectangles
	   Param X: a rectangle SDOGeometry object
	   Param Y: a rectangle SDOGeometry object */
	 private double rectangleRectangleDistance(SDOGeometry X, SDOGeometry Y){
		 double minDistance = Double.POSITIVE_INFINITY;	//minimum distance between two points on the shapes
		 double currentDistance;	//distance between two points in question
		 System.out.println(X.coords.length);
		 System.out.println(Y.coords.length);
		 for(int i=0 ; i<2 ; i++){
			 for(int j=0 ; j<2 ; j++){
				 currentDistance = dist(X.coords[i*2], X.coords[i*2+1], Y.coords[j*2], Y.coords[j*2+1]); //Calculate the distance between the two points
				 if(currentDistance < minDistance){ //And if it's the smallest one we've found yet
					 minDistance = currentDistance;	//save it for future comparisons
				 }
			 }//end inner for loop
		 }//end outer for loop
		 
		 if(minDistance == Double.POSITIVE_INFINITY){ //Check for errors
			 minDistance = -1;
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
		 
		 if(minDistance == Double.POSITIVE_INFINITY){ //Check for errors
			 minDistance = -1;
		 }
		 
		 return minDistance;
	 }
	 
	 /*Returns the distance between a rectangle and a circle, passed in that order
	   Param X: a rectangle SDOGeometry object
	   Param Y: a circle SDOGeometry object */
	 private double rectangleCircleDistance(SDOGeometry X, SDOGeometry Y){
		 double minDistance = Double.POSITIVE_INFINITY;	//minimum distance between two points on the shapes
		 double currentDistance;	//distance between two points in question
		 double radius = dist(Y.coords[0],Y.coords[1],Y.coords[2],Y.coords[3]);
		 
		 for(int i=0 ; i<4 ; i++){ //go through each corner of the rectangle
			currentDistance = dist(X.coords[i*2],X.coords[i*2+1],Y.coords[0],Y.coords[1]); //find the distance
			if(currentDistance < minDistance){ //And if it's the smallest one we've found yet
				minDistance = currentDistance;	//save it for future comparisons
			}
		 }
		 
		 //minDistance is currently the distance between a corner and the circle's center
		 minDistance = minDistance-radius;	//this makes it the distance between a corner and the circle's outside
		 
		 if(minDistance < 0){	//if it turns out there is overlap between the shapes
			 minDistance = 0;	//then the distance is actually 0;
		 }
		 
		 if(minDistance == Double.POSITIVE_INFINITY){ //Check for errors
			 minDistance = -1;
		 }
		 
		 return minDistance;
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
		 
		 if(minDistance == Double.POSITIVE_INFINITY){ //Check for errors
			 minDistance = -1;
		 }
		 
		 return minDistance;
	 }
	 
	 
	/*Returns the distance between a triangle and a circle, passed in that order
	  Param X: a triangle SDOGeometry object
	  Param Y: a circle SDOGeometry object */
	 private double triangleCircleDistance(SDOGeometry X, SDOGeometry Y){
		 double minDistance = Double.POSITIVE_INFINITY;	//minimum distance between two points on the shapes
		 double currentDistance;	//distance between two points in question
		 double radius = dist(Y.coords[0],Y.coords[1],Y.coords[2],Y.coords[3]);
		 
		 for(int i=0 ; i<3 ; i++){ //go through each corner of the triangle
			currentDistance = dist(X.coords[i*2],X.coords[i*2+1],Y.coords[0],Y.coords[1]); //find the distance
			if(currentDistance < minDistance){ //And if it's the smallest one we've found yet
				minDistance = currentDistance;	//save it for future comparisons
			}
		 }
		 
		 //minDistance is currently the distance between a corner and the circle's center
		 minDistance = minDistance-radius;	//this makes it the distance between a corner and the circle's outside
		 
		 if(minDistance < 0){	//if it turns out there is overlap between the shapes
			 minDistance = 0;	//then the distance is actually 0;
		 }
		 
		 if(minDistance == Double.POSITIVE_INFINITY){ //Check for errors
			 minDistance = -1;
		 }
		 
		 return minDistance;
	 }

	 
	 /*Returns the distance between two circles */
	 private double circleCircleDistance(SDOGeometry X, SDOGeometry Y){
		 double distance = dist(X.coords[0],X.coords[1],Y.coords[0],Y.coords[1]); //find distance between the centers
		 double radiusX = dist(X.coords[0],X.coords[1],X.coords[2],X.coords[3]);
		 double radiusY = dist(Y.coords[0],Y.coords[1],Y.coords[2],Y.coords[3]);
		 
		 distance = distance - radiusX - radiusY; //subtracts the two radii from the distance between the circles' centers
		 
		 if(distance < 0){	//if it turns out there is overlap between the shapes
			 distance = 0;	//then the distance is actually 0;
		 }
		 
		 if(distance == Double.POSITIVE_INFINITY){ //Check for errors
			 distance = -1;
		 }
		 
		 return distance;
	 }
	 
	 private double circlePolygonDistance(SDOGeometry X, SDOGeometry Y){
		 return -1;
	 }
	 
	 private double polygonPolygonDistance(SDOGeometry X, SDOGeometry Y){
		 return -1;
	 }
	 	 
	 private double trianglePolygonDistance(SDOGeometry X, SDOGeometry Y){
		 return -1;
	 }
	 	 
	 private double rectanglePolygonDistance(SDOGeometry X, SDOGeometry Y){
		 return -1;
	 }
}