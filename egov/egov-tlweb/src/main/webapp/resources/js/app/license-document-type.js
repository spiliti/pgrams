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

$(document).ready(function () {
    var tableContainer1 = $("#document-Table");
    $("#searchbtn").click(function () {
        tableContainer1.dataTable({
            "sDom": "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-md-6 col-xs-12'i>" +
            "<'col-md-3 col-xs-6'l><'col-md-3 col-xs-6 text-right'p>>",
            "aLengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
            "bDestroy": true,
            "autoWidth": false,
            "ajax": {
                type: "POST",
                url: "search?" + $("#searchdocumentform").serialize(),
                dataSrc: function (json) {
                    if (json.data == '')
                        $('.report-section').hide();
                    else
                        $('.report-section').show();
                    return json.data;
                }
            },
            "columns": [
                {"data": "name"},
                {"data": "applicationType"},
                {"data": "mandatory"},
                {"data": "enabled"},
                {
                    "data": null, 'sClass': "text-center", "target": -1, "defaultContent":

                    '<button type="button" class="btn btn-xs btn-secondary edit add-margin">' +
                    '<span class="glyphicon glyphicon-edit"></span>&nbsp;</button>'
                }
                ,
                {"data": "id", "visible": false}
            ]
        });
    });

    $("#document-Table").on('click', 'tbody tr td .edit', function (event) {
        var id = tableContainer1.fnGetData($(this).parent().parent(), 5);
        var url = '/tl/documenttype/edit/' + id;
        window.open(url, id, 'width=900, height=700, top=300, left=260,scrollbars=yes');

    });

    $("#enabled").click(function () {
        if ($("#enabled").prop('checked') == false) {
            $('input:checkbox[name=mandatory]').attr('checked', false);
        }
    });

    $('input:checkbox[name=mandatory]').click(function () {
        if ($("#enabled").prop('checked') == false) {
            bootbox.alert("Please first make the document enabled");
            return false;
        }
        else
            $('input:checkbox[name=mandatory]').attr('checked', true);
    });
});


