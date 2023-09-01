<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>

<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<html>
<head>
<title><s:text name='transferProperty' /></title>
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/font-icons/font-awesome/css/font-awesome.min.css' context='/egi'/>">

<link
	href="<cdn:url value='/resources/global/css/bootstrap/bootstrap-datepicker.css' context='/egi'/>"
	rel="stylesheet" type="text/css" />
<script
	src="<cdn:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/egi'/>"></script>
<script type="text/javascript">
	jQuery.noConflict();
	jQuery("#loadingMask").remove();
	function loadOnStartUp() {
		enableBlock();
		try {
			jQuery(".datepicker").datepicker({
				format : "dd/mm/yyyy",
				autoclose : true
			});
		} catch (e) {
			console.warn("No Date Picker " + e);
		}

		var aadhartextboxes = jQuery('.txtaadhar');
		aadhartextboxes.each(function() {
			if (jQuery(this).val()) {
				getAadharDetailsForTransferee(this);
			}
		});
	} 
</script>
</head>
<body onload="loadOnStartUp();">
	<div class="formmainbox">
		<s:if test="%{hasErrors()}">
			<div class="errorstyle" id="property_error_area">
				<div class="errortext">
					<s:actionerror />
					<s:fielderror />
				</div>
			</div>
		</s:if>
		<s:form action="save" name="transferform" theme="simple"
			enctype="multipart/form-data">
			<s:push value="model">
				<s:token />
				<div class="headingbg">
					<s:text name="transferortitle" />
				</div>
				<s:if
					test="%{@org.egov.ptis.constants.PropertyTaxConstants@MUTATION_TYPE_REGISTERED_TRANSFER.equalsIgnoreCase(type)}">
					<span class="bold" style="margin:auto; display:table; color:maroon;"><s:property
							value="%{@org.egov.ptis.constants.PropertyTaxConstants@ALL_READY_REGISTER}" /></span>
				</s:if>
				<s:else>
					<span class="bold" style="margin:auto; display:table; color:maroon;"><s:property
							value="%{@org.egov.ptis.constants.PropertyTaxConstants@FULLTT}" /></span>
				</s:else>
				<s:hidden name="type" value="%{type}" />
				<s:hidden name="applicationSource" value="%{applicationSource}" />
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td class="bluebox2" style="width: 5%;">&nbsp;</td>
						<td class="bluebox" style="width: 20%"><s:text name="prop.Id"></s:text>
							:</td>
						<td class="bluebox"><span class="bold"><s:property
									value="basicproperty.upicNo" default="N/A" /></span> <s:hidden
								name="assessmentNo" id="assessmentNo"
								value="%{basicproperty.upicNo}" /> <s:hidden
								name="meesevaApplicationNumber" id="meesevaApplicationNumber"
								value="%{meesevaApplicationNumber}" /></td>
						<td class="bluebox">&nbsp;</td>
						<td style="width: 25%;">&nbsp;</td>
					</tr>
					<tr>
						<td class="bluebox2">&nbsp;</td>
						<td class="bluebox"><s:text name="PropertyAddress"></s:text>
							:</td>
						<td class="bluebox"><span class="bold"><s:property
									value="basicproperty.address" default="N/A" /></span></td>
						<td class="bluebox"><s:text name="Zone"></s:text> :</td>
						<td class="bluebox"><span class="bold"><s:property
									value="basicproperty.propertyID.zone.name" default="N/A" /></span></td>
					</tr>

					<tr>
						<td class="greybox2">&nbsp;</td>
						<td class="greybox"><s:text name="Ward" /> :</td>
						<td class="greybox"><span class="bold"><s:property
									value="basicproperty.propertyID.ward.name" default="N/A" /></span></td>
						<td class="greybox"><s:text name="block" /> :</td>
						<td class="greybox"><span class="bold"><s:property
									value="basicproperty.propertyID.area.name" default="N/A" /></span></td>
					</tr>

					<tr>
						<td class="greybox2">&nbsp;</td>
						<td class="greybox"><s:text name="CurrentTax" /> :</td>
						<td class="greybox"><span class="bold">Rs. <s:property
									value="currentPropertyTaxFirstHalf" /> /-
						</span></td>
					</tr>

					<tr>
						<td class="greybox2">&nbsp;</td>
						<td class="greybox"><s:text name="CurrentSecondHalfTax" /> :</td>
						<td class="greybox"><span class="bold">Rs. <s:property
									value="currentPropertyTaxSecondHalf" /> /-
						</span></td>
					</tr>
					<tr>
						<td colspan="5">
							<div class="headingsmallbg">
								<span class="bold"><s:text name="transferorDetails"></s:text></span>
							</div>
						</td>
					</tr>
					<tr>
						<td colspan="5">
							<table class="tablebottom" id="" width="100%" border="0"
								cellpadding="0" cellspacing="0">
								<tbody>
									<tr>
										<%-- <th class="bluebgheadtd"><s:text name="adharno" /></th> --%>
										<th class="bluebgheadtd"><s:text name="MobileNumber" /></th>
										<th class="bluebgheadtd"><s:text name="OwnerName" /></th>
										<th class="bluebgheadtd"><s:text name="gender" /></th>
										<th class="bluebgheadtd"><s:text name="EmailAddress" /></th>
										<th class="bluebgheadtd"><s:text name="GuardianRelation" /></th>
										<th class="bluebgheadtd"><s:text name="Guardian" /></th>
									</tr>
									<s:iterator value="basicproperty.propertyOwnerInfo"
										status="status">
										<tr>
											<%-- <td class="blueborderfortd" align="center"><s:if
													test='%{owner.aadhaarNumber == ""}'>
													<span class="bold">N/A</span>
												</s:if> <s:else>
													<span class="bold"><s:property
															value="%{owner.aadhaarNumber}" default="N/A" /> </span>
												</s:else></td> --%>
											<td class="blueborderfortd" align="center"><span
												class="bold"><s:property value="owner.mobileNumber" /></span></td>
											<td class="blueborderfortd" align="center"><span
												class="bold"><s:property value="owner.name" /></span></td>
											<td class="blueborderfortd" align="center"><span
												class="bold"><s:property value="owner.gender" /></span></td>
											<td class="blueborderfortd" align="center"><s:if
													test='%{owner.emailId == ""}'>
													<span class="bold">N/A</span>
												</s:if> <s:else>
													<span class="bold"><s:property
															value="%{owner.emailId}" /></span>
												</s:else></td>
											<td class="blueborderfortd" align="center"><span
												class="bold"><s:property
														value="owner.guardianRelation" default="N/A" /></span></td>
											<td class="blueborderfortd" align="center"><span
												class="bold"><s:property value="owner.guardian"
														default="N/A" /></span></td>
										</tr>
									</s:iterator>
								</tbody>
							</table>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<%@ include file="transfereeDetailsForm.jsp"%>
					<s:if
						test="%{@org.egov.ptis.constants.PropertyTaxConstants@MUTATION_TYPE_REGISTERED_TRANSFER.equalsIgnoreCase(type)}">
						<tr>
							<%@ include file="transferProperty-registrationDetails-edit.jsp"%>
						</tr>
					</s:if>
					<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<s:if
						test="%{@org.egov.ptis.constants.PropertyTaxConstants@MUTATION_TYPE_REGISTERED_TRANSFER.equalsIgnoreCase(type)}">
						<tr>
							<td class="bluebox2">&nbsp;</td>
							<td class="bluebox"><s:text name="transferreason"></s:text>
								<span class="mandatory1">*</span> :</td>
							<td class="bluebox"><s:select name="mutationReason"
									id="transRsnId" list="dropdownData.MutationReason" listKey="id"
									listValue="mutationName" headerKey="-1"
									headerValue="%{getText('default.select')}"
									value="%{mutationReason.id}" onchange="enableBlock();" /></td>
								<td class="bluebox decreeDetailsRow"><s:text name="decreeNum" /><span
								class="mandatory1">*</span> :</td>
							<td class="bluebox decreeDetailsRow"><s:textfield name="decreeNumber" id="decreeNum"
									maxlength="64" value="%{decreeNumber}"
									 /></td>
								</tr>
								<tr>
								
							<td class="bluebox2">&nbsp;</td>
							
							<td class="bluebox decreeDetailsRow"><s:text name="decreeDate" /><span
								class="mandatory1">*</span> :</td>
							<td class="bluebox decreeDetailsRow"><s:date name="decreeDate" var="decreeDate"
									format="dd/MM/yyyy" /> <s:textfield name="decreeDate"
									id="decreeDate" maxlength="10" value="%{decreeDate}"
									autocomplete="off"
									onkeyup="DateFormat(this,this.value,event,false,'3')"
									onblur="validateDateFormat(this);" cssClass="datepicker" /></td>
							<td class="greybox decreeDetailsRow"><s:text name="courtName" /> <span
								class="mandatory1">*</span> :</td>
							<td class="greybox decreeDetailsRow"><s:textarea cols="30" rows="2"
									name="courtName" id="courtName"
									onchange="return validateMaxLength(this);"
									onblur="trim(this,this.value);"></s:textarea></td>
									</tr>
						</tr>
						<tr class="documentDetRow">
							<td class="bluebox2 deedDetailsRow">&nbsp;</td>
							<td class="bluebox deedDetailsRow"><s:text name="docNum" /><span
								class="mandatory1">*</span> :</td>
							<td class="bluebox deedDetailsRow"><s:textfield name="deedNo" id="docNum"
									maxlength="64"
									onblur="checkZero(this);validateRegDocNumber(this,'Registration Document Number')" /></td>
							<td class="bluebox deedDetailsRow"><s:text name="docDate" /><span
								class="mandatory1">*</span> :</td>
							<td class="bluebox deedDetailsRow"><s:date name="deedDate" var="docDate"
									format="dd/MM/yyyy" /> <s:textfield name="deedDate"
									id="deedDate" maxlength="10" value="%{docDate}"
									autocomplete="off"
									onkeyup="DateFormat(this,this.value,event,false,'3')"
									onblur="validateDateFormat(this);" cssClass="datepicker" /></td>
						</tr>
					</s:if>
					<tr>
						<td class="bluebox2">&nbsp;</td>
						<td class="bluebox"><s:text name="label.parties.value" /><span
							class="mandatory1">*</span> :</td>
						<td class="bluebox"><s:textfield name="partyValue"
								value="%{partyValue}" id="partyValue" maxlength="16"
								onblur="validNumber(this);checkZero(this);calculateMutationFee();" /></td>
						<td class="bluebox"><s:text name="label.department.value" /><span
							class="mandatory1">*</span> :</td>
						<td class="bluebox"><s:textfield name="departmentValue"
								value="%{departmentValue}" id="departmentValue" maxlength="16"
								onblur="validNumber(this);checkZero(this);calculateMutationFee();" /></td>
					</tr>
					<tr>
						<td class="bluebox2">&nbsp;</td>
						<td class="bluebox"><s:text name="payablefee" /> :</td>
						<td class="bluebox"><s:textfield name="mutationFee"
								value="%{mutationFee}" id="mutationFee" maxlength="16" readonly="true"/></td>
						<td class="bluebox" colspan="2" />
					</tr>
					</table>
					</table>
					<div class="panel-body custom-form" id="otherReasons">
						<s:if test="%{!documentTypes.isEmpty()}">
							<jsp:include page="transfer-documentform.jsp" />
						</s:if>
					</div>
					<div class="panel-body custom-form" id="succession">
						<s:if test="%{!successionDocs.isEmpty() && !(@org.egov.ptis.constants.PropertyTaxConstants@ADDITIONAL_RULE_FULL_TRANSFER.equalsIgnoreCase(type))}">
							<jsp:include page="succession-documentform.jsp" />
						</s:if>
					</div>
					<s:if test="%{!(@org.egov.ptis.constants.PropertyTaxConstants@ADDITIONAL_RULE_FULL_TRANSFER.equalsIgnoreCase(type) 
						&& applicationSource.equalsIgnoreCase('online'))}">

						<s:if
							test="%{propertyByEmployee == true && applicationSource != 'online' && !citizenPortalUser}">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<jsp:include page="../workflow/commonWorkflowMatrix.jsp" />
								</tr>
								<tr>
									<jsp:include page="../workflow/commonWorkflowMatrix-button.jsp" />
								</tr>
							</table>
						</s:if>
						<s:else>
							<tr align="center">
								<jsp:include page="../workflow/commonWorkflowMatrix-button.jsp" />
							</tr>
						</s:else>
					</s:if>
					<s:else>
						<s:hidden id="workFlowAction" name="workFlowAction" value="Forward" />
						<div class="buttonbottom" align="center">
							<s:submit type="submit" value="Proceed for Payment"
								cssClass="buttonsubmit" id="fullTransferButton"
								name="fullTransferButton" />
						<input type="button" name="button2" id="button2" value="Close"
							class="button" onclick="window.close();" />
					</div>
				</s:else>
			</s:push>
		</s:form>
		<div align="left" class="mandatory1" style="font-size: 11px">*
			Mandatory Fields</div>
	</div>
	<script type="text/javascript">
		function confirmClose() {
			var result = confirm("Do you want to close the window?");
			if (result == true) {
				window.close();
				return true;
			} else {
				return false;
			}
		}
		function enableBlock() {
			var obj = document.getElementById("transRsnId");
			if (obj != null || obj != "undefined") {
				var selectedValue = obj.options[obj.selectedIndex].text;
				if (selectedValue == '<s:property value="%{@org.egov.ptis.constants.PropertyTaxConstants@MUTATIONRS_DECREE_BY_CIVIL_COURT}" />') {
					jQuery("td.decreeDetailsRow").show();
				jQuery("td.deedDetailsRow").hide();
			}
				 else 
				 {	jQuery("td.decreeDetailsRow").hide();
				jQuery("td.deedDetailsRow").show();
				 }
			}
			if (selectedValue == '<s:property value="%{@org.egov.ptis.constants.PropertyTaxConstants@MUTATIONRS_SUCCESSION}" />') {
				jQuery('#succession').show();
				jQuery('#otherReasons').hide();
			}
			else {
				jQuery('#succession').hide();
				jQuery('#otherReasons').show();
			}
			if (selectedValue == '<s:property value="%{@org.egov.ptis.constants.PropertyTaxConstants@MUTATIONRS_UNREG_WILL}" />'
					|| selectedValue == '<s:property value="%{@org.egov.ptis.constants.PropertyTaxConstants@MUTATIONRS_REG_WILL}" />'){
				jQuery('#transferTable tbody tr:eq(5)').find('td:eq(1)')
						.append('<span class="mandatory1">*</span>');
				jQuery('#transferTable tbody tr:eq(5)').find('input').prop('required',true);
			}
			else {
				jQuery('#transferTable tbody tr:eq(5)').find('td:eq(1)').find('span').remove();
				jQuery('#transferTable tbody tr:eq(5)').find('input').prop('required',false);
			}
			if (selectedValue == '<s:property value="%{@org.egov.ptis.constants.PropertyTaxConstants@MUTATIONRS_UNREG_WILL}" />')
					jQuery('.documentDetRow').find('span').remove();
			else if(jQuery('.documentDetRow').find('span').text() == ''){
				jQuery('.documentDetRow').find('td:eq(1)').append('<span class="mandatory1">*</span>');
				jQuery('.documentDetRow').find('td:eq(3)').append('<span class="mandatory1">*</span>');
			}
		}
		
		function onSubmit() {
			if (document.getElementById("partyValue").value == '') {
				bootbox.alert("Please enter the Parties consideration value");
				return false;
			}else if (document.getElementById("departmentValue").value == '') {
				bootbox.alert("Please enter the Department guidelines value");
				return false;
			}else if(document.getElementById("mutationFee").value == ''){
				bootbox.alert("Please enter valid values in Parties consideration value and Department guidelines value for Mutation Fee calculations");
				return false;
			}
			var obj = document.getElementById("transRsnId");
				var selectedValue = obj.options[obj.selectedIndex].text;
			if(selectedValue =='<s:property value="%{@org.egov.ptis.constants.PropertyTaxConstants@MUTATIONRS_SUCCESSION}" />')
				jQuery('#otherReasons').remove();
			else 
				jQuery('#succession').remove();
			var tosubmit = true;
			jQuery('.transfereeGender').removeAttr('disabled');
			var mutationType = '<s:property value="%{model.type}"/>'; 
			if(mutationType == 'REGISTERED TRANSFER'){
				jQuery(".validateDocs").each(function() {
					if (jQuery(this).val() == '') {
						bootbox.alert("attach the document");
						tosubmit = false;
						return false;
					}
				});
			}

			if (tosubmit)
				return true;
			else
				return false;
		}
	</script>
    <script src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/egi'/>"></script>
    <script type="text/javascript"
            src="<cdn:url value='/resources/javascript/transferProperty.js?rnd=${app_release_no}'/>"></script>
</body>
</html>