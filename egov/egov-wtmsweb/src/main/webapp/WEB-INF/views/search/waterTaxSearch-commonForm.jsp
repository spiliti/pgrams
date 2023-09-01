<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>

<%@ page contentType="text/html" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>

<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title">
					<strong><spring:message code='title.watertaxSearch' />
					</strong>
				</div>
			</div>
			<div class="panel-body">
				<form:form  class="form-horizontal form-groups-bordered"
					id="waterSearchRequestForm" modelAttribute="connectionSearchRequest" method="post" action="/wtms/search/waterSearch/commonSearch-form/">
					
					<input type="hidden"  id="validMessage" name="validMessage" name="validMessage"  value="${validMessage}" />
					<input type="hidden"  id="mode" name="mode" name="mode"  value="${mode}" />
					<input type="hidden"  id="applicationType" name="applicationType" value="${applicationType}" />
					<input type="hidden"  id="meesevaApplicationNumber" name="meesevaApplicationNumber" value="${meesevaApplicationNumber}" />
					<div class="form-group">
					<label for="field-1" class="col-md-4 control-label"><spring:message code='lbl1.consumer.number'/></label>
						<div class="col-md-4 add-margin">
							<form:input type="text" path="consumerCode" name="consumerCode" class="form-control patternvalidation" data-pattern="number" maxlength="15" id="consumerCode" required="required"/>
						</div>
						<form:errors path="consumerCode" cssClass="add-margin error-msg" />
					</div>
					<br/>
					<div class="form-group">
						<div class="text-center">
							<button type="submit" class="btn btn-primary" id="submitButtonId">
								<spring:message code="lbl.submit" />
							</button>
							<input type="button" class="btn btn-default" onclick="customReset();" value="Reset"/>
							<a href="javascript:void(0);" id="closeComplaints"
								class="btn btn-default" onclick="self.close()"><spring:message code='lbl.close' /></a>
						</div>
				</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
<script>
	function customReset() {
		$("#consumerCode").val('');
	}
</script>
<script src="<cdn:url value='/resources/js/app/connectionCommonsearch.js?rnd=${app_release_no}'/>"
	type="text/javascript"></script>
	

