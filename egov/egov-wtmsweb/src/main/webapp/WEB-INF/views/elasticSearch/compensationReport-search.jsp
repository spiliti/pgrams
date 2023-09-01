<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2018  eGovernments Foundation
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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %> 
<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title">
					<strong><spring:message code='title.compensation.report' /></strong>
				</div>
			</div>

			<div class="panel-body">
				<form:form  class="form-horizontal form-groups-bordered"
					id="compensationReportRequestForm" modelAttribute="compensationReportRequest" action="">
					<div class="form-group">
					<label for="field-1" class="col-sm-3 control-label">
					      <spring:message code="lbl.Service" /></label>
						<div class="col-sm-3 add-margin">
						  <form:select name="moduleName" path="" data-first-option="false" id="moduleName"
							cssClass="form-control" >
							<form:option value="">
								<spring:message code="lbl.select"/>
							</form:option>
							<form:options items="${modulesList}"  />  
						</form:select>
						</div>
						<label for="field-1" class="col-sm-2 control-label"><spring:message
										code="lbl.appType" /></label>
						<div class="col-sm-3 add-margin">
						<form:select name="applicationType" path="" data-first-option="false" id="applicationType"
							cssClass="form-control" >
							<form:option value="">
								<spring:message code="lbl.select"/>
							</form:option>
							<form:options items="${applicationTypeList}" />
						</form:select>
						</div>     
						</div>  
						<div class="form-group">
							<label for="field-1" class="col-sm-3 control-label"> <spring:message
										code="lbl.fromDate" /> <span class="mandatory"/>
								</label>
						<div class="col-sm-3 add-margin">
						<input type="text" name="fromDate" class="form-control datepicker" data-date-end-date="0d" 
								id="start_date" data-inputmask="'mask': 'd/m/y'"
								required="required" />
						</div>
						<label for="field-1" class="col-sm-2 control-label"> <spring:message code="lbl.toDate" /> <span class="mandatory"/></label>
						<div class="col-sm-3 add-margin">
						<input type="text" name="toDate" class="form-control datepicker today" data-date-end-date="0d" 
								id="end_date" data-inputmask="'mask': 'd/m/y'" required="required" />
						</div>

						</div>
					
					<div class="form-group">    
						<div class="text-center">
							<button type="button" class="btn btn-primary" id="compensationreport" 
								><spring:message code='lbl.search' /></button>
								
							<button class="btn btn-danger" type="reset" ><spring:message code="lbl.reset"/></button>
							<a href="javascript:void(0);" id="closeComplaints"
								class="btn btn-default" onclick="window.close()"><spring:message code='lbl.close' /></a>
						</div>
			    	</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
	
	<div class="row" >
					<div id="reportgeneration-header"
			class="col-md-12 table-header text-left">
			<fmt:formatDate value="${currentDate}" var="currDate"
				pattern="dd-MM-yyyy" />
			<spring:message code="lbl.reportgeneration" />
			:
			<c:out value="${currDate}"></c:out>
		</div>
	<table class="table table-bordered datatable dt-responsive"
		id="reportResults">

	</table>
</div>



<link rel="stylesheet"
	href="<cdn:url value='/resources/global/js/jquery/plugins/datatables/responsive/css/datatables.responsive.css' context='/egi'/>">
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/egi'/>"/>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/egi'/>">
<script
	src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/egi'/>"
	type="text/javascript"></script>
<script
	src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/egi'/>"
	type="text/javascript"></script>
	<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/dataTables.buttons.min.js' context='/egi'/>"></script>
	<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.bootstrap.min.js' context='/egi'/>"></script>
	<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.flash.min.js' context='/egi'/>"></script>
	<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/jszip.min.js' context='/egi'/>"></script>
	<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/vfs_fonts.js' context='/egi'/>"></script>
	
<script
	src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/dataTables.tableTools.js' context='/egi'/>"
	type="text/javascript"></script>
	<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.html5.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.print.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/TableTools.min.js' context='/egi'/>"
	type="text/javascript"></script>
<script
	src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/responsive/js/datatables.responsive.js' context='/egi'/>"
	type="text/javascript"></script>
<script src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/moment.min.js' context='/egi'/>"></script>
<script src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/datetime-moment.js' context='/egi'/>"></script>
<script
	src="<cdn:url  value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/egi'/>"
	type="text/javascript"></script>

<script src="<cdn:url  value='/resources/js/app/compensationreport.js?rnd=${app_release_no}'/>"
	type="text/javascript"></script>    
