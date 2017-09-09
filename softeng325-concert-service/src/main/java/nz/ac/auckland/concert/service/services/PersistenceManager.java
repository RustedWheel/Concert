package nz.ac.auckland.concert.service.services;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Singleton class that manages an EntityManagerFactory. When a
 * PersistenceManager is instantiated, it creates an EntityManagerFactory. An
 * EntityManagerFactory is required to create an EntityManager, which represents
 * a persistence context (session with a database). 
 * 
 * When a Web service application component (e.g. a resource object) requires a 
 * persistence context, it should call the PersistentManager's 
 * createEntityManager() method to acquire one.
 * 
 */
public class PersistenceManager {
	private static PersistenceManager _instance = null;
	
	private EntityManagerFactory _entityManagerFactory;
	
	protected PersistenceManager() {
		_entityManagerFactory = Persistence.createEntityManagerFactory("nz.ac.auckland.concert");
	}
	
	public EntityManager createEntityManager() {
		return _entityManagerFactory.createEntityManager();
	}
	
	public static PersistenceManager instance() {
		if(_instance == null) {
			_instance = new PersistenceManager();
		}
		return _instance;
	}

}
