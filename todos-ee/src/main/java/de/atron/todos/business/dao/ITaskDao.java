package de.atron.todos.business.dao;

import java.util.List;

import de.atron.todos.business.entity.Filter;
import de.atron.todos.business.entity.Task;

public interface ITaskDao {

	List<Task> listAll();

	List<Task> filterAll(Filter filter);

	Task get(long taskId);

	long save(Task task);

	void update(Task task);

	void delete(long taskId);

}