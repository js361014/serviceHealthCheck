package service.health.check.front.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import service.health.check.models.Config;

import static org.junit.Assert.assertEquals;

public class ConfigControllerTest extends AbstractTest {

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void getConfig() throws Exception {
		for(Config.ConfigName configName : Config.ConfigName.values()) {
			String uri = "/config/" + configName;
			MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
					.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
			int status = mvcResult.getResponse().getStatus();
			assertEquals(200, status);
			String content = mvcResult.getResponse().getContentAsString();
			Config config = super.mapFromJson(content, Config.class);
			assertEquals(config.getName(), configName);
			assertEquals(config.getValue(), Config.defaultConfigsValues.get(configName));
		}
	}
}
