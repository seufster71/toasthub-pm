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

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_permission")
public class Permission extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected String name;
	protected String rights;
	protected String code;
	protected Instant effStart;
	protected Instant effEnd;
	
	protected Product product;
	protected Project project;
	
	// transient
	protected RolePermission rolePermission;
	
	// constructors
	public Permission(){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
	}
	
	public Permission(String code, String name, String rights) {
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
		this.setCode(code);
		this.setName(name);
		this.setRights(rights);
	}
	

	// Setters and getters
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "rights")
	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}

	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "code")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "eff_start", updatable = false)
	public Instant getEffStart() {
		return effStart;
	}
	public void setEffStart(Instant effStart) {
		this.effStart = effStart;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "eff_end", updatable = false)
	public Instant getEffEnd() {
		return effEnd;
	}
	public void setEffEnd(Instant effEnd) {
		this.effEnd = effEnd;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Transient
	public RolePermission getRolePermission() {
		return this.rolePermission;
	}
	public void setRolePermission(RolePermission rolePermission) {
		this.rolePermission = rolePermission;
	}
	
	
	@JsonView({View.Member.class,View.Admin.class})
	@ManyToOne(targetEntity = Product.class)
	@JoinColumn(name = "product_id")
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}

	@JsonView({View.Member.class,View.Admin.class})
	@ManyToOne(targetEntity = Project.class)
	@JoinColumn(name = "project_id")
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
}
