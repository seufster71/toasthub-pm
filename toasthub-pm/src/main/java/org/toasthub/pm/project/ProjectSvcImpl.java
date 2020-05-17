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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.PrefCacheUtil;
import org.toasthub.pm.model.Project;

@Service("ProjectSvc")
public class ProjectSvcImpl implements ProjectSvc, ServiceProcessor {

	@Autowired
	@Qualifier("ProjectDao")
	ProjectDao projectDao;
	
	@Autowired
	UtilSvc utilSvc;
	
	@Autowired
	PrefCacheUtil prefCacheUtil;

	
	@Override
	public void process(RestRequest request, RestResponse response) {
		String action = (String) request.getParams().get(GlobalConstant.ACTION);
		List<String> global =  new ArrayList<String>(Arrays.asList("LANGUAGES"));
		
		Long count = 0l;
		switch (action) {
		case "LIST":
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			this.itemCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
			if (count != null && count > 0){
				this.items(request, response);
			}
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
				List<String> forms =  new ArrayList<String>(Arrays.asList("PM_PROJECT_PAGE"));
				request.addParam(PrefCacheUtil.PREFFORMKEYS, forms);
			}
			request.addParam(PrefCacheUtil.PREFGLOBAL, global);
			prefCacheUtil.getPrefInfo(request,response);
			this.save(request, response);
			break;
		default:
			utilSvc.addStatus(RestResponse.INFO, RestResponse.ACTIONNOTEXIST, PrefCacheUtil.getPrefText(request, "GLOBAL_SERVICE", "GLOBAL_SERVICE_ACTION_NOT_AVAIL").getValue(), response);
			break;
		}
	}
	
	@Override
	public void items(RestRequest request, RestResponse response) {
		try {
			projectDao.items(request, response);
			if (response.getParam("items") == null){
				utilSvc.addStatus(RestResponse.INFO, RestResponse.EMPTY, PrefCacheUtil.getPrefText(request, "GLOBAL_SERVICE", "GLOBAL_SERVICE_NO_ITEMS").getValue(), response);
			}
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, PrefCacheUtil.getPrefText(request, "GLOBAL_SERVICE", "GLOBAL_SERVICE_EXECUTION_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}

	
	@Override
	public void itemCount(RestRequest request, RestResponse response) {
		try {
			projectDao.itemCount(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, PrefCacheUtil.getPrefText(request, "GLOBAL_SERVICE", "GLOBAL_SERVICE_EXECUTION_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}

	@Override
	public void delete(RestRequest request, RestResponse response) {
		try {
			projectDao.delete(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, PrefCacheUtil.getPrefText(request, "GLOBAL_SERVICE", "GLOBAL_SERVICE_DELETE_SUCCESS").getValue(), response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, PrefCacheUtil.getPrefText(request, "GLOBAL_SERVICE", "GLOBAL_SERVICE_DELETE_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}
	
	@Override
	public void item(RestRequest request, RestResponse response) {
		try {
			projectDao.item(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, PrefCacheUtil.getPrefText(request, "GLOBAL_SERVICE", "GLOBAL_SERVICE_EXECUTION_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}

	@Override
	public void save(RestRequest request, RestResponse response) {
		try {
			// validate
			utilSvc.validateParams(request, response);
			
			if ((Boolean) request.getParam(GlobalConstant.VALID) == false) {
				utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, PrefCacheUtil.getPrefText(request, "GLOBAL_SERVICE", "GLOBAL_SERVICE_VALIDATION_ERR").getValue(), response);
				return;
			}
			// get existing item
			Map<String,Object> inputList = (Map<String, Object>) request.getParam(GlobalConstant.INPUTFIELDS);
			if (inputList.containsKey(GlobalConstant.ITEMID) && inputList.get(GlobalConstant.ITEMID) != null && !"".equals(inputList.get(GlobalConstant.ITEMID))) {
				request.addParam(GlobalConstant.ITEMID, inputList.get(GlobalConstant.ITEMID));
				projectDao.item(request, response);
				request.addParam(GlobalConstant.ITEM, response.getParam(GlobalConstant.ITEM));
				response.getParams().remove(GlobalConstant.ITEM);
			} else {
				Project project = new Project();
				project.setArchive(false);
				project.setLocked(false);
				request.addParam(GlobalConstant.ITEM, project);
			}
			// marshall
			utilSvc.marshallFields(request, response);
		
			
			// save
			projectDao.save(request, response);
			
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, PrefCacheUtil.getPrefText(request, "GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_SUCCESS").getValue(), response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, PrefCacheUtil.getPrefText(request, "GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_FAIL").getValue(), response);
			e.printStackTrace();
		}
		
	}
}
