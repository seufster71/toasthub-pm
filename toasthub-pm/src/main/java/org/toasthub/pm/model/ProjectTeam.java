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
@Table(name = "pm_project_team")
public class ProjectTeam extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Project project;
	protected Team team;

	// Transient
	protected Long teamId;
	
	// Constructor
	public ProjectTeam(){}
	
	
	public ProjectTeam(Long id, boolean active, Long teamId) {
		this.setId(id);
		this.setActive(active);
		this.setTeamId(teamId);
	}
	
	public ProjectTeam(Team team) {
		this.setTeam(team);
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
	}
	
	public ProjectTeam(boolean active, boolean archive, boolean locked, Project project, Team team) {
		this.setActive(active);
		this.setArchive(archive);
		this.setLocked(locked);
		this.setProject(project);
		this.setTeam(team);
	}
	
	public ProjectTeam(Project project, Team team) {
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setProject(project);
		this.setTeam(team);
	}
	
	@JsonIgnore
	@ManyToOne(targetEntity = Project.class)
	@JoinColumn(name = "project_id")
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}

	@JsonIgnore
	@ManyToOne(targetEntity = Team.class)
	@JoinColumn(name = "team_id")
	public Team getTeam() {
		return team;
	}
	public void setTeam(Team team) {
		this.team = team;
	}

	@JsonView({View.Admin.class})
	@Transient
	public Long getTeamId() {
		if (this.team == null) {
			return this.teamId;
		} else {
			return this.team.getId();
		}
	}
	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	
}
