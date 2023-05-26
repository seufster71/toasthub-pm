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
@Table(name = "pm_deploy_system")
public class DeploySystem extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Deploy deploy;
	protected String serverName;
	protected String sshUsername;
	protected String sshPassword;
	protected String sshToken;
	protected String stagingDir;

	
	//Constructor
	public DeploySystem() {
		super();
	}
	
	public DeploySystem(String code, Text title, Boolean defaultLang, String dir){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
	}
	
	// HQL query constructor
	public DeploySystem(Long id, String serverName, String sshUsername, String stagingDir, Boolean active, Boolean archive, Boolean locked, Instant created, Instant modified){
		this.setId(id);
		this.setServerName(serverName);
		this.setSshUsername(sshUsername);
		this.setStagingDir(stagingDir);
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
	@Column(name = "server_name")
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "ssh_username")
	public String getSshUsername() {
		return sshUsername;
	}
	public void setSshUsername(String sshUsername) {
		this.sshUsername = sshUsername;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "ssh_password")
	public String getSshPassword() {
		return sshPassword;
	}
	public void setSshPassword(String sshPassword) {
		this.sshPassword = sshPassword;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "ssh_token")
	public String getSshToken() {
		return sshToken;
	}
	public void setSshToken(String sshToken) {
		this.sshToken = sshToken;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "staging_dir")
	public String getStagingDir() {
		return stagingDir;
	}
	public void setStagingDir(String stagingDir) {
		this.stagingDir = stagingDir;
	}

}
