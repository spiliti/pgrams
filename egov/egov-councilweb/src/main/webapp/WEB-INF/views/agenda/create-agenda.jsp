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
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>


<form:form role="form" action="new" modelAttribute="councilPreamble"
	id="councilPreambleform"
	cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">

	<jsp:include page="../councilpreamble/councilpreamble-search-form.jsp" />

</form:form>
<div class="col-md-12">
	<div class="panel-heading">
		<div class="panel-title text-center no-float">
			<c:if test="${not empty message}">
				<strong style="color: red;">Entered ${message}</strong>
			</c:if>
		</div>
	</div>
</div>

<div class="row display-hide report-section">
	<div class="col-md-12 table-header text-left">Preamble Search
		Result</div>
	<div class="col-md-12 form-group report-table-container">
		<table class="table table-bordered table-hover multiheadertbl"
			id="resultTable">
		</table>
	</div>
</div>
<form:form role="agendaform" action="create" modelAttribute="councilAgenda"
	id="councilAgendaform" cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">
<input type="hidden" id="autoAgendaNoGenEnabled" value="${autoAgendaNoGenEnabled}" />

	<div class="row display-hide agenda-section">
		<c:choose>
			<c:when test="${!autoAgendaNoGenEnabled}">
				<div class="col-md-12">
					<div class="table-header text-left col-md-8 col-sm-7">Create
						Agenda</div>
				</div>
				<br />
				<br />
				<div class="col-md-12">
					<div class="col-md-6 col-sm-6">
						<label class="col-md-4 control-label text-right"><spring:message
								code="lbl.agendaNumber" /> <span class="mandatory"></span> </label>
						<div class="col-md-5 col-sm-5 ">
							<form:input path="agendaNumber" id="agendaNumber" type="text"
								class="form-control patternvalidation" maxlength="20" placeholder=""
								autocomplete="off" />
							<form:errors path="agendaNumber" cssClass="error-msg" />
						</div>
					</div>
					<div class="col-md-6 col-sm-6">
						<label class="col-md-7 control-label text-right"><spring:message
								code="lbl.committeetype" /><span class="mandatory"></span> </label>
						<div class="col-md-5 col-sm-5 pull-right">
							<form:select path="committeeType" id="committeeType"
								required="required" cssClass="form-control"
								cssErrorClass="form-control error">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:options items="${committeeType}" itemValue="id"
									itemLabel="name" />
							</form:select>
							<form:errors path="committeeType" cssClass="error-msg" />
						</div>
					</div>
				</div>
				<br />
				<br />
			</c:when>
			<c:otherwise>
				<div class="col-md-12">
					<div class="form-group">
						<div class="table-header text-left col-md-8 col-sm-7">Create
							Agenda</div>
						<label class="col-md-2 col-sm-2 control-label text-right"><spring:message
								code="lbl.committeetype" /> <span class="mandatory"></span> </label>
						<div class="col-md-2 col-sm-3">
							<form:select path="committeeType" id="committeeType"
								required="required" cssClass="form-control"
								cssErrorClass="form-control error">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:options items="${committeeType}" itemValue="id"
									itemLabel="name" />
							</form:select>
							<form:errors path="committeeType" cssClass="error-msg" />
						</div>
					</div>
				</div>
			</c:otherwise>
		</c:choose>

		<div class="col-md-12 report-table-container dragging">
			<table
				class="table table-bordered table-hover multiheadertbl sorted_table"
				id=agendaTable>
				<thead>
					<tr>
						<th width="5%"><spring:message code="lbl.serial.no" /></th>
						<th width="7%"><spring:message code="lbl.preamble.number" /></th>
						<th width="12%"><spring:message code="lbl.department" /></th>
						<th><spring:message code="lbl.gist.preamble" /></th>
						<th width="9%"><spring:message code="lbl.amount" /></th>
						<th width="11%"><spring:message code="lbl.action" /></th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="error-msg">Note: Using drag and drop on preamble
				item's can change order.</div>
		</div>

		<div class="form-group">
			<div class="text-center">
				<input type="button" class='btn btn-primary validate' value='Create'
					id="btnsave"> <a href='javascript:void(0)'
					class='btn btn-default' onclick='self.close()'><spring:message
						code='lbl.close' /></a>
			</div>
		</div>
	</div>
</form:form>
<script>
	$('#btnsearch').click(function(e) {

		if ($('form').valid()) {
			return true;
		} else {
			e.preventDefault();
		}
	});
</script>

<link rel="stylesheet"
	href="<cdn:url value='/resources/app/css/council-style.css?rnd=${app_release_no}'/>" />
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/egi'/>" />
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/egi'/>" />
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/egi'/>">
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/jquery-sortable.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/bootstrap/typeahead.bundle.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.inputmask.bundle.min.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js' context='/egi'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/egi'/>"
	type="text/javascript"></script>
<script
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/datetime-moment.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/app/js/councilAgenda.js?rnd=${app_release_no}'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/app/js/common-util-helper.js?rnd=${app_release_no}'/>"></script>
