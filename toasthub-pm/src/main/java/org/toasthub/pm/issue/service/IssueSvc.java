package org.toasthub.pm.issue.service;

import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

public interface IssueSvc {

	public void process(RestRequest request, RestResponse response);
	public void itemCount(RestRequest request, RestResponse response);
	public void item(RestRequest request, RestResponse response);
	public void items(RestRequest request, RestResponse response);
	public void itemColumns(RestRequest request, RestResponse response);
	public void delete(RestRequest request, RestResponse response);
	public void save(RestRequest request, RestResponse response);
}
