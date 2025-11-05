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
import org.spin.mobile_service.service.adempiere.AdempiereService;
import org.spin.proto.mobile.adempiere.AdempieresServiceGrpc.AdempieresServiceImplBase;
import org.spin.proto.mobile.adempiere.ListOrganizationsRequest;
import org.spin.proto.mobile.adempiere.ListOrganizationsResponse;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class Adempiere extends AdempieresServiceImplBase {

	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(Adempiere.class);


	@Override
	public void listOrganizations(ListOrganizationsRequest request, StreamObserver<ListOrganizationsResponse> responseObserver) {
		try {
			ListOrganizationsResponse.Builder builderList = AdempiereService.listOrganizations(request);
			responseObserver.onNext(
				builderList.build()
			);
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

}
