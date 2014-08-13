package net.tarilabs.reex2014;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.time.FastDateFormat;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

// TODO normally XmlTransient below would suffice with Resteasy 3 on Wildfly, but for the moment we stick to RESTeasy2.x on JBoss AS 7
@JsonIgnoreProperties(value={"ref"})
public class Alert<T> {
	
	private static FastDateFormat fdf;
	static {
		fdf = FastDateFormat.getInstance("yyyy-MM-dd 'T' HH:mm:ss.SSS Z");
	}

	private long ts;
	private String condition;
	private AlertType type;
	
	@XmlTransient
	private T ref;
	
	public Alert(long ts, String condition,	AlertType type, T ref) {
		super();
		this.ts = ts;
		this.condition = condition;
		this.type = type;
		this.ref = ref;
	}

	public long getTs() {
		return ts;
	}
	
	public String getTsFormatted() {
		return fdf.format(ts);
	}

	public String getCondition() {
		return condition;
	}

	public AlertType getType() {
		return type;
	}
	
	public T getRef() {
		return ref;
	}
	
	/**
	 * Why this? See {@link https://java.net/jira/browse/JSP_SPEC_PUBLIC-113}
	 * @return
	 */
	public String getTSimpleClassName() {
		return ref.getClass().getSimpleName();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Alert [");
		if (getTsFormatted() != null)
			builder.append("getTsFormatted()=").append(getTsFormatted()).append(", ");
		if (condition != null)
			builder.append("condition=").append(condition).append(", ");
		if (type != null)
			builder.append("type=").append(type).append(", ");
		builder.append("ts=").append(ts);
		builder.append("]");
		return builder.toString();
	}
}
