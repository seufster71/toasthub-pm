/*
 * Copyright (C) 2016 The ToastHub Project
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

package org.toasthub.pm.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pm_watcher")
public class Watcher extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long user;
	private Defect defect;
	private Enhancement enhancement;
	

	//Constructor
	public Watcher() {
		super();
	}
	

	// Methods
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@JoinColumn(name = "user_id")
	public Long getUser() {
		return user;
	}
	public void setUser(Long user) {
		this.user = user;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Defect.class)
	@JoinColumn(name = "defect_id")
	public Defect getDefect() {
		return defect;
	}
	public void setDefect(Defect defect) {
		this.defect = defect;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Enhancement.class)
	@JoinColumn(name = "enhancement_id")
	public Enhancement getEnhancement() {
		return enhancement;
	}
	public void setEnhancement(Enhancement enhancement) {
		this.enhancement = enhancement;
	}

	

	
}
