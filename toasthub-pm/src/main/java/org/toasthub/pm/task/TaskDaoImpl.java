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

package org.toasthub.pm.task;


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
import org.toasthub.pm.model.PMConstant;
import org.toasthub.pm.model.Product;
import org.toasthub.pm.model.Project;
import org.toasthub.pm.model.Release;
import org.toasthub.pm.model.Sprint;
import org.toasthub.pm.model.Task;

@Repository("TaskDao")
@Transactional("TransactionManagerData")
public class TaskDaoImpl implements TaskDao {
	
	@Autowired
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;
	@Autowired
	PrefCacheUtil prefCacheUtil;
	
	@Override
	public void delete(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			
			Task task = (Task) entityManagerDataSvc.getInstance().getReference(Task.class,  new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
			entityManagerDataSvc.getInstance().remove(task);
			
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

	@Override
	public void save(RestRequest request, RestResponse response) throws Exception {
		Task task = (Task) request.getParam(GlobalConstant.ITEM);
		
		if (request.containsParam(PMConstant.PRODUCTID)) {
			Product product = (Product) entityManagerDataSvc.getInstance().getReference(Product.class,  new Long((Integer) request.getParam(PMConstant.PRODUCTID)));
			if (task.getProduct() == null || task.getProduct() != null && !task.getProduct().getId().equals(new Long((Integer) request.getParam(PMConstant.PRODUCTID)))) {
				task.setProduct(product);
			}
		} else if (request.containsParam(PMConstant.PROJECTID)) {
			Project project = (Project) entityManagerDataSvc.getInstance().getReference(Project.class,  new Long((Integer) request.getParam(PMConstant.PROJECTID)));
			if (task.getProject() == null || task.getProject() != null && !task.getProject().getId().equals(new Long((Integer) request.getParam(PMConstant.PROJECTID)))) {
				task.setProject(project);
			}
		} else if (request.containsParam(PMConstant.RELEASEID)) {
			Release release = (Release) entityManagerDataSvc.getInstance().getReference(Release.class,  new Long((Integer) request.getParam(PMConstant.RELEASEID)));
			if (task.getRelease() == null || task.getRelease() != null && !task.getRelease().getId().equals(new Long((Integer) request.getParam(PMConstant.RELEASEID)))) {
				task.setRelease(release);
			}
		} else if (request.containsParam(PMConstant.BACKLOGID)) {
			Backlog backlog = (Backlog) entityManagerDataSvc.getInstance().getReference(Backlog.class,  new Long((Integer) request.getParam(PMConstant.BACKLOGID)));
			if (task.getBacklog() == null || task.getBacklog() != null && !task.getBacklog().getId().equals(new Long((Integer) request.getParam(PMConstant.BACKLOGID)))) {
				task.setBacklog(backlog);
			}
		} else if (request.containsParam(PMConstant.SPRINTID)) {
			Sprint sprint = (Sprint) entityManagerDataSvc.getInstance().getReference(Sprint.class,  new Long((Integer) request.getParam(PMConstant.SPRINTID)));
			if (task.getSprint() == null || task.getSprint() != null && !task.getSprint().getId().equals(new Long((Integer) request.getParam(PMConstant.SPRINTID)))) {
				task.setSprint(sprint);
			}
		}
		entityManagerDataSvc.getInstance().merge(task);
	}

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT DISTINCT x FROM Task AS x ";
		
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
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_SUMMARY")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.summary LIKE :summaryValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_PRODUCT")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.product.name LIKE :productValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_PROJECT")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.project.name LIKE :projectValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_STARTDATE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.startDate LIKE :startDateValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_ENDDATE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.endDate LIKE :endDateValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_STATUS")){
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
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_TASK_TABLE_SUMMARY")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.summary ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_TASK_TABLE_PRODUCT")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.product.name ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_TASK_TABLE_PROJECT")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.project.name ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_TASK_TABLE_STARTDATE")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.startDate ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_TASK_TABLE_ENDDATE")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.endDate ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_TASK_TABLE_STATUS")){
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
			queryStr += " ORDER BY x.id DESC";
		}
		
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		}
		if (request.containsParam(PMConstant.PRODUCTID)) {
			query.setParameter("productId", new Long((Integer) request.getParam(PMConstant.PRODUCTID)));
		} else if (request.containsParam(PMConstant.PROJECTID)) {
			query.setParameter("projectId", new Long((Integer) request.getParam(PMConstant.PROJECTID)));
		} else if (request.containsParam(PMConstant.RELEASEID)) {
			query.setParameter("releaseId", new Long((Integer) request.getParam(PMConstant.RELEASEID)));
		} else if (request.containsParam(PMConstant.BACKLOGID)) {
			query.setParameter("backlogId", new Long((Integer) request.getParam(PMConstant.BACKLOGID)));
		} else if (request.containsParam(PMConstant.SPRINTID)) {
			query.setParameter("sprintId", new Long((Integer) request.getParam(PMConstant.SPRINTID)));
		}
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_NAME")){
						query.setParameter("nameValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_PRODUCT")){
						query.setParameter("productValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_PROJECT")){
						query.setParameter("projectValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_STARTDATE")){
						query.setParameter("startDateValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_ENDDATE")){
						query.setParameter("endDateValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_STATUS")){
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
		List<Task> results = query.getResultList();

		response.addParam(GlobalConstant.ITEMS, results);
		
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT COUNT(DISTINCT x) FROM Task as x ";
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
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_SUMMARY")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.summary LIKE :summaryValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_PRODUCT")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.product.name LIKE :productValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_PROJECT")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.project.name LIKE :projectValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_STARTDATE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.startDate LIKE :startDateValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_ENDDATE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.endDate LIKE :endDateValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_STATUS")){
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
		} 
		
		if (request.containsParam(PMConstant.PRODUCTID)) {
			query.setParameter("productId", new Long((Integer) request.getParam(PMConstant.PRODUCTID)));
		} else if (request.containsParam(PMConstant.PROJECTID)) {
			query.setParameter("projectId", new Long((Integer) request.getParam(PMConstant.PROJECTID)));
		} else if (request.containsParam(PMConstant.RELEASEID)) {
			query.setParameter("releaseId", new Long((Integer) request.getParam(PMConstant.RELEASEID)));
		} else if (request.containsParam(PMConstant.BACKLOGID)) {
			query.setParameter("backlogId", new Long((Integer) request.getParam(PMConstant.BACKLOGID)));
		} else if (request.containsParam(PMConstant.SPRINTID)) {
			query.setParameter("sprintId", new Long((Integer) request.getParam(PMConstant.SPRINTID)));
		}
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_NAME")){
						query.setParameter("nameValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_PRODUCT")){
						query.setParameter("productValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_PROJECT")){
						query.setParameter("projectValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_STARTDATE")){
						query.setParameter("startDateValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_ENDDATE")){
						query.setParameter("endDateValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TASK_TABLE_STATUS")){
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
			String queryStr = "SELECT x FROM Task AS x WHERE x.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
			query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
			Task task = (Task) query.getSingleResult();
			
			response.addParam(GlobalConstant.ITEM, task);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_MISSING_ID",prefCacheUtil.getLang(request)), response);
		}
	}

}
