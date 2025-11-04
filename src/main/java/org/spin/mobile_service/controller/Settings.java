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
package org.spin.mobile_service.controller;

import org.compiere.util.CLogger;
import org.spin.mobile_service.service.settings.SettingsService;
import org.spin.proto.mobile.settings.BaseSettings;
import org.spin.proto.mobile.settings.GetBaseSettingsRequest;
import org.spin.proto.mobile.settings.GetDashboardScreenRequest;
import org.spin.proto.mobile.settings.GetHomeScreenRequest;
import org.spin.proto.mobile.settings.GetSystemInfoRequest;
import org.spin.proto.mobile.settings.HomeScreen;
import org.spin.proto.mobile.settings.DashboardScreen;
import org.spin.proto.mobile.settings.SettingsServiceGrpc.SettingsServiceImplBase;
import org.spin.proto.mobile.settings.SystemInfo;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class Settings extends SettingsServiceImplBase {

	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(Settings.class);


	@Override
	public void getSystemInfo(GetSystemInfoRequest request, StreamObserver<SystemInfo> responseObserver) {
		try {
			SystemInfo.Builder builder = SettingsService.getSystemInfo();
			responseObserver.onNext(builder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.warning(e.getLocalizedMessage());
			e.printStackTrace();
			responseObserver.onError(
				Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException()
			);
		}
	}

	@Override
	public void getBaseSettings(GetBaseSettingsRequest request, StreamObserver<BaseSettings> responseObserver) {
		try {
			responseObserver.onNext(SettingsService.getBaseSettings().build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.warning(e.getLocalizedMessage());
			e.printStackTrace();
			responseObserver.onError(
				Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException()
			);
		}
	}
	
	@Override
	public void getDashboardScreen(GetDashboardScreenRequest request, StreamObserver<DashboardScreen> responseObserver) {
		try {
			responseObserver.onNext(SettingsService.getDashboardScreen(request));
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.warning(e.getLocalizedMessage());
			e.printStackTrace();
			responseObserver.onError(
				Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException()
			);
		}
	}

	@Override
	public void getHomeScreen(GetHomeScreenRequest request, StreamObserver<HomeScreen> responseObserver) {
		try {
			responseObserver.onNext(SettingsService.getHomeScreen(request));
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.warning(e.getLocalizedMessage());
			responseObserver.onError(
				Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException()
			);
		}
	}

}
