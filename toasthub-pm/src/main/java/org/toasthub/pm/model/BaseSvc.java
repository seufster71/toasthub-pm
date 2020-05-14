package org.toasthub.pm.model;

import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

public interface BaseSvc {

	public void process(RestRequest request, RestResponse response);
	public void itemCount(RestRequest request, RestResponse response);
	public void item(RestRequest request, RestResponse response);
	public void items(RestRequest request, RestResponse response);
	public void delete(RestRequest request, RestResponse response);
	public void save(RestRequest request, RestResponse response);
}
