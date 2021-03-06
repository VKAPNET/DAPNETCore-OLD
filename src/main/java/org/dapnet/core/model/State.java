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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dapnet.core.Settings;
import org.dapnet.core.rest.GsonTypeAdapterFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class State implements Serializable {

	private static final long serialVersionUID = 7604901183837032119L;
	private static final Logger logger = LogManager.getLogger();
	private static final Gson gson;

	static {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		builder.registerTypeAdapterFactory(new GsonTypeAdapterFactory());
		gson = builder.create();
	}

	@NotNull(message = "nicht vorhanden")
	@Valid
	private ConcurrentMap<String, CallSign> callSigns = new ConcurrentHashMap<>();

	@NotNull(message = "nicht vorhanden")
	@Valid
	private ConcurrentMap<String, Node> nodes = new ConcurrentHashMap<>();

	@NotNull(message = "nicht vorhanden")
	@Valid
	private ConcurrentMap<String, User> users = new ConcurrentHashMap<>();

	@NotNull(message = "nicht vorhanden")
	@Valid
	private Collection<Call> calls;

	@NotNull(message = "nicht vorhanden")
	@Valid
	private ConcurrentMap<String, Transmitter> transmitters = new ConcurrentHashMap<>();

	@NotNull(message = "nicht vorhanden")
	@Valid
	private ConcurrentMap<String, TransmitterGroup> transmitterGroups = new ConcurrentHashMap<>();

	@NotNull(message = "nicht vorhanden")
	@Valid
	private ConcurrentMap<String, Rubric> rubrics = new ConcurrentHashMap<>();

	@NotNull(message = "nicht vorhande")
	@Valid
	private ConcurrentMap<String, NewsList> news = new ConcurrentHashMap<>();

	@NotNull
	@Valid
	private CoreStatistics stats = new CoreStatistics();

	public State() {
		calls = Collections.synchronizedList(new ArrayList<>());

		setModelReferences();
	}

	public void setModelReferences() {
		// Setting reference to state in model for allow returning of reference
		// instead of strings
		Activation.setState(this);
		Call.setState(this);
		CallSign.setState(this);
		News.setState(this);
		Node.setState(this);
		Rubric.setState(this);
		Transmitter.setState(this);
		TransmitterGroup.setState(this);
	}

	public static State readFromFile() throws Exception {
		try (InputStreamReader reader = new InputStreamReader(
				new FileInputStream(Settings.getModelSettings().getStateFile()), "UTF-8")) {
			return gson.fromJson(reader, State.class);
		}
	}

	public void writeToFile() {
		File file = new File(Settings.getModelSettings().getStateFile());
		try {
			if (file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}

			try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8")) {
				writer.write(gson.toJson(this));
				writer.flush();
			}

			logger.info("Successfully wrote state to file");
		} catch (Exception e) {
			logger.fatal("Failed to write state file: ", e);
		}
	}

	public Collection<Call> getCalls() {
		return calls;
	}

	public ConcurrentMap<String, CallSign> getCallSigns() {
		return callSigns;
	}

	public ConcurrentMap<String, Node> getNodes() {
		return nodes;
	}

	public ConcurrentMap<String, User> getUsers() {
		return users;
	}

	public ConcurrentMap<String, Transmitter> getTransmitters() {
		return transmitters;
	}

	public ConcurrentMap<String, TransmitterGroup> getTransmitterGroups() {
		return transmitterGroups;
	}

	public ConcurrentMap<String, Rubric> getRubrics() {
		return rubrics;
	}

	public ConcurrentMap<String, NewsList> getNews() {
		return news;
	}

	public CoreStatistics getCoreStats() {
		return stats;
	}

}
