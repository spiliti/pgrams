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
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>
<div class="row" id="page-content">
	<div class="col-md-12">
		<form:form class="form-horizontal form-groups-bordered"
			id="genericSubregisterform" name="genericSubregisterform"
			modelAttribute="legalSubReportResult" method="get">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title">Generic Sub Report</div>
				</div>
				<div class="panel-body custom-form">
				<div class="form-group" id="aggregated">
					<div class="form-group">
						<label for="field-1" class="col-sm-4 control-label"><spring:message
								code="lbl.aggregatedby" /> :</span></label>
						<div class="col-sm-3 add-margin">
							<div class="col-sm-12 add-margin">
								<form:select id="aggregatedBy" name="aggregatedBy"
									path="aggregatedBy" cssClass="form-control">
									<form:option value="">
										<spring:message code="lbl.select" />
									</form:option>
									<c:forEach items="${aggregatedByList}" var="aggregatedByvalue">
										<form:option value="${aggregatedByvalue}"> ${aggregatedByvalue} </form:option>
									</c:forEach>
								</form:select>
							</div>
						</div>
					</div>
					</div>
				</div>
				<div class="panel-heading">
					<div class="panel-title">Reports Criteria</div>
				</div>
				<div class="form-group" id="reportscriteria">
				<div class="form-group">
					<label class="col-sm-2 control-label"><spring:message
							code="lbl.casecatagory" /> :</label>
					<div class="col-sm-3 add-margin">
						<form:select name="caseCategory" path="caseCategory"
							data-first-option="false" id="caseCategory"
							cssClass="form-control">
							<form:option value="">
								<spring:message code="lbls.select" />
							</form:option>
							<form:options items="${caseTypeList}" itemValue="caseType"
								itemLabel="caseType" />
						</form:select>
					</div>
					<label class="col-sm-2 control-label text-right"><spring:message
							code="lbl.standingcons" /> :</label>
					<div class="col-sm-3 add-margin">
						<form:input class="form-control" maxlength="50" name="standingCounsel"
							id="standingCounsel" path="" />
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-2 control-label"><spring:message
							code="lbl.courttype" /> :</label>
					<div class="col-sm-3 add-margin">
						<form:select name="courtType" path="courtType"
							data-first-option="false" id="courtType" cssClass="form-control">
							<form:option value="">
								<spring:message code="lbls.select" />
							</form:option>
							<form:options items="${courtTypeList}" itemValue="courtType"
								itemLabel="courtType" />
						</form:select>
					</div>
					<label class="col-sm-2 control-label text-right"><spring:message
							code="lbl.court" /> :</label>
					<div class="col-sm-3 add-margin">
						<form:select name="courtName" path="courtName"
							data-first-option="false" id="courtName" cssClass="form-control">
							<form:option value="">
								<spring:message code="lbls.select" />
							</form:option>
							<form:options items="${courtsList}" itemValue="name"
								itemLabel="name" />
						</form:select>
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-2 control-label"><spring:message
							code="lbl.judgmentype" /> :</label>
					<div class="col-sm-3 add-margin">
						<form:select name="judgmentType" path="judgmentType"
							data-first-option="false" id="judgmentType"
							cssClass="form-control">
							<form:option value="">
								<spring:message code="lbls.select" />
							</form:option>
							<form:options items="${judgmentTypeList}" itemValue="name"
								itemLabel="name" />
						</form:select>
					</div>
					<label class="col-sm-2 control-label text-right"><spring:message
							code="lbl.petitiontype" /> :</label>
					<div class="col-sm-3 add-margin">
						<form:select name="petitionType" path="petitionType"
							data-first-option="false" id="petitionType"
							cssClass="form-control">
							<form:option value="">
								<spring:message code="lbls.select" />
							</form:option>
							<form:options items="${petitiontypeList}" itemValue="petitionType"
								itemLabel="petitionType" />
						</form:select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label text-right"><spring:message
							code="lbl.casestatus" /> :</label>
					<div class="col-sm-3 add-margin">
						<form:select name="caseStatus" path="caseStatus"
							data-first-option="false" id="caseStatus" cssClass="form-control">
							<form:option value="">
								<spring:message code="lbls.select" />
							</form:option>
							<form:options items="${statusList}" itemValue="description"
								itemLabel="description" />
						</form:select>
					</div>
					<label class="col-sm-2 control-label text-right"><spring:message
							code="lbl.officerincharge" />:</label>
					<div class="col-sm-3 add-margin">
					     <input id="positionName" type="text" class="form-control typeahead" placeholder="" autocomplete="off" />
                       		 <form:hidden path="officerIncharge" id="positionId"/>
                        	<form:errors path="officerIncharge" cssClass="add-margin error-msg" />		
					</div>
				</div>
				<div class="form-group" id="reportstatus">
					<label class="col-sm-2 control-label text-right"><spring:message
							code="lbl.reportstatus" />:</label>
					<div class="col-sm-3 add-margin">
						<form:select name="reportStatus" path="reportStatus"
							data-first-option="false" id="reportStatus"
							cssClass="form-control">
							<form:option value="">
								<spring:message code="lbls.select" />
							</form:option>
							<form:options items="${reportStatusList}" itemValue="name"
								itemLabel="name" />
						</form:select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label text-right"> <spring:message
							code="lbl.fromDate" /> :
					</label>
					<div class="col-sm-3 add-margin">
						<input type="text" name="caseFromDate" path=""
							class="form-control datepicker" data-date-end-date="0d"
							id="caseFromDate" data-inputmask="'mask': 'd/m/y' onblur=" onchnageofDate()"/>
					</div>
					<label class="col-sm-2 control-label text-right"> <spring:message
							code="lbl.toDate" /> :
					</label>
					<div class="col-sm-3 add-margin">
						<input type="text" name="caseToDate" path=""
							class="form-control datepicker" data-date-end-date="0d"
							id="caseToDate" data-inputmask="'mask': 'd/m/y'" />
					</div>
				</div>
				</div>


				<div class="row">
					<div class="text-center">
						<button type="button" class="btn btn-primary"
							id="searchid">
							<spring:message code="lbl.search" />
						</button>
						<a href="javascript:void(0)" class="btn btn-default"
							data-dismiss="modal" onclick="self.close()"><spring:message
								code="lbl.close" /></a>
					</div>
				</div>
			</div>

		</form:form>

		<div id="reportgeneration-header"
			class="col-md-12 table-header text-left">
			<fmt:formatDate value="${currDate}" var="currDate"
				pattern="dd-MM-yyyy" />
			<spring:message code="lbl.reportgeneration" />
			:
			<c:out value="${currDate}"></c:out>
		</div>
		<div id="tabledata">
			<table class="table table-bordered table-hover multiheadertbl"
				id="genericSubReport-table">
			</table>
		</div>
		
	</div>
</div>

<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/egi'/>" />
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/egi'/>">
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/dataTables.buttons.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.bootstrap.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.flash.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/jszip.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/pdfmake.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/vfs_fonts.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.html5.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/extensions/buttons/buttons.print.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/egi'/>"></script>
	<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/egi'/>" />
<script type="text/javascript"
	src="<cdn:url value='/resources/js/app/genericSubReport.js?rnd=${app_release_no}'/>"></script>