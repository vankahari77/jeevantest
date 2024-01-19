package com.unipart.BSkyB.priv;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2016-12-16 11:29:44 GMT
// -----( ON-HOST: SRV451.ugc

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.softwareag.util.IDataMap;
import com.ibm.icu.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
// --- <<IS-END-IMPORTS>> ---

public final class services

{
	// ---( internal utility methods )---

	final static services _instance = new services();

	static services _newInstance() { return new services(); }

	static services _cast(Object o) { return (services)o; }

	// ---( server methods )---




	public static final void datePatternConversion (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(datePatternConversion)>> ---
		// @sigtype java 3.5
		// [i] field:0:required dateStr
		// [o] field:0:required result
		IDataCursor cursor = null;  
		try  
		{  
			String result="";
		    SimpleDateFormat sdf;
		    SimpleDateFormat sdf1;
		  
		    	String dateStr = IDataUtil.getString( cursor, "dateStr" ); 
		    	sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		        sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		        result=sdf1.format(sdf.parse(dateStr));
		   IDataUtil.put( cursor, "result", result );  
		}  
		catch( Throwable t )  
		{  
		   throw new ServiceException( t );  
		}  
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void getSLOCs (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getSLOCs)>> ---
		// @sigtype java 3.5
		// [i] field:0:required notes
		// [o] field:0:required fromSLOC
		// [o] field:0:required toSLOC
		// --------------------------------------------------------------------------------
		// Get the pipeline
		// --------------------------------------------------------------------------------
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		String notes = IDataUtil.getString(pipelineCursor, "notes");
		
		// --------------------------------------------------------------------------------
		// Initialise the output variables
		// --------------------------------------------------------------------------------
		
		// Possible combinations in notes field
		
		// UnLocked(xxx)- Locked(yyy)
		// UnLocked()- Locked(yyy)
		// UnLocked(xxx)- Locked()
		// UnLocked()- Locked()
		
		// Locked(xxx)- UnLocked(yyy)
		// Locked(xxx)- UnLocked()
		// Locked()- UnLocked(yyy)
		// Locked()- UnLocked()
		
		// Locked(xxx)- Locked(yyy)
		// Locked(xxx)- Locked()
		// Locked()- Locked(yyy)
		// Locked()- Locked()
		
		String unLockedSLOC = "";
		String lockedSLOC = "";
		String fromSLOC = "";
		String toSLOC = "";
		
		// Look for  UnLocked SLOC
		int unLockedStart = notes.indexOf("UnLocked(");
		
		if (unLockedStart == -1) {
			// No UnLocked keyword, assume two Locked keywords.
			unLockedStart = notes.indexOf("Locked(");
			unLockedStart += 7;
		}
		else {
			unLockedStart += 9;
		}
		
		int unLockedEnd = notes.indexOf(")", unLockedStart);
		
		if (unLockedEnd <= unLockedStart) {
			unLockedSLOC = "NDCPICK";
		}
		else {
			unLockedSLOC = notes.substring(unLockedStart, unLockedEnd);
		}
		
		// Try extracting Locked SLOC after UnLocked or first Locked SLOC
		int lockedStart;
		lockedStart = notes.indexOf("Locked(", unLockedEnd);
		if (lockedStart == -1) {
			// Not found, must be before UnLocked so start from beginning of notes field 
			lockedStart = notes.indexOf("Locked(");
		}
		lockedStart += 7;
		
		int lockedEnd = notes.indexOf(")", lockedStart);
		
		if (lockedEnd <= lockedStart) {
			lockedSLOC = "NDCIQC";
		}
		else {
			lockedSLOC = notes.substring(lockedStart, lockedEnd);
		}
		
		// Determine from and to SLOC's
		if (unLockedStart < lockedStart) {
			fromSLOC = unLockedSLOC;
			toSLOC = lockedSLOC;
		}
		else { 
			fromSLOC = lockedSLOC;
			toSLOC = unLockedSLOC;			
		}
		
		
		// --------------------------------------------------------------------------------
		// Put the field values back in the pipeline
		// --------------------------------------------------------------------------------
		
		IDataUtil.put(pipelineCursor, "fromSLOC", fromSLOC);
		IDataUtil.put(pipelineCursor, "toSLOC", toSLOC);
		
		pipelineCursor.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void suppressOrderNumber (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(suppressOrderNumber)>> ---
		// @sigtype java 3.5
		// [i] recref:0:required BSB_StockMovement com.unipart.BSkyB.priv.docs:BSB_StockMovement
		// [i] field:1:required prefix
		// [o] recref:0:required BSB_StockMovement com.unipart.BSkyB.priv.docs:BSB_StockMovement
		// pipeline
		/** 
		 * Utility service for suppressing Order number based on the supplied prefixes
		 * @version 1 
		 * 
		 * @param BSB_StockMovement - 3pl stock movement document
		 *  
		 * @param prefix - Sting array of prefixes to suppress
		 * 
		 * @return - BSB_StockMovement with customer order number suppressed for the prefixes
		 *  
		 * @throws ServiceException if there are any errors
		 * 
		 * @author  DT
		 */
		String	CustomerOrderNumber = null;
		try{
		IDataMap 	pl = new IDataMap(pipeline);
		String[]	prefixes= pl.getAsStringArray("prefix");
		IData [] 	StockMoves = (IData[]) pl.getNested("BSB_StockMovement","StockMovement","Movements","StockMove");
		for ( IData StockMove :StockMoves )
			 {
				IDataCursor StockMoveCursor = StockMove.getCursor();			
					CustomerOrderNumber = IDataUtil.getString( StockMoveCursor, "CustomerOrderNumber" );
				if (CustomerOrderNumber!=null)	
				for (String prefix: prefixes)
					 if(CustomerOrderNumber.startsWith(prefix)) IDataUtil.put( StockMoveCursor,"CustomerOrderNumber", "" );															
					  StockMoveCursor.destroy();
			}
		   
		}catch (RuntimeException re)
		{
			  throw new RuntimeException ("Runtime Error while processing suppressOrderNumber "+ re);
			
		}
		
		catch (Exception e){
				throw new ServiceException ("Error while processing suppression of Customer Order Number"+ e.toString());
		}
		
			
		// --- <<IS-END>> ---

                
	}
}

