package it.zero11.xroads.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cron")
public class Cron implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int SUCCESS = 0;
	public static final int EXECUTING = 1;
	public static final int FAILED = 2;
	public static final int PENDING = -1;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	@Column(name="scheduled_time")
	private Date scheduledTime;
	
	@Column(name="execution_time")
	private Date executionTime;
	
	@Column(name="completed_time")
	private Date completedTime;
	
	private Integer status;
	
	private String node;
	
	@Column(name="xroads_module")
	private String xRoadsModule;
	
	@Column(name="error", columnDefinition = "text")
	private String error;
	
	@Column(name="force_execution")
	private Boolean forceExecution;

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getScheduledTime() {
		return scheduledTime;
	}
	
	public void setScheduledTime(Date scheduledTime) {
		this.scheduledTime = scheduledTime;
	}
	
	public Date getExecutionTime() {
		return executionTime;
	}
	
	public void setExecutionTime(Date executionTime) {
		this.executionTime = executionTime;
	}
	
	public Date getCompletedTime() {
		return completedTime;
	}
	
	public void setCompletedTime(Date completedTime) {
		this.completedTime = completedTime;
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public String getNode() {
		return node;
	}
	
	public void setNode(String node) {
		this.node = node;
	}
	
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}

	public Boolean getForceExecution() {
		return forceExecution;
	}

	public void setForceExecution(Boolean forceExecution) {
		this.forceExecution = forceExecution;
	}

	public String getxRoadsModule() {
		return xRoadsModule;
	}

	public void setxRoadsModule(String xRoadsModule) {
		this.xRoadsModule = xRoadsModule;
	}
	
}
