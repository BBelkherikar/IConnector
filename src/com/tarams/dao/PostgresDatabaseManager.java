package com.tarams.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.manifoldcf.core.database.DBInterfacePostgreSQL;
import org.apache.manifoldcf.core.interfaces.ColumnDescription;
import org.apache.manifoldcf.core.interfaces.IResultRow;
import org.apache.manifoldcf.core.interfaces.IResultSet;
import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class PostgresDatabaseManager extends DBInterfacePostgreSQL {

	protected final static String DATABASE_TABLE_NAME = "TestTable";

	protected final static String ID_FEILD = "id";
	protected final static String FILE_NAME = "fileName";
	protected final static String FILE_CREATED_DATE = "createdDate";
	protected final static String FILE_MODIFIED_DATE = "modifiedDate";
	protected final static String FILE_SIZE= "size";

	/** The thread context */
	protected IThreadContext threadContext;

	public PostgresDatabaseManager(IThreadContext tc, String databaseName, String userName, String password) throws ManifoldCFException {
		super(tc, databaseName, userName, password);
		this.threadContext=tc;
	}


	@Override
	public void openDatabase() throws ManifoldCFException {
		super.openDatabase();
		System.out.println("==Data base in opened ==");
	}



	public void createDatabaseTable() throws ManifoldCFException {

		Map columnMap = new HashMap();
		//		columnMap.put(ID_FEILD,new ColumnDescription("BIGINT",true,false,null,null,false));
		columnMap.put(FILE_NAME,new ColumnDescription("VARCHAR(255)",false,false,null,null,false));
		columnMap.put(FILE_CREATED_DATE,new ColumnDescription("DATE",false,true,null,null,false));
		columnMap.put(FILE_MODIFIED_DATE,new ColumnDescription("DATE",false,true,null,null,false));
		columnMap.put(FILE_SIZE,new ColumnDescription("BIGINT",false,true,null,null,false));
		performCreate(DATABASE_TABLE_NAME,columnMap,null);
		System.out.println("Table created successfully with name"+"\t"+DATABASE_TABLE_NAME);
	}


	public void insertFileInfo(Map<String, Object> paramMap) throws ManifoldCFException {
		performInsert(DATABASE_TABLE_NAME, paramMap, null);
		System.out.println(":Records inserted successfully:");
	}


	public void showAllFileInfo()throws ManifoldCFException {

		System.out.println("=============showAllFileInfo() start=============");
		IResultSet set = performQuery("SELECT * FROM "+DATABASE_TABLE_NAME,null,null,null);
		System.out.println("Total No of Rows:"+set.getRowCount());
		int i = 0;
		while (i < set.getRowCount()) {
			IResultRow row = set.getRow(i);
			//			System.out.println((String)row.getValue(ID_FEILD));
			System.out.println(row.getValue(FILE_NAME));
			System.out.println(row.getValue(FILE_CREATED_DATE));
			System.out.println(row.getValue(FILE_SIZE));
			System.out.println(row.getValue(FILE_MODIFIED_DATE));
			i++;
		}
		System.out.println("=============showAllFileInfo() end=============");
	}


	public String[] getAllFileNames()  throws ManifoldCFException{
		IResultSet set = performQuery("SELECT * FROM "+DATABASE_TABLE_NAME,null,null,null);

		String[] results = new String[set.getRowCount()];
		int i = 0;
		while (i < results.length){	
			IResultRow row = set.getRow(i);
			results[i] = (String)row.getValue(FILE_NAME);
			i++;
		}
		return results;
	}

	public String printValues(String[] values) {
		StringBuffer sb = new StringBuffer("{");
		int i = 0;
		while (i < values.length){
			if (i > 0)
				sb.append(",");
			sb.append(values[i++]);
		}
		sb.append("}");
		return sb.toString();
	}


	@Override
	public void closeDatabase() throws ManifoldCFException {
		super.closeDatabase();
		System.out.println("--Database is closed now--");
	}


}
