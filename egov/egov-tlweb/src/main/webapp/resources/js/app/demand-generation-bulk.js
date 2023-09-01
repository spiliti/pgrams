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
    var tbl = $("#tbldemandgenerate").DataTable({
        dom: "<'row'<'col-xs-4 pull-right'f>r>t<'row add-margin'<'col-md-3 col-xs-6'i><'col-md-2 col-xs-6'l>" +
            "<'col-md-3 col-xs-6 text-left'B><'col-md-5 col-xs-6 text-right'p>>",
        "autoWidth": false,
        "bDestroy": true,
        responsive: true,
        destroy: true,
        buttons: [{
            extend: 'pdf',
            title: 'Demand Generation Log',
            filename: 'Demand Generation Log',
            orientation: 'portrait',
            pageSize: 'A4',
            exportOptions: {
                columns: ':visible'
            }
        }, {
            extend: 'excel',
            filename: 'Demand Generation Log',
            exportOptions: {
                columns: ':visible'
            }
        }, {
            extend: 'print',
            title: 'Demand Generation Log',
            filename: 'Demand Generation Log',
            exportOptions: {
                columns: ':visible'
            }
        }],
        columns: [
            {
                "data": function (row, type, set, meta) {
                    return {
                        name: row.licenseNumber,
                        id: row.licenseId
                    };
                },
                "render": function (data, type, row) {
                    return '<a href="javascript:void(0);" onclick="openTradeLicense(this);" data-hiddenele="id" data-eleval="'
                        + data.id + '">' + data.name + '</a>';
                },
                "sTitle": "License No."
            }, {
                "data": "detail",
                "sTitle": "Details"
            }, {
                "data": "status",
                "sTitle": "Status"
            }, {
                "data": function (row, type, set, meta) {
                    return {
                        id: row.licenseId
                    };
                },
                'sClass': "text-center",
                "bSortable": false,
                "target": -1,
                "render": function (data, type, row) {
                    return pending ? '<span class="add-padding">' +
                        '<button type="button" class="btn btn-xs btn-secondary" id="'+data.id+'" onclick="tryDemandGen(this)">Regenerate</button>' : '';
                },
                "visible" : pending
            }]
    });

    var licenseCount = licenseIds.length;
    var batchSize = 100;
    var processed = 0;

    var batchDemandGeneration = function () {
        $('.progress-bar').attr('aria-valuemax', licenseCount);
        var batch = licenseIds.splice(0, batchSize);
        if (batch.length == 0) {
            $.post('generate',
                {
                    installmentYear: $("#installmentYear").val(),
                    licenseIds: batch.join(',')
                }
            ).done(function (data) {
                    tbl.clear();
                    tbl.rows.add(data).draw();
                    logDetails = tbl.rows().data();
                    $('.progress-bar-title').text(licenseCount + ' of ' + licenseCount + ' processed');
                    $('.progress-bar').css("width", '100%').attr('aria-valuenow', 100);
                    setTimeout(function () {
                        $('.progress').hide();
                        $('#progress-footer').hide();
                        $('#progress-status').html("Demand generation is completed !<br\/> " +
                            "Check the 'Details' in the table for any failed demand generation.");
                    }, 2000);
                }
            );
            return $.Deferred().resolve().promise();
        }
        return $.post('generate',
            {
                installmentYear: $("#installmentYear").val(),
                licenseIds: batch.join(',')
            })
            .done(function (data) {
                processed += batchSize;
                $('.progress-bar-title')
                    .text(processed + ' of ' + licenseCount);
                $('.progress-bar')
                    .css("width", ((processed / licenseCount) * 100) + '%')
                    .attr('aria-valuenow', batch.length);
                tbl.rows.add(data).draw();
            })
            .fail(function (e) {
                //do nothing
            })
            .then(function () {
                return batchDemandGeneration();
            });
    };

    $("#show-pending-btn").click(function () {
        tbl.clear();
        $('.report-table-container').show();
        tbl.rows.add((typeof logDetails == 'string') ? $.parseJSON(logDetails) : logDetails).draw();
    });

    $("#genDmdBtn").click(function () {
        $('.report-table-container').show();
        tbl.clear();
        if (licenseIds.length == 0) {
            $('.report-table-container').show();
            tbl.rows.add((typeof logDetails == 'string') ? $.parseJSON(logDetails) : logDetails).draw();
        } else {
            $('.progress-div').show();
            batchDemandGeneration();
        }
    });
});

function openTradeLicense(obj) {
    window.open("/tl/viewtradelicense/viewTradeLicense-view.action?id=" + $(obj).data('eleval'), '',
        'scrollbars=yes,width=1000,height=700,status=yes');
}

function tryDemandGen(obj) {
    window.open("/tl/demand/generate/" + $(obj).attr('id')+"?forPrevYear=true", '',
        'scrollbars=yes,width=1000,height=700,status=yes');
}