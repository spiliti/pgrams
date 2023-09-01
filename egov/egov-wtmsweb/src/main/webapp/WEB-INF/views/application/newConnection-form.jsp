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

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>
<div class="row">
	<div class="col-md-12">
		<div class="text-right error-msg" style="font-size: 14px;">
			<spring:message code="lbl.application.date" />
			:
			<fmt:formatDate pattern="dd/MM/yyyy"
				value="${waterConnectionDetails.applicationDate}" />
		</div>
		<form:form role="form" action="newConnection-create"
			modelAttribute="waterConnectionDetails" id="waterConnectionForm"
			cssClass="form-horizontal form-groups-bordered"
			enctype="multipart/form-data">
			<form:hidden path="applicationType" id="applicationType"
				value="${waterConnectionDetails.applicationType.id}" />
			<form:hidden path="connectionStatus" id="connectionStatus"
				value="${waterConnectionDetails.connectionStatus}" />
			<form:hidden path="meesevaApplicationNumber"
				id="meesevaApplicationNumber"
				value="${waterConnectionDetails.meesevaApplicationNumber}" />
			<input type="hidden" name="waterConnectionDetails"
				value="${waterConnectionDetails.id}">
			<input type="hidden"
				name="waterConnectionDetails.sewerageApplicationDetails"
				value="${sewerageApplicationDetails}">
			<form:hidden path="sewerageApplicationDetails.applicationType.id"
				id="waterConnectionDetails.sewerageApplicationDetails.applicationType.id"
				value="${waterConnectionDetails.sewerageApplicationDetails.applicationType.id}" />

			<input type="hidden" id="currentUser" name="currentUser"
				value="${currentUser}" />
			<input type="hidden" id="isCSCOperator" name="isCSCOperator"
				value="${isCSCOperator}" />
			<input type="hidden" id="citizenPortalUser" name="citizenPortalUser"
				value="${citizenPortalUser}" />
			<input type="hidden" id="isAnonymousUser" name="isAnonymousUser"
				value="${isAnonymousUser}" />
			<form:hidden id="mode" path="" value="${mode}" />
			<form:hidden path="legacy" id="legacy" value="false" />
			<input type="hidden" name="allowIfPTDueExists"
				id="allowIfPTDueExists" value="${allowIfPTDueExists}">
			<input type="hidden" name="validateIfPTDueExists"
				id="validateIfPTDueExists" value="${validateIfPTDueExists}">
			<input type="hidden" name="approvalPosOnValidate"
				id="approvalPosOnValidate" value="${approvalPosOnValidate}">
			<input type="hidden" name="noJAORSAMessage" id="noJAORSAMessage"
				value="${noJAORSAMessage}">
			<form:hidden id="documentName" path="" value="${documentName}" />
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title">
						<spring:message code="lbl.applicant.details" />
					</div>
				</div>
				<div class="panel-body custom-form ">
					<c:if test='${sewerageTaxenabled}'>
						<div class="panel-heading">
							<div class="panel-title">
								<input type="checkbox" id="addSewerageAppln"
									name="sewerageApplication">&nbspApply for Sewerage
								Connection also</input><br>
							</div>
						</div>
					</c:if>
					<jsp:include page="applicantdetails.jsp"></jsp:include>
					<jsp:include page="connectiondetails.jsp"></jsp:include>
					<div class="sewerageDetails">
						<jsp:include page="sewerageconnection.jsp"></jsp:include>
					</div>
					<jsp:include page="documentdetails.jsp"></jsp:include>
				</div>
			</div>
			<jsp:include page="../common/commonWorkflowMatrix.jsp" />
			<div class="buttonbottom" align="center">
				<jsp:include page="../common/commonWorkflowMatrix-button.jsp" />
			</div>
		</form:form>
	</div>
</div>

<script
	src="<cdn:url value='/resources/js/app/newconnection.js?rnd=${app_release_no}'/>"></script>
<script
	src="<cdn:url value='/resources/js/app/connectiondetails.js?rnd=${app_release_no}'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/egi'/>"></script>
