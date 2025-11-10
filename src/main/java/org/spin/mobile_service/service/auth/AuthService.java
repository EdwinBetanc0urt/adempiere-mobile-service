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
package org.spin.mobile_service.service.auth;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MSession;
import org.compiere.model.MUser;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Login;
import org.compiere.util.Util;
import org.eevolution.hr.model.MHRDepartment;
import org.eevolution.hr.model.MHREmployee;
import org.spin.proto.mobile.auth.CheckTokenRequest;
import org.spin.proto.mobile.auth.CheckTokenResponse;
import org.spin.proto.mobile.auth.LoginRequest;
import org.spin.proto.mobile.auth.LoginResponse;
import org.spin.proto.mobile.auth.LoginResponseData;
import org.spin.proto.mobile.auth.LogoutRequest;
import org.spin.proto.mobile.auth.LogoutResponse;
import org.spin.service.grpc.authentication.SessionManager;
import org.spin.service.grpc.util.value.TextManager;

public class AuthService {

	public static CheckTokenResponse checkToken(CheckTokenRequest chekToken) {
		if(Util.isEmpty(chekToken.getToken())) {
			throw new AdempiereException("Token is Mandatory");
		}
		//	
		CheckTokenResponse.Builder response = CheckTokenResponse.newBuilder();
		//	Validate Token
		SessionManager.getSessionFromToken(chekToken.getToken());
		return response.build();
	}


	public static LogoutResponse logout(LogoutRequest logout) {
		MSession session = MSession.get(Env.getCtx(), false);
		//	Logout
		session.logout();
		return LogoutResponse.newBuilder().setMessage("Logged Out").setResult(true).build();
	}


	public static LoginResponse login(LoginRequest loginRequest) {
		if(Util.isEmpty(loginRequest.getEmail()) || Util.isEmpty(loginRequest.getPassword())) {
			throw new AdempiereException("@AD_User_ID@ @NotFound@");
		}
		
		Login login = new Login(Env.getCtx());
		int userId = login.getAuthenticatedUserId(loginRequest.getEmail(), loginRequest.getPassword());
		//	Get Values from role
		if(userId <= 0) {
			throw new AdempiereException("@AD_User_ID@ / @AD_Role_ID@ / @AD_Org_ID@ @NotFound@");
		}
		MUser user = MUser.get(Env.getCtx(), userId);
		if (user == null) {
			throw new AdempiereException("@AD_User_ID@ / @AD_Role_ID@ / @AD_Org_ID@ @NotFound@");
		}
		LoginResponse.Builder builder = LoginResponse.newBuilder();
		int roleId = SessionManager.getDefaultRoleId(userId);
		//	Get Values from role
		if(roleId < 0) {
			throw new AdempiereException("@AD_User_ID@ / @AD_Role_ID@ / @AD_Org_ID@ @NotFound@");
		}

		//	Organization
		int organizationId = SessionManager.getDefaultOrganizationId(roleId, userId);
		if(organizationId < 0) {
			throw new AdempiereException("@AD_User_ID@: @AD_Org_ID@ @NotFound@");
		}
		int warehouseId = 0;
		if (warehouseId <= 0 && organizationId > 0) {
			warehouseId = SessionManager.getDefaultWarehouseId(organizationId);
		}
		//	Session values
		final String bearerToken = SessionManager.createSessionAndGetToken(
			loginRequest.getDeviceInfo(),
			Language.AD_Language_en_US,
			roleId,
			userId,
			organizationId,
			warehouseId,
			false
		);
		LoginResponseData.Builder loginData = LoginResponseData.newBuilder()
			.setToken(bearerToken)
			.setName(user.getName())
			.setEmail(user.getEMail())
			.setPhone(
				TextManager.getValidString(
					user.getPhone()
				)
			)
			.setCompanyId(user.getAD_Client_ID())
			.setIsAdmin(user.isProjectManager())
			.setId(userId)
			//	TODO: Add real data
			.setIsFaceRegistered(false)
			.setAvatar("https://www.adempiere.io/assets/icon/logo.png")
		;
		//	Get employee data
		if(user.getC_BPartner_ID() > 0) {
			MHREmployee employee = MHREmployee.getActiveEmployee(Env.getCtx(), user.getC_BPartner_ID(), null);
			if(employee != null) {
				loginData.setIsHr(true);
				if(employee.getHR_Department_ID() > 0) {
					MHRDepartment department = MHRDepartment.getById(Env.getCtx(), employee.getHR_Department_ID(), null);
					if(department != null) {
						loginData.setDepartmentId(
								department.getHR_Department_ID()
							)
							.setDepartmentName(
								TextManager.getValidString(
									department.getName()
								)
							)
						;
					}
				}
			}
		}
		builder.setData(loginData)
			.setResult(true)
			.setMessage("Successfully Login")
		;
		//	Return session
		return builder.build();
	}

}
