package tests;

import global.*;
import global.SDOGeometry.SDOGeomType;

public class SDOGeometryTest {

	public static void main (String args[])
	{
		
		SDOGeomType myType = SDOGeomType.RECTANGLE;
		double[] myCoords1 = {1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 1.0};
		SDOGeometry myShape1 = new SDOGeometry(myType, myCoords1);
		
		double[] myCoords2 = {2.0, 3.0, 2.0, 4.0, 4.0, 4.0, 4.0, 2.0};
		SDOGeometry myShape2 = new SDOGeometry(myType, myCoords2);
		System.out.println(myShape1.toString());
		System.out.println(myShape2.toString());
		
		System.out.println("Testing conversion to double array and back again:");
		SDOGeometry myShape3 = new SDOGeometry(myShape1.convertToDoubleArray());
		System.out.println(myShape3.toString());
		
		System.out.println("Distance between rectangles: " + myShape1.Distance(myShape2));
		System.out.println("Rectangle Area: " + myShape1.area());
		
		double[] myCoords3 = {2.0, 3.0, 2.0, 4.0, 3.0, 3.0};
		SDOGeometry myShape4 = new SDOGeometry(SDOGeomType.TRIANGLE, myCoords3);
		System.out.println(myShape4.toString());
		System.out.println("Triangle Area: " + myShape4.area());
		System.out.println("Distance from Triangle to Rectangle: " + myShape4.Distance(myShape1));
		
		double[] myCoords4 = {4.0, 1.0, 3.0, 1.0};
		SDOGeometry myShape5 = new SDOGeometry(SDOGeomType.CIRCLE, myCoords4);
		System.out.println("Circle Area: " + myShape5.area());
		System.out.println("Distance from Circle to Rectangle: " + myShape5.Distance(myShape1));
		
		System.out.println("Error message should follow:");
		double[] myCoords5 = {4.0, 1.0, 3.0, 1.0};
		SDOGeometry myShape6 = new SDOGeometry(SDOGeomType.RECTANGLE, myCoords5);
	}
		
}


