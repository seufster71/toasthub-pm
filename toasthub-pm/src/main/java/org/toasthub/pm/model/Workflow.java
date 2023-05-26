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
import jakarta.persistence.Table;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_workflow")
public class Workflow extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected String name;
	protected String description;
	protected Long userId;
	
	//Constructor
	public Workflow() {
		super();
	}
	
	public Workflow(String name, String description){
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
	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
