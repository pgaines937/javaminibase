package tests;

import btree.BTreeFile;
import btree.IntegerKey;
import btree.StringKey;
//import com.infomatiq.jsi.SpatialIndex;
//import com.infomatiq.jsi.rtree.RTree;
//import com.infomatiq.jsi.Rectangle;
import global.*;
import global.GlobalConst.Sdo_gtype;
import heap.Heapfile;
import heap.Scan;
import heap.Tuple;
import index.IndexScan;
import iterator.*;

import java.awt.*;
import java.io.IOException;
import java.util.Vector;

public class Test3 {

	public static void main (String args[])
	{
		boolean test3_flag;
		
		Test3Driver test3driver = new Test3Driver ();
		
		test3_flag = test3driver.indexTest();
        if (test3_flag != true) {
            System.out.println("Error ocurred during Test3");
        }
        else {
            System.out.println("Test3- Index Test completed successfully");
        }
	}
}

class Test3Driver extends TestDriver implements GlobalConst
{
	private boolean OK = true;
	private boolean FAIL = false;
	private Vector shapesTable;
	private static short REC_LEN1 = 50;
	private static short REC_LEN2 = 50;
	private static short REC_LEN3 = 50;
	public Test3Driver ()
	{
		
	}
	
	public boolean indexTest()
	{
		System.out.println("Started Index Test");
		
		boolean status = OK;
		AttrType[] attrType = new AttrType[3];
	    attrType[0] = new AttrType(AttrType.attrInteger);
	    attrType[1] = new AttrType(AttrType.attrString);
	    attrType[2] = new AttrType(AttrType.attrSdoGeometry);
	    short[] attrSize = new short[3];
	    attrSize[0] = REC_LEN2;
	    attrSize[1] = REC_LEN1;
	    attrSize[2] = REC_LEN3;
		int num_shapes_table_attributes = 3;
		int num_shapes_table = 2;
		
		//insert two table entries for shapes table
        
        double[] vertices1 = new double[] {1.0, 1.0, 2.0, 3.0};

        shapesTable.addElement(new ShapesTable(1, "Rectangle", new Sdo_geometry(Sdo_gtype.RECTANGLE, vertices1)));

        double[] vertices2 = new double[] {2.5, 3.5, 3.5, 4.5};

        shapesTable.addElement(new ShapesTable(2, "Rectangle", new Sdo_geometry(Sdo_gtype.RECTANGLE, vertices2)));
        
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
        
        // inserting the tuple into file "ShapesTable"
        RID             rid;
        Heapfile        f = null;
        try 
        {
          f = new Heapfile("ShapeIndex.in");
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
        
        //actual inserting
        
        for (int i=0; i<num_shapes_table; i++) 
        {
            try 
            {
            	//t.setIntFld(1, ((ShapesTable)shapesTable.elementAt(i)).shapeId);
            	t.setStrFld(2, ((ShapesTable)shapesTable.elementAt(i)).shapeName);
            	//t.setSdoGeometryFld(3, ((ShapesTable)shapesTable.elementAt(i)).shape);
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
            	rid = f.insertRecord(t.returnTupleByteArray());
            }
            catch (Exception e) 
            {
            	System.err.println("*** error in Heapfile.insertRecord() ***");
            	status = FAIL;
            	e.printStackTrace();
            }      
         }

		//create a scan on the heap file
        Scan scan = null;
        
        try 
        {
            scan = new Scan(f);
        }
        catch (Exception e) 
        {
            status = FAIL;
            e.printStackTrace();
            Runtime.getRuntime().exit(1);
        }
        
        // create the index file using Binary Tree
        BTreeFile btf = null;
        try 
        {
          btf = new BTreeFile("BTreeIndex", AttrType.attrString, REC_LEN1, 1/*delete*/); 
        }
        catch (Exception e) 
        {
          status = FAIL;
          e.printStackTrace();
          Runtime.getRuntime().exit(1);
        }
        
        System.out.println("BTreeIndex created successfully.\n"); 
        
        rid = new RID();
        String key = null;
        Tuple temp = null;
        
        try 
        {
            temp = scan.getNext(rid);
        }
          catch (Exception e) 
        {
            status = FAIL;
            e.printStackTrace();
        }
        
        while ( temp != null) 
        {
            t.tupleCopy(temp);
            
            try 
            {
            	key = t.getStrFld(2);
            }
            catch (Exception e) 
            {
            	status = FAIL;
            	e.printStackTrace();
            }
            
            try 
            {
            	btf.insert(new StringKey(key), rid); 
            }
            catch (Exception e) 
            {
            	status = FAIL;
            	e.printStackTrace();
            }
            
            try 
            {
            	temp = scan.getNext(rid);
            }
            catch (Exception e) 
            {
            	status = FAIL;
            	e.printStackTrace();
            }
            
        }
        scan.closescan();
        System.out.println("BTreeIndex file created successfully.\n"); 
        		
		return true;
	}
}
