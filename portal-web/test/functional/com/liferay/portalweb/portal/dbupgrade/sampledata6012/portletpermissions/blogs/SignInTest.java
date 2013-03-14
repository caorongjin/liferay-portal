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

package com.liferay.portalweb.portal.dbupgrade.sampledata6012.portletpermissions.blogs;

import com.liferay.portalweb.portal.BaseTestCase;
import com.liferay.portalweb.portal.util.RuntimeVariables;

/**
 * @author Brian Wing Shun Chan
 */
public class SignInTest extends BaseTestCase {
	public void testSignIn() throws Exception {
		selenium.selectWindow("null");
		selenium.selectFrame("relative=top");
		selenium.open("/web/guest/home/");
		selenium.waitForElementPresent("link=Sign In");
		selenium.clickAt("link=Sign In", RuntimeVariables.replace("Sign In"));
		selenium.waitForPageToLoad("30000");
		selenium.type("//input[@name='_58_login']",
			RuntimeVariables.replace("test@liferay.com"));
		selenium.type("//input[@name='_58_password']",
			RuntimeVariables.replace("test"));
		selenium.clickAt("//input[@name='_58_rememberMeCheckbox']",
			RuntimeVariables.replace("Remember Me"));
		selenium.clickAt("//input[@value='Sign In']",
			RuntimeVariables.replace("Sign In"));
		selenium.waitForPageToLoad("30000");
	}
}