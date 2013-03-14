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

package com.liferay.portalweb.portlet.webcontentlist.portlet.configureportletwclfilterstructure;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portal.controlpanel.webcontent.wcstructure.addwcstructure.AddWCStructureTest;
import com.liferay.portalweb.portal.controlpanel.webcontent.wcstructure.addwcstructure.TearDownWCStructureTest;
import com.liferay.portalweb.portal.util.TearDownPageTest;
import com.liferay.portalweb.portlet.webcontentlist.portlet.addportletwcl.AddPageWCLTest;
import com.liferay.portalweb.portlet.webcontentlist.portlet.addportletwcl.AddPortletWCLTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class ConfigurePortletWCLFilterStructureTests extends BaseTestSuite {
	public static Test suite() {
		TestSuite testSuite = new TestSuite();
		testSuite.addTestSuite(AddPageWCLTest.class);
		testSuite.addTestSuite(AddPortletWCLTest.class);
		testSuite.addTestSuite(AddWCStructureTest.class);
		testSuite.addTestSuite(ConfigurePortletWCLFilterStructureTest.class);
		testSuite.addTestSuite(ViewPortletWCLFilterStructureTest.class);
		testSuite.addTestSuite(TearDownWCStructureTest.class);
		testSuite.addTestSuite(TearDownPageTest.class);

		return testSuite;
	}
}