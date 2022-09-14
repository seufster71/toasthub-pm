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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_test_case_deploy")
public class TestCaseDeploy extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected TestCase testCase;
	protected Deploy deploy;

	// Transient
	protected Long deployId;
	protected Long testCaseId;
	
	// Constructor
	public TestCaseDeploy(){}
	
	
	public TestCaseDeploy(Long id, boolean active, Long deployId) {
		this.setId(id);
		this.setActive(active);
		this.setDeployId(deployId);
	}
	
	public TestCaseDeploy(Long id, boolean active, Long deployId, Long testCaseId) {
		this.setId(id);
		this.setActive(active);
		this.setDeployId(deployId);
		this.setTestCaseId(testCaseId);
	}
	
	public TestCaseDeploy(Deploy deploy) {
		this.setDeploy(deploy);
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
	}
	
	public TestCaseDeploy(boolean active, boolean archive, boolean locked, TestCase testCase, Deploy deploy) {
		this.setActive(active);
		this.setArchive(archive);
		this.setLocked(locked);
		this.setTestCase(testCase);
		this.setDeploy(deploy);
	}
	
	public TestCaseDeploy(TestCase testCase, Deploy deploy) {
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setTestCase(testCase);
		this.setDeploy(deploy);
	}
	
	@JsonIgnore
	@ManyToOne(targetEntity = TestCase.class)
	@JoinColumn(name = "test_case_id")
	public TestCase getTestCase() {
		return testCase;
	}
	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
	}

	@JsonIgnore
	@ManyToOne(targetEntity = Deploy.class)
	@JoinColumn(name = "deploy_id")
	public Deploy getDeploy() {
		return deploy;
	}
	public void setDeploy(Deploy deploy) {
		this.deploy = deploy;
	}

	@JsonView({View.Admin.class})
	@Transient
	public Long getDeployId() {
		if (this.deploy == null) {
			return this.deployId;
		} else {
			return this.deploy.getId();
		}
	}
	public void setDeployId(Long deployId) {
		this.deployId = deployId;
	}

	@JsonView({View.Admin.class})
	@Transient
	public Long getTestCaseId() {
		if (this.testCase == null) {
			return this.testCaseId;
		} else {
			return this.testCase.getId();
		}
	}
	public void setTestCaseId(Long testCaseId) {
		this.testCaseId = testCaseId;
	}
}
