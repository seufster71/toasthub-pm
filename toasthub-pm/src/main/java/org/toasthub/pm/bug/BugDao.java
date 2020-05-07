package org.toasthub.pm.bug;

import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

public interface BugDao {

	public void getItems(RestRequest request, RestResponse response) throws Exception;
	public void getItemCount(RestRequest request, RestResponse response) throws Exception;
	public void delete(RestRequest request, RestResponse response) throws Exception;
	public void save(RestRequest request, RestResponse response) throws Exception;
	public void getItem(RestRequest request, RestResponse response) throws Exception;
	
}
