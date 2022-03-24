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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_deploy_pipeline")
public class DeployPipeline extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Deploy deploy;
	protected String name;
	protected Integer sequence;
	protected String repositoryUrl;
	protected String branch;
	protected String compileType; 
	protected String commandlineScript;

	
	//Constructor
	public DeployPipeline() {
		super();
	}
	
	public DeployPipeline(String code, Text title, Boolean defaultLang, String dir){
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
	
	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "sequence")
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "repository_url")
	public String getRepositoryUrl() {
		return repositoryUrl;
	}
	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "branch")
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "compile_type")
	public String getCompileType() {
		return compileType;
	}
	public void setCompileType(String compileType) {
		this.compileType = compileType;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "commandline_script")
	public String getCommandlineScript() {
		return commandlineScript;
	}
	public void setCommandlineScript(String commandlineScript) {
		this.commandlineScript = commandlineScript;
	}

	
}
