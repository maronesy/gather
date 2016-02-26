package cs428.project.gather.data.model;


import java.sql.Timestamp;

import javax.persistence.*;

@Entity
public class ChangeLog {
	private @Id @GeneratedValue Long id;
	private String changeType;
	private String additionalInfo;
	private Timestamp datetime;

	@ManyToOne
	private Registrant registrant;
	
	protected ChangeLog() {}

	public ChangeLog(String changeType, String additionalInfo, Timestamp datetime) {
		this.setChangeType(changeType);
		this.setAdditionalInfo(additionalInfo);
		this.setDatetime(datetime);
	}

	public String getChangeType() {
		return changeType;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Timestamp getDatetime() {
		return datetime;
	}

	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}
}
