package com.tarams.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.manifoldcf.core.database.BaseTable;
import org.apache.manifoldcf.core.interfaces.ColumnDescription;
import org.apache.manifoldcf.core.interfaces.DBInterfaceFactory;
import org.apache.manifoldcf.core.interfaces.IDFactory;
import org.apache.manifoldcf.core.interfaces.IResultRow;
import org.apache.manifoldcf.core.interfaces.IResultSet;
import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.interfaces.StringSet;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class DatabaseManager extends BaseTable {

	protected final static String DATABASE_TABLE_NAME = "myExample_table";

	protected final static String ID_FEILD = "id";
	protected final static String FILE_NAME = "fileName";
	protected final static String FILE_CREATED_DATE = "createdDate";
	protected final static String FILE_MODIFIED_DATE = "modifiedDate";
	protected final static String FILE_SIZE= "size";

	/** The overall table cache key */
	protected final static String TABLE_CACHEKEY = "table-"+DATABASE_TABLE_NAME;
	/** The prefix of the per-key cache key */
	protected final static String KEY_CACHEKEY_PREFIX = TABLE_CACHEKEY + "-";
	/** The overall table cache key set */
	protected final static StringSet tableCacheKeySet = new StringSet(TABLE_CACHEKEY);

	/** The thread context */
	protected IThreadContext threadContext;

	/** Constructor */


	public DatabaseManager(IThreadContext threadContext)
			throws ManifoldCFException
	{

		super(DBInterfaceFactory.make(threadContext,
				ManifoldCF.getMasterDatabaseName(),
				ManifoldCF.getMasterDatabaseUsername(),
				ManifoldCF.getMasterDatabasePassword()),
				DATABASE_TABLE_NAME);
		//	    Connection connection=ConnectionFactory.getConnection("", "", "", "", "", 0, true).getConnection();
		this.threadContext = threadContext;
	}



	public void initialize()
			throws ManifoldCFException
	{
		destroy();
		// Create the table
		Map columnMap = new HashMap();
		columnMap.put(ID_FEILD,new ColumnDescription("BIGINT",true,false,null,null,false));
		columnMap.put(FILE_NAME,new ColumnDescription("VARCHAR(255)",false,false,null,null,false));
		columnMap.put(FILE_CREATED_DATE,new ColumnDescription("DATE",false,true,null,null,false));
		columnMap.put(FILE_MODIFIED_DATE,new ColumnDescription("DATE",false,true,null,null,false));
		columnMap.put(FILE_SIZE,new ColumnDescription("BIGINT",false,true,null,null,false));
		performCreate(columnMap,null);
		System.out.println("initialize() -> successfully:");
		// Create an index
		//performAddIndex(null,new IndexDescription(true,new String[]{FILE_NAME,FILE_CREATED_DATE,FILE_MODIFIED_DATE,FILE_SIZE}));
	}


	public void insertFileInfo(String filename, Date createdDate,Date modifiedDate,Long fileSize)
			throws ManifoldCFException
	{
		// Prepare the fields
		Map fields = new HashMap();
		fields.put(ID_FEILD,IDFactory.make(threadContext));
		fields.put(FILE_NAME,filename);
		fields.put(FILE_CREATED_DATE,createdDate);
		fields.put(FILE_MODIFIED_DATE,modifiedDate);
		fields.put(FILE_SIZE,fileSize);
		// Prepare the invalidation keys
		//		StringSet invalidationKeys = new StringSet(new String[]{makeKeyCacheKey(filename)});
		performInsert(fields,null);
		System.out.println("Records inserted successfully-");
	}


	public void showFileInfo(String filename) throws ManifoldCFException {
		// We will cache this against the table as a whole, and also against the
		// values for the given key.  Any changes to either will invalidate it.
//		StringSet cacheKeys = new StringSet(new String[]{TABLE_CACHEKEY,makeKeyCacheKey(filename)});
		// Construct the parameters
		ArrayList params = new ArrayList();
		params.add(filename);
		// Perform the query
		IResultSet set = performQuery("SELECT "+FILE_NAME+","+FILE_SIZE+" FROM "+getTableName()+
				" WHERE "+FILE_NAME+"=?",params,null,null);
		// Assemble the results
		System.out.println("Number of rows "+set.getRowCount());
		
		int i = 0;
		while (i < set.getRowCount()){
			IResultRow row = set.getRow(i);
			System.out.println("FILE NAME FROM DB "+(String)row.getValue(FILE_NAME));
			System.out.print("FILE SIZE FROM DB " +(String)row.getValue(FILE_SIZE));
			i++;
		}	

	}


	public void showAllFileInfo()throws ManifoldCFException {

		System.out.println("=======================showAllFileInfo() start===================================");
//		StringSet cacheKeys = new StringSet(new String[]{TABLE_CACHEKEY,makeKeyCacheKey(ID_FEILD)});
		// Perform the query
		IResultSet set = performQuery("SELECT * FROM "+getTableName(),null,null,null);
		// Assemble the results

		int i = 0;
		while (i < set.getRowCount())
		{
			IResultRow row = set.getRow(i);
			System.out.println((String)row.getValue(ID_FEILD));
			System.out.println((String)row.getValue(FILE_NAME));
			System.out.println((String)row.getValue(FILE_CREATED_DATE));
			System.out.println((String)row.getValue(FILE_MODIFIED_DATE));
			System.out.print((String)row.getValue(FILE_SIZE));
			i++;
		}
		System.out.println("=======================showAllFileInfo() End===================================");
	}

	public void deleteKeyValues(String fileName)
			throws ManifoldCFException
	{
		// Prepare the parameters
		ArrayList params = new ArrayList();
		params.add(fileName);
		// Prepare the invalidation keys
		StringSet invalidationKeys = new StringSet(new String[]{makeKeyCacheKey(fileName)});
		// Perform the delete
		performDelete("WHERE "+FILE_NAME+"=?",params,invalidationKeys);
	}

	public void deleteValue(String fileName)
			throws ManifoldCFException
	{
		// Prepare the parameters
		ArrayList params = new ArrayList();
		params.add(fileName);
		// Prepare the invalidation keys
		StringSet invalidationKeys = new StringSet(new String[]{TABLE_CACHEKEY});
		// Perform the delete
		performDelete("WHERE "+FILE_NAME+"=?",params,invalidationKeys);
	}

	protected static String printValues(String[] values)
	{
		StringBuffer sb = new StringBuffer("{");
		int i = 0;
		while (i < values.length)
		{
			if (i > 0)
				sb.append(",");
			sb.append(values[i++]);
		}
		sb.append("}");
		return sb.toString();
	}


	public void destroy()throws ManifoldCFException{
		performDrop(tableCacheKeySet);
	}

	/** Construct a cache key for the given lookup key */
	protected static String makeKeyCacheKey(String key){
		return KEY_CACHEKEY_PREFIX + key;
	}

}
