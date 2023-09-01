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

jQuery(document).ready(function($) {

	$('#submit').click(function(){			
		$("#demandresultTable").dataTable({
			 "bInfo" : false,
			   "bPaginate": false,
			    "bLengthChange": false,
			    fnDrawCallback: function (settings) {
			    	if(settings.fnRecordsDisplay() > 0){
			    		$("#showhide").show();
			    	}
			    	else
			    	{
			    	      $("#showhide").hide();
			        }
			        },
			    
			ajax : {
				url : "/stms/transactions/seweragedemand-batch",      
				type: "POST",
				beforeSend : function() {					
					$('.loader-class').modal('show', {
						backdrop : 'static'
					});
				},
				"data" : getFormData(jQuery('form')),
				complete : function() {
					
					$('.loader-class').modal('hide');
				}	
			},
			
			"bDestroy" : true,
			"autoWidth": false,
			"sDom": "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-md-6 col-xs-12'i><'col-md-3 col-xs-6'l>>",			
			"columns" : [
						{  "data" : "jobname",
			             
						},
						{
							
							"data":"status",
						},
						
						
						{
							"data":"createdDate"
						}
						
							]		
				});
			
		if($("#seweragedemandstatussearchform").valid()){
		
			jQuery('.report-section').removeClass('display-hide');
		$("#resultTable").dataTable({
			ajax : {
				url : "/stms/transactions/seweragedemand-status",      
				type: "POST",
				beforeSend : function() {
					$('.loader-class').modal('show', {
						backdrop : 'static'
					});
				},
				"data" : getFormData(jQuery('form')),
				complete : function() {
					$('.loader-class').modal('hide');
				}
			},
			
			"bDestroy" : true,
			"autoWidth": false,
			"sDom": "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-md-6 col-xs-12'i><'col-md-3 col-xs-6'l><'col-md-3 col-xs-6 text-right'p>>",				"columns" : [{  "data" : "financialyear"} ,
						{  "data" : "noOfSuccess",
							"render" : function(data, type, row){
								return '<a onclick="openPopup(\'/stms/transactions/seweragedemand-status-records-view/'
								+"0"+"~"+row.financialyear+'\')" href = javascript:void(0);">'
								+row.noOfSuccess
								+'</a>';
							}
						
						} ,
						{  "data" : "noOfFailure",
							"render" : function(data, type, row){
								return '<a onclick="openPopup(\'/stms/transactions/seweragedemand-status-records-view/'
								+"1"+"~"+row.financialyear+'\')" href = javascript:void(0);">'
								+row.noOfFailure
								+'</a>';
							}
						},
						
							]		
				});
		}
			
	});
	
});

function openPopup(url){
	window.open(url,'window',"scrollbars=1, resizable=yes, height=600, width=800, status=yes");
}

function getFormData($form){
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};

    $.map(unindexed_array, function(n, i){
        indexed_array[n['name']] = n['value'];
    });

    return indexed_array;
}
