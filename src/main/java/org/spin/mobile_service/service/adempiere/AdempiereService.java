/************************************************************************************
 * Copyright (C) 2012-2018 E.R.P. Consultores y Asociados, C.A.                     *
 * Contributor(s): Yamel Senih ysenih@erpya.com                                     *
 * This program is free software: you can redistribute it and/or modify             *
 * it under the terms of the GNU General Public License as published by             *
 * the Free Software Foundation, either version 2 of the License, or                *
 * (at your option) any later version.                                              *
 * This program is distributed in the hope that it will be useful,                  *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the                     *
 * GNU General Public License for more details.                                     *
 * You should have received a copy of the GNU General Public License                *
 * along with this program. If not, see <https://www.gnu.org/licenses/>.            *
 ************************************************************************************/
package org.spin.mobile_service.service.adempiere;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.core.domains.models.I_AD_Org;
import org.compiere.model.MOrg;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.spin.proto.mobile.adempiere.ListOrganizationsRequest;
import org.spin.proto.mobile.adempiere.ListOrganizationsResponse;
import org.spin.proto.mobile.adempiere.Organization;
import org.spin.service.grpc.authentication.SessionManager;
import org.spin.service.grpc.util.db.LimitUtil;
import org.spin.service.grpc.util.value.TextManager;

public class AdempiereService {

	public static ListOrganizationsResponse.Builder listOrganizations(ListOrganizationsRequest request) {
		String whereClause = "AD_Org_ID > 0";
		List<Object> parameters = new ArrayList<Object>();

		//	For search value
		final String searchValue = TextManager.getDecodeUrl(
			request.getSearchValue()
		);
		if (!Util.isEmpty(searchValue, true)) {
			whereClause += " AND ("
				+ "UPPER(Value) LIKE '%' || UPPER(?) || '%' "
				+ "OR UPPER(Name) LIKE '%' || UPPER(?) || '%' "
				+ ")"
			;
			//	Add parameters
			parameters.add(searchValue);
			parameters.add(searchValue);
		}

		Query query = new Query(
			Env.getCtx(),
			I_AD_Org.Table_Name,
			whereClause,
			null
		)
			.setClient_ID()
			.setOnlyActiveRecords(true)
			// .setApplyAccessFilter(MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO)
			.setParameters(parameters)
		;

		int count = query.count();
		String nexPageToken = "";
		int pageNumber = LimitUtil.getPageNumber(SessionManager.getSessionUuid(), request.getPageToken());
		int limit = LimitUtil.getPageSize(request.getPageSize());
		int offset = (pageNumber - 1) * limit;
		//	Set page token
		if (LimitUtil.isValidNextPageToken(count, offset, limit)) {
			nexPageToken = LimitUtil.getPagePrefix(SessionManager.getSessionUuid()) + (pageNumber + 1);
		}

		ListOrganizationsResponse.Data.Builder dataBuilder = ListOrganizationsResponse.Data.newBuilder()
			.setRecordCount(count)
			.setNextPageToken(nexPageToken)
		;

		query.setLimit(limit, offset)
			.getIDsAsList()
			.forEach(organizationId -> {
				Organization.Builder builder = convertOrganization(organizationId);
				dataBuilder.addOrganizations(builder);
			})
		;

		ListOrganizationsResponse.Builder responseBuilder = ListOrganizationsResponse.newBuilder()
			.setResult(true)
			.setData(dataBuilder)
		;
		return responseBuilder;
	}


	private static Organization.Builder convertOrganization(int organizationId) {
		if (organizationId < 0) {
			return Organization.newBuilder();
		}
		MOrg organization = MOrg.get(Env.getCtx(), organizationId);
		return convertOrganization(organization);
	}
	private static Organization.Builder convertOrganization(MOrg organization) {
		Organization.Builder builder = Organization.newBuilder();
		if (organization == null || organization.getAD_Org_ID() < 0) {
			return builder;
		}
		builder.setId(
				organization.getAD_Org_ID()
			)
			.setValue(
				TextManager.getValidString(
					organization.getValue()
				)
			)
			.setName(
				TextManager.getValidString(
					organization.getName()
				)
			)
			.setDescription(
				TextManager.getValidString(
					organization.getDescription()
				)
			)
		;
		return builder;
	}

}
