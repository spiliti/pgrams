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
        <div class="panel panel-primary" data-collapsed="0">
            <div class="panel-heading">
                <div class="panel-title">
                    <strong><spring:message code="title.dcbreport"/></strong>
                </div>
            </div>
            <div class="panel-body">
                <form:form class="form-horizontal form-groups-bordered" action=""
                           id="dcbreportform" modelAttribute="dCBReportRequest" method="get">

                    <div class="form-group">
                        <label class="col-sm-3 control-label text-right">
                            <spring:message code="lbl.license.no"/>
                        </label>
                        <div class="col-sm-6 add-margin">
                            <input type="text" name="licenseNumber" id="licensenumber"
                                   class="form-control patternvalidation" maxlength="15"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label text-right">
                            <spring:message code="lbl.license.status"/>
                        </label>
                        <div class="col-sm-6 add-margin">
                            <select name="active" id="activeLicense" class="form-control">
                                <option value="0"><spring:message code="lbl.default.all"/></option>
                                <option value="1"><spring:message code="lbl.isactive"/></option>
                                <option value="2"><spring:message code="lbl.inactive"/></option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label text-right"> <spring:message code='baseregister.ward'/></label>
                        <div class="col-sm-6 add-margin">
                            <form:select multiple="true" path="" id="ward" cssClass="form-control">
                                <form:options items="${revenueWards}" itemLabel="name" itemValue="id"/>
                            </form:select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label text-right"> <spring:message code='lbl.election.ward'/></label>
                        <div class="col-sm-6 add-margin">
                            <form:select multiple="true" path="" id="adminward" cssClass="form-control">
                                <form:options items="${electionWards}" itemLabel="name" itemValue="id"/>
                            </form:select>
                        </div>
                    </div>
                    <div class="form-group add-margin">
                        <div class="text-center">
                            <button type="button" id="btnsearch" class="btn btn-primary">
                                <spring:message code="lbl.search"/>
                            </button>
                            <button type="button" class="btn btn-default" data-dismiss="modal"
                                    onclick="window.close();">
                                <spring:message code="lbl.close"/>
                            </button>
                        </div>
                    </div>
                </form:form>
                <div class="row display-hide report-section add-margin">
                    <table class="table table-bordered datatable dt-responsive table-hover multiheadertbl" id="tbldcbdrilldown"
                           style="overflow-x: auto;max-width: 100%;min-width: 100%">
                        <thead>
                        <tr>
                            <th rowspan="2"></th>
                            <th rowspan="2"></th>
                            <th rowspan="2"></th>
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
                            <td colspan="5" style="text-align: right;"><spring:message code="lbl.total"/></td>
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
        </div>
    </div>
</div>
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
        src="<cdn:url  value='/resources/js/app/dcb-report.js?rnd=${app_release_no}'/>"></script>
