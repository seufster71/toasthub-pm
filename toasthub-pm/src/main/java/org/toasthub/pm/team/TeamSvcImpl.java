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

package org.toasthub.pm.team;

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
import org.toasthub.pm.model.ProductTeam;
import org.toasthub.pm.model.ProjectTeam;
import org.toasthub.pm.model.ReleaseTeam;
import org.toasthub.pm.model.Team;
import org.toasthub.pm.role.RoleSvc;
import org.toasthub.security.model.MyUserPrincipal;
import org.toasthub.security.model.User;

@Service("PMTeamSvc")
public class TeamSvcImpl implements TeamSvc, ServiceProcessor {

	@Autowired
	@Qualifier("PMTeamDao")
	TeamDao teamDao;
	
	@Autowired
	UtilSvc utilSvc;
	
	@Autowired
	PrefCacheUtil prefCacheUtil;
	
	@Autowired
	RoleSvc roleSvc;
	
	@Override
	public void process(RestRequest request, RestResponse response) {
		String action = (String) request.getParams().get(GlobalConstant.ACTION);
		List<String> global =  new ArrayList<String>(Arrays.asList("LANGUAGES"));
		User user = ((MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		
		Long count = 0l;
		switch (action) {
		case "INIT":
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			request.addParam(GlobalConstant.USERREF, user.getId());
			this.itemCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
			if (count != null && count > 0){
				this.items(request, response);
			}
			this.addLinkTeams(request, response);
			break;
		case "LIST":
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			request.addParam(GlobalConstant.USERREF, user.getId());
			this.itemCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
			if (count != null && count > 0){
				this.items(request, response);
			}
			this.addLinkTeams(request, response);
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
				List<String> forms =  new ArrayList<String>(Arrays.asList("PM_TEAM_FORM"));
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
				if ("PRODUCT".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					forms.add("PM_TEAM_PRODUCT_FORM");
				} else if ("PROJECT".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					forms.add("PM_TEAM_PROJECT_FORM");
				} else if ("RELEASE".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					forms.add("PM_TEAM_RELEASE_FORM");
				} else if ("BACKLOG".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					forms.add("PM_TEAM_BACKLOG_FORM");
				} else if ("DEPLOY".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					forms.add("PM_TEAM_DEPLOY_FORM");
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
			teamDao.items(request, response);
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
			teamDao.itemCount(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_EXECUTION_FAIL",prefCacheUtil.getLang(request)), response);
			e.printStackTrace();
		}
	}

	@Override
	public void delete(RestRequest request, RestResponse response) {
		try {
			teamDao.delete(request, response);
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
				teamDao.item(request, response);
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
				teamDao.item(request, response);
				request.addParam(GlobalConstant.ITEM, response.getParam(GlobalConstant.ITEM));
				response.getParams().remove(GlobalConstant.ITEM);
			} else {
				// new Team
				Team team = new Team();
				team.setActive(true);
				team.setArchive(false);
				team.setLocked(false);
				request.addParam(GlobalConstant.ITEM, team);
				
			}
			// marshall
			utilSvc.marshallFields(request, response);
		
			
			// save
			teamDao.save(request, response);
			
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, prefCacheUtil.getPrefText( "GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_SUCCESS", prefCacheUtil.getLang(request)), response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, prefCacheUtil.getPrefText( "GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_FAIL", prefCacheUtil.getLang(request)), response);
			e.printStackTrace();
		}
		
	}

	@Override
	public void linkParentModify(RestRequest request, RestResponse response) {
		try {
			teamDao.linkTeam(request, response);
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
				teamDao.linkTeam(request, response);
				request.addParam(GlobalConstant.ITEM, response.getParam(GlobalConstant.ITEM));
				response.getParams().remove(GlobalConstant.ITEM);
			} else {
				// new Team Link
				if ("PRODUCT".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					ProductTeam productTeam = new ProductTeam();
					productTeam.setActive(true);
					productTeam.setArchive(false);
					productTeam.setLocked(false);
					request.addParam(GlobalConstant.ITEM, productTeam);
				} else if ("PROJECT".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					ProjectTeam projectTeam = new ProjectTeam();
					projectTeam.setActive(true);
					projectTeam.setArchive(false);
					projectTeam.setLocked(false);
					request.addParam(GlobalConstant.ITEM, projectTeam);
				} else if ("RELEASE".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					ReleaseTeam releaseTeam = new ReleaseTeam();
					releaseTeam.setActive(true);
					releaseTeam.setArchive(false);
					releaseTeam.setLocked(false);
					request.addParam(GlobalConstant.ITEM, releaseTeam);
				} else if ("BACKLOG".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					BacklogTeam backlogTeam = new BacklogTeam();
					backlogTeam.setActive(true);
					backlogTeam.setArchive(false);
					backlogTeam.setLocked(false);
					request.addParam(GlobalConstant.ITEM, backlogTeam);
				} else if ("DEPLOY".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					DeployTeam deployTeam = new DeployTeam();
					deployTeam.setActive(true);
					deployTeam.setArchive(false);
					deployTeam.setLocked(false);
					request.addParam(GlobalConstant.ITEM, deployTeam);
				} else {
					utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, "Missing parent type", response);
					return;
				}
			}
			// marshall
			utilSvc.marshallFields(request, response);
			
			// save
			teamDao.linkTeamSave(request, response);
			
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, prefCacheUtil.getPrefText( "GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_SUCCESS", prefCacheUtil.getLang(request)), response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, prefCacheUtil.getPrefText( "GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_FAIL", prefCacheUtil.getLang(request)), response);
			e.printStackTrace();
		}
	}
	
	private void addLinkTeams(RestRequest request, RestResponse response) {
		try {
			
			if (request.containsParam(GlobalConstant.PARENTTYPE) && !"".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
				if ("PRODUCT".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					teamDao.linkTeams(request, response);
					// add link to items
					List<ProductTeam> productTeams = (List<ProductTeam>) response.getParam("linkTeams");
					List<Team> teams = (List<Team>) response.getParam(GlobalConstant.ITEMS);
					for (ProductTeam productTeam : productTeams) {
						for (Team team : teams) {
							if (productTeam.getTeamId() == team.getId()) {
								team.setProductTeam(productTeam);
							}
						}
					}
				} else if ("PROJECT".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					teamDao.linkTeams(request, response);
					// add link to items
					List<ProjectTeam> projectTeams = (List<ProjectTeam>) response.getParam("linkTeams");
					List<Team> teams = (List<Team>) response.getParam(GlobalConstant.ITEMS);
					for (ProjectTeam projectTeam : projectTeams) {
						for (Team team : teams) {
							if (projectTeam.getTeamId() == team.getId()) {
								team.setProjectTeam(projectTeam);
							}
						}
					}
				} else if ("BACKLOG".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					teamDao.linkTeams(request, response);
					// add link to items
					List<BacklogTeam> backlogTeams = (List<BacklogTeam>) response.getParam("linkTeams");
					List<Team> teams = (List<Team>) response.getParam(GlobalConstant.ITEMS);
					for (BacklogTeam backlogTeam : backlogTeams) {
						for (Team team : teams) {
							if (backlogTeam.getTeamId() == team.getId()) {
								team.setBacklogTeam(backlogTeam);
							}
						}
					}
				} else if ("RELEASE".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					teamDao.linkTeams(request, response);
					// add link to items
					List<ReleaseTeam> releaseTeams = (List<ReleaseTeam>) response.getParam("linkTeams");
					List<Team> teams = (List<Team>) response.getParam(GlobalConstant.ITEMS);
					for (ReleaseTeam releaseTeam : releaseTeams) {
						for (Team team : teams) {
							if (releaseTeam.getTeamId() == team.getId()) {
								team.setReleaseTeam(releaseTeam);
							}
						}
					}
				} else if ("DEPLOY".equals(request.getParam(GlobalConstant.PARENTTYPE))) {
					teamDao.linkTeams(request, response);
					// add link to items
					List<DeployTeam> deployTeams = (List<DeployTeam>) response.getParam("linkTeams");
					List<Team> teams = (List<Team>) response.getParam(GlobalConstant.ITEMS);
					for (DeployTeam deployTeam : deployTeams) {
						for (Team team : teams) {
							if (deployTeam.getTeamId() == team.getId()) {
								team.setDeployTeam(deployTeam);
							}
						}
					}
				}
			}
		} catch(Exception e) {
			
		}
	}

}
