/*******************************************************************************
 * (c) 201X SAP SE or an SAP affiliate company. All rights reserved.
 ******************************************************************************/
package com.vivek.odatav4_sample;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sap.cloud.sdk.hana.connectivity.cds.CDSException;
import com.sap.cloud.sdk.hana.connectivity.cds.CDSQuery;
import com.sap.cloud.sdk.hana.connectivity.cds.CDSSelectQueryBuilder;
import com.sap.cloud.sdk.hana.connectivity.cds.CDSSelectQueryResult;
import com.sap.cloud.sdk.hana.connectivity.cds.ConditionBuilder;
import com.sap.cloud.sdk.hana.connectivity.handler.CDSDataSourceHandler;
import com.sap.cloud.sdk.hana.connectivity.handler.DataSourceHandlerFactory;
import com.sap.cloud.sdk.odatav2.connectivity.FilterExpression;
import com.sap.cloud.sdk.odatav2.connectivity.ODataCreateRequest;
import com.sap.cloud.sdk.odatav2.connectivity.ODataCreateRequestBuilder;
import com.sap.cloud.sdk.odatav2.connectivity.ODataCreateResult;
import com.sap.cloud.sdk.odatav2.connectivity.ODataDeleteRequest;
import com.sap.cloud.sdk.odatav2.connectivity.ODataDeleteRequestBuilder;
import com.sap.cloud.sdk.odatav2.connectivity.ODataDeleteResult;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQueryBuilder;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQueryResult;
import com.sap.cloud.sdk.odatav2.connectivity.ODataType;
import com.sap.cloud.sdk.odatav2.connectivity.ODataUpdateRequest;
import com.sap.cloud.sdk.odatav2.connectivity.ODataUpdateRequestBuilder;
import com.sap.cloud.sdk.odatav2.connectivity.ODataUpdateResult;
import com.sap.cloud.sdk.service.prov.api.DatasourceExceptionType;
import com.sap.cloud.sdk.service.prov.api.EntityData;
import com.sap.cloud.sdk.service.prov.api.Severity;
import com.sap.cloud.sdk.service.prov.api.annotations.Action;
import com.sap.cloud.sdk.service.prov.api.annotations.Function;
import com.sap.cloud.sdk.service.prov.api.operations.Create;
import com.sap.cloud.sdk.service.prov.api.operations.Delete;
import com.sap.cloud.sdk.service.prov.api.operations.Query;
import com.sap.cloud.sdk.service.prov.api.operations.Read;
import com.sap.cloud.sdk.service.prov.api.operations.Update;
import com.sap.cloud.sdk.service.prov.api.request.CreateRequest;
import com.sap.cloud.sdk.service.prov.api.request.DeleteRequest;
import com.sap.cloud.sdk.service.prov.api.request.OperationRequest;
import com.sap.cloud.sdk.service.prov.api.request.QueryRequest;
import com.sap.cloud.sdk.service.prov.api.request.ReadRequest;
import com.sap.cloud.sdk.service.prov.api.request.UpdateRequest;
import com.sap.cloud.sdk.service.prov.api.response.CreateResponse;
import com.sap.cloud.sdk.service.prov.api.response.DeleteResponse;
import com.sap.cloud.sdk.service.prov.api.response.ErrorResponse;
import com.sap.cloud.sdk.service.prov.api.response.OperationResponse;
import com.sap.cloud.sdk.service.prov.api.response.QueryResponse;
import com.sap.cloud.sdk.service.prov.api.response.ReadResponse;
import com.sap.cloud.sdk.service.prov.api.response.UpdateResponse;

@Component
public class EPMSampleService {

	Logger logger = LoggerFactory.getLogger(EPMSampleService.class);

	 private static final String DESTINATION_NAME = "GWEndpoint";
	 
	//Implementation of QUERY operation, for Products 	
	@Query(serviceName = "EPMSampleService", entity = "Products")
	public QueryResponse getProducts(QueryRequest queryRequest) { // the name of the method can be arbitrary
		logger.debug("==> now call backend OData V2 service");		

		QueryResponse queryResponse = null;
		try {

			logger.debug("==> now execute query on Products");

			ODataQueryResult result = ODataQueryBuilder
					.withEntity("/sap/opu/odata/IWBEP/GWSAMPLE_BASIC", "ProductSet") 
					.select("ProductID", "Name", "Description", "Category")
					.build()
					.execute(DESTINATION_NAME);

			logger.debug("==> After calling backend OData V2 service: result: " + result);		

			final List<ProductEntity> v2ProductList = result.asList(ProductEntity.class);

			queryResponse = QueryResponse.setSuccess().setData(v2ProductList).response();
			
			// you can also retrieve the ODataQueryResult as Map Data and set the same in the response
			
			//final List<Map<String, Object>> v2ProductMapList = result.asListOfMaps();

			//queryResponse = QueryResponse.setSuccess().setData(v2ProductMapList).response();
			
			return queryResponse;

		} catch (IllegalArgumentException | ODataException e) {
			logger.error("==> Exception calling backend OData V2 service for Query of Products: " + e.getMessage());


			ErrorResponse errorResponse = ErrorResponse.getBuilder()
					.setMessage("There is an error.  Check the logs for the details.")
					.setStatusCode(500)
					.setCause(e)
					.response();
			queryResponse = QueryResponse.setError(errorResponse);
		}
		return queryResponse;
	}

	// Implementation of READ operation, for Products
	@Read(entity = "Products", serviceName = "EPMSampleService")
	public ReadResponse getProduct(ReadRequest readRequest) {
		logger.debug("==> now call backend OData V2 service");		

		ReadResponse readResponse = null;
		try {

			logger.debug("==> now execute read for ProductID: " + readRequest.getKeys().get("ProductID").toString());

			ODataQueryResult result = ODataQueryBuilder.
					withEntity("/sap/opu/odata/IWBEP/GWSAMPLE_BASIC", "ProductSet")
					.select("ProductID", "Name", "Description", "Category")
					.keys(readRequest.getKeys()) // this is how you can pass the key predicate for the Read request
					.build()
					.execute(DESTINATION_NAME);

			logger.debug("==> After calling backend OData V2 service: result: " + result);		

			final ProductEntity v2Product = result.as(ProductEntity.class);
			readResponse = ReadResponse.setSuccess().setData(v2Product).response();
			
			// you can also retrieve the ODataQueryResult as Map Data and set the same in the response			
			
			//final Map<String, Object> v2ProductMap = result.asMap();
			//readResponse = ReadResponse.setSuccess().setData(v2ProductMap).response();
			
			return readResponse;

		} catch (IllegalArgumentException | ODataException e) {
			logger.error("==> Exception calling backend OData V2 service for Read of a Product: " + e.getMessage());


			ErrorResponse errorResponse = ErrorResponse.getBuilder()
// for setting translatable error messages, message key and the dynamic place holders for parameters are defined in i18n.properties file					
					.setMessage("PRD_READ_ERR", readRequest.getKeys().get("ProductID")) 
					.setStatusCode(500)
					.setCause(e)
					.response();
			readResponse = ReadResponse.setError(errorResponse);
		}
		return readResponse;
	}
	
	// Reading a Product for a SalesOrderLineItem
	@Read(entity = "Products", serviceName = "EPMSampleService", sourceEntity = "SalesOrderLineItems")
	public ReadResponse getProductForSOItem(ReadRequest readRequest) {

		ReadResponse readResponse = null;			
		EntityData SOItemEntity;
		try{
			String sourceEntityName = readRequest.getSourceEntityName();

			//Read SalesOrderLineItems to check if the passed SOLineItemID exists
			if ( sourceEntityName.equals("SalesOrderLineItems")) {	

				SOItemEntity = readSalesOrderLineItem(readRequest.getSourceKeys());				
				if (SOItemEntity == null) {
					ErrorResponse errorResponse = ErrorResponse.getBuilder()
							.setMessage("Parent SalesOrderLineItem does not exist")
							.setStatusCode(401)
							.response();
					readResponse = ReadResponse.setError(errorResponse);
				} else {
					String productID = SOItemEntity.getElementValue("ProductID").toString();

					try {

						logger.debug("==> now execute");
						ODataQueryResult result = ODataQueryBuilder.
								withEntity("/sap/opu/odata/IWBEP/GWSAMPLE_BASIC", "ProductSet('"+productID+"')" )
								.select("ProductID", "Name", "Description", "Category")
								.build()
								.execute(DESTINATION_NAME);

						logger.debug("==> After calling backend OData V2 service: result: " + result);		

						final ProductEntity v2Product = result.as(ProductEntity.class);
						readResponse = ReadResponse.setSuccess().setData(v2Product).response();
						return readResponse;

					} catch (IllegalArgumentException | ODataException e) {
						logger.error("==> Exception calling backend OData V2 service: " + e.getMessage());
						ErrorResponse errorResponse = ErrorResponse.getBuilder()
								.setMessage("Error error error")
								.setStatusCode(500)
								.setCause(e)
								.response();
						readResponse = ReadResponse.setError(errorResponse);
					}
				}
			}
		}catch(Exception e){
			readResponse = ReadResponse.setError(ErrorResponse.getBuilder().setMessage(e.getMessage()).setStatusCode(500).response());
		}

		return readResponse;
	}
	
	//Implementation of CREATE operation, for Products 
	@Create(entity = "Products", serviceName = "EPMSampleService")
	public CreateResponse createProduct(CreateRequest createRequest) {
		
		logger.debug("==> now call backend OData V2 service");	
		
		CreateResponse createResponse = null;
		ProductEntity prodAsPOJO = createRequest.getDataAs(ProductEntity.class); //getting the request data as a PoJo
		
		try{
			

			// setting the non-nullable properties of the entity ProductSet of the OData V2 data source
			// you can set them via a PoJo
			prodAsPOJO.setSupplierID("0100000000");
			prodAsPOJO.setTaxTarifCode(1);
			prodAsPOJO.setMeasureUnit("EA");
			prodAsPOJO.setCurrencyCode("INR");
			prodAsPOJO.setTypeCode("PR");

			logger.debug("==> now execute create for ProductID: "+ prodAsPOJO.getProductID());
			
			ODataCreateRequest testCreate = ODataCreateRequestBuilder
					.withEntity("/sap/opu/odata/IWBEP/GWSAMPLE_BASIC", "ProductSet")
					.withBodyAs(prodAsPOJO).build(); //passing request body as PoJo

			ODataCreateResult createResult;

			createResult = testCreate.execute(DESTINATION_NAME);
			
			logger.debug("==> After calling backend OData V2 service: result: " + createResult);
			
			Map<String, Object> responseMap = createResult.asMap();
			if (responseMap != null)
				for (Entry<String, Object> e : responseMap.entrySet()) {
					logger.error(e.getKey());
					logger.error(" Value " + e.getValue());
				}
			else
				logger.error("Response for OData V2 Create is null!");
			
			//setting the response data as a PoJo  
			createResponse = CreateResponse.setSuccess()
			                        .setData(createResult.as(ProductEntity.class)).response();
			
			
			// you can also set the response data as a map
			//CreateResponse createResponse = CreateResponse.setSuccess()
			//		.setData(createResult.asMap()).response();
			
			return createResponse;

		} catch (IllegalArgumentException | ODataException  e) {
			logger.error("==> Exception calling backend OData V2 service for Create of a Product: " + e.getMessage());

			ErrorResponse errorResponse = ErrorResponse.getBuilder()
// for setting translatable error messages, message key and the dynamic place holders for parameters are defined in i18n.properties file					
					.setMessage("PRD_CREATE_ERR", prodAsPOJO.getProductID()) 
					.setStatusCode(500)
					.setCause(e)
					.response();
			return CreateResponse.setError(errorResponse);
		}
	}

	//Implementation of UPDATE operation, for Products 
	@Update(entity = "Products", serviceName = "EPMSampleService")
	public UpdateResponse updateProduct(UpdateRequest updateRequest) {
		
		Map<String, Object> requestMap = updateRequest.getMapData();
		
		logger.debug("==> now call backend OData V2 service");	
		
		try {		
			
			// setting the non-nullable properties of the entity ProductSet of the OData V2 data source
			// you can set them via a Map or a PoJo	
					
			requestMap.put("SupplierID", "0100000000");
			requestMap.put("TaxTarifCode", 1);
			requestMap.put("MeasureUnit", "EA");
			requestMap.put("CurrencyCode", "INR");
			requestMap.put("TypeCode", "PR");
	
			logger.debug("==> now execute create for ProductID: "+ updateRequest.getDataAs(ProductEntity.class).getProductID());
			
			ODataUpdateRequest testUpdate = ODataUpdateRequestBuilder
					.withEntity("/sap/opu/odata/IWBEP/GWSAMPLE_BASIC", "ProductSet", updateRequest.getKeys())					
					.withHeader("If-Match", "*") //this is how you can pass headers to the OData V2 data source
					.withBodyAsMap(requestMap) //passing request body as a Map				
					.build();

			ODataUpdateResult result = testUpdate.execute(DESTINATION_NAME);
			
			logger.debug("==> After calling backend OData V2 service");
			
		} catch (IllegalArgumentException | ODataException e) {
			logger.error("==> Exception calling backend OData V2 service for Update of a Product: " + e.getMessage());

			ErrorResponse errorResponse = ErrorResponse.getBuilder()
// for setting translatable error messages, message key and the dynamic place holders for parameters are defined in i18n.properties file					
					.setMessage("PRD_UPDATE_ERR", requestMap.get("ProductID")) 
					.setStatusCode(500)
					.setCause(e)
					.response();
			return UpdateResponse.setError(errorResponse);
		}		

		UpdateResponse updateResponse = UpdateResponse.setSuccess().response();
		return updateResponse;
	}		
	
	//Implementation of DELETE operation, for Products 	
	@Delete(entity = "Products", serviceName = "EPMSampleService")
	public DeleteResponse deleteProd(DeleteRequest deleteRequest) {
		
		logger.debug("==> now call backend OData V2 service");	
	
		try {
			logger.debug("==> now execute delete for ProductID: "+ deleteRequest.getKeys().get("ProductID"));
			
			ODataDeleteRequest testDelete = ODataDeleteRequestBuilder
					.withEntity("/sap/opu/odata/IWBEP/GWSAMPLE_BASIC", "ProductSet", deleteRequest.getKeys())
					.withHeader("If-Match", "*")
					.build();	
			
			ODataDeleteResult result = testDelete.execute("ErpQueryEndpoint");
			logger.debug("==> After calling backend OData V2 service");			
			
		} catch (ODataException e) {
			logger.error("==> Exception calling backend OData V2 service for Update of a Product: " + e.getMessage());

			ErrorResponse errorResponse = ErrorResponse.getBuilder()
// for setting translatable error messages, message key and the dynamic place holders for parameters are defined in i18n.properties file					
					.setMessage("PRD_DELETE_ERR", deleteRequest.getKeys().get("ProductID")) 
					.setStatusCode(500)
					.setCause(e)
					.response();
			return DeleteResponse.setError(errorResponse);
		}
		DeleteResponse deleteResponse = DeleteResponse.setSuccess().response();
		return deleteResponse;
	}		

	// implementation of a function import to get product with given Id
	@Function(Name="FIGetProductWithId" ,serviceName = "EPMSampleService")
	public OperationResponse getProductWithId(OperationRequest functionRequest)
	{
		Map<String,Object> params = functionRequest.getParameters(); 
		String ProductId = params.get("Id").toString();
		Map<String,Object> keys = new HashMap();
		keys.put("ProductID", ProductId);
		OperationResponse functionResponse = null;
		
		logger.debug("==> now call backend OData V2 service");	

		try {

			logger.debug("==> now execute read for ProductID: " + ProductId );

			ODataQueryResult result = ODataQueryBuilder.
					withEntity("/sap/opu/odata/IWBEP/GWSAMPLE_BASIC", "ProductSet")
					.select("ProductID", "Name", "Description", "Category")
					.keys(keys) // this is how you can pass the key predicate for the Read request
					.build()
					.execute(DESTINATION_NAME);

			logger.debug("==> After calling backend OData V2 service: result: " + result);		

			final ProductEntity v2Product = result.as(ProductEntity.class);
			EntityData ed = EntityData.createFrom(v2Product, "Products");
			List<EntityData> filteredProducts = new ArrayList<EntityData>();
			filteredProducts.add(ed);
			functionResponse = OperationResponse.setSuccess().setEntityData(filteredProducts).response();
			return functionResponse;

		} catch (IllegalArgumentException | ODataException e) {
			logger.error("==> Exception calling backend OData V2 service for Read of a Product: " + e.getMessage());
			
// for setting translatable error messages, message key and the dynamic place holders for parameters are defined in i18n.properties file					

			functionResponse = OperationResponse.setError(ErrorResponse.getBuilder()
							// for setting translatable error messages, message key and the dynamic place holders for parameters are defined in i18n.properties file							
							.setMessage("FI_ERR","FIGetProductWithId") 	
							.addErrorDetail("FI_ERR_DETAIL", "FI", ProductId)							
							.setStatusCode(500)														
							.response());
		}
		
		return functionResponse;
	}
	
	// implementation of a function import to get products by given Category
	@Function(Name="FIGetProductsByCategory" ,serviceName = "EPMSampleService")
	public OperationResponse getProductsByCategory(OperationRequest functionRequest)
	{
		Map<String,Object> params = functionRequest.getParameters(); 
		String Category = params.get("Category").toString();
		OperationResponse functionResponse = null;		
		
		logger.debug("==> now call backend OData V2 service");	

		try {

			logger.debug("==> now execute query on Products for Category: " + Category );
			
			//this is how you can create a filter expression to pass to the OData V2 query API
			FilterExpression filter = new FilterExpression("Category", "eq", ODataType.of(Category));			

			ODataQueryResult result = ODataQueryBuilder.
					withEntity("/sap/opu/odata/IWBEP/GWSAMPLE_BASIC", "ProductSet")
					.select("ProductID", "Name", "Description", "Category")
					.filter(filter)
					.build()
					.execute(DESTINATION_NAME);

			logger.debug("==> After calling backend OData V2 service: result: " + result);		

			final List<ProductEntity> v2ProductList = result.asList(ProductEntity.class);
			
// fetch each product and put into EntityData List
			List<EntityData> filteredProducts = new ArrayList<EntityData>();
			
			for (ProductEntity v2Product : v2ProductList){
			
			EntityData ed = EntityData.createFrom(v2Product, "Products");
			
			filteredProducts.add(ed);
			}
			
			functionResponse = OperationResponse.setSuccess().setEntityData(filteredProducts).response();
			return functionResponse;

		} catch (IllegalArgumentException | ODataException e) {
			logger.error("==> Exception calling backend OData V2 service for Read of a Product: " + e.getMessage());
			
// for setting translatable error messages, message key and the dynamic place holders for parameters are defined in i18n.properties file					

			functionResponse = OperationResponse.setError(ErrorResponse.getBuilder()
							// for setting translatable error messages, message key and the dynamic place holders for parameters are defined in i18n.properties file							
							.setMessage("FI_ERR","FIGetProductsByCategory") 	
							.addErrorDetail("FI_ERR_DETAIL2", "FI", Category)							
							.setStatusCode(500)														
							.response());
		}
		
		return functionResponse;
	}
	

	@Query(entity = "SalesOrders", serviceName = "EPMSampleService")
	public QueryResponse getAllSalesOrders(QueryRequest queryRequest) {
		QueryResponse queryResponse = null;
		try{
			queryResponse =  QueryResponse.setSuccess().setEntityData(getEntitySet(queryRequest)).response();
		}catch(Exception e){
			queryResponse =  QueryResponse.setError(ErrorResponse.getBuilder()
					.setMessage("SO_QUERY_ERR")
					.setStatusCode(500)
					.response());
		}
		return queryResponse;
	}

	@Create(entity = "SalesOrders", serviceName = "EPMSampleService")
	public CreateResponse createSalesOrder(CreateRequest createRequest) {
		return createSalesOrderEntity(createRequest);
	}

	@Read(entity = "SalesOrders", serviceName = "EPMSampleService")
	public ReadResponse getSalesOrder(ReadRequest readRequest) {
		ReadResponse readResponse = null;
		try{
			readResponse = ReadResponse.setSuccess().setData(readEntity(readRequest)).response();
		}catch (Exception e){
			readResponse = ReadResponse.setError(ErrorResponse.getBuilder()
					.setMessage("SO_READ_ERR", readRequest.getKeys().get("SalesOrderID"))
					.setStatusCode(500)
					.response());
		}
		return readResponse;
	}
	@Update(entity = "SalesOrders", serviceName = "EPMSampleService")
	public UpdateResponse updateSalesOrder(UpdateRequest updateRequest) {
		UpdateResponse updateResponse  = null;
		try{
			updateEntity(updateRequest);
			updateResponse = UpdateResponse.setSuccess().response();
		}catch (Exception e){
			updateResponse = UpdateResponse.setError(ErrorResponse.getBuilder()
					.setMessage("SO_UPDATE_ERR", updateRequest.getMapData().get("SalesOrderID"))
					.setStatusCode(500)
					.response());
		}
		return updateResponse;
	}
	@Delete(entity = "SalesOrders", serviceName = "EPMSampleService")
	public DeleteResponse deleteSalesOrder(DeleteRequest deleteRequest) {
		DeleteResponse deleteResponse = null;
		try{
			deleteEntity(deleteRequest);
			deleteResponse = DeleteResponse.setSuccess().response();
		}catch(Exception e){
			deleteResponse = DeleteResponse.setError(ErrorResponse.getBuilder()
					.setMessage("SO_DELETE_ERR", deleteRequest.getKeys().get("SalesOrderID"))
					.setStatusCode(500).response());
		}
		return deleteResponse;
	}
	
	// implementation of a action import to change Note of the given Sales Order
	@Action(Name="AIChangeSalesOrderNote" ,serviceName = "EPMSampleService")
	public OperationResponse changeSONote(OperationRequest actionRequest)
	{
		Map<String,Object> params=actionRequest.getParameters();
		OperationResponse actionResponse = null;
        String Note = (String) params.get("Note");
        String Id = (String) params.get("Id");		
		try {
		    if(params.get("Note")!= null && params.get("Id")!= null) {
		        // this is how you can add messages in container 
			    actionRequest.getMessageContainer().addInfoMessage("ACTION_IMPORT_INFO", "AI", Id);		        
		        changeSONote(Note, Id);  		     
		    }
		    // this is how you can make the additional messages from message container show up as details in sap-message header
		    actionRequest.getMessageContainer().setLeadingMessage("ACTION_IMPORT_SUCCESS", "AI");
		    actionResponse = OperationResponse.setSuccess().response();
		} catch (Exception e) {
			actionResponse = OperationResponse
							.setError(ErrorResponse.getBuilder()
							.setMessage("ACTION_IMPORT_ERROR")
							.setStatusCode(500)
							.addErrorDetail("ACTION_IMPORT_INFO", "AI", Id)
							.setCause(e)
							.response());
		}

		return actionResponse;

	}	
	@Query(entity = "SalesOrderLineItems", serviceName = "EPMSampleService")
	public QueryResponse getAllSOLineItems(QueryRequest queryRequest) {
		QueryResponse queryResponse = null;
		try{
			queryResponse =  QueryResponse.setSuccess().setEntityData(getEntitySet(queryRequest)).response();
		}catch(Exception e){
			queryResponse =  QueryResponse.setError(ErrorResponse.getBuilder().setMessage(e.getMessage()).setStatusCode(500).response());
		}
		return queryResponse;
	} 
	@Query(entity = "SalesOrderLineItems", serviceName = "EPMSampleService", sourceEntity = "SalesOrders")
	public QueryResponse getSOLineItemsForSO(QueryRequest queryRequest) {
		QueryResponse queryResponse = null;
		EntityData SOEntity;
		try{
			String sourceEntityName = queryRequest.getSourceEntityName();

			//Read SalesOrders to check if the passed SalesOrderID exists
			if ( sourceEntityName.equals("SalesOrders")) {

				SOEntity = readSalesOrder(queryRequest.getSourceKeys());


				if (SOEntity == null) {
					ErrorResponse errorResponse = ErrorResponse.getBuilder()
							.setMessage("Parent SalesOrder does not exist")
							.setStatusCode(401)
							.response();
					queryResponse = QueryResponse.setError(errorResponse);
				} 
				else {
					queryResponse =  QueryResponse.setSuccess().setEntityData(getSOItemsForSO(queryRequest.getSourceKeys())).response();
				}
			}
		}catch(Exception e){
			logger.error("==> Exception fetching SOItems for a SO from CDS: " + e.getMessage());
			queryResponse = QueryResponse.setError(ErrorResponse.getBuilder().setMessage(e.getMessage()).setCause(e).response());
		}

		return queryResponse;
	}    

	@Read(entity = "SalesOrderLineItems", serviceName = "EPMSampleService")
	public ReadResponse getSOItem(ReadRequest readRequest) {
		ReadResponse readResponse = null;
		try{
			readResponse = ReadResponse.setSuccess().setData(readEntity(readRequest)).response();
		}catch(Exception e){
			readResponse = ReadResponse.setError(ErrorResponse.getBuilder().setMessage(e.getMessage()).setStatusCode(500).response());
		}
		return readResponse;
	}
	@Create(entity = "SalesOrderLineItems", serviceName = "EPMSampleService")
	public CreateResponse createSalesOrderLineItems(CreateRequest createRequest) {
		CreateResponse createResponse = null;
		try{
			createResponse = CreateResponse.setSuccess().setData(createEntity( createRequest)).response();
		}catch(Exception e){
			createResponse = CreateResponse.setError(ErrorResponse.getBuilder().setMessage(e.getMessage()).setStatusCode(500).response());
		}
		return createResponse;
	}
	@Create(entity = "SalesOrderLineItems", serviceName = "EPMSampleService", sourceEntity = "SalesOrders")
	public CreateResponse createSalesOrderLineItemForSalesOrder(CreateRequest createRequest) {

		CreateResponse createResponse = null;				
		EntityData SOEntity;
		try{
			String sourceEntityName = createRequest.getSourceEntityName();

			//Read SalesOrders to check if the passed SalesOrderID exists
			if ( sourceEntityName.equals("SalesOrders")) {

				SOEntity = readSalesOrder(createRequest.getSourceKeys());


				if (SOEntity == null) {
					ErrorResponse errorResponse = ErrorResponse.getBuilder()
							.setMessage("Parent SalesOrder does not exist")
							.setStatusCode(401)
							.response();
					createResponse = CreateResponse.setError(errorResponse);
				} 
				else {
					// you can do further validation to check that the payload data contains the SalesOrderID same as that in the URL
					// for that you can use the createRequest.getData() and further find the specific property's value			

					createResponse = CreateResponse.setSuccess().setData(createEntity(createRequest)).response();

				}}
		}catch(Exception e){
			logger.error("==> Exception while creating a SOItem for a SO in CDS: " + e.getMessage());
			createResponse = CreateResponse.setError(ErrorResponse.getBuilder().setMessage(e.getMessage()).setCause(e).response());
		}
		return createResponse;
	}

	private EntityData createEntity(CreateRequest createRequest)throws Exception
	{    
		CDSDataSourceHandler dsHandler = DataSourceHandlerFactory.getInstance().getCDSHandler(getConnection(), createRequest.getEntityMetadata().getNamespace());
		EntityData ed = null;
		try{
			ed = dsHandler.executeInsert(createRequest.getData(), true);
		}catch(CDSException e){
			logger.error("Exception while creating an entity in CDS: "+e.getMessage());
			throw e;
		}
		return ed;
	}

	private CreateResponse createSalesOrderEntity(CreateRequest createRequest)
	{   
		EntityData ed = null;
		try{
			CDSDataSourceHandler dsHandler = DataSourceHandlerFactory.getInstance().getCDSHandler(getConnection(), createRequest.getEntityMetadata().getNamespace());
			ed = dsHandler.executeInsert(createRequest.getData(), true);
		}catch(CDSException e){
			logger.error("Exception while creating a Sales Order entity in CDS: "+e.getMessage());
			ErrorResponse errorResponse = null;
			
			if(e.getType().equals(DatasourceExceptionType.INTEGRITY_CONSTRAINT_VIOLATION)){

				// this is how you can add messages to container
				createRequest.getMessageContainer().addErrorMessage("INTEGRITY_CONSTRAINT_ERROR", "CDS", createRequest.getMapData().get("SalesOrderID"));
				
				errorResponse = ErrorResponse.getBuilder()
								.setMessage("SO_CREATE_ERR", createRequest.getMapData().get("SalesOrderID"))
								.setStatusCode(500)
								.addContainerMessages(Severity.ERROR) //the container messages will show up as details in the error response 
								.response();
			}else if(e.getType().equals(DatasourceExceptionType.DATABASE_CONNECTION_ERROR)){
				
				
				errorResponse = ErrorResponse.getBuilder()
								.setMessage("SO_CREATE_ERR", createRequest.getMapData().get("SalesOrderID"))
								.setStatusCode(500)
								.addErrorDetail("DATABASE_CONNECTION_ERROR", "CDS") //another way to add error detail in the error response
								.response();
			}else{
				errorResponse = ErrorResponse.getBuilder()
								.setStatusCode(500)
								.setMessage("SO_CREATE_ERR", createRequest.getMapData().get("SalesOrderID") ).response();
			}
			return CreateResponse.setError(errorResponse);
		}catch(Exception e){
			logger.error("==> Exception while creating a SO in CDS: " + e.getMessage());
			return CreateResponse.setError(ErrorResponse.getBuilder()
					.setMessage("SO_CREATE_ERR", createRequest.getMapData().get("SalesOrderID"))
					.setCause(e)
					.response());
		}
		// this is how you can provide additional information messages as part of sap-message response header in case of success		
		createRequest.getMessageContainer().addInfoMessage("SO_CREATE_INFO","CDS",createRequest.getMapData().get("SalesOrderID"));
		createRequest.getMessageContainer().setLeadingMessage("SO_CREATE_SUCCESS", "CDS");
		return CreateResponse.setSuccess().setData(ed).response();
	}

	private void updateEntity(UpdateRequest updateRequest) throws Exception{
		CDSDataSourceHandler dsHandler = DataSourceHandlerFactory.getInstance().getCDSHandler(getConnection(), updateRequest.getEntityMetadata().getNamespace());
		try{
			dsHandler.executeUpdate(updateRequest.getData(), updateRequest.getKeys(), false);
		}catch(CDSException e){
			logger.error("Exception while updating an entity in CDS: " + e.getMessage());
			throw e;
		}
	}
	
	private void deleteEntity(DeleteRequest deleteRequest) throws Exception{
		CDSDataSourceHandler dsHandler = DataSourceHandlerFactory.getInstance().getCDSHandler(getConnection(), deleteRequest.getEntityMetadata().getNamespace());
		try{
			dsHandler.executeDelete(deleteRequest.getEntityMetadata().getName(), deleteRequest.getKeys());
		}catch(CDSException e){
			logger.error("Exception while deleting an entity in CDS: "+e.getMessage());
			throw e;
		}
	}
	private EntityData readEntity(ReadRequest readRequest)throws Exception {
		CDSDataSourceHandler dsHandler = DataSourceHandlerFactory.getInstance().getCDSHandler(getConnection(), readRequest.getEntityMetadata().getNamespace());
		EntityData ed = null;
		try{
			ed = dsHandler.executeRead(readRequest.getEntityMetadata().getName(), readRequest.getKeys(), readRequest.getEntityMetadata().getElementNames());
		}catch(CDSException e){
			logger.error("Exception while reading an entity in CDS: "+e.getMessage());
			throw e;
		}
		return ed;
	}
	private List<EntityData> getEntitySet(QueryRequest queryRequest) throws Exception{
		String fullQualifiedName = queryRequest.getEntityMetadata().getNamespace()+"."+queryRequest.getEntityMetadata().getName();
		CDSDataSourceHandler dsHandler = DataSourceHandlerFactory.getInstance().getCDSHandler(getConnection(), queryRequest.getEntityMetadata().getNamespace());
		try {
			CDSQuery cdsQuery = new CDSSelectQueryBuilder(fullQualifiedName).orderBy("SalesOrderID", false).build();                    			
			CDSSelectQueryResult cdsSelectQueryResult = dsHandler.executeQuery(cdsQuery);
			return cdsSelectQueryResult.getResult();
		} catch (CDSException e) {
			logger.error("==> Exception while fetching query data from CDS: " + e.getMessage());
			throw e;
		}
	}
	
	private List<EntityData> getSOItemsForSO(Map<String,Object> SalesOrderID) throws Exception{
		String fullQualifiedName = "EPMSample.SalesOrderLineItems";
		CDSDataSourceHandler dsHandler = DataSourceHandlerFactory.getInstance().getCDSHandler(getConnection(), "EPMSample");
		try {
			CDSQuery cdsQuery = new CDSSelectQueryBuilder(fullQualifiedName).where(new ConditionBuilder().columnName("SalesOrderID").EQ(SalesOrderID.get("SalesOrderID").toString()).build())
					.orderBy("SOLineItemID", false).build();
			//.orderBy(queryRequest.getOrderByProperties().iterator().next().toString(), false).build();
			CDSSelectQueryResult cdsSelectQueryResult = dsHandler.executeQuery(cdsQuery);
			return cdsSelectQueryResult.getResult();
		} catch (CDSException e) {
			logger.error("Exception while reading SalesOrder Item in CDS: "+e.getMessage());
			throw e;
		}
		
	}

	private EntityData readSalesOrder(Map<String,Object> SalesOrderID) throws Exception{
		CDSDataSourceHandler dsHandler = DataSourceHandlerFactory.getInstance().getCDSHandler(getConnection(), "EPMSample");
		List <String> properties = Arrays.asList("SalesOrderID", "Note", "GrossAmount", "CustomerName");
		EntityData ed = null;
		try{
			ed = dsHandler.executeRead("SalesOrders", SalesOrderID, properties);
		}catch(CDSException e){
			logger.error("Exception while reading SalesOrder entity in CDS: "+e.getMessage());
			throw e;
		}
		return ed;
	}
	private EntityData readSalesOrderLineItem(Map<String,Object> SalesOrderLineItemID) throws Exception{
		CDSDataSourceHandler dsHandler = DataSourceHandlerFactory.getInstance().getCDSHandler(getConnection(), "EPMSample");
		List <String> properties = Arrays.asList("ProductID");
		EntityData ed = null;
		try{
			ed = dsHandler.executeRead("SalesOrderLineItems", SalesOrderLineItemID, properties);
		}catch(CDSException e){
			logger.error("Exception while reading SalesOrderLineItems entity in CDS: "+e.getMessage());
			throw e;
		}
		return ed;
	}	
	
	private void changeSONote(String Note, String Id) throws Exception {
	
		CDSDataSourceHandler dsHandler = DataSourceHandlerFactory.getInstance().getCDSHandler(getConnection(), "EPMSample");
		try{
			// first do a read of the Sales Order for the given Id.
			Map<String,Object> key = new HashMap<String,Object>();
			key.put("SalesOrderID", Id);		
			EntityData ed = readSalesOrder(key);
			logger.error("Sales Order before note update from Entity Data: Gross Amount: "+ed.getElementValue("GrossAmount") + ", Note: "+ ed.getElementValue("Note") + ", CustomerName: "+ ed.getElementValue("CustomerName"));
			
			SalesOrder so = ed.as(SalesOrder.class);
			// change the Note property coming from the parameter
			
			logger.error("Sales Order before note update form PoJo: Gross Amount: "+so.getGrossAmount() + ", Note: "+ so.getNote() + ", CustomerName: "+ so.getCustomerName());
			
			so.setNote(Note); // update the Note with corresponding incoming parameter value of Action Import call	
			ed = EntityData.createFrom(so, "SalesOrders"); // populate the EntityData with changed Note and pass to CDS Update API			
			
			dsHandler.executeUpdate(ed, key, false);
		}catch(CDSException e){
			logger.error("Exception while updating the Note for Sales Order with ID " + Id + ": " + e.getMessage());
			throw e;
		}
	}	

	private static Connection getConnection() throws SQLException ,NamingException{
		Connection conn = null;
		Context ctx;
		try {
			ctx = new InitialContext();
			conn = ((DataSource) ctx.lookup("java:comp/env/jdbc/java-hdi-container")).getConnection();
		} catch (SQLException | NamingException e) {
			e.printStackTrace();
			throw e;
		}
		return conn;
	}
}
