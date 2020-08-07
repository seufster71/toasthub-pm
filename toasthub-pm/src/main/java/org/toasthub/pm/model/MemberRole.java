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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_member_role")
public class MemberRole extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Member member;
	protected Role role;
	protected Integer sortOrder;
	protected Instant startDate;
	protected Instant endDate;

	// transient
	protected Long roleId;
		
	// Constructor
	public MemberRole(){}
	
	
	public MemberRole(Long id, boolean active, Integer sortOrder, Instant startDate, Instant endDate, Long roleId) {
		this.setId(id);
		this.setActive(active);
		this.setSortOrder(sortOrder);
		this.setStartDate(startDate);
		this.setEndDate(endDate);
		this.setRoleId(roleId);
	}
	
	public MemberRole(Role role, Integer sortOrder, Instant startDate, Instant endDate) {
		this.setRole(role);
		this.setSortOrder(sortOrder);
		this.setStartDate(startDate);
		this.setEndDate(endDate);
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
	}
	
	public MemberRole(boolean active, boolean archive, boolean locked, Integer sortOrder, Instant startDate, Instant endDate, Member member, Role role) {
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setSortOrder(sortOrder);
		this.setStartDate(startDate);
		this.setEndDate(endDate);
		this.setMember(member);
		this.setRole(role);
	}
	
	@JsonIgnore
	@ManyToOne(targetEntity = Member.class)
	@JoinColumn(name = "member_id")
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	
	@JsonIgnore
	@ManyToOne(targetEntity = Role.class)
	@JoinColumn(name = "role_id")
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	
	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "sort_order")
	public Integer getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
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
	
	@JsonView({View.Admin.class})
	@Transient
	public Long getRoleId() {
		if (this.role == null) {
			return this.roleId;
		} else {
			return this.role.getId();
		}
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
}
