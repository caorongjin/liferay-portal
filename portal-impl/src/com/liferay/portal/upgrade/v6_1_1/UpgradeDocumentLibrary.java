/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.upgrade.v6_1_1;

import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Sergio González
 */
public class UpgradeDocumentLibrary extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updateFileEntries();
		updateFileEntryTypes();
	}

	protected boolean hasFileEntry(long groupId, long folderId, String title)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getConnection();

			ps = con.prepareStatement(
				"select count(*) from DLFileEntry where groupId = ? and " +
					"folderId = ? and title = ?");

			ps.setLong(1, groupId);
			ps.setLong(2, folderId);
			ps.setString(3, title);

			rs = ps.executeQuery();

			while (rs.next()) {
				long count = rs.getLong(1);

				if (count > 0) {
					return true;
				}
			}

			return false;
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected void updateFileEntries() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getConnection();

			ps = con.prepareStatement(
				"select fileEntryId, groupId, folderId, title, extension, " +
					"version from DLFileEntry");

			rs = ps.executeQuery();

			while (rs.next()) {
				long fileEntryId = rs.getLong("fileEntryId");
				long groupId = rs.getLong("groupId");
				long folderId = rs.getLong("folderId");
				String title = rs.getString("title");
				String extension = rs.getString("extension");
				String version = rs.getString("version");

				String periodAndExtension = StringPool.PERIOD + extension;

				if (!title.endsWith(periodAndExtension)) {
					continue;
				}

				title = FileUtil.stripExtension(title);

				String uniqueTitle = title;

				int count = 0;

				while (hasFileEntry(groupId, folderId, uniqueTitle) ||
					   ((count != 0) &&
						hasFileEntry(
							groupId, folderId,
							uniqueTitle + periodAndExtension))) {

					count++;

					uniqueTitle = title + String.valueOf(count);
				}

				if (count <= 0) {
					continue;
				}

				uniqueTitle += periodAndExtension;

				runSQL(
					"update DLFileEntry set title = '" + uniqueTitle +
						"' where fileEntryId = " + fileEntryId);
				runSQL(
					"update DLFileVersion set title = '" + uniqueTitle +
						"' where fileEntryId = " + fileEntryId +
							" and DLFileVersion.version = '" + version + "'");
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected void updateFileEntryType(long fileEntryTypeId, long stuctureId)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getConnection();

			ps = con.prepareStatement(
				"select uuid_ from dlFileEntryType where fileEntryTypeId = "
					+ String.valueOf(fileEntryTypeId));

			rs = ps.executeQuery();

			while (rs.next()) {
				String uuid = rs.getString("uuid_");

				runSQL("update ddmStructure set structureKey = 'auto_" + uuid +
					"' where structureId = " + stuctureId);
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected void updateFileEntryTypes() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getConnection();

			ps = con.prepareStatement(
				"select structureId, structureKey from ddmStructure where " +
					"type_ = 1");

			rs = ps.executeQuery();

			while (rs.next()) {
				long structureId = rs.getLong("structureId");

				String structureKey = rs.getString("structureKey");

				try {
					long fileEntryTypeId = Long.valueOf(StringUtil.extractLast(
						structureKey, StringPool.UNDERLINE));

					updateFileEntryType(fileEntryTypeId, structureId);
				}
				catch (NumberFormatException nfe) {
					continue;
				}
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

}