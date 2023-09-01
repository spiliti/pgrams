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

<%@ include file="/includes/taglibs.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>
<html>
<head>
    <title><s:text name="page.title.newtrade"/></title>
    <script>

        function validateLicenseForm() {
            if ($('#licenseForm').valid()) {
                if (document.getElementById("mobilePhoneNumber").value.trim() == '' || document.getElementById("mobilePhoneNumber").value == null) {
                    showMessage('newLicense_error', '<s:text name="newlicense.mobilephonenumber.null" />');
                    window.scroll(0, 0);
                    return false;
                } else if (document.getElementById("applicantName").value.trim() == '' || document.getElementById("applicantName").value == null) {
                    showMessage('applicantname_error', '<s:text name="newlicense.applicantname.null" />');
                    $('#applicantName').focus();
                    return false;
                } else if (document.getElementById("fatherOrSpouseName").value.trim() == '' || document.getElementById("fatherOrSpouseName").value == null) {
                    showMessage('fatherorSpouse_error', '<s:text name="newlicense.fatherorspousename.null" />');
                    $('#fatherOrSpouseName').focus();
                    return false;
                } else if (document.getElementById("emailId").value.trim() == '' || document.getElementById("emailId").value == null || !isValidEmailAddress(document.getElementById("emailId").value)) {
                    showMessage('email_error', '<s:text name="newlicense.email.null" />');
                    $('#emailId').focus();
                    return false;
                } else if (document.getElementById("licenseeAddress").value.trim() == '' || document.getElementById("licenseeAddress").value == null) {
                    showMessage('licenseeAddress_error', '<s:text name="newlicense.licenseeaddress.null" />');
                    $('#licenseeAddress').focus();
                    return false;
                } else if (document.getElementById("boundary").value == '') {
                    showMessage('locality_error', '<s:text name="newlicense.locality.null" />');
                    $('#boundary').focus();
                    return false;
                } else if (document.getElementById("ownershipType").value == '-1') {
                    showMessage('ownership_error', '<s:text name="newlicense.ownershiptype.null" />');
                    $('#ownershipType').focus();
                    return false;
                } else if (document.getElementById("nameOfEstablishment").value.trim() == '' || document.getElementById("nameOfEstablishment").value == null) {
                    showMessage('nameOfEstablishment_error', '<s:text name="newlicense.tradeTitle.null" />');
                    $('#nameOfEstablishment').focus();
                    return false;
                }
                else if (document.getElementById("address").value.trim() == '' || document.getElementById("address").value == null) {
                    showMessage('address_error', '<s:text name="newlicense.licenseaddress.null" />');
                    $('#address').focus();
                    return false;
                } else if (document.getElementById("buildingType").value == '-1') {
                    showMessage('natureOfBusiness_error', '<s:text name="newlicense.buildingtype.null" />');
                    $('#buildingType').focus();
                    return false;
                } else if (document.getElementById("category").value == '-1') {
                    showMessage('category_error', '<s:text name="newlicense.category.null" />');
                    $('#category').focus();
                    return false;
                } else if (document.getElementById("subCategory").value == '-1') {
                    showMessage('subCategory_error', '<s:text name="newlicense.subcategory.null" />');
                    $('#subCategory').focus();
                    return false;
                } else if (document.getElementById("tradeArea_weight").value == '' || document.getElementById("tradeArea_weight").value == null) {
                    showMessage('tradeArea_weight_error', '<s:text name="newlicense.tradeareaweight.null" />');
                    $('#tradeArea_weight').focus();
                    return false;
                } else if (document.getElementById("uom").value == "") {
                    showMessage('uom_error', '<s:text name="newlicense.uom.null" />');
                    $('#uom').focus();
                    return false;
                } else if (document.getElementById("startDate").value == '' || document.getElementById("startDate").value == null) {
                    showMessage('newLicense_error', '<s:text name="newlicense.startDate.null" />');
                    window.scroll(0, 0);
                    return false;
                } else if (document.getElementById("showAgreementDtl").checked) {
                    if (document.getElementById("agreementDate").value == '' || document.getElementById("agreementDate").value == null) {
                        showMessage('newLicense_error', '<s:text name="newlicense.agreementDate.null" />');
                        window.scroll(0, 0);
                        return false;
                    } else if (document.getElementById("agreementDocNo").value.trim() == '' || document.getElementById("agreementDocNo").value == null) {
                        showMessage('newLicense_error', '<s:text name="newlicense.agreementDocNo.null" />');
                        window.scroll(0, 0);
                        return false;
                    }

                }


                var mobileno = document.getElementById('mobilePhoneNumber').value;
                if (mobileno.length > 0 && mobileno.length < 10) {
                    $('#mobileError').removeClass("hide");
                    $("#mobilePhoneNumber").focus();
                    return false;
                }
                else {
                    return true;
                }
            }
            else

                return false;
        }

        //This method will be called from included jsp
        function onSubmitValidations() {
            return validateLicenseForm();
        }

        function formsubmit() {

            if (!validateLicenseForm()) {
                return false;
            }
            return onSubmit();
            // $("#licenseForm").submit();
        }

        function onSave() {

            if (!validateLicenseForm()) {
                return false;
            }
            document.newTradeLicense.action = '${pageContext.request.contextPath}/newtradelicense/newTradeLicense-save.action';
            return true;
        }

        $(document).ready(function () {
            $("#mobilePhoneNumber").change(function () {
                var mobileno = document.getElementById('mobilePhoneNumber').value;
                if (mobileno.length > 0 && mobileno.length < 10) {
                    $('#mobileError').removeClass("hide");
                    $("#mobilePhoneNumber").focus();
                    return false;
                }
                else if (mobileno.length == 10)
                    $('#mobileError').hide();
            });
            $("#emailId").change(function () {
                if (document.getElementById("emailId").value.trim() == '' || document.getElementById("emailId").value == null || !isValidEmailAddress(document.getElementById("emailId").value)) {
                    showMessage('email_error', '<s:text name="newlicense.email.null" />');
                    $("#emailId").focus();
                    return false;
                }
                else if (!document.getElementById("emailId").value.trim() == '' || !document.getElementById("emailId").value == null || isValidEmailAddress(document.getElementById("emailId").value))
                    $('#email_error').hide();
            });
        })

        function onCancelSubmit() {
            clearMessage('newLicense_error');
            toggleFields(false, "");
            document.getElementById("workFlowAction").disabled = false;
            document.getElementById("workFlowAction").value = "Reject";
            document.newTradeLicense.action = '${pageContext.request.contextPath}/newtradelicense/newTradeLicense-approve.action';
            return true;
        }

        //this method gets called from included jsp
        function onSubmit() {
            var mode = $("#mode").val();
            var workflowaction = $("#workFlowAction").val();
            <s:if test="%{!isNewWorkflow()}">
            <s:if test="%{workflowaction != null && workflowaction == 'Generate Provisional Certificate'}">
            window.open("/tl/viewtradelicense/generate-provisional-certificate.action?model.id=" + $('#id').val(), 'gc' + $('#id').val(), 'scrollbars=yes,width=1000,height=700,status=yes');
            return false;
            </s:if>
            <s:if test="%{mode!=null && ((mode=='view' || mode=='editForApproval' || mode== 'disableApprover') &&  mode!='editForReject' )}">
            clearMessage('newLicense_error');
            toggleFields(false, "");
            document.newTradeLicense.action = '${pageContext.request.contextPath}/newtradelicense/newTradeLicense-approve.action';
            </s:if>
            <s:elseif  test="%{mode!=null && mode=='editForReject'}">
            clearMessage('newLicense_error');
            toggleFields(false, "");
            document.newTradeLicense.action = '${pageContext.request.contextPath}/newtradelicense/newTradeLicense-approve.action';
            </s:elseif>
            <s:else>
            clearMessage('newLicense_error');
            toggleFields(false, "");
            document.newTradeLicense.action = '${pageContext.request.contextPath}/newtradelicense/newTradeLicense-create.action';
            </s:else>
            return true;
            </s:if>
            <s:else>
            clearMessage('newLicense_error');
            toggleFields(false, "");
            <s:if test="%{hasState()}">
            document.newTradeLicense.action = '${pageContext.request.contextPath}/newtradelicense/newTradeLicense-approve.action';
            </s:if>
            <s:else>
            document.newTradeLicense.action = '${pageContext.request.contextPath}/newtradelicense/newTradeLicense-create.action';
            </s:else>
            return true;
            </s:else>
        }
    </script>
    <script>
        function onBodyLoad() {
            var currentState = document.getElementById("currentWfstate").value;
            showHideAgreement();
            <s:if test="%{!isNewWorkflow()}">
            if (currentState == 'Second level fee collected') {
                $("span").remove(".mandatory");
            }
            try {
                if (document.getElementById("mode").value == 'disableApprover') {
                    toggleFields(true, ['Submit', 'Reject', 'button2', 'Approve', 'approverComments', 'Sign', 'Preview', 'Generate Certificate']);
                    $(".show-row").hide();
                    $('#approverComments').removeAttr('<span class="mandatory"></span>');
                    $('#approverDepartment').removeAttr('<span class="mandatory"></span>');
                    $('#approverDesignation').removeAttr('<span class="mandatory"></span>');
                    $('#approverPositionId').removeAttr('<span class="mandatory"></span>');
                    $('#workflowCommentsDiv label').text('<s:text name="newlicense.fieldInspection.label" />');
                }
            } catch (e) {
                console.error(e);
            }

            try {
                if (document.getElementById("mode").value == 'ACK') {

                    toggleFields(true, ['approverDepartment', 'approverDesignation', 'approverPositionId', 'approverComments', 'Generate Certificate',
                        'Forward', 'Reject', 'Reassign', 'button2', 'Approve', 'Sign', 'Preview', 'closeBtn', 'closeDiv', 'currentWfstate']);
                    //remove onclick event for propertyno search button
                    $("#searchImg").removeAttr("onclick");
                    // remove onclick event for add and delete button having class = add-padding
                    $('.add-padding').attr('onclick', '').unbind('click');
                    // renaming approver remarks label for second level of workflow
                    <s:if test="%{getNextAction()!='END'}">
                    $('#workflowCommentsDiv label').text('<s:text name="newlicense.fieldInspection.label" />');
                    $('#workflowCommentsDiv label').append('<span class="mandatory"></span>');
                    </s:if>
                    document.getElementById("btncancel").disabled = false;
                    document.getElementById("closebn").disabled = false;
                    document.getElementById('workflowDiv').style.visibility = 'hidden';

                }
            } catch (e) {
                console.error(e);
            }
            try {
                if (document.getElementById("mode").value == 'view' || document.getElementById("mode").value == 'editForReject') {

                    toggleFields(true, ['approverDepartment', 'approverDesignation', 'approverPositionId', 'approverComments', 'Generate Certificate',
                        'Forward', 'Reject', 'Reassign', 'button2', 'Approve', 'Sign', 'Preview', 'currentWfstate']);
                    //remove onclick event for propertyno search button
                    $("#searchImg").removeAttr("onclick");
                    // remove onclick event for add and delete button having class = add-padding
                    $('.add-padding').attr('onclick', '').unbind('click');
                    // renaming approver remarks label for second level of workflow
                    <s:if test="%{getNextAction()!='END'}">
                    $('#workflowCommentsDiv label').text('<s:text name="newlicense.fieldInspection.label" />');
                    $('#workflowCommentsDiv label').append('<span class="mandatory"></span>');
                    </s:if>
                    if (currentState == 'SI/MHO approved') {
                        document.getElementById('approverDetailHeading').hidden = true
                    }

                }
            } catch (e) {
                console.error(e);
            }
            try {
                if (document.getElementById("mode").value == 'editForApproval') {
                    toggleFields(true, ['approverDepartment', 'approverDesignation', 'approverPositionId', 'approverComments', 'Generate Certificate',
                        'Forward', 'Reject', 'Reassign', 'button2', 'Approve']);
                    //remove onclick event for propertyno search button
                    document.getElementById("tradeArea_weight").disabled = false;
                    $("#searchImg").removeAttr("onclick");
                    // remove onclick event for add and delete button having class = add-padding
                    $('.add-padding').attr('onclick', '').unbind('click');
                    // renaming approver remarks label for second level of workflow
                    <s:if test="%{getNextAction()!='END'}">
                    $('#workflowCommentsDiv label').text('<s:text name="newlicense.fieldInspection.label" />');
                    $('#workflowCommentsDiv label').append('<span class="mandatory"></span>');
                    </s:if>
                }
            } catch (e) {
                console.error(e);
            }
            if (currentState == 'First level fee collected')
                $("#certificateDiv").show();
            if ($('#boundary')) {
                $('#boundary').attr('disabled', false);
            }
            if ($('#parentBoundary')) {
                $('#parentBoundary').attr('disabled', false);
            }
            if ($("#mode").val() != 'disableApprover') {
                $(".supportdocs").prop("disabled", false);
            }
            </s:if>
            <s:else>
            $('#licenseForm :input').not(':hidden').not(':button').each(function (input, item) {
                if ($(item).closest('#workflowDiv').length == 0)
                    $(item).attr('disabled', true);
            });
            var fields = $("#fieldenabled").val();
            fields = fields.split(",");
            if (fields == 'all')
                $('#licenseForm :input').each(function (input, item) {
                    $(item).attr('disabled', false);
                });
            else if (fields != 'none')
                $.each(fields, function (key, value) {
                    $('#' + (value)).attr('disabled', false);
                    console.log($('#' + (value)));
                });
            </s:else>
        }
    </script>

</head>
<body onload="onBodyLoad()">
<div id="newLicense_error" class="error-msg" style="display:none;" align="center"></div>
<div class="row">
    <div class="col-md-12">
        <div class="text-right error-msg" style="font-size:14px;"><s:text name="dateofapplication.lbl"/> : <s:date
                name="applicationDate" format="dd/MM/yyyy"/></div>
        <s:if test="%{applicationNumber!=null}">
            <div class="text-right error-msg" style="font-size:14px;"><s:text name="application.num"/> : <s:property
                    value="%{applicationNumber}"/></div>
        </s:if>
        <s:if test="%{hasErrors()}">
            <div class="text-center error-msg" align="center" style="font-size: 14px;">
                <s:actionerror/>
                <s:fielderror/>
            </div>
        </s:if>
        <s:if test="%{hasActionMessages()}">
            <div class="messagestyle">
                <s:actionmessage theme="simple"/>
            </div>
        </s:if>
        <s:if test="%{getMessage() != null}">
            <div class="alert alert-danger view-content" style="font-size: 18px;">
                <s:text name="%{getMessage()}"/>
            </div>
        </s:if>

        <s:form id="licenseForm" name="newTradeLicense" action="newTradeLicense-create" theme="simple"
                enctype="multipart/form-data"
                cssClass="form-horizontal form-groups-bordered" validate="true">
            <s:push value="model">
                <s:token/>
                <s:hidden name="actionName" value="create"/>
                <s:hidden id="detailChanged" name="detailChanged"/>
                <s:hidden id="applicationDate" name="applicationDate"/>
                <s:hidden id="mode" name="mode" value="%{mode}"/>
                <s:hidden id="currentWfstate" name="currentWfstate" value="%{state.value}"/>
                <s:hidden name="model.id" id="tlid"/>
                <s:hidden name="id" id="id"/>
                <s:hidden name="feeTypeId" id="feeTypeId"/>
                <input type="hidden" name="applicationNo" value="${param.applicationNo}" id="applicationNo"/>
                <s:hidden name="newWorkflow" id="newWorkflow"/>
                <s:if test="%{!isNewWorkflow()}">
                    <s:if test="%{state==null}">
                        <s:hidden id="additionalRule" name="additionalRule" value="%{additionalRule}"/>
                    </s:if>
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <s:if test="%{mode=='edit'}">
                                <div class="panel-title" style="text-align:center">
                                    <s:text name='page.title.edittrade'/>
                                </div>
                            </s:if>
                            <s:else>
                                <div class="panel-title" style="text-align:center">
                                    <s:if test="%{licenseAppType.code=='RENEW'}">
                                        <s:text name='renewtradeLicense.heading'/>
                                    </s:if>
                                    <s:else>
                                        <s:text name='newtradeLicense.heading'/>
                                    </s:else>
                                </div>
                            </s:else>

                            <ul class="nav nav-tabs" id="settingstab">
                                <li class="active"><a data-toggle="tab" href="#tradedetails" data-tabidx="0"
                                                      aria-expanded="true"><s:text name="license.tradedetail"/></a></li>
                                <li class=""><a data-toggle="tab" href="#tradeattachments" data-tabidx="1"
                                                aria-expanded="false"><s:text name="license.support.docs"/></a></li>
                            </ul>
                        </div>
                        <div class="panel-body">
                            <div class="tab-content">
                                <div class="tab-pane fade active in" id="tradedetails">
                                    <%@ include file='../common/licensee.jsp' %>
                                    <%@ include file='../common/license-address.jsp' %>
                                    <%@ include file='../common/license.jsp' %>
                                </div>
                                <div class="tab-pane fade" id="tradeattachments">
                                    <%@include file="../common/supportdocs-new.jsp" %>
                                </div>
                            </div>
                        </div>
                    </div>
                    <%@ include file='../common/license-fee-detail-view.jsp' %>
                    <s:if test="%{transitionInprogress() && !licenseHistory.isEmpty}">
                        <div class="panel panel-primary">
                            <%@ include file='../common/license-workflow-history.jsp' %>
                        </div>
                    </s:if>
                    <div style="text-align: center;" hidden="true" id="closeDiv">
                        <input type="button" name="closeBtn" id="closeBtn" value="Close"
                               class="button" onclick="window.close();" style="margin:0 5px"/>
                    </div>
                    <s:if test="%{state!=null && state.value!='License Created' && egwStatus.code!='SECONDLVLCOLLECTIONPENDING'}">
                        <div class="panel panel-primary" id="workflowDiv">
                            <%@ include file='../common/license-workflow-dropdown.jsp' %>
                            <%@ include file='../common/license-workflow-button.jsp' %>
                        </div>
                    </s:if>
                    <s:elseif
                            test="%{state!=null && (state.value=='License Created' || egwStatus.code=='SECONDLVLCOLLECTIONPENDING')}">
                        <div class="text-center">
                            <s:hidden id="workFlowAction" name="workFlowAction"/>
                            <s:hidden name="currentState" value="%{state.value}"/>
                            <button type="submit" id="btncancel" class="btn btn-primary"
                                    onclick="return onCancelSubmit();">
                                Cancel
                            </button>
                            <button type="button" id="closebn" class="btn btn-default" onclick="window.close();">
                                Close
                            </button>
                        </div>
                    </s:elseif>
                    <s:else>
                        <div class="row">
                            <div class="text-center">
                                <button type="submit" id="btnsave" class="btn btn-primary"
                                        onclick="return formsubmit();">
                                    Save
                                </button>
                                <button type="button" id="btnclose" class="btn btn-default" onclick="window.close();">
                                    Close
                                </button>
                            </div>
                        </div>
                    </s:else>
                </s:if>
                <s:else>
                    <div class="panel panel-primary">
                        <s:hidden id="fieldenabled" name="enabledFields" value="%{getEnabledFields()}"/>
                        <div class="panel-heading">
                            <s:if test="%{mode=='edit'}">
                                <div class="panel-title" style="text-align:center">
                                    <s:text name='page.title.edittrade'/>
                                </div>
                            </s:if>
                            <s:else>
                                <div class="panel-title" style="text-align:center">
                                    <s:if test="%{licenseAppType.code=='RENEW'}">
                                        <s:text name='renewtradeLicense.heading'/>
                                    </s:if>
                                    <s:else>
                                        <s:text name='newtradeLicense.heading'/>
                                    </s:else>
                                </div>
                            </s:else>

                            <ul class="nav nav-tabs" id="settingstab">
                                <li class="active"><a data-toggle="tab" href="#tradedetails" data-tabidx="0"
                                                      aria-expanded="true"><s:text name="license.tradedetail"/></a></li>
                                <li class=""><a data-toggle="tab" href="#tradeattachments" data-tabidx="1"
                                                aria-expanded="false"><s:text name="license.support.docs"/></a></li>
                            </ul>
                        </div>
                        <div class="panel-body">
                            <div class="tab-content">
                                <div class="tab-pane fade active in" id="tradedetails">
                                    <%@ include file='../common/licensee.jsp' %>
                                    <%@ include file='../common/license-address.jsp' %>
                                    <%@ include file='../common/license.jsp' %>
                                </div>
                                <div class="tab-pane fade" id="tradeattachments">
                                    <%@include file="../common/supportdocs-new.jsp" %>
                                </div>
                            </div>
                        </div>
                    </div>
                    <%@ include file='../common/license-fee-detail-view.jsp' %>
                    <s:if test="%{transitionInprogress() && !licenseHistory.isEmpty}">
                        <div class="panel panel-primary">
                            <%@ include file='../common/license-workflow-history.jsp' %>
                        </div>
                    </s:if>
                    <div style="text-align: center;" hidden="true" id="closeDiv">
                        <input type="button" name="closeBtn" id="closeBtn" value="Close"
                               class="button" onclick="window.close();" style="margin:0 5px"/>
                    </div>
                    <div class="panel panel-primary" id="workflowDiv">
                        <%@ include file='../common/license-workflow-dropdown.jsp' %>
                        <%@ include file='../common/license-workflow-button.jsp' %>
                        <s:if test="%{hasState()}">
                            <div class="text-center">
                                <button type="submit" id="btsave" class="btn btn-primary"
                                        onclick="return onSave();">Save
                                </button>
                            </div>
                        </s:if>
                    </div>
                </s:else>
            </s:push>
        </s:form>
        <div style="text-align: center;" id="btndiv">
            <input type="button" class="btn btn-primary" id="certificateDiv" value="Generate Provisional Certificate"
                   style="display: none;"
                   onclick="window.open('/tl/viewtradelicense/generate-provisional-certificate.action?model.id=
                   <s:property
                           value="%{id}"/>', '_blank', 'height=650,width=980,scrollbars=yes,left=0,top=0,status=yes');"/>
        </div>
    </div>
</div>
<jsp:include page="../common/process-owner-reassignment.jsp"/>
<script src="<cdn:url  value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/egi'/>"></script>
</body>
</html>