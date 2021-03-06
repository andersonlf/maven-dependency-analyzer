package com.andersonlfeitosa.mavendependencyanalyzer.manager;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.andersonlfeitosa.mavendependencyanalyzer.entity.ArtifactEntity;
import com.andersonlfeitosa.mavendependencyanalyzer.entity.DependencyEntity;
import com.andersonlfeitosa.mavendependencyanalyzer.entity.Packaging;
import com.andersonlfeitosa.mavendependencyanalyzer.entity.Scope;
import com.andersonlfeitosa.mavendependencyanalyzer.entity.Type;
import com.andersonlfeitosa.mavendependencyanalyzer.log.Log;
import com.andersonlfeitosa.mavendependencyanalyzer.report.HTMLReporter;
import com.andersonlfeitosa.mavendependencyanalyzer.report.IReport;
import com.andersonlfeitosa.mavendependencyanalyzer.strategy.IPomReader;
import com.andersonlfeitosa.mavendependencyanalyzer.strategy.impl.DirectoryPomReaderStrategy;
import com.andersonlfeitosa.mavendependencyanalyzer.strategy.impl.PomRootReaderStrategy;
import com.andersonlfeitosa.mavendependencyanalyzer.xml.object.Dependency;
import com.andersonlfeitosa.mavendependencyanalyzer.xml.object.Project;

public class MavenDependencyAnalyzer {

	private static final MavenDependencyAnalyzer instance = new MavenDependencyAnalyzer();

	private static final Log logger = Log.getLogger();
	
	private EntityManagerFactory entityManagerFactory = null;
	
	private EntityManager entityManager = null;

	private MavenDependencyAnalyzer() {
	}

	public static MavenDependencyAnalyzer getInstance() {
		return instance;
	}

	public void execute(String fileOrDirectory) {
		initResources();
		
		File file = new File(fileOrDirectory);
		IPomReader reader = createPomReader(file.isDirectory());
		Map<String, Project> poms = reader.read(file);
		persistObjects(poms);
		plotGraph();
		
		closeResources();
	}

	private void initResources() {
		entityManagerFactory = Persistence.createEntityManagerFactory("dmpu");
		entityManager = entityManagerFactory.createEntityManager();
	}

	private void closeResources() {
		entityManager.close();
		entityManagerFactory.close();
	}

	private void plotGraph() {
		List<ArtifactEntity> artifacts = findAllArtifacts();
		IReport reporter = new HTMLReporter();
		reporter.print(artifacts);
	}

	private IPomReader createPomReader(boolean directory) {
		return directory ? new DirectoryPomReaderStrategy()
				: new PomRootReaderStrategy();
	}
	
	private List<ArtifactEntity> findAllArtifacts() {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		Query query = entityManager.createQuery("from com.andersonlfeitosa.mavendependencyanalyzer.entity.ArtifactEntity");
		List<ArtifactEntity> artifacts = query.getResultList();
		
		transaction.commit();
		return artifacts;
	}
	

	private void persistObjects(Map<String, Project> poms) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		for (Project project : poms.values()) {
			ArtifactEntity artifact = findArtifactOrCreate(entityManager,new Dependency(project.getGroupId(), project.getArtifactId(), project.getVersion()));
			artifact.setPackaging(Packaging.getPackaging(project.getPackaging()));

			logger.debug(artifact);
			entityManager.persist(artifact);

			if (project.getDependencies() != null) {
				for (Dependency dep : project.getDependencies()) {
					DependencyEntity dependency = new DependencyEntity();
					dependency.setArtifact(artifact);
					dependency.setClassifier(dep.getClassifier());
					dependency.setDependency(findArtifactOrCreate(entityManager, dep));
					dependency.setScope(Scope.getScope(dep.getScope()));
					dependency.setType(Type.getType(dep.getType()));
					artifact.getDependencies().add(dependency);

					logger.debug(dependency);
					entityManager.persist(dependency);
				}
			}

			logger.debug(artifact);
			entityManager.persist(artifact);
		}

		transaction.commit();
	}

	private ArtifactEntity findArtifactOrCreate(EntityManager entityManager,
			Dependency dependency) {
		ArtifactEntity result = null;

		Query query = entityManager
				.createQuery("from com.andersonlfeitosa.mavendependencyanalyzer.entity.ArtifactEntity a "
						+ "where a.groupId = :groupId "
						+ "and a.artifactId = :artifactId "
						+ "and a.version = :version");

		query.setParameter("groupId", dependency.getGroupId());
		query.setParameter("artifactId", dependency.getArtifactId());
		query.setParameter("version", dependency.getVersion());

		@SuppressWarnings("unchecked")
		List<ArtifactEntity> artifacts = query.getResultList();

		if (artifacts != null && !artifacts.isEmpty()) {
			result = artifacts.get(0);
		}

		if (result == null) {
			result = new ArtifactEntity();
			result.setArtifactId(dependency.getArtifactId());
			result.setGroupId(dependency.getGroupId());
			result.setVersion(dependency.getVersion());
		}

		return result;
	}

}
