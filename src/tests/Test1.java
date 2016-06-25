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

class ShapesTable {
    public static final String tablename = "ShapesTable";
    public int shapeId;
    public String shapeName;
    public Sdo_geometry shape;

    public ShapesTable(int _shapeId, String _shapeName, Sdo_geometry _shape) {
        this.shapeId = _shapeId;
        this.shapeName = _shapeName;
        this.shape = _shape;
    }
}

class SdoGeoMetaData 
{
	public String tableName;
    public String columnName;
    public double[] xDim;
    public double[] yDim;

    public SdoGeoMetaData(String tableName, String columnName, double[] xDim, double[] yDim) 
    {
        this.tableName = tableName;
        this.columnName = columnName;
        this.xDim = xDim;
        this.yDim = yDim;
    }
}


public class Test1 {

	public static void main (String args[])
	{
		boolean test1_flag;
		
		Test1Driver test1driver = new Test1Driver ();
		
		test1_flag = test1driver.createTest();
        if (test1_flag != true) {
            System.out.println("Error ocurred during Test1");
        }
        else {
            System.out.println("Test1 completed successfully");
        }
	}
}

class Test1Driver extends TestDriver implements GlobalConst
{
	private boolean OK = true;
	private boolean FAIL = false;
	private Vector shapesTable;
	
	public Test1Driver()
	{
		
	}
	
	public boolean createTest()
	{
		System.out.println ("Started Test 1");
		
		boolean status = OK;
		//int num_shapes_table_attributes = 3;
		//int num_shapes_table = 2;
		//int num_sdo_geom_attributes = ;
		//int num_sdo_geom = ;
		
		String dbpath = "/tmp/" + System.getProperty("user.name") + ".minibase.test1db";
        String logpath = "/tmp/" + System.getProperty("user.name") + ".test1log";

        String remove_cmd = "/bin/rm -rf ";
        String remove_logcmd = remove_cmd + logpath;
        String remove_dbcmd = remove_cmd + dbpath;
        String remove_test1cmd = remove_cmd + dbpath;
		
        try 
        {
            Runtime.getRuntime().exec(remove_logcmd);
            Runtime.getRuntime().exec(remove_dbcmd);
            Runtime.getRuntime().exec(remove_test1cmd);
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
        
        Tuple t1 = new Tuple();
        try 
        {
          t1.setHdr((short) 3,STtypes, STsizes);
        }
        catch (Exception e)
        {
            System.err.println("*** error in Tuple.setHdr() ***");
            status = FAIL;
            e.printStackTrace();
        }
        
        int size1 = t1.size();
                   
        if (status != OK) 
        {
               //bail out
               System.err.println ("*** Error creating relation for shapes");
               Runtime.getRuntime().exit(1);
         }
            
          //creating the ShapesTable finish
        
        //create the Sdo_Geom_Metadata table
  
        AttrType[] MDtypes = new AttrType[4];
        MDtypes[0] = new AttrType(AttrType.attrString);
        MDtypes[1] = new AttrType(AttrType.attrString);
        MDtypes[2] = new AttrType(AttrType.attrRealNoArray);
        MDtypes[3] = new AttrType(AttrType.attrRealNoArray);
        
        short[] MDsizes = new short[2];
        MDsizes[0] = 30; //first elt. is 30
        MDsizes[1] = 30;
        
        Tuple t2 = new Tuple();
        try 
        {
          t2.setHdr((short) 4,MDtypes, MDsizes);
        }
        catch (Exception e)
        {
            System.err.println("*** error in Tuple.setHdr() ***");
            status = FAIL;
            e.printStackTrace();
        }
        
        int size2 = t2.size();
        
        if (status != OK) 
        {
               //bail out
               System.err.println ("*** Error creating relation for sdogeometadata");
               Runtime.getRuntime().exit(1);
         }
            
          //creating the ShapesTable finish
           
           return true;
	}
}
