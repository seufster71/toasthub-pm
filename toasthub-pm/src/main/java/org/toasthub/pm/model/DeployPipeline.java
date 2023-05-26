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
@Table(name = "pm_deploy_pipeline")
public class DeployPipeline extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Deploy deploy;
	protected String name;
	protected Integer sortOrder;
	protected String scmURL;
	protected String scmUser;
	protected String scmPassword;
	protected String branch;
	protected String compiler; 
	protected String commandlineScript;

	
	//Constructor
	public DeployPipeline() {
		super();
	}
	
	public DeployPipeline(String name, Text title, Boolean defaultLang, String dir){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
	}

	// HQL query constructor
	public DeployPipeline(Long id, String name, Integer sortOrder, String scmURL, String branch,Boolean active,Boolean archive,Boolean locked,Instant created,Instant modified){
		this.setId(id);
		this.setName(name);
		this.setSortOrder(sortOrder);
		this.setScmURL(scmURL);
		this.setBranch(branch);
		this.setActive(active);
		this.setArchive(archive);
		this.setLocked(locked);
		this.setCreated(created);
		this.setModified(modified);
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
	@Column(name = "sort_order")
	public Integer getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "scm_url")
	public String getScmURL() {
		return scmURL;
	}
	public void setScmURL(String scmURL) {
		this.scmURL = scmURL;
	}
	
	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "scm_user")
	public String getScmUser() {
		return scmUser;
	}
	public void setScmUser(String scmUser) {
		this.scmUser = scmUser;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "scm_password")
	public String getScmPassword() {
		return scmPassword;
	}
	public void setScmPassword(String scmPassword) {
		this.scmPassword = scmPassword;
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
	@Column(name = "compiler")
	public String getCompiler() {
		return compiler;
	}
	public void setCompiler(String compiler) {
		this.compiler = compiler;
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
