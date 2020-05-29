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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.Text;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_enhancement")
public class Enhancement extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String summary;
	private String description;
	
	private Long reportedBy;
	private String assignee;
	private String severity;
	private int priority;
	private String itemVersion;
	private String externalRef;
	private String internalRef;
	
	private String status;
	
	private double developEstimate;
	private double testEstimate;
	
	private double developDuration;
	private double testDuration;
	
	private Product product;
	private Project project;
	private Release release;
	private Backlog backlog;
	private Sprint sprint;

	//Constructor
	public Enhancement() {
		super();
	}
	
	public Enhancement(String code, Text title, Boolean defaultLang, String dir){
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
	@Column(name = "reported_by_id")
	public Long getReportedBy() {
		return reportedBy;
	}
	public void setReportedBy(Long reportedBy) {
		this.reportedBy = reportedBy;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "assignee_ids")
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
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
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
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

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "develop_estimate")
	public double getDevelopEstimate() {
		return developEstimate;
	}
	public void setDevelopEstimate(double developEstimate) {
		this.developEstimate = developEstimate;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "test_estimate")
	public double getTestEstimate() {
		return testEstimate;
	}
	public void setTestEstimate(double testEstimate) {
		this.testEstimate = testEstimate;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "develop_duration")
	public double getDevelopDuration() {
		return developDuration;
	}
	public void setDevelopDuration(double developDuration) {
		this.developDuration = developDuration;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "test_duration")
	public double getTestDuration() {
		return testDuration;
	}
	public void setTestDuration(double testDuration) {
		this.testDuration = testDuration;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Product.class)
	@JoinColumn(name = "product_id")
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Project.class)
	@JoinColumn(name = "project_id")
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Release.class)
	@JoinColumn(name = "release_id")
	public Release getRelease() {
		return release;
	}
	public void setRelease(Release release) {
		this.release = release;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Backlog.class)
	@JoinColumn(name = "backlog_id")
	public Backlog getBacklog() {
		return backlog;
	}
	public void setBacklog(Backlog backlog) {
		this.backlog = backlog;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Sprint.class)
	@JoinColumn(name = "sprint_id")
	public Sprint getSprint() {
		return sprint;
	}
	public void setSprint(Sprint sprint) {
		this.sprint = sprint;
	}

	
}
