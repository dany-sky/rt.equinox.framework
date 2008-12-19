/*
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.service.framework;

import java.util.Map;

import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;

/**
 * Framework service that is used to create composite bundles.
 * <p>
 * If present, there will only be a single instance of this service registered
 * with the Framework.
 * 
 * @ThreadSafe
 * @version $Revision: 1.1.2.2 $
 */
// TODO javadoc needs review
public interface CompositeBundleFactory {
	/**
	 * Manifest header (named &quot;CompositeServiceFilter-Import&quot;)
	 * identifying the service filters that are used by a child composite bundle
	 * to select services that will be registered into a child framework from a
	 * parent composite bundle.
	 */
	public static final String	COMPOSITE_SERVICE_FILTER_IMPORT	= "CompositeServiceFilter-Import";

	/**
	 * Manifest header (named &quot;CompositeServiceFilter-Export&quot;)
	 * identifying the service filters that are used by a parent composite
	 * bundle to select services that will be registered into a parent framework
	 * from a child composite bundle.
	 */
	public static final String	COMPOSITE_SERVICE_FILTER_EXPORT	= "CompositeServiceFilter-Export";

	/**
	 * Installs a <code>CompositeBundle</code>. The composite bundle has a new
	 * child <code>Framework</code> associated with it and a surrogate bundle
	 * which is installed in the child framework. Composite bundles share
	 * packages and services between parent framework they are installed in and
	 * the the child framework.
	 * <p>
	 * The following steps are required to create a composite bundle:
	 * <ol>
	 * <li>If a bundle containing the same location string is already installed,
	 * then if the Bundle object is a <code>CompositeBundle</code> then that
	 * composite bundle is returned; otherwise a BundleException is thrown
	 * indicating that an incompatible bundle is already installed at the
	 * specified location.</li>
	 * <li>The composite bundle's associated resources are allocated. The
	 * associated resources minimally consist of a unique identifier and a
	 * persistent storage area. If this step fails, a BundleException is thrown.
	 * <li>The compositeManifest map is used to provide the headers for the
	 * composite bundle and its surrogate bundle.
	 * <p>
	 * If composite manifest map does not contain the following header(s) then a
	 * BundleException is thrown:
	 * <ul>
	 * <li> {@link Constants#BUNDLE_SYMBOLICNAME Bundle-SymbolicName} the
	 * symbolic name used for the composite bundle and its surrogate bundle.
	 * </ul>
	 * </p>
	 * The composite manifest map may optionally contain the following
	 * header(s):
	 * <ul>
	 * <li> {@link Constants#BUNDLE_VERSION Bundle-Version} the bundle version
	 * used for the composite bundle and its surrogate bundle.</li>
	 * <li> {@link Constants#IMPORT_PACKAGE Import-Package} the packages which
	 * are imported from the parent framework by the composite bundle and are
	 * exported to the child framework by the surrogate bundle.</li>
	 * <li>{@link Constants#EXPORT_PACKAGE Export-Package} the packages which
	 * are imported from the child framework by the surrogate bundle and are
	 * exported to the parent framework by the composite bundle.</li>
	 * <li>{@link #COMPOSITE_SERVICE_FILTER_IMPORT
	 * CompositeServiceFilter-Import} the service filters which are acquired
	 * from the parent framework by the composite bundle and are registered in
	 * the child framework by the surrogate bundle.</li>
	 * <li>{@link #COMPOSITE_SERVICE_FILTER_EXPORT
	 * CompositeServiceFilter-Export} the service filters which are acquired
	 * from the child framework by the surrogate bundle and are registered in
	 * the parent framework by the composite bundle.</li>
	 * <li>{@link Constants#BUNDLE_MANIFESTVERSION Bundle-ManifestVersion} the
	 * bundle manifest version. If this header is not specified then the default
	 * is to use version 2. A BundleException is thrown if this header is
	 * specified and the version is less than 2.</li>
	 * </ul>
	 * The composite manifest map must not contain the following headers. If a
	 * composite manifest map does contain one of the following headers then a
	 * BundleException is thrown:
	 * <ul>
	 * <li> {@link Constants#BUNDLE_ACTIVATIONPOLICY Bundle-ActivationPolicy}</li>
	 * <li> {@link Constants#BUNDLE_ACTIVATOR Bundle-Activator}</li>
	 * <li> {@link Constants#BUNDLE_CLASSPATH Bundle-ClassPath}</li>
	 * <li> {@link Constants#BUNDLE_LOCALIZATION Bundle-Localization}</li>
	 * <li> {@link Constants#BUNDLE_NATIVECODE Bundle-NativeCode}</li>
	 * <li> {@link Constants#DYNAMICIMPORT_PACKAGE DynamicImport-Package}</li>
	 * <li> {@link Constants#FRAGMENT_HOST Fragment-Host}</li>
	 * <li> {@link Constants#REQUIRE_BUNDLE Require-Bundle}</li>
	 * </ul>
	 * <li>A child framework is created which uses a storage area under the
	 * composite bundle's associated persistent storage. Note that if the
	 * framework configuration property {@link Constants#FRAMEWORK_STORAGE
	 * org.osgi.framework.storage} is specified in the framework config then it
	 * is ignored.</li>
	 * <li>The child framework is initialized (see {@link Framework#init()}).
	 * <li>A surrogate bundle is installed into the child framework</li>
	 * <li>The composite bundle's state is set to INSTALLED.</li>
	 * <li>A bundle event of type {@link BundleEvent#INSTALLED} is fired for the
	 * composite bundle.
	 * <li>The <code>CompositeBundle</code> object for the newly composite
	 * bundle is returned
	 * </ol>
	 * <p>
	 * 
	 * @param frameworkConfig the child framework configuration.
	 * @param location the bundle location used for the child composite bundle
	 *        and its companion bundle.
	 * @param compositeManifest the manifest used to create the composite bundle
	 * @return A new child composite bundle.
	 * @throws BundleException If the composite manifest is invalid or there is
	 *         some other problem with installing the composite bundle.
	 * @throws SecurityException If the caller does not have
	 *         <code>AllPermission</code>.
	 * @see Framework
	 * @see CompositeBundle
	 */
	CompositeBundle installCompositeBundle(
			Map /* <String, String> */frameworkConfig, String location,
			Map /* <String, String> */compositeManifest) throws BundleException;

}
