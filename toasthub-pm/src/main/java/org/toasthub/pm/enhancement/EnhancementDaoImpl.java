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

package org.toasthub.pm.enhancement;


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
import org.toasthub.pm.model.Backlog;
import org.toasthub.pm.model.Enhancement;
import org.toasthub.pm.model.PMConstant;
import org.toasthub.pm.model.Product;
import org.toasthub.pm.model.Project;
import org.toasthub.pm.model.Release;
import org.toasthub.pm.model.Sprint;

@Repository("PMEnhancementDao")
@Transactional("TransactionManagerData")
public class EnhancementDaoImpl implements EnhancementDao {
	
	@Autowired
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;
	@Autowired
	PrefCacheUtil prefCacheUtil;
	
	@Override
	public void delete(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			
			Enhancement enhancement = (Enhancement) entityManagerDataSvc.getInstance().getReference(Enhancement.class,  Long.valueOf((Integer) request.getParam(GlobalConstant.ITEMID)));
			entityManagerDataSvc.getInstance().remove(enhancement);
			
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

	@Override
	public void save(RestRequest request, RestResponse response) throws Exception {
		Enhancement enhancement = (Enhancement) request.getParam(GlobalConstant.ITEM);
		
		if (request.containsParam(PMConstant.PRODUCTID)) {
			Product product = (Product) entityManagerDataSvc.getInstance().getReference(Product.class,  Long.valueOf((Integer) request.getParam(PMConstant.PRODUCTID)));
			if (enhancement.getProduct() == null || enhancement.getProduct() != null && !enhancement.getProduct().getId().equals(Long.valueOf((Integer) request.getParam(PMConstant.PRODUCTID)))) {
				enhancement.setProduct(product);
			}
		} else if (request.containsParam(PMConstant.PROJECTID)) {
			Project project = (Project) entityManagerDataSvc.getInstance().getReference(Project.class,  Long.valueOf((Integer) request.getParam(PMConstant.PROJECTID)));
			if (enhancement.getProject() == null || enhancement.getProject() != null && !enhancement.getProject().getId().equals(Long.valueOf((Integer) request.getParam(PMConstant.PROJECTID)))) {
				enhancement.setProject(project);
			}
		} else if (request.containsParam(PMConstant.RELEASEID)) {
			Release release = (Release) entityManagerDataSvc.getInstance().getReference(Release.class,  Long.valueOf((Integer) request.getParam(PMConstant.RELEASEID)));
			if (enhancement.getRelease() == null || enhancement.getRelease() != null && !enhancement.getRelease().getId().equals(Long.valueOf((Integer) request.getParam(PMConstant.RELEASEID)))) {
				enhancement.setRelease(release);
			}
		} else if (request.containsParam(PMConstant.BACKLOGID)) {
			Backlog backlog = (Backlog) entityManagerDataSvc.getInstance().getReference(Backlog.class,  Long.valueOf((Integer) request.getParam(PMConstant.BACKLOGID)));
			if (enhancement.getBacklog() == null || enhancement.getBacklog() != null && !enhancement.getBacklog().getId().equals(Long.valueOf((Integer) request.getParam(PMConstant.BACKLOGID)))) {
				enhancement.setBacklog(backlog);
			}
		} else if (request.containsParam(PMConstant.SPRINTID)) {
			Sprint sprint = (Sprint) entityManagerDataSvc.getInstance().getReference(Sprint.class,  Long.valueOf((Integer) request.getParam(PMConstant.SPRINTID)));
			if (enhancement.getSprint() == null || enhancement.getSprint() != null && !enhancement.getSprint().getId().equals(Long.valueOf((Integer) request.getParam(PMConstant.SPRINTID)))) {
				enhancement.setSprint(sprint);
			}
		}
		entityManagerDataSvc.getInstance().merge(enhancement);
	}

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT DISTINCT x FROM Enhancement AS x ";
		
		boolean and = false;
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "x.active =:active ";
			and = true;
		}
		
		if (request.containsParam(PMConstant.PRODUCTID)) {
			if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
			queryStr += "x.product.id =:productId ";
			and = true;
		} else if (request.containsParam(PMConstant.PROJECTID)) {
			if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
			queryStr += "x.project.id =:projectId ";
			and = true;
		} else if (request.containsParam(PMConstant.RELEASEID)) {
			if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
			queryStr += "x.release.id =:releaseId ";
			and = true;
		} else if (request.containsParam(PMConstant.BACKLOGID)) {
			if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
			queryStr += "x.backlog.id =:backlogId ";
			and = true;
		} else if (request.containsParam(PMConstant.SPRINTID)) {
			if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
			queryStr += "x.sprint.id =:sprintId ";
			and = true;
		} else {
		//	if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
		//	queryStr += "x.product IS NULL AND x.project IS NULL AND x.release IS NULL AND x.backlog IS NULL AND x.sprint IS NULL ";
		//	and = true;
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
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_SUMMARY")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.summary LIKE :summaryValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_ID")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.id LIKE :idValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_SEVERITY")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.severity LIKE :severityValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_PRIORITY")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.priority LIKE :priorityValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_STATUS")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.status LIKE :statusValue"; 
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
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_ENHANCEMENT_TABLE_SUMMARY")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.summary ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_ENHANCEMENT_TABLE_ID")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.id ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_ENHANCEMENT_TABLE_SEVERITY")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.severity ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_ENHANCEMENT_TABLE_PRIORITY")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.priority ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_ENHANCEMENT_TABLE_STATUS")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.status ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
				}
			}
		}
		if (!"".equals(orderItems.toString())) {
			queryStr += " ORDER BY ".concat(orderItems.toString());
		} else {
			// default order
			queryStr += " ORDER BY x.id DESC";
		}
		
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} 
		if (request.containsParam(PMConstant.PRODUCTID)) {
			query.setParameter("productId", Long.valueOf((Integer) request.getParam(PMConstant.PRODUCTID)));
		} else if (request.containsParam(PMConstant.PROJECTID)) {
			query.setParameter("projectId", Long.valueOf((Integer) request.getParam(PMConstant.PROJECTID)));
		} else if (request.containsParam(PMConstant.RELEASEID)) {
			query.setParameter("releaseId", Long.valueOf((Integer) request.getParam(PMConstant.RELEASEID)));
		} else if (request.containsParam(PMConstant.BACKLOGID)) {
			query.setParameter("backlogId", Long.valueOf((Integer) request.getParam(PMConstant.BACKLOGID)));
		} else if (request.containsParam(PMConstant.SPRINTID)) {
			query.setParameter("sprintId", Long.valueOf((Integer) request.getParam(PMConstant.SPRINTID)));
		}
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_SUMMARY")){
						query.setParameter("summaryValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_ID")){
						String val = (String) item.get(GlobalConstant.SEARCHVALUE);
						String[] valParts = val.split("-");
						query.setParameter("idValue", "%" + valParts[1] + "%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_SEVERITY")){
						query.setParameter("severityValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_PRIORITY")){
						query.setParameter("priorityValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_STATUS")){
						query.setParameter("statusValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
				}
			}
		}
		if (request.containsParam(GlobalConstant.LISTLIMIT) && (Integer) request.getParam(GlobalConstant.LISTLIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.LISTSTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.LISTLIMIT));
		}
		@SuppressWarnings("unchecked")
		List<Enhancement> enhancement = query.getResultList();

		response.addParam(GlobalConstant.ITEMS, enhancement);
		
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT COUNT(DISTINCT x) FROM Enhancement as x ";
		boolean and = false;
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "x.active =:active ";
			and = true;
		}
		
		if (request.containsParam(PMConstant.PRODUCTID)) {
			if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
			queryStr += "x.product.id =:productId ";
			and = true;
		} else if (request.containsParam(PMConstant.PROJECTID)) {
			if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
			queryStr += "x.project.id =:projectId ";
			and = true;
		} else if (request.containsParam(PMConstant.RELEASEID)) {
			if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
			queryStr += "x.release.id =:releaseId ";
			and = true;
		} else if (request.containsParam(PMConstant.BACKLOGID)) {
			if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
			queryStr += "x.backlog.id =:backlogId ";
			and = true;
		} else if (request.containsParam(PMConstant.SPRINTID)) {
			if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
			queryStr += "x.sprint.id =:sprintId ";
			and = true;
		} else {
		//	if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
		//	queryStr += "x.product IS NULL AND x.project IS NULL AND x.release IS NULL AND x.backlog IS NULL AND x.sprint IS NULL ";
		//	and = true;
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
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_SUMMARY")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.summary LIKE :summaryValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_ID")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.id LIKE :idValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_SEVERITY")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.severity LIKE :severityValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_PRIORITY")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.priority LIKE :priorityValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_STATUS")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.status LIKE :statusValue"; 
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
		} 
		if (request.containsParam(PMConstant.PRODUCTID)) {
			query.setParameter("productId", Long.valueOf((Integer) request.getParam(PMConstant.PRODUCTID)));
		} else if (request.containsParam(PMConstant.PROJECTID)) {
			query.setParameter("projectId", Long.valueOf((Integer) request.getParam(PMConstant.PROJECTID)));
		} else if (request.containsParam(PMConstant.RELEASEID)) {
			query.setParameter("releaseId", Long.valueOf((Integer) request.getParam(PMConstant.RELEASEID)));
		} else if (request.containsParam(PMConstant.BACKLOGID)) {
			query.setParameter("backlogId", Long.valueOf((Integer) request.getParam(PMConstant.BACKLOGID)));
		} else if (request.containsParam(PMConstant.SPRINTID)) {
			query.setParameter("sprintId", Long.valueOf((Integer) request.getParam(PMConstant.SPRINTID)));
		}
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_SUMMARY")){
						query.setParameter("summaryValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_ID")){
						String val = (String) item.get(GlobalConstant.SEARCHVALUE);
						String[] valParts = val.split("-");
						query.setParameter("idValue", "%" + valParts[1] + "%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_SEVERITY")){
						query.setParameter("severityValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_PRIORITY")){
						query.setParameter("priorityValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_ENHANCEMENT_TABLE_STATUS")){
						query.setParameter("statusValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
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
			String queryStr = "SELECT x FROM Enhancement AS x WHERE x.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
			query.setParameter("id", Long.valueOf((Integer) request.getParam(GlobalConstant.ITEMID)));
			Enhancement enhancement = (Enhancement) query.getSingleResult();
			
			response.addParam(GlobalConstant.ITEM, enhancement);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_MISSING_ID",prefCacheUtil.getLang(request)), response);
		}
	}

}
