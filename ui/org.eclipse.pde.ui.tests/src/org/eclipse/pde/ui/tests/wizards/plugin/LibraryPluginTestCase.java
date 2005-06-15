/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.ui.tests.wizards.plugin;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.pde.core.build.*;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.*;
import org.eclipse.pde.internal.core.*;
import org.eclipse.pde.internal.core.build.*;
import org.eclipse.pde.ui.tests.*;

import junit.framework.*;

public class LibraryPluginTestCase extends NewProjectTest {

	private static final String PROJECT_NAME = "com.example.xyz";

	public static Test suite() {
		return new TestSuite(LibraryPluginTestCase.class);
	}
	
	public void testLibrariesFromWorkspacePlugin() {
		try {
			playScript(Catalog.LIBRARY_PLUGIN_1);
			verifyProject(false, "pdeuiant.jar", "bin");
			verifyPluginModel(null, "pdeuiant.jar");
			verifyBuildProperties(false, false, "pdeuiant.jar");
		} catch (CoreException e) {
			fail("testLibrariesFromWorkspacePlugin:" + e);
		}
	}
	
	public void testLibrariesFromWorkspacePluginWithManifest() {
		try {
			playScript(Catalog.LIBRARY_PLUGIN_2);
			verifyProject(true, "pdeuiant.jar", "bin");
			verifyPluginModel(null, "pdeuiant.jar");
			verifyBuildProperties(true, false, "pdeuiant.jar");
		} catch (CoreException e) {
			fail("testLibrariesFromWorkspacePluginWithManifest:" + e);
		}
	}
	
	public void testUnzipFromWorkspacePlugin() {
		try {
			playScript(Catalog.LIBRARY_PLUGIN_3);
			verifyProject(false, ".", "bin");
			verifyPluginModel(null, ".");
			verifyBuildProperties(false, true, ".");
		} catch (CoreException e) {
			fail("testLibrariesFromWorkspacePlugin:" + e);
		}
	}
	
	public void testUnzipFromWorkspacePluginWithManifest() {
		try {
			playScript(Catalog.LIBRARY_PLUGIN_4);
			verifyProject(true, ".", "bin");
			verifyPluginModel(null, ".");
			verifyBuildProperties(true, true, ".");
		} catch (CoreException e) {
			fail("testLibrariesFromWorkspacePluginWithManifest:" + e);
		}
	}
	
	public void verifyType(boolean ui, String className) throws CoreException {
		if (className != null) {
			IJavaProject jProject = JavaCore.create(getProject());
			IType type = jProject.findType(className);
			assertNotNull(type);
			assertTrue(type.isClass());
			assertFalse(type.isBinary());
			if (ui)
				assertEquals("AbstractUIPlugin", type.getSuperclassName());
			else
				assertEquals("Plugin", type.getSuperclassName());
		}

	}

	protected String getProjectName() {
		return PROJECT_NAME;
	}
	
	private void verifyProject(boolean isBundle, String libName, String outputFolder) throws CoreException {
		verifyProjectExistence();
		verifyNatures();
		verifyManifestFiles(isBundle);
		verifyClasspath(libName, outputFolder);
	}
	
	private void verifyNatures() {
		assertTrue("Project does not have a PDE nature.", hasNature(PDE.PLUGIN_NATURE));
		assertTrue("Simple Project has a Java nature.", hasNature(JavaCore.NATURE_ID));
	}
	
	private void verifyManifestFiles(boolean isBundle) {
		if (isBundle) {
			assertTrue(getProject().getFile("META-INF/MANIFEST.MF").exists());
			assertFalse(getProject().getFile("plugin.xml").exists());
		} else {
			assertTrue(getProject().getFile("plugin.xml").exists());
			assertFalse(getProject().getFile("META-INF/MANIFEST.MF").exists());
		}
	}
	
	private void verifyClasspath(String libName, String outputFolder) throws CoreException {
		IJavaProject jProject = JavaCore.create(getProject());
		IPath expected = new Path(getProjectName()).append(outputFolder).makeAbsolute();
		assertEquals(expected, jProject.getOutputLocation());
		
		IClasspathEntry[] entries = jProject.getRawClasspath();
		assertEquals(3, entries.length);
		
		// verify library
		IClasspathEntry entry = entries[0];
		assertEquals(IClasspathEntry.CPE_LIBRARY, entry.getEntryKind());
		assertEquals(new Path(getProjectName()).append(libName).makeAbsolute(), entry.getPath());
		
		// verify PDE container 
		entry = entries[1];
		assertEquals(IClasspathEntry.CPE_CONTAINER, entry.getEntryKind());
		assertEquals(new Path(PDECore.CLASSPATH_CONTAINER_ID), entry.getPath());
		
		// verify JRE container
		entry = entries[2];
		assertEquals(IClasspathEntry.CPE_CONTAINER, entry.getEntryKind());
		assertEquals(new Path(JavaRuntime.JRE_CONTAINER), entry.getPath());
		
		// verify no errors
		assertEquals(0, getProject().findMarkers(null, true, IResource.DEPTH_INFINITE).length);

	}
	
	private void verifyPluginModel(String className, String libraryName) {
		IPluginModelBase model = PDECore.getDefault().getModelManager().findModel(getProject());
		assertTrue("Model is not found.", model != null);
		IPlugin plugin = (IPlugin)model.getPluginBase();
		assertEquals("com.example.xyz", plugin.getId());
		assertEquals("1.0.1", plugin.getVersion());
		assertEquals("Eclipse.org", plugin.getProviderName());
		assertEquals("Xyz Plug-in", plugin.getName());
		if (className == null)
			assertNull(plugin.getClassName());
		else
			assertEquals(className, plugin.getClassName());
		assertEquals(1, plugin.getLibraries().length);
		assertEquals(libraryName, plugin.getLibraries()[0].getName());
		assertEquals(0, plugin.getExtensionPoints().length);
		assertEquals(0, plugin.getExtensions().length);
	}

	private void verifyBuildProperties(boolean isBundle, boolean isUnzip, String libraryName) {
		IFile buildFile = getProject().getFile("build.properties"); //$NON-NLS-1$
		assertTrue("Build.properties does not exist.", buildFile.exists());
		
		IBuildModel model =  new WorkspaceBuildModel(buildFile);
		try {
			model.load();
		} catch (CoreException e) {
			fail("Model cannot be loaded:" + e);
		}
		
		IBuild build = model.getBuild();
		assertEquals(isUnzip ? 3 : 1, build.getBuildEntries().length);
		
		// verify bin.includes
		IBuildEntry entry = build.getEntry("bin.includes");
		assertNotNull(entry);		
		String[] tokens = entry.getTokens();
		assertEquals(2, tokens.length);
		assertEquals(isBundle ? "META-INF/" : "plugin.xml", tokens[0]);
		if (libraryName.equals("."))
			assertEquals("org/", tokens[1]);
		else
			assertEquals(libraryName, tokens[1]);
		
		if (isUnzip) {
			entry = build.getEntry("source..");
			assertNotNull(entry);
			tokens = entry.getTokens();
			assertEquals(1, tokens.length);
			assertEquals(".", tokens[0]);

			entry = build.getEntry("output..");
			assertNotNull(entry);
			tokens = entry.getTokens();
			assertEquals(1, tokens.length);
			assertEquals(".", tokens[0]);
			
		}
		
	}

}
