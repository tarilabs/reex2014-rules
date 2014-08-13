package net.tarilabs.reex2014;

import org.kie.api.definition.type.PropertyReactive;

/**
 * The role of this class is being a reference for the main sentence uttered by the system
 *
 */
@PropertyReactive
public class Cogito {

	private long lastRssTs;
	private long lastTweetTs;
	private String text;
	public long getLastRssTs() {
		return lastRssTs;
	}
	public void setLastRssTs(long lastRssTs) {
		this.lastRssTs = lastRssTs;
	}
	public long getLastTweetTs() {
		return lastTweetTs;
	}
	public void setLastTweetTs(long lastTweetTs) {
		this.lastTweetTs = lastTweetTs;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Cogito [lastRssTs=").append(lastRssTs)
				.append(", lastTweetTs=").append(lastTweetTs).append(", ");
		if (text != null)
			builder.append("text=").append(text);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
