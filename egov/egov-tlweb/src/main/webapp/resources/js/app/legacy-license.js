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

    $("span.tradelicenceerror").each(function () {

        if ($(this).html()) {
            var validate = $(this).attr('id').split(".")[0];
            $("*[name=" + validate + "]").focus();
        }
        return;

    })


    $('#startDate').change(function () {

        var dt = $("#startDate").datepicker('getDate');
        var agrementdate = new Date(dt.getFullYear(), dt.getMonth(), dt.getDate() - 1);
        $("#agreementDate").datepicker('setEndDate', agrementdate);

    })

    $('#boundary').change(function () {
        parentBoundary = '';
        adminWard = '';
    });

    if ($('#category').val() != '') {
        $('#category').trigger('change');
    }

    $('#subCategory').change(function () {
        changeUom();
    });

    if ($('#boundary').val() != '') {
        $('#boundary').trigger('change');
    }

    if ($("#propertyNo") && $("#propertyNo").val() !== "") {
        getPropertyDetails();
    }

    function changeUom() {
        $('#uom').text('');
        $.ajax({
            url: "/tl/licensesubcategory/detail-by-feetype",
            type: "GET",
            data: {
                subCategoryId: $('#subCategory').val(),
                feeTypeId: $('#feeTypeId').val()
            },
            cache: false,
            dataType: "json",
            success: function (response) {
                if (response)
                    $('#uom').text(response.uom.name);
                else {
                    $('#uom').text('');
                    bootbox.alert("No UOM mapped for the selected Sub Category");
                }
            }
        });
    }

    showHideAgreement();

    applicationdate();

    var showdetail = $("#agreementDate").val();
    if (showdetail != "")
        $("#showAgreementDtl").prop("checked", true);

});

$('form').validate({
    ignore: ".ignore",
    invalidHandler: function (e, validator) {
        if (validator.errorList.length)
            $('#settingstab a[href="#' +
                $(validator.errorList[0].element).closest(".tab-pane").attr('id') + '"]').tab('show');
        validator.errorList[0].element.focus();
    }
});


function applicationdate() {

    var today = new Date();
    var dd = today.getDate();
    var mm = today.getMonth() + 1; // January is 0!

    var yyyy = today.getFullYear();
    if (dd < 10) {
        dd = '0' + dd;
    }
    if (mm < 10) {
        mm = '0' + mm;
    }
    var currentdate = dd + '/' + mm + '/' + yyyy;
    $("#DATE").val(currentdate);

}

function checkOldLicenseNumberUnique() {
    if ($('#oldLicenseNumber').val() != '') {
        $.ajax({
            url: "../legacylicense/old-licenseno-is-unique",
            type: "GET",
            data: {
                oldLicenseNumber: $('#oldLicenseNumber').val(),
            },
            cache: false,
            dataType: "json",
            success: function (response) {
                if (response) {
                    bootbox.alert("A license already exist with the same Old License Number!");
                    $('#oldLicenseNumber').val('');
                    return false;
                }
                else
                    return true;
            }
        });
    }
}

$('#boundary').change(function () {
    $('#parentBoundary').find('option:gt(0)').remove();
    $('#adminWard').find('option:gt(0)').remove();
    if (this.value !== '') {
        $.ajax({
            type: "GET",
            url: "/egi/boundary/ajaxBoundary-blockByLocality",
            cache: true,
            dataType: "json",
            data: {'locality': this.value}
        }).done(function (response) {
            if (response.results.boundaries.length < 1) {
                bootbox.alert("Could not find ward for Locality : " +
                    $('#boundary').find(":selected").text());
                $('#boundary').val('');
                return;
            }
            $.each(response.results.boundaries, function (key, boundary) {
                $('#parentBoundary').append('<option '
                    + (boundary.wardId === $('#parentBoundary').data('selected-id') ? 'selected="selected"' : "")
                    + 'value="' + boundary.wardId + '">' + boundary.wardName + '</option>');
            });
            $('#parentBoundary').removeAttr('data-selected-id');
        });
        $.ajax({
            type: "GET",
            url: "/egi/boundary/ward-bylocality",
            cache: true,
            dataType: "json",
            data: {'locality': this.value}
        }).done(function (response) {
            $.each(response, function (key, boundary) {
                $('#adminWard').append('<option '
                    + (boundary.wardId === $('#adminWard').data('selected-id') ? 'selected="selected"' : "")
                    + 'value="' + boundary.wardId + '">' + boundary.wardName + '</option>');
            });
            $('#adminWard').removeAttr('data-selected-id');
        });
    }
});

$('#category').change(function () {
    var val = $(this).val();
    if (val !== '') {
        var results = [];
        $.ajax({
            type: "GET",
            url: '/tl/licensesubcategory/by-category',
            data: {categoryId: val},
            dataType: "json",
            success: function (data) {
                $.each(data, function (i) {
                    var obj = {};
                    obj['id'] = data[i]['id']
                    obj['text'] = data[i]['name'];
                    results.push(obj);
                });
                select2initialize($("#subCategory"), results, false);

                $('[name="tradeName"]').val($('[name="tradeName"]').data('selected-id')).trigger('change');
            },
            error: function () {
                bootbox.alert('something went wrong on server');
            }
        });
    }
});

function validateForm() {
    if ($('#legacyLicenseForm').valid()) {
        var mobileno = $('#mobilePhoneNumber').val();
        if (mobileno.length > 0 && mobileno.length < 10) {
            $('#mobileError').removeClass("hide");
            $("#mobilePhoneNumber").focus();
            return false;
        }
        else if ($("#showAgreementDtl").checked) {
            if ($("#agreementDate").val() == ''
                || $("#agreementDate").val() == null) {
                showMessage('enterLicense_error', $("#agreementDateerror").val());
                window.scroll(0, 0);
                return false;
            }
            else if ($("#agreementDocNo").val().trim() == '' || $("#agreementDocNo").val() == null) {
                showMessage('enterLicense_error',
                    $("#agreementDocNoerror").val());
                window.scroll(0, 0);
                return false;
            } else {
                /*validate fee details*/
                if (validate_feedetails()) {
                    //checkbox checked
                    if (feedetails_checked()) {
                        formsubmit();
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } else {
            /*validate fee details*/
            if (validate_feedetails()) {
                //checkbox checked
                if (feedetails_checked()) {
                    formsubmit();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
    else
        return false;

}

function validate_feedetails() {

    var validated = false;
    var globalindex;

    $("table.feedetails tbody tr").each(function (index) {
        var rowval = $(this).find("input.feeamount").val();
        if (parseFloat(rowval) > 0) {
            globalindex = index;
            validated = true;
        } else {
            if (index == (globalindex + 1)) {
                bootbox.alert($(this).find("input.feeyear").val() + ' financial year fee details amount is missing!');
                validated = false;
                return false;
            } else {
                if ($(this).is(":last-child")) {
                    bootbox.alert($(this).find("input.feeyear").val() + ' financial year fee details amount is mandatory!');
                    validated = false;
                    return false;
                }
            }
        }
    });
    return validated;
}

function feedetails_checked() {

    var checkindex;
    var validated = false;

    $('.case:checked').each(function () {
        checkindex = $(this).closest('tr').index();
    });

    if (checkindex != undefined) {
        $("table.feedetails tbody tr").each(function (index) {
            if (index > checkindex) {
                validated = true;
                return;
            } else {
                var rowval = $(this).find("input.feeamount").val();
                if (parseFloat(rowval) > 0) {
                    if ($(this).is(":last-child")) {
                        //leave it
                        validated = true;
                    } else {
                        if ($(this).find('input[type=checkbox]:checked').val() == undefined) {
                            bootbox.alert($(this).find("input.feeyear").val() + ' financial year fee details paid should be checked!');
                            validated = false;
                            return false;
                        }
                    }
                }
            }
        });
    } else {
        validated = true;
    }
    return validated;
}


function formsubmit() {
    /* submit the form */
//	toggleFields(false, "");
    $("#legacyLicenseForm").submit();
}

