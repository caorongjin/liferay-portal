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

package com.liferay.portalweb.portal.controlpanel.recyclebin.blogs.movetorecyclebinblogentry;

import com.liferay.portalweb.portal.BaseTestCase;
import com.liferay.portalweb.portal.util.RuntimeVariables;

/**
 * @author Brian Wing Shun Chan
 */
public class MoveToRecycleBinBlogsEntryTest extends BaseTestCase {
	public void testMoveToRecycleBinBlogsEntry() throws Exception {
		selenium.selectWindow("null");
		selenium.selectFrame("relative=top");
		selenium.open("/web/guest/home/");
		selenium.clickAt("link=Blogs Test Page",
			RuntimeVariables.replace("Blogs Test Page"));
		selenium.waitForPageToLoad("30000");
		assertEquals(RuntimeVariables.replace("Blogs Entry Title"),
			selenium.getText(
				"//div[@class='entry-title']/h2[contains(.,'Blogs Entry Title')]/a"));
		selenium.waitForVisible(
			"//div[@class='lfr-meta-actions edit-actions entry']/table/tbody/tr[contains(.,'Move to the Recycle Bin')]/td[3]/span/a");
		assertEquals(RuntimeVariables.replace("Move to the Recycle Bin"),
			selenium.getText(
				"//div[@class='lfr-meta-actions edit-actions entry']/table/tbody/tr[contains(.,'Move to the Recycle Bin')]/td[3]/span/a"));
		selenium.clickAt("//div[@class='lfr-meta-actions edit-actions entry']/table/tbody/tr[contains(.,'Move to the Recycle Bin')]/td[3]/span/a",
			RuntimeVariables.replace("Move to the Recycle Bin"));
		selenium.waitForPageToLoad("30000");
		assertEquals(RuntimeVariables.replace(
				"The selected item was moved to the Recycle Bin. Undo"),
			selenium.getText(
				"//div[@class='portlet-msg-success taglib-trash-undo']"));
		assertFalse(selenium.isTextPresent("Blogs Entry Title"));
	}
}