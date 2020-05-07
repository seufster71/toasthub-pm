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

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.utils.TenantContext;

@Repository("BugDao")
@Transactional("TransactionManagerData")
public class BugDaoImpl implements BugDao {
	
	@Autowired
	protected EntityManagerDataSvc entityManagerDataSvc;

	@Override
	public void getItems(RestRequest request, RestResponse response) throws Exception{
		
	}

	@Override
	public void getItemCount(RestRequest request, RestResponse response) throws Exception {
		
	}
	
	
	
	@Override
	public void delete(RestRequest request, RestResponse response) throws Exception {
		
	}

	
	@Override
	public void getItem(RestRequest request, RestResponse response) throws Exception {
		//	Long id
		
	}

	

	@Override
	public void save(RestRequest request, RestResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
