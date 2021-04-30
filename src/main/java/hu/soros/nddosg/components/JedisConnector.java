package hu.soros.nddosg.components;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

@Component
public class JedisConnector {

	@Value("${value.name}")
	String valueName;

	private static final Logger LOGGER = LoggerFactory.getLogger(JedisConnector.class);

	public static Jedis getConnection() {
		try {

			String rUrl = System.getenv("REDIS_URL");
			rUrl = rUrl == null ? System.getenv("REDIS_LOCAL_URL") : rUrl;
			if(rUrl == null) {
				throw new IllegalStateException("Value name is not present in connector.");
			}
			TrustManager bogusTrustManager = new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			};

			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] { bogusTrustManager }, new java.security.SecureRandom());

			HostnameVerifier bogusHostnameVerifier = (hostname, session) -> true;

			return new Jedis(URI.create(rUrl), sslContext.getSocketFactory(), sslContext.getDefaultSSLParameters(),
					bogusHostnameVerifier);

		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException("Cannot obtain Redis connection!", e);
		}
	}

	@PostConstruct
	public void init() {
		LOGGER.trace("Init...");
		try {
			Jedis jedis = JedisConnector.getConnection();
			jedis.select(0);
			String cachedResponse = jedis.get(valueName);
			LOGGER.trace("cachedResponse: bn", cachedResponse);
			if (cachedResponse == null) {
				LOGGER.trace("Cached response not found. Setting to 0.");
				jedis.set(valueName, "0");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
