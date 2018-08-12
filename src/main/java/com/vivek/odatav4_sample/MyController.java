package com.vivek.odatav4_sample;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQuery;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQueryBuilder;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQueryResult;
import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;

import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.salesorder.SalesOrder;

@RestController
public class MyController{
	
	@GetMapping("/")
	public String sayHello() {
		return "Hello World!!";
	}
	
	
	@GetMapping("/salesorders")
	public List<SalesOrder> getSalesOrder() throws ODataException{
		String destinationName = "ErpQueryEndpoint";
		final ErpConfigContext configContext = new ErpConfigContext(destinationName); 
		

		ODataQuery query= ODataQueryBuilder
		        .withEntity("/s4hanacloud/sap/opu/odata/sap/API_SALES_ORDER_SRV",
		                "A_SalesOrder")
		        .withHeader("apikey", "7PGAIoLeZfJzcTX1Dyt9ZpEu6bHxm0Ch", true)
		        
		        .select(SalesOrder.SALES_ORDER.getFieldName(), 
		        		SalesOrder.SALES_ORDER_TYPE.getFieldName(),
		        		SalesOrder.SALES_ORGANIZATION.getFieldName(),
		        		SalesOrder.TOTAL_NET_AMOUNT.getFieldName(),
		        		SalesOrder.REQUESTED_DELIVERY_DATE.getFieldName()
		        		)
		        .top(10)
		        .build();
		
		ODataQueryResult result = query.execute(configContext);
         
		final List<SalesOrder> v4SalesOrders = result.asList(SalesOrder.class);
				
		return v4SalesOrders;
	}
}