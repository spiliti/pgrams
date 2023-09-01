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
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>

<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title">Council Preamble</div>
			</div>
			<div class="panel-body custom">
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.preamble.number" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						${councilPreamble.preambleNumber}</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.status" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						${councilPreamble.status.code}</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.department" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						${councilPreamble.department.name}</div>
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.amount" />
					</div>
					<div class="col-sm-3 add-margin view-content">
						${councilPreamble.sanctionAmount ne null ? councilPreamble.sanctionAmount:'N/A'}</div>		
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.gistofpreamble" />
					</div>
					<div class="col-sm-9 add-margin view-content">
						${councilPreamble.gistOfPreamble}</div>
				</div>
				<div class="row add-border">
					<div class="col-md-3 col-xs-6 add-margin">
						<spring:message code="lbl.upload" />
					</div>
					<div class="col-md-3 col-xs-12 add-margin down-file view-content"
						id="links">
						<c:choose>
							<c:when test="${councilPreamble.filestoreid != null}">
								<a
									href="/council/councilpreamble/downloadfile/${councilPreamble.filestoreid.fileStoreId}"
									data-gallery target="_blank">${councilPreamble.filestoreid.fileName}</a>

							</c:when>
							<c:otherwise>
								<spring:message code="msg.no.attach.found" />
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				<div class="row add-border">
					<div class="col-xs-3 add-margin">
						<spring:message code="lbl.ward" />
					</div>
				<c:choose>
					<c:when test="${!councilPreamble.wards.isEmpty()}">
					<div class="col-sm-9 add-margin view-content">
						<c:forEach items="${councilPreamble.wards}" var="ward"
							varStatus="i">
							<c:if test="${i.index ne 0}">, </c:if> ${ward.name}
						</c:forEach>
					</div>
					</c:when>
					<c:otherwise>
					<div class="col-sm-9 add-margin view-content">
						N/A
					</div>
					</c:otherwise>
					</c:choose>
				</div>
			</div>

		</div>
		<div class="panel panel-primary" data-collapsed="0">
			<jsp:include page="applicationhistory-view.jsp"></jsp:include>
		</div>

		<c:if test="${not councilPreamble.meetingMOMs.isEmpty()}">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title">Minutes of Meeting Details</div>
				</div>
				<div class="panel-body">
					<table class="table table-bordered" id="momdetails">
						<thead>
							<tr>
								<th><spring:message code="lbl.meeting.number" /></th>
								<th><spring:message code="lbl.meeting.date" /></th>
								<th><spring:message code="lbl.meeting.type" /></th>
								<th><spring:message code="lbl.preamble.status" /></th>
								<th><spring:message code="lbl.resolutionNumber" /></th>
								<th><spring:message code="lbl.resolution" /></th>
								<th><spring:message code="lbl.action" /></th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${!councilPreamble.meetingMOMs.isEmpty()}">
									<c:forEach items="${councilPreamble.meetingMOMs}"
										var="preamble" varStatus="counter">
										<tr>
											<td><c:out value="${preamble.meeting.meetingNumber}" /></td>
											<td width="8%"><c:out
													value="${preamble.meeting.meetingDate}" /></td>
											<td><c:out
													value="${preamble.meeting.committeeType ne null?preamble.meeting.committeeType.name:'N/A'}" /></td>
											<td><c:out value="${preamble.preamble.status ne null?preamble.preamble.status.code:'N/A'}" /></td>
											<td><c:out value="${preamble.resolutionNumber ne null?preamble.resolutionNumber:'N/A'}" /></td>
											<td><span class="more"><c:out
														value="${preamble.resolutionDetail ne null?preamble.resolutionDetail:'N/A'}" /></span></td>
											<td><button type="button"
													class="btn btn-xs btn-secondary view">
													<i class="fa fa-eye" aria-hidden="true"></i>&nbsp;&nbsp;View
												</button> <input type="hidden" name="councilMeeting"
												value="${preamble.meeting.id}" id="test" /></td>
										</tr>
									</c:forEach>
								</c:when>
								<%-- <c:otherwise>
							<div class="col-md-3 col-xs-6 add-margin">
								<spring:message code="lbl.noMeeting.Detail" />
							</div>
						</c:otherwise> --%>
							</c:choose>
						</tbody>
					</table>
				</div>
			</div>
		</c:if>
		<div class="text-center hide-close">
			<div class="add-margin">
				<a href="javascript:void(0)" class="btn btn-default"
					onclick="self.close()">Close</a>
			</div>
		</div>
	</div>
</div>

<style>
.morecontent span {
	display: none;
}
</style>

<script
	src="<cdn:url value='/resources/app/js/councilPreambleHelper.js?rnd=${app_release_no}'/>"></script>
<script
	src="<cdn:url value='/resources/app/js/showMoreorLessContent.js?rnd=${app_release_no}'/>"></script>

