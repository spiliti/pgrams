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

<%@ taglib prefix="s" uri="/WEB-INF/taglib/struts-tags.tld"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCgxY6DqJ4TxnRfKjlZR8SfLSQRtOSTxEU"></script>
<script src="<cdn:url value='/resources/js/app/property-map.js?rnd=${app_release_no}'/>"></script>
<s:if test="%{(viewMap.propertyWF != null && viewMap.propertyWF != '' && viewMap.propertyWF == 'WF')}">
<s:set var="prop" value="property"/>
</s:if>
<s:else>
	<s:set var="prop" value="basicProperty.property"/>
</s:else>

<s:if test="%{basicProperty.property.getIsExemptedFromTax()}">
	<div class="headermessage">
		This property tax is exempted with reason <span class="bold"><s:property
				default="N/A"
				value="%{basicProperty.property.taxExemptedReason.name}" /></span>
	</div>
</s:if>

<div class="panel panel-primary" data-collapsed="0"
	style="text-align: left">
	<div class="panel-heading">
		<div class="panel-title">Property Details</div>
	</div>
	 <div class="panel-body">
			<div class="row add-border">
				<div class="col-xs-3 add-margin">
					<s:text name="Old.assessmentno" />
				</div>
				<div class="col-xs-3 add-margin view-content">
					<s:if test="%{(basicProperty.oldMuncipalNum != null && basicProperty.oldMuncipalNum !='')}">
						<s:property value="%{basicProperty.oldMuncipalNum}"/>
					</s:if>
					<s:else>
				     	N/A
					</s:else>
				</div>
			</div>
		<div class="row add-border">
			<div class="col-xs-3 add-margin">
				<s:text name="prop.Id" />
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property default="N/A" value="%{basicProperty.upicNo}" />
			</div>

			<div class="col-xs-3 add-margin">
				<s:text name="prntPropAssessmentNum" />
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property default="N/A" value="%{viewMap.parentProps}" />
			</div>
		</div>
		<div class="row add-border">
			<div class="col-xs-3 add-margin">
				<s:text name="ownership.type"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property value="%{property.propertyDetail.propertyTypeMaster.type}"
					default="N/A" />
				</span>
			</div>
			<div class="col-xs-3 add-margin">
				<s:text name="exemptioncategory"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property value="%{taxExemptedReason.name}" default="N/A" />
				</span>
			</div>
		</div>
		<div class="row add-border">
			<div class="col-xs-3 add-margin">
				<s:text name="annualvalue" />
			</div>
			<div class="col-xs-3 add-margin view-content">
				Rs.
				<s:if test='%{viewMap.ARV != null}'>
				<s:text name="format.money">
					<s:param value="viewMap.ARV" />
				</s:text>
				</s:if>
				<s:else>0</s:else>
			</div>
			<div class="col-xs-3 add-margin">
				<s:text name="effectivedt" />
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:if test="%{basicProperty.propOccupationDate != null}">
					<s:date name="%{basicProperty.propOccupationDate}"
						format="dd/MM/yyyy" />
				</s:if>
				<s:else>
							N/A
				</s:else>
			</div>
		</div>
		<div class="row add-border">
			<div class="col-xs-3 add-margin">
				<s:text name="property.type"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
			   <s:property default="N/A"
                                value="%{@org.egov.ptis.constants.PropertyTaxConstants@PROPERTY_TYPE_CATEGORIES.get(#prop.propertyDetail.categoryType)}" />
			</div>
			<div class="col-xs-3 add-margin">
				<s:text name="apartcomplex.name"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property default="N/A"
					value="%{basicProperty.property.propertyDetail.apartment.name}" />
			</div>
		</div>
		<div class="row add-border">
			<div class="col-xs-3 add-margin">
				<s:text name="extent.site"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property default="N/A"
					value="#prop.propertyDetail.sitalArea.area" />
			</div>
			<div class="col-xs-3 add-margin">
				<s:text name="label.property.department"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property default="N/A"
					value="%{basicProperty.property.propertyDetail.propertyDepartment.name}" />
			</div>
		</div>
		<div class="row add-border">
			<div class="col-xs-3 add-margin">
				<s:text name="reg.docno"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:if test="%{(viewMap.documentno != null && viewMap.documentno != '')}">
                    <s:property value="%{viewMap.documentno}" />
                </s:if>
				<s:else>
					N/A
				</s:else>
			</div> 
			<div class="col-xs-3 add-margin">
				<s:text name="reg.docdate"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:if test="%{(viewMap.documentdate != null)}">
                    <s:date name="%{viewMap.documentdate}" format="dd/MM/yyyy" />
                </s:if>
				<s:else>
					N/A
				</s:else>
			</div>
		</div>
		<div class="row add-border">
			<div class="col-xs-3 add-margin"> 
				<s:text name="rsnForCreatin"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property
					value="%{propertyDetail.propertyMutationMaster.mutationName}" />
			</div>
			<div class="col-xs-3 add-margin">
				<s:text name="assessmentDate"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
			<s:date name="%{basicProperty.assessmentdate}" var="assessmentDate" format="dd/MM/yyyy" />
				<s:property default="N/A" value="%{#assessmentDate}" /> 
			</div>
		</div>
		

	</div>
</div>

<div class="panel panel-primary" data-collapsed="0"
	style="text-align: left">
	<div class="panel-heading">
		<div class="panel-title">Address Details</div>

	</div>
	<div class="panel-body">
		<div class="row add-border">
			<div class="col-xs-3 add-margin">
				<s:text name="doorNo" />
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:if test="%{(viewMap.doorNo != null && viewMap.doorNo != '')}">
					<s:property value="%{viewMap.doorNo}" />
				</s:if>
				<s:else>
				  	N/A
				</s:else>
			</div>
		</div>
		<div class="row add-border">
			<div class="col-xs-3 add-margin">
				<s:text name="PropertyAddress" />
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property default="N/A" value="%{basicProperty.address}" />
			</div>

			<div class="col-xs-3 add-margin">
				<s:text name="CorrAddr" />
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property default="N/A" value="%{viewMap.ownerAddress}" />
			</div>
		</div>
		<div class="row add-border">
			<div class="col-xs-3 add-margin">
				<s:text name="Zone" />
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property default="N/A"
					value="%{basicProperty.propertyID.zone.name}" />
			</div>

			<div class="col-xs-3 add-margin">
				<s:text name="Ward" />
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property default="N/A"
					value="%{basicProperty.propertyID.ward.name}" />
			</div>
		</div>
		<div class="row add-border">
			<div class="col-xs-3 add-margin">
				<s:text name="block" />
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property default="N/A"
					value="%{basicProperty.propertyID.area.name}" />
			</div>
			<div class="col-xs-3 add-margin">
				<s:text name="locality"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property default="N/A"
					value="%{basicProperty.propertyID.locality.name}" />
			</div>
		</div>
		<div class="row add-border">
			<div class="col-xs-3 add-margin">Election Ward</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property default="N/A"
					value="%{basicProperty.propertyID.electionBoundary.name}" />
			</div>
			<div class="col-xs-3 add-margin">EB Block</div>
			<div class="col-xs-3 add-margin view-content">N/A</div>
		</div>
		<div class="row add-border">
			<div class="col-xs-3 add-margin">
				<s:text name="latitude"></s:text> 
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property default="N/A"
					value="%{basicProperty.latitude}" />
			</div>
			<div class="col-xs-3 add-margin">
				<s:text name="longitude"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
				<s:property default="N/A"
					value="%{basicProperty.longitude}" />
			</div>
		</div>
		<s:if test="%{basicProperty.latitude != null && basicProperty.longitude != null}">
		<div class="row add-border" align="center">
			<input type="button" name="showMap" id="show-map"
						value="View On Map" class="buttonsubmit" data-toggle="modal" data-target="#myModal"/>
		</div>				
		</s:if>
		
	</div>
</div>

<table style="width: 100%;">

	<tr>
		<td colspan="5">
			<div class="headingsmallbg"
				style="font-size: 19px; font-family: regular;">
				<span><s:text name="ownerdetails.title"></s:text></span>
			</div>
		</td>
	</tr>

	<tr>
		<td colspan="5">
			<div id="OwnerNameDiv">
				<%@ include file="../common/OwnerNameView.jsp"%>
			</div>
		</td>
	</tr>
	<tr class="amenities">
		<td colspan="5">
			<div class="headingsmallbg"
				style="font-size: 19px; font-family: regular;">
				<span> <s:text name="amenities"></s:text>
				</span>
			</div>
		</td>
	</tr>

	<tr class="amenities">
		<td colspan="5">
			<table width="100%" border="0" align="center" cellpadding="0"
				cellspacing="0" class="tablebottom">

				<tr>
					<th class="bluebgheadtd" width="11.1111111111%"><s:text
							name="lift"></s:text></th>
					<th class="bluebgheadtd" width="11.1111111111%"><s:text
							name="toilets"></s:text></th>
					<th class="bluebgheadtd" width="11.1111111111%"><s:text
							name="watertap"></s:text></th>
					<th class="bluebgheadtd" width="11.1111111111%"><s:text
							name="electricity"></s:text></th>
					<th class="bluebgheadtd" width="11.1111111111%"><s:text
							name="attachbathroom"></s:text></th>
					<th class="bluebgheadtd" width="11.1111111111%"><s:text
							name="waterharvesting"></s:text></th>
					<th class="bluebgheadtd" width="11.1111111111%"><s:text
							name="cableconnection"></s:text></th>
				</tr>

				<tr>
                        <td class="blueborderfortd"><s:if
                                test="#prop.propertyDetail.lift">Yes</s:if> <s:else>No</s:else>
                        </td>
                        <td class="blueborderfortd"><s:if
                                test="#prop.propertyDetail.toilets">Yes</s:if>
                            <s:else>No</s:else></td>
                        <td class="blueborderfortd"><s:if
                                test="#prop.propertyDetail.waterTap">Yes</s:if>
                            <s:else>No</s:else></td>
                        <td class="blueborderfortd"><s:if
                                test="#prop.propertyDetail.electricity">Yes</s:if>
                            <s:else>No</s:else></td>
                        <td class="blueborderfortd"><s:if
                                test="#prop.propertyDetail.attachedBathRoom">Yes</s:if>
                            <s:else>No</s:else></td>
                        <td class="blueborderfortd"><s:if
                                test="#prop.propertyDetail.waterHarvesting">Yes</s:if>
                            <s:else>No</s:else></td>
                        <td class="blueborderfortd"><s:if
                                test="#prop.propertyDetail.cable">Yes</s:if>
                            <s:else>No</s:else></td>
                 </tr>
			</table>
			<br />
		<br />
		</td>
	</tr>
</table>

<div class="panel panel-primary construction" data-collapsed="0"
	style="text-align: left">
	<div class="panel-heading">
		<div class="panel-title">
			<s:text name="title.constructiontypes" />
		</div>

	</div>
	<div class="panel-body">
		<div class="row add-border">
			<div class="col-xs-3 add-margin">
				<s:text name="floortype"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
                    <s:property default="N/A"
                                value="#prop.propertyDetail.floorType.name" />

			</div>
			<div class="col-xs-3 add-margin">
				<s:text name="rooftype"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
                  <s:property default="N/A"
                                value="#prop.propertyDetail.roofType.name" />

			</div>
		</div>
		<div class="row add-border">
			<div class="col-xs-3 add-margin">
				<s:text name="walltype"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
                    <s:property default="N/A"
                                value="#prop.propertyDetail.wallType.name" />

			</div>
			<div class="col-xs-3 add-margin">
				<s:text name="woodtype"></s:text>
			</div>
			<div class="col-xs-3 add-margin view-content">
               <s:property default="N/A"
                                value="#prop.propertyDetail.woodType.name" />

			</div>
		</div>
	</div>
</div>

<table class="table-fixed">
	<tr class="floordetails">
		<td colspan="5" width="5%">
		    <div class="headingsmallbg" style="font-size: 19px; font-family: regular;">
				<span><s:text name="FloorDetailsHeader" /> </span>
			</div>
		</td>
	</tr>

	<tr class="floordetails">
		<td colspan="5">
			<div class="overflow-x-scroll">
				<%@ include file="../common/FloorView.jsp"%>
			</div>
		</td>
	</tr>
	<tr class="vacantlanddetaills">
		<td colspan="5">
			<div class="headingsmallbg"
				style="font-size: 19px; font-family: regular;">
				<span><s:text name="VacantLandDetailsHeader" /> </span>
			</div>
		</td>
	</tr>

	<tr class="vacantlanddetaills">
		<td colspan="5">
			<div align="center">
				<%@ include file="../common/vacantLandView.jsp"%> 
			</div>
		</td>
	</tr>

</table>
<br>
<div class="panel panel-primary" data-collapsed="0"
	style="text-align: left">
	<div class="panel-heading">
		<div class="panel-title">Tax Details</div>
	</div>
	<%@ include file="../common/taxDetails.jsp"%>	
</div>
<div id="myModal" class="modal fade" role="dialog">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Property On Google Map</h4>
      </div>
			<div id="map-canvas" style="height:500px;width:500pxpx;"></div>      
			<div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>

  </div>
</div>
<script type="text/javascript">
	function showDocumentManagerView(indexNum) {
		var url = "../view/viewProperty!viewDoc.action?propertyId=" + indexNum;
		window.open(url, 'docview', 'width=1000,height=400');
	}
	var lat = parseFloat('<s:property value="%{basicProperty.latitude}"/>');
    var lng = parseFloat('<s:property value="%{basicProperty.longitude}"/>');
	jQuery('#show-map').on('click',initialize(lat, lng));
</script>


