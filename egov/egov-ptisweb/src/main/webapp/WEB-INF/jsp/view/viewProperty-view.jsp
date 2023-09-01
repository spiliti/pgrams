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

<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<html>
<head>
<title><s:text name="viewPropDet.title" /></title>
<script type="text/javascript">
	function loadOnStartup() {
		var propType = '<s:property value="%{basicProperty.property.propertyDetail.propertyTypeMaster.type}"/>';
		var appurtenantLandChecked = '<s:property value="%{basicProperty.property.propertyDetail.appurtenantLandChecked}"/>';
		enableFieldsForPropTypeView(propType, appurtenantLandChecked);
		var btnCheckbox = document.getElementById('taxEnsureCheckbox');
		var btnPayTax = document.getElementById('PayTax');
		var buttorOperatorPayTax = document.getElementById('operatorPayBill');

		if (btnPayTax != null) {
			btnPayTax.disabled = (btnCheckbox.checked) ? false : true;
		}

		if (buttorOperatorPayTax != null) {
			buttorOperatorPayTax.disabled = (btnCheckbox.checked) ? false
					: true;
		}
		
		maskAadharAndMobileNumber();
	}
	
	function maskAadharAndMobileNumber(){
		jQuery("#nameTable tr").find('td').each(function() {
			if (jQuery(this).attr('id') == 'aadharNumView'){
				var aadharNo = jQuery(this).find('span.bold').text();
				aadharNo = jQuery.trim(aadharNo.replace(/[\t\n]+/g, ' '));
				if(aadharNo != '' && aadharNo != 'N/A'){
				    jQuery(this).find('span.bold').html(aadharNo.replace(aadharNo.substr(0, 8),"********"));
				}
			}
			if (jQuery(this).attr('id') == 'mobileNumView'){
				var mobileNo = jQuery(this).find('span.bold').text();
				mobileNo = jQuery.trim(mobileNo.replace(/[\t\n]+/g, ' '));
				if(mobileNo != ''){
					jQuery(this).find('span.bold').html(mobileNo.replace(mobileNo.substr(0, 8),"********"));
				}
			}
		});
	}

	function switchPayTaxButton(ensureCheckbox) {
		var buttonPayTax = document.getElementById('PayTax');

		if (buttonPayTax == null) {
			document.getElementById('operatorPayBill').disabled = (ensureCheckbox.checked) ? false
					: true;
		} else {
			buttonPayTax.disabled = (ensureCheckbox.checked) ? false : true;
		}

	}

	jQuery(document)
			.ready(
					function() {
						jQuery('#payBill')
								.click(
										function() {
											var propertyId = '<s:property value="%{basicProperty.upicNo}"/>';
											window.location = '/../ptis/collection/collectPropertyTax-generateBill.action?propertyId='
													+ propertyId;
										});
					});
</script>
</head>
<body onload="loadOnStartup(); ">
	<s:form action="searchProperty" method="post" name="indexform"
		theme="simple">
		<s:push value="model">
			<div class="">
				<s:if test="%{errorMessage != null && errorMessage != ''}">
					<s:property value="%{errorMessage}" />
					<div class="buttonbottom" align="center">
						<input type="button" class="buttonsubmit" name="SearchProperty"
							id="SearchProperty" value="Search Property"
							onclick="window.location='../search/searchProperty-searchForm.action';" />
						<input type="button" name="btnPrint" id="btnPrint" value="Print"
							class="buttonsubmit" onclick="window.print();" /> <input
							type="button" name="button2" id="button2" value="Close"
							class="button" onclick="window.close();" />
					</div>
				</s:if>
				<s:else>
					<jsp:include page="viewProperty.jsp" />
					<div class="buttonbottom no-print" align="center">
						<!--From application index search same view page is given, if new property is under work flow and assessment no is not generated then all links are disabled  -->
						<s:if test="%{basicProperty.upicNo!=null &&  !citizenPortalUser}">
							<s:if
								test="%{!property.getIsExemptedFromTax() && (isCitizen || roleName.contains(@org.egov.ptis.constants.PropertyTaxConstants@CSC_OPERATOR_ROLE.toUpperCase()) ||
					roleName.contains(@org.egov.ptis.constants.PropertyTaxConstants@ROLE_COLLECTION_OPERATOR.toUpperCase()))}">
								<div align="center">
									<s:checkbox name="taxEnsureCheckbox" id="taxEnsureCheckbox"
										onclick="switchPayTaxButton(this);" required="true" />
									<span style="font-size: 15px; color: red"> <s:text
											name="msg.payBill.verification" /> <br>
									<br> <s:text name="msg.activeDemand" />
									</span>
								</div>
								<br>
								<div align="center">
									<s:if test="%{isCitizen}">
										<input type="button" name="PayTax" id="PayTax" value="Pay Tax"
											class="buttonsubmit"
											onclick="window.location='../citizen/collection/collection-generateBill.action?assessmentNumber=<s:property value="%{propertyId}" />';" />

									</s:if>
									<s:else>
										<input type="button" name="operatorPayBill"
											id="operatorPayBill" value="Pay Bill" class="buttonsubmit"
											onclick="window.location='/../ptis/collection/collectPropertyTax-generateBill.action?propertyId=<s:property value="%{propertyId}" />';" />

									</s:else>
								</div>
							</s:if>
							<br />
							<!-- common buttons starts here -->

							<s:if test="%{!property.getIsExemptedFromTax()}">
								<input type="button" class="buttonsubmit" name="btnViewDCB"
									id="btnViewDCB" value="View DCB"
									onclick="window.location='../view/viewDCBProperty-displayPropInfo.action?propertyId=<s:property value="%{basicProperty.upicNo}" />';" />
							</s:if>

							<s:if test="%{!isCitizen}">
								<input type="button" class="buttonsubmit" name="SearchProperty"
									id="SearchProperty" value="Search Property"
									onclick="window.location='../search/searchProperty-searchForm.action';" />
							</s:if>
							<s:else>
								<input type="button" class="buttonsubmit" name="SearchProperty"
									id="SearchPropertyByCitizen" value="Search Property"
									onclick="window.location='../citizen/search/search-searchForm.action';" />
							</s:else>
						</s:if>
						<s:if test="%{!citizenPortalUser}">
						<input type="button" name="btnPrint" id="btnPrint" value="Print"
							class="buttonsubmit" onclick="window.print();" /> <input
							type="button" name="button2" id="button2" value="Close"
							class="button" onclick="window.close();" />
						</s:if>
						<s:hidden label="upicNo" id="upicNo" name="upicNo"
							value="%{basicProperty.upicNo}" />
						<!-- common buttons ends here -->
					</div>
			</div>
			</s:else>
		</s:push>
		<s:if test="%{citizenPortalUser}">
			<tr>
				<td><div align="center">
				<input type="button" name="button2" id="button2" value="Close"
				class="button" onclick="window.close();" />
				</div></td>
			</tr>
		</s:if>
	</s:form>
</body>
</html>
