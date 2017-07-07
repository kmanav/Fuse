package com.redhat.training.jb421;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.training.jb421.model.Address;
import com.redhat.training.jb421.model.CatalogItem;
import com.redhat.training.jb421.model.Customer;
import com.redhat.training.jb421.model.Order;
import com.redhat.training.jb421.model.OrderItem;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/bundle-camel-context.xml" })
@UseAdviceWith(true)
public class RestOrderRouteTest {

	@Autowired
	public CamelContext camelContext;

	private EntityManagerFactory emf;

	private EntityManager em;
	
	private Order testOrder;
	private List<OrderItem> testItems;
	private Customer testCustomer;
	private Address testAddress;

	@PersistenceUnit
	public void setEntityManagerFactory(EntityManagerFactory emf) {
		this.emf = emf;
	}

	@Before
	public void setup() {
		em = emf.createEntityManager();
		createTestData();
	}

	@Test
	@DirtiesContext
	public void testShipAddress() throws Exception {
		camelContext.start();
		Thread.sleep(3000);

		//Get shipping address for test order
		String result = getRESTData("shipAddress", testOrder.getId());
		
		ObjectMapper om = new ObjectMapper();
		Address returned = om.readValue(result, Address.class);
		assertEquals(testAddress,returned);
	}
	
	@Test
	@DirtiesContext
	public void testOrderTotal() throws Exception {
		camelContext.start();
		Thread.sleep(3000);

		//Get order total for test order
		String result = getRESTData("orderTotal", testOrder.getId());
		
		BigDecimal decimalResult = new BigDecimal(result);
		BigDecimal expectedTotal = new BigDecimal(0.00);
		for(OrderItem item : testItems){
			expectedTotal = expectedTotal.add(item.getExtPrice().multiply(new BigDecimal(item.getQuantity()))).setScale(2, RoundingMode.DOWN);
		}
		assertEquals(decimalResult, expectedTotal);
	}
	
	@Test
	@DirtiesContext
	public void testOrderItems() throws Exception {
		camelContext.start();
		Thread.sleep(3000);

		//Get order items for test order
		String result = getRESTData("itemList", testOrder.getId());
		
		ObjectMapper om = new ObjectMapper();
		Set<OrderItem> items = om.readValue(result, new TypeReference<Set<OrderItem>>(){});
		assertEquals(testOrder.getOrderItems(), items);
		
	}
	
	@After
	public void cleanup(){
		removeTestData();
		em.close();
	}

	private String getRESTData(String methodName,Integer orderId) throws Exception{
		StringBuilder result = new StringBuilder();
		URL url = new URL("http://localhost:8080/orders/"+methodName+"/" + orderId);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
	}
	
	private void removeTestData(){
		em.getTransaction().begin();
		em.remove(testOrder);
		em.remove(testCustomer);
		em.remove(testAddress);
		em.getTransaction().commit();
	}
	
	private void createTestData(){
		em.getTransaction().begin();
		testAddress = new Address(150,"100 E Davie Street", "", "", "Raleigh", "NC", "27601", "USA");
		em.persist(testAddress);
		testCustomer = new Customer(150,"User","Name","user01","changeme","user01@example.com");
		testCustomer.setBillingAddress(testAddress);
		testCustomer.setShippingAddress(testAddress);
		em.persist(testCustomer);
		testOrder = new Order();
		testOrder.setId(150);
		testOrder.setCustomer(testCustomer);
		testItems = new ArrayList<OrderItem>();
		OrderItem testItem = new OrderItem();
		OrderItem testItem2 = new OrderItem();
		testItem.setId(150);
		testItem.setExtPrice(new BigDecimal(4.99).setScale(2, RoundingMode.DOWN));
		testItem.setQuantity(2);
		testItem2.setId(151);
		testItem2.setExtPrice(new BigDecimal(6.99).setScale(2, RoundingMode.DOWN));
		testItem2.setQuantity(3);
		testItems.add(testItem);
		testItems.add(testItem2);
		testOrder.getOrderItems().addAll(testItems);
		em.persist(testOrder);
		em.persist(testItem);
		em.flush();
		em.getTransaction().commit();
	}


}
