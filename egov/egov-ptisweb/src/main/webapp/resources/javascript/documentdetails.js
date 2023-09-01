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

jQuery('#assessmentDocumentNames').change(function() {
	jQuery('#docNo').val("");
	jQuery('#docDate').val("");
	jQuery('#proceedingNo').val("");
	jQuery('#proceedingDate').val("");
	jQuery('#courtname').val("");
	jQuery('#signedCheck').prop('checked', false);
	documentTypeEdit();
});

function documentTypeEdit() {
	var dropdownvalue = jQuery('#assessmentDocumentNames :selected').text();
	enableOwnerDetailsFields(dropdownvalue);
	serialNoToggle(dropdownvalue);
}

function documentTypeToggle(dropdownvalue) {
	if (dropdownvalue.indexOf('Certificate') != -1) {
		jQuery(".docNoDate").show();
		jQuery(".proceeding").show();
		jQuery(".courtName").hide();
		jQuery(".signed").hide();
		jQuery('#Patta_Certificate').show();
		jQuery('#MRO_Proceedings').show();
		jQuery('#Will_Deed').hide();
		jQuery('#Decree_Document').hide();
		jQuery('#Registered_Document').hide();
		jQuery('#Photo_of_Property_With_Holder').hide();
		jQuery('#docNoLabel').html('Certificate No <span class="mandatory1">* :');
		jQuery('#docDateLabel').html('Certificate Date <span class="mandatory1">* :');
	} else if (dropdownvalue.indexOf('Decree') != -1) {
		jQuery(".docNoDate").show();
		jQuery(".proceeding").hide();
		jQuery(".courtName").show();
		jQuery(".signed").hide();
		jQuery('#Patta_Certificate').hide();
		jQuery('#MRO_Proceedings').hide();
		jQuery('#Will_Deed').hide();
		jQuery('#Decree_Document').show();
		jQuery('#Photo_of_Property_With_Holder').hide();
		jQuery('#Registered_Document').hide();
		jQuery('#docNoLabel').html('Decree No <span class="mandatory1">* :');
		jQuery('#docDateLabel').html('Decree Date <span class="mandatory1">* :');
	} else if (dropdownvalue.indexOf('Registered Will Document') != -1) {
		jQuery(".docNoDate").show();
		jQuery(".proceeding").hide();
		jQuery(".courtName").hide();
		jQuery(".signed").hide();
		jQuery('#Patta_Certificate').hide();
		jQuery('#MRO_Proceedings').hide();
		jQuery('#Will_Deed').show();
		jQuery('#Decree_Document').hide();
		jQuery('#Photo_of_Property_With_Holder').hide();
		jQuery('#Registered_Document').hide();
		jQuery('#docNoLabel').html('Deed No <span class="mandatory1">* :');
		jQuery('#docDateLabel').html('Deed Date <span class="mandatory1">* :');
	} else if (dropdownvalue.indexOf('Un-registered Will Document') != -1) {
		jQuery(".docNoDate").show();
		jQuery(".proceeding").hide();
		jQuery(".courtName").hide();
		jQuery(".signed").show();
		jQuery('#Patta_Certificate').hide();
		jQuery('#MRO_Proceedings').hide();
		jQuery('#Will_Deed').show();
		jQuery('#Decree_Document').hide();
		jQuery('#Photo_of_Property_With_Holder').hide();
		jQuery('#Registered_Document').hide();
		jQuery('#docNoLabel').html('Deed No <span class="mandatory1">* :');
		jQuery('#docDateLabel').html('Deed Date <span class="mandatory1">* :');
	} else if (dropdownvalue.indexOf('Registered Document') != -1) {
		jQuery(".docNoDate").show();
		jQuery(".proceeding").hide();
		jQuery(".courtName").hide();
		jQuery(".signed").hide();
		jQuery('#Patta_Certificate').hide();
		jQuery('#MRO_Proceedings').hide();
		jQuery('#Will_Deed').hide();
		jQuery('#Decree_Document').hide();
		jQuery('#Registered_Document').show();
		jQuery('#Photo_of_Property_With_Holder').hide();
		jQuery('#docNoLabel').html('Registered Document No <span class="mandatory1">* :');
		jQuery('#docDateLabel').html('Registered Document Date <span class="mandatory1">* :');
	} else if (dropdownvalue.indexOf('Notary document') != -1) {
		jQuery(".docNoDate").hide();
		jQuery(".proceeding").hide();
		jQuery(".courtName").hide();
		jQuery(".signed").hide();
		jQuery('#Patta_Certificate').hide();
		jQuery('#MRO_Proceedings').hide();
		jQuery('#Will_Deed').hide();
		jQuery('#Decree_Document').hide();
		jQuery('#Photo_of_Property_With_Holder').show();
		jQuery('#Registered_Document').hide();
		jQuery("#aadharNo").attr('readonly', true);
		document.getElementById("corrAddressDiff").value=false;
		jQuery("#CorrAddressDiv").find('input:text').val('');
		jQuery("#CorrAddressDiv").hide();
	}
}

var populateDefaultCitizenSuccess = function(req, res) {
	var results = res.results;
	var rowidx = 0;
	if (results != '') {
		jQuery(
				"input[name='basicProperty.propertyOwnerInfoProxy[" + rowidx
						+ "].owner.name']").val(results[0].name);
		jQuery(
				"input[name='basicProperty.propertyOwnerInfoProxy[" + rowidx
						+ "].owner.name']").attr("readonly", true);
		jQuery(
				"select[name='basicProperty.propertyOwnerInfoProxy[" + rowidx
						+ "].owner.guardianRelation']").val(results[0].guardianRelation);
		jQuery(
				"select[name='basicProperty.propertyOwnerInfoProxy[" + rowidx
						+ "].owner.guardianRelation']:not(:selected)").attr('disabled', true);
		jQuery(
				"input[name='basicProperty.propertyOwnerInfoProxy[" + rowidx
						+ "].owner.guardian']").val(results[0].guardian);
		jQuery(
				"input[name='basicProperty.propertyOwnerInfoProxy[" + rowidx
						+ "].owner.guardian']").attr("readonly", true);
		jQuery(
				"input[name='propertyDetail.floorDetailsProxy[" + rowidx
						+ "].buildingPermissionNo']").val("");
		jQuery(
				"input[name='propertyDetail.floorDetailsProxy[" + rowidx
						+ "].buildingPermissionDate']").val("");
		jQuery(
				"input[name='propertyDetail.floorDetailsProxy[" + rowidx
						+ "].buildingPlanPlinthArea.area']").val("");
		jQuery(
				"input[name='propertyDetail.floorDetailsProxy[" + rowidx
						+ "].buildingPermissionNo']").attr("readonly", true);
		jQuery(
				"input[name='propertyDetail.floorDetailsProxy[" + rowidx
						+ "].buildingPermissionDate']").attr("readonly", true);
		jQuery(
				"input[name='propertyDetail.floorDetailsProxy[" + rowidx
						+ "].buildingPlanPlinthArea.area']").attr("readonly", true);
		
	}
}

var populateDefaultCitizenFailure = function() {
	bootbox.alert('Error in fetching default citizen');
}

function populateDefaultCitizen() {
	var assessmentDocumentType = jQuery('#assessmentDocumentNames :selected')
			.text();
	if (assessmentDocumentType.indexOf('Notary document') != -1) {
		makeJSONCall([ "name", "guardian",
				"guardianRelation" ],
				'/ptis/common/ajaxcommon-defaultcitizen-fordoctype.action', {
					assessmentDocumentType : assessmentDocumentType
				}, populateDefaultCitizenSuccess, populateDefaultCitizenFailure);
	}
}

function viewDocument(fileStoreId) {
	var sUrl = "/egi/downloadfile?fileStoreId=" + fileStoreId
			+ "&moduleName=PTIS";
	window.open(sUrl, "window",
			'scrollbars=yes,resizable=no,height=400,width=400,status=yes');
}

jQuery(".doctable input:file")
		.change(
				function() {
					var fileName = jQuery(this).val();
					var fileInput = jQuery(this);
					var maxSize = 5242880; // file size in bytes(5MB)
					var inMB = maxSize / 1024 / 1024;
					if (fileInput.get(0).files.length) {
						var fileSize = this.files[0].size; // in bytes
						if (fileSize > maxSize) {
							bootbox.alert('File size should not exceed ' + inMB
									+ ' MB!');
							fileInput
									.replaceWith(fileInput.val('').clone(true));
							return false;
						}
					}
					if (fileName) {
						jQuery(this)
								.after(
										"<a href='javascript:void(0);' onclick='clearSelectedFile(this);' class='fileclear'><span class='tblactionicon delete'><i class='fa fa-times-circle'></i></span></a>");
					} else {
						if (jQuery(this).next().is("span")) {
							jQuery(this).next().remove();
						}
					}
				});

function clearSelectedFile(obj) {
	jQuery(obj).parent().find('input:file').val('');
	jQuery(obj).remove();
}

function enableOwnerDetailsFields(dropdownvalue) {
	if (dropdownvalue.indexOf('Notary document') == -1) {
		var rowidx = 0;
		jQuery(
				"input[name='basicProperty.propertyOwnerInfoProxy[" + rowidx
						+ "].owner.name']").attr("readonly", false);
		jQuery(
				"select[name='basicProperty.propertyOwnerInfoProxy[" + rowidx
						+ "].owner.gender']").attr("disabled", false);
		jQuery(
				"select[name='basicProperty.propertyOwnerInfoProxy[" + rowidx
						+ "].owner.guardianRelation']").attr("disabled", false);
		jQuery(
				"input[name='basicProperty.propertyOwnerInfoProxy[" + rowidx
						+ "].owner.guardian']").attr("readonly", false);
		jQuery(
				"input[name='propertyDetail.floorDetailsProxy[" + rowidx
						+ "].buildingPermissionNo']").attr("readonly", false);
		jQuery(
				"input[name='propertyDetail.floorDetailsProxy[" + rowidx
						+ "].buildingPermissionDate']").attr("readonly", false);
		jQuery(
				"input[name='propertyDetail.floorDetailsProxy[" + rowidx
						+ "].buildingPlanPlinthArea.area']").attr("readonly", false);
	}
	
}
