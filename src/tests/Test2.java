package tests;

import btree.BTreeFile;
import btree.IntegerKey;
import global.*;
import heap.Heapfile;
import heap.Scan;
import heap.Tuple;
import index.IndexScan;
import iterator.*;
import java.util.ArrayList;

import java.io.IOException;
import java.util.Vector;


class SdoGeoMetaData {
    public String tableName;
    public String columnName;
    public double[] xDim;
    public double[] yDim;

    public SdoGeoMetaData(String tableName, String columnName, double[] xDim, double[] yDim) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.xDim = xDim;
        this.yDim = yDim;
    }
}



public class Test2 {

	public static void main (String args[])
	{
		boolean test2_flag = true;
		
        System.out.println ("************************************Started Test 2**************************************");
		Test2Driver test2driver = new Test2Driver ();
		test2_flag = test2driver.insertTest();
        if (test2_flag != true) 
        {
            System.out.println("Error occurred during Test2");
        }
        else 
        {
            System.out.println("Test2 completed successfully");
        }
        System.out.println ("************************************Ended Test 2****************************************");
	}
}

class Test2Driver extends TestDriver implements GlobalConst
{
	private boolean OK = true;
	private boolean FAIL = false;
	private ArrayList shapesTable = new ArrayList();
	private SdoGeoMetaData sdoGeomMDTable;
	
	public Test2Driver()
	{
        //print query
        /*System.out.println("CREATE TABLE ShapesTable");
        System.out.println("shapesId NUMBER PRIMARY KEY");
        System.out.println("name VARCHAR2(32)");
        System.out.println("shape SDO_GEOMETRY)");

        // print query
        System.out.println("INSERT INTO ShapesTable VALUES(1, Rectangle1,SDO_GEOMETRY(RECTANGLE, vertices1[1.0, 1.0, 2.0, 2.0, 3.0, 3.0, 4.0, 4.0]");
        System.out.println("INSERT INTO ShapesTable VALUES(2, Rectangle2,SDO_GEOMETRY(RECTANGLE, vertices1[2.5, 2.5, 3.5, 3.5, 4.5, 4.5, 5.5, 5.5]");
	*/
	}
	
	public boolean insertTest()
	{
		System.out.println ("Started Test 2");
		
		boolean status = OK;
		int num_shapes_table_attributes = 3;
		int num_shapes_table = 2;
		int num_sdo_geom_attributes = 4;
		int num_sdo_geom = 1;
		
		//insert two table entries for shapes table
        
        double[] vertices1 = new double[] {1.0, 1.0, 3.0, 1.0, 3.0, 3.0, 1.0, 3.0};

        shapesTable.add(new ShapesTable(1, "Rectangle1", new SDOGeometry(SDOGeometry.SDOGeomType.RECTANGLE, vertices1)));

        vertices1 = new double[] {2.0, 2.0, 4.0, 2.0, 4.0, 4.0, 2.0, 4.0};

        shapesTable.add(new ShapesTable(2, "Rectangle2", new SDOGeometry(SDOGeometry.SDOGeomType.RECTANGLE, vertices1)));
        
        //finished inserting two entries for shapes table
		
		String dbpath = "/tmp/" + System.getProperty("user.name") + ".minibase.test2db";
        String logpath = "/tmp/" + System.getProperty("user.name") + ".test2log";

        String remove_cmd = "/bin/rm -rf ";
        String remove_logcmd = remove_cmd + logpath;
        String remove_dbcmd = remove_cmd + dbpath;
        String remove_test2cmd = remove_cmd + dbpath;
		
        try 
        {
            Runtime.getRuntime().exec(remove_logcmd);
            Runtime.getRuntime().exec(remove_dbcmd);
            Runtime.getRuntime().exec(remove_test2cmd);
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

        //create the ShapesTables
        AttrType[] STtypes = new AttrType[3];
        STtypes[0] = new AttrType(AttrType.attrInteger);
        STtypes[1] = new AttrType(AttrType.attrString);
        STtypes[2] = new AttrType(AttrType.attrSdoGeometry);
        
        short [] STsizes = new short [1];
        STsizes[0] = 30; //first elt. is 30
        
        
        Tuple t = new Tuple();
        try 
        {
          t.setHdr((short) 3,STtypes, STsizes);
        }
        catch (Exception e)
        {
            System.err.println("*** error in Tuple.setHdr() ***");
            status = FAIL;
            e.printStackTrace();
        }
        
        int size = t.size();

        // print query
        System.out.println ("---------------------------------Query Executed---------------------------------");
        System.out.println("INSERT INTO ShapesTable VALUES(1, Rectangle1,SDO_GEOMETRY(RECTANGLE, vertices1[1.0, 1.0, 2.0, 2.0, 3.0, 3.0, 4.0, 4.0]");
        System.out.println("INSERT INTO ShapesTable VALUES(2, Rectangle2,SDO_GEOMETRY(RECTANGLE, vertices1[2.5, 2.5, 3.5, 3.5, 4.5, 4.5, 5.5, 5.5]");
        System.out.println ("---------------------------------Query Executed---------------------------------");

        // inserting the tuple into file "ShapesTable"
        RID             rid;
        Heapfile        f = null;
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

        
        //actual inserting
        
        for (int i=0; i<num_shapes_table; i++)
        {
            try 
            {
            	t.setIntFld(1, ((ShapesTable)shapesTable.get(i)).shapeId);
            	t.setStrFld(2, ((ShapesTable)shapesTable.get(i)).shapeName);
            	t.setSdoGeometryFld(3, ((ShapesTable)shapesTable.get(i)).shape);
            	//t.setFloFld(4, (float)((ShapesTable)shapesTable.elementAt(i)).age);
            }
            catch (Exception e) 
            {
            	System.err.println("*** Heapfile error in Tuple.setStrFld() ***");
            	status = FAIL;
            	e.printStackTrace();
            }

          //actual inserting to table finished
            
         //write each record to the heap file
            try 
            {
            	//rid = f.insertRecord(t.returnTupleByteArray());
            }
            catch (Exception e) 
            {
            	System.err.println("*** error in Heapfile.insertRecord() ***");
            	status = FAIL;
            	e.printStackTrace();
            }      
         }

        //creating and inserting into sdometadata table
        dbpath = "/tmp/" + System.getProperty("user.name") + ".minibase.sdogeommetadb";
        logpath = "/tmp/" + System.getProperty("user.name") + ".sdogeommetalog";

        remove_cmd = "/bin/rm -rf ";
        remove_logcmd = remove_cmd + logpath;
        remove_dbcmd = remove_cmd + dbpath;
        remove_test2cmd = remove_cmd + dbpath;

        try {
            Runtime.getRuntime().exec(remove_logcmd);
            Runtime.getRuntime().exec(remove_dbcmd);
            Runtime.getRuntime().exec(remove_test2cmd);
        } 
        catch (IOException e) 
        {
            System.err.println("" + e);
        }


     // creating the sdogeommetadata relation
        AttrType[] MDtypes = new AttrType[4];
        MDtypes[0] = new AttrType(AttrType.attrString);
        MDtypes[1] = new AttrType(AttrType.attrString);
        MDtypes[2] = new AttrType(AttrType.attrRealNoArray);
        MDtypes[3] = new AttrType(AttrType.attrRealNoArray);
     // finish creating the sdogeommetadata relation
        
        //SOS
        short[] MDsizes = new short[2];
        MDsizes[0] = 30; //first elt. is 30
        MDsizes[1] = 30;
        
        t = new Tuple();
        try 
        {
            t.setHdr((short) 4, MDtypes, MDsizes);
        } 
        catch (Exception e) 
        {
            System.err.println("*** error in Tuple.setHdr() ***");
            status = FAIL;
            e.printStackTrace();
        }
        
        size = t.size();

        // inserting the tuple into file "sdogeommetadata"
        try 
        {
            f = new Heapfile("sdogeommetadata.in");
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
            t.setHdr((short) 4, MDtypes, MDsizes);
        }
        catch (Exception e) 
        {
            System.err.println("*** error in Tuple.setHdr() ***");
            status = FAIL;
            e.printStackTrace();
        }
        
        sdoGeomMDTable = new SdoGeoMetaData(ShapesTable.tablename,"shape",new double[]{0, 20,0.005}, new double[]{0,20,0.005});
        
        try 
        {
            t.setStrFld(1,sdoGeomMDTable.tableName);
            t.setStrFld(2,sdoGeomMDTable.columnName);
            t.setRealArrayFld(3, sdoGeomMDTable.xDim);
            t.setRealArrayFld(4,sdoGeomMDTable.yDim);
        }
        catch (Exception e) 
        {
            System.err.println("*** Heapfile error in Tuple.setStrFld() ***");
            status = FAIL;
            e.printStackTrace();
        }
        
        //inserting record in the heap file
        try 
        {
            //rid = f.insertRecord(t.returnTupleByteArray());
        }
        catch (Exception e) 
        {
            System.err.println("*** error in Heapfile.insertRecord() ***");
            status = FAIL;
            e.printStackTrace();
        }
        //finish inserting record in the heap file        
        
        if (status != OK) 
        {
            //bail out
            System.err.println ("*** Error creating relation for shapes");
            Runtime.getRuntime().exit(1);
        }
            
         //write each record to the heap file finish
           
           return true;
	}
}
