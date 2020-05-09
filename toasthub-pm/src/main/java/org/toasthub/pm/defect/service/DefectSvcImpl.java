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

package org.toasthub.pm.defect.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.PrefCacheUtil;
import org.toasthub.pm.defect.repository.DefectDao;
import org.toasthub.security.model.UserContext;

@Service("BugSvc")
public class DefectSvcImpl implements DefectSvc, ServiceProcessor {

	@Autowired
	@Qualifier("BugDao")
	DefectDao bugDao;
	
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
		case "INIT": 
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			this.itemCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
			this.itemColumns(request, response);
			if (count != null && count > 0){
				this.items(request, response);
			}
			response.addParam(GlobalConstant.ITEMNAME, request.getParam(GlobalConstant.ITEMNAME));
			
			
			break;
		case "LIST":
			prefCacheUtil.getPrefInfo(request,response);
			this.itemCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
			this.itemColumns(request, response);
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
				List<String> forms =  new ArrayList<String>(Arrays.asList("ADMIN_LANGUAGE_PAGE"));
				request.addParam(PrefCacheUtil.PREFFORMKEYS, forms);
			}
			request.addParam(PrefCacheUtil.PREFGLOBAL, global);
			prefCacheUtil.getPrefInfo(request,response);
			this.save(request, response);
			break;
		default:
			utilSvc.addStatus(RestResponse.INFO, RestResponse.ACTIONNOTEXIST, "Action not available", response);
			break;
		}
	}
	
	@Override
	public void items(RestRequest request, RestResponse response) {
		try {
			bugDao.items(request, response);
			if (response.getParam("acquaintances") == null){
				utilSvc.addStatus(RestResponse.INFO, RestResponse.EMPTY, "No Items", response);
			}
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, PrefCacheUtil.getPrefText(request, "BUG_SERVICE", "BUG_SERVICE_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}

	
	@Override
	public void itemCount(RestRequest request, RestResponse response) {
		try {
			bugDao.itemCount(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, PrefCacheUtil.getPrefText(request, "BUG_SERVICE", "BUG_SERVICE_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}

	@Override
	public void delete(RestRequest request, RestResponse response) {
		try {
			bugDao.delete(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Delete Successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Unable to complete request", response);
			e.printStackTrace();
		}
	}
	
	@Override
	public void item(RestRequest request, RestResponse response) {
		try {
			bugDao.item(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Unable to complete request", response);
			e.printStackTrace();
		}
	}

	@Override
	public void itemColumns(RestRequest request, RestResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(RestRequest request, RestResponse response) {
		// TODO Auto-generated method stub
		
	}
}
