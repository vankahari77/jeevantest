package com.unipart.BSkyB.priv;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-05-24 07:49:21 BST
// -----( ON-HOST: SRV451.ugc

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
// --- <<IS-END-IMPORTS>> ---

public final class util

{
	// ---( internal utility methods )---

	final static util _instance = new util();

	static util _newInstance() { return new util(); }

	static util _cast(Object o) { return (util)o; }

	// ---( server methods )---




	public static final void prepareAppleASNOrderLines (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(prepareAppleASNOrderLines)>> ---
		// @sigtype java 3.5
		// [i] record:1:optional DTL
		// [i] - field:0:required @record-id
		// [i] - field:0:required @segment-id
		// [i] - field:0:optional @area
		// [i] - field:0:optional @position
		// [i] - field:0:optional @record-count
		// [i] - field:0:optional Pallet_SSCC
		// [i] - field:0:optional SSCC_Indicator
		// [i] - field:0:optional Customer_PO_line_item_Number
		// [i] - field:0:optional Package_SSCC
		// [i] - field:0:optional Customer_PartNumber
		// [i] - field:0:optional Apple_Part_Number
		// [i] - field:0:optional Serialized_Flag
		// [i] - field:0:optional Serial_Number
		// [i] - field:0:optional IMEI
		// [i] - field:0:optional Quantity
		// [i] - field:0:optional Software_Version
		// [i] - field:0:optional ICCID
		// [i] - field:0:optional Universal_Product_Code
		// [i] - field:0:optional UNLOCKCODE
		// [i] - field:0:optional unDefData
		// [i] - field:0:optional segmentCount
		// [i] field:0:required purchaseOrderNo
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		
			// DTL
			IData[]	DTL = IDataUtil.getIDataArray( pipelineCursor, "DTL" );
			String	purchaseOrderNo = IDataUtil.getString( pipelineCursor, "purchaseOrderNo" );
			IData	Pallets = null;
			if ( DTL != null)
			{
				Map<String,Map<String, Map<String,IData>>>    hmPallet = new HashMap<String,Map<String, Map<String,IData>>>();
				for ( int i = 0; i < DTL.length; i++ )
				{
					IDataCursor DTLCursor = DTL[i].getCursor();
						String	Pallet_SSCC = IDataUtil.getString( DTLCursor, "Pallet_SSCC" );
						String	Customer_PO_line_item_Number = IDataUtil.getString( DTLCursor, "Customer_PO_line_item_Number" );
						String	Package_SSCC = IDataUtil.getString( DTLCursor, "Package_SSCC" );
						String	Customer_PartNumber = IDataUtil.getString( DTLCursor, "Customer_PartNumber" );
						String	Apple_Part_Number = IDataUtil.getString( DTLCursor, "Apple_Part_Number" );
						String	Serial_Number = IDataUtil.getString( DTLCursor, "Serial_Number" );
						String	IMEI = IDataUtil.getString( DTLCursor, "IMEI" );
						String	Quantity = IDataUtil.getString( DTLCursor, "Quantity" );
						
						IData serialItem=null;
						if( IMEI != null && Serial_Number != null)
						{
							serialItem=IDataFactory.create();
							IDataCursor SerialItemCursor = serialItem.getCursor();
							IDataUtil.put( SerialItemCursor, "IMEI", IMEI );
							IDataUtil.put( SerialItemCursor, "SerialNo", Serial_Number );
							SerialItemCursor.destroy();
						}
		
					DTLCursor.destroy();
					
					Map<String, Map<String, IData>> hmTempContainer = (Map<String, Map<String, IData>>)(hmPallet.get(Pallet_SSCC));
					String orderLinekey = Customer_PO_line_item_Number +"_"+ Apple_Part_Number+"_"+Quantity;
					if(hmTempContainer != null)
					{
						
						HashMap<String, IData> hmTempOrderLines = (HashMap<String, IData>) hmTempContainer.get(Package_SSCC);
						if(hmTempOrderLines != null)
						{
							
							IData serialItems =  (IData) hmTempOrderLines.get(orderLinekey);
							
							if(serialItems != null)
							{
								IDataCursor serialItemsCursor = serialItems.getCursor();
								IData[] serialItemArray = IDataUtil.getIDataArray( serialItemsCursor, "SerialItem" );
								IData[] newserialItemArray = new IData[serialItemArray.length + 1];
								for(int j=0; j<serialItemArray.length; j++)
								{
									newserialItemArray[j]=serialItemArray[j];
								}
								newserialItemArray[serialItemArray.length]=serialItem;
								IDataUtil.put( serialItemsCursor, "SerialItem", newserialItemArray );
								serialItemsCursor.destroy();
							
								
							}
							else
							{
								serialItems = IDataFactory.create();
								IDataCursor serialItemsCursor = serialItems.getCursor();
								IData[] serialItemArray = new IData[1];
								serialItemArray[0]=serialItem;
								IDataUtil.put( serialItemsCursor, "SerialItem", serialItemArray );
								serialItemsCursor.destroy();
								hmTempOrderLines.put(orderLinekey, serialItems);
							}
						}
						else
						{
							hmTempOrderLines= new HashMap<String, IData>();
							IData serialItems = IDataFactory.create();
							IDataCursor serialItemsCursor = serialItems.getCursor();
							IData[] serialItemsArray = new IData[1];
							serialItemsArray[0]=serialItem;
							IDataUtil.put( serialItemsCursor, "SerialItem", serialItemsArray );
							serialItemsCursor.destroy();
							hmTempOrderLines.put(Package_SSCC, serialItems);
							hmTempContainer.put(Pallet_SSCC, hmTempOrderLines);
							
						}
		
					}
					else
					{
						HashMap<String, IData> hmTempOrderLines = new HashMap<String, IData>();
						IData serialItems = IDataFactory.create();
						IDataCursor serialItemsCursor = serialItems.getCursor();
						IData[] serialItemsArray = new IData[1];
						serialItemsArray[0]=serialItem;
						IDataUtil.put( serialItemsCursor, "SerialItem", serialItemsArray );
						serialItemsCursor.destroy();
						
						hmTempOrderLines.put(orderLinekey, serialItems);	
						hmTempContainer = new HashMap<String, Map<String, IData>>();
						hmTempContainer.put(Package_SSCC, hmTempOrderLines);
						hmPallet.put(Pallet_SSCC, hmTempContainer);
						
											
					}
				}
				
				// ASN.Pallets
				Pallets = IDataFactory.create();
				IDataCursor PalletsCursor = Pallets.getCursor();
		
				// ASN.Pallets.Pallet
				IData[]	Pallet = new IData[hmPallet.keySet().size()];
				int i_4=0;
				for ( Map.Entry<String,Map<String, Map<String,IData>>> entry1 : hmPallet.entrySet()) 
				{
					String keyPallet = entry1.getKey();
					Pallet[i_4] = IDataFactory.create();
					IDataCursor PalletCursor = Pallet[i_4].getCursor();
					IDataUtil.put( PalletCursor, "PalletID",  keyPallet );	
					Map<String, Map<String, IData>> hmContainer = (Map<String, Map<String, IData>>)entry1.getValue();
					
					// ASN.Pallets.Pallet.Packages
					IData	Packages = IDataFactory.create();
					IDataCursor PackagesCursor = Packages.getCursor();
					
					IData[]	Package = new IData[hmContainer.keySet().size()];
					
					//IData[]	Container = new IData[hmContainer.keySet().size()];
					int i_5=0;
					for ( Map.Entry<String, Map<String, IData>> entry2 : hmContainer.entrySet()) 
					{
						String keyContainer = entry2.getKey();
						Package[i_5] = IDataFactory.create();
						IDataCursor PackageCursor = Package[i_5].getCursor();
						IDataUtil.put( PackageCursor, "PackageID", keyContainer );
						
						Map<String, IData> hmOrderLines = (Map<String, IData>)entry2.getValue();
						
						IData	OrderLines = IDataFactory.create();
						IDataCursor OrderLinesCursor = OrderLines.getCursor();
						IData[]	OrderLine = new IData[hmOrderLines.keySet().size()];
						int i_6=0;
						for ( Map.Entry<String, IData> entry3 : hmOrderLines.entrySet()) 
						{
							String keyOrderLine = entry3.getKey();
							OrderLine[i_6]=IDataFactory.create();
							IDataCursor OrderLineCursor = OrderLine[i_6].getCursor();
							StringTokenizer st = new StringTokenizer(keyOrderLine,"_");  
						     while (st.hasMoreTokens()) {  
						         // Prepare the Order Header
						         IDataUtil.put( OrderLineCursor, "LineNumber", st.nextToken() );
						         IDataUtil.put( OrderLineCursor, "SupplierPartNo", st.nextToken() );
						         IDataUtil.put( OrderLineCursor, "Quantity", st.nextToken() );
						        
						     }  
		
						     IData serialItems = (IData)entry3.getValue();
						     IDataCursor serialItemsCursor = serialItems.getCursor();
						     IData[] serialItemArray = IDataUtil.getIDataArray( serialItemsCursor, "SerialItem" );
						     if(serialItemArray != null)
						     {
						    	 IDataUtil.put( OrderLineCursor, "Quantity", "" + serialItemArray.length );
						     }
						     serialItemsCursor.destroy();
						     IDataUtil.put( OrderLineCursor, "SerialItems", serialItems);
						     OrderLineCursor.destroy();
		     
						     i_6++;
						    
						}
						
						
						
						
						IDataUtil.put( OrderLinesCursor, "OrderLine", OrderLine );
						OrderLinesCursor.destroy();
						IDataUtil.put( PackageCursor, "OrderLines", OrderLines );
						PackageCursor.destroy();
						i_5++;
					}
					IDataUtil.put( PackagesCursor, "Package", Package );
					PackagesCursor.destroy();
					IDataUtil.put( PalletCursor, "Packages", Packages );
					PalletCursor.destroy();
					i_4++;
					
				}
				IDataUtil.put( PalletsCursor, "Pallet", Pallet );
				PalletsCursor.destroy();
			}
			
		pipelineCursor.destroy();
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "Pallets", Pallets);
		pipelineCursor_1.destroy();
		
		// pipeline
			
		// --- <<IS-END>> ---

                
	}



	public static final void prepareNameWithDelimiter (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(prepareNameWithDelimiter)>> ---
		// @sigtype java 3.5
		// [i] field:0:required title
		// [i] field:0:required firstname
		// [i] field:0:required surname
		// [o] field:0:required nameDelimiter
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
			String	title = IDataUtil.getString( pipelineCursor, "title" );
			String	firstname = IDataUtil.getString( pipelineCursor, "firstname" );
			String	surname = IDataUtil.getString( pipelineCursor, "surname" );
		pipelineCursor.destroy();
		
		if(title == null || title.isEmpty())
		{
			title=" ";
		}
		if(firstname == null || firstname.isEmpty())
		{
			firstname=" ";
		}
		if(surname == null || surname.isEmpty())
		{
			surname = " ";
		}
		
		String nameDelimiter = title +"+"+firstname+"+"+surname;
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "nameDelimiter", nameDelimiter );
		pipelineCursor_1.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void validateDataForAnovoASN (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(validateDataForAnovoASN)>> ---
		// @sigtype java 3.5
		// [i] record:0:required InventoryMovement
		// [i] - field:0:required @type
		// [i] - field:0:required WmSenderID
		// [i] - field:0:required WmReceiverID
		// [i] - field:0:required Key
		// [i] - field:0:required SiteID
		// [i] - field:0:required OwnerID
		// [i] - field:0:required ClientID
		// [i] - field:0:required SkuID
		// [i] - field:0:required ReferenceID
		// [i] - field:0:required LineID
		// [i] - field:0:optional FromSiteID
		// [i] - field:0:optional ToSiteID
		// [i] - field:0:required FromLocationID
		// [i] - field:0:optional ToLocationID
		// [i] - field:0:optional FinalLocationID
		// [i] - field:0:optional ConfigurationID
		// [i] - field:0:optional ContainerID
		// [i] - field:0:optional PalletID
		// [i] - field:0:optional BatchID
		// [i] - field:0:optional QualityControlCStatus
		// [i] - field:0:optional ExpiryDateTimeStamp
		// [i] - field:0:optional ConditionID
		// [i] - field:0:optional SpecCode
		// [i] - field:0:required DateTimeStamp
		// [i] - field:0:optional SupplierID
		// [i] - field:0:optional CustomerID
		// [i] - field:0:optional ShipByDate
		// [i] - field:0:optional DeliverByDate
		// [i] - field:0:required UpdateQuantity
		// [i] - field:0:optional Notes
		// [i] - field:0:optional UserDefType1
		// [i] - field:0:optional UserDefType2
		// [i] - field:0:optional UserDefType3
		// [i] - field:0:optional UserDefType4
		// [i] - field:0:optional UserDefType5
		// [i] - field:0:optional UserDefType6
		// [i] - field:0:optional UserDefType7
		// [i] - field:0:optional UserDefType8
		// [i] - field:0:optional UserDefChk1
		// [i] - field:0:optional UserDefChk2
		// [i] - field:0:optional UserDefChk3
		// [i] - field:0:optional UserDefChk4
		// [i] - field:0:optional UserDefDate1
		// [i] - field:0:optional UserDefDate2
		// [i] - field:0:optional UserDefDate3
		// [i] - field:0:optional UserDefDate4
		// [i] - field:0:optional UserDefNum1
		// [i] - field:0:optional UserDefNum2
		// [i] - field:0:optional UserDefNum3
		// [i] - field:0:optional UserDefNum4
		// [i] - field:0:optional UserDefNote1
		// [i] - field:0:optional UserDefNote2
		// [i] - field:0:optional ExtraNotes
		// [i] - field:0:optional ReasonID
		// [i] - field:0:optional UserID
		// [i] - field:0:optional TagID
		// [i] - field:0:optional ListID
		// [i] - field:0:optional LockCode
		// [i] - field:0:optional FromZone
		// [i] - field:0:optional ToZone
		// [i] - field:0:optional SerialAtPick
		// [i] - field:0:optional OrderType
		// [i] - field:0:optional DeliveryConfirmationExpected
		// [i] - field:0:optional QuantityToBuild
		// [i] - field:0:optional WorkGroup
		// [i] - field:0:optional RDTUserMode
		// [i] - field:0:optional OrderSource
		// [i] - record:1:optional SerialData
		// [i] -- field:0:required SerialNumber
		// [i] -- field:0:optional SerialNumberType
		// [o] field:0:required valid
		// [o] field:0:required message
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String valid = "true";
		String message="Elements empty or not present-";
		
			// InventoryMovement
			IData	InventoryMovement = IDataUtil.getIData( pipelineCursor, "InventoryMovement" );
			if ( InventoryMovement != null)
			{
				IDataCursor InventoryMovementCursor = InventoryMovement.getCursor();
		
					String	SiteID = IDataUtil.getString( InventoryMovementCursor, "SiteID" );
					
					String	SkuID = IDataUtil.getString( InventoryMovementCursor, "SkuID" );
					if(SkuID == null || SkuID.isEmpty())
					{
						valid="false";
						message=message + "SkuID,";
						
					}
					String	ReferenceID = IDataUtil.getString( InventoryMovementCursor, "ReferenceID" );
					if(ReferenceID == null || ReferenceID.isEmpty())
					{
						valid="false";
						message=message + "ReferenceID,";
					}
		
					String	LineID = IDataUtil.getString( InventoryMovementCursor, "LineID" );
					if(LineID == null || LineID.isEmpty())
					{
						valid="false";
						message=message + "LineID,";
					}
		
					String	ToLocationID = IDataUtil.getString( InventoryMovementCursor, "ToLocationID" );
					/*if(ToLocationID == null || ToLocationID.isEmpty())
					{
						valid="false";
						message=message + "ToLocationID,";
					} */
		
					String	ContainerID = IDataUtil.getString( InventoryMovementCursor, "ContainerID" );
					if(ContainerID == null || ContainerID.isEmpty())
					{
						valid="false";
						message=message + "ContainerID,";
					}
		
					String	PalletID = IDataUtil.getString( InventoryMovementCursor, "PalletID" );
					
					if(PalletID == null || PalletID.isEmpty())
					{
						valid="false";
						message=message + "PalletID,";
					}
		
					String	SupplierID = IDataUtil.getString( InventoryMovementCursor, "SupplierID" );
					String	CustomerID = IDataUtil.getString( InventoryMovementCursor, "CustomerID" );
					if(CustomerID == null || CustomerID.isEmpty())
					{
						valid="false";
						message=message + "CustomerID,";
					}
					String	DateTimeStamp = IDataUtil.getString( InventoryMovementCursor, "DateTimeStamp" );
					if(DateTimeStamp == null || DateTimeStamp.isEmpty())
					{
						valid="false";
						message=message + "DateTimeStamp,";
					}
		
					String	DeliverByDate = IDataUtil.getString( InventoryMovementCursor, "DeliverByDate" );
					String	OrderType = IDataUtil.getString( InventoryMovementCursor, "OrderType" );
		
		
				InventoryMovementCursor.destroy();
			}
		pipelineCursor.destroy();
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "valid", valid );
		IDataUtil.put( pipelineCursor_1, "message", message );
		pipelineCursor_1.destroy();
			
		// --- <<IS-END>> ---

                
	}
}

