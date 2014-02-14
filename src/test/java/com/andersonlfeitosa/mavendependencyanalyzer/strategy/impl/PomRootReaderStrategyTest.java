package com.andersonlfeitosa.mavendependencyanalyzer.strategy.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.andersonlfeitosa.mavendependencyanalyzer.strategy.IPomReader;
import com.andersonlfeitosa.mavendependencyanalyzer.xml.object.Project;

public class PomRootReaderStrategyTest {
	
	private IPomReader reader;

	@Before
	public void init() {
		reader = new PomRootReaderStrategy();
	}
	
	@Test
	public void testReadApp1() {
		File file = createFile("poms/app1/pom.xml");
		Map<String, Project> poms = reader.read(file); 
		assertSame(4, poms.values().size());
		
		Project project = poms.get("com.app1:app1:1.0.0.0-SNAPSHOT");
		assertEquals(project.getModules().get(0), "mod1");
	}

	private File createFile(String path) {
		File file = null;
		
		try {
			file = new File(getClass().getClassLoader().getResource(path).toURI());
		} catch (URISyntaxException e) {
			fail("error");
		}
		
		return file;
	}

}
