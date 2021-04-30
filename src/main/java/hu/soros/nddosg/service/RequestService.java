package hu.soros.nddosg.service;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Service;

import hu.soros.nddosg.components.JedisConnector;
import hu.soros.nddosg.dto.MainCounterDto;
import redis.clients.jedis.Jedis;

@Service
public class RequestService {

	@Value("${value.name}")
	private String valueName;

	@Autowired
	private JedisConnector jedisConnector;

	@Autowired
	@Qualifier("brokerMessagingTemplate")
	private MessageSendingOperations<String> brokerMessagingTemplate;

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestService.class);

	public void getRequest(HttpServletRequest request) throws Exception {

		try {
			if(valueName == null) {
				throw new IllegalStateException("Value name is not present in service.");
			}
			Jedis jedis = JedisConnector.getConnection();
			jedis.select(0);
			String data = null;
			String cachedResponse = findMainCounter();
			LOGGER.trace("cachedResponse: {}", cachedResponse);
			if (cachedResponse != null) {
				data = Long.toString(Long.parseLong(cachedResponse) + 1);
				LOGGER.trace("data: {}", data);
				jedis.set(valueName, data);
			} else {
				LOGGER.error("Runtime cachedResponse is null.");
				data = "1";
				jedis.set(valueName, data);
			}
			publishCurrentMainCounterResult(data);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public void publishCurrentMainCounterResult(String data) {
		LOGGER.trace("Publishing...");
		MainCounterDto result = new MainCounterDto();
		result.setData(data);
		brokerMessagingTemplate.convertAndSend("/topic/mainCounter", result);
	}

	public String findMainCounter() {
		LOGGER.trace("Getting main counter data...");
		Jedis jedis = JedisConnector.getConnection();
		jedis.select(0);
		return jedis.get(valueName);
	}
}
