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

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<form:form role="form" method="post" class="form-horizontal form-groups-bordered" modelAttribute="unitOfMeasurement">
    <div class="row">
        <div class="col-md-12">
            <c:if test="${not empty message}">
                <div class="alert alert-success" role="alert">
                    <spring:message code="${message}" arguments="${name}"/>
                </div>
            </c:if>
            <div class="panel panel-primary" data-collapsed="0">
                <div class="panel-heading">
                    <div class="panel-title"><strong><spring:message code="title.update.uom"/></strong>
                    </div>
                </div>
                <div class="panel-body custom-form">
                    <div class="form-group">
                        <label class="col-sm-2 control-label"> <spring:message code="lbl.name"/><span class="mandatory"></span>
                        </label>
                        <div class="col-sm-3">
                            <form:input path="name" id="name" type="text" class="form-control low-width patternvalidation" maxlength="50"
                                        cssErrorClass="form-control error low-width patternvalidation"
                                        data-pattern="alphanumericwithspacehyphenunderscore" required="required"/>
                            <form:errors path="name" cssClass="error-msg"/>
                        </div>
                        <label class="col-sm-2 control-label"> <spring:message code="lbl.code"/><span class="mandatory"></span>
                        </label>
                        <div class="col-sm-3">
                            <form:input path="code" id="code" disabled="true" type="text" class="form-control low-width" required="required"/>
                            <form:errors path="code" cssClass="error-msg"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label text-right">
                            <spring:message code="lbl.active"/>
                        </label>
                        <div class="col-sm-3 add-margin">
                            <form:checkbox path="active" id="active"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="text-center">
                    <button type="submit" class="btn btn-primary"><spring:message code="lbl.update"/></button>
                    <a href='javascript:void(0)' class='btn btn-default' onclick='window.location="."'><spring:message code='lbl.back'/></a>
                    <button type="reset" class="btn btn-default"><spring:message code="lbl.reset"/></button>
                    <button type="button" class="btn btn-default" onclick="self.close()"><spring:message code="lbl.close"/></button>
                </div>
            </div>
        </div>
    </div>
</form:form>