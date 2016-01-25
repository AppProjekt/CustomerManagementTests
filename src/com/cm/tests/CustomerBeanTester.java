package com.cm.tests;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.junit.Before;
import org.junit.Test;

import com.cm.ejb.interfaces.CustomerDAO;
import com.cm.persistence.entities.Address;
import com.cm.persistence.entities.Communication;
import com.cm.persistence.entities.Customer;
import com.cm.persistence.enums.CommunicationType;
import com.cm.persistence.enums.Gender;
import com.cm.persistence.enums.Kind;

public class CustomerBeanTester {
	
	private CustomerDAO customerDAO;

	@Before
	public void setUp() throws Exception {

		try {
	        final Hashtable<String, Comparable> jndiProperties = 
				new Hashtable<String, Comparable>();

	        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, 
					"org.jboss.naming.remote.client.InitialContextFactory");
	        
		    jndiProperties.put("jboss.naming.client.ejb.context", true);
		    
		    jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
	        jndiProperties.put(Context.SECURITY_PRINCIPAL, "admin");
	        jndiProperties.put(Context.SECURITY_CREDENTIALS, "sicher");
	        
	        final Context context = new InitialContext(jndiProperties);
	        
	        final String lookupName = 
					"CustomerManagement/CustomerManagementEJB/CustomerBean!com.cm.ejb.interfaces.CustomerDAO";
	        
	        customerDAO = (CustomerDAO) context.lookup(lookupName);
	        
		} catch (Exception ex) {
			throw ex;
		}
	}
	
	@Test
	public void test() {
		assertNotNull(customerDAO);
		
		// Customer anlegen
		Customer customer = new Customer();
		customer.setFirstName("Karsten");
		customer.setLastName("Samaschke");
		customer.setGender(Gender.Male);
		
		Calendar birthday = Calendar.getInstance();
		birthday.set(Calendar.DAY_OF_MONTH, 1);
		birthday.set(Calendar.MONTH, 1);
		birthday.set(Calendar.YEAR, 1975);
		customer.setBirthday(birthday.getTime());
		
		// Adresse definieren
		Address address = new Address();
		address.setCity("Berlin");
		address.setCountry("Germany");
		address.setStreet("Warschauer Strasse 58a");
		address.setZip("10243");
		address.setKind(Kind.Business);
		customer.getAddresses().add(address);
		
		// E-Mail-Adresse definieren
		Communication communication = new Communication();
		communication.setName("Geschäft");
		communication.setValue("info@samaschke.de");
		communication.setCommunicationType(CommunicationType.Email);
		communication.setKind(Kind.Business);
		customer.getCommunications().add(communication);
		
		Customer result = customerDAO.create(customer);
		assertNotEquals(result.getId(), 0);
		
		// Kunden erneut abrufen
		//result = customerDAO.getCustomer(result.getId());
				
		// Adresse uberprufen
		assertTrue(result.getAddresses().size() == 1);
		Address addressFromServer = result.getAddresses().get(0);
		assertEquals(address.getStreet(), addressFromServer.getStreet());
				
		// E-Mail ueberprufen
		assertTrue(result.getCommunications().size() == 1);
		Communication communicationFromServer = result.getCommunications().get(0);
		assertEquals(communication.getValue(), communicationFromServer.getValue());	
	}
}
