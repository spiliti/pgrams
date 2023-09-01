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

<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td colspan="2">
			<div class="headingsmallbg">
				<s:text name="docsectiontitle" />
			</div>
		</td>
	</tr>

	<tr>
		<td colspan="5">
			<table class="tablebottom successiondoctable" id="successionTable" width="100%"
				border="0" cellpadding="0" cellspacing="0">
				<tbody>
					<tr>
						<th class="bluebgheadtd"><s:text name="doctable.sno" /></th>
						<th class="bluebgheadtd"><s:text name="doctable.doctype" /></th>
						<th class="bluebgheadtd"><s:text name="upload.file" /></th>
					</tr>
					<s:iterator value="successionDocs" status="status"
						var="documentType">
						<tr>
							<td class="blueborderfortd" style="text-align: left"><span
								class="bold"><s:property value="#status.index + 1" /></span></td>
							<td class="blueborderfortd" style="text-align: left"><s:property
									value="name" /> <s:if test="mandatory">
									<span class="mandatory1">*</span>
								</s:if></td>
							<td class="blueborderfortd" style="text-align: left"><s:if
									test="%{successionDocuments.isEmpty()}">
									<s:hidden name="documentsProxy[%{#status.index}].type.id" value="%{id}"></s:hidden>
									<s:if test="mandatory">
										<s:file name="documentsProxy[%{#status.index}].uploads"
											value="%{documentsProxy[#status.index].uploads}"
											cssClass="button validateDocs" required="true" />
									</s:if>
									<s:else>
										<s:file name="documentsProxy[%{#status.index}].uploads"
											value="%{documentsProxy[#status.index].uploads}"
											cssClass="button" />
									</s:else>
								</s:if> <s:elseif
									test="%{!successionDocuments[#status.index].files.isEmpty()}">
									<s:iterator value="%{successionDocuments[#status.index].files}">
										<s:hidden name="documentsProxy[%{#status.index}].type.id"></s:hidden>
										<s:if test="%{allowEditDocument}">
										<s:file name="documentsProxy[%{#status.index}].uploads"
											value="%{documentsProxy[#status.index].uploads}"
											cssClass="button" />
									<a
											href="javascript:viewDocument('<s:property value="fileStoreId"/>')">
											<s:property value="%{fileName}" />
										</a> 
										</s:if>
										<s:else> <a
											href="javascript:viewDocument('<s:property value="fileStoreId"/>')">
											<s:property value="%{fileName}" />
										</a> </s:else>
									</s:iterator>
								</s:elseif> <s:else>
									<s:hidden name="documentsProxy[%{#status.index}].type.id"></s:hidden>
									<s:file name="documentsProxy[%{#status.index}].uploads"
											value="%{documentsProxy[#status.index].uploads}"
											cssClass="button" />
								</s:else></td>
						</tr>
						<s:set value="%{#status.index + 1}" var="OldMaxIndex" />
					</s:iterator>
				</tbody>
			</table>
		</td>
	</tr>
</table>
<script>
	function viewDocument(fileStoreId) {
		var sUrl = "/egi/downloadfile?fileStoreId=" + fileStoreId
				+ "&moduleName=PTIS";
		window.open(sUrl, "window",
				'scrollbars=yes,resizable=no,height=400,width=400,status=yes');
	}

	jQuery(".successiondoctable input:file")
			.change(
					function() {
						var fileName = jQuery(this).val();
						var fileInput = jQuery(this);
						var maxSize = 5242880; //file size  in bytes(5MB)
						var inMB = maxSize / 1024 / 1024;
						if (fileInput.get(0).files.length) {
							var fileSize = this.files[0].size; // in bytes
							if (fileSize > maxSize) {
								bootbox.alert('File size should not exceed '
										+ inMB + ' MB!');
								fileInput.replaceWith(fileInput.val('').clone(
										true));
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
</script>