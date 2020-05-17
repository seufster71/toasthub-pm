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

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_test_scenario")
public class TestScenario extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String summary;
	private String description;
	private String environment;
	private String userInfo;
	private String setupInfo;
	
	
	private String type; // Acceptance, System, Integeration, Unit
	
	private Defect defect;
	private Enhancement enhancement;
	



	//Constructor
	public TestScenario() {
		super();
	}
	
	public TestScenario(String summary, String description, String type){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
		this.setSummary(summary);
		this.setDescription(description);
		this.setType(type);
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
	@Column(name = "type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	@Column(name = "user_info")
	public String getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "setup_info")
	public String getSetupInfo() {
		return setupInfo;
	}
	public void setSetupInfo(String setupInfo) {
		this.setupInfo = setupInfo;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Defect.class)
	@JoinColumn(name = "defect_id")
	public Defect getDefect() {
		return defect;
	}
	public void setDefect(Defect defect) {
		this.defect = defect;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Enhancement.class)
	@JoinColumn(name = "enhancement_id")
	public Enhancement getEnhancement() {
		return enhancement;
	}
	public void setEnhancement(Enhancement enhancement) {
		this.enhancement = enhancement;
	}

	
}
