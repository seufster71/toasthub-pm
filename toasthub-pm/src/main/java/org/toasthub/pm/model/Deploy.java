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
	protected String scmUser;
	protected String scmPassword;
	protected String serverName;
	protected String sshUsername;
	protected String sshPassphrase;
	protected String sshToken;
	protected String workspace;
	protected String stagingDir;
	
	protected long ownerId;

	
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
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "last_success")
	public Instant getLastSuccess() {
		return lastSuccess;
	}
	public void setLastSuccess(Instant lastSuccess) {
		this.lastSuccess = lastSuccess;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "last_fail")
	public Instant getLastFail() {
		return lastFail;
	}
	public void setLastFail(Instant lastFail) {
		this.lastFail = lastFail;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "last_duration")
	public double getLastDuration() {
		return lastDuration;
	}
	public void setLastDuration(double lastDuration) {
		this.lastDuration = lastDuration;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
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
	@Column(name = "pass_phrase")
	public String getSshPassphrase() {
		return sshPassphrase;
	}
	public void setSshPassphrase(String sshPassphrase) {
		this.sshPassphrase = sshPassphrase;
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
	@Column(name = "workspace")
	public String getWorkspace() {
		return workspace;
	}
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "staging_dir")
	public String getStagingDir() {
		return stagingDir;
	}
	public void setStagingDir(String stagingDir) {
		this.stagingDir = stagingDir;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "owner_id")
	public long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	
}
