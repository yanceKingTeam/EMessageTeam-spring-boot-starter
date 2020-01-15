package com.yance.config.interfaces;

import com.yance.text.ExceptionNoticeResolverFactory;

@FunctionalInterface
public interface ExceptionNoticeResolverConfigure {

	public void addResolver(ExceptionNoticeResolverFactory factory);
}
