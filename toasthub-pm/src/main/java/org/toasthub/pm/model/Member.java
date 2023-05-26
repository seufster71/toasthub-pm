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
import java.util.Map;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_member")
public class Member extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Team team;
	protected Long userId;
	protected String name;
	protected String username;
	protected String type; // INTERNAL, EXTERNAL
	protected Instant startDate;
	protected Instant endDate;
	protected Set<MemberRole> memberRoles;
	
	// Constructor
	public Member(){}
	
	public Member(Team team){
		this.team = team;
	}
	
	public Member(Long id, boolean active, Instant startDate, Instant endDate) {
		this.id = id;
		this.active = active;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public Member(boolean active, boolean archive, boolean locked, String type, Team team, Long userId, String name, String username, Instant startDate, Instant endDate) {
		this.setActive(active);
		this.setArchive(archive);
		this.setLocked(locked);
		this.setTeam(team);
		this.setType(type);
		this.setUserId(userId);
		this.setName(name);
		this.setUsername(username);
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

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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
	@Column(name = "username")
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Transient
	public void setType(Map map) {
		if (map.containsKey("value")) {
			this.setType((String)map.get("value"));
		}
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

	@JsonIgnore
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	public Set<MemberRole> getMemberRoles() {
		return memberRoles;
	}
	public void setMemberRoles(Set<MemberRole> memberRoles) {
		this.memberRoles = memberRoles;
	}

}
