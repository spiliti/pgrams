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

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>

<div class="row" id="page-content">
    <div class="col-md-12">
        <c:if test="${not empty message}">
            <div class="alert alert-success" role="alert">
                <spring:message code="${message}" arguments="${name}"/>
            </div>
        </c:if>
        <form:form role="form" method="post" modelAttribute="licenseSubCategory" class="form-horizontal form-groups-bordered">
        <div class="panel panel-primary" data-collapsed="0">
            <div class="panel-heading">
                <div class="panel-title"><strong><spring:message code="title.modify.subcategory"/></strong></div>
            </div>
            <div class="panel-body custom-form">
                <div class="form-group">
                    <label class="col-sm-2 control-label"> <spring:message code="licenseCategory.category.lbl"/><span class="mandatory"></span>
                    </label>
                    <div class="col-sm-3 add-margin">
                        <form:select path="category" cssClass="form-control" cssErrorClass="form-control error" disabled="true" required="required">
                            <form:option value="">
                                <spring:message code="lbl.category.select"/>
                            </form:option>
                            <form:options items="${licenseCategories}" itemValue="id" itemLabel="name"/>
                        </form:select>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label text-right"><spring:message code="lbl.name"/> <span id="mandatory" class="mandatory"></span></label>
                    <div class="col-sm-3 add-margin">
                        <form:input path="name" id="name" cssClass="form-control" cssErrorClass="form-control error" required="required" maxLength="150"/>
                        <form:errors path="name" cssClass="error-msg"/>
                    </div>
                    <label class="col-sm-2 control-label text-right"><spring:message code="lbl.code"/> <span id="mandatory" class="mandatory"></span></label>
                    <div class="col-sm-3 add-margin">
                        <form:input path="code" id="code" cssClass="form-control patternvalidation" data-pattern="alphabets" cssErrorClass="form-control error"
                                    readonly="true" required="required" maxLength="5"/>
                        <form:errors path="code" cssClass="error-msg"/>
                    </div>
                </div>
                <div class="panel-heading">
                    <div class="panel-title text-left"><strong><spring:message code="title.details"/></strong></div>
                </div>
                <div class="col-md-12">
                    <table class="table table-bordered" id="subcat">
                        <thead>
                        <tr>
                            <th class="text-center"><spring:message code="lbl.feetype"/><span class="mandatory"></span></th>
                            <th class="text-center"><spring:message code="lbl.rateType"/><span class="mandatory"></span></th>
                            <th class="text-center" colspan="2"><spring:message code="license.uom.lbl"/><span class="mandatory"></span></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty licenseSubCategory.licenseSubCategoryDetails}">
                                <c:forEach items="${licenseSubCategory.licenseSubCategoryDetails}" var="licenseSubCategoryDetail" varStatus="item">
                                    <c:set var="display" value="table-row"/>
                                    <c:if test="${licenseSubCategoryDetail.markedForRemoval}">
                                        <c:set var="display" value="none"/>
                                    </c:if>
                                    <tr style="display:${display}">
                                        <td>
                                            <form:select path="licenseSubCategoryDetails[${item.index}].feeType" id="licenseSubCategoryDetails[${item.index}].feeType" value="${licenseSubCategoryDetail.feeType}" cssClass="form-control feeType" required="required">
                                                <form:option value="">
                                                    <spring:message code="lbl.select"/>
                                                </form:option>
                                                <form:options items="${licenseFeeTypes}" itemValue="id" itemLabel="name"/>
                                            </form:select>
                                            <form:errors path="licenseSubCategoryDetails[${item.index}].feeType" cssClass="add-margin error-msg"/>
                                        </td>
                                        <td>
                                            <form:select path="licenseSubCategoryDetails[${item.index}].rateType" id="licenseSubCategoryDetails[${item.index}].rateType" value="${licenseSubCategoryDetail.rateType}" cssClass="form-control rateType" required="required">
                                                <form:option value="">
                                                    <spring:message code="lbl.select"/>
                                                </form:option>
                                                <form:options items="${rateTypes}"/>
                                            </form:select>
                                            <form:errors path="licenseSubCategoryDetails[${item.index}].rateType" cssClass="add-margin error-msg"/>
                                        </td>
                                        <td>
                                            <form:select path="licenseSubCategoryDetails[${item.index}].uom" id="licenseSubCategoryDetails[${item.index}].uom" value="${licenseSubCategoryDetail.uom}" cssClass="form-control uom" required="required">
                                                <form:option value="">
                                                    <spring:message code="lbl.select"/>
                                                </form:option>
                                                <form:options items="${licenseUomTypes}" itemValue="id" itemLabel="name"/>
                                            </form:select>
                                            <form:hidden path="licenseSubCategoryDetails[${item.index}].markedForRemoval" id="licenseSubCategoryDetails[${item.index}].markedForRemoval"
                                                         value="${licenseSubCategoryDetail.markedForRemoval}" class="markedForRemoval"/>
                                            <form:errors path="licenseSubCategoryDetails[${item.index}].uom" cssClass="add-margin error-msg"/>
                                        </td>
                                        <td align="center">
                                            <span class="add-padding"><i class="fa fa-trash" aria-hidden="true" id="deleterow"></i></span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td>
                                        <form:select path="licenseSubCategoryDetails[0].feeType" id="licenseSubCategoryDetails[0].feeType" cssClass="form-control feeType" required="required">
                                            <form:option value="">
                                                <spring:message code="lbl.select"/>
                                            </form:option>
                                            <form:options items="${licenseFeeTypes}" itemValue="id" itemLabel="name"/>
                                        </form:select>
                                        <form:errors path="licenseSubCategoryDetails[0].feeType" cssClass="add-margin error-msg"/>

                                    </td>
                                    <td>
                                        <form:select path="licenseSubCategoryDetails[0].rateType" id="licenseSubCategoryDetails[0].rateType" cssClass="form-control rateType" required="required">
                                            <form:option value="">
                                                <spring:message code="lbl.select"/>
                                            </form:option>
                                            <form:options items="${rateTypes}"/>
                                        </form:select>
                                        <form:errors path="licenseSubCategoryDetails[0].rateType" cssClass="add-margin error-msg"/>
                                    </td>
                                    <td>
                                        <form:select path="licenseSubCategoryDetails[0].uom" id="licenseSubCategoryDetails[0].uom" cssClass="form-control uom" required="required">
                                            <form:option value="">
                                                <spring:message code="lbl.select"/>
                                            </form:option>
                                            <form:options items="${licenseUomTypes}" itemValue="id" itemLabel="name"/>
                                        </form:select>
                                        <form:errors path="licenseSubCategoryDetails[0].uom" cssClass="add-margin error-msg"/>
                                    </td>
                                    <td align="center">
                                        <span class="add-padding"><i class="fa fa-trash" aria-hidden="true" id="deleterow"></i></span>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                        <tfoot>
                        <tr>
                            <td colspan="4">
                                <button type="button" class="btn btn-secondary pull-right" id="addrow">
                                    <i class="fa fa-plus-circle" aria-hidden="true"></i> &nbsp;<spring:message code="lbl.add.more"/>
                                </button>
                            </td>
                        </tr>
                        </tfoot>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <div class="form-group">
        <div class="text-center">
            <button type="submit" class="btn btn-primary"><spring:message code="lbl.update"/></button>
            <a href='javascript:void(0)' class='btn btn-default' onclick='window.location="."'><spring:message code='lbl.back'/></a>
            <button type="reset" class="btn btn-default"><spring:message code="lbl.reset"/></button>
            <button type="button" class="btn btn-default" data-dismiss="modal" onclick="self.close()"><spring:message code="lbl.close"/></button>
        </div>
    </div>
    </form:form>
</div>
<script src="<cdn:url  value='/resources/js/app/license-subcategory.js?rnd=${app_release_no}'/>"></script>

