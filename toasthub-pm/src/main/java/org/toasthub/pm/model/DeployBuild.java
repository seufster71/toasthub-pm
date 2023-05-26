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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.Text;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_deploy_build")
public class DeployBuild extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Deploy deploy;
	protected Instant startDate;
	protected Instant endDate;
	protected String buildStatus;
	protected String consoleOutputFile;

	
	//Constructor
	public DeployBuild() {
		super();
	}
	
	public DeployBuild(String code, Text title, Boolean defaultLang, String dir){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
	
		
	}

	// Methods
	@JsonIgnore
	@ManyToOne(targetEntity = Deploy.class)
	@JoinColumn(name = "deploy_id")
	public Deploy getDeploy() {
		return deploy;
	}
	public void setDeploy(Deploy deploy) {
		this.deploy = deploy;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "start_date")
	public Instant getStartDate() {
		return startDate;
	}
	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "end_date")
	public Instant getEndDate() {
		return endDate;
	}
	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "build_status")
	public String getBuildStatus() {
		return buildStatus;
	}
	public void setBuildStatus(String buildStatus) {
		this.buildStatus = buildStatus;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "console_output_file")
	public String getConsoleOutputFile() {
		return consoleOutputFile;
	}
	public void setConsoleOutputFile(String consoleOutputFile) {
		this.consoleOutputFile = consoleOutputFile;
	}

}
