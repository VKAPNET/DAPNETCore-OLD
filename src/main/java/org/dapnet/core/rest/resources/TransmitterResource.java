/*
 * DAPNET CORE PROJECT
 * Copyright (C) 2016
 *
 * Daniel Sialkowski
 *
 * daniel.sialkowski@rwth-aachen.de
 *
 * Institute of High Frequency Technology
 * RWTH AACHEN UNIVERSITY
 * Melatener Str. 25
 * 52074 Aachen
 */

package org.dapnet.core.rest.resources;

import org.dapnet.core.model.Transmitter;
import org.dapnet.core.rest.RestSecurity;
import org.dapnet.core.rest.exceptionHandling.EmptyBodyException;

import java.security.NoSuchAlgorithmException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/transmitters")
@Produces("application/json")
public class TransmitterResource extends AbstractResource {
	private final AuthKeyGenerator keyGenerator;

	public TransmitterResource() {
		try {
			keyGenerator = new AuthKeyGenerator();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	@GET
	public Response getTransmitters() throws Exception {
		RestSecurity.SecurityStatus status = checkAuthorization(RestSecurity.SecurityLevel.USER_ONLY);
		return getObject(restListener.getState().getTransmitters(), status);
	}

	@GET
	@Path("{transmitter}")
	public Response getTransmitter(@PathParam("transmitter") String transmitterName) throws Exception {
		RestSecurity.SecurityStatus status = checkAuthorization(RestSecurity.SecurityLevel.USER_ONLY);
		return getObject(restListener.getState().getTransmitters().get(transmitterName), status);
	}

	@PUT
	@Path("{transmitter}")
	@Consumes("application/json")
	public Response putTransmitter(@PathParam("transmitter") String transmitterName, String transmitterJSON)
			throws Exception {
		if (restListener.getState().getTransmitters().containsKey(transmitterName)) {
			// Overwrite
			checkAuthorization(RestSecurity.SecurityLevel.OWNER_ONLY,
					restListener.getState().getTransmitters().get(transmitterName));
		} else {
			// Create
			checkAuthorization(RestSecurity.SecurityLevel.USER_ONLY);
		}

		// Create Transmitter
		Transmitter transmitter = gson.fromJson(transmitterJSON, Transmitter.class);
		if (transmitter != null) {
			// Only Status OFFLINE or DISABLED is accepted:
			if (transmitter.getStatus() == null || transmitter.getStatus() != Transmitter.Status.DISABLED)
				transmitter.setStatus(Transmitter.Status.OFFLINE);
			transmitter.setName(transmitterName);
		} else {
			throw new EmptyBodyException();
		}

		return handleObject(transmitter, "putTransmitter",
				!restListener.getState().getTransmitters().containsKey(transmitterName), true);
	}

	@DELETE
	@Path("{transmitter}")
	public Response deleteTransmitter(@PathParam("transmitter") String transmitter) throws Exception {
		Transmitter oldTransmitter = restListener.getState().getTransmitters().get(transmitter);
		if (oldTransmitter != null) {
			checkAuthorization(RestSecurity.SecurityLevel.OWNER_ONLY);
		} else {
			checkAuthorization(RestSecurity.SecurityLevel.USER_ONLY);
		}

		return deleteObject(oldTransmitter, "deleteTransmitter", true);
	}

	@GET
	@Path("/auth_key")
	public Response getAuthKey() throws Exception {
		RestSecurity.SecurityStatus status = checkAuthorization(RestSecurity.SecurityLevel.ADMIN_ONLY);
		String key = keyGenerator.generateKey();

		return Response.status(Response.Status.OK).entity(getExclusionGson(status).toJson(key)).build();
	}
}