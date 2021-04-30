package hu.soros.nddosg.service;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Service;

import hu.soros.nddosg.components.JedisConnector;
import hu.soros.nddosg.dto.MainCounterDto;
import redis.clients.jedis.Jedis;

@Service
public class RequestService {

	@Value("${value.name}")
	private String valueName;

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestService.class);

	public MainCounterDto getRequest(HttpServletRequest request) throws Exception {

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
				jedis.close();
			}
			return new MainCounterDto(data);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public String findMainCounter() {
		LOGGER.trace("Getting main counter data...");
		Jedis jedis = JedisConnector.getConnection();
		jedis.select(0);
		String value = jedis.get(valueName);
		jedis.close();
		return value;
	}
}
