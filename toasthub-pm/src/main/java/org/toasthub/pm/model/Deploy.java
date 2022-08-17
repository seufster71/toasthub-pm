/*
 * Copyright (C) 2020 The ToastHub Project
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

/**
 * @author Edward H. Seufert
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
@Table(name = "pm_deploy")
public class Deploy extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected String name;
	protected Instant lastSuccess;
	protected Instant lastFail;
	protected double lastDuration;
	protected String runStatus;
	protected String cronSchedule;
	protected String workspace;
	
	protected long userId;

	protected Product product;
	protected Project project;
	
	//Constructor
	public Deploy() {
		super();
	}
	
	public Deploy(String code, Text title, Boolean defaultLang, String dir){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
	}
	
	// HQL query constructor
	public Deploy(Long id, String name, Instant lastSuccess, Instant lastFail, double lastDuration,String runStatus,Boolean active,Boolean archive,Boolean locked,Instant created,Instant modified){
		this.setId(id);
		this.setName(name);
		this.setLastSuccess(lastSuccess);
		this.setLastFail(lastFail);
		this.setLastDuration(lastDuration);
		this.setRunStatus(runStatus);
		this.setActive(active);
		this.setArchive(archive);
		this.setLocked(locked);
		this.setCreated(created);
		this.setModified(modified);
	}

	// Methods
	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "last_success")
	public Instant getLastSuccess() {
		return lastSuccess;
	}
	public void setLastSuccess(Instant lastSuccess) {
		this.lastSuccess = lastSuccess;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "last_fail")
	public Instant getLastFail() {
		return lastFail;
	}
	public void setLastFail(Instant lastFail) {
		this.lastFail = lastFail;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "last_duration")
	public double getLastDuration() {
		return lastDuration;
	}
	public void setLastDuration(double lastDuration) {
		this.lastDuration = lastDuration;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "run_status")
	public String getRunStatus() {
		return runStatus;
	}
	public void setRunStatus(String runStatus) {
		this.runStatus = runStatus;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "cron_schedule")
	public String getCronSchedule() {
		return cronSchedule;
	}
	public void setCronSchedule(String cronSchedule) {
		this.cronSchedule = cronSchedule;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "workspace")
	public String getWorkspace() {
		return workspace;
	}
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "user_id")
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Product.class)
	@JoinColumn(name = "product_id")
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Project.class)
	@JoinColumn(name = "project_id")
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
}
