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
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Sergio González
 */
public class UpgradeDocumentLibrary extends UpgradeProcess {
	protected void updateFileEntriesTitle() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getConnection();

			ps = con.prepareStatement(
				"select fileEntryId, title, extension from DLFileEntry");

			rs = ps.executeQuery();

			while (rs.next()) {
				long fileEntryId = rs.getLong("fileEntryId");

				String title = rs.getString("title");
				String extension = rs.getString("extension");

				StringBundler sb = new StringBundler(5);

				sb.append("update DLFileEntry set title = '");
				sb.append(title);
				sb.append(StringPool.PERIOD);
				sb.append(extension);
				sb.append("' where fileEntryId = ");
				sb.append(fileEntryId);

				runSQL(sb.toString());
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected void updateFileVersionTitle() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getConnection();

			ps = con.prepareStatement(
				"select fileVersionId, title, extension from DLFileVersion");

			rs = ps.executeQuery();

			while (rs.next()) {
				long fileVersionId = rs.getLong("fileVersionId");

				String title = rs.getString("title");
				String extension = rs.getString("extension");

				StringBundler sb = new StringBundler(5);

				sb.append("update DLFileVersion set title = '");
				sb.append(title);
				sb.append(StringPool.PERIOD);
				sb.append(extension);
				sb.append("' where fileVersionId = ");
				sb.append(fileVersionId);

				runSQL(sb.toString());
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		updateFileEntriesTitle();
		updateFileVersionTitle();
	}

}