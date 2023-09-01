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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>

<div class="row">
	<div class="col-md-12">
		<form:form class="form-horizontal form-groups-bordered" id="dcbDrillDownWardwiseReportForm" modelAttribute="dcbReportWardwiseResult" commandName="dcbReportWardwiseResult" method="get">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title">
					</div>
				</div>
				<div class="panel-body">
					<label class="col-sm-2 control-label text-right"> <spring:message code="lbl.propertytype" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="propertyType" data-first-option="false" id="propertyType" cssClass="form-control">
							<form:option value="ALL">
								<spring:message code="lbl.all" />
							</form:option>
							<form:options items="${propertyType}" />
						</form:select>
						<form:errors path="propertyType" cssClass="add-margin error-msg" />
					</div>
						<label class="col-sm-2 control-label text-right" ><spring:message code="lbl.ward"></spring:message></label>
					<div class="col-sm-3 add-margin">
						<select name="wards" multiple id="ward" size="10"
							class="form-control wards tick-indicator">
							<option value="0">All</option>
							<c:forEach items="${wards}" var="ward">
								<option value="${ward.id}" title="${ward.name}">${ward.name}</option>
							</c:forEach>
						</select>
						<form:errors path="wards" cssClass="error-msg" />
					</div>
				</div>
			</div>
			<div class="row">
				<div class="text-center">
					<button type="button" class="btn btn-primary btnSearch" id="search">
						<spring:message code="lbl.search" />
					</button>
					<button type="reset" class="btn btn-danger"><spring:message code="lbl.reset"/></button>
					<a href="javascript:void(0)" onclick="self.close()"
						class="btn btn-default"><spring:message code="lbl.close" /></a>
				</div>
			</div>
		</form:form>
	</div>
</div>
<div class="row display-hide report-section">
	<div class="col-md-12 table-header text-left"><spring:message code="lbl.drill.reportDetails"/></div>
	<div class="col-md-12 form-group report-table-container">
		<table class="table table-bordered datatable dt-responsive table-hover multiheadertbl" id="tbldcbdrilldown-report">
			<thead>
				<tr>
					<th rowspan="2"><spring:message code="lbl.wardNumber"/></th>
					<th rowspan="2"><spring:message code="lbl.noofassessments"/></th>
					<th colspan="3"><spring:message code="lbl.demand"/></th>
					<th colspan="3"><spring:message code="lbl.collection"/></th>
					<th colspan="3"><spring:message code="lbl.balance"/></th>
				</tr>
				<tr>
					<th><spring:message code="lbl.arrear"/></th>
					<th><spring:message code="lbl.current"/></th>
					<th><spring:message code="lbl.total"/></th>
					<th><spring:message code="lbl.arrear"/></th>
					<th><spring:message code="lbl.current"/></th>
					<th><spring:message code="lbl.total"/></th>
					<th><spring:message code="lbl.arrear"/></th>
					<th><spring:message code="lbl.current"/></th>
					<th><spring:message code="lbl.total"/></th>
				</tr>
			</thead>
			<tfoot id="report-footer">
				<tr>
					<td align="center"><spring:message code="lbl.total"/></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</tfoot>
		</table>
	</div>
</div>
<link rel="stylesheet"
	href="<cdn:url value='/resources/css/sewerage-style.css?rnd=${app_release_no}'/>" />
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/egi'/>"/>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/egi'/>">
<script type="text/javascript" src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/egi'/>"></script>
<script type="text/javascript" src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/egi'/>"></script>
<script type="text/javascript" src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/dataTables.tableTools.js' context='/egi'/>"></script>
<script type="text/javascript" src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/TableTools.min.js' context='/egi'/>"></script>
<script type="text/javascript" src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/responsive/js/datatables.responsive.js' context='/egi'/>"></script>
<script src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/moment.min.js' context='/egi'/>"></script>
<script src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/datetime-moment.js' context='/egi'/>"></script>
<script  src="<cdn:url  value='/resources/js/reports/sewerageDCBWardwise.js?rnd=${app_release_no}' /> " ></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/js/search/common-util-helper.js?rnd=${app_release_no}'/>"></script>