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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import de.atron.todos.business.dao.TaskDao;
import de.atron.todos.business.entity.Filter;
import de.atron.todos.business.entity.Task;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TaskDaoTest {

    private TaskDao cut;

    @SuppressWarnings("unchecked")
	@Before
    public void setUp() {
        cut = new TaskDao();
        cut.entityManager = Mockito.mock(EntityManager.class);
		TypedQuery<Task> mockQuery = Mockito.mock(TypedQuery.class);

        Mockito.when(cut.entityManager.createNamedQuery(Matchers.anyString(), Matchers.any(Class.class))).thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(sampleTasks());
    }

    @Test
    public void testFilterContextMatch() {
        Filter filter = new Filter();
        filter.setContexts(new HashSet<>(Arrays.asList("abc")));
        List<Task> filteredTasks = cut.filterAll(filter);

        Assert.assertEquals(1, filteredTasks.size());
        Assert.assertEquals(2L, filteredTasks.iterator().next().getId());
    }

    @Test
    public void testFilterContextNoMatch() {
        Filter filter = new Filter();
        filter.setContexts(new HashSet<>(Arrays.asList("abcd")));
        List<Task> filteredTasks = cut.filterAll(filter);

        Assert.assertEquals(0, filteredTasks.size());
    }

    @Test
    public void testFilterNameMatch() {
        Filter filter = new Filter();
        filter.setText("test");
        List<Task> filteredTasks = cut.filterAll(filter);

        Assert.assertEquals(3, filteredTasks.size());
        assertContainsExactly(filteredTasks, 1L, 2L, 3L);
    }

    @Test
    public void testFilterNameMatchUpperCase() {
        Filter filter = new Filter();
        filter.setText("teSt");
        List<Task> filteredTasks = cut.filterAll(filter);

        Assert.assertEquals(3, filteredTasks.size());
        assertContainsExactly(filteredTasks, 1L, 2L, 3L);
    }

    @Test
    public void testFilterNameMatchUnicode() {
        Filter filter = new Filter();
        filter.setText("täSt");
        List<Task> filteredTasks = cut.filterAll(filter);

        Assert.assertEquals(2, filteredTasks.size());
        assertContainsExactly(filteredTasks, 5L, 6L);
    }

    @Test
    public void testMultipleNameContexts() {
        Filter filter = new Filter();
        filter.setText("tEst");
        filter.setContexts(new HashSet<>(Arrays.asList("abc")));
        List<Task> filteredTasks = cut.filterAll(filter);

        Assert.assertEquals(1, filteredTasks.size());
        Assert.assertEquals(2L, filteredTasks.iterator().next().getId());
    }

    @Test
    public void testFilterNameContextNoMatch() {
        Filter filter = new Filter();
        filter.setText("test");
        filter.setContexts(new HashSet<>(Arrays.asList("abcd")));
        List<Task> filteredTasks = cut.filterAll(filter);

        Assert.assertEquals(0, filteredTasks.size());
    }

    @Test
    public void testFilterPriority() {
        Filter filter = new Filter();
        filter.setPriorityThreshold(1);
        List<Task> filteredTasks = cut.filterAll(filter);

        Assert.assertEquals(3, filteredTasks.size());
        assertContainsExactly(filteredTasks, 2L, 4L, 6L);

        filter.setPriorityThreshold(2);
        filteredTasks = cut.filterAll(filter);

        Assert.assertEquals(1, filteredTasks.size());
        assertContainsExactly(filteredTasks, 6L);
    }

    private List<Task> sampleTasks() {
        Task firstTask = createTask("test123", 1L, null);
        Task secondTask = createTask("anotherTest234", 2L, 1, "abc", "efg");
        Task thirdTask = createTask("abctEsT42", 3L, null);
        Task fourthTask = createTask("another157", 4L, 1, "cde");
        Task fifthTask = createTask("täst", 5L, null);
        Task sixthTask = createTask("anothertÄst", 6L, 2, "efg");

        return Arrays.asList(firstTask, secondTask, thirdTask, fourthTask, fifthTask, sixthTask);
    }

    private Task createTask(final String name, final long id, final Integer priority, final String... contexts) {
        final Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setPriority(priority);
        task.setContexts(new HashSet<>(Arrays.asList(contexts)));
        return task;
    }

    private void assertContainsExactly(final List<Task> filteredTasks, final Long... ids) {
        List<Long> idsLeft = new ArrayList<Long>(Arrays.asList(ids));

        for (final Task task : filteredTasks) {
            Assert.assertTrue(idsLeft.remove(task.getId()));
        }
        Assert.assertTrue(idsLeft.isEmpty());
    }

}
