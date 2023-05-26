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

package org.toasthub.pm.role;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.pm.model.Member;
import org.toasthub.pm.model.MemberRole;
import org.toasthub.pm.model.PMConstant;
import org.toasthub.pm.model.Role;
import org.toasthub.pm.model.Team;

@Repository("PMRoleDao")
@Transactional("TransactionManagerData")
public class RoleDaoImpl implements RoleDao {
	
	@Autowired
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;
	
	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {

		if ( !(request.containsParam(PMConstant.PARENTID) && !"".equals(request.getParam(PMConstant.PARENTID))) ) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing Parent ID", response);
			return;
		}
		
		if ( !(request.containsParam(PMConstant.PARENTTYPE) && !"".equals(request.getParam(PMConstant.PARENTTYPE))) ) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing Parent Type", response);
			return;
		}
		
		String queryStr = "SELECT DISTINCT x FROM Role as x WHERE x.team.id =: parentId ";
		
		
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
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ROLE_TABLE_NAME")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.name LIKE :nameValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ROLE_TABLE_CODE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.code LIKE :codeValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ROLE_TABLE_STATUS")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.active LIKE :statusValue"; 
						or = true;
					}
				}
			}
			if (!"".equals(lookupStr)) {
				queryStr += " AND ( " + lookupStr + " ) ";
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
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_ROLE_TABLE_NAME")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.name ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_ROLE_TABLE_CODE")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.code ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_ROLE_TABLE_STATUS")){
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
		
		Long parentVal = request.getParamLong(PMConstant.PARENTID);
		if ("MEMBER".equals(request.getParam(PMConstant.PARENTTYPE))) {
			parentVal = request.getParamLong(PMConstant.TEAMID);
		}
		
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr).setParameter("parentId", parentVal);
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} 
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ROLE_TABLE_NAME")){
						query.setParameter("nameValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ROLE_TABLE_CODE")){
						query.setParameter("codeValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ROLE_TABLE_STATUS")){
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
		List<Role> roles = query.getResultList();

		response.addParam(GlobalConstant.ITEMS, roles);
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		
		if ( !(request.containsParam(PMConstant.PARENTID) && !"".equals(request.getParam(PMConstant.PARENTID))) ) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing Parent ID", response);
			return;
		}
		
		if ( !(request.containsParam(PMConstant.PARENTTYPE) && !"".equals(request.getParam(PMConstant.PARENTTYPE))) ) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing Parent Type", response);
			return;
		}
		
		String queryStr  = "SELECT COUNT(DISTINCT x) FROM Role as x WHERE x.team.id =: parentId ";
		
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
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ROLE_TABLE_NAME")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.name LIKE :nameValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ROLE_TABLE_CODE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.code LIKE :codeValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ROLE_TABLE_STATUS")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.active LIKE :statusValue"; 
						or = true;
					}
				}
			}
			if (!"".equals(lookupStr)) {
				queryStr += " AND ( " + lookupStr + " ) ";
			}
			
		}

		Long parentVal = request.getParamLong(PMConstant.PARENTID);
		if ("MEMBER".equals(request.getParam(PMConstant.PARENTTYPE))) {
			parentVal = request.getParamLong(PMConstant.TEAMID);
		}
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr).setParameter("parentId", parentVal);
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ROLE_TABLE_NAME")){
						query.setParameter("nameValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ROLE_TABLE_CODE")){
						query.setParameter("codeValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ROLE_TABLE_STATUS")){
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
			String queryStr = "SELECT x FROM Role AS x WHERE x.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
			query.setParameter("id", request.getParamLong(GlobalConstant.ITEMID));
			Role role = (Role) query.getSingleResult();
			
			response.addParam(GlobalConstant.ITEM, role);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

	@Override
	public void delete(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			
			Role role = (Role) entityManagerDataSvc.getInstance().getReference(Role.class,  request.getParamLong(GlobalConstant.ITEMID));
			entityManagerDataSvc.getInstance().remove(role);
			
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

	@Override
	public void save(RestRequest request, RestResponse response) throws Exception {
		Role role = (Role) request.getParam(GlobalConstant.ITEM);
		if (request.containsParam(GlobalConstant.PARENTID) && !"".equals(request.getParam(GlobalConstant.PARENTID))) {
			if (request.containsParam(GlobalConstant.PARENTTYPE) && "TEAM".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				Team team = (Team) entityManagerDataSvc.getInstance().getReference(Team.class,  request.getParamLong(GlobalConstant.PARENTID));
				role.setTeam(team);
			}
		}
		entityManagerDataSvc.getInstance().merge(role);
	}

	
	@Override
	public void memberRoles(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.PARENTID) && !"".equals(request.getParam(GlobalConstant.PARENTID))) {
			String queryStr = "SELECT new MemberRole(x.id, x.active, x.locked, x.sortOrder, x.startDate, x.endDate, x.role.id) FROM MemberRole AS x WHERE x.member.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
			query.setParameter("id", request.getParamLong(GlobalConstant.PARENTID));
			List<MemberRole> roles = query.getResultList();
			
			response.addParam("memberRoles", roles);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
		
	}
	
	@Override
	public void memberRole(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			String queryStr = "SELECT r FROM MemberRole AS r WHERE r.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
			query.setParameter("id", request.getParamLong(GlobalConstant.ITEMID));
			MemberRole memberRole = (MemberRole) query.getSingleResult();
			
			response.addParam(GlobalConstant.ITEM, memberRole);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
		
	}

	@Override
	public void memberRoleSave(RestRequest request, RestResponse response) throws Exception {
		MemberRole memberRole = (MemberRole) request.getParam(GlobalConstant.ITEM);
		
		if (memberRole.getRole() == null) {
			Role role = (Role) entityManagerDataSvc.getInstance().getReference(Role.class,  request.getParamLong("roleId"));
			memberRole.setRole(role);
		}
		if (memberRole.getMember() == null) {
			Member member = (Member) entityManagerDataSvc.getInstance().getReference(Member.class,  request.getParamLong("memberId"));
			memberRole.setMember(member);
		}
		
		entityManagerDataSvc.getInstance().merge(memberRole);
	}

	@Override
	public void teamRole(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			String queryStr = "SELECT x FROM Role AS x WHERE x.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
			query.setParameter("id", request.getParamLong(GlobalConstant.ITEMID));
			Role role = (Role) query.getSingleResult();
			
			response.addParam(GlobalConstant.ITEM, role);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
		
	}

}
