/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.container.tests.dummys;

import java.util.*;
import org.eclipse.osgi.container.*;
import org.eclipse.osgi.container.Module.Event;
import org.eclipse.osgi.container.Module.Settings;
import org.eclipse.osgi.container.Module.State;
import org.eclipse.osgi.container.ModuleContainerAdaptor.ContainerEvent;
import org.osgi.framework.FrameworkListener;

public class DummyModuleDataBase extends ModuleDataBase {

	private Map<String, Collection<ModuleCapability>> namespaces = new HashMap<String, Collection<ModuleCapability>>();
	private List<DummyModuleEvent> moduleEvents = new ArrayList<DummyModuleEvent>();
	private List<DummyContainerEvent> containerEvents = new ArrayList<DummyContainerEvent>();

	@Override
	protected void addCapabilities(ModuleRevision revision) {
		List<ModuleCapability> capabilities = revision.getModuleCapabilities(null);
		for (ModuleCapability capability : capabilities) {
			Collection<ModuleCapability> namespace = namespaces.get(capability.getNamespace());
			if (namespace == null) {
				namespace = new ArrayList<ModuleCapability>();
				namespaces.put(capability.getNamespace(), namespace);
			}
			namespace.add(capability);
		}
	}

	@Override
	protected void removeCapabilities(ModuleRevision revision) {
		List<ModuleCapability> capabilities = revision.getModuleCapabilities(null);
		for (ModuleCapability capability : capabilities) {
			Collection<ModuleCapability> namespace = namespaces.get(capability.getNamespace());
			if (namespace != null)
				namespace.remove(capability);
		}
	}

	@Override
	protected List<ModuleCapability> findCapabilities(ModuleRequirement requirement) {
		Collection<ModuleCapability> namespace = namespaces.get(requirement.getNamespace());
		List<ModuleCapability> candidates = new ArrayList<ModuleCapability>();
		if (namespace == null)
			return candidates;
		for (ModuleCapability candidate : namespace) {
			if (requirement.matches(candidate))
				candidates.add(candidate);
		}
		return candidates;
	}

	@Override
	protected Module createModule(String location, long id, EnumSet<Settings> settings, int startlevel) {
		return new DummyModule(id, location, this.container, this, settings, startlevel);
	}

	@Override
	protected SystemModule createSystemModule() {
		return new DummySystemModule(this.container, this);
	}

	void addEvent(DummyModuleEvent event) {
		synchronized (moduleEvents) {
			moduleEvents.add(event);
			moduleEvents.notifyAll();
		}
	}

	void addEvent(DummyContainerEvent event) {
		synchronized (containerEvents) {
			containerEvents.add(event);
			containerEvents.notifyAll();
		}
	}

	public List<DummyModuleEvent> getModuleEvents() {
		return getEvents(moduleEvents);
	}

	public List<DummyContainerEvent> getContainerEvents() {
		return getEvents(containerEvents);
	}

	private static <E> List<E> getEvents(List<E> events) {
		synchronized (events) {
			List<E> result = new ArrayList<E>(events);
			events.clear();
			return result;
		}
	}

	public List<DummyModuleEvent> getModuleEvents(int expectedNum) {
		return getEvents(expectedNum, moduleEvents);
	}

	public List<DummyContainerEvent> getContainerEvents(int expectedNum) {
		return getEvents(expectedNum, containerEvents);
	}

	private static <E> List<E> getEvents(int expectedNum, List<E> events) {
		synchronized (events) {
			long timeout = 5000;
			while (events.size() < expectedNum && timeout > 0) {
				long startTime = System.currentTimeMillis();
				try {
					events.wait(timeout);
				} catch (InterruptedException e) {
					// continue to wait
				}
				timeout = timeout - (System.currentTimeMillis() - startTime);
			}
			List<E> result = new ArrayList<E>(events);
			events.clear();
			return result;
		}
	}

	public static class DummyModuleEvent {
		public final Module module;
		public final Event event;
		public final State state;

		public DummyModuleEvent(Module module, Event event, State state) {
			this.module = module;
			this.event = event;
			this.state = state;
		}

		public boolean equals(Object o) {
			if (!(o instanceof DummyModuleEvent))
				return false;
			DummyModuleEvent that = (DummyModuleEvent) o;
			return this.event.equals(that.event) && this.module.equals(that.module) && this.state.equals(that.state);
		}

		public String toString() {
			return module + ": " + event + ": " + state;
		}
	}

	public static class DummyContainerEvent {
		public final ContainerEvent type;
		public final Module module;
		public final Throwable error;
		public final FrameworkListener[] listeners;

		public DummyContainerEvent(ContainerEvent type, Module module,
				Throwable error, FrameworkListener... listeners) {
			this.type = type;
			this.module = module;
			this.error = error;
			this.listeners = listeners;
		}

		public boolean equals(Object o) {
			if (!(o instanceof DummyContainerEvent))
				return false;
			DummyContainerEvent that = (DummyContainerEvent) o;
			return this.type.equals(that.type) && this.module.equals(that.module);
		}

		public String toString() {
			return module + ": " + type;
		}
	}
}
