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


<%@ page contentType="text/html" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>
<div class="row">
    <div class="col-md-12">
        <form:form class="form-horizontal form-groups-bordered" action=""
                   id="installmentWiseDCBForm" modelAttribute="installmentWiseDCBForm" method="get">
            <div class="panel panel-primary" data-collapsed="0">
                <div class="panel-heading">
                    <div class="panel-title">
                        <strong><spring:message code="lbl.dcb.installmentwise"/></strong>
                    </div>
                </div>
                <div class="panel-body"></div>
                <div class="form-group">
                    <label class="col-sm-3 control-label text-right"><spring:message
                            code="license.licensenumber"/></label>
                    <div class="col-sm-6 add-margin">
                        <input type="text" name="licenseNumber" id="licensenumber"
                               class="form-control patternvalidation" maxlength="15"/>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label text-right">Status</label>
                    <div class="col-sm-6 add-margin">
                        <select name="active" id="activeLicense" class="form-control">
                            <option value="0">All</option>
                            <option value="1">Active</option>
                            <option value="2">Inactive</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label text-right"> <spring:message
                            code="lbl.installmentyear"/></label>
                    <div class="col-sm-6 add-margin">
                        <select name="financialYear" class="form-control" id="financialyear"
                                required="required">
                            <c:forEach items="${financialYears}" var="finYear">
                                <option value="${finYear.finYearRange}">${finYear.finYearRange}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label text-right">
                        <spring:message code='baseregister.ward'/></label>
                    <div class="col-sm-6 add-margin">
                        <form:select multiple="true" path="wardId" id="ward" cssClass="form-control">
                            <form:options items="${revenueWards}" itemLabel="name" itemValue="id"/>
                        </form:select>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="text-center">
                    <button type="submit" id="btnsearch" class="btn btn-primary">
                        <spring:message code="lbl.search"/>
                    </button>
                    <button type="button" class="btn btn-default" data-dismiss="modal"
                            onclick="window.close();">
                        <spring:message code="lbl.close"/>
                    </button>
                </div>
            </div>
        </form:form>
    </div>
</div>

<div class="row display-hide report-section">
    <div class="col-md-12 table-header text-left">
        <spring:message code="lbl.installmentwise.dcb.report"/>
        Report Details
    </div>
    <div class="col-md-12 form-group report-table-container">
        <table
                class="table table-bordered datatable dt-responsive table-hover multiheadertbl"
                id="tblinstallmentdcb">
            <thead>
            <tr>
                <th rowspan="2"></th>
                <th rowspan="2"></th>
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
                <td colspan="2"><spring:message
                        code="lbl.total"/></td>
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
<div id="report-backbutton" class="col-xs-12 text-center"></div>
<link rel="stylesheet"
      href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/egi'/>"/>
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
<script type="text/javascript"
        src="<cdn:url  value='/resources/js/app/installmentwisedcb-report.js?rnd=${app_release_no}'/>"></script>
