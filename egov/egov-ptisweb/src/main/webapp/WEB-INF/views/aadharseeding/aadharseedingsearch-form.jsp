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
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>
<div id="aadharSeedingError" class="errorstyle" style="color: red"></div>
<form:form role="form" action="" modelAttribute="aadharSeeding"
	 id="aadharSeedingForm"
	class="form-horizontal form-groups-bordered">
	
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title">
						<spring:message code="lbl.aadhar.search" />
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label"> <spring:message
							code="lbl.ward" /> :
					</label>
					<div class="col-sm-2">
						<form:select path="wardId" id="wardId" class="form-control">
						    <form:option value="">Select</form:option>
							<form:options items="${wardId}" />
						</form:select>
					</div>
					
					<label class="col-sm-3 control-label"> <spring:message
							code="lbl.elec.wardno" /> :
					</label>
					<div class="col-sm-2">
						<form:select path="electionWardId" id="electionWardId" class="form-control">
						    <form:option value="">Select</form:option>
							<form:options items="${electionWardId}" />
						</form:select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label"> <spring:message
							code="lbl.assmtno" /> :
					</label>
					<div class="col-sm-2">
						<form:input path="" id="assessmentNo" type="text"
									class="form-control" />
					</div>
					
					<label class="col-sm-3 control-label"> <spring:message
							code="lbl.doorNumber" /> :
					</label>
					<div class="col-sm-2">
						<form:input path="" id="doorNo" type="text"
									class="form-control" />
					</div>
				</div>
			</div>

		</div>
	</div>
		<div class="row">
				<div class="text-center">
					<button type="button" id="searchAssessment" class="btn btn-primary">
						Search</button>
					<button type="button" id="btnClose" class="btn btn-default" onClick=window.close()>Close</button>
				</div>
			</div>
</form:form>
<spring:message code="reports.note.text" />
		<div id="searchAssessmentResult-header" class="col-md-12 table-header text-left">
		
			</div>
		<table class="table table-bordered datatable dt-responsive table-hover" id="searchAssessmentResult-table">
		</table>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/egi'/>"/>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/egi'/>"/>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/egi'/>">
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.tableTools.js' context='/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/TableTools.min.js' context='/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/bootstrap/typeahead.bundle.js' context='/egi'/>"></script>
<script src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.inputmask.bundle.min.js' context='/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js' context='/egi'/>"></script>
<script type="text/javascript" src="<cdn:url value='/resources/js/app/aadharseeding.js?rnd=${app_release_no}'/>"></script>

