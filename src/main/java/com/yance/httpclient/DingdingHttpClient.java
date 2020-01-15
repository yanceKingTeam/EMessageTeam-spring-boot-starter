package com.yance.httpclient;

import java.net.URI;

import com.yance.pojos.dingding.DingDingNotice;
import com.yance.pojos.dingding.DingDingResult;

@FunctionalInterface
public interface DingdingHttpClient {

	DingDingResult post(URI url, DingDingNotice body, Class<DingDingResult> clazz);

}
