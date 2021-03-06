package com.yance.message;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.yance.content.ExceptionNotice;
import com.yance.httpclient.DingdingHttpClient;
import com.yance.pojos.dingding.DingDingAt;
import com.yance.pojos.dingding.DingDingNotice;
import com.yance.pojos.dingding.DingDingResult;
import com.yance.properties.DingDingExceptionNoticeProperty;
import com.yance.properties.ExceptionNoticeProperty;
import com.yance.properties.enums.DingdingTextType;
import com.yance.text.ExceptionNoticeResolverFactory;

public class DingDingNoticeSendComponent implements INoticeSendComponent {

	private final DingdingHttpClient httpClient;

	private final ExceptionNoticeProperty exceptionNoticeProperty;

	private Map<String, DingDingExceptionNoticeProperty> map;

	private final ExceptionNoticeResolverFactory exceptionNoticeResolverFactory;

	private final Log logger = LogFactory.getLog(getClass());

	public DingDingNoticeSendComponent(DingdingHttpClient httpClient, ExceptionNoticeProperty exceptionNoticeProperty,
			Map<String, DingDingExceptionNoticeProperty> map,
			ExceptionNoticeResolverFactory exceptionNoticeResolverFactory) {
		this.httpClient = httpClient;
		this.exceptionNoticeProperty = exceptionNoticeProperty;
		this.map = map;
		this.exceptionNoticeResolverFactory = exceptionNoticeResolverFactory;
	}

	/**
	 * @return the exceptionNoticeProperty
	 */
	public ExceptionNoticeProperty getExceptionNoticeProperty() {
		return exceptionNoticeProperty;
	}

	/**
	 * @return the map
	 */
	public Map<String, DingDingExceptionNoticeProperty> getMap() {
		return map;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(Map<String, DingDingExceptionNoticeProperty> map) {
		this.map = map;
	}

	@Override
	public void send(String blamedFor, ExceptionNotice exceptionNotice) {
		DingDingExceptionNoticeProperty dingDingExceptionNoticeProperty = map.get(blamedFor);
		if (dingDingExceptionNoticeProperty != null) {
			String notice = exceptionNoticeResolverFactory.resolve("dingding", exceptionNotice);
			DingDingNotice dingDingNotice = exceptionNoticeProperty.getDingdingTextType() == DingdingTextType.TEXT
					? new DingDingNotice(notice, new DingDingAt(dingDingExceptionNoticeProperty.getPhoneNum()))
					: new DingDingNotice("异常通知", notice, new DingDingAt(dingDingExceptionNoticeProperty.getPhoneNum()));
			DingDingResult result = httpClient.post(generateUrl(dingDingExceptionNoticeProperty), dingDingNotice,
					DingDingResult.class);
			logger.debug(result);
		} else
			logger.error("无法进行钉钉通知，不存在背锅侠");
	}

	@Override
	public Collection<String> getAllBuddies() {
		return map.keySet();
	}

	protected URI generateUrl(DingDingExceptionNoticeProperty dingDingExceptionNoticeProperty) {
		boolean enableSign = dingDingExceptionNoticeProperty.isEnableSignatureCheck();
		String signSec = dingDingExceptionNoticeProperty.getSignSecret();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dingDingExceptionNoticeProperty.getWebHook());
		if (enableSign && !StringUtils.isEmpty(signSec)) {
			Long timestamp = System.currentTimeMillis();
			String sign = generateSign(timestamp, signSec);
			Assert.notNull(sign, "calculate sign goes error!");
			builder.queryParam("timestamp", timestamp).queryParam("sign", sign);
		}
		URI uri = builder.build(true).toUri();
		return uri;
	}

	protected String generateSign(Long timestamp, String sec) {
		String combine = String.format("%d\n%s", timestamp, sec);
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(sec.getBytes("UTF-8"), "HmacSHA256"));
			byte[] signData = mac.doFinal(combine.getBytes("UTF-8"));
			return URLEncoder.encode(Base64.encodeBase64String(signData), "UTF-8");
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
			e.printStackTrace();
		}
		return null;
	}

}
