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
package org.spin.mobile.controller;

import org.compiere.util.CLogger;
import org.spin.proto.mobile.appoinment.AppoinmentServiceGrpc.AppoinmentServiceImplBase;
import org.spin.mobile.service.appoinment.AppoinmentService;
import org.spin.proto.mobile.appoinment.GetListRequest;
import org.spin.proto.mobile.appoinment.GetListResponse;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class Appoinment extends AppoinmentServiceImplBase {
	
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(Appoinment.class);
	
	@Override
	public void getList(GetListRequest request, StreamObserver<GetListResponse> responseObserver) {
		try {
			responseObserver.onNext(AppoinmentService.getList(request));
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
}
