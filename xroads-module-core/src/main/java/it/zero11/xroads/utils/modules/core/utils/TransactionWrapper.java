package it.zero11.xroads.utils.modules.core.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import it.zero11.xroads.utils.EntityManagerUtils;

public class TransactionWrapper implements AutoCloseable {
	final EntityManager em;
	final EntityTransaction tx;
	
	public TransactionWrapper(){
		em = EntityManagerUtils.createEntityManager();
		tx = em.getTransaction();
		tx.begin();
	}
	
	public EntityTransaction getTx() {
		return tx;
	}

	public EntityManager getEm() {
		return em;
	}

	public void commit(){
		tx.commit();
	}

	@Override
	public void close() {
		try{
			if (tx.isActive()) {
	            tx.rollback();
	        }
		}finally {
			em.close();
		}
	}
}