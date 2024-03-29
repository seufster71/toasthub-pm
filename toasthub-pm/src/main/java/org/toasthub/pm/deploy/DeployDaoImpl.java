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

package org.toasthub.pm.deploy;

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
import org.toasthub.pm.model.Deploy;
import org.toasthub.pm.model.DeployPipeline;
import org.toasthub.pm.model.DeploySystem;
import org.toasthub.pm.model.PMConstant;


@Repository("PMDeployDao")
@Transactional("TransactionManagerData")
public class DeployDaoImpl implements DeployDao {
	
	@Autowired
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;
	@Autowired
	PrefCacheUtil prefCacheUtil;
	
	
	@Override
	public void delete(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			String action = (String) request.getParams().get(GlobalConstant.ACTION);
			if (action.contains("PIPELINE")) {
				DeployPipeline deployPipeline = (DeployPipeline) entityManagerDataSvc.getInstance().getReference(DeployPipeline.class, request.getParamLong(GlobalConstant.ITEMID));
				entityManagerDataSvc.getInstance().remove(deployPipeline);
			} else if (action.contains("SYSTEM")) {
				DeploySystem deploySystem = (DeploySystem) entityManagerDataSvc.getInstance().getReference(DeploySystem.class, request.getParamLong(GlobalConstant.ITEMID));
				entityManagerDataSvc.getInstance().remove(deploySystem);
			} else {
				Deploy deploy = (Deploy) entityManagerDataSvc.getInstance().getReference(Deploy.class, request.getParamLong(GlobalConstant.ITEMID));
				entityManagerDataSvc.getInstance().remove(deploy);
			}
			
			
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

	@Override
	public void save(RestRequest request, RestResponse response) throws Exception {
		String action = (String) request.getParams().get(GlobalConstant.ACTION);
		if (action.contains("PIPELINE")) {
			if (request.containsParam(PMConstant.DEPLOYID)) {
				DeployPipeline deployPipeline = (DeployPipeline) request.getParam(GlobalConstant.ITEM);
				if (deployPipeline.getSortOrder() == null) {
					// get highest order
					Object max = entityManagerDataSvc.getInstance().createQuery("SELECT max(x.sortOrder) FROM DeployPipeline AS x WHERE x.deploy.id =: parentId").setParameter("parentId", request.getParamLong(PMConstant.DEPLOYID)).getSingleResult();
					if (max != null) {
						int order = (int) max + 1;
						deployPipeline.setSortOrder(order);
					} else {
						deployPipeline.setSortOrder(1);
					}
				}
				Deploy deploy = (Deploy) entityManagerDataSvc.getInstance().getReference(Deploy.class, request.getParamLong(PMConstant.DEPLOYID));
				if (deployPipeline.getDeploy() == null || deployPipeline.getDeploy() != null && !deployPipeline.getDeploy().getId().equals(request.getParamLong(PMConstant.DEPLOYID))) {
					deployPipeline.setDeploy(deploy);
				}
				entityManagerDataSvc.getInstance().merge(deployPipeline);
			} else {
				utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_MISSING_ID",prefCacheUtil.getLang(request)), response);
			}
		} else if (action.contains("SYSTEM")) {
			if (request.containsParam(PMConstant.DEPLOYID)) {
				DeploySystem deploySystem = (DeploySystem) request.getParam(GlobalConstant.ITEM);
				
				Deploy deploy = (Deploy) entityManagerDataSvc.getInstance().getReference(Deploy.class, request.getParamLong(PMConstant.DEPLOYID));
				if (deploySystem.getDeploy() == null || deploySystem.getDeploy() != null && !deploySystem.getDeploy().getId().equals(request.getParamLong(PMConstant.DEPLOYID))) {
					deploySystem.setDeploy(deploy);
				}
				entityManagerDataSvc.getInstance().merge(deploySystem);
			} else {
				utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_MISSING_ID",prefCacheUtil.getLang(request)), response);
			}
		} else {
			Deploy deploy = (Deploy) request.getParam(GlobalConstant.ITEM);
			entityManagerDataSvc.getInstance().merge(deploy);
		}
	}

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		String action = (String) request.getParams().get(GlobalConstant.ACTION);
		
		boolean and = true;
		
		String queryStr = "";
		if (action.contains("PIPELINE")) {
			queryStr = "SELECT NEW DeployPipeline(x.id,x.name,x.sortOrder,x.scmURL,x.branch,x.active,x.archive,x.locked,x.created,x.modified) FROM DeployPipeline AS x WHERE x.deploy.id = :deployId ";
		} else if (action.contains("SYSTEM")) {
			queryStr = "SELECT NEW DeploySystem(x.id,x.serverName,x.sshUsername,x.stagingDir,x.active,x.archive,x.locked,x.created,x.modified) FROM DeploySystem AS x WHERE x.deploy.id = :deployId ";
		} else {
			queryStr = "SELECT NEW Deploy(x.id,x.name,x.lastSuccess,x.lastFail,x.lastDuration,x.runStatus,x.active,x.archive,x.locked,x.created,x.modified) FROM Deploy as x WHERE x.active = :active AND ";
			if (request.containsParam(PMConstant.PRODUCTID)) {
				queryStr += "x.product.id = :productId ";
			} else if (request.containsParam(PMConstant.PROJECTID)) {
				queryStr += "x.project.id = :projectId ";
			} else {
				queryStr += "x.product IS NULL AND x.project IS NULL AND (x.userId = :userId OR x.id IN (SELECT dt.deploy.id FROM DeployTeam AS dt WHERE dt.team.id IN (SELECT DISTINCT t.id FROM Team AS t LEFT JOIN t.members as m WHERE m.userId = :userId))) ";
			}
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
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_TABLE_NAME") || item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_PIPELINE_TABLE_NAME") ){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.name LIKE :nameValue"; 
						or = true;
					} else if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_SYSTEM_TABLE_NAME")) {
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.serverName LIKE :nameValue"; 
						or = true;
					}
					if ( item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_TABLE_STATUS") || item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_PIPELINE_TABLE_STATUS") ||
							item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_SYSTEM_TABLE_STATUS") ){
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
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_DEPLOY_TABLE_NAME") || item.get(GlobalConstant.ORDERCOLUMN).equals("PM_DEPLOY_PIPELINE_TABLE_NAME")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.name ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					} else if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_DEPLOY_SYSTEM_TABLE_NAME")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.serverName ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}	
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_DEPLOY_TABLE_STATUS") || item.get(GlobalConstant.ORDERCOLUMN).equals("PM_DEPLOY_PIPELINE_TABLE_STATUS") ||
							item.get(GlobalConstant.ORDERCOLUMN).equals("PM_DEPLOY_SYSTEM_TABLE_STATUS")){
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
			if (action.contains("SYSTEM")) {
				queryStr += " ORDER BY x.serverName";
			} else {
				queryStr += " ORDER BY x.name";
			}
		}
		
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		}  else {
			query.setParameter("active", true);
		}
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) { 
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_TABLE_NAME") || item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_PIPELINE_TABLE_NAME") |
							item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_SYSTEM_TABLE_NAME")){
						query.setParameter("nameValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					} 
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_TABLE_STATUS") || item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_PIPELINE_TABLE_STATUS") ||
							item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_SYSTEM_TABLE_STATUS")){
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
		
		
		if (action.contains("PIPELINE")) {
			if (request.containsParam(PMConstant.DEPLOYID)){
				query.setParameter("deployId", request.getParamLong(PMConstant.DEPLOYID));
			}
			@SuppressWarnings("unchecked")
			List<DeployPipeline> deployPipeline = query.getResultList();
			response.addParam(GlobalConstant.ITEMS, deployPipeline);
		} else if (action.contains("SYSTEM")) {
			if (request.containsParam(PMConstant.DEPLOYID)){
				query.setParameter("deployId", request.getParamLong(PMConstant.DEPLOYID));
			}
			@SuppressWarnings("unchecked")
			List<DeploySystem> deploySystem = query.getResultList();
			response.addParam(GlobalConstant.ITEMS, deploySystem);
		} else {
			query.setParameter("userId", request.getParamLong(PMConstant.USERID));
			@SuppressWarnings("unchecked")
			List<Deploy> deploy = query.getResultList();
			response.addParam(GlobalConstant.ITEMS, deploy);
		}
		
		
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String action = (String) request.getParams().get(GlobalConstant.ACTION);

		boolean and = true;
		
		String queryStr = "";

		if (action.contains("PIPELINE")) {
			queryStr = "SELECT COUNT(DISTINCT x) FROM DeployPipeline as x WHERE x.deploy.id = :deployId ";
		} else if (action.contains("SYSTEM")) {
			queryStr = "SELECT COUNT(DISTINCT x) FROM DeploySystem as x WHERE x.deploy.id = :deployId ";
		} else {
			queryStr = "SELECT COUNT(DISTINCT x) FROM Deploy as x WHERE x.active = :active AND ";
			if (request.containsParam(PMConstant.PRODUCTID)) {
				queryStr += "x.product.id = :productId ";
			} else if (request.containsParam(PMConstant.PROJECTID)) {
				queryStr += "x.project.id = :projectId ";
			} else {
				queryStr += "x.product IS NULL AND x.project IS NULL AND (x.userId = :userId OR x.id IN (SELECT dt.deploy.id FROM DeployTeam AS dt WHERE dt.team.id IN (SELECT DISTINCT t.id FROM Team AS t LEFT JOIN t.members as m WHERE m.userId = :userId))) ";
			}
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
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_TABLE_NAME") || item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_PIPELINE_TABLE_NAME")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.name LIKE :nameValue"; 
						or = true;
					} else if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_SYSTEM_TABLE_NAME")) {
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.serverName LIKE :nameValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_TABLE_NAME") || item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_PIPELINE_TABLE_STATUS") ||
							item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_SYSTEM_TABLE_NAME")){
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
		}  else {
			query.setParameter("active", true);
		}
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_TABLE_NAME") || item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_PIPELINE_TABLE_NAME") ||
							item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_SYSTEM_TABLE_NAME")){
						query.setParameter("nameValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_TABLE_NAME") || item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_PIPELINE_TABLE_STATUS") ||
							item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_DEPLOY_SYSTEM_TABLE_NAME")){
						if ("active".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", true);
						} else if ("disabled".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", false);
						}
					}
					
				}
			}
		}
		
		if (action.contains("PIPELINE")) {
			if (request.containsParam(PMConstant.DEPLOYID)){
				query.setParameter("deployId", request.getParamLong(PMConstant.DEPLOYID));
			}
		} else if (action.contains("SYSTEM")) {
			if (request.containsParam(PMConstant.DEPLOYID)){
				query.setParameter("deployId", request.getParamLong(PMConstant.DEPLOYID));
			}
		} else {
			if (request.containsParam(PMConstant.PRODUCTID)) {
				query.setParameter("productId", request.getParamLong(PMConstant.PRODUCTID));
			} else if (request.containsParam(PMConstant.PROJECTID)) {
				query.setParameter("projectId", request.getParamLong(PMConstant.PROJECTID));
			} else {
				query.setParameter("userId", request.getParamLong(PMConstant.USERID));
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
			String action = (String) request.getParams().get(GlobalConstant.ACTION);
			String queryStr = "SELECT x FROM Deploy AS x WHERE x.id =:id";
			if (action.contains("PIPELINE")) {
				queryStr = "SELECT x FROM DeployPipeline AS x WHERE x.id =:id";
			} else if (action.contains("SYSTEM")) {
				queryStr = "SELECT x FROM DeploySystem AS x WHERE x.id =:id";
			}
			
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
			query.setParameter("id", request.getParamLong(GlobalConstant.ITEMID));
			if (action.contains("PIPELINE")) {
				DeployPipeline deployPipeline = (DeployPipeline) query.getSingleResult();
				response.addParam(GlobalConstant.ITEM, deployPipeline);
			} else if (action.contains("SYSTEM")) {
				DeploySystem deploySystem = (DeploySystem) query.getSingleResult();
				response.addParam(GlobalConstant.ITEM, deploySystem);
			} else {
				Deploy deploy = (Deploy) query.getSingleResult();
				response.addParam(GlobalConstant.ITEM, deploy);
			}
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_MISSING_ID",prefCacheUtil.getLang(request)), response);
		}
	}

}
