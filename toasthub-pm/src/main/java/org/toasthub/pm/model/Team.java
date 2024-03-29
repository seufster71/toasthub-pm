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
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.Text;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_team")
public class Team extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	protected String name;
	protected String type;
	protected long ownerId;
	
	protected Set<Member> members;
	
	// Transient
	protected ProductTeam productTeam;
	protected ProjectTeam projectTeam;
	protected BacklogTeam backlogTeam;
	protected ReleaseTeam releaseTeam;
	protected DeployTeam deployTeam;
	protected TestCaseTeam testCaseTeam;
	
	//Constructor
	public Team() {
		super();
	}
	
	public Team(String code, Text title, Boolean defaultLang, String dir){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
		
	}

	// Methods

	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@JsonView({View.Admin.class})
	@Column(name = "type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "owner_id")
	public long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	@JsonIgnore
	@OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
	public Set<Member> getMembers() {
		return members;
	}
	public void setMembers(Set<Member> members) {
		this.members = members;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Transient
	public ProductTeam getProductTeam() {
		return productTeam;
	}
	public void setProductTeam(ProductTeam productTeam) {
		this.productTeam = productTeam;
	}

	@JsonView({View.Member.class,View.Admin.class})
	@Transient
	public ProjectTeam getProjectTeam() {
		return projectTeam;
	}
	public void setProjectTeam(ProjectTeam projectTeam) {
		this.projectTeam = projectTeam;
	}

	@JsonView({View.Member.class,View.Admin.class})
	@Transient
	public BacklogTeam getBacklogTeam() {
		return backlogTeam;
	}
	public void setBacklogTeam(BacklogTeam backlogTeam) {
		this.backlogTeam = backlogTeam;
	}

	@JsonView({View.Member.class,View.Admin.class})
	@Transient
	public ReleaseTeam getReleaseTeam() {
		return releaseTeam;
	}
	public void setReleaseTeam(ReleaseTeam releaseTeam) {
		this.releaseTeam = releaseTeam;
	}

	@JsonView({View.Member.class,View.Admin.class})
	@Transient
	public DeployTeam getDeployTeam() {
		return deployTeam;
	}
	public void setDeployTeam(DeployTeam deployTeam) {
		this.deployTeam = deployTeam;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Transient
	public TestCaseTeam getTestCaseTeam() {
		return testCaseTeam;
	}
	public void setTestCaseTeam(TestCaseTeam testCaseTeam) {
		this.testCaseTeam = testCaseTeam;
	}
	
}
