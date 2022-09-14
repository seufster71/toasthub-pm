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

package org.toasthub.pm.project;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.PrefCacheUtil;
import org.toasthub.pm.model.PMConstant;
import org.toasthub.pm.model.Product;
import org.toasthub.pm.model.Project;

@Repository("PMProjectDao")
@Transactional("TransactionManagerData")
public class ProjectDaoImpl implements ProjectDao {
	
	@Autowired
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;
	@Autowired
	PrefCacheUtil prefCacheUtil;
	
	@Override
	public void delete(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			
			Project project = (Project) entityManagerDataSvc.getInstance().getReference(Project.class,  request.getParamLong(GlobalConstant.ITEMID));
			entityManagerDataSvc.getInstance().remove(project);
			
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

	@Override
	public void save(RestRequest request, RestResponse response) throws Exception {
		Project project = (Project) request.getParam(GlobalConstant.ITEM);
		
		if (request.containsParam(PMConstant.PRODUCTID)) {
			Product product = (Product) entityManagerDataSvc.getInstance().getReference(Product.class,  request.getParamLong(PMConstant.PRODUCTID));
			if (project.getProduct() == null || project.getProduct() != null && !project.getProduct().getId().equals(request.getParamLong(PMConstant.PRODUCTID))) {
				project.setProduct(product);
			}
		}
		entityManagerDataSvc.getInstance().merge(project);
	}

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT DISTINCT x FROM Project AS x WHERE x.active = :active AND ";
		boolean and = true;
		 
		if (request.containsParam(PMConstant.PRODUCTID)) {
			queryStr += "x.product.id = :productId ";
		} else {
			queryStr += "x.product IS NULL AND (x.userId = :userId OR x.id IN (SELECT dt.project.id FROM ProjectTeam AS dt WHERE dt.team.id IN (SELECT DISTINCT t.id FROM Team AS t LEFT JOIN t.members as m WHERE m.userId = :userId))) ";
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
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_NAME")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.name LIKE :nameValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_PRODUCT")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.product.name LIKE :productValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_STARTDATE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.startDate LIKE :startDateValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_ENDDATE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.endDate LIKE :endDateValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_STATUS")){
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
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_PROJECT_TABLE_NAME")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.name ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_PROJECT_TABLE_PRODUCT")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.product.name ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_PROJECT_TABLE_STARTDATE")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.startDate ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_PROJECT_TABLE_ENDDATE")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.endDate ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_PROJECT_TABLE_STATUS")){
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
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} else {
			query.setParameter("active", true);
		}
		
		if (request.containsParam(PMConstant.PRODUCTID)) {
			query.setParameter("productId", request.getParamLong(PMConstant.PRODUCTID));
		} else {
			query.setParameter("userId", request.getParamLong(PMConstant.USERID));
		}
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_NAME")){
						query.setParameter("nameValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_PRODUCT")){
						query.setParameter("productValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_STARTDATE")){
						query.setParameter("startDateValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_ENDDATE")){
						query.setParameter("endDateValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_STATUS")){
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
		List<Project> projects = query.getResultList();
		
		
		// check to see if it can be shared
		for (Project project : projects) {
			if (project.getProduct() != null) {
				queryStr = "SELECT COUNT(x) FROM ProductTeam AS x WHERE x.product.id = :id ";
				query = entityManagerDataSvc.getInstance().createQuery(queryStr);
				query.setParameter("id", project.getProduct().getId());
				Long count = (Long) query.getSingleResult();
				if (count == null){
					count = 0l;
				}
				if (count > 0) {
					project.setAllowShare(false);
				}
			}
		}

		response.addParam(GlobalConstant.ITEMS, projects);
		
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT COUNT(DISTINCT x) FROM Project AS x WHERE x.active = :active AND ";
		boolean and = true;
		if (request.containsParam(PMConstant.PRODUCTID)) {
			queryStr += "x.product.id = :productId ";
		} else {
			queryStr += "x.product IS NULL AND (x.userId = :userId OR x.id IN (SELECT dt.project.id FROM ProjectTeam AS dt WHERE dt.team.id IN (SELECT DISTINCT t.id FROM Team AS t LEFT JOIN t.members as m WHERE m.userId = :userId))) ";
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
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_NAME")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.name LIKE :nameValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_PRODUCT")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.product.name LIKE :projectValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_STARTDATE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.startDate LIKE :startDateValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_ENDDATE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.endDate LIKE :endDateValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_STATUS")){
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
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} else {
			query.setParameter("active", true);
		}
		
		if (request.containsParam(PMConstant.PRODUCTID)) {
			query.setParameter("productId", request.getParamLong(PMConstant.PRODUCTID));
		} else {
			query.setParameter("userId", request.getParamLong(PMConstant.USERID));
		}
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_NAME")){
						query.setParameter("nameValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_PRODUCT")){
						query.setParameter("productValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_STARTDATE")){
						query.setParameter("startDateValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_ENDDATE")){
						query.setParameter("endDateValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_PROJECT_TABLE_STATUS")){
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
			String queryStr = "SELECT x FROM Project AS x WHERE x.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
			query.setParameter("id", request.getParamLong(GlobalConstant.ITEMID));
			Project project = (Project) query.getSingleResult();
			
			response.addParam(GlobalConstant.ITEM, project);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_MISSING_ID",prefCacheUtil.getLang(request)), response);
		}
	}

}
