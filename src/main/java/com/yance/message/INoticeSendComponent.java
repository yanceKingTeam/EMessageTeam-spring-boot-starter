package com.yance.message;

import java.util.Collection;

import com.yance.content.ExceptionNotice;

public interface INoticeSendComponent {

	public void send(String blamedFor, ExceptionNotice exceptionNotice);

	public Collection<String> getAllBuddies();

}
