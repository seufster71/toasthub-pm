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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.PrefCacheUtil;
import org.toasthub.pm.model.BacklogTeam;
import org.toasthub.pm.model.DeployTeam;
import org.toasthub.pm.model.PMConstant;
import org.toasthub.pm.model.ProductTeam;
import org.toasthub.pm.model.ProjectTeam;
import org.toasthub.pm.model.ReleaseTeam;
import org.toasthub.pm.model.Team;
import org.toasthub.pm.model.TestCase;
import org.toasthub.pm.model.TestCaseDeploy;
import org.toasthub.pm.model.TestCaseTeam;
import org.toasthub.security.model.MyUserPrincipal;
import org.toasthub.security.model.User;

@Service("PMTestCaseSvc")
public class TestCaseSvcImpl implements TestCaseSvc, ServiceProcessor {

	@Autowired
	@Qualifier("PMTestCaseDao")
	TestCaseDao testCaseDao;
	
	@Autowired
	UtilSvc utilSvc;
	
	@Autowired
	PrefCacheUtil prefCacheUtil;

	
	@Override
	public void process(RestRequest request, RestResponse response) {
		User user = ((MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		request.addParam(PMConstant.USERID, user.getId());
		
		String action = (String) request.getParams().get(GlobalConstant.ACTION);
		List<String> global =  new ArrayList<String>(Arrays.asList("LANGUAGES"));
		
		Long count = 0l;
		switch (action) {
		case "INIT":
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			this.itemCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
			if (count != null && count > 0){
				this.items(request, response);
			}
			this.addLinkDeploy(request, response);
			response.addParam(GlobalConstant.ITEMNAME, request.getParam(GlobalConstant.ITEMNAME));
			break;
		case "LIST":
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			this.itemCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
			if (count != null && count > 0){
				this.items(request, response);
			}
			this.addLinkDeploy(request, response);
			response.addParam(GlobalConstant.ITEMNAME, request.getParam(GlobalConstant.ITEMNAME));
			break;
		case "ITEM":
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			this.item(request, response);
			break;
		case "DELETE":
			this.delete(request, response);
			break;
		case "SAVE":
			if (!request.containsParam(PrefCacheUtil.PREFFORMKEYS)) {
				List<String> forms =  new ArrayList<String>(Arrays.asList("PM_TESTCASE_FORM"));
				request.addParam(PrefCacheUtil.PREFFORMKEYS, forms);
			}
			request.addParam(PrefCacheUtil.PREFGLOBAL, global);
			prefCacheUtil.getPrefInfo(request,response);
			this.save(request, response);
			break;
		case "LINK_PARENT_MODIFY":
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			this.linkParentModify(request, response);
			break;
		case "LINK_PARENT_SAVE":
			if (!request.containsParam(PrefCacheUtil.PREFFORMKEYS)) {
				
				List<String> forms =  new ArrayList<String>();
				if ("DEPLOY".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					forms.add("PM_TESTCASE_DEPLOY_FORM");
				}
				request.addParam(PrefCacheUtil.PREFFORMKEYS, forms);
			}
			prefCacheUtil.getPrefInfo(request,response);
			this.linkParentSave(request, response);
			break;
		default:
			utilSvc.addStatus(RestResponse.INFO, RestResponse.ACTIONNOTEXIST, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_ACTION_NOT_AVAIL",prefCacheUtil.getLang(request)), response);
			break;
		}
	}
	
	@Override
	public void items(RestRequest request, RestResponse response) {
		try {
			testCaseDao.items(request, response);
			if (response.getParam("items") == null){
				utilSvc.addStatus(RestResponse.INFO, RestResponse.EMPTY, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_NO_ITEMS",prefCacheUtil.getLang(request)), response);
			}
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_EXECUTION_FAIL",prefCacheUtil.getLang(request)), response);
			e.printStackTrace();
		}
	}

	
	@Override
	public void itemCount(RestRequest request, RestResponse response) {
		try {
			testCaseDao.itemCount(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_EXECUTION_FAIL",prefCacheUtil.getLang(request)), response);
			e.printStackTrace();
		}
	}

	@Override
	public void delete(RestRequest request, RestResponse response) {
		try {
			testCaseDao.delete(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_DELETE_SUCCESS",prefCacheUtil.getLang(request)), response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_DELETE_FAIL",prefCacheUtil.getLang(request)), response);
			e.printStackTrace();
		}
	}
	
	@Override
	public void item(RestRequest request, RestResponse response) {
		try {
			if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
				testCaseDao.item(request, response);
			}
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_EXECUTION_FAIL",prefCacheUtil.getLang(request)), response);
			e.printStackTrace();
		}
	}

	@Override
	public void save(RestRequest request, RestResponse response) {
		try {
			// validate
			utilSvc.validateParams(request, response);
			
			if ((Boolean) request.getParam(GlobalConstant.VALID) == false) {
				utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_VALIDATION_ERR",prefCacheUtil.getLang(request)), response);
				return;
			}
			// get existing item
			Map<String,Object> inputList = (Map<String, Object>) request.getParam(GlobalConstant.INPUTFIELDS);
			if (inputList.containsKey(GlobalConstant.ITEMID) && inputList.get(GlobalConstant.ITEMID) != null && !"".equals(inputList.get(GlobalConstant.ITEMID))) {
				request.addParam(GlobalConstant.ITEMID, inputList.get(GlobalConstant.ITEMID));
				testCaseDao.item(request, response);
				request.addParam(GlobalConstant.ITEM, response.getParam(GlobalConstant.ITEM));
				response.getParams().remove(GlobalConstant.ITEM);
			} else {
				TestCase testCase = new TestCase();
				testCase.setActive(true);
				testCase.setArchive(false);
				testCase.setLocked(false);
				testCase.setUserId(((MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId());
				request.addParam(GlobalConstant.ITEM, testCase);
			}
			// marshall
			utilSvc.marshallFields(request, response);
		
			
			// save
			testCaseDao.save(request, response);
			
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_SUCCESS",prefCacheUtil.getLang(request)), response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_FAIL",prefCacheUtil.getLang(request)), response);
			e.printStackTrace();
		}
	}
	
	private void addLinkDeploy(RestRequest request, RestResponse response) {
		try {
			
			if (request.containsParam(GlobalConstant.PARENTTYPE) && !"".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				if ("DEPLOY".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					testCaseDao.linkDeploys(request, response);
					// add link to items
					List<TestCaseDeploy> testCaseDeploys = (List<TestCaseDeploy>) response.getParam("linkDeploys");
					List<TestCase> testCases = (List<TestCase>) response.getParam(GlobalConstant.ITEMS);
					for (TestCaseDeploy testCaseDeploy : testCaseDeploys) {
						for (TestCase testCase : testCases) {
							if (testCaseDeploy.getTestCaseId() == testCase.getId()) {
								testCase.setTestCaseDeploy(testCaseDeploy);
							}
						}
					}
				}
				
			}
		} catch(Exception e) {
			
		}
	}
	
	@Override
	public void linkParentModify(RestRequest request, RestResponse response) {
		try {
			testCaseDao.linkDeploy(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_EXECUTION_FAIL",prefCacheUtil.getLang(request)), response);
			e.printStackTrace();
		}
	}
	
	@Override
	public void linkParentSave(RestRequest request, RestResponse response) {
		try {
			// validate
			utilSvc.validateParams(request, response);
			
			if ((Boolean) request.getParam(GlobalConstant.VALID) == false) {
				utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_VALIDATION_ERR",prefCacheUtil.getLang(request)), response);
				return;
			}
			
			// get existing item
			Map<String,Object> inputList = (Map<String, Object>) request.getParam(GlobalConstant.INPUTFIELDS);
			if (inputList.containsKey(GlobalConstant.ITEMID) && inputList.get(GlobalConstant.ITEMID) != null && !"".equals(inputList.get(GlobalConstant.ITEMID))) {
				request.addParam(GlobalConstant.ITEMID, inputList.get(GlobalConstant.ITEMID));
				testCaseDao.linkDeploy(request, response);
				request.addParam(GlobalConstant.ITEM, response.getParam(GlobalConstant.ITEM));
				response.getParams().remove(GlobalConstant.ITEM);
			} else {
				// new Team Link
				if ("DEPLOY".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					TestCaseDeploy testCaseDeploy = new TestCaseDeploy();
					testCaseDeploy.setActive(true);
					testCaseDeploy.setArchive(false);
					testCaseDeploy.setLocked(false);
					request.addParam(GlobalConstant.ITEM, testCaseDeploy);
				} else {
					utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, "Missing parent type", response);
					return;
				}
			}
			// marshall
			utilSvc.marshallFields(request, response);
			
			// save
			testCaseDao.linkDeploySave(request, response);
			
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, prefCacheUtil.getPrefText( "GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_SUCCESS", prefCacheUtil.getLang(request)), response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, prefCacheUtil.getPrefText( "GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_FAIL", prefCacheUtil.getLang(request)), response);
			e.printStackTrace();
		}
	}
}
