package org.toasthub.pm.bug;

import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

public interface BugSvc {

	public void process(RestRequest request, RestResponse response);
}
