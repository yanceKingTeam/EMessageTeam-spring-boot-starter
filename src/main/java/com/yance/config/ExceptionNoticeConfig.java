package com.yance.config;

import java.time.Duration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.yance.exceptionhandle.ExceptionHandler;
import com.yance.httpclient.DefaultDingdingHttpClient;
import com.yance.httpclient.DingdingHttpClient;
import com.yance.properties.ExceptionNoticeFrequencyStrategy;
import com.yance.properties.ExceptionNoticeProperty;
import com.yance.text.ExceptionNoticeResolverFactory;

@Configuration
@EnableConfigurationProperties({ ExceptionNoticeProperty.class, ExceptionNoticeFrequencyStrategy.class })
@ConditionalOnProperty(name = "exceptionnotice.open-notice", havingValue = "true", matchIfMissing = true)
@EnableScheduling
public class ExceptionNoticeConfig {

	@Autowired
	private ExceptionNoticeProperty exceptionNoticeProperty;

	@Autowired
	private ExceptionNoticeFrequencyStrategy exceptionNoticeFrequencyStrategy;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	private final Log logger = LogFactory.getLog(ExceptionNoticeConfig.class);

	@Bean
	@ConditionalOnMissingBean
	public ExceptionNoticeResolverFactory exceptionNoticeResolverFactory() {
		logger.debug("创建resolverFactory");
		ExceptionNoticeResolverFactory exceptionNoticeResolverFactory = new ExceptionNoticeResolverFactory();
		return exceptionNoticeResolverFactory;
	}

	@Bean
	@ConditionalOnMissingBean
	public ExceptionHandler exceptionHandler(DingdingHttpClient httpClient,
			ExceptionNoticeResolverFactory exceptionNoticeResolverFactory) {
		logger.debug("创建exceptionHandler");
		ExceptionHandler exceptionHandler = new ExceptionHandler(exceptionNoticeProperty,
				exceptionNoticeFrequencyStrategy);
		return exceptionHandler;
	}

	@Bean
	@ConditionalOnMissingBean
	public DingdingHttpClient dingdingHttpClient() {
		RestTemplate restTemplate = restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(20)).build();
		DingdingHttpClient dingdingHttpClient = new DefaultDingdingHttpClient(restTemplate);
		return dingdingHttpClient;
	}
}
