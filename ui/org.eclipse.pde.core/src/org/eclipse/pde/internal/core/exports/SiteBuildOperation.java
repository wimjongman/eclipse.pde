/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.exports;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import org.eclipse.pde.internal.build.IBuildPropertiesConstants;
import org.eclipse.pde.internal.build.IXMLConstants;
import org.eclipse.pde.internal.core.P2Utils;
import org.eclipse.pde.internal.core.PDECore;

/**
 * Performs a site build operation that will build any features needed by the site and generate
 * p2 metadata for those features.
 * 
 * @see FeatureBasedExportOperation
 * @see FeatureExportOperation
 */
public class SiteBuildOperation extends FeatureBasedExportOperation {

	public SiteBuildOperation(FeatureExportInfo info) {
		super(info);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.exports.FeatureBasedExportOperation#createPostProcessingFiles()
	 */
	protected void createPostProcessingFiles() {
		createPostProcessingFile(new File(fFeatureLocation, FEATURE_POST_PROCESSING));
		createPostProcessingFile(new File(fFeatureLocation, PLUGIN_POST_PROCESSING));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.exports.FeatureExportOperation#createAntBuildProperties(java.lang.String, java.lang.String, java.lang.String)
	 */
	protected HashMap createAntBuildProperties(String os, String ws, String arch) {
		HashMap map = super.createAntBuildProperties(os, ws, arch);
		// P2 Build Properties
		if (fInfo.toDirectory) {
			map.put(IXMLConstants.TARGET_P2_METADATA, IBuildPropertiesConstants.TRUE);
			map.put(IBuildPropertiesConstants.PROPERTY_P2_FLAVOR, P2Utils.P2_FLAVOR_DEFAULT);
			map.put(IBuildPropertiesConstants.PROPERTY_P2_PUBLISH_ARTIFACTS, IBuildPropertiesConstants.FALSE);
			try {
				map.put(IBuildPropertiesConstants.PROPERTY_P2_METADATA_REPO, new File(fInfo.destinationDirectory).toURL().toString());
				map.put(IBuildPropertiesConstants.PROPERTY_P2_ARTIFACT_REPO, new File(fInfo.destinationDirectory).toURL().toString());
			} catch (MalformedURLException e) {
				PDECore.log(e);
			}
		}
		return map;
	}

}
