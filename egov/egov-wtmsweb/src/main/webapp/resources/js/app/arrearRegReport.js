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
var reportdatatable;
jQuery(document).ready(function($) {

	tableContainer1 = $("#ageingReport-table");
	drillDowntableContainer = $("#drilldownReport-table");
	$(".btn-primary").click(function() {
		
		var zone= $('#zone').val();
		
		var ward= $('#ward').val();
		if(zone=="" || zone==null){
			bootbox.alert("please Select Zone");
			return false;
		}
		
		if(ward==null || ward ==""){
			bootbox.alert("please Select Ward");
			return false;
		}
		
		else {
			var url = '/wtms/reports/arrear/arrearReport';
			$('#drillDownReportForm').attr('method', 'post');
			$('#drillDownReportForm').attr('action', url);
			$('#drillDownReportForm').attr('name', 'myform');
			document.forms["myform"].submit();
			return true;
		}
	
	});
	$('#drilldownReportSearch').click(function(e) {
		
			callajaxdatatableForDrilDownReport();
		
	});
});

function callajaxdatatableForDrilDownReport() {

	

	
	var ward = $("#ward").val();
	var block = $("#block").val();

	$('.report-section').removeClass('display-hide');
	$('#report-footer').show();
	//$('#report-backbutton').hide();

	//e.stopPropagation();

}
$('#ward').change(function(){
	console.log("came on change of ward"+$('#ward').val());
	jQuery.ajax({
		url: "/egi/boundary/ajaxBoundary-blockByWard.action",
		type: "GET",
		data: {
			wardId : jQuery('#ward').val()
		},
		cache: false,
		dataType: "json",
		success: function (response) {
			console.log("success"+response);
			jQuery('#block').html("");
			jQuery('#block').append("<option value=''>Select</option>");
			jQuery.each(response, function(index, value) {
				jQuery('#block').append($('<option>').text(value.blockName).attr('value', value.blockId));
			});
		}, 
		error: function (response) {
			jQuery('#block').html("");
			jQuery('#block').append("<option value=''>Select</option>");
			console.log("failed");
		}
	});
});








