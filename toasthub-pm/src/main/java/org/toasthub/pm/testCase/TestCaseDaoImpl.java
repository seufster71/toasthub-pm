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

package org.toasthub.pm.testCase;


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
import org.toasthub.core.preference.model.PrefCacheUtil;
import org.toasthub.pm.model.Deploy;
import org.toasthub.pm.model.PMConstant;
import org.toasthub.pm.model.TestCase;
import org.toasthub.pm.model.TestCaseDeploy;

@Repository("PMTestCaseDao")
@Transactional("TransactionManagerData")
public class TestCaseDaoImpl implements TestCaseDao {
	
	@Autowired
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;
	@Autowired
	PrefCacheUtil prefCacheUtil;
	
	@Override
	public void delete(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			
			TestCase testCase = (TestCase) entityManagerDataSvc.getInstance().getReference(TestCase.class,  request.getParamLong(GlobalConstant.ITEMID));
			entityManagerDataSvc.getInstance().remove(testCase);
			
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

	@Override
	public void save(RestRequest request, RestResponse response) throws Exception {
		TestCase testCase = (TestCase) request.getParam(GlobalConstant.ITEM);
		boolean idEmpty = false;
		if (testCase.getId() == null) {
			idEmpty = true;
			testCase = entityManagerDataSvc.getInstance().merge(testCase);
		}
		if (idEmpty && request.containsParam(PMConstant.PARENTID)) {
			Deploy deploy = (Deploy) entityManagerDataSvc.getInstance().getReference(Deploy.class,  request.getParamLong(PMConstant.PARENTID));
			TestCaseDeploy testCaseDeploy = new TestCaseDeploy(testCase,deploy);
			entityManagerDataSvc.getInstance().merge(testCaseDeploy);
		}
		
	}

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT DISTINCT x FROM TestCase AS x WHERE x.archive = :archive AND (x.userId = :userId OR x.id IN (SELECT tct.testCase.id FROM TestCaseTeam AS tct WHERE tct.team.id IN (SELECT DISTINCT t.id FROM Team AS t LEFT JOIN t.members as m WHERE m.userId = :userId))) ";
		
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
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TESTCASE_TABLE_NAME")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.name LIKE :nameValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TESTCASE_TABLE_STATUS")){
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
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_TESTCASE_TABLE_NAME")){
						if (comma) { orderItems.append(","); }
						orderItems.append("x.name ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("PM_TESTCASE_TABLE_STATUS")){
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
		
		Boolean archive = false;
		if (request.containsParam(GlobalConstant.ARCHIVE)) {
			archive = (Boolean) request.getParam(GlobalConstant.ARCHIVE);
			
		}
		query.setParameter("archive", archive);
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TESTCASE_TABLE_NAME")){
						query.setParameter("nameValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TESTCASE_TABLE_STATUS")){
						if ("active".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", true);
						} else if ("disabled".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", false);
						}
					}
				}
			}
		}
		query.setParameter("userId", request.getParamLong(PMConstant.USERID));
		if (request.containsParam(GlobalConstant.LISTLIMIT) && (Integer) request.getParam(GlobalConstant.LISTLIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.LISTSTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.LISTLIMIT));
		}
		
		@SuppressWarnings("unchecked")
		List<TestCase> results = query.getResultList();

		response.addParam(GlobalConstant.ITEMS, results);
		
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT COUNT(DISTINCT x) FROM TestCase as x WHERE x.archive = :archive AND (x.userId = :userId OR x.id IN (SELECT tct.testCase.id FROM TestCaseTeam AS tct WHERE tct.team.id IN (SELECT DISTINCT t.id FROM Team AS t LEFT JOIN t.members as m WHERE m.userId = :userId))) ";
	
		
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
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TESTCASE_TABLE_NAME")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "x.name LIKE :nameValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TESTCASE_TABLE_STATUS")){
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

		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
		Boolean archive = false;
		if (request.containsParam(GlobalConstant.ARCHIVE)) {
			archive = (Boolean) request.getParam(GlobalConstant.ARCHIVE);
			
		}
		query.setParameter("archive", archive);
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TESTCASE_TABLE_NAME")){
						query.setParameter("nameValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("PM_TESTCASE_TABLE_STATUS")){
						if ("active".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", true);
						} else if ("disabled".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", false);
						}
					}
				}
			}
		}
		query.setParameter("userId", request.getParamLong(PMConstant.USERID));
		Long count = (Long) query.getSingleResult();
		if (count == null){
			count = 0l;
		}
		response.addParam(GlobalConstant.ITEMCOUNT, count);
		
	}

	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			String queryStr = "SELECT x FROM TestCase AS x WHERE x.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
			query.setParameter("id", request.getParamLong(GlobalConstant.ITEMID));
			TestCase testCase = (TestCase) query.getSingleResult();
			
			response.addParam(GlobalConstant.ITEM, testCase);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_MISSING_ID",prefCacheUtil.getLang(request)), response);
		}
	}

	@Override
	public void linkDeploys(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.PARENTTYPE) && !"".equals(request.getParam(GlobalConstant.PARENTTYPE)) 
				&& request.containsParam(GlobalConstant.PARENTID) && !"".equals(request.getParam(GlobalConstant.PARENTID))) {
			if ("DEPLOY".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				String queryStr = "SELECT new TestCaseDeploy(x.id, x.active, x.deploy.id, x.testCase.id) FROM TestCaseDeploy AS x WHERE x.deploy.id =:id";
				Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
				query.setParameter("id", request.getParamLong(GlobalConstant.PARENTID));
				List<TestCaseDeploy> testCaseDeploys = query.getResultList();
				response.addParam("linkDeploys", testCaseDeploys);
			} else {
				response.addParam("linkDeploys", null);
			}
			
			
			
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

	@Override
	public void linkDeploy(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.PARENTTYPE) && !"".equals(request.getParam(GlobalConstant.PARENTTYPE)) 
				&& request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			String queryStr = "";
			if ("DEPLOY".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				queryStr = "SELECT x FROM TestCaseDeploy AS x WHERE x.id =:id";
				Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
				query.setParameter("id", request.getParamLong(GlobalConstant.ITEMID));
				TestCaseDeploy testCaseDeploy = (TestCaseDeploy) query.getSingleResult();
				response.addParam(GlobalConstant.ITEM, testCaseDeploy);
			} else {
				response.addParam(GlobalConstant.ITEM, null);
			}
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_MISSING_ID",prefCacheUtil.getLang(request)), response);
		}
		
	}

	@Override
	public void linkDeploySave(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.PARENTTYPE) && !"".equals(request.getParam(GlobalConstant.PARENTTYPE)) 
				&& request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			if ("DEPLOY".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				TestCaseDeploy testCaseDeploy = (TestCaseDeploy) request.getParam(GlobalConstant.ITEM);
				if (request.containsParam(GlobalConstant.PARENTID) && !"".equals(request.getParam(GlobalConstant.PARENTID))) {
					Deploy deploy = (Deploy) entityManagerDataSvc.getInstance().getReference(Deploy.class,  request.getParamLong(GlobalConstant.PARENTID));
					testCaseDeploy.setDeploy(deploy);
				}
				if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
					TestCase testCase = (TestCase) entityManagerDataSvc.getInstance().getReference(TestCase.class,  request.getParamLong(GlobalConstant.ITEMID));
					testCaseDeploy.setTestCase(testCase);
				}
				entityManagerDataSvc.getInstance().merge(testCaseDeploy);
			}
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_MISSING_ID",prefCacheUtil.getLang(request)), response);
		}
		
	}
}
