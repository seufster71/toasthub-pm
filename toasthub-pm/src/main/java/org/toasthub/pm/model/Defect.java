/*
 * Copyright (C) 2016 The ToastHub Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.toasthub.pm.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.MenuItem;
import org.toasthub.core.general.model.Text;
import org.toasthub.security.model.Application;
import org.toasthub.security.model.User;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "issue")
public class Defect extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String summary;
	private String description;
	private String stepsToRecreate;
	private String environment;
	private String workAround;
	private String envUsers;
	
	private User reportedBy;
	private User assignee;
	private String severity;
	private String priority;
	private String itemVersion;
	private String externalRef;
	private String internalRef;
	
	private String status;
	
	private String originalEstimate;
	private String actualDuration;
	
	private Application application;
	private Product product;
	
	private Project project;
	private Release release;
	
	private Set<User> watchers;


	//Constructor
	public Defect() {
		super();
	}
	
	public Defect(String code, Text title, Boolean defaultLang, String dir){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
		

		
	}
	
	// Methods
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "summary")
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "steps_to_recreate")
	public String getStepsToRecreate() {
		return stepsToRecreate;
	}
	public void setStepsToRecreate(String stepsToRecreate) {
		this.stepsToRecreate = stepsToRecreate;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "environment")
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "work_around")
	public String getWorkAround() {
		return workAround;
	}
	public void setWorkAround(String workAround) {
		this.workAround = workAround;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "env_users")
	public String getEnvUsers() {
		return envUsers;
	}
	public void setEnvUsers(String envUsers) {
		this.envUsers = envUsers;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "reported_by_id")
	public User getReportedBy() {
		return reportedBy;
	}
	public void setReportedBy(User reportedBy) {
		this.reportedBy = reportedBy;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "assignee_id")
	public User getAssignee() {
		return assignee;
	}
	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "severity")
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "priority")
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "item_version")
	public String getItemVersion() {
		return itemVersion;
	}
	public void setItemVersion(String itemVersion) {
		this.itemVersion = itemVersion;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "external_ref")
	public String getExternalRef() {
		return externalRef;
	}
	public void setExternalRef(String externalRef) {
		this.externalRef = externalRef;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "internal_ref")
	public String getInternalRef() {
		return internalRef;
	}
	public void setInternalRef(String internalRef) {
		this.internalRef = internalRef;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	

	
	
	
	
	
	
	
	

	
}
