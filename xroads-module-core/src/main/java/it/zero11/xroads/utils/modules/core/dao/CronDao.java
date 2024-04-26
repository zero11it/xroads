package it.zero11.xroads.utils.modules.core.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import it.zero11.xroads.model.Cron;
import it.zero11.xroads.utils.modules.core.utils.TransactionWrapper;

public class CronDao {
	private static CronDao instance = null;

	public static CronDao getInstance() {
		if (instance == null) {
			synchronized (CronDao.class){
				if (instance == null){
					instance = new CronDao();
				}
			}
		}
		return instance;
	}

	private CronDao(){
	}

	public int cleanRunningFreezedCron(String nodeName, long cleanRunningFreezed) {
		try (TransactionWrapper etw = new TransactionWrapper()){
			TypedQuery<Cron> query = etw.getEm().createQuery("FROM Cron cron WHERE cron.status in (:status) and cron.node = :node and executionTime < :dateFailed", Cron.class);
			query.setParameter("status", Arrays.asList(Cron.EXECUTING));
			query.setParameter("node", nodeName);
			query.setParameter("dateFailed", new Date(new Date().getTime() - cleanRunningFreezed));
			List<Cron> result = query.getResultList();
			for (Cron cron:result){
				cron.setStatus(Cron.FAILED);
				cron.setError("Crashed - freezed");
			}

			etw.commit();
			
			return result.size();
		}
	}
	
	public synchronized void cleanSchedule(long cleanDelaySuccess, long cleanDelayFailed) {
		Date now = new Date();

		try (TransactionWrapper etw = new TransactionWrapper()){
			Query query = etw.getEm().createQuery("delete FROM Cron where (status = :statusSuccess and scheduledTime < :dateSuccess) or ((status = :statusFailed or status = :statusPending) and scheduledTime < :dateFailed)");
			query.setParameter("statusSuccess", Cron.SUCCESS);
			query.setParameter("dateSuccess", new Date(now.getTime() - cleanDelaySuccess));
			query.setParameter("statusFailed", Cron.FAILED);
			query.setParameter("statusPending", Cron.PENDING);
			query.setParameter("dateFailed", new Date(now.getTime() - cleanDelayFailed));
			query.executeUpdate();

			etw.commit();
		}
	}
	
	public synchronized boolean isPendingExecution(String name) {
		try (TransactionWrapper etw = new TransactionWrapper()){
			TypedQuery<Cron> query = etw.getEm().createQuery("FROM Cron cron WHERE cron.status in (:status) and scheduledTime < :dateNow and name = :name", Cron.class);
			query.setParameter("status", Arrays.asList(Cron.PENDING));
			query.setParameter("dateNow", new Date());
			query.setParameter("name", name);
			List<Cron> result = query.getResultList();
			
			etw.commit();
			
			return result.size() > 0;
		}
	}

	public void addScheduleNowIfNotScheduled(String name, String xRoadsModule) {
		if (!CronDao.getInstance().isPendingExecution(name)){
			CronDao.getInstance().addSchedule(name, xRoadsModule, null, new Date(), false);
		}
	}
	
	public synchronized void addSchedule(String name, String xRoadsModule, String node, Date scheduledTime, boolean force) {
		try (TransactionWrapper etw = new TransactionWrapper()){
			Cron cron = new Cron();
			cron.setName(name);
			cron.setxRoadsModule(xRoadsModule);
			cron.setStatus(Cron.PENDING);
			cron.setNode(node);
			cron.setScheduledTime(scheduledTime);
			cron.setForceExecution(Boolean.valueOf(force));
			
			etw.getEm().persist(cron);
			etw.commit();
		}
	}

	public Cron getLastScheduled() {
		return getLastScheduled(null);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized Cron getLastScheduled(String node) {
		List<Cron> result;
		try (TransactionWrapper etw = new TransactionWrapper()){
			if (node == null){
				Query query = etw.getEm().createQuery("FROM Cron order by scheduledTime desc");
				query.setMaxResults(1);
				result = query.getResultList();
			}else{
				Query query = etw.getEm().createQuery("FROM Cron where node = :node order by scheduledTime desc");
				query.setParameter("node", node);
				query.setMaxResults(1);
				result = query.getResultList();
			}

			etw.commit();
		}
		
		if (result.isEmpty()){
			return null;
		}else{
			return result.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public synchronized List<String> getCurrentNodeList() {
		List<String> result;
		try (TransactionWrapper etw = new TransactionWrapper()){
			Query query = etw.getEm().createQuery("select distinct cron.node FROM Cron cron WHERE cron.status in (:status)");
			query.setParameter("status", Arrays.asList(Cron.SUCCESS, Cron.FAILED, Cron.EXECUTING));
			result = query.getResultList();

			etw.commit();
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized List<Cron> getErrors(String name, Integer limit) {
		List<Cron> result;
		try (TransactionWrapper etw = new TransactionWrapper()){
			Query query = etw.getEm().createQuery("FROM Cron cron WHERE cron.name = :name and cron.error is not null")
					.setMaxResults(limit);
			query.setParameter("name", name);
			result = query.getResultList();
			etw.commit();
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public synchronized Cron deQueue(String node, boolean onlyNodeCron) {
		Cron cron = null;
		try (TransactionWrapper etw = new TransactionWrapper()){
			Query query = etw.getEm().createNativeQuery("select * from cron cron1 where cron1.status = ? and cron1.scheduled_time < timezone('UTC', now()) and "
					+ (onlyNodeCron ? " cron1.node = ? " :  " (cron1.node is null or cron1.node = ?)")
					+ " and not exists ( select * from cron cron2 where cron2.status = ? and cron2.scheduled_time > cron1.scheduled_time - interval '6 hours' and cron2.name = cron1.name) "
					+ " order by scheduled_time asc limit 1 for update", Cron.class);
			query.setParameter(1, Cron.PENDING);
			query.setParameter(2, node);
			query.setParameter(3, Cron.EXECUTING);
			List<Cron> result = query.getResultList();
			if (!result.isEmpty()){
				cron = result.get(0);
				cron.setStatus(Cron.EXECUTING);
				cron.setExecutionTime(new Date());
				cron.setNode(node);
			}
			
			etw.commit();
		}
		
		return cron;
	}

	public void failed(Cron cron, String error) {
		try (TransactionWrapper etw = new TransactionWrapper()){
			cron = etw.getEm().find(Cron.class, cron.getId(), LockModeType.PESSIMISTIC_WRITE);
			if (cron.getStatus().intValue() != Cron.EXECUTING){
				System.err.println(Thread.currentThread().getName() +" Cron "+ cron.getId() + " NOT IN EXECUTING STATUS");
				return;
			}
			
			cron.setStatus(Cron.FAILED);
			cron.setCompletedTime(new Date());
			cron.setError(error);
			
			etw.commit();
		}
	}

	public void successful(Cron cron) {
		try (TransactionWrapper etw = new TransactionWrapper()){
			cron = etw.getEm().find(Cron.class, cron.getId(), LockModeType.PESSIMISTIC_WRITE);
			if (cron.getStatus().intValue() != Cron.EXECUTING){
				System.err.println(Thread.currentThread().getName() +" Cron "+ cron.getId() + " NOT IN EXECUTING STATUS");
				return;
			}
			
			cron.setStatus(Cron.SUCCESS);
			cron.setCompletedTime(new Date());
			cron.setError(null);
			
			etw.commit();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Cron> getAllCron() {
		List<Cron> result;
		try (TransactionWrapper etw = new TransactionWrapper()){
			Query query = etw.getEm().createQuery("FROM Cron order by scheduledTime desc");
			result = query.getResultList();

			etw.commit();
		}
		
		return result;
	}
}
