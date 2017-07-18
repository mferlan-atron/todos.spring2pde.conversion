/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * ATRON electronic GmbH ("ATRON") CONFIDENTIAL                               *
 * Unpublished Copyright (c) 2008-2020 ATRON electronic GmbH, All Rights      *
 * Reserved.                                                                  *
 *                                                                            *
 * NOTICE: All information contained herein is, and remains the property of   *
 * ATRON. The intellectual and technical concepts contained herein are        *
 * proprietary to ATRON and may be covered by U.S. and Foreign Patents,       *
 * patents in process, and are protected by trade secret or copyright law.    *
 * Dissemination of this information or reproduction of this material is      *
 * strictly forbidden unless prior written permission is obtained             *
 * from ATRON. Access to the source code contained herein is hereby forbidden *
 * to anyone except current ATRON employees, managers or contractors who have *
 * executed. Confidentiality and Non-disclosure agreements explicitly         *
 * covering such access.                                                      *
 *                                                                            *
 *                                                                            *
 * The copyright notice above does not evidence any actual or intended        *
 * publication or disclosure of this source code, which includes information  *
 * that is confidential and/or proprietary, and is a trade secret, of ATRON.  *
 * ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC PERFORMANCE, OR       *
 * PUBLIC DISPLAY OF OR THROUGH USE OF THIS SOURCE CODE WITHOUT THE EXPRESS   *
 * WRITTEN CONSENT OF ATRON IS STRICTLY PROHIBITED, AND IN VIOLATION OF       *
 * APPLICABLE LAWS AND INTERNATIONAL TREATIES. THE RECEIPT OR POSSESSION OF   *
 * THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY   *
 * RIGHTS TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO            *
 * MANUFACTURE, USE, OR SELL ANYTHING THAT IT MAY DESCRIBE, IN WHOLE OR IN    *
 * PART.                                                                      *
 *                                                                            *
 * $$Id: $$                                                                   *
 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

package de.atron.todos.business.dao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.atron.todos.business.entity.Filter;
import de.atron.todos.business.entity.Task;

import java.util.ArrayList;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TaskDao implements ITaskDao {

	@PersistenceContext
	EntityManager entityManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.atron.todos.business.dao.ITaskDao#listAll()
	 */
	@Override
	public List<Task> listAll() {
		return entityManager.createNamedQuery("Task.findAll", Task.class).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.atron.todos.business.dao.ITaskDao#filterAll(de.atron.todos.business.
	 * entity.Filter)
	 */
	@Override
	public List<Task> filterAll(final Filter filter) {
		List<Task> listAll = listAll();
		List<Task> result = new ArrayList<>();
		for (Task t : listAll) {
			if (filter.matches(t)) {
				result.add(t);
			}
		}
		return result;
		// return
		// listAll().parallelStream().filter(filter::matches).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.atron.todos.business.dao.ITaskDao#get(long)
	 */
	@Override
	public Task get(final long taskId) {
		return entityManager.find(Task.class, taskId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.atron.todos.business.dao.ITaskDao#save(de.atron.todos.business.entity.
	 * Task)
	 */
	@Override
	public long save(final Task task) {
		final Task managedTask = entityManager.merge(task);
		entityManager.flush();

		return managedTask.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.atron.todos.business.dao.ITaskDao#update(de.atron.todos.business.
	 * entity.Task)
	 */
	@Override
	public void update(final Task task) {
		entityManager.merge(task);
		entityManager.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.atron.todos.business.dao.ITaskDao#delete(long)
	 */
	@Override
	public void delete(final long taskId) {
		final Task managedTask = entityManager.find(Task.class, taskId);
		entityManager.remove(managedTask);
		entityManager.flush();
	}

}
