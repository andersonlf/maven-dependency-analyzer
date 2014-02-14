package com.andersonlfeitosa.mavendependencyanalyzer.dao;

import java.io.Closeable;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.andersonlfeitosa.mavendependencyanalyzer.log.Log;

public class PersistenceUtil implements Closeable {

	private static final PersistenceUtil instance = new PersistenceUtil();

	private static final Log logger = Log.getLogger();
	
	private EntityManagerFactory entityManagerFactory = null;
	
	private PersistenceUtil() {
		entityManagerFactory = Persistence.createEntityManagerFactory("dmpu");
		logger.info("starting entity manager factory for maven dependency analyzer");
	}

	public static PersistenceUtil getInstance() {
		return instance;
	}

	@Override
	public void close() throws IOException {
		entityManagerFactory.close();
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}
	
	public EntityManager getEntityManager() {
		return getEntityManagerFactory().createEntityManager();
	}

}
