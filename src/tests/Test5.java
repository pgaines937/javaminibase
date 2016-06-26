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


public class Test5 {
	
	public static void main (String args[])
	{
		boolean test5_flag = true;
		Test5Driver test5driver = new Test5Driver();
		//test5_flag = test5driver.areaTest();
		
		if (test5_flag != true) 
		{
            System.out.println("Error occurred Area Test 5");
        }
        else 
        {
            System.out.println("Area test 5 completed successfully");
        }
	}

}

class Test5Driver extends TestDriver implements GlobalConst
{
	private boolean OK = true;
    private boolean FAIL = false;
    private Vector shapesTable;
    
    Test5Driver ()
    {
        //print query
        System.out.println("CREATE TABLE ShapesTable");
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
    }
    
    public boolean areaTest ()
    {
        Initialize();
    	AreaQuery();
    	System.out.println("Finished Area Test5");
    	return true;
    }

    public void Initialize ()
    {
        System.out.print("Started Test 5- Area Test" + "\n");

        //build shapesTable table
        shapesTable = new Vector();

        double[] vertices1 = new double[] {1.0, 1.0, 1.0, 2.0, 2.0, 1.0, 2.0, 2.0};
        shapesTable.addElement(new ShapesTable(1, "Rectangle1", new SDOGeometry(SDOGeometry.SDOGeomType.RECTANGLE, vertices1)));

        vertices1 = new double[] {2.5, 2.5, 2.5, 3.5, 3.5, 2.5, 3.5, 3.5};

        shapesTable.addElement(new ShapesTable(2, "Rectangle2", new SDOGeometry(SDOGeometry.SDOGeomType.RECTANGLE, vertices1)));

        boolean status = OK;
        int num_shapes_table_attributes = 3;
        int num_shapes_table = 2;
        int num_sdo_geom_attributes = 4;
        int num_sdo_geom = 1;

        String dbpath = "/tmp/" + System.getProperty("user.name") + ".minibase.Test5db";
        String logpath = "/tmp/" + System.getProperty("user.name") + ".test5log";

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
        System.out.println("CREATE TABLE ShapesTable");
        System.out.println("shapesId NUMBER PRIMARY KEY");
        System.out.println("name VARCHAR2(32)");
        System.out.println("shape SDO_GEOMETRY)");

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
        System.out.println("INSERT INTO ShapesTable VALUES(1, Rectangle1,SDO_GEOMETRY(RECTANGLE, vertices1[1.0, 1.0, 1.0, 2.0, 2.0, 1.0, 2.0, 2.0]");
        System.out.println("INSERT INTO ShapesTable VALUES(2, Rectangle2,SDO_GEOMETRY(RECTANGLE, vertices1[2.5, 2.5, 2.5, 3.5, 3.5, 2.5, 3.5, 3.5]");


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
                t.setIntFld(1, ((ShapesTable) shapesTable.elementAt(i)).shapeId);
                t.setStrFld(2, ((ShapesTable) shapesTable.elementAt(i)).shapeName);
                t.setSdoGeometryFld(3, ((ShapesTable) shapesTable.elementAt(i)).shape);
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
    
    public void AreaQuery ()
    {
    	System.out.println("*****************************Query to execute*******************************");
    	boolean status = OK;
    	
    	System.out.println("Query: Find the area of Rectangle1"+
    						"SELECT st1.shapeName, SDO_GEOM.SDO_AREA (st1.shape, 0.005)"+
    						"FROM ShapesTable st1"+
    						"WHERE st1.shape='Rectangle1'\n");
    	
    	CondExpr[] outFilter = new CondExpr[2];
        outFilter[0] = new CondExpr();
        outFilter[1] = new CondExpr();

        AreaQuery_CondExpr(outFilter);

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
            am  = new FileScan("ShapesTable.in", STtypes, STsizes,
                    (short)3, (short)2,
                    STprojection, outFilter);
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
        
        System.out.println("done");
        
        double area;
        try 
        {
            System.out.println("shapeName, Area");
            while ((t = am.get_next()) != null) 
            {
                SDOGeometry sdoval = t.getSdoGeometryFld(2);
                
                if (sdoval != null) 
                {
                    String output = "SDOGeometry(shapeType-" + (int) sdoval.shapeType.ordinal() + ",[ ";
                    for (double d : sdoval.coords)
                        output += d + ",";
                    System.out.println(output + "])");
                }
                area = sdoval.area();
                System.out.println(t.getStrFld(1) + ", " + area);
            }
        }
        catch (Exception e) 
        {
            System.err.println (""+e);
            e.printStackTrace();
            Runtime.getRuntime().exit(1);
        }        
    }
    
    public void AreaQuery_CondExpr (CondExpr[] expr)
    {
    	expr[0].next  = null;
        expr[0].op    = new AttrOperator(AttrOperator.aopEQ);
        expr[0].type1 = new AttrType(AttrType.attrSymbol);
        expr[0].type2 = new AttrType(AttrType.attrString);
        expr[0].operand1.symbol = new FldSpec (new RelSpec(RelSpec.outer), 2);
        expr[0].operand2.string = "Rectangle1";

        expr[1] = null;
    }
}
