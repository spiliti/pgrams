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

jQuery(document).ready(function(){
	changeIsMeterDamaged();
});

function currentReadingChange(){
	var currentMeterReading = $('#metercurrentReading').val();
	if(currentMeterReading==="") {
		
		$('#isMeterDamaged').attr('disabled', false);
	}
	else {
		$("#isMeterDamaged").val(false);
		$('#isMeterDamaged').prop('checked', false);
		$('#isMeterDamaged').attr('disabled', true);
	}
}

$('#metercurrentReadingDate').on('change', function(){
		$.ajax({
				url: "/wtms/ajax-meterReadingEntryExist",     
					type: "GET",
					data: {
						givenDate : $('#metercurrentReadingDate').val() ,
						requestConsumerCode : $('#consumerCode').val() 
					},
					dataType: "json",
					async : false,
			}).done(function(value) {
				 if(value) {
					 bootbox.alert('Meter Reading for entered Date already exists');
					 $('#metercurrentReadingDate').val('');
					 return false;
				 } else {
					 document.forms[0].submit;
					 return true; 
				 }
			});
	});

	var currentInstallmentExist = $('#currentInstallmentExist').val();
	if (currentInstallmentExist) {
		$('#submitButtonId').hide();
	}
	function getUrlToPring() {
		var consumerCode = $('#consumerCode').val();
		var url = '/wtms/application/meterdemandnotice?pathVar=' + consumerCode;
		$('#waterConnectionForm').attr('method', 'get');
		$('#waterConnectionForm').attr('action', url);
		window.location = url;
	}
	function valiateReading() {
		var isMeterDamaged = $('#isMeterDamaged').val();
		var previousReading = $('#previousreading').val();
		var currentReading = $('#metercurrentReading').val();
		var currentMeterDate = $('#metercurrentReadingDate').val();
		var previousMeterDate = $('#previousreadingDate').val();
		if (currentReading === '' && isMeterDamaged==="false") {
			bootbox.alert('Either Current Meter Reading or isMeterDamaged value is required');
			return false;
		}
		else if (currentReading!="" && (currentReading - previousReading) < 0) {
			bootbox.alert('Current Meter Reading should not be less than Previous Meter Reading');
			$('#metercurrentReading').val('');
			return false;
		}
		else if(validateTapExecutionDate())
		{
		return false;

		}
		if (currentMeterDate != undefined && previousMeterDate != undefined && !validateCurrentAndExecutionDateRange(previousMeterDate, currentMeterDate)) {
			bootbox.alert("Current Meter Reading Date should not be less than Previous Meter Reading Date "+previousMeterDate);
			$('#metercurrentReadingDate').val('');
			return false;
		}
		document.forms[0].submit;
		return true;
		
		
	}
// this date validation is to check less than 
	function validateCurrentAndExecutionDateRange(fromDate, toDate) {
		if (fromDate != "" && toDate != "") {
			var stsplit = fromDate.split("/");
			var ensplit = toDate.split("/");

			var startDate = Date.parse(stsplit[1] + "/" + stsplit[0] + "/"
					+ stsplit[2]);
			var endDate = Date.parse(ensplit[1] + "/" + ensplit[0] + "/"
					+ ensplit[2]);

			// Check the date range, 86400000 is the number of milliseconds in one day
			var difference = (endDate - startDate) / (86400000 * 7);
			if (difference < 0) {
				return false;
			}
			return true;
			
		}
		return true;
	}
	

	function validateTapExecutionDate() {
		var metercurrentdate = $('#metercurrentReadingDate').val();	 
		var executionDate = $('#executionDate').val();
		if(metercurrentdate !='' && executionDate != '' && !validateCurrentAndExecutionDateRange(executionDate,metercurrentdate)) {
			bootbox.alert("The Current Meter Reading Date can not be less than the Date of Execution.");
			$('#metercurrentReadingDate').val('');
			return false;			
		}
	}
	
	function changeIsMeterDamaged(){
		if($("#isMeterDamaged").is(":checked")){
			$("#isMeterDamaged").val(true);
		}
		else
			$("#isMeterDamaged").val(false);
	}

