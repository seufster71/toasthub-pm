package org.toasthub.pm.defect.repository;

import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

public interface DefectDao {

	public void delete(RestRequest request, RestResponse response) throws Exception;
	public void save(RestRequest request, RestResponse response) throws Exception;
	public void items(RestRequest request, RestResponse response) throws Exception;
	public void itemCount(RestRequest request, RestResponse response) throws Exception;
	public void item(RestRequest request, RestResponse response) throws Exception;
}
