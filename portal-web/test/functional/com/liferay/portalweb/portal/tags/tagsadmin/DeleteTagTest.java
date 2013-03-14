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

package com.liferay.portalweb.portal.tags.tagsadmin;

import com.liferay.portalweb.portal.BaseTestCase;
import com.liferay.portalweb.portal.util.RuntimeVariables;

/**
 * @author Brian Wing Shun Chan
 */
public class DeleteTagTest extends BaseTestCase {
	public void testDeleteTag() throws Exception {
		selenium.selectWindow("null");
		selenium.selectFrame("relative=top");
		selenium.open("/web/guest/home/");
		selenium.waitForElementPresent("link=Control Panel");
		selenium.clickAt("link=Control Panel",
			RuntimeVariables.replace("Control Panel"));
		selenium.waitForPageToLoad("30000");
		selenium.clickAt("link=Tags", RuntimeVariables.replace("Tags"));
		selenium.waitForPageToLoad("30000");
		selenium.waitForVisible("//h1[@class='header-title']/span");
		assertEquals(RuntimeVariables.replace("selenium ide"),
			selenium.getText("//h1[@class='header-title']/span"));
		selenium.clickAt("//input[@id='deleteTagButton']",
			RuntimeVariables.replace("Delete"));
		assertTrue(selenium.getConfirmation()
						   .matches("^Are you sure you want to delete this tag[\\s\\S]$"));
		selenium.waitForVisible("//div[@id='portletMessages']");
		assertEquals(RuntimeVariables.replace(
				"Your request processed successfully."),
			selenium.getText("//div[@id='portletMessages']"));
		assertTrue(selenium.isElementNotPresent("//div[2]/ul/li/div/span/a"));
		assertTrue(selenium.isElementNotPresent("link=selenium ide"));
	}
}