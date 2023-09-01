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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>

<script src="<cdn:url value='/resources/js/app/viewregistration.js?rnd=${app_release_no}'/> "></script>

  <div class="row" id="page-content">
	<div class="col-md-12"> 
		<div class="text-right error-msg" style="font-size:14px;"></div>
		
		<c:set value="/mrs/reissue/create" var="actionUrl" />
		<c:if test="${reissue.status.code == 'REJECTED'}">
			 <c:set value="/mrs/reissue/workflow?id=${reissue.id}" var="actionUrl" />
		</c:if>
		<c:if test="${reissue.status.code == 'APPROVED'}">
			<c:set value="/mrs/reissue/certificate?reIssueId=${reissue.id}" var="actionUrl"></c:set> 
		</c:if>
		
		<form:form role="form" 
			modelAttribute="reIssue" id="form-reissue"
			cssClass="form-horizontal form-groups-bordered"
			enctype="multipart/form-data">
			
			<input type="hidden" name="reIssue" id="reIssue" value="${reissue.id}" />
			<input type="hidden" id="reIssueId" value="${reissue.id}" />
			<input type="hidden" id="reIssueStatus" value="${reissue.status.code}" />
			<input type="hidden" id="feeCollected" value="${reissue.feeCollected}" />
			<form:hidden path="" name="registration.id" id="reIssueRegistrationId" value="${reissue.registration.id}"/>	
			<form:hidden path="" id="workFlowAction" name="workFlowAction"/>
			
			<c:if test="${reissue.status.code =='CREATED' && !reissue.feeCollected}"> 
			 <div  data-collapsed="0">
				<div class="panel-heading">
					<div  style="color: red; font-size: 16px;" align="center">
						<spring:message  code="lbl.collect.reissuemarriageFee"/> 
					</div> 
				</div>
			</div>	
			</c:if>
			
					<ul class="nav nav-tabs" id="settingstab">
					    <li class="active"><a data-toggle="tab" href="#applicant-info" data-tabidx=0><spring:message code="subheading.applicant.info"/></a></li>
					    <li><a data-toggle="tab" href="#checklist-info" data-tabidx=1><spring:message code="lbl.registration.detail"/></a></li>
					 </ul>
					  
					  <div class="tab-content">
					    <div id="applicant-info" class="tab-pane fade in active">
					    	<div class="panel panel-primary" data-collapsed="0">
					    		<c:set value="applicant" var="applicant" scope="request"></c:set>
								<form:hidden path="applicant.id" />
								<jsp:include page="viewapplicantinfo.jsp">
									<jsp:param value="subheading.applicant.info" name="header" />
								</jsp:include>
								<jsp:include page="viewreissuedocumentdetails.jsp"></jsp:include>
					    	</div>
					    </div>
					    <div id="checklist-info" class="tab-pane fade">
					    	<div class="panel panel-primary" data-collapsed="0">
					    		<jsp:include page="../../common/generalinfo.jsp" />
					    	</div>					    	
					    </div>
					   	<jsp:include page="../../common/reg-reissue-wfhistory.jsp"></jsp:include>
					    
					  </div>
			<div class="buttonbottom" align="center"><a href="javascript:void(0)" class="btn btn-default" onclick="self.close()"><spring:message code="lbl.close"/></a></div>
		</form:form>
	</div>
</div>
<script src="<cdn:url value='/resources/js/app/mrgcert-reissue.js?rnd=${app_release_no}'/> "></script> 
<script src="<cdn:url value='/resources/global/js/egov/inbox.js' context='/egi'/>"></script>