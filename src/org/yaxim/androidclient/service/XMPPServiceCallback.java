package org.yaxim.androidclient.service;

import org.jivesoftware.smack.packet.Message;

public interface XMPPServiceCallback {
	void notifyMessage(String[] from, String messageBody, boolean silent_notification, Message.Type msgType);
	void setGracePeriod(boolean activate);
	void connectionStateChanged();
	void mucInvitationReceived(String roomname, String room, String password, String invite_from, String roomdescription);
}
