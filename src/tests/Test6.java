package tests;
import btree.BTreeFile;
import btree.IntegerKey;
import global.*;
import global.SDOGeometry.SDOGeomType;
import heap.Heapfile;
import heap.Scan;
import heap.Tuple;
import index.IndexScan;
import iterator.*;

import java.awt.*;
import java.io.IOException;
import java.util.Vector;
import java.util.ArrayList;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.util.GeometricShapeFactory;


public class Test6 {
	
	public static void main (String args[])
	{
		boolean test6_flag = true;
        System.out.println ("************************************Started Test 6**************************************");
		Test6Driver test6driver = new Test6Driver();
		test6_flag = test6driver.intersectionTest();
		
		if (test6_flag != true) 
		{
            System.out.println("Error occurred Area Test 6");
        }
        else 
        {
            System.out.println("Area test 6 completed successfully");
        }
        System.out.println ("************************************Ended Test 6**************************************");
	}

}

class Test6Driver extends TestDriver implements GlobalConst
{
	private boolean OK = true;
    private boolean FAIL = false;
    private ArrayList shapesTable = new ArrayList();
    
    Test6Driver ()
    {
        //print query
        /*System.out.println("CREATE TABLE ShapesTable");
        System.out.println("shapesId NUMBER PRIMARY KEY");
        System.out.println("name VARCHAR2(32)");
        System.out.println("shape SDO_GEOMETRY)");

        // print query
        System.out.println("INSERT INTO ShapesTable VALUES(1, Rectangle1,SDO_GEOMETRY(RECTANGLE, vertices1[1.0, 1.0, 2.0, 2.0, 3.0, 3.0, 4.0, 4.0]");
        System.out.println("INSERT INTO ShapesTable VALUES(2, Rectangle2,SDO_GEOMETRY(RECTANGLE, vertices1[2.5, 2.5, 3.5, 3.5, 4.5, 4.5, 5.5, 5.5]");

        System.out.println("Query: Find the area of Rectangle1"+
                "SELECT st1.shapeName, SDO_GEOM.SDO_AREA (st1.shape, 0.005)"+
                "FROM ShapesTable st1"+
                "WHERE st1.shape='Rectangle1'\n");
                */
    }
    
    public boolean intersectionTest ()
    {
        Initialize();
    	IntersectionQuery();
    	System.out.println("Finished Area Test6");
    	return true;
    }

    public void Initialize ()
    {
        System.out.print("Started Test 6- Area Test" + "\n");

        //build shapesTable table
        //shapesTable = new Vector();

        double[] vertices1 = new double[] {1.0, 1.0, 3.0, 1.0, 3.0, 3.0, 1.0, 3.0};
        shapesTable.add(new ShapesTable(1, "Rectangle1", new SDOGeometry(SDOGeometry.SDOGeomType.RECTANGLE, vertices1)));

        vertices1 = new double[] {2.0, 2.0, 4.0, 2.0, 4.0, 4.0, 2.0, 4.0};

        shapesTable.add(new ShapesTable(2, "Rectangle2", new SDOGeometry(SDOGeometry.SDOGeomType.RECTANGLE, vertices1)));

        boolean status = OK;
        int num_shapes_table_attributes = 3;
        int num_shapes_table = 2;
        int num_sdo_geom_attributes = 4;
        int num_sdo_geom = 1;

        String dbpath = "/tmp/" + System.getProperty("user.name") + ".minibase.Test6db";
        String logpath = "/tmp/" + System.getProperty("user.name") + ".test6log";

        String remove_cmd = "/bin/rm -rf ";
        String remove_logcmd = remove_cmd + logpath;
        String remove_dbcmd = remove_cmd + dbpath;
        String remove_sts3cmd = remove_cmd + dbpath;

        try
        {
            Runtime.getRuntime().exec(remove_logcmd);
            Runtime.getRuntime().exec(remove_dbcmd);
            Runtime.getRuntime().exec(remove_sts3cmd);
        }
        catch (IOException e)
        {
            System.err.println("" + e);
        }

        SystemDefs sysdef = new SystemDefs(dbpath, 1000, NUMBUF, "Clock");


        //print query
        //System.out.println("CREATE TABLE ShapesTable");
        //System.out.println("shapesId NUMBER PRIMARY KEY");
        //System.out.println("name VARCHAR2(32)");
        //System.out.println("shape SDO_GEOMETRY)");

        // creating the shapesTable relation
        AttrType[] STtypes = new AttrType[3];
        STtypes[0] = new AttrType(AttrType.attrInteger);
        STtypes[1] = new AttrType(AttrType.attrString);
        STtypes[2] = new AttrType(AttrType.attrSdoGeometry);

        //SOS
        short[] STsizes = new short[1];
        STsizes[0] = 30; //first elt. is 30

        Tuple t = new Tuple();
        try
        {
            t.setHdr((short) 3, STtypes, STsizes);
        }
        catch (Exception e)
        {
            System.err.println("*** error in Tuple.setHdr() ***");
            status = FAIL;
            e.printStackTrace();
        }


        int size = t.size();
        System.out.println("Size:" + size);

        // print query
        //System.out.println("INSERT INTO ShapesTable VALUES(1, Rectangle1,SDO_GEOMETRY(RECTANGLE, vertices1[1.0, 1.0, 1.0, 2.0, 2.0, 1.0, 2.0, 2.0]");
        //System.out.println("INSERT INTO ShapesTable VALUES(2, Rectangle2,SDO_GEOMETRY(RECTANGLE, vertices1[2.5, 2.5, 2.5, 3.5, 3.5, 2.5, 3.5, 3.5]");


        // selecting the tuple into file "ShapesTable"
        RID rid;
        Heapfile f = null;
        try
        {
            f = new Heapfile("ShapesTable.in");
        }
        catch (Exception e)
        {
            System.err.println("*** error in Heapfile constructor ***");
            status = FAIL;
            e.printStackTrace();
        }

        t = new Tuple(size);
        try
        {
            t.setHdr((short) 3, STtypes, STsizes);
        }
        catch (Exception e)
        {
            System.err.println("*** error in Tuple.setHdr() ***");
            status = FAIL;
            e.printStackTrace();
        }

        for (int i = 0; i < num_shapes_table; i++)
        {
            try
            {
                t.setIntFld(1, ((ShapesTable) shapesTable.get(i)).shapeId);
                t.setStrFld(2, ((ShapesTable) shapesTable.get(i)).shapeName);
                t.setSdoGeometryFld(3, ((ShapesTable) shapesTable.get(i)).shape);
            }
            catch (Exception e)
            {
                System.err.println("*** Heapfile error in Tuple.setStrFld() ***");
                status = FAIL;
                e.printStackTrace();
            }

            try
            {
                rid = f.insertRecord(t.returnTupleByteArray());
            }
            catch (Exception e)
            {
                System.err.println("*** error in Heapfile.selectRecord() ***");
                status = FAIL;
                e.printStackTrace();
            }
        }

        if (status != OK)
        {
            //bail out
            System.err.println("*** Error creation relation for ShapesTable");
            Runtime.getRuntime().exit(1);
        }
    }
    
    public void IntersectionQuery ()
    {
    	System.out.println("---------------------------------Query to execute---------------------------------");
    	boolean status = OK;
    	
    	System.out.println("Query: Find the intersection of Rectangle1 and Rectangle2"+
    						"SELECT SDO_GEOM.SDO_INTERSECTION (st1.shape, st2.shape 0.005)"+
    						"FROM ShapesTable st1, ShapesTable st2"+
    						"WHERE st1.shapeName = 'Rectangle1' AND st2.shapeName = 'Rectangle2'\n");
    	System.out.println("---------------------------------Query to execute---------------------------------");

    	CondExpr[] outFilter = new CondExpr[3];
        outFilter[0] = new CondExpr();
        outFilter[1] = new CondExpr();
        outFilter[2] = new CondExpr();

        //IntersectionQuery_CondExpr(outFilter);

        Tuple t = new Tuple();
        t = null;

        AttrType [] STtypes = new AttrType[3];
        STtypes[0] = new AttrType (AttrType.attrInteger);
        STtypes[1] = new AttrType (AttrType.attrString);
        STtypes[2] = new AttrType (AttrType.attrSdoGeometry);

        //SOS
        short [] STsizes = new short[1];
        STsizes[0] = 30; //first elt. is 30

        FldSpec [] STprojection = new FldSpec[2];
        STprojection[0] = new FldSpec(new RelSpec(RelSpec.outer), 2);
        STprojection[1] = new FldSpec(new RelSpec(RelSpec.outer), 3);

        AttrType [] jtype = new AttrType[2];
        jtype[0] = new AttrType (AttrType.attrString);
        jtype[1] = new AttrType (AttrType.attrSdoGeometry);
        
        CondExpr [] selects = new CondExpr [1];
        selects = null;

        FileScan am = null;
        try 
        {
            //am  = new FileScan("ShapesTable.in", STtypes, STsizes, (short)3, (short)2, STprojection, outFilter);
            am  = new FileScan("ShapesTable.in", STtypes, STsizes, (short)3, (short)2, STprojection, null);
        }
        catch (Exception e) 
        {
            status = FAIL;
            System.err.println (""+e);
            e.printStackTrace();
        }
        
        if (status != OK) 
        {
            //bail out
            System.err.println ("*** Error setting up scan for ShapesTable");
            Runtime.getRuntime().exit(1);
        }
        
        //System.out.println("done");
        
        double area;
        int i = 0;
        SDOGeometry [] sdoval = new SDOGeometry [2];
        try 
        {
            //System.out.println("shapeName, Area");
            while ((t = am.get_next()) != null) 
            {
                sdoval[i] = t.getSdoGeometryFld(2);
                i++;
            }
            Coordinate[] points = sdoval[0].intersection(sdoval[1]);

        	if (points != null)
            {
                String output = "SDO_GEOMETRY(shapeType- "+(int) sdoval[0].shapeType.ordinal() + " intersects "+(int) sdoval[1].shapeType.ordinal() + ", SDO_ORDINATE_ARRAY[";
                for (Coordinate point : points)
                    output += point.toString() + ",";
                System.out.println(output + "])");
            }
            //System.out.println("sdoval[0]="+sdoval[0]);
            //System.out.println("sdoval[1]="+sdoval[1]);
        }
        catch (Exception e) 
        {
            System.err.println (""+e);
            e.printStackTrace();
            Runtime.getRuntime().exit(1);
        }        
    }
    
    /*public void IntersectionQuery_CondExpr (CondExpr[] expr)
    {
    	expr[0].next  = null;
        expr[0].op    = new AttrOperator(AttrOperator.aopEQ);
        expr[0].type1 = new AttrType(AttrType.attrSymbol);
        expr[0].type2 = new AttrType(AttrType.attrString);
        expr[0].operand1.symbol = new FldSpec (new RelSpec(RelSpec.outer), 2);
        expr[0].operand2.string = "Rectangle1";
        
        expr[1].next  = null;
        expr[1].op    = new AttrOperator(AttrOperator.aopEQ);
        expr[1].type1 = new AttrType(AttrType.attrSymbol);
        expr[1].type2 = new AttrType(AttrType.attrString);
        expr[1].operand1.symbol = new FldSpec (new RelSpec(RelSpec.outer), 2);
        expr[1].operand2.string = "Rectangle2";
        
        expr[2] = null;
    }*/
}