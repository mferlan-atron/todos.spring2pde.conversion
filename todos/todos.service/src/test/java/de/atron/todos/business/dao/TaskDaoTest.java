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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import de.atron.todos.business.entity.Task;

public class TaskDaoTest {

    private TaskDao cut;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        this.cut = new TaskDao();
        this.cut.entityManager = Mockito.mock(EntityManager.class);
        TypedQuery<Task> mockQuery = Mockito.mock(TypedQuery.class);

        Mockito.when(
            this.cut.entityManager.createNamedQuery(ArgumentMatchers.anyString(), ArgumentMatchers.any(Class.class)))
            .thenReturn(mockQuery);
        Mockito.when(mockQuery.getResultList()).thenReturn(sampleTasks());
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

    @Test
    public void test() {
    }

}
