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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.Text;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_role")
public class Role extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Team team;
	protected String name;
	protected String code;
	protected Set<RolePermission> permissions;
	protected Instant startDate;
	protected Instant endDate;
	protected Set<MemberRole> memberRoles;

	// transient
	protected MemberRole memberRole;
	
	//Constructor
	public Role() {
		super();
	}
	
	public Role(String code, Text title, Boolean defaultLang, String dir){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
	}

	public Role(boolean active, boolean archive, boolean locked, String name, String code, Team team, Instant startDate, Instant endDate) {
		this.setActive(active);
		this.setArchive(archive);
		this.setLocked(locked);
		this.setName(name);
		this.setCode(code);
		this.setTeam(team);
		this.setStartDate(startDate);
		this.setEndDate(endDate);
	}
	
	// Methods
	@JsonIgnore
	@ManyToOne(targetEntity = Team.class)
	@JoinColumn(name = "team_id")
	public Team getTeam() {
		return team;
	}
	public void setTeam(Team team) {
		this.team = team;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "code")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	@JsonIgnore
	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	public Set<RolePermission> getPermissions() {
		return permissions;
	}
	public void setPermissions(Set<RolePermission> permissions) {
		this.permissions = permissions;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "start_date")
	public Instant getStartDate() {
		return startDate;
	}
	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "end_date")
	public Instant getEndDate() {
		return endDate;
	}
	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}

	@JsonView({View.Member.class,View.Admin.class})
	@Transient
	public MemberRole getMemberRole() {
		return memberRole;
	}
	public void setMemberRole(MemberRole memberRole) {
		this.memberRole = memberRole;
	}
	
	@JsonIgnore
	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	public Set<MemberRole> getMemberRoles() {
		return memberRoles;
	}
	public void setMemberRoles(Set<MemberRole> memberRoles) {
		this.memberRoles = memberRoles;
	}
}
