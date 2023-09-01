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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/includes/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<script>

    function getUsersByDesignationAndDept() {
        populateapproverPositionId(
            {
                approverDepartmentId: $('#approverDepartment').val(),
                designationId: $('#approverDesignation').val()
            });
    }

    function callAlertForDepartment() {
        var value = $('#approverDepartment').val();
        if (value == "-1") {
            bootbox.alert("Please select an Approver Department");
            $('#approverDepartment').focus();
            return false;
        }
    }

    function callAlertForDesignation() {
        var value = $('#approverDesignation');
        if (value == "-1") {
            bootbox.alert("Please select an Approver Designation");
            $('#approverDesignation').focus();
            return false;
        }
    }

    function loadDesignationByDeptAndType(typeValue, departmentValue, currentStateValue, amountRuleValue, additionalRuleValue, pendingActionsValue, currentDesignation) {

        $("#approverDesignation").empty();
        $("#approverDesignation").append("<option value='-1'>----Choose----</option>");
        $("#approverPositionId").empty();
        $("#approverPositionId").append("<option value='-1'>----Choose----</option>");
        populateapproverDesignation({
            departmentRule: departmentValue,
            type: typeValue,
            amountRule: amountRuleValue,
            additionalRule: additionalRuleValue,
            currentState: currentStateValue,
            pendingAction: pendingActionsValue,
            designation: currentDesignation
        });
    }

    function loadDesignationFromMatrix() {
        var dept = $("#approverDepartment option:selected").text();
        var currentState = $('#currentState').val();
        var amountRule = $('#amountRule').val();
        var additionalRule = $('#additionalRule').val();
        var pendingAction = $('#pendingActions').val();
        var stateType = '<s:property value="%{stateType}"/>';
        var currentDesignation = $('#currentDesignation').val();
        loadDesignationByDeptAndType(stateType, dept, currentState, amountRule, additionalRule, pendingAction, currentDesignation);
    }

    function populateApprover() {
        getUsersByDesignationAndDept();
    }

</script>
<s:if test="%{getNextAction()!='END'}">
    <s:if test="%{!state.isEnded()}">
        <s:hidden id="currentState" name="currentState" value="%{state.value}"/>
    </s:if>
    <s:else>
        <s:hidden id="currentState" name="currentState" value=""/>
    </s:else>
    <s:hidden id="additionalRule" name="additionalRule" value="%{additionalRule}"/>
    <s:hidden id="amountRule" name="amountRule" value="%{amountRule}"/>
    <s:hidden id="workFlowDepartment" name="workFlowDepartment" value="%{workFlowDepartment}"/>
    <s:hidden id="pendingActions" name="pendingActions" value="%{pendingActions}"/>
    <s:hidden id="approverName" name="approverName"/>
    <s:if test="%{!isNewWorkflow()}">
        <s:hidden id="currentDesignation" name="currentDesignation" value=""/>
        <s:if test="%{getNextAction()!='Second level collection pending'}">
            <div class="panel-heading custom_form_panel_heading">
                <div class="panel-title">
                    <s:text name="title.approval.information"/>
                </div>
            </div>
            <div class="row show-row" id="approverDetailHeading">
                <div class="form-group">
                    <label class="col-sm-3 add-margin text-right"><s:text name="wf.approver.department"/><span
                            class="mandatory"></span></label>
                    <div class="col-sm-3 add-margin">
                        <s:select name="approverDepartment" id="approverDepartment"
                                  list="dropdownData.approverDepartmentList"
                                  listKey="id" listValue="name" headerKey="-1" headerValue="----Choose----"
                                  value="%{approverDepartment}" onchange="loadDesignationFromMatrix();"
                                  cssClass="form-control"/>
                        <egov:ajaxdropdown fields="['Text','Value']"
                                           url="workflow/ajaxWorkFlow-getDesignationsByObjectType.action"
                                           id="approverDesignation" dropdownId="approverDesignation"
                                           contextToBeUsed="/eis"/>
                    </div>
                    <label class="col-sm-2 add-margin text-right"><s:text name="wf.approver.designation"/><span
                            class="mandatory"></span></label>
                    <div class="col-sm-3 add-margin">
                        <s:select id="approverDesignation" name="approverDesignation"
                                  list="dropdownData.designationList" listKey="designationId" headerKey="-1"
                                  listValue="designationName" headerValue="----Choose----"
                                  onchange="populateApprover();" onfocus="callAlertForDepartment();"
                                  cssClass="form-control"/>
                        <egov:ajaxdropdown id="approverPositionId" fields="['Text','Value']"
                                           dropdownId="approverPositionId"
                                           url="workflow/ajaxWorkFlow-getPositionByPassingDesigId.action"
                                           contextToBeUsed="/eis"/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-3 add-margin text-right"><s:text name="wf.approver"/><span
                            class="mandatory"></span></label>
                    <div class="col-sm-3 add-margin">
                        <s:select id="approverPositionId" name="approverPositionId" list="dropdownData.approverList"
                                  headerKey="-1" headerValue="----Choose----" listKey="id" listValue="firstName"
                                  onfocus="callAlertForDesignation();"
                                  value="%{approverPositionId}" cssClass="form-control"/>
                    </div>
                </div>
            </div>
        </s:if>
    </s:if>
    <s:else>
        <div class="panel-heading custom_form_panel_heading">
            <div class="panel-title">
                <s:text name="title.approval.information"/>
            </div>
        </div>
        <s:hidden id="currentDesignation" name="currentDesignation" value="%{currentDesignation}"/>
        <s:if test="%{forwardEnabled()==true}">
            <div class="row show-row" id="approverDetailHeading">
                <div class="form-group">
                    <label class="col-sm-3 add-margin text-right"><s:text name="wf.approver.department"/><span
                            class="mandatory"></span></label>
                    <div class="col-sm-3 add-margin">
                        <s:select name="approverDepartment" id="approverDepartment"
                                  list="dropdownData.approverDepartmentList"
                                  listKey="id" listValue="name" headerKey="-1" headerValue="----Choose----"
                                  value="%{approverDepartment}" onchange="loadDesignationFromMatrix();"
                                  cssClass="form-control"/>
                        <egov:ajaxdropdown fields="['Text','Value']"
                                           url="workflow/ajaxWorkFlow-getDesignationsByObjectType.action"
                                           id="approverDesignation" dropdownId="approverDesignation"
                                           contextToBeUsed="/eis"/>
                    </div>
                    <label class="col-sm-2 add-margin text-right"><s:text name="wf.approver.designation"/><span
                            class="mandatory"></span></label>
                    <div class="col-sm-3 add-margin">
                        <s:select id="approverDesignation" name="approverDesignation"
                                  list="dropdownData.designationList" listKey="designationId" headerKey="-1"
                                  listValue="designationName" headerValue="----Choose----"
                                  onchange="populateApprover();" onfocus="callAlertForDepartment();"
                                  cssClass="form-control"/>
                        <egov:ajaxdropdown id="approverPositionId" fields="['Text','Value']"
                                           dropdownId="approverPositionId"
                                           url="workflow/ajaxWorkFlow-getPositionByPassingDesigId.action"
                                           contextToBeUsed="/eis"/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-3 add-margin text-right"><s:text name="wf.approver"/><span
                            class="mandatory"></span></label>
                    <div class="col-sm-3 add-margin">
                        <s:select id="approverPositionId" name="approverPositionId" list="dropdownData.approverList"
                                  headerKey="-1" headerValue="----Choose----" listKey="id" listValue="firstName"
                                  onfocus="callAlertForDesignation();"
                                  value="%{approverPositionId}" cssClass="form-control"/>
                    </div>
                </div>
            </div>
        </s:if>
    </s:else>
</s:if>
<div id="workflowCommentsDiv" align="center" class="row">
    <div class="form-group">
        <label class="col-sm-3 add-margin text-right">
            <s:text name="wf.approver.remarks"/>
            <c:if test="${mode!='disableApprover' || licenseAppType.code=='CLOSURE'}">
                <span class="mandatory"/>
            </c:if>
        </label>
        <div class="col-sm-8 add-margin">
            <s:textarea id="approverComments" name="approverComments" class="form-control"></s:textarea>
        </div>
    </div>
</div>
<script>
    var designationId = '${approverDesignation}';
    var positionId = '${approverPositionId}';
    if ($("#approverDepartment").val() != "-1") {
        $("#approverDepartment").trigger("change");
        window.setTimeout(function () {
            $("#approverDesignation").val(designationId);
            $("#approverDesignation").trigger("change");
            window.setTimeout(function () {
                $("#approverPositionId").val(positionId);
            }, 2000);
        }, 1000);

    }
</script>