package service.health.check.front.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import service.health.check.models.Address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.beanutils.PropertyUtils;

public class AddressControllerTest extends AbstractTest {

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void createAddress() throws Exception {
		String uri = "/addresses";
		Address address = new Address();
		address.setHost("https://www.google.com/");
		address.setPort("443");
		address.setFirstAdmin("egrpreyd@sharklasers.com");
		address.setSecondAdmin("egrpreyd@sharklasers.com");
		address.setSendNotificationAfter(10);
		address.setResendNotificationAfter(60);
		String inputJson = super.mapToJson(address);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(inputJson)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();
		Address addedAddress = super.mapFromJson(content, Address.class);
		assertEquals(addedAddress.getHost(), address.getHost());
		assertEquals(addedAddress.getPort(), address.getPort());
		assertEquals(addedAddress.getFirstAdmin(), address.getFirstAdmin());
		assertEquals(addedAddress.getSecondAdmin(), address.getSecondAdmin());
		assertEquals(addedAddress.getSecondNotificationSent(), address.getSecondNotificationSent());
		assertEquals(addedAddress.getResendNotificationAfter(), address.getResendNotificationAfter());
		assertNotNull(addedAddress.getId());
	}

	@Test
	public void createAddressWithoutNotNullProperty() throws Exception {
		String uri = "/addresses";
		Field[] attributes = Address.class.getDeclaredFields();
		for (Field attribute : attributes) {
			Annotation[] annotations = attribute.getDeclaredAnnotations();
			boolean isNotNull = false;
			for (Annotation annotation : annotations) {
				if (annotation.annotationType()
						.getName()
						.equals("javax.validation.constraints.NotNull")) {
					isNotNull = true;
				}
			}
			if (isNotNull) {
				Address address = new Address();
				address.setHost("https://www.google.com/");
				address.setPort("443");
				address.setFirstAdmin("egrpreyd@sharklasers.com");
				address.setSecondAdmin("egrpreyd@sharklasers.com");
				address.setSendNotificationAfter(10);
				address.setResendNotificationAfter(60);
				PropertyUtils.setSimpleProperty(address, attribute.getName(), null);
				String inputJson = super.mapToJson(address);
				MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(inputJson)).andReturn();

				int status = mvcResult.getResponse().getStatus();
				assertEquals(400, status);
			}
		}
	}

}
