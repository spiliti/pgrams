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

function openTradeLicense(obj) {
    window.open("/tl/viewtradelicense/viewTradeLicense-view.action?id="
        + $(obj).data('eleval'), '',
        'scrollbars=yes,width=1000,height=700,status=yes');
}

function populateData(reportData) {
    $('.report-section').removeClass('display-hide');
    $('#report-footer').show();
    var dcbTable = $("#tbldcbdrilldown");
    var reportdatatable = dcbTable.dataTable({
        dom: "<'row'<'col-xs-4 pull-right'f>r>t<'row add-margin'<'col-md-3 col-xs-6'i><'col-md-2 col-xs-6'l><'col-md-3 col-xs-6 text-right'B><'col-md-4 col-xs-6 text-right'p>>",
        "autoWidth": false,
        "bDestroy": true,
        "bPaginate": false,
        "bInfo": false,
        responsive: true,
        destroy: true,
        "searching": false,
        'aaData': reportData,
        buttons: [],
        columns: [
            {
                "data": function (row, type, set, meta) {
                    return {
                        name: row.licensenumber,
                        id: row.licenseid
                    };
                },
                "render": function (data, type, row) {
                    return '<a href="javascript:void(0);" onclick="openTradeLicense(this);" data-hiddenele="id" data-eleval="'
                        + data.id + '">' + data.name + '</a>';
                },
                "sTitle": "License No."
            }, {
                "data": "arr_demand",
                "sTitle": "Arrears"
            }, {
                "data": "curr_demand",
                "sTitle": "Current"
            }, {
                "data": "username",
                "sTitle": "username",
                "bVisible": false
            }, {
                "data": "total_demand",
                "sTitle": "Total"
            }, {
                "data": "arr_coll",
                "sTitle": "Arrears"
            }, {
                "data": "curr_coll",
                "sTitle": "Current"
            }, {
                "data": "total_coll",
                "sTitle": "Total"
            }, {
                "data": "arr_balance",
                "sTitle": "Arrears"
            }, {
                "data": "curr_balance",
                "sTitle": "Current"
            }, {
                "data": "total_balance",
                "sTitle": "Total"
            }],
        "footerCallback": function (row, data, start, end, display) {
            var api = this.api(), data;
            if (data.length == 0) {
                $('#report-footer').hide();
            } else {
                $('#report-footer').show();
            }
            if (data.length > 0) {
                updateTotalFooter(1, api);
                updateTotalFooter(2, api);
                updateTotalFooter(4, api);
                updateTotalFooter(5, api);
                updateTotalFooter(6, api);
                updateTotalFooter(7, api);
                updateTotalFooter(8, api);
                updateTotalFooter(9, api);
                updateTotalFooter(10, api);
            }
        },
        "aoColumnDefs": [{
            "aTargets": [2, 3, 4, 5, 6, 7, 8, 9, 10],
            "mRender": function (data, type, full) {
                return formatNumberInr(data);
            }
        }]
    });
    $('.loader-class').modal('hide');

    if ($('#mode').val() == 'property') {
        reportdatatable.fnSetColumnVis(1, false);
    } else {
        reportdatatable.fnSetColumnVis(1, true);
    }
}

function installmentwiseReportDate(reportData) {
    $('.report-section').removeClass('display-hide');
    $('#report-footer').show();
    var dcbTable = $("#tblinstallmentdcb");
    var reportdatatable = dcbTable.dataTable({
        dom: "<'row'<'col-xs-4 pull-right'f>r>t<'row add-margin'<'col-md-3 col-xs-6'i><'col-md-2 col-xs-6'l><'col-md-3 col-xs-6 text-right'B><'col-md-4 col-xs-6 text-right'p>>",
        "autoWidth": false,
        "bDestroy": true,
        "bPaginate": false,
        "bInfo": false,
        responsive: true,
        destroy: true,
        "searching": false,
        'aaData': reportData,
        buttons: [],
        "order": [[0, "desc"]],
        columns: [{
            "data": "installment",
            "sTitle": "Demand Reason"
        }, {
            "data": "currentDemand",
            "sTitle": "Demand"
        }, {
            "data": "currentCollection",
            "sTitle": "Collection"
        }, {
            "data": "currentBalance",
            "sTitle": "Balance"
        }],
        "footerCallback": function (row, data, start, end, display) {
            var api = this.api(), data;
            if (data.length == 0) {
                $('#report-footer').hide();
            } else {
                $('#report-footer').show();
            }
            if (data.length > 0) {
                updateTotalFooter(1, api);
                updateTotalFooter(2, api);
                updateTotalFooter(3, api);
            }
        }
    });
}

function updateTotalFooter(colidx, api) {
    // Remove the formatting to get integer data for summation
    var intVal = function (i) {
        return typeof i === 'string' ? i.replace(/[\$,]/g, '') * 1
            : typeof i === 'number' ? i : 0;
    };

    // Total over this page
    var pageTotal = api.column(colidx, {page: 'current'}).data().reduce(function (a, b) {
        return intVal(a) + intVal(b);
    }, 0);

    // Update footer
    $(api.column(colidx).footer()).html(formatNumberInr(pageTotal));
}

function formatNumberInr(x) {
    if (x) {
        x = x.toString();
        var afterPoint = '';
        if (x.indexOf('.') > 0)
            afterPoint = x.substring(x.indexOf('.'), x.length);
        x = Math.floor(x);
        x = x.toString();
        var lastThree = x.substring(x.length - 3);
        var otherNumbers = x.substring(0, x.length - 3);
        if (otherNumbers != '')
            lastThree = ',' + lastThree;
        var res = otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",")
            + lastThree + afterPoint;
        return res;
    }
    return x;
}