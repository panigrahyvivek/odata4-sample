/*******************************************************************************
 * (c) 201X SAP SE or an SAP affiliate company. All rights reserved.
 ******************************************************************************/
package com.vivek.odatav4_sample;

import com.sap.cloud.sdk.result.ElementName;
import com.sap.cloud.sdk.service.prov.api.annotations.Key;

public class ProductEntity {
	
	@Key
	@ElementName( "ProductID" )
    private String ProductID;

	@ElementName( "Name" )
    private String Name;

    @ElementName( "Description" )
    private String Description;
 
    @ElementName( "Category" )
    private String Category;
	
	@ElementName( "SupplierID")
	private String SupplierID;
	
	@ElementName( "TaxTarifCode")
	private int TaxTarifCode;
	
	@ElementName( "MeasureUnit")
	private String MeasureUnit;
	
	@ElementName( "CurrencyCode")
	private String CurrencyCode;
	
	@ElementName( "TypeCode")
	private String TypeCode;
	
    public String getSupplierID() {
		return SupplierID;
	}

	public void setSupplierID(String supplierID) {
		SupplierID = supplierID;
	}

	public int getTaxTarifCode() {
		return TaxTarifCode;
	}

	public void setTaxTarifCode(int taxTarifCode) {
		TaxTarifCode = taxTarifCode;
	}

	public String getMeasureUnit() {
		return MeasureUnit;
	}

	public void setMeasureUnit(String measureUnit) {
		MeasureUnit = measureUnit;
	}

	public String getCurrencyCode() {
		return CurrencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		CurrencyCode = currencyCode;
	}

	public String getTypeCode() {
		return TypeCode;
	}

	public void setTypeCode(String typeCode) {
		TypeCode = typeCode;
	}
    
    public String getProductID() {
		return ProductID;
	}

	public void setProductID(String productID) {
		this.ProductID = productID;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		this.Name = name;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		this.Description = description;
	}

	public String getCategory() {
		return Category;
	}

	public void setCategory(String category) {
		this.Category = category;
	}

	
}
