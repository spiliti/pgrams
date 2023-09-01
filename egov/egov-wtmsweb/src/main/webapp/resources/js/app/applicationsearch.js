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
var tableContainer;
jQuery(document).ready(function ($) {
	
		$.fn.dataTable.moment( 'DD/MM/YYYY' );
	
	    $(":input").inputmask();
		
	    $(".datepicker").datepicker({
	        format: "dd/mm/yyyy"
		});
	    tableContainer=$('#aplicationSearchResults');
	    /*document.onkeydown=function(evt){
			 var keyCode = evt ? (evt.which ? evt.which : evt.keyCode) : event.keyCode;
		if(keyCode == 13){
			$('#searchResultDiv').show();
			submitForm();	
		}
		 }*/
	    $('#searchapplication').click(function () {
	    	if($('#applicationSearchRequestForm').valid())
	    		submitForm();
	    	else
	    		return false;
	    });
		
	  
		function validRange(start, end) {
	        var startDate = Date.parse(start);
	        var endDate = Date.parse(end);
			
	        // Check the date range, 86400000 is the number of milliseconds in one day
	        var difference = (endDate - startDate) / (86400000 * 7);
	        if (difference < 0) {
	        	bootbox.alert("The start date must come before the end date.");
				$('#end_date').val('');
				return false;
				} else {
				return true;
			}
			
	        return true;
			
			
		}
		
		$(".btn-primary").click(function(event){
			$('#searchResultDiv').show();
			if($('#start_date').val() != '' && $('#end_date').val() != ''){
					var start = $('#start_date').val();
					var end = $('#end_date').val();
					var stsplit = start.split("/");
						var ensplit = end.split("/");
						
						start = stsplit[1] + "/" + stsplit[0] + "/" + stsplit[2];
						end = ensplit[1] + "/" + ensplit[0] + "/" + ensplit[2];
						
						if(!validRange(start,end))
						{
						return false;
						}
					
					else {
						document.forms[0].submit;
						return true;
					}
				}else{
			 }
				event.preventDefault();
			
		});
		
	$('#searchapp').keyup(function(){
		tableContainer.fnFilter(this.value);
	});
	
	$('#aplicationSearchResults').on('click','tbody tr',function(e) {
	    var elementType = $(e.target).prop('nodeName');

		if(elementType != 'BUTTON'){
		tableContainer.$('tr.row_selected').removeClass('row_selected');
        $(this).addClass('row_selected');
        var url= tableContainer.fnGetData( this,1);
        $('#applicationSearchRequestForm').attr('method', 'get');
		$('#applicationSearchRequestForm').attr('action', url);
		$('#applicationSearchRequestForm').attr('mode', 'search');
		window.open(url,'window','scrollbars=yes,resizable=yes,height=700,width=800,status=yes');
		}
		 
	});
	
	
	$('#moduleName').change(function(){
		$.ajax({
			url: "/wtms/elastic/appSearch/ajax-moduleTypepopulate",     
			type: "GET",
			data: {
				appModuleName : $('#moduleName').val()   
			},
			dataType: "json",
			success: function (response) {
				console.log("success"+response);
				$('#applicationType').empty();
				$('#applicationType').append($("<option value=''>Select from below</option>"));
				$.each(response, function(index, value) {
					$('#applicationType').append($('<option>').text(value));
				});
				
			}, 
			error: function (response) {
				console.log("failed");
			}
		});
	});
	
	$(".btn-danger").click(function(event){
		$('#searchResultDiv').hide();
	});

	$("#aplicationSearchResults").on('click','tbody tr td  .view',function(event) {
		var moduleName = reportdatatable.fnGetData($(this).parent().parent(),11);
		var applicantName = reportdatatable.fnGetData($(this).parent().parent(),4);
		var consumerCode = reportdatatable.fnGetData($(this).parent().parent(),10);
		var applicantAddress = reportdatatable.fnGetData($(this).parent().parent(),6);
		var url = reportdatatable.fnGetData($(this).parent().parent(),1);
		if(consumerCode=='' || consumerCode === null){
			bootbox.alert("you can't link the application which is under process")
			
		}
		else{
		bootbox.confirm({
							    message: "Do you want to link\n" +
							    		"consumer Number " + consumerCode  + 
							    		" with applicant Name "+ applicantName +
							    		" and applicant address "+ applicantAddress +
							    		"\nto your account",
							    buttons: {
							        confirm: {
							            label: 'Yes',
							            className: 'btn-primary'
							        },
							        cancel: {
							            label: 'No',
							            className: 'btn-danger'
							        }
							    },
							    callback: function(result) {
									if (result) {
										openPopupPage("/portal/citizen/linkconnection/",consumerCode,moduleName,url,applicantName);
									} else {
										event.stopPropagation();
										event.preventDefault();
										
									}
								}
							});
		
		}
		
	});
	
});
function openPopupPage(relativeUrl,consumerCode,moduleName,url,applicantName)
{
 var param = { 'consumerCode' : consumerCode, 'moduleName': moduleName ,'url': url,'applicantName':applicantName };
 OpenWindowWithPost(relativeUrl, "width=1000, height=600, left=100, top=100, resizable=yes, scrollbars=yes", param);
}
 

function OpenWindowWithPost(url, windowoption,params)
{
	 var form = document.createElement("form");
	 form.setAttribute("action", url);
	// form.setAttribute("target", name);
	 form.setAttribute("method", "post");
	 for (var i in params)
	 {
	   if (params.hasOwnProperty(i))
	   {
	     var input = document.createElement('input');
	     input.type = 'hidden';
	     input.name = i;
	     input.value = params[i];
	     form.appendChild(input);
	   }
	 }
	 document.body.appendChild(form);
	 form.submit();
	}

$(document).on("keypress", 'form', function (e) {
    var code = e.keyCode || e.which;
    if (code == 13) {
        e.preventDefault();
        return false;
    }
});

var reportdatatable;
function submitForm(){
	$('.loader-class').modal('show', {backdrop: 'static'});
	$.post("/wtms/elastic/appSearch/", $('#applicationSearchRequestForm').serialize())
	.done(function (searchResult) {
		//console.log(JSON.stringify(searchResult));
		
		reportdatatable = tableContainer.dataTable({
			destroy:true,
			"sDom": "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-md-6 col-xs-12'i><'col-md-3 col-xs-6'l><'col-md-3 col-xs-6 text-right'p>>",
			"aLengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
			"autoWidth": false,
			searchable:true,
			data: searchResult,
			columns: [
			{title: 'Application Type', data: 'applicationType'},
			{title: 'url' ,data: 'url',"bVisible": false},
			{title: 'Application Number', data: 'applicationNumber'},
			{title: 'Application Date', data :"applicationCreatedDate"},
			{title: 'Applicant Name', data: 'applicantName'},
			{title: 'Channel', data: 'source'},
			{title: 'Applicant Address', data: 'applicationAddress'},
			{title: 'Status', data: 'applicationStatus'},
			{title: 'Current Owner', data: 'ownername'},
			{title:'action', "data" : null, "sClass" : "text-center", "target":-1,"bVisible": false,
			    sortable: false,
			    "render": function ( data, type, full, meta ) {
			       	return '<button type="button" id ="viewbutton" class="btn btn-xs btn-secondary view"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;LINK</button>';
			    }
			},
			{ title: 'Counsumer code', data: 'consumerCode', "bVisible": false
				},
				{
					title: 'Module code', data: 'moduleName', "bVisible": false
				}
			],
			"aaSorting": [[1, 'desc']]
		});
	    if($('#citizenRole').val()=='true'){
	    	console.log($('#citizenRole').val());
	    	reportdatatable.fnSetColumnVis(9, true);
	    }
	});
	$('.loader-class').modal('hide');
}
