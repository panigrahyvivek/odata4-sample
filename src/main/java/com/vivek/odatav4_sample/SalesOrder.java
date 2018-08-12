/*******************************************************************************
 * (c) 201X SAP SE or an SAP affiliate company. All rights reserved.
 ******************************************************************************/
package com.vivek.odatav4_sample;

import com.sap.cloud.sdk.service.prov.api.annotations.Key;

public class SalesOrder {

	@Key
	private String SalesOrderID;

	private String CustomerName;

	private String Note;

	private int GrossAmount;

	
	
	

	public String getSalesOrderID() {
		return SalesOrderID;
	}

	public void setSalesOrderID(String salesOrderID) {
		SalesOrderID = salesOrderID;
	}

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	public String getNote() {
		return Note;
	}

	public void setNote(String note) {
		Note = note;
	}

	public int getGrossAmount() {
		return GrossAmount;
	}

	public void setGrossAmount(int grossAmount) {
		GrossAmount = grossAmount;
	}



}
