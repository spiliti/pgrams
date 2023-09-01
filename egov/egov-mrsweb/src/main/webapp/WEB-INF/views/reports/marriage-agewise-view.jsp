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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title">Applicant Age Wise Application Details :-</div>
			</div>

			<div class="panel-body custom">
				<table class="table table-bordered  multiheadertbl" id=marriageregistrationagewise>
					<thead>
						<tr>
							<th><spring:message code="lbl.serial.no" /></th>
							<th><spring:message code="lbl.application.no" /></th>
							<th><spring:message code="lbl.registration.no" /></th>
							<th><spring:message code="lbl.applicant.name" /></th>
							<th><spring:message code="lbl.age" /></th>
							<th><spring:message code="lbl.date.of.marriage" /></th>
							<th><spring:message code="lbl.place.of.marriage" /></th>
							<th><spring:message code="lbl.residence.address" /></th>
							<th><spring:message code="lbl.registrationunitname"/></th>
							<th><spring:message code="lbl.Boundary" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${marriageRegistrations}" var="reg"
							varStatus="counter">
							<tr>
								<td>${counter.index+1}<input type="hidden" name="applicant"
									id="applicant" value="" /></td>
								<td><c:out value="${reg.applicationNo}" /></td>
								<td><c:out value="${reg.registrationNo!=null?reg.registrationNo:'N/A'}"></c:out></td>
								<td><c:choose>
										<c:when test="${applicantType == 'husband'}">
											<c:out value="${reg.husband.name.firstName}" />
										</c:when>
										<c:otherwise>
											<c:out value="${reg.wife.name.firstName}" />
										</c:otherwise>
									</c:choose></td>
								<td><c:choose>
										<c:when test="${applicantType == 'husband'}">
											<c:out value="${reg.husband.ageInYearsAsOnMarriage}" />
										</c:when>
										<c:otherwise>
											<c:out value="${reg.wife.ageInYearsAsOnMarriage}" />
										</c:otherwise>
									</c:choose></td>
								<td><c:out value="${reg.dateOfMarriage}" /></td>
								<td><c:out value="${reg.placeOfMarriage}" /></td>
								<td><c:choose>
										<c:when test="${applicantType == 'husband'}">
											<c:out value="${reg.husband.contactInfo.residenceAddress}" />
										</c:when>
										<c:otherwise>
											<c:out value="${reg.wife.contactInfo.residenceAddress}" />
										</c:otherwise>
									</c:choose></td>
										<td><c:out value="${reg.marriageRegistrationUnit.name}" /></td>
								<td><c:out value="${reg.zone.name}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
<div class="row">
	<div class="text-center">
		<a href="javascript:void(0)" class="btn btn-default"
			onclick="self.close()"><spring:message code="lbl.close" /></a>
	</div>
</div>