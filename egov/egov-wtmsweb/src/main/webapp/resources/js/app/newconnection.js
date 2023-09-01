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
$(document).ready(function(){
	loadPropertyDetails();

	//sewerage validation
	$('.sewerageDetails').hide();
	$('#propertyIdentifier').val($('#ptAssessmentNo').val());

	$('#addSewerageAppln').change(function(){
			if ($('#addSewerageAppln').is(":checked")) {
				$('.sewerageDetails').show();
				if($('#propertyIdentifier').val() != '')
                    validateSewerageConnection();
			}
			else {
				$('.sewerageDetails').hide();
			}
	});

	if($("#connectionType").val()=="METERED"){
		$(".showfields").show();
	}
	$('#noOfClosetsResidential').blur(function(){
		
		var propertyType = $('#sewpropertyType').val()
		var noOfClosetsResidential = $('#noOfClosetsResidential').val()
		if(noOfClosetsResidential==="0" ){
			bootbox.alert("Please enter number of closets value more than 0");
		}
		else if(noOfClosetsResidential != ''){
			validateClosets(propertyType , noOfClosetsResidential,true);
		} else{
			bootbox.alert("Please Enter Closets For Property Type Residential");
		}
	});
	
	$('#noOfClosetsNonResidential').blur(function(){
		var propertyType = $('#sewpropertyType').val()
		var noOfClosetsNonResidential = $('#noOfClosetsNonResidential').val()
		if(noOfClosetsNonResidential==="0" ){
			bootbox.alert("Please enter number of closets value more than 0");
		}
		else if(noOfClosetsNonResidential != ''){
			validateClosets(propertyType , noOfClosetsNonResidential,false);
		} else{
			bootbox.alert("Please Enter Closets For Property Type Non Residential");
		}
	});

	function validateClosets(propertyType,noOfClosets,flag)
	{
		console.log("propertytax :"+propertyType+"noOfClosets:"+noOfClosets);
			$.ajax({
				url: "/stms/ajaxconnection/check-closets-exists",      
				type: "GET",
				data: {
					propertyType : propertyType , 
					noOfClosets : noOfClosets,
					flag : flag
				},
				dataType: "json",
				success: function (response) { 
					if(response != '') {
						bootbox.alert(response);
						resetClosetDetails();
					} 
				}, 
				error: function (response) {
					
					bootbox.alert("Oops Something wrong!!");
				}
			});
	}
	
	function resetClosetDetails() {
		$('#noOfClosetsResidential').val('');
		$('#noOfClosetsNonResidential').val('');
	}
	
	
	if($('#noJAORSAMessage') && $('#noJAORSAMessage').val()) {
		bootbox.alert($('#noJAORSAMessage').val());
		$(".show-row").hide();
		$('#approverDetailHeading').hide();
		$('#approvalDepartment').removeAttr('required');
		$('#approvalDesignation').removeAttr('required');
		$('#approvalPosition').removeAttr('required');
	}

	var mode =$('#mode').val();
	var validateIfPTDueExists=$('#validateIfPTDueExists').val();
	var currentloggedInUser=$('#currentUser').val();
	var citizenPortal=$('#citizenPortalUser').val();
	var isAnonymousUser = $('#isAnonymousUser').val();
	if($('#isCSCOperator').val()=='true' || (currentloggedInUser=='true' && mode=='' && validateIfPTDueExists=='') ||(currentloggedInUser=='true' && validateIfPTDueExists=='false')
	|| (citizenPortal=='true' && mode=='' && validateIfPTDueExists=='') ||(citizenPortal=='true' && validateIfPTDueExists=='false')
	|| (isAnonymousUser=='true' && mode=='' && validateIfPTDueExists=='') ||(isAnonymousUser=='true' && validateIfPTDueExists=='false'))
        	{
		$(".show-row").hide(); 
		$('#approvalDepartment').removeAttr('required');
		$('#approvalDesignation').removeAttr('required');
		$('#approvalPosition').removeAttr('required');
		}

	var documentName = $('#documentName').val();
	var other ="Other"
	
	$('#cardHolderDiv').hide();
	$('#bplCardHolderName').removeAttr('required');
	changecategory();
	
	$('#connectionCategorie').change(function(){
		changecategory();
	});
	
	$('#propertyIdentifier').blur(function(){
		validatePrimaryConnection();
        if ($('#addSewerageAppln').is(":checked")) {
            validateSewerageConnection();
        }
	});
	
	function changecategory(){
		if ($('#connectionCategorie :selected').text().localeCompare("BPL") == 0 ) {  
			$("#cardHolderDiv").show();
	    	$("#bplCardHolderName").attr('required', 'required');
	    	$("#bplCardHolderName").val();
	    	return makeDocumentDetailsMandatory();
		}
		else if($('#connectionCategorie :selected').text().localeCompare("BPL") != -1)  {
			$("#cardHolderDiv").hide();
	    	$("#bplCardHolderName").removeAttr('required');
	    	$("#bplCardHolderName").val('');
	    	return makeDocumentDetailsNonMandatory();
		}
	}
	
	function makeDocumentDetailsMandatory(){
		$("#documentListTable").find('tbody').find('tr').each(function() { 
			if($(this).text().trim().startsWith("White Ration Card")) {
				$(this).find('[type=checkbox]').prop('checked',true);
				$(this).find('[type=checkbox]').prop('disabled',true);
				$(this).find('[type=text]').attr('required','required');
				$(this).find('[type=file]').attr('required','required');
			}
		});
	}
	
	function makeDocumentDetailsNonMandatory(){
		$("#documentListTable").find('tbody').find('tr').each(function() { 
			if($(this).text().trim().startsWith("White Ration Card")) {
				$(this).find('[type=checkbox]').prop('checked',false);
				$(this).find('[type=text]').removeAttr('required');
				$(this).find('[type=file]').removeAttr('required');
				$(this).find('[type=text]').attr('value','');
				$(this).find('[type=file]').attr('value','');
				$(this).find('[class=error]').hide();
			}
		});
	}
	
	function validatePrimaryConnection() {
		propertyID=$('#propertyIdentifier').val();
		if(propertyID != '') {
			$.ajax({
				url: "/wtms/ajaxconnection/check-primaryconnection-exists",
				type: "GET",
                data: {
					propertyID : propertyID  
				},
				dataType: "json",
				success: function (response) { 
					console.log("success"+response);
					if(response != '') {
						
						if($('#legacy'))
						{
							var radioValue = $("input[name='applicationType']:checked").val();
							 $('#frm input[type="radio"]').each(function(){
							      $(this).checked = false;  
							  });
							 $('input[type="radio"]').val=radioValue;
				           if(radioValue==2)
							 loadPropertyDetails();
							else{
								resetPropertyDetails();
								bootbox.alert(response);
							}

						}
						else
						{	
							resetPropertyDetails();
							bootbox.alert(response);
						}
					}
					else {
						loadPropertyDetails();
					}
				}, 
				error: function (response) {
					console.log("failed");
					resetPropertyDetails();
					bootbox.alert("Primary connection validation failed");
				}
			});
		}		
	}
	
	changeLabel();
	function changeLabel() {
		if ($('#usageType :selected').text().localeCompare("Lodges") == 0) {
			$('#persons').hide();
			$('#rooms').show();
			$('#personsdiv').hide();
			$('#roomsdiv').show();
			$('#numberOfPerson').val('');
		}
		else {
			$('#persons').show();
			$('#rooms').hide();
			$('#personsdiv').show();
			$('#roomsdiv').hide();
			$('#numberOfRooms').val('');
		}
	}
	$('#usageType').change(function () {
		changeLabel();
	});
	
	
	try{
	deptapp=$('#approvalDepartment').val() ;
	if(deptapp.length != 0){
			$.ajax({
				url: "/eis/ajaxWorkFlow-getDesignationsByObjectType",     
				type: "GET",
				data: {
					approvalDepartment : $('#approvalDepartment').val(),
					departmentRule : $('#approvalDepartment').find("option:selected").text(),
					type : $('#stateType').val(),
					currentState : $('#currentState').val(),
					amountRule : $('#amountRule').val(),
					additionalRule : $('#additionalRule').val(),
					pendingAction : $('#pendingActions').val()
				},
				dataType: "json",
				success: function (response) {
					console.log("success"+response);
					$('#approvalDesignation').empty();
					
					$.each(response, function(index, value) {
						$('#approvalDesignation').append($('<option>').text(value.name).attr('value', value.id));
					});
					
				}, 
				error: function (response) {
					bootbox.alert('json fail');
					console.log("failed");
				}
			});
			if($('#approvalPosOnValidate').val().length != 0){
			$.ajax({
				url: "/wtms/ajaxconnection/assignmentByPositionId",     
				type: "GET",
				data: {
					approvalPositionId : $('#approvalPosOnValidate').val(),
					
				},
				dataType: "json",
				success: function (response) {
					console.log("success"+response);
					$('#approvalPosition').empty();
					$.each(response, function(index, value) {
						//$('#approvalPosition').append($('<option>').text(value.name).attr('value', value.id));
						$('#approvalPosition').append($('<option>').text(value.userName+'/'+value.positionName).attr('value', value.positionId));  
					});
					
				}, 
				error: function (response) {
					console.log("failed");
				}
			});
			}
	}
	}
	catch(e)
	{
		console.log("ERROR :"+e);
	}
});
$('#consumerCodeData').blur(function(){
	console.log('Got blur event');
		$.ajax({
				url: "/wtms/ajax-consumerCodeExistFordataEntry",     
					type: "GET",
					cache: true,
					data: {
						consumerCode : $('#consumerCodeData').val() 
						
					},
					dataType: "json",
			}).done(function(value) {
				 if(value == true) {
					 bootbox.alert('Entered ConsumerCode Allready Exist');
					 $('#consumerCodeData').val('');
					 return false;
				 } else {
					 document.forms[0].submit;
					 return true; 
				 }
			});
		
	});


function loadPropertyDetails() {
	propertyID=$('#propertyIdentifier').val();
	allowIfPTDueExists = $('#allowIfPTDueExists').val();
	
	if(propertyID != '') {
		$.ajax({
			url: "/ptis/rest/property/"+propertyID,      
			type: "GET",
			dataType: "json",
			success: function (response) { 
				console.log("success"+response);
				
				if(response.errorDetails.errorCode != null && response.errorDetails.errorCode != '') {
					if($('#legacy'))
					{
				    resetPropertyDetails();
					}
					bootbox.alert(response.errorDetails.errorMessage);
				}
				else {	
					if(allowIfPTDueExists=='false' && response.propertyDetails.taxDue > 0) {
						resetPropertyDetails();
						errorMessage = "Property tax is due with Rs. "+response.propertyDetails.taxDue+"/- for the assessment no "+propertyID+", please pay the due amount to create new water tap connection"
						bootbox.alert(errorMessage);
					}
					else {					
						$('#propertyIdentifierError').html('');
						applicantName = '';
						for(i=0; i<response.ownerNames.length; i++) {
							if(applicantName == '')
								applicantName = response.ownerNames[i].ownerName;
							else 							
								applicantName = applicantName+ ', '+response.ownerNames[i].ownerName;
						}
						$("#applicantname").val(applicantName);
						$("#nooffloors").val(response.propertyDetails.noOfFloors);
						if(response.ownerNames[0].mobileNumber != '')
							$("#mobileNumber").val(response.ownerNames[0].mobileNumber);
						if(response.ownerNames[0].emailId != '')
							$("#email").val(response.ownerNames[0].emailId);
						$("#propertyaddress").val(response.propertyAddress);
						boundaryData = '';
						if(response.boundaryDetails.zoneName != null && response.boundaryDetails.zoneName != '')
							boundaryData = response.boundaryDetails.zoneName;
						if(response.boundaryDetails.wardName != null && response.boundaryDetails.wardName != '') {
							if(boundaryData == '')
								boundaryData = response.boundaryDetails.wardName;
							else
								boundaryData = boundaryData + " / " + response.boundaryDetails.wardName;
						}
						if(response.boundaryDetails.blockName != null && response.boundaryDetails.blockName != '') {
							if(boundaryData == '')
								boundaryData = response.boundaryDetails.blockName;
							else
								boundaryData = boundaryData + " / " +response.boundaryDetails.blockName; 
						}
						$("#aadhaar").val(response.ownerNames[0].aadhaarNumber);
						$("#locality").val(response.boundaryDetails.localityName);
						$("#zonewardblock").val(boundaryData);
						$("#propertytax").val(response.propertyDetails.currentTax);
					}
				}					
			}, 
			error: function (response) {
				console.log("failed");
			}
		});
	}		
}

function validateSewerageConnection() {
	var propertyID=$('#propertyIdentifier').val();
	if(propertyID != '') {
		$.ajax({
			url: "/stms/ajaxconnection/check-connection-exists",      
			type: "GET",
			data: {
				propertyID : propertyID  
			},
			dataType: "json",
			success: function (response) { 
				if(response != '') {
						resetPropertyDetails();
						bootbox.alert(response);
				}
				else {
					loadPropertyDetails();
				}
			}, 
			error: function (response) {
				resetPropertyDetails();
				bootbox.alert("connection validation failed");
			}
		});
	} else {
		resetPropertyDetails();
		}
}
function resetPropertyDetails() {
	$('#propertyIdentifier').val('');
	$('#applicantname').val('');
	$('#mobileNumber').val('');
	$('#email').val('');
	$('#propertyaddress').val('');
	$('#locality').val('');
	$('#zonewardblock').val('');
	$('#propertytax').val('0.00');
}