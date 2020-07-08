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
@Table(name = "pm_team_member")
public class TeamMember extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Team team;
	protected Role role;
	protected Member member;
	protected Integer order;
	protected Instant startDate;
	protected Instant endDate;
	
	// transient
	protected Long teamId;
	protected Long roleId;
	protected Long memberId;
	
	// Constructor
	public TeamMember(){}
	
	public TeamMember(Team team, Member member, Role role){
		this.team = team;
		this.member = member;
		this.role = role;
	}
	
	public TeamMember(Long id, boolean active, Integer order, Instant startDate, Instant endDate, Long roleId) {
		this.id = id;
		this.active = active;
		this.order = order;
		this.startDate = startDate;
		this.endDate = endDate;
		this.roleId = roleId;
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
	
	@JsonIgnore
	@ManyToOne(targetEntity = Role.class)
	@JoinColumn(name = "role_id")
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
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
	
	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "sort_order")
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "eff_start")
	public Instant getStartDate() {
		return startDate;
	}
	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}
	
	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "eff_end")
	public Instant getEndDate() {
		return endDate;
	}
	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Transient
	public Long getRoleId() {
		if (role == null) {
			return roleId;
		} else {
			return role.getId();
		}
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	@JsonView({View.Member.class,View.Admin.class})
	@Transient
	public Long getTeamId() {
		if (team == null) {
			return teamId;
		} else {
			return team.getId();
		}
	}
	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	@JsonView({View.Member.class,View.Admin.class})
	@Transient
	public Long getMemberId() {
		if (member == null) {
			return memberId;
		} else {
			return member.getId();
		}
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	
	
}
