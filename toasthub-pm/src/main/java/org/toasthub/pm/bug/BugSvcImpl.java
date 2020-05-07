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

package org.toasthub.pm.bug;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.mail.MailSvc;
import org.toasthub.core.preference.model.PrefCacheUtil;
import org.toasthub.security.model.UserContext;
import org.toasthub.security.userManager.UserManagerSvc;

@Service("BugSvc")
public class BugSvcImpl implements BugSvc, ServiceProcessor {

	@Autowired
	@Qualifier("BugDao")
	BugDao bugDao;
	
	@Autowired 
	UserManagerSvc userManagerSvc;
	
	@Autowired
	MailSvc mailSvc;
	
	@Autowired
	UtilSvc utilSvc;
	
	@Autowired
	PrefCacheUtil prefCacheUtil;
	
	@Autowired
	UserContext login;
	
	static Map<String,String> paramMap = new HashMap<String,String>();
	static {
		paramMap.put("receiverId", "Long");
		paramMap.put("memberId", "Long");
		paramMap.put("appUserId", "Long");
	}
	
	@Override
	public void process(RestRequest request, RestResponse response) {
		utilSvc.preProcessParams(request, paramMap);
		String action = (String) request.getParams().get(GlobalConstant.ACTION);
		
		Long count = 0l;
		switch (action) {
		case "INIT": 
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			
			
			
			break;
		case "LIST":
			this.getItemCount(request, response);
			if ((Long)response.getParam("acquaintanceCount") > 0) {
				this.getItems(request, response);
			}
			break;
		case "SHOW":
			
			this.getItem(request, response);
			break;
		case "DELETE":
			this.delete(request, response);
			break;
		case "SAVE":
			
			break;
		
		default:
			utilSvc.addStatus(RestResponse.INFO, RestResponse.ACTIONNOTEXIST, "Action not available", response);
			break;
		}
	}
	
	protected void getItems(RestRequest request, RestResponse response) {
		try {
			bugDao.getItems(request, response);
			if (response.getParam("acquaintances") == null){
				utilSvc.addStatus(RestResponse.INFO, RestResponse.EMPTY, "No Items", response);
			}
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, PrefCacheUtil.getPrefText(request, "BUG_SERVICE", "BUG_SERVICE_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}

	

	protected void getItemCount(RestRequest request, RestResponse response) {
		try {
			bugDao.getItemCount(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, PrefCacheUtil.getPrefText(request, "BUG_SERVICE", "BUG_SERVICE_FAIL").getValue(), response);
			e.printStackTrace();
		}
	}

	
	protected void delete(RestRequest request, RestResponse response) {
		try {
			bugDao.delete(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Delete Successful", response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Unable to complete request", response);
			e.printStackTrace();
		}
	}
	
	
	protected void getItem(RestRequest request, RestResponse response) {
		try {
			bugDao.getItem(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Unable to complete request", response);
			e.printStackTrace();
		}
	}
}
