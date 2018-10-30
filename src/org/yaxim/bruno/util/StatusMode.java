package org.yaxim.bruno.util;

import org.yaxim.bruno.R;

public enum StatusMode {
	unknown(0 /* not a status you can set */, R.drawable.ic_status_unknown),
	offline(R.string.status_offline, R.drawable.ic_status_offline),
	xa(R.string.status_xa, R.drawable.ic_status_xa),
	dnd(R.string.status_dnd, R.drawable.ic_status_dnd),
	away(R.string.status_away, R.drawable.ic_status_away),
	available(R.string.status_available, R.drawable.ic_status_available),
	chat(R.string.status_chat, R.drawable.ic_status_chat),
	subscribe(0 /* not a status you can set */, R.drawable.ic_status_subscribe);

	private final int textId;
	private final int drawableId;

	StatusMode(int textId, int drawableId) {
		this.textId = textId;
		this.drawableId = drawableId;
	}

	public int getTextId() {
		return textId;
	}

	public int getDrawableId() {
		return drawableId;
	}

	public String toString() {
		return name();
	}

	public static StatusMode fromString(String status) {
		return StatusMode.valueOf(status);
	}

}
