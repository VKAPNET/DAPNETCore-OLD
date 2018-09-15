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

package org.dapnet.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.dapnet.core.model.validator.ValidName;
import org.dapnet.core.rest.RestAuthorizable;

public class TransmitterGroup implements Serializable, RestAuthorizable, Searchable {
	private static final long serialVersionUID = 2641698714366327412L;
	private static volatile State state;

	// ID
	@NotNull
	@Size(min = 3, max = 20)
	private String name;

	@NotNull
	@Size(min = 0, max = 60)
	private String description;

	@NotNull
	@Size(min = 1, message = "must contain at least one transmitterName")
	private Collection<String> transmitterNames;

	@NotNull
	@Size(min = 1, message = "must contain at least one ownerName")
	private Collection<String> ownerNames;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<String> getTransmitterNames() {
		return transmitterNames;
	}

	public void setTransmitterNames(Collection<String> transmitterNames) {
		this.transmitterNames = transmitterNames;
	}

	@Override
	public Collection<String> getOwnerNames() {
		return ownerNames;
	}

	public void setOwnerNames(Collection<String> owners) {
		this.ownerNames = owners;
	}

	public static void setState(State statePar) {
		state = statePar;
	}

	@ValidName(message = "must contain names of existing users", fieldName = "ownerNames", constraintName = "ValidOwnerNames")
	public Collection<User> getOwners() throws Exception {
		if (state == null) {
			throw new Exception("StateNotSetException");
		}

		if (ownerNames == null) {
			return null;
		}

		ConcurrentMap<String, User> users = state.getUsers();
		ArrayList<User> result = new ArrayList<>();
		for (String owner : ownerNames) {
			User u = users.get(owner.toLowerCase());
			if (u != null)
				result.add(u);
		}

		if (ownerNames.size() == result.size()) {
			return result;
		} else {
			return null;
		}
	}

	@ValidName(message = "must contain names of existing transmitters", fieldName = "transmitterNames", constraintName = "ValidTransmitterNames")
	public Collection<Transmitter> getTransmitters() throws Exception {
		if (state == null) {
			throw new Exception("StateNotSetException");
		}

		if (transmitterNames == null) {
			return null;
		}

		ConcurrentMap<String, Transmitter> transmitters = state.getTransmitters();
		ArrayList<Transmitter> result = new ArrayList<>();
		for (String transmitterName : transmitterNames) {
			Transmitter t = transmitters.get(transmitterName.toLowerCase());
			if (t != null) {
				result.add(t);
			}
		}

		if (result.size() == transmitterNames.size()) {
			return result;
		} else {
			return null;
		}
	}

	public boolean contains(String transmitter) {
		for (String transmitterName : transmitterNames) {
			if (transmitter.equalsIgnoreCase(transmitterName)) {
				return true;
			}
		}

		return false;
	}

	public boolean contains(Transmitter transmitter) {
		return contains(transmitter.getName());
	}

	@Override
	public String toString() {
		return String.format("TransmitterGroup{name='%s'}", name);
	}
}
