/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
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

$(document).ready(function () {
    $("#btn_submit").click(function () {
        if ($("#btn_submit").name == 'Close') {
            return true;
        }
        if ($('#inc_messge').val() == '') {
            $('#inc_messge').addClass('error');
        } else {
            document.forms[0].submit();
            $('#inc_messge').removeClass('error');
        }

    });

    $('.slide-history-menu').click(function () {
        $('.history-slide').slideToggle();
        if ($('#toggle-his-icon').hasClass('fa fa-angle-down')) {
            $('#toggle-his-icon').removeClass('fa fa-angle-down').addClass('fa fa-angle-up');
        } else {
            $('#toggle-his-icon').removeClass('fa fa-angle-up').addClass('fa fa-angle-down');
        }
    });

    $('#ct-sel-jurisd').change(function () {
        console.log("came jursidiction" + $('#ct-sel-jurisd').val());
        $.ajax({
            url: "/pgr/ajax-getChildLocation",
            type: "GET",
            data: {
                id: $('#ct-sel-jurisd').val()
            },
            dataType: "json",
            success: function (response) {
                $('#location').find('option:gt(0)').remove();
                $.each(response, function (index, value) {

                    $('#location').append($('<option>').text(value.name).attr('value', value.id));
                });

            },
            error: function (response) {
                console.log("failed");
            }
        });
    });

    $('#approvalDepartment').change(function () {
        resetCurrentOwner();
        if ($('#approvalDepartment').val() != '') {
            $.ajax({
                url: "/pgr/ajax-approvalDesignations",
                type: "GET",
                data: {
                    approvalDepartment: $('#approvalDepartment').val()
                },
                dataType: "json",
                success: function (response) {
                    $('#approvalDesignation').find('option:gt(0)').remove();
                    $('#approvalPosition').find('option:gt(0)').remove();
                    $.each(response, function (index, value) {
                        $('#approvalDesignation').append($('<option>').text(value.name).attr('value', value.id));
                    });

                },
                error: function (response) {
                    console.log("failed");
                }
            });
        }
    });

    $('#approvalDesignation').change(function () {
        resetCurrentOwner();
        if ($('#approvalDesignation').val() != '') {
            $.ajax({
                url: "/pgr/ajax-approvalPositions",
                type: "GET",
                data: {
                    approvalDesignation: $('#approvalDesignation').val(),
                    approvalDepartment: $('#approvalDepartment').val()
                },
                dataType: "json",
                success: function (response) {
                    $('#approvalPosition').find('option:gt(0)').remove();
                    if (response == '') {
                        bootbox.alert("No employee found, either assignment or grievance role is missing.")
                    } else {
                        $.each(response, function (index, employee) {
                            $('#approvalPosition').append($('<option>').text(employee.name)
                                .attr('value', employee.positionId)
                                .attr('data-employee-id', employee.empId));
                        });
                    }
                },
                error: function (response) {
                    console.log("failed");
                }
            });
        }
    });

    $('#approvalPosition').change(function () {
        var employeeId = $("#approvalPosition option:selected").data().employeeId;
        if (employeeId)
            $("#currentOwner").val(employeeId);
        else
            resetCurrentOwner();
    });

    var resetCurrentOwner = function () {
        $("#currentOwner").val(currentOwner);
    }
});
