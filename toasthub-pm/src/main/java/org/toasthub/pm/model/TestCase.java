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

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_test_case")
public class TestCase extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected String name;
	protected String description;
	protected String environment;
	protected String userInfo;
	protected String setupInfo;
	// protected String type; // Acceptance, System, Integeration, Unit

	protected Deploy deploy;

	//Constructor
	public TestCase() {
		super();
	}
	
	public TestCase(String summary, String description){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
		this.setName(name);
		this.setDescription(description);
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
	@Column(name = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "environment")
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "user_info")
	public String getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "setup_info")
	public String getSetupInfo() {
		return setupInfo;
	}
	public void setSetupInfo(String setupInfo) {
		this.setupInfo = setupInfo;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Deploy.class)
	@JoinColumn(name = "deploy_id")
	public Deploy getDeploy() {
		return deploy;
	}
	public void setDeploy(Deploy deploy) {
		this.deploy = deploy;
	}
	
}
