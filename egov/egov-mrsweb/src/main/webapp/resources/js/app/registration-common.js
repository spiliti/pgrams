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

$(document)
		.ready(
				function() {
					
					if ($('#serialNoExists').val()) {
						bootbox.alert($('#serialNoExists').val());
					}
					if ($('#employeeAssgnNotValid').val()) {
						$('#approvalDepartment').val('');
						bootbox.alert($('#employeeAssgnNotValid').val());
					}
					// remove mandatory (*) on form load for serial no and page
					// no
					$('.validate-madatory').find("span").removeClass(
							"mandatory");

					// Added to avoid submitting parent form on Preview button
					// click
					// Used to preview certificate in case of Digital signture
					$("#Preview").toggleClass('btn-primary btn-default');
					$("#Preview").prop('type', 'button');

					$("#select-venue").change(
							function() {
								var venue = $("#select-venue option:selected")
										.text();
								if (venue == 'Residence') {
									$('#txt-placeofmrg').val('');
									$('.toggle-madatory').find("span")
											.removeClass("mandatory");
									$('.addremoverequired').removeAttr(
											"required");
									$("#txt-placeofmrg").attr("disabled",
											"disabled");
								} else {
									$('.toggle-madatory').find("span")
											.addClass("mandatory");
									$('.addremoverequired').attr("required",
											"true");
									$("#txt-placeofmrg").removeAttr("disabled",
											"disabled");
								}
							});

					$("#select-venue").trigger('change');

					// default currentdate in create screen
					if ($('#txt-dateOfMarriage').val() == "")
						$('#txt-dateOfMarriage').datepicker('setDate',
								new Date());
					// Date of marriage shld not be editable in workflow
					if ($('#registrationStatus').val() == 'CREATED'
							|| $('#registrationStatus').val() == 'APPROVED') {
						$('#txt-dateOfMarriage').attr('disabled', 'disabled');
					}
					// make form elements disabled at final level of workflow
					// (with and without digital signature)
					if (($('#registrationStatus').val() == 'APPROVED' && $(
							'#pendingActions').val() == 'Digital Signature Pending')
							|| $('#registrationStatus').val() == 'DIGITALSIGNED') {
						$(':input').attr('readonly', 'readonly');
						$('.form-control').attr("disabled", 'disabled');
						$('#form-updateregistration select').attr('disabled',
								'disabled');
						$(':checkbox[readonly="readonly"]').click(function() {
							return false;
						});
						$(".file-ellipsis.upload-file").attr('disabled',
								'disabled');
						$('#approvalComent').removeAttr('readonly');
					} else if ($('#registrationStatus').val() == 'APPROVED'
							&& $('#pendingActions').val() == 'Certificate Print Pending') {
						$(':input').attr('readonly', 'readonly');
						$('.form-control').attr("disabled", 'disabled');
						$('#form-updateregistration select').attr('disabled',
								'disabled');
						$(':checkbox[readonly="readonly"]').click(function() {
							return false;
						});
						$(".file-ellipsis.upload-file").attr('disabled',
								'disabled');
						$('.exclude_readonly_input').removeAttr('readonly');
						$('#approvalComent').removeAttr('readonly');
					}

					if ($('#registrationStatus').val() == 'APPROVED'
							&& $("#feeCollected").val() == 'false') {
						$("[id='Print Certificate']").hide();
					}

					// Removing file attachment validation from workflow and
					// modify screen
					if ($('#registrationStatus').val() != '') {
						$(".file-ellipsis.upload-file").removeAttr('required');
					}

					$('body').on('click', 'img.attach-photo', function() {

						var img = $(this);

						var inputPhoto = document.createElement('INPUT');
						inputPhoto.setAttribute('type', 'file');

						inputPhoto.onchange = function() {

							var image = $(inputPhoto).prop('files')[0];
							var fileReader = new FileReader();

							fileReader.onload = function(e) {
								var imgData = e.target.result;
								$(img).prop('src', imgData);
								$($(img).siblings('input')).val(imgData);
							}

							fileReader.readAsDataURL(image);
							var span = $(img).siblings('span');
							$(span).removeClass('error-msg');
							$(span).text(image.name);
						}

						$(inputPhoto).trigger('click');
					});
					
					 if ($("#feeCollected").val() == "false" && (($('#registrationStatus').val() != '' && $('#registrationStatus').val() == 'CREATED' && $(
						'#nextActn').val() != 'Junior/Senior Assistance approval pending' && ($(
						'#nextActn').val() != 'Revenue Clerk Approval Pending' && $(
						'#nextActn').val() != 'Clerk Approval Pending') && $(
						'#nextActn').val() != 'Chief Medical Officer of Health Approval Pending' && $(
						'#nextActn').val() != 'Municipal Health Officer Approval Pending')&& ($(
						'#registrationStatus').val() != 'REJECTED' || ($('#currentState').val() =='Clerk Approved')
				))){
						$(".show-row").hide();
						$("#Approve").hide();
						$('#approverDetailHeading').hide();
						$('#approvalDepartment').removeAttr('required');
						$('#approvalDesignation').removeAttr('required');
						$('#approvalPosition').removeAttr('required');
						
						} 
					 else if ($("#feeCollected").val() == 'true' && $(
						'#nextActn').val() != 'Clerk Approval Pending' && 
						($('#nextActn').val() != 'Chief Medical Officer of Health Approval Pending' &&
						$('#nextActn').val() != 'Municipal Health Officer Approval Pending')){
						$(".show-row").hide();
						$("#Approve").show();
						$('#approverDetailHeading').hide();
						$('#approvalDepartment').removeAttr('required');
						$('#approvalDesignation').removeAttr('required');
						$('#approvalPosition').removeAttr('required');
					 }
					 else if($('#nextActn').val() == 'Chief Medical Officer of Health Approval Pending' || $(
						'#nextActn').val() == 'Municipal Health Officer Approval Pending'){
						if ($("#feeCollected").val() == "false"){
						 $(".show-row").show();
							$("#Approve").hide();
							$('#approverDetailHeading').show();
							$('#approvalDepartment').attr('required', 'required');
							$('#approvalDesignation').attr('required', 'required');
							$('#approvalPosition').attr('required', 'required'); 
						}
						else
							{
							$(".show-row").show();
							$("#Approve").show();
							$('#approverDetailHeading').show();
							$('#approvalDepartment').attr('required', 'required');
							$('#approvalDesignation').attr('required', 'required');
							$('#approvalPosition').attr('required', 'required');
							}
					 }
					 else {
						$(".show-row").show();
						$('#approverDetailHeading').show();
						$('#approvalDepartment').attr('required', 'required');
						$('#approvalDesignation').attr('required', 'required');
						$('#approvalPosition').attr('required', 'required');
					}

					// Showing the respective tab when mandatory data is not
					// filled in
					$('div.tab-content input').bind(
							'invalid',
							function(e) {
								if (!e.target.validity.valid) {
									var elem = $(e.target).parents(
											"div[id$='-info']")[0];

									$('.nav-tabs-top li.active').removeClass(
											'active')
									$('.nav-tabs-bottom li.active')
											.removeClass('active')

									if (elem != undefined || elem != null) {
										$(
												'.nav-tabs-top a[href="#'
														+ elem.id + '"]')
												.parent().addClass('active');
										$(
												'.nav-tabs-bottom a[href="#'
														+ elem.id + '"]')
												.parent().addClass('active');
										$('div[id$="-info"].active')
												.removeClass('in active');
										$('div#' + elem.id).addClass(
												'in active');
									}

									var imgAttach = e.target.id;
								}
							});

					$('#table_search').keyup(function() {
						$('#registration_table').fnFilter(this.value);
					});

					$('.slide-history-menu').click(
							function() {
								$(this).parent().find('.history-slide')
										.slideToggle();
								if ($(this).parent().find('#toggle-his-icon')
										.hasClass('fa fa-angle-down')) {
									$(this).parent().find('#toggle-his-icon')
											.removeClass('fa fa-angle-down')
											.addClass('fa fa-angle-up');
								} else {
									$(this).parent().find('#toggle-his-icon')
											.removeClass('fa fa-angle-up')
											.addClass('fa fa-angle-down');
								}
							});

					$('.slide-document-menu').click(
							function() {
								$(this).parent().find('.documentAttach-slide')
										.slideToggle();
								if ($(this).parent().find('#toggle-his-icon')
										.hasClass('fa fa-angle-down')) {
									$(this).parent().find('#toggle-his-icon')
											.removeClass('fa fa-angle-down')
											.addClass('fa fa-angle-up');
								} else {
									$(this).parent().find('#toggle-his-icon')
											.removeClass('fa fa-angle-up')
											.addClass('fa fa-angle-down');
								}
							});

					jQuery('form')
							.validate(
									{
										ignore : ".ignore",
										invalidHandler : function(e, validator) {
											if (validator.errorList.length)
												$(
														'#settingstab a[href="#'
																+ jQuery(
																		validator.errorList[0].element)
																		.closest(
																				".tab-pane")
																		.attr(
																				'id')
																+ '"]').tab(
														'show');
										}
									});

					//To render all marriage registration images from second level
					$('.setimage').each(
							function() {
								var encodedPhoto = $(this)
										.find('.encodedPhoto').val();
								if (encodedPhoto) {
									$(this).find('.marriage-img').attr(
											{
												src : "data:image/jpg;base64,"
														+ encodedPhoto
											});
									$(this).find('.marriage-img').attr(
											'data-exist', '');
								}
							});

					// To show preview of all uploaded images of marriage registration
					$('.upload-file[data-fileto]').change(function(e) {
						if (this.files[0].name) {
							readURL(this, $(this).data('fileto'));
						}
					});

					$('#Preview').click(
							function() {
								var url = '/mrs/registration/viewCertificate/'
										+ $('#marriageRegistration').val();
								window.open(url);
							});

					if ($('#message').val()) {
						bootbox.alert($('#message').val());
						return false;
					}
});
	
	

function readURL(input, imgId) {
	if (input.files && input.files[0]) {
		var reader = new FileReader();
		reader.onload = function(e) {
			$('img[id="' + imgId + '"]').attr('src', e.target.result);
		}
		reader.readAsDataURL(input.files[0]);
	}
}

function validateChecklists() {
	// Passport is assumed to be common proof for both age and residence
	// If passport is not attached then validate for other age and residence
	// proof documents
	if ($('#registrationStatus').val() == "" && $('#source').val()!='CHPK') {
		var ageAddrProofAttached = false;
		if ($('input[id^="indvcommonhusbandPassport"]').val() == "") {
			$('input[type="file"][id^="ageproofhusband"]').toArray().map(
					function(item) {
						if (item.value != "")
							ageAddrProofAttached = true;
					});
			if (!ageAddrProofAttached) {
				bootbox.alert("Any one Age Proof for Husband is mandatory");
				return false;
			}

			ageAddrProofAttached = false;
			$('input[type="file"][id^="addressproofhusband"]').toArray().map(
					function(item) {
						if (item.value != "")
							ageAddrProofAttached = true;
					});
			if (!ageAddrProofAttached) {
				bootbox
						.alert("Any one Residence Proof for Husband is mandatory");
				return false;
			}
		}
		if ($('input[id^="indvcommonwifePassport"]').val() == "") {
			ageAddrProofAttached = false;
			$('input[type="file"][id^="ageproofwife"]').toArray().map(
					function(item) {
						if (item.value != "")
							ageAddrProofAttached = true;
					});
			if (!ageAddrProofAttached) {
				bootbox.alert("Any one Age Proof for Wife is mandatory");
				return false;
			}

			ageAddrProofAttached = false;
			$('input[type="file"][id^="addressproofwife"]').toArray().map(
					function(item) {
						if (item.value != "")
							ageAddrProofAttached = true;
					});
			if (!ageAddrProofAttached) {
				bootbox.alert("Any one Residence Proof for Wife is mandatory");
				return false;
			}
		}
		return true;
	} else {
		return true;
	}
}

function removeMandatory() {
	if ($('#registrationStatus').val() == 'Created') {
		$('#husband-photo').removeAttr('required');
		$('#wife-photo').removeAttr('required');
	}
}

function validateApplicationDate() {
	var one_day = 1000 * 60 * 60 * 24
	var start;
	var end;
	var days;
	if ($('#applicationDate').val()) {
		start = $('#txt-dateOfMarriage').datepicker('getDate');
		end = $('#applicationDate').datepicker('getDate'); // current date
		days = (end.getTime() - start.getTime()) / one_day;
	} else {
		start = $('#txt-dateOfMarriage').datepicker('getDate'); // date of
																// marriage
		end = new Date();
		days = Math.round((end.getTime() - start.getTime()) / one_day);
	}// current date
	return days;
}

