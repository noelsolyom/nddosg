package hu.soros.nddosg.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import hu.soros.nddosg.components.JedisConnector;
import hu.soros.nddosg.dto.MainCounterDto;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RequestService implements BeanNameAware, ApplicationContextAware {

	private String beanName;
	private ApplicationContext context;

	@Value("${value.name}")
	private String valueName;

	@Autowired
	@Qualifier("brokerMessagingTemplate")
	private MessageSendingOperations<String> brokerMessagingTemplate;

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestService.class);

	public String getRequest() throws Exception {
		JedisPool jedisPool = JedisConnector.getPool();
		try (Jedis jedis = jedisPool.getResource()) {
			if (valueName == null) {
				throw new IllegalStateException("Value name is not present in service.");
			}
			jedis.select(0);
			String data = null;
			String cachedResponse = jedis.get(valueName);
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
			jedis.close();
			jedis.quit();
			jedisPool.destroy();
			MainCounterDto result = new MainCounterDto(data);
			((RequestService) this.context.getBean(beanName)).publishCurrentMainCounterResult(result);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			jedisPool.destroy();
			throw e;
		}

	}

	@Async
	public void publishCurrentMainCounterResult(MainCounterDto result) throws Exception {
		brokerMessagingTemplate.convertAndSend("/topic/mainCounter", result);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

}
