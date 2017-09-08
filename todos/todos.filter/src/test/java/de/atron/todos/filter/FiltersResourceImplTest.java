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

package de.atron.todos.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import de.atron.todos.resources.FilterDTO;
import de.atron.todos.resources.TaskDTO;
import de.atron.todos.resources.TasksResource;

public class FiltersResourceImplTest {

    private FiltersResourceImpl cut;

    @Before
    public void setUp() {
        this.cut = new FiltersResourceImpl();
        this.cut.tasksProxy = Mockito.mock(TasksResource.class);
        Mockito.when(this.cut.tasksProxy.listAll()).thenReturn(sampleTasks());
    }

    @Test
    public void testFilterContextMatch() {
        FilterDTO filter = new FilterDTO();
        filter.setContexts(new HashSet<>(Arrays.asList("abc")));
        List<TaskDTO> filteredTasks = this.cut.filterTasks(filter);

        Assert.assertEquals(1, filteredTasks.size());
        Assert.assertEquals(2L, filteredTasks.iterator().next().getId());
    }

    @Test
    public void testFilterContextNoMatch() {
        FilterDTO filter = new FilterDTO();
        filter.setContexts(new HashSet<>(Arrays.asList("abcd")));
        List<TaskDTO> filteredTasks = this.cut.filterTasks(filter);

        Assert.assertEquals(0, filteredTasks.size());
    }

    @Test
    public void testFilterNameMatch() {
        FilterDTO filter = new FilterDTO();
        filter.setText("test");
        List<TaskDTO> filteredTasks = this.cut.filterTasks(filter);

        Assert.assertEquals(3, filteredTasks.size());
        assertContainsExactly(filteredTasks, 1L, 2L, 3L);
    }

    @Test
    public void testFilterNameMatchUpperCase() {
        FilterDTO filter = new FilterDTO();
        filter.setText("teSt");
        List<TaskDTO> filteredTasks = this.cut.filterTasks(filter);

        Assert.assertEquals(3, filteredTasks.size());
        assertContainsExactly(filteredTasks, 1L, 2L, 3L);
    }

    @Test
    public void testFilterNameMatchUnicode() {
        FilterDTO filter = new FilterDTO();
        filter.setText("täSt");
        List<TaskDTO> filteredTasks = this.cut.filterTasks(filter);

        Assert.assertEquals(2, filteredTasks.size());
        assertContainsExactly(filteredTasks, 5L, 6L);
    }

    @Test
    public void testMultipleNameContexts() {
        FilterDTO filter = new FilterDTO();
        filter.setText("tEst");
        filter.setContexts(new HashSet<>(Arrays.asList("abc")));
        List<TaskDTO> filteredTasks = this.cut.filterTasks(filter);

        Assert.assertEquals(1, filteredTasks.size());
        Assert.assertEquals(2L, filteredTasks.iterator().next().getId());
    }

    @Test
    public void testFilterNameContextNoMatch() {
        FilterDTO filter = new FilterDTO();
        filter.setText("test");
        filter.setContexts(new HashSet<>(Arrays.asList("abcd")));
        List<TaskDTO> filteredTasks = this.cut.filterTasks(filter);

        Assert.assertEquals(0, filteredTasks.size());
    }

    @Test
    public void testFilterPriority() {
        FilterDTO filter = new FilterDTO();
        filter.setPriorityThreshold(1);
        List<TaskDTO> filteredTasks = this.cut.filterTasks(filter);

        Assert.assertEquals(3, filteredTasks.size());
        assertContainsExactly(filteredTasks, 2L, 4L, 6L);

        filter.setPriorityThreshold(2);
        filteredTasks = this.cut.filterTasks(filter);

        Assert.assertEquals(1, filteredTasks.size());
        assertContainsExactly(filteredTasks, 6L);
    }

    private List<TaskDTO> sampleTasks() {
        TaskDTO firstTask = createTask("test123", 1L, null);
        TaskDTO secondTask = createTask("anotherTest234", 2L, 1, "abc", "efg");
        TaskDTO thirdTask = createTask("abctEsT42", 3L, null);
        TaskDTO fourthTask = createTask("another157", 4L, 1, "cde");
        TaskDTO fifthTask = createTask("täst", 5L, null);
        TaskDTO sixthTask = createTask("anothertÄst", 6L, 2, "efg");

        return Arrays.asList(firstTask, secondTask, thirdTask, fourthTask, fifthTask, sixthTask);
    }

    private TaskDTO createTask(final String name, final long id, final Integer priority, final String... contexts) {
        final TaskDTO task = new TaskDTO();
        task.setId(id);
        task.setName(name);
        task.setPriority(priority);
        task.setContexts(new HashSet<>(Arrays.asList(contexts)));
        return task;
    }

    private void assertContainsExactly(final List<TaskDTO> filteredTasks, final Long... ids) {
        List<Long> idsLeft = new ArrayList<Long>(Arrays.asList(ids));

        for (final TaskDTO task: filteredTasks) {
            Assert.assertTrue(idsLeft.remove(task.getId()));
        }
        Assert.assertTrue(idsLeft.isEmpty());
    }

}
