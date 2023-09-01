<%-- <%--
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
   
<html>
	<head>
		<title><s:text name="legacy.demand"/></title>
	</head>
<body>
	<div class="formmainbox">
		<div class="headingbg">
			<s:text name="legacy.demand"/>	
		</div>
		<s:form theme="simple">
			<br />
			<table width="300px" border="0" align="center" cellspacing="0"
				cellpadding="0">
				<tr>
					<td class="bluebox" width="180px" colspan="2">
						<div align="center">
							<s:text name="prop.Id" /> : 
							<span class="bold">
								<s:property value="%{propertyId}" />
							</span>
						</div>
					</td>
				</tr>
			</table>
			<s:if test="%{demandList.isEmpty()}">
				<span class="bold"  style="font-size: 14px"> <br><s:text name="no.legacy.demand"/></span>
			</s:if>
			<s:else>
				<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="tablebottom">
					
					<tr>

						<th class="bluebgheadtd" align="center" colspan="1" rowspan="2">
							<s:text name="demand.date" />
						</th>
						<th class="bluebgheadtd" align="center" colspan="1" rowspan="2">
							<s:text name="current.tax" />
						</th>
						<th class="bluebgheadtd" align="center" colspan="1" rowspan="2">
							<s:text name="arrear.tax" />
						</th>
						<th class="bluebgheadtd" align="center" colspan="1" rowspan="2">
							<s:text name="arrear.penalty" />
						</th>
						<th class="bluebgheadtd" align="center" colspan="1" rowspan="2">
							<s:text name="total.tax" />
						</th>
						</tr><br/><tr>
						</tr>			
					<s:iterator value="demandList" var="viewLegacyDemand">
						<tr>
						
							<td class="blueborderfortd" colspan="1">
								<div align="center">
									<s:property value="year" />
								</div>
							</td>
							<td class="blueborderfortd" colspan="1">
								<div align="center">
									<s:property value="currentTax" />
								</div>
							</td>
							<td class="blueborderfortd" colspan="1">
								<div align="center">
									<s:property value="arrearTax"/>
								</div>
							</td>
							<td class="blueborderfortd" colspan="1">
								<div align="center">
									<s:property value="arrearPenalty"/>
								</div>
							</td>
							<td class="blueborderfortd" colspan="1">
								<div align="center">
									<s:property value="totalTax" />
								</div>
							</td>
							</tr>
					</s:iterator>
				</table>
			</s:else>
			
			<div class="buttonbottom" align="center">
				<input type="button" name="button2" id="button2" value="Close"
					class="button" onclick="return confirmClose();" />
			</div>
		</s:form>
	</div>
</body>
</html>
