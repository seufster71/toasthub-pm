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
import org.toasthub.core.general.model.Text;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_scrum_request")
public class ScrumRequest extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected String name;
	protected String subject;
	protected String description;
	protected String requestDaysTimes; // json object
	protected String emailTo; // json object
	
	protected Scrum scrum;

	//Constructor
	public ScrumRequest() {
		super();
	}
	
	public ScrumRequest(String code, Text title, Boolean defaultLang, String dir){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
		

		
	}

	// Methods
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "subject")
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "request_days_times")
	public String getRequestDaysTimes() {
		return requestDaysTimes;
	}
	public void setRequestDaysTimes(String requestDaysTimes) {
		this.requestDaysTimes = requestDaysTimes;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "email_to")
	public String getEmailTo() {
		return emailTo;
	}
	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Scrum.class)
	@JoinColumn(name = "scrum_id")
	public Scrum getScrum() {
		return scrum;
	}
	public void setScrum(Scrum scrum) {
		this.scrum = scrum;
	}

	

	
	
	
	
	
	
	
	

	
}
