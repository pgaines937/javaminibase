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
	        
	        checkShape();	//Checks to make sure shape has valid number of coordinates; prints error message otherwise.
	        	
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
		 
		 for(int i=0; i<coords.length; i++){	//Copy the input array over to our coordinate array
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
		 for(int i=coords.length; i<8; i++){	//any array values not needed in our representation are instead given the POSITIVE_INFINITY value
			 output[i+1] = Double.POSITIVE_INFINITY;
		 }
		 
		 return output;
	 }
	 
	 public double area() {
		 double area = 0.0;
		 switch (this.shapeType) {
			 case RECTANGLE:	//Rectangle area is length*width
				 double length = dist(coords[0],coords[1],coords[2],coords[3]); //find length
				 double width = dist(coords[0],coords[1],coords[6],coords[7]); //find width
				 area = length * width;	//find area
				 break;
			 case TRIANGLE:
				  area = Math.abs((coords[0]*(coords[3]-coords[5])) + (coords[2]*(coords[5]-coords[1])) + (coords[4]*(coords[1]-coords[3])))/2;
				  break;
			 case CIRCLE:
				 double radius = dist(coords[0],coords[1],coords[2],coords[3]);
				 area = Math.abs((radius * radius * Math.PI));
				 break;
			 case POLYGON:
				 area = 0;
				 break;
			 default:
				 area = 0;
		 }
		 return area;
	 }
	 
	 //Determines the distance between this shape and another, returns the distance (-1 if error)
	 public double Distance(SDOGeometry X)
	 {
		 
		 double distance = -1.0;
			
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
				default:
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
				default:
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
				default:
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
				default:
				
				
					break;
			}
			 break;
		//If the shapetype is out of bounds, return -1 as distance to signify error	 
		 default:
			 distance = -1;
			 break;
	 }//end outer switch
     
      return distance;
		 
		 
		 
		 /*
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
		 for(int i=0 ; i<4 ; i++){
			 for(int j=0 ; j<4 ; j++){
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
	 
	 /*
	  * Creates a string representation of this geometry type
	  * Basic format is 
	  * <shapeType>
	  * {<coords>}
	  */
	 public String toString(){
		 
		 String output = geomTypeToString(this.shapeType) + "\n {" + coords[0];
		 for(int i=1; i<coords.length; i++){
			 output = output + ", " + coords[i]; 
		 }
		 output = output + "}";
		 return output;
	 }
	 
	 /* Takes in a SDOGeomType, and converts it into a string
	  * So a RECTANGLE becomes "RECTANGLE" */
	 
	 public String geomTypeToString(SDOGeomType typeToConvert){
		 String output;
		 
		 switch (typeToConvert) {
			 
		 case RECTANGLE:
			 output = "RECTANGLE";
			 break;
		 case TRIANGLE:
			 output = "TRIANGLE";
			 break;
		 case CIRCLE:
			 output = "CIRCLE";
			 break;
		 case POLYGON:
			 output = "POLYGON";
			 break;
		default:
			output = "ERROR";
			break;
	 }// end switch
		 
		 return output;
	 }
	 
	 /*
	  * This method checks to see if the shape has the correct number of coordinates
	  * for its shapetype. Will return TRUE if so, FALSE otherwise. Will also print
	  * a warning to the console if shape in invalid
	  * RECTANGLE = 8 (4 corners)
	  * TRIANGLE = 6 (3 corners)
	  * CIRCLE = 4 (one center and one point on the circle)
	  * POLYGON = 8 (undefined at this time)
	  */
	 private boolean checkShape() {
		 boolean flag = false;
		 int correctNumberOfCoordinates = -1;
		 
		 switch(shapeType){	//First, find the number of coordinates we're supposed to have, based on the shapetype
		 case RECTANGLE: 
		 case POLYGON:	
			 correctNumberOfCoordinates = 8;
			 break;
		 case TRIANGLE:
			 correctNumberOfCoordinates = 6;
			 break;
		 case CIRCLE:
			 correctNumberOfCoordinates = 4;
			 break;
		default:
			correctNumberOfCoordinates = -1; //If there's a shapeType error, we'll error out at any number of coordinates
		 }
		 
		 if(coords.length == correctNumberOfCoordinates){ 	//Then, if the number of coordinates present matches
			 flag = true;									//what we expected, we're good, so return TRUE!
		 }
		 else {												//Otherwise, we'll print an error message and return FALSE.
			 System.out.println("WARNING: INVALID NUMBER OF COORDINATES (" + coords.length + ") FOR SHAPE TYPE " + geomTypeToString(this.shapeType));
		 }
		 
		 return flag;
		 
	 }
}


