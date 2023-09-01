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

<div class="buttonbottom" align="center">
    <s:hidden id="workFlowAction" name="workFlowAction"/>
    <table>
        <tr>
            <td>
                <s:iterator value="validActions" var="name">
                <s:if test="%{name!=''}">
            <td>
                <input type="button" class="buttonsubmit custom-button" value="${name}"
                       id="${name}" name="${name}" style="margin:0 5px"/>
            </td>
            </s:if>
            </s:iterator>
            <td><input type="button" name="button2" id="button2" value="Close"
                       class="button" onclick="window.close();" style="margin:0 5px"/></td>
            </td>
        </tr>
    </table>
</div>
<script>
    $(document).ready(function () {
        $(".buttonsubmit").click(function () {
            var name = $(this).val();
            if (name == 'Reassign') {
                $(".reassign-screen").show();
                $('#approvalPosition').find('option:gt(0)').remove();
                var result = [];
                $.ajax({
                    url: "/tl/license/reassign",
                    type: "GET",
                    dataType: "json",
                    cache: false,
                    success: function (data) {
                        $.each(data, function (i) {
                            var obj = {};
                            obj['id'] = i;
                            obj['text'] = data[i];
                            result.push(obj);
                        });
                        $.each(result, function (i) {
                            $('#approvalPosition').append($('<option>').text(result[i].text).attr('value', result[i].id));
                        })
                        $('.reassign-screen').modal('show', {backdrop: 'static'});
                    },
                });
            } else {
                if (!onSubmitValidations()) {
                    return false;
                }
                $("#workFlowAction").val(name);
                var approverDeptId = $("#approverDepartment");
                var approverDesgId = $("#approverDesignation");
                var approverPosId = $("#approverPositionId");
                var approverComments = $("#approverComments").val();
                if (approverPosId && approverPosId.val() != -1) {
                    var approver = $("#approverPositionId option:selected").text();
                    $("approverName").val(approver.split('~')[0]);
                }
                <s:if test="%{getNextAction()!='END'}">
                if (name == "Forward" || name == "forward") {
                    if (approverDeptId && approverDeptId.val() == -1) {
                        bootbox.alert("Please select the Approver Department ");
                        return false;
                    } else if (approverDesgId && approverDesgId.val() == -1) {
                        bootbox.alert("Please select the Approver Designation ");
                        return false;
                    } else if (approverPosId && approverPosId.val() == -1) {
                        bootbox.alert("Please select the Approver ");
                        return false;
                    }
                }
                </s:if>
                if (name == "Save" || name == "save" || name == "Forward" || name == "forward" || name == "approve" || name == "Approve" || name == "Sign" || name == "sign") {
                    if (approverComments == null || approverComments == "" || approverComments.trim().length == 0) {
                        bootbox.alert("Please enter the Remarks ");
                        return false;
                    }
                }
                if ((name == "Reject" || name == "reject" || name == "Cancel")) {
                    if (approverComments == null || approverComments == "" || approverComments.trim().length == 0) {
                        bootbox.alert("Please enter rejection Remarks ");
                        return false;
                    }
                }
                var button = $(this);
                if (name == "Sign") {
                    <s:if test="%{!isDigitalSignatureEnabled()}">
                    bootbox.alert("Digital signature is not enabled, please enabled and try again.");
                    return false;
                    </s:if>
                }
                bootbox.confirm({
                    message: 'Please confirm, if you wish to ' + name + ' this application.',
                    buttons: {
                        'cancel': {
                            label: 'No',
                            className: 'btn-default'
                        },
                        'confirm': {
                            label: 'Yes',
                            className: 'btn-danger'
                        }
                    },
                    callback: function (result) {
                        if (result) {
                            var formOk = onSubmit();
                            if (formOk) {
                                button.closest('form').submit();
                            }
                        }
                    }

                });
            }
        });
    });
</script>