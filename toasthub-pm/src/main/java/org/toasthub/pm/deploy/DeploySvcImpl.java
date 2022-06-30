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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.scp.client.ScpClient;
import org.apache.sshd.scp.client.ScpClientCreator;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.PrefCacheUtil;
import org.toasthub.pm.model.Deploy;
import org.toasthub.pm.model.DeployPipeline;
import org.toasthub.pm.model.DeploySystem;

@Service("PMDeploySvc")
public class DeploySvcImpl implements DeploySvc, ServiceProcessor {

	@Autowired
	@Qualifier("PMDeployDao")
	DeployDao deployDao;
	
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
			if (count != null && count > 0){
				this.items(request, response);
			}
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
				List<String> forms =  new ArrayList<String>(Arrays.asList("PM_DEPLOY_FORM"));
				request.addParam(PrefCacheUtil.PREFFORMKEYS, forms);
			}
			request.addParam(PrefCacheUtil.PREFGLOBAL, global);
			prefCacheUtil.getPrefInfo(request,response);
			this.save(request, response);
			break;
		case "INIT_PIPELINE":
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			this.itemCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
			if (count != null && count > 0){
				this.items(request, response);
			}
			response.addParam(GlobalConstant.ITEMNAME, request.getParam(GlobalConstant.ITEMNAME));
			break;
		case "LIST_PIPELINE":
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			this.itemCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
			if (count != null && count > 0){
				this.items(request, response);
			}
			response.addParam(GlobalConstant.ITEMNAME, request.getParam(GlobalConstant.ITEMNAME));
			break;
		case "ITEM_PIPELINE":
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			this.item(request, response);
			break;
		case "DELETE_PIPELINE":
			this.delete(request, response);
			break;
		case "SAVE_PIPELINE":
			if (!request.containsParam(PrefCacheUtil.PREFFORMKEYS)) {
				List<String> forms =  new ArrayList<String>(Arrays.asList("PM_DEPLOY_PIPELINE_FORM"));
				request.addParam(PrefCacheUtil.PREFFORMKEYS, forms);
			}
			request.addParam(PrefCacheUtil.PREFGLOBAL, global);
			prefCacheUtil.getPrefInfo(request,response);
			this.save(request, response);
			break;
		case "INIT_SYSTEM":
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			this.itemCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
			if (count != null && count > 0){
				this.items(request, response);
			}
			response.addParam(GlobalConstant.ITEMNAME, request.getParam(GlobalConstant.ITEMNAME));
			break;
		case "LIST_SYSTEM":
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			this.itemCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
			if (count != null && count > 0){
				this.items(request, response);
			}
			response.addParam(GlobalConstant.ITEMNAME, request.getParam(GlobalConstant.ITEMNAME));
			break;
		case "ITEM_SYSTEM":
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			this.item(request, response);
			break;
		case "DELETE_SYSTEM":
			this.delete(request, response);
			break;
		case "SAVE_SYSTEM":
			if (!request.containsParam(PrefCacheUtil.PREFFORMKEYS)) {
				List<String> forms =  new ArrayList<String>(Arrays.asList("PM_DEPLOY_SYSTEM_FORM"));
				request.addParam(PrefCacheUtil.PREFFORMKEYS, forms);
			}
			request.addParam(PrefCacheUtil.PREFGLOBAL, global);
			prefCacheUtil.getPrefInfo(request,response);
			this.save(request, response);
			break;
		case "TESTSSH":
			this.testSSH(request, response);
			break;
		case "TESTSCM":
			this.testSCM(request, response);
			break;
		default:
			utilSvc.addStatus(RestResponse.INFO, RestResponse.ACTIONNOTEXIST, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_ACTION_NOT_AVAIL",prefCacheUtil.getLang(request)), response);
			break;
		}
	}
	
	@Override
	public void items(RestRequest request, RestResponse response) {
		try {
			deployDao.items(request, response);
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
			deployDao.itemCount(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_EXECUTION_FAIL",prefCacheUtil.getLang(request)), response);
			e.printStackTrace();
		}
	}

	@Override
	public void delete(RestRequest request, RestResponse response) {
		try {
			deployDao.delete(request, response);
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_DELETE_SUCCESS",prefCacheUtil.getLang(request)), response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, prefCacheUtil.getPrefText("GLOBAL_SERVICE", "GLOBAL_SERVICE_DELETE_FAIL",prefCacheUtil.getLang(request)), response);
			e.printStackTrace();
		}
	}
	
	@Override
	public void item(RestRequest request, RestResponse response) {
		try {
			deployDao.item(request, response);
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
				deployDao.item(request, response);
				request.addParam(GlobalConstant.ITEM, response.getParam(GlobalConstant.ITEM));
				response.getParams().remove(GlobalConstant.ITEM);
			} else {
				String action = (String) request.getParams().get(GlobalConstant.ACTION);
				if (action.contains("PIPELINE")) {
					DeployPipeline deployPipeline = new DeployPipeline();
					deployPipeline.setActive(true);
					deployPipeline.setArchive(false);
					deployPipeline.setLocked(false);
					request.addParam(GlobalConstant.ITEM, deployPipeline);
				} else if (action.contains("SYSTEM")) {
					DeploySystem deploySystem = new DeploySystem();
					deploySystem.setActive(true);
					deploySystem.setArchive(false);
					deploySystem.setLocked(false);
					request.addParam(GlobalConstant.ITEM, deploySystem);
				} else {
					Deploy deploy = new Deploy();
					deploy.setActive(true);
					deploy.setArchive(false);
					deploy.setLocked(false);
					request.addParam(GlobalConstant.ITEM, deploy);
				}
			}
			// marshall
			utilSvc.marshallFields(request, response);
			
			// save
			deployDao.save(request, response);
			
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, prefCacheUtil.getPrefText( "GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_SUCCESS", prefCacheUtil.getLang(request)), response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, prefCacheUtil.getPrefText( "GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_FAIL", prefCacheUtil.getLang(request)), response);
			e.printStackTrace();
		}
	}
	
	public void testSSH(RestRequest request, RestResponse response) {
		response.addParam("sshTest", "SUCCESS");
		SshClient client = SshClient.setUpDefaultClient();
	    client.start();
	    
	    String username = (String) request.getParam("sshUser");
	    String host = (String) request.getParam("sshServer");
	    String password = (String) request.getParam("sshPassword");
	    int port = 22;
	    long defaultTimeoutSeconds = 1l;
	    
	    String command = "dir\n";
	    
	    
	    try (ClientSession session = client.connect(username, host, port).verify(defaultTimeoutSeconds, TimeUnit.SECONDS).getSession()) {
	        session.addPasswordIdentity(password);
	        session.auth().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
	        
	        try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream(); 
	          ClientChannel channel = session.createChannel(Channel.CHANNEL_SHELL)) {
	            channel.setOut(responseStream);
	            try {
	                channel.open().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
	                try (OutputStream pipedIn = channel.getInvertedIn()) {
	                	System.out.println("Send command");
	                    pipedIn.write("pwd\n".getBytes());
	                    pipedIn.flush();
	                }
	                
	                channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED, ClientChannelEvent.EOF), TimeUnit.SECONDS.toMillis(defaultTimeoutSeconds));
	                String responseString = new String(responseStream.toByteArray());
	                System.out.println("Print response");
	                System.out.println(responseString);
	                
	                
	                channel.open().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
	                try (OutputStream pipedIn = channel.getInvertedIn()) {
	                	System.out.println("Send command");
	                    pipedIn.write("ls -la\n".getBytes());
	                    pipedIn.flush();
	                }
	                channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED, ClientChannelEvent.EOF), TimeUnit.SECONDS.toMillis(defaultTimeoutSeconds));
	                responseString = new String(responseStream.toByteArray());
	                System.out.println("Print response");
	                System.out.println(responseString);
	                
	                
	                channel.open().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
	                ScpClientCreator creator = ScpClientCreator.instance();
	                ScpClient scpClient = creator.createScpClient(session);
	                
	                scpClient.upload(Paths.get("/Users/edwardseufert/testfile"), "/home/serveradmin1/", ScpClient.Option.Recursive, ScpClient.Option.PreserveAttributes, ScpClient.Option.TargetIsDirectory);
	                
	            } finally {
	                channel.close(false);
	            }
	        }
	    } catch (Exception e) {
	    	utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, prefCacheUtil.getPrefText( "GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_FAIL", prefCacheUtil.getLang(request)), response);
			e.printStackTrace();
	    } finally {
	        client.stop();
	    }
		utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, prefCacheUtil.getPrefText( "GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_SUCCESS", prefCacheUtil.getLang(request)), response);
	}
	
	public void testSCM(RestRequest request, RestResponse response) {
		response.addParam("scmTest", "SUCCESS");
		
		String username = (String) request.getParam("scmUser");
	    String host = (String) request.getParam("scmServer");
	    String password = (String) request.getParam("scmPassword");
		
	    try {
	    	RestTemplate restTemplate = new RestTemplate();
	    	HttpHeaders headers = new HttpHeaders();
	    	headers.add("Authorization", "Bearer "+password);
	    	headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
	    	HttpEntity<String> entity = new HttpEntity<>("body", headers);
	    	
	    	String resourceUrl = "https://api.github.com/users/"+username+"/repos";
	    	ResponseEntity<Object[]> responseJson = restTemplate.exchange(resourceUrl, HttpMethod.GET, entity, Object[].class);
	    	//		getForEntity(resourceUrl, String.class);
	        
	        Object[] productsJson = responseJson.getBody();
	        for (Object item : productsJson) {
	        	System.out.println(item.toString());
	        }
	        
	        // test download
	        //String githubSourceUrl, String accessToken
/*	        String destinationDir = "/Users/edwardseufert/toasthubWorkspace";
	        String githubSourceUrl = "https://github.com/seufster71/toasthub-core.git";
	        String branchName = "master";
	       CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(username, password);
	        URL fileUrl = new URL("file://"+destinationDir);
	        File destinationFile = FileUtils.toFile(fileUrl);
	        //delete any existing file
	        FileUtils.deleteDirectory(destinationFile);
	        Git.cloneRepository().setURI(githubSourceUrl)
	                .setBranch(branchName)
	                .setDirectory(destinationFile)
	                .setCredentialsProvider(credentialsProvider)
	                .call();
	        if(destinationFile.length() > 0){
	        	System.out.println("Good");
	        }else{
	        	System.out.println("Bad");
	        }
	      
	        InvocationRequest invocationRequest = new DefaultInvocationRequest();
	        Properties properties = new Properties();
	        properties.setProperty("skipTests", "true");
	        invocationRequest.setProperties(properties);
	        invocationRequest.setMavenHome(new File("/opt/apache-maven-3.8.6"));
	        invocationRequest.setPomFile(new File(destinationDir+"/toasthub-core/pom.xml"));
	        invocationRequest.setGoals( Arrays.asList("clean","install"));
	        Invoker invoke = new DefaultInvoker();
	        invoke.execute(invocationRequest);
	        */
	    } catch (Exception e) {
	    	utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, prefCacheUtil.getPrefText( "GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_FAIL", prefCacheUtil.getLang(request)), response);
			e.printStackTrace();
	    } finally {
	    	
	    }
		utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, prefCacheUtil.getPrefText( "GLOBAL_SERVICE", "GLOBAL_SERVICE_SAVE_SUCCESS", prefCacheUtil.getLang(request)), response);
	}
	
}
