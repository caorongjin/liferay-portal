<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/html/portlet/dynamic_data_list_display/init.jsp" %>

<%
DDLRecordSet recordSet = null;

try {
	if (Validator.isNotNull(recordSetId)) {
		recordSet = DDLRecordSetLocalServiceUtil.getRecordSet(recordSetId);
	}
%>

	<c:choose>
		<c:when test="<%= (recordSet != null) %>">

			<%
			portletDisplay.setTitle(recordSet.getName(locale));

			renderRequest.setAttribute(WebKeys.DYNAMIC_DATA_LISTS_RECORD_SET, recordSet);
			%>

			<liferay-util:include page="/html/portlet/dynamic_data_lists/view_record_set.jsp">
				<liferay-util:param name="displayDDMTemplateId" value="<%= String.valueOf(displayDDMTemplateId) %>" />
				<liferay-util:param name="formDDMTemplateId" value="<%= String.valueOf(formDDMTemplateId) %>" />
				<liferay-util:param name="editable" value="<%= String.valueOf(editable) %>" />
				<liferay-util:param name="spreadsheet" value="<%= String.valueOf(spreadsheet) %>" />
			</liferay-util:include>
		</c:when>
		<c:otherwise>

			<%
			renderRequest.setAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
			%>

			<br />

			<div class="portlet-msg-info">
				<liferay-ui:message key="select-an-existing-list-or-add-a-list-to-be-displayed-in-this-portlet" />
			</div>
		</c:otherwise>
	</c:choose>

<%
}
catch (NoSuchRecordSetException nsrse) {
%>

	<div class="portlet-msg-error">
		<%= LanguageUtil.get(pageContext, "the-selected-list-no-longer-exists") %>
	</div>

<%
}

boolean hasConfigurationPermission = PortletPermissionUtil.contains(permissionChecker, layout, portletDisplay.getId(), ActionKeys.CONFIGURATION);

boolean showAddListIcon = hasConfigurationPermission && DDLPermission.contains(permissionChecker, scopeGroupId, ActionKeys.ADD_RECORD_SET);
boolean showAddTemplateIcon = (recordSet != null) && DDMPermission.contains(permissionChecker, scopeGroupId, ddmResource, ActionKeys.ADD_TEMPLATE);
boolean showEditDisplayTemplateIcon = (displayDDMTemplateId != 0) && DDMTemplatePermission.contains(permissionChecker, displayDDMTemplateId, ActionKeys.UPDATE);
boolean showEditFormTemplateIcon = (formDDMTemplateId != 0) && DDMTemplatePermission.contains(permissionChecker, formDDMTemplateId, ActionKeys.UPDATE);
%>

<c:if test="<%= themeDisplay.isSignedIn() && (showAddListIcon || showAddTemplateIcon || showEditDisplayTemplateIcon || showEditFormTemplateIcon || hasConfigurationPermission ) %>">
	<div class="lfr-meta-actions icons-container">
		<div class="icon-actions">
			<c:if test="<%= showAddTemplateIcon %>">
				<liferay-portlet:renderURL portletName="<%= PortletKeys.DYNAMIC_DATA_MAPPING %>" var="addFormTemplateURL" windowState="<%= WindowState.MAXIMIZED.toString() %>">
					<portlet:param name="struts_action" value="/dynamic_data_mapping/edit_template" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
					<portlet:param name="portletResource" value="<%= portletDisplay.getId() %>" />
					<portlet:param name="portletResourceNamespace" value="<%= renderResponse.getNamespace() %>" />
					<portlet:param name="groupId" value="<%= String.valueOf(scopeGroupId) %>" />
					<portlet:param name="classNameId" value="<%= String.valueOf(PortalUtil.getClassNameId(DDMStructure.class)) %>" />
					<portlet:param name="classPK" value="<%= String.valueOf(recordSet.getDDMStructureId()) %>" />
					<portlet:param name="structureAvailableFields" value='<%= renderResponse.getNamespace() + "structureAvailableFields" %>' />
					<portlet:param name="ddmResource" value="<%= ddmResource %>" />
				</liferay-portlet:renderURL>

				<liferay-ui:icon
					image="add_template_form"
					message="add-form-template"
					url="<%= addFormTemplateURL %>"
				/>

				<liferay-portlet:renderURL portletName="<%= PortletKeys.DYNAMIC_DATA_MAPPING %>" var="addDisplayTemplateURL" windowState="<%= WindowState.MAXIMIZED.toString() %>">
					<portlet:param name="struts_action" value="/dynamic_data_mapping/edit_template" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
					<portlet:param name="portletResource" value="<%= portletDisplay.getId() %>" />
					<portlet:param name="groupId" value="<%= String.valueOf(scopeGroupId) %>" />
					<portlet:param name="classNameId" value="<%= String.valueOf(PortalUtil.getClassNameId(DDMStructure.class)) %>" />
					<portlet:param name="classPK" value="<%= String.valueOf(recordSet.getDDMStructureId()) %>" />
					<portlet:param name="type" value="<%= DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY %>" />
					<portlet:param name="ddmResource" value="<%= ddmResource %>" />
				</liferay-portlet:renderURL>

				<liferay-ui:icon
					image="add_template_display"
					message="add-display-template"
					url="<%= addDisplayTemplateURL %>"
				/>
			</c:if>

			<c:if test="<%= showEditFormTemplateIcon %>">
				<liferay-portlet:renderURL portletName="<%= PortletKeys.DYNAMIC_DATA_MAPPING %>" var="editFormTemplateURL" windowState="<%= WindowState.MAXIMIZED.toString() %>">
					<portlet:param name="struts_action" value="/dynamic_data_mapping/edit_template" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
					<portlet:param name="portletResource" value="<%= portletDisplay.getId() %>" />
					<portlet:param name="portletResourceNamespace" value="<%= renderResponse.getNamespace() %>" />
					<portlet:param name="templateId" value="<%= String.valueOf(formDDMTemplateId) %>" />
					<portlet:param name="structureAvailableFields" value='<%= renderResponse.getNamespace() + "structureAvailableFields" %>' />
				</liferay-portlet:renderURL>

				<liferay-ui:icon
					image="../file_system/small/xml"
					message="edit-form-template"
					url="<%= editFormTemplateURL %>"
				/>
			</c:if>

			<c:if test="<%= showEditDisplayTemplateIcon %>">
				<liferay-portlet:renderURL portletName="<%= PortletKeys.DYNAMIC_DATA_MAPPING %>" var="editDisplayTemplateURL" windowState="<%= WindowState.MAXIMIZED.toString() %>">
					<portlet:param name="struts_action" value="/dynamic_data_mapping/edit_template" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
					<portlet:param name="portletResource" value="<%= portletDisplay.getId() %>" />
					<portlet:param name="templateId" value="<%= String.valueOf(displayDDMTemplateId) %>" />
				</liferay-portlet:renderURL>

				<liferay-ui:icon
					image="../file_system/small/xml"
					message="edit-display-template"
					url="<%= editDisplayTemplateURL %>"
				/>
			</c:if>

			<c:if test="<%= hasConfigurationPermission %>">
				<liferay-ui:icon
					cssClass="portlet-configuration"
					image="configuration"
					message="select-list"
					method="get"
					onClick="<%= portletDisplay.getURLConfigurationJS() %>"
					url="<%= portletDisplay.getURLConfiguration() %>"
				/>
			</c:if>

			<c:if test="<%= showAddListIcon %>">
				<liferay-portlet:renderURL portletName="<%= PortletKeys.DYNAMIC_DATA_LISTS %>" var="addListURL" windowState="<%= WindowState.MAXIMIZED.toString() %>">
					<portlet:param name="struts_action" value="/dynamic_data_lists/edit_record_set" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
					<portlet:param name="portletResource" value="<%= portletDisplay.getId() %>" />
					<portlet:param name="groupId" value="<%= String.valueOf(scopeGroupId) %>" />
				</liferay-portlet:renderURL>

				<liferay-ui:icon
					image="add_article"
					message="add-list"
					url="<%= addListURL %>"
				/>
			</c:if>
		</div>
	</div>
</c:if>