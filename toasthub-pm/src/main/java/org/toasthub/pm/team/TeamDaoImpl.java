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

package org.toasthub.pm.team;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.PrefCacheUtil;
import org.toasthub.pm.model.Backlog;
import org.toasthub.pm.model.BacklogTeam;
import org.toasthub.pm.model.Member;
import org.toasthub.pm.model.MemberRole;
import org.toasthub.pm.model.Permission;
import org.toasthub.pm.model.Product;
import org.toasthub.pm.model.ProductTeam;
import org.toasthub.pm.model.Project;
import org.toasthub.pm.model.ProjectTeam;
import org.toasthub.pm.model.Release;
import org.toasthub.pm.model.ReleaseTeam;
import org.toasthub.pm.model.Role;
import org.toasthub.pm.model.RolePermission;
import org.toasthub.pm.model.Team;
import org.toasthub.security.model.MyUserPrincipal;
import org.toasthub.security.model.User;

@Repository("PMTeamDao")
@Transactional("TransactionManagerData")
public class TeamDaoImpl implements TeamDao {
	
	@Autowired
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;
	@Autowired
	PrefCacheUtil prefCacheUtil;
	
	@Override
	public void delete(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			
			Team team = (Team) entityManagerDataSvc.getInstance().getReference(Team.class,  new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
			entityManagerDataSvc.getInstance().remove(team);
			
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

	@Override
	public void save(RestRequest request, RestResponse response) throws Exception {
		Team team = (Team) request.getParam(GlobalConstant.ITEM);
		
		if (team.getId() == null) {
			team = entityManagerDataSvc.getInstance().merge(team);
			
			// add yourself as member
			User user = ((MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
			Member member = new Member(true, false, false, "INTERNAL", team, user.getId(), user.getFirstname()+" "+user.getLastname(), user.getUsername(), Instant.now().plus(-3650,ChronoUnit.DAYS),Instant.now().plus(21900, ChronoUnit.DAYS));
			member = entityManagerDataSvc.getInstance().merge(member);
			
			// Admin Role
			Role role = new Role(true, false, false, "Admin", "ADMIN", team, Instant.now().plus(-3650,ChronoUnit.DAYS),Instant.now().plus(21900, ChronoUnit.DAYS));
			role = entityManagerDataSvc.getInstance().merge(role);
			MemberRole memberRole = new MemberRole(true, false, false, 1, Instant.now().plus(-3650,ChronoUnit.DAYS),Instant.now().plus(21900, ChronoUnit.DAYS), member, role);
			memberRole = entityManagerDataSvc.getInstance().merge(memberRole);
			String queryStr = "SELECT x FROM Permission AS x WHERE x.code =:code";
			
			Permission permission = null;
			RolePermission rolePermission = null;
			// Allow
			List<String> permList = new ArrayList<String>(Arrays.asList("PMPRODUCTV","PMPRODUCTC","PMPRODUCTM","PMPRODUCTD"
					,"PMPROJECTV","PMPROJECTC","PMPROJECTM","PMPROJECTD"
					,"PMBACKLOGV","PMBACKLOGC","PMBACKLOGM","PMBACKLOGD"
					,"PMCOMMENTV","PMCOMMENTC","PMCOMMENTM","PMCOMMENTD"
					,"PMDEFECTV","PMDEFECTC","PMDEFECTM","PMDEFECTD"
					,"PMENHANCEMENTV","PMENHANCEMENTC","PMENHANCEMENTM","PMENHANCEMENTD"
					,"PMRELEASEV","PMRELEASEC","PMRELEASEM","PMRELEASED"
					,"PMROLEV","PMROLEC","PMROLEM","PMROLED"
					,"PMPERMISSIONV","PMPERMISSIONC","PMPERMISSIONM","PMPERMISSIOND"
					,"PMSCRUMV","PMSCRUMC","PMSCRUMM","PMSCRUMD"
					,"PMSPRINTV","PMSPRINTC","PMSPRINTM","PMSPRINTD"
					,"PMTASKV","PMTASKC","PMTASKM","PMTASKD"
					,"PMTEAMV","PMTEAMC","PMTEAMM","PMTEAMD"
					,"PMTEAMMEMBERV","PMTEAMMEMBERC","PMTEAMMEMBERM","PMTEAMMEMBERD"
					,"PMTESTCASEV","PMTESTCASEC","PMTESTCASEM","PMTESTCASED"
					,"PMTESTSCENARIOV","PMTESTSCENARIOC","PMTESTSCENARIOM","PMTESTSCENARIOD"
					,"PMWORKFLOWV","PMWORKFLOWC","PMWORKFLOWM","PMWORKFLOWD"));
			for(String code : permList) {
				permission = (Permission) entityManagerDataSvc.getInstance().createQuery(queryStr).setParameter("code", code).getSingleResult();
				rolePermission = new RolePermission(true,false,true,Instant.now().plus(-3650,ChronoUnit.DAYS),Instant.now().plus(21900, ChronoUnit.DAYS),"Y",role,permission);
				rolePermission = entityManagerDataSvc.getInstance().merge(rolePermission);
			}
			
			// Member Role
			role = new Role(true, false, false, "Member", "MEMBER", team, Instant.now().plus(-3650,ChronoUnit.DAYS),Instant.now().plus(21900, ChronoUnit.DAYS));
			role = entityManagerDataSvc.getInstance().merge(role);
			
			// Allow
			permList = new ArrayList<String>(Arrays.asList("PMPRODUCTV","PMPRODUCTC","PMPRODUCTM","PMPRODUCTD"
					,"PMPROJECTV","PMPROJECTC","PMPROJECTM","PMPROJECTD"
					,"PMBACKLOGV","PMBACKLOGC","PMBACKLOGM","PMBACKLOGD"
					,"PMCOMMENTV","PMCOMMENTC","PMCOMMENTM","PMCOMMENTD"
					,"PMDEFECTV","PMDEFECTC","PMDEFECTM","PMDEFECTD"
					,"PMENHANCEMENTV","PMENHANCEMENTC","PMENHANCEMENTM","PMENHANCEMENTD"
					,"PMRELEASEV","PMRELEASEC","PMRELEASEM","PMRELEASED"
					,"PMROLEV"
					,"PMPERMISSIONV"
					,"PMSCRUMV","PMSCRUMC","PMSCRUMM","PMSCRUMD"
					,"PMSPRINTV","PMSPRINTC","PMSPRINTM","PMSPRINTD"
					,"PMTASKV","PMTASKC","PMTASKM","PMTASKD"
					,"PMTEAMV"
					,"PMTEAMMEMBERV"
					,"PMTESTCASEV","PMTESTCASEC","PMTESTCASEM","PMTESTCASED"
					,"PMTESTSCENARIOV","PMTESTSCENARIOC","PMTESTSCENARIOM","PMTESTSCENARIOD"
					,"PMWORKFLOWV"));
			for(String code : permList) {
				permission = (Permission) entityManagerDataSvc.getInstance().createQuery(queryStr).setParameter("code", code).getSingleResult();
				rolePermission = new RolePermission(true,false,true,Instant.now().plus(-3650,ChronoUnit.DAYS),Instant.now().plus(21900, ChronoUnit.DAYS),"Y",role,permission);
				rolePermission = entityManagerDataSvc.getInstance().merge(rolePermission);
			}
			// Deny
			permList = new ArrayList<String>(Arrays.asList(
					"PMROLEC","PMROLEM","PMROLED"
					,"PMPERMISSIONC","PMPERMISSIONM","PMPERMISSIOND"
					,"PMTEAMC","PMTEAMM","PMTEAMD"
					,"PMTEAMMEMBERC","PMTEAMMEMBERM","PMTEAMMEMBERD"
					,"PMWORKFLOWC","PMWORKFLOWM","PMWORKFLOWD"));
			for(String code : permList) {
				permission = (Permission) entityManagerDataSvc.getInstance().createQuery(queryStr).setParameter("code", code).getSingleResult();
				rolePermission = new RolePermission(true,false,true,Instant.now().plus(-3650,ChronoUnit.DAYS),Instant.now().plus(21900, ChronoUnit.DAYS),"N",role,permission);
				rolePermission = entityManagerDataSvc.getInstance().merge(rolePermission);
			}
			
			// Tester
			role = new Role(true, false, false, "Tester", "TESTER", team, Instant.now().plus(-3650,ChronoUnit.DAYS),Instant.now().plus(21900, ChronoUnit.DAYS));
			role = entityManagerDataSvc.getInstance().merge(role);
			
			// Allow
			permList = new ArrayList<String>(Arrays.asList("PMPRODUCTV"
					,"PMPROJECTV"
					,"PMBACKLOGV"
					,"PMCOMMENTV","PMCOMMENTC","PMCOMMENTM","PMCOMMENTD"
					,"PMDEFECTV","PMDEFECTC"
					,"PMENHANCEMENTV","PMENHANCEMENTC"
					,"PMRELEASEV"
					,"PMROLEV"
					,"PMPERMISSIONV"
					,"PMSCRUMV"
					,"PMSPRINTV"
					,"PMTASKV","PMTASKC"
					,"PMTEAMV"
					,"PMTEAMMEMBERV"
					,"PMTESTCASEV","PMTESTCASEC","PMTESTCASEM","PMTESTCASED"
					,"PMTESTSCENARIOV","PMTESTSCENARIOC","PMTESTSCENARIOM","PMTESTSCENARIOD"
					,"PMWORKFLOWV"));
			for(String code : permList) {
				permission = (Permission) entityManagerDataSvc.getInstance().createQuery(queryStr).setParameter("code", code).getSingleResult();
				rolePermission = new RolePermission(true,false,true,Instant.now().plus(-3650,ChronoUnit.DAYS),Instant.now().plus(21900, ChronoUnit.DAYS),"Y",role,permission);
				rolePermission = entityManagerDataSvc.getInstance().merge(rolePermission);
			}
			// Deny
			permList = new ArrayList<String>(Arrays.asList(
					"PMPRODUCTC","PMPRODUCTM","PMPRODUCTD"
					,"PMPROJECTC","PMPROJECTM","PMPROJECTD"
					,"PMBACKLOGC","PMBACKLOGM","PMBACKLOGD"
					,"PMDEFECTM","PMDEFECTD"
					,"PMENHANCEMENTM","PMENHANCEMENTD"
					,"PMRELEASEC","PMRELEASEM","PMRELEASED"
					,"PMROLEC","PMROLEM","PMROLED"
					,"PMPERMISSIONC","PMPERMISSIONM","PMPERMISSIOND"
					,"PMSCRUMC","PMSCRUMM","PMSCRUMD"
					,"PMSPRINTC","PMSPRINTM","PMSPRINTD"
					,"PMTASKM","PMTASKD"
					,"PMTEAMC","PMTEAMM","PMTEAMD"
					,"PMTEAMMEMBERC","PMTEAMMEMBERM","PMTEAMMEMBERD"
					,"PMWORKFLOWC","PMWORKFLOWM","PMWORKFLOWD"));
			for(String code : permList) {
				permission = (Permission) entityManagerDataSvc.getInstance().createQuery(queryStr).setParameter("code", code).getSingleResult();
				rolePermission = new RolePermission(true,false,true,Instant.now().plus(-3650,ChronoUnit.DAYS),Instant.now().plus(21900, ChronoUnit.DAYS),"N",role,permission);
				rolePermission = entityManagerDataSvc.getInstance().merge(rolePermission);
			}
			
			// Viewer
			role = new Role(true, false, false, "Viewer", "VIEWER", team, Instant.now().plus(-3650,ChronoUnit.DAYS),Instant.now().plus(21900, ChronoUnit.DAYS));
			role = entityManagerDataSvc.getInstance().merge(role);
			
			// Allow
			permList = new ArrayList<String>(Arrays.asList("PMPRODUCTV"
					,"PMPROJECTV"
					,"PMBACKLOGV"
					,"PMCOMMENTV"
					,"PMDEFECTV"
					,"PMENHANCEMENTV"
					,"PMRELEASEV"
					,"PMROLEV"
					,"PMPERMISSIONV"
					,"PMSCRUMV"
					,"PMSPRINTV"
					,"PMTASKV","PMTASKC"
					,"PMTEAMV"
					,"PMTEAMMEMBERV"
					,"PMTESTCASEV"
					,"PMTESTSCENARIOV"
					,"PMWORKFLOWV"));
			for(String code : permList) {
				permission = (Permission) entityManagerDataSvc.getInstance().createQuery(queryStr).setParameter("code", code).getSingleResult();
				rolePermission = new RolePermission(true,false,true,Instant.now().plus(-3650,ChronoUnit.DAYS),Instant.now().plus(21900, ChronoUnit.DAYS),"Y",role,permission);
				rolePermission = entityManagerDataSvc.getInstance().merge(rolePermission);
			}
			// Deny
			permList = new ArrayList<String>(Arrays.asList(
					"PMPRODUCTC","PMPRODUCTM","PMPRODUCTD"
					,"PMPROJECTC","PMPROJECTM","PMPROJECTD"
					,"PMBACKLOGC","PMBACKLOGM","PMBACKLOGD"
					,"PMCOMMENTC","PMCOMMENTM","PMCOMMENTD"
					,"PMDEFECTC","PMDEFECTM","PMDEFECTD"
					,"PMENHANCEMENTC","PMENHANCEMENTM","PMENHANCEMENTD"
					,"PMRELEASEC","PMRELEASEM","PMRELEASED"
					,"PMROLEC","PMROLEM","PMROLED"
					,"PMPERMISSIONC","PMPERMISSIONM","PMPERMISSIOND"
					,"PMSCRUMC","PMSCRUMM","PMSCRUMD"
					,"PMSPRINTC","PMSPRINTM","PMSPRINTD"
					,"PMTASKM","PMTASKD"
					,"PMTEAMC","PMTEAMM","PMTEAMD"
					,"PMTEAMMEMBERC","PMTEAMMEMBERM","PMTEAMMEMBERD"
					,"PMTESTCASEC","PMTESTCASEM","PMTESTCASED"
					,"PMTESTSCENARIOC","PMTESTSCENARIOM","PMTESTSCENARIOD"
					,"PMWORKFLOWC","PMWORKFLOWM","PMWORKFLOWD"));
			for(String code : permList) {
				permission = (Permission) entityManagerDataSvc.getInstance().createQuery(queryStr).setParameter("code", code).getSingleResult();
				rolePermission = new RolePermission(true,false,true,Instant.now().plus(-3650,ChronoUnit.DAYS),Instant.now().plus(21900, ChronoUnit.DAYS),"N",role,permission);
				rolePermission = entityManagerDataSvc.getInstance().merge(rolePermission);
			}
		} else {
			entityManagerDataSvc.getInstance().merge(team);
		}
		
		if (request.containsParam(GlobalConstant.PARENTID) && !"".equals(request.getParam(GlobalConstant.PARENTID))) {
			Product product = (Product) entityManagerDataSvc.getInstance().getReference(Product.class,  new Long((Integer) request.getParam(GlobalConstant.PARENTID)));
			ProductTeam productTeam = new ProductTeam(product,team);
			entityManagerDataSvc.getInstance().merge(productTeam);
		}
	}

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT DISTINCT x FROM Team AS x LEFT JOIN x.members as m WHERE m.userId =:userId ";
		
		boolean and = false;
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "x.active =:active ";
			and = true;
		}
		
		// search
		ArrayList<LinkedHashMap<String,String>> searchCriteria = null;
		if (request.containsParam(GlobalConstant.SEARCHCRITERIA) && !request.getParam(GlobalConstant.SEARCHCRITERIA).equals("")) {
			if (request.getParam(GlobalConstant.SEARCHCRITERIA) instanceof Map) {
				searchCriteria = new ArrayList<>();
				searchCriteria.add((LinkedHashMap<String, String>) request.getParam(GlobalConstant.SEARCHCRITERIA));
			} else {
				searchCriteria = (ArrayList<LinkedHashMap<String, String>>) request.getParam(GlobalConstant.SEARCHCRITERIA);
			}
			
			// Loop through all the criteria
			boolean or = false;
			
			String lookupStr = "";
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TEAM_TABLE_NAME")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.name LIKE :nameValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TEAM_TABLE_STATUS")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.active LIKE :statusValue"; 
						or = true;
					}
				}
			}
			if (!"".equals(lookupStr)) {
				if (!and) { 
					queryStr += " WHERE ( " + lookupStr + " ) ";
				} else {
					queryStr += " AND ( " + lookupStr + " ) ";
				}
			}
			
		}
		// order by
		ArrayList<LinkedHashMap<String,String>> orderCriteria = null;
		StringBuilder orderItems = new StringBuilder();
		if (request.containsParam(GlobalConstant.ORDERCRITERIA) && !request.getParam(GlobalConstant.ORDERCRITERIA).equals("")) {
			if (request.getParam(GlobalConstant.ORDERCRITERIA) instanceof Map) {
				orderCriteria = new ArrayList<>();
				orderCriteria.add((LinkedHashMap<String, String>) request.getParam(GlobalConstant.ORDERCRITERIA));
			} else {
				orderCriteria = (ArrayList<LinkedHashMap<String, String>>) request.getParam(GlobalConstant.ORDERCRITERIA);
			}
			
			// Loop through all the criteria
			boolean comma = false;
			
			for (LinkedHashMap<String,String> item : orderCriteria) {
				if (item.containsKey(GlobalConstant.ORDERCOLUMN) && item.containsKey(GlobalConstant.ORDERDIR)) {
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_TEAM_TABLE_NAME")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.name ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_TEAM_TABLE_STATUS")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.active ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
				}
			}
		}
		if (!"".equals(orderItems.toString())) {
			queryStr += " ORDER BY ".concat(orderItems.toString());
		} else {
			// default order
			queryStr += " ORDER BY x.name";
		}
		
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		query.setParameter("userId", (Long) request.getParam(GlobalConstant.USERREF));
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} 
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TEAM_TABLE_NAME")){
						query.setParameter("nameValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TEAM_TABLE_STATUS")){
						if ("active".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", true);
						} else if ("disabled".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", false);
						}
					}
				}
			}
		}
		if (request.containsParam(GlobalConstant.LISTLIMIT) && (Integer) request.getParam(GlobalConstant.LISTLIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.LISTSTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.LISTLIMIT));
		}
		@SuppressWarnings("unchecked")
		List<Team> teams = query.getResultList();

		response.addParam(GlobalConstant.ITEMS, teams);
		
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT COUNT(DISTINCT x) FROM Team as x LEFT JOIN x.members as m WHERE m.userId =:userId ";
		boolean and = false;
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "x.active =:active ";
			and = true;
		}
		
		ArrayList<LinkedHashMap<String,String>> searchCriteria = null;
		if (request.containsParam(GlobalConstant.SEARCHCRITERIA) && !request.getParam(GlobalConstant.SEARCHCRITERIA).equals("")) {
			if (request.getParam(GlobalConstant.SEARCHCRITERIA) instanceof Map) {
				searchCriteria = new ArrayList<>();
				searchCriteria.add((LinkedHashMap<String, String>) request.getParam(GlobalConstant.SEARCHCRITERIA));
			} else {
				searchCriteria = (ArrayList<LinkedHashMap<String, String>>) request.getParam(GlobalConstant.SEARCHCRITERIA);
			}
			
			// Loop through all the criteria
			boolean or = false;
			
			String lookupStr = "";
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TEAM_TABLE_NAME")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.name LIKE :nameValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TEAM_TABLE_STATUS")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.active LIKE :statusValue"; 
						or = true;
					}
				}
			}
			if (!"".equals(lookupStr)) {
				if (!and) { 
					queryStr += " WHERE ( " + lookupStr + " ) ";
				} else {
					queryStr += " AND ( " + lookupStr + " ) ";
				}
			}
			
		}

		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		query.setParameter("userId", (Long) request.getParam(GlobalConstant.USERREF));
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		}
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TEAM_TABLE_NAME")){
						query.setParameter("nameValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TEAM_TABLE_STATUS")){
						if ("active".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", true);
						} else if ("disabled".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", false);
						}
					}
				}
			}
		}
		
		Long count = (Long) query.getSingleResult();
		if (count == null){
			count = 0l;
		}
		response.addParam(GlobalConstant.ITEMCOUNT, count);
		
	}

	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			String queryStr = "SELECT x FROM Team AS x WHERE x.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
			query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
			Team team = (Team) query.getSingleResult();
			
			response.addParam(GlobalConstant.ITEM, team);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_MISSING_ID",prefCacheUtil.getLang(request)), response);
		}
	}


	@Override
	public void linkTeams(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.PARENTTYPE) && !"".equals(request.getParam(GlobalConstant.PARENTTYPE)) 
				&& request.containsParam(GlobalConstant.PARENTID) && !"".equals(request.getParam(GlobalConstant.PARENTID))) {
			if ("PRODUCT".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				String queryStr = "SELECT new ProductTeam(x.id, x.active, x.team.id) FROM ProductTeam AS x WHERE x.product.id =:id";
				Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
				query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.PARENTID)));
				List<ProductTeam> teams = query.getResultList();
				response.addParam("linkTeams", teams);
			} else if ("PROJECT".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				String queryStr = "SELECT new ProjectTeam(x.id, x.active, x.team.id) FROM ProjectTeam AS x WHERE x.project.id =:id";
				Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
				query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.PARENTID)));
				List<ProjectTeam> teams = query.getResultList();
				response.addParam("linkTeams", teams);
			} else if ("RELEASE".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				String queryStr = "SELECT new ReleaseTeam(x.id, x.active, x.team.id) FROM ReleaseTeam AS x WHERE x.release.id =:id";
				Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
				query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.PARENTID)));
				List<ReleaseTeam> teams = query.getResultList();
				response.addParam("linkTeams", teams);
			} else if ("BACKLOG".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				String queryStr = "SELECT new BacklogTeam(x.id, x.active, x.team.id) FROM BacklogTeam AS x WHERE x.backlog.id =:id";
				Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
				query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.PARENTID)));
				List<BacklogTeam> teams = query.getResultList();
				response.addParam("linkTeams", teams);
			} else {
				response.addParam("linkTeams", null);
			}
			
			
			
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

	@Override
	public void linkTeam(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.PARENTTYPE) && !"".equals(request.getParam(GlobalConstant.PARENTTYPE)) 
				&& request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			String queryStr = "";
			if ("PRODUCT".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				queryStr = "SELECT x FROM ProductTeam AS x WHERE x.id =:id";
				Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
				query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
				ProductTeam productTeam = (ProductTeam) query.getSingleResult();
				response.addParam(GlobalConstant.ITEM, productTeam);
			} else if ("PROJECT".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				queryStr = "SELECT x FROM ProjectTeam AS x WHERE x.id =:id";
				Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
				query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
				ProjectTeam projectTeam = (ProjectTeam) query.getSingleResult();
				response.addParam(GlobalConstant.ITEM, projectTeam);
			} else if ("BACKLOG".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				queryStr = "SELECT x FROM BacklogTeam AS x WHERE x.id =:id";
				Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
				query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
				BacklogTeam backlogTeam = (BacklogTeam) query.getSingleResult();
				response.addParam(GlobalConstant.ITEM, backlogTeam);
			} else if ("RELEASE".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				queryStr = "SELECT x FROM ReleaseTeam AS x WHERE x.id =:id";
				Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
				query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
				ReleaseTeam releaseTeam = (ReleaseTeam) query.getSingleResult();
				response.addParam(GlobalConstant.ITEM, releaseTeam);
			} else {
				response.addParam(GlobalConstant.ITEM, null);
			}
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_MISSING_ID",prefCacheUtil.getLang(request)), response);
		}
		
	}

	@Override
	public void linkTeamSave(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.PARENTTYPE) && !"".equals(request.getParam(GlobalConstant.PARENTTYPE)) 
				&& request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			if ("PRODUCT".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				ProductTeam productTeam = (ProductTeam) request.getParam(GlobalConstant.ITEM);
				if (request.containsParam(GlobalConstant.PARENTID) && !"".equals(request.getParam(GlobalConstant.PARENTID))) {
					Product product = (Product) entityManagerDataSvc.getInstance().getReference(Product.class,  new Long((Integer) request.getParam(GlobalConstant.PARENTID)));
					productTeam.setProduct(product);
				}
				if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
					Team team = (Team) entityManagerDataSvc.getInstance().getReference(Team.class,  new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
					productTeam.setTeam(team);
				}
				entityManagerDataSvc.getInstance().merge(productTeam);
			} else if ("PROJECT".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				ProjectTeam projectTeam = (ProjectTeam) request.getParam(GlobalConstant.ITEM);
				if (request.containsParam(GlobalConstant.PARENTID) && !"".equals(request.getParam(GlobalConstant.PARENTID))) {
					Project project = (Project) entityManagerDataSvc.getInstance().getReference(Project.class,  new Long((Integer) request.getParam(GlobalConstant.PARENTID)));
					projectTeam.setProject(project);
				}
				if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
					Team team = (Team) entityManagerDataSvc.getInstance().getReference(Team.class,  new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
					projectTeam.setTeam(team);
				}
				entityManagerDataSvc.getInstance().merge(projectTeam);
			} else if ("BACKLOG".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				BacklogTeam backlogTeam = (BacklogTeam) request.getParam(GlobalConstant.ITEM);
				if (request.containsParam(GlobalConstant.PARENTID) && !"".equals(request.getParam(GlobalConstant.PARENTID))) {
					Backlog backlog = (Backlog) entityManagerDataSvc.getInstance().getReference(Backlog.class,  new Long((Integer) request.getParam(GlobalConstant.PARENTID)));
					backlogTeam.setBacklog(backlog);
				}
				if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
					Team team = (Team) entityManagerDataSvc.getInstance().getReference(Team.class,  new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
					backlogTeam.setTeam(team);
				}
				entityManagerDataSvc.getInstance().merge(backlogTeam);
			} else if ("RELEASE".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				ReleaseTeam releaseTeam = (ReleaseTeam) request.getParam(GlobalConstant.ITEM);
				if (request.containsParam(GlobalConstant.PARENTID) && !"".equals(request.getParam(GlobalConstant.PARENTID))) {
					Release release = (Release) entityManagerDataSvc.getInstance().getReference(Release.class,  new Long((Integer) request.getParam(GlobalConstant.PARENTID)));
					releaseTeam.setRelease(release);
				}
				if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
					Team team = (Team) entityManagerDataSvc.getInstance().getReference(Team.class,  new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
					releaseTeam.setTeam(team);
				}
				entityManagerDataSvc.getInstance().merge(releaseTeam);
			}
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_MISSING_ID",prefCacheUtil.getLang(request)), response);
		}
		
	}

}
