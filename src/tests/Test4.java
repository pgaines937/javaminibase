package tests;

import btree.BTreeFile;
import btree.IntegerKey;
import global.*;
import heap.Heapfile;
import heap.Scan;
import heap.Tuple;
import index.IndexScan;
import iterator.*;

import java.io.IOException;
import java.util.Vector;

public class Test4 {

	public static void main (String args[])
	{
		boolean test4_flag;
		Test4Driver test4driver = new Test4Driver();
		test4_flag = test4driver.distanceTest();

		if (test4_flag != true) 
		{
			System.out.println("Error ocurred during Test 4");
		}
		else 
		{
			System.out.println("Distance Test 4 completed successfully");
		}
	}
}

class Test4Driver extends TestDriver implements GlobalConst
{
	private boolean OK = true;
    private boolean FAIL = false;
    private Vector shapesTable;
    
    Test4Driver ()
    {
    	System.out.print("Started Test 4- Distance Test" + "\n");

        //build shapesTable table
        shapesTable = new Vector();

        double[] vertices1 = new double[] {1.0, 1.0, 2.0, 3.0};
        shapesTable.addElement(new ShapesTable(1, "Rectangle1", new Sdo_geometry(Sdo_gtype.RECTANGLE, vertices1)));

        vertices1 = new double[] {2.5, 3.5, 3.5, 4.5};

        shapesTable.addElement(new ShapesTable(2, "Rectangle2", new Sdo_geometry(Sdo_gtype.RECTANGLE, vertices1)));
        
        boolean status = OK;
        int num_shapes_table_attributes = 3;
		int num_shapes_table = 2;
		int num_sdo_geom_attributes = 4;
		int num_sdo_geom = 1;
        
        String dbpath = "/tmp/" + System.getProperty("user.name") + ".minibase.Test4db";
        String logpath = "/tmp/" + System.getProperty("user.name") + ".test4log";

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

        // creating the ShapesTable relation
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
    
    public boolean distanceTest ()
    {
    	DistanceQuery();       
    	System.out.println("Finished Distance Test 4");
    	return true;
    }
    
    public void DistanceQuery ()
    {
    	System.out.println("*****************************Query to execute*******************************");
    	boolean status = OK;
    	
    	System.out.println("Query: Find the distance of Rectangle1 and Rectangle2"+
    						"SELECT SDO_GEOM.SDO_DISTANCE (st1.shape, st2.shape, 0.005)"+
    						"FROM ShapesTable st1, ShapesTable st2"+
    						"WHERE st1.shape='Rectangle1' AND st2.shape='Rectangle2'\n");
    	
    	CondExpr[] outFilter = new CondExpr[3];
        outFilter[0] = new CondExpr();
        outFilter[1] = new CondExpr();
        outFilter[2] = new CondExpr();
        
        DistanceQuery_CondExpr(outFilter);

        Tuple t = new Tuple();
        t = null;
        
        AttrType [] Mtypes = new AttrType[3];
        Mtypes[0] = new AttrType (AttrType.attrInteger);
        Mtypes[1] = new AttrType (AttrType.attrString);
        Mtypes[2] = new AttrType (AttrType.attrSdoGeometry);

        //SOS
        short [] Msizes = new short[1];
        Msizes[0] = 30; //first elt. is 30

        FldSpec [] Mprojection = new FldSpec[2];
        Mprojection[0] = new FldSpec(new RelSpec(RelSpec.outer), 2);
        Mprojection[1] = new FldSpec(new RelSpec(RelSpec.outer), 3);

        AttrType [] jtype = new AttrType[2];
        jtype[0] = new AttrType (AttrType.attrString);
        jtype[1] = new AttrType (AttrType.attrSdoGeometry);

        CondExpr [] selects = new CondExpr [1];
        selects = null;
        
        FileScan am = null;
        try 
        {
            am  = new FileScan("ShapesTable.in", Mtypes, Msizes,
                    (short)3, (short)2,
                    Mprojection, null);
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
        Sdo_geometry x[] = new Sdo_geometry[2];
        
        try 
        {
            int i = 0;
            while ((t = am.get_next()) != null) 
            {
                t.print(jtype);
                x[i++] = t.getSdoGeometryFld(2);	//2nd field is shape in t now
            }
            double distance = x[0].Distance(x[1]);
            
            if (distance > 0.0)
            	System.out.println ("Distance is "+distance);
            else 
            	System.out.println ("Distance is 0");       
        }
        catch (Exception e) 
        {
            System.err.println (""+e);
            e.printStackTrace();
            Runtime.getRuntime().exit(1);
        }
    }
        
    public void DistanceQuery_CondExpr (CondExpr[]expr)
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
    	
    }   
}
