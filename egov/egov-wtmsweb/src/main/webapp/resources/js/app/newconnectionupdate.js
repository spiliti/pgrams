/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2018  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces, 
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any 
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines, 
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
$(document).ready(
    function () {
        var validator = $("#waterConnectionForm").validate({
            highlight: function (element, errorClass) {
                $(element).fadeOut(function () {
                    $(element).fadeIn();
                });
            }
        });

        if ($("#connectionType").val() === "METERED") {
            $(".showfields").show();
            $("#waterSupplyType").attr('required', 'required');
            $("#buildingName").attr('required', 'required');
        }

        var typeOfConnection = $('#typeOfConnection').val();
        if (typeOfConnection == "CHANGEOFUSE") {
            $("#waterSourceDropdown").prop("disabled", true);
            $("#connectionCategorie").prop("disabled", true);
        }
        var executionDate = $("#executionDate").val();
        var status = $('#statuscode').val();
        var wfstate = $('#wfstate').val();
        var currentstate = $('#wfstateDesc').val();
        var mode = $('#mode').val();
        var source = $("#source").val();
        if (("SURVEY" == source || "CSC" === source || "CITIZENPORTAL" == source || "MEESEVA" == source || "SYSTEM" == source || "" == source) &&
            typeOfConnection == "REGLZNCONNECTION" && ((mode === 'addDemand' || mode === 'fieldInspection') && executionDate === "")) {
            $("#Forward").hide();
            $('#approvalComent').removeAttr('required');
            $('#approvalComent').hide();
        } else {
            $("#Forward").show();
        }
        var isCommissionerLoggedIn = $('#isCommissionerLoggedIn').val();
        $('#approvalComent').show();

        var closerConnection = $('#closerConnection').val();
        var approvalPositionExist = $('#approvalPositionExist').val();

        if (currentstate != 'Rejected' && status == 'ESTIMATIONNOTICEGENERATED') {
            if (!$('#proceedWithoutDonation').val() && $("#mode").val()!="error") {
                bootbox.alert("Collect fees to continue application");
                $('#approvalDepartment').removeAttr('required');
                $('#approvalDesignation').removeAttr('required');
                $(".show-row").hide();
                $("#Forward").hide();
                return false;
            }
            else {
                $("#Forward").hide();
                $("#Reject").hide();
            }
        }

        if (status == 'DIGITALSIGNATUREPENDING' || status == 'CLOSUREDIGSIGNPENDING' || status == 'RECONNDIGSIGNPENDING') {
            $("#Approve").hide();
        }

        if (status == 'ESTIMATIONAMOUNTPAID' || status == 'CLOSERINPROGRESS' || status == 'RECONNECTIONINPROGRESS') {
            $("#Sign").hide();
            $("#Preview").hide();
        }
        if ((typeOfConnection == "REGLZNCONNECTION" && mode == "fieldInspection") || (approvalPositionExist != 0
            && ((status == 'CREATED' && wfstate != null) || status == 'VERIFIED' || status == 'WORKORDERGENERATED' || status == 'APPROVED'
            ))) {
            $(".show-row").hide();
            $('#approverDetailHeading').hide();
            $('#approvalDepartment').removeAttr('required');
            $('#approvalDesignation').removeAttr('required');
            $('#approvalPosition').removeAttr('required');
        }
        if (isCommissionerLoggedIn == 'true' || (typeOfConnection == 'REGLZNCONNECTION' && approvalPositionExist == '' && status == 'CREATED' && executionDate == '' && currentstate != 'Rejected')) {
            $(".show-row").hide();
            $('#approverDetailHeading').hide();
            removedMandatoryCheckForApprovalDetails();
        }
        if (isCommissionerLoggedIn == 'true' && (status == 'CLOSUREDIGSIGNPENDING' || status == 'RECONNDIGSIGNPENDING')) {
            $(".show-row").hide();
            $('#approverDetailHeading').hide();
            removedMandatoryCheckForApprovalDetails();
        }
        if (approvalPositionExist != 0
            && (closerConnection != null)
            && (status == 'CLOSERAPPROVED' || status == 'RECONNECTIONAPPROVED')) {
            $(".show-row").hide();
            $('#approverDetailHeading').hide();
            removedMandatoryCheckForApprovalDetails();
        }
        if (approvalPositionExist != 0
            && (mode == 'closeredit' && closerConnection != null)) {
            $(".show-row").show();
            $('#approverDetailHeading').show();
            addMandatoryCheckForApprovalDetails();
        }

        if (approvalPositionExist != 0 && status == 'CREATED'
            && (mode == 'edit' || mode == '')) {
            $(".show-row").show();
            $('#approverDetailHeading').show();
            addMandatoryCheckForApprovalDetails();
        }
        if (status == 'ESTIMATIONAMOUNTPAID'
            && (approvalPositionExist == '' || approvalPositionExist == 0)) {
            $(".show-row").show();
            addMandatoryCheckForApprovalDetails();
        }

        function validateDateRange(fromDate, toDate) {
            if (fromDate != "" && toDate != "") {
                var stsplit = fromDate.split("/");
                var ensplit = toDate.split("/");

                startDate = Date.parse(stsplit[1] + "/" + stsplit[0] + "/" + stsplit[2]);
                endDate = Date.parse(ensplit[1] + "/" + ensplit[0] + "/" + ensplit[2]);

                var difference = (endDate - startDate) / (86400000 * 7);
                if (difference < 0) {
                    return false;
                } else {
                    return true;
                }
            }
            return true;
        }

        $("form").submit(function () {
            if ($('form').valid()) {
                $('.loader-class').modal('show', {
                    backdrop: 'static'
                });
            } else
                $('.loader-class').modal('hide');
        });

        $(".btn-primary").click(
            function () {
                var action = document.getElementById("workFlowAction").value;
                var status = $('#statuscode').val();
                var isSanctionedDetailEnable = $('#isSanctionedDetailEnable').val();
                if (action == 'Reject' && status == 'ESTIMATIONNOTICEGENERATED') {
                    $('#approvalDepartment').val('');
                    $('#approvalDesignation').val('');
                    $('#approvalPosition').val('');
                }
                if ((action == 'Generate Estimation Notice' && status == 'VERIFIED') || (action == 'Generate WorkOrder' && status == 'APPROVED')
                    || (status == 'ESTIMATIONAMOUNTPAID' && action == 'Forward')) {
                    if (action == 'Forward' && ($('#approvalDepartment').val() == '' || $('#approvalDesignation').val() == ''
                        || $('#approvalPosition').val() == '')) {
                        addMandatoryCheckForApprovalDetails();
                        bootbox.alert("Please select approver details!");
                        $('#approvalDepartment').focus();
                        return false;
                    }
                    else {
                        document.getElementById("mode").value = mode;
                        document.forms[0].submit();
                    }
                }
                if (isSanctionedDetailEnable == 'true' && action == 'Forward') {
                    $('#approvalNumber').removeAttr('required');
                    $('#approvalDate').removeAttr('required');
                } else if (isSanctionedDetailEnable == 'true' && action == 'Approve') {
                    if ($('#approvalNumber').val() == '') {
                        bootbox.alert("Please enter Sanction Number!");
                        $('#approvalNumber').focus();
                        return false;
                    }
                }

                if ((action == 'Forward' && status == 'CLOSERINITIATED')) {
                    document.getElementById("mode").value = mode;
                    document.forms[0].submit();
                }
                if (status == 'ESTIMATIONNOTICEGENERATED') {
                    if (action == null || action == '')
                        return false;
                }
                if (action == 'Approve' || action == 'Sign' || action == 'Preview') {
                    removedMandatoryCheckForApprovalDetails();
                    $('#approvalDepartment').val('');
                    $('#approvalDesignation').val('');
                    $('#approvalPosition').val('');
                    document.getElementById("mode").value = mode;
                    document.forms[0].submit();
                }

                if (status == 'CLOSERINITIATED'
                    && action == 'Reject') {

                    $('#Reject').attr('formnovalidate', 'true');
                    var approvalComent = $('#approvalComent').val();
                    if (approvalComent == "") {
                        bootbox.alert("Please enter rejection comments!");
                        $('#approvalComent').focus();
                        return false;
                    } else {
                        validateWorkFlowApprover(action);
                        document.forms[0].submit();
                    }
                }

                if (status == 'RECONNECTIONINITIATED' && action == 'Reject') {
                    $('#Reject').attr('formnovalidate', 'true');
                    var approvalComent = $('#approvalComent').val();
                    if (approvalComent == "") {
                        bootbox.alert("Please enter rejection comments!");
                        $('#approvalComent').focus();
                        return false;
                    } else {
                        validateWorkFlowApprover(action);
                        document.forms[0].submit();
                    }
                }
                if (status == 'CREATED' && action == 'Submit' && mode == 'fieldInspection') {
                    $('#approvalComent').removeAttr('required');
                    if ($('form').valid()) {
                        var estimationCharge = $('#estimationCharges').val();
                        if (estimationCharge < 0) {
                            bootbox.alert("Please enter the value greater than ZERO for Estimation charges!");
                            $('#estimationCharges').focus();
                            return false;
                        }
                        else if ($("#applicationType").val() === 'REGLZNCONNECTION' && $("#waterTaxDueforParent").val() > 0) {
                            bootbox.alert("Water Tax demand is due. Please pay the amount " + $("#waterTaxDueforParent").val() + " and continue workflow");
                            return false;
                        }
                        else {
                            validateWorkFlowApprover(action);
                            document.forms[0].submit();
                        }
                    } else {
                        setTimeout(function () {
                            off = ($(document).scrollTop() - 140);
                            $('html, body').animate(
                                {
                                    scrollTop: off
                                },
                                0);
                        }, 100);
                    }
                } else if (status == 'WORKORDERGENERATED' && action == 'Execute Tap') {
                    validateTapExecutionDate(action);
                } else if (status == 'CREATED' && action == 'Reject') {

                    $('#pipelineDistance').val(0);
                    if ($('#estimationCharges'))
                        $('#estimationCharges').val(0);

                    $('#Reject').attr('formnovalidate', 'true');
                    var approvalComent = $('#approvalComent').val();
                    if (approvalComent == "") {
                        bootbox.alert("Please enter rejection comments!");
                        $('#approvalComent').focus();
                        return false;
                    } else {
                        validateWorkFlowApprover(action);
                        document.forms[0].submit();
                    }
                } else {
                    document.getElementById("mode").value = mode;
                    var applicationDate = $('#applicationDate').html();
                    var approvalDate = $(
                        '#approvalDate').val();
                    if (status == 'ESTIMATIONAMOUNTPAID' && action == 'Approve') {
                        if (approvalDate == '') {
                            $('#approvalDate').attr('required', 'required');
                        }
                        if (applicationDate != '' && approvalDate != '') {
                            if ($('form').valid()) {
                                validateWorkFlowApprover(action);
                                document.forms[0].submit();
                            }
                        }
                    } else if (action == '' && (status == 'CREATED' || status == 'CLOSERINITIATED' || status == 'RECONNECTIONINITIATED')) {
                        return false;
                    }
                    else if (action != 'Cancel') {
                        if (validateForm(validator))
                            document.forms[0].submit();
                        else
                            return false;
                    }
                }
            });

        changeCategory();

        function changeCategory() {
            if ($('#connectionCategorie :selected').text().localeCompare("BPL") == 0) {
                $("#cardHolderDiv").show();
                $("#bplCardHolderName").attr('required', 'required');
                $("#bplCardHolderName").val();
            } else {
                $("#cardHolderDiv").hide();
                $("#bplCardHolderName").removeAttr('required');
                $("#bplCardHolderName").val(null);
            }
        }

        $('#connectionCategorie').change(function () {
            changeCategory();
        });
        changeLabel();

        function changeLabel() {
            if ($('#usageType :selected').text().localeCompare(
                "Lodges") == 0) {
                $('#persons').hide();
                $('#rooms').show();
                $('#personsdiv').hide();
                $('#roomsdiv').show();
                $('#numberOfPerson').val(null);
            } else {
                $('#persons').show();
                $('#rooms').hide();
                $('#personsdiv').show();
                $('#roomsdiv').hide();
                $('#numberOfRooms').val(null);

            }
        }

        $('#usageType').change(function () {
            changeLabel();
        });

        $('#executionDate').blur(function () {
            var action = $('#workFlowAction').val();
            validateTapExecutionDate(action);
        });

        function validateTapExecutionDate(action) {
            var applicationDate = $('#appDate').val();
            var executionDate = $('#executionDate').val();
            if (applicationDate != '' && executionDate != '') {
                if (validateDateRange(applicationDate, executionDate)) {
                    validateWorkFlowApprover(action);

                } else {
                    bootbox.alert("The Execution Date can not be less than the Date of Application.");
                    $('#executionDate').val('');
                    return false;
                }
            }
        }

        if ($('#meterFocus').val() == 'true') {
            $('#meterSerialNumber').focus();
        }


    });

function removedMandatoryCheckForApprovalDetails() {
    $('#approvalDepartment').removeAttr('required');
    $('#approvalDesignation').removeAttr('required');
    $('#approvalPosition').removeAttr('required');
}

function addMandatoryCheckForApprovalDetails() {
    $('#approvalDepartment').attr('required', 'required');
    $('#approvalDesignation').attr('required', 'required');
    $('#approvalPosition').attr('required', 'required');
}


function validateForm(validator) {
    if ($('#waterConnectionForm').valid()) {
        return true;
    } else {
        $errorInput = undefined;

        $.each(validator.invalidElements(),
            function (index, elem) {

                if (!$(elem).is(":visible") && !$(elem).val() && index == 0
                    && $(elem).closest('div').find('.bootstrap-tagsinput').length > 0) {
                    $errorInput = $(elem);
                }

                if (!$(elem).is(":visible") && !$(elem).closest('div.panel-body').is(":visible")) {
                    $(elem).closest('div.panel-body').show();
                }
            });

        if ($errorInput)
            $errorInput.tagsinput('focus');

        validator.focusInvalid();
        return false;
    }
}