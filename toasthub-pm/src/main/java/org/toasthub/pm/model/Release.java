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
import javax.persistence.Transient;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.Text;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_release")
public class Release extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected String name;
	protected String description;
	protected Instant startDate;
	protected Instant endDate;
	protected Long userId;
	
	protected Product product;
	protected Project project;

	protected Boolean allowShare = true;

	//Constructor
	public Release() {
		super();
	}
	
	public Release(String code, Text title, Boolean defaultLang, String dir){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
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
	@Column(name = "start_date")
	public Instant getStartDate() {
		return startDate;
	}
	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "end_date")
	public Instant getEndDate() {
		return endDate;
	}
	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}
	
	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Product.class)
	@JoinColumn(name = "product_id")
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Project.class)
	@JoinColumn(name = "project_id")
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	
	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Transient
	public Boolean getAllowShare() {
		return allowShare;
	}
	public void setAllowShare(Boolean allowShare) {
		this.allowShare = allowShare;
	}
}
