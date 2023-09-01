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
package org.egov.ptis.actions.view;

import static org.egov.ptis.constants.PropertyTaxConstants.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.infra.admin.master.entity.Role;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.utils.StringUtils;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.ptis.client.util.PropertyTaxUtil;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.dao.demand.PtDemandDao;
import org.egov.ptis.domain.dao.property.BasicPropertyDAO;
import org.egov.ptis.domain.entity.demand.Ptdemand;
import org.egov.ptis.domain.entity.document.DocumentTypeDetails;
import org.egov.ptis.domain.entity.objection.RevisionPetition;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.Document;
import org.egov.ptis.domain.entity.property.Floor;
import org.egov.ptis.domain.entity.property.Property;
import org.egov.ptis.domain.entity.property.PropertyImpl;
import org.egov.ptis.domain.entity.property.PropertyMutation;
import org.egov.ptis.domain.entity.property.PropertyStatusValues;
import org.egov.ptis.domain.entity.property.VacancyRemission;
import org.egov.ptis.domain.repository.vacancyremission.VacancyRemissionRepository;
import org.egov.ptis.domain.service.property.PropertyService;
import org.egov.ptis.domain.service.transfer.PropertyTransferService;
import org.egov.ptis.notice.PtNotice;
import org.egov.ptis.service.utils.PropertyTaxCommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


@ParentPackage("egov")
@Results({ @Result(name = "view", location = "viewProperty-view.jsp"),
        @Result(name = "viewApplication", location = "viewApplication-view.jsp"),
        @Result(name = "viewApplicationError", location = "viewProperty-error.jsp") })
public class ViewPropertyAction extends BaseFormAction {

    private static final String DOCUMENTDATE = "documentdate";
    private static final String DOCUMENTNO = "documentno";
    private static final long serialVersionUID = 4609817011534083012L;
    private static final Logger LOGGER = Logger.getLogger(ViewPropertyAction.class);
    private String propertyId;
    private BasicProperty basicProperty;
    private PropertyImpl property;
    private transient Map<String, Object> viewMap;
    private transient PropertyTaxUtil propertyTaxUtil;
    private String roleName;
    private boolean isDemandActive;
    private String applicationNo;
    private String applicationType;
    private String[] floorNoStr = new String[275];
    private String errorMessage;
    private String isCitizen;
    private boolean citizenPortalUser;
    private List<PtNotice> endorsementNotices;

    @Autowired
    private transient BasicPropertyDAO basicPropertyDAO;
    @Autowired
    private transient PtDemandDao ptDemandDAO;
    @Autowired
    private transient UserService userService;
    @Autowired
    @Qualifier("transferOwnerService")
    private transient PropertyTransferService transferOwnerService;
    @Autowired
    private transient PropertyTaxCommonUtils propertyTaxCommonUtils;
    @Autowired
    private transient PropertyService propService;
    @Autowired
    private transient VacancyRemissionRepository vacancyRemissionRepository;
    @PersistenceContext
    private transient EntityManager entityManager;

    private transient Map<String, Map<String, BigDecimal>> demandCollMap = new TreeMap<>();
    
    private transient List<HashMap<String, Object>> historyMap = new ArrayList<>();
    
    private transient List<Document> documents = new ArrayList<>();
       
    @Override
    public StateAware getModel() {
        return property;
    }

    @Action(value = "/view/viewProperty-viewForm")
    public String viewForm() {
        try {
            viewMap = new HashMap<>();
            if (propertyId != null && !propertyId.isEmpty())
                setBasicProperty(basicPropertyDAO.getBasicPropertyByPropertyID(propertyId));
            else if (applicationNo != null && !applicationNo.isEmpty()) {
                    getBasicPropForAppNo(applicationNo, applicationType);
                setPropertyId(basicProperty.getUpicNo());
            }
            if (property == null)
                property = (PropertyImpl) getBasicProperty().getProperty();
            final Ptdemand ptDemand = ptDemandDAO.getNonHistoryCurrDmdForProperty(property);
            if (ptDemand == null) {
                setErrorMessage("No Tax details for current Demand period.");
                return "view";
            }
            if (!property.getPropertyDetail().getFloorDetails().isEmpty())
                setFloorDetails(property);
            checkIsDemandActive(property);
            viewMap.put("doorNo", getBasicProperty().getAddress().getHouseNoBldgApt() == null ? NOT_AVAILABLE
                    : getBasicProperty().getAddress().getHouseNoBldgApt());
			viewMap.put("ownerAddress",
					getBasicProperty().getPropertyOwnerInfo().get(0).getOwner().getAddress().isEmpty() ? NOT_AVAILABLE
							: getBasicProperty().getPropertyOwnerInfo().get(0).getOwner().getAddress().get(0)
									.toString());
			viewMap.put("ownershipType", basicProperty.getProperty() != null
                    ? basicProperty.getProperty().getPropertyDetail().getPropertyTypeMaster().getType()
                    : property.getPropertyDetail().getPropertyTypeMaster()
                            .getType());
            if (!property.getIsExemptedFromTax()) {
                demandCollMap = propertyTaxUtil.prepareDemandDetForView(property,
                        propertyTaxCommonUtils.getCurrentInstallment());
                for (final Entry<String, Map<String, BigDecimal>> entry : demandCollMap.entrySet()) {
                    final String key = entry.getKey();
                    final Map<String, BigDecimal> reasonDmd = entry.getValue();
                    if (key.equals(CURRENTYEAR_FIRST_HALF)) {
                        viewMap.put("firstHalf", CURRENTYEAR_FIRST_HALF);
                        viewMap.put(
                                "firstHalfGT",
                                reasonDmd.get(DEMANDRSN_STR_GENERAL_TAX) != null ? propertyTaxCommonUtils.getAggregateGenralTax(reasonDmd)
                                        : reasonDmd.get(DEMANDRSN_STR_VACANT_TAX));
                        viewMap.put(
                                "firstHalfEC",
                                reasonDmd.get(DEMANDRSN_STR_EDUCATIONAL_TAX) != null ? reasonDmd
                                        .get(DEMANDRSN_STR_EDUCATIONAL_TAX) : BigDecimal.ZERO);
                        viewMap.put("firstHalfLC", reasonDmd.get(DEMANDRSN_STR_LIBRARY_CESS));
                        viewMap.put(
                                "firstHalfUAP",
                                reasonDmd.get(DEMANDRSN_STR_UNAUTHORIZED_PENALTY) != null ? reasonDmd
                                        .get(DEMANDRSN_STR_UNAUTHORIZED_PENALTY) : BigDecimal.ZERO);
                        viewMap.put(
                                "firstHalfTotal", reasonDmd.get(CURR_FIRSTHALF_DMD_STR));
                        viewMap.put(
                                "firstHalfTaxDue",
                                reasonDmd.get(CURR_FIRSTHALF_DMD_STR)
                                        .subtract(reasonDmd.get(CURR_FIRSTHALF_COLL_STR)));

                    } else if (key.equals(CURRENTYEAR_SECOND_HALF)) {
                        viewMap.put("secondHalf", CURRENTYEAR_SECOND_HALF);
                        viewMap.put(
                                "secondHalfGT",
                                reasonDmd.get(DEMANDRSN_STR_GENERAL_TAX) != null ? propertyTaxCommonUtils.getAggregateGenralTax(reasonDmd)
                                        : reasonDmd.get(DEMANDRSN_STR_VACANT_TAX));
                        viewMap.put(
                                "secondHalfEC",
                                reasonDmd.get(DEMANDRSN_STR_EDUCATIONAL_TAX) != null ? reasonDmd
                                        .get(DEMANDRSN_STR_EDUCATIONAL_TAX) : BigDecimal.ZERO);
                        viewMap.put("secondHalfLC", reasonDmd.get(DEMANDRSN_STR_LIBRARY_CESS));
                        viewMap.put(
                                "secondHalfUAP",
                                reasonDmd.get(DEMANDRSN_STR_UNAUTHORIZED_PENALTY) != null ? reasonDmd
                                        .get(DEMANDRSN_STR_UNAUTHORIZED_PENALTY) : BigDecimal.ZERO);
                        viewMap.put(
                                "secondHalfTotal", reasonDmd.get(CURR_SECONDHALF_DMD_STR));
                        viewMap.put(
                                "secondHalfTaxDue",
                                reasonDmd.get(CURR_SECONDHALF_DMD_STR)
                                        .subtract(reasonDmd.get(CURR_SECONDHALF_COLL_STR)));

                    } else {
                        viewMap.put("arrears", ARREARS);
                        viewMap.put("arrearTax", reasonDmd.get(ARR_DMD_STR));
                        viewMap.put("totalArrDue", reasonDmd.get(ARR_DMD_STR).subtract(reasonDmd.get(ARR_COLL_STR)));
                    }
                }

            }
            if (ptDemand.getDmdCalculations() != null && ptDemand.getDmdCalculations().getAlv() != null)
                viewMap.put("ARV", ptDemand.getDmdCalculations().getAlv());
            else
                viewMap.put("ARV", BigDecimal.ZERO);

            propertyTaxUtil.setPersistenceService(persistenceService);
            PropertyStatusValues statusValues = propertyTaxCommonUtils.getPropStatusValues(basicProperty);
            if (null != statusValues && null != statusValues.getReferenceBasicProperty())
                viewMap.put("parentProps", statusValues.getReferenceBasicProperty().getUpicNo());
            
            final Long userId = (Long) session().get(SESSIONLOGINID);
            if (userId != null) {
                setRoleName(getRolesForUserId(userId));
                citizenPortalUser = propService.isCitizenPortalUser(userService.getUserById(userId));
            }
            if (StringUtils.isNotBlank(applicationNo))
                return "viewApplication";
			else {
				getPropertyDocumentDetails();
				return "view";
			}
        } catch (final Exception e) {
            LOGGER.error("Exception in View Property: ", e);
            return "viewApplicationError";
        }
    }

	private void getPropertyDocumentDetails() {
		if (propertyTaxCommonUtils.getLatestApprovedMutationForAssessmentNo(basicProperty.getUpicNo()) != null) {
			PropertyMutation propertyMutation = propertyTaxCommonUtils
					.getLatestApprovedMutationForAssessmentNo(basicProperty.getUpicNo());
			viewMap.put(DOCUMENTNO,
					propertyMutation.getDeedNo() != null ? propertyMutation.getDeedNo() : StringUtils.EMPTY);
			viewMap.put(DOCUMENTDATE,
					propertyMutation.getDeedDate() != null ? propertyMutation.getDeedDate() : null);
		} else {
			setDocDetails();
		}
	}

    private void checkIsDemandActive(final Property property) {
        if (property.getStatus().equals(PropertyTaxConstants.STATUS_DEMAND_INACTIVE))
            isDemandActive = false;
        else
            isDemandActive = true;
    }

    private String getRolesForUserId(final Long userId) {
        String roleName;
        final List<String> roleNameList = new ArrayList<>();
        final User user = userService.getUserById(userId);
        for (final Role role : user.getRoles()) {
            roleName = role.getName() != null ? role.getName() : "";
            roleNameList.add(roleName);
        }
        return roleNameList.toString().toUpperCase();
    }

    private void getBasicPropForAppNo(final String appNo, final String appType){
        if (!StringUtils.isBlank(appType))
            if (Arrays.asList(APPLICATION_TYPE_NEW_ASSESSENT, APPLICATION_TYPE_ALTER_ASSESSENT, APPLICATION_TYPE_TAX_EXEMTION,
                    APPLICATION_TYPE_BIFURCATE_ASSESSENT,
                    APPLICATION_TYPE_DEMOLITION, APPLICATION_TYPE_AMALGAMATION).contains(appType)) {
                final Query query = entityManager.createNamedQuery("PROPERTYIMPL_BY_APPLICATIONNO");
                query.setParameter("applicatiNo", appNo);
                property = (PropertyImpl) query.getSingleResult();
                setBasicProperty(property.getBasicProperty());
                setHistoryMap(propService.populateHistory(property));
                if (appType.equalsIgnoreCase(APPLICATION_TYPE_NEW_ASSESSENT)) {
                    getDocumentDetails();
                } else if (appType.equals(APPLICATION_TYPE_TAX_EXEMTION) && property.getIsExemptedFromTax()) {
                    property.setDocuments(property.getTaxExemptionDocuments());
                }
            } else if (appType.equalsIgnoreCase(APPLICATION_TYPE_REVISION_PETITION)
                    || appType.equalsIgnoreCase(APPLICATION_TYPE_GRP)) {
                final Query query = entityManager.createNamedQuery("RP_BY_APPLICATIONNO");
                query.setParameter("applicatiNo", appNo);
                RevisionPetition rp = (RevisionPetition) query.getSingleResult();
                setBasicProperty(rp.getBasicProperty());
                setHistoryMap(propService.populateHistory(rp));
                property = (PropertyImpl) rp.getBasicProperty().getProperty();
                property.setDocuments(rp.getDocuments());
            } else if (appType.equalsIgnoreCase(APPLICATION_TYPE_TRANSFER_OF_OWNERSHIP)) {
                final PropertyMutation propertyMutation = transferOwnerService
                        .getPropertyMutationByApplicationNo(appNo);
                setBasicProperty(propertyMutation.getBasicProperty());
                setHistoryMap(propService.populateHistory(propertyMutation));
                property = (PropertyImpl) propertyMutation.getBasicProperty().getProperty();
                property.setDocuments(propertyMutation.getDocuments());
                if (propertyMutation.getMutationReason().getType().equals(ADDITIONAL_RULE_FULL_TRANSFER)) {
                    viewMap.put(DOCUMENTNO, propertyMutation.getMutationRegistrationDetails().getDocumentNo());
                    viewMap.put(DOCUMENTDATE, propertyMutation.getMutationRegistrationDetails().getDocumentDate());
                }
                else if (propertyMutation.getMutationReason().getCode().equals(CIVILCOURTDECREE)) {
                    viewMap.put(DOCUMENTNO, propertyMutation.getDecreeNumber());
                    viewMap.put(DOCUMENTDATE, propertyMutation.getDecreeDate());
                }
                else {
                    viewMap.put(DOCUMENTNO, propertyMutation.getDeedNo());
                    viewMap.put(DOCUMENTDATE, propertyMutation.getDeedDate());
                }
            } else if (appType.equalsIgnoreCase(APPLICATION_TYPE_VACANCY_REMISSION)) {
                final VacancyRemission vacancyRemission= vacancyRemissionRepository.getVRByApplicationNo(appNo);
                setBasicProperty(vacancyRemission.getBasicProperty());
                setHistoryMap(propService.populateHistory(vacancyRemission));
                property = vacancyRemission.getBasicProperty().getActiveProperty();
                property.setDocuments(vacancyRemission.getDocuments());
            }
        endorsementNotices = propertyTaxCommonUtils.getEndorsementNotices(appNo);
    }
    
   public void getDocumentDetails() {
        setDocDetails();
        if (property.getStatus().equals('W'))
            viewMap.put("propertyWF", "WF");
    }

	private void setDocDetails() {
		try {
			final Query query = entityManager.createNamedQuery("DOCUMENT_TYPE_DETAILS_BY_ID");
			query.setParameter("basicProperty", basicProperty.getId());
			DocumentTypeDetails documentTypeDetails = (DocumentTypeDetails) query.getSingleResult();
			viewMap.put(DOCUMENTNO, documentTypeDetails.getDocumentNo());
			viewMap.put(DOCUMENTDATE, documentTypeDetails.getDocumentDate());
		} catch (Exception e) {
			viewMap.put(DOCUMENTNO,
					basicProperty.getRegdDocNo() != null ? basicProperty.getRegdDocNo() : StringUtils.EMPTY);
			viewMap.put(DOCUMENTDATE, basicProperty.getRegdDocDate() != null ? basicProperty.getRegdDocDate() : null);
		}
	}

    public void setFloorDetails(final Property property) {
        final List<Floor> floors = property.getPropertyDetail().getFloorDetails();
        property.getPropertyDetail().setFloorDetailsProxy(floors);
        int i = 0;
        for (final Floor flr : floors) {
            floorNoStr[i] = FLOOR_MAP.get(flr.getFloorNo());
            i++;
        }
    }

    public String getFloorNoStr(final Integer floorNo) {
        return FLOOR_MAP.get(floorNo);
    }

    public List<Floor> getFloorDetails() {
        return new ArrayList<>(property.getPropertyDetail().getFloorDetails());
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(final String propertyId) {
        this.propertyId = propertyId;
    }

    public BasicProperty getBasicProperty() {
        return basicProperty;
    }

    public void setBasicProperty(final BasicProperty basicProperty) {
        this.basicProperty = basicProperty;
    }

    public PropertyTaxUtil getPropertyTaxUtil() {
        return propertyTaxUtil;
    }

    public void setPropertyTaxUtil(final PropertyTaxUtil propertyTaxUtil) {
        this.propertyTaxUtil = propertyTaxUtil;
    }

    public Map<String, Object> getViewMap() {
        return viewMap;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(final String roleName) {
        this.roleName = roleName;
    }

    public boolean getIsDemandActive() {
        return isDemandActive;
    }

    public void setIsDemandActive(final boolean isDemandActive) {
        this.isDemandActive = isDemandActive;
    }

    public Property getProperty() {
        return property;
    }

    public void setBasicPropertyDAO(final BasicPropertyDAO basicPropertyDAO) {
        this.basicPropertyDAO = basicPropertyDAO;
    }

    public void setPtDemandDAO(final PtDemandDao ptDemandDAO) {
        this.ptDemandDAO = ptDemandDAO;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(final String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(final String applicationType) {
        this.applicationType = applicationType;
    }

    public String[] getFloorNoStr() {
        return floorNoStr;
    }

    public void setFloorNoStr(final String[] floorNoStr) {
        this.floorNoStr = floorNoStr;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, Map<String, BigDecimal>> getDemandCollMap() {
        return demandCollMap;
    }

    public void setDemandCollMap(final Map<String, Map<String, BigDecimal>> demandCollMap) {
        this.demandCollMap = demandCollMap;
    }

    public String getIsCitizen() {
        return isCitizen;
    }

    public void setIsCitizen(final String isCitizen) {
        this.isCitizen = isCitizen;
    }

    public PropertyTaxCommonUtils getPropertyTaxCommonUtils() {
        return propertyTaxCommonUtils;
    }

    public void setPropertyTaxCommonUtils(final PropertyTaxCommonUtils propertyTaxCommonUtils) {
        this.propertyTaxCommonUtils = propertyTaxCommonUtils;
    }

    public boolean isCitizenPortalUser() {
        return citizenPortalUser;
    }

    public void setCitizenPortalUser(boolean citizenPortalUser) {
        this.citizenPortalUser = citizenPortalUser;
    }

    public void setPropService(PropertyService propService) {
        this.propService = propService;
    }

    public List<HashMap<String, Object>> getHistoryMap() {
        return historyMap;
    }

    public void setHistoryMap(List<HashMap<String, Object>> historyMap) {
        this.historyMap = historyMap;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<PtNotice> getEndorsementNotices() {
        return endorsementNotices;
    }

    public void setEndorsementNotices(List<PtNotice> endorsementNotices) {
        this.endorsementNotices = endorsementNotices;
    }
}
