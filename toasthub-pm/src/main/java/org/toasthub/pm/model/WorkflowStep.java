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

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_workflow_step")
public class WorkflowStep extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected String name;
	protected String nextStep;    // JSON array
	protected int sortOrder;
	
	protected Workflow workflow;
	

	//Constructor
	public WorkflowStep() {
		super();
		this.setSortOrder(0);
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
	}
	
	public WorkflowStep(String name){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
		this.setName(name);
		this.setSortOrder(0);
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
	@ManyToOne(targetEntity = Workflow.class)
	@JoinColumn(name = "workflow_id")
	public Workflow getWorkflow() {
		return workflow;
	}
	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "next_step")
	public String getNextStep() {
		return nextStep;
	}

	public void setNextStep(String nextStep) {
		this.nextStep = nextStep;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "sort_order")
	public int getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	
}
