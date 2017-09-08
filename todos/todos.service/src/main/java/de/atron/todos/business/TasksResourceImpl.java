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

package de.atron.todos.business;

import java.net.URI;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import de.atron.todos.business.dao.ITaskDao;
import de.atron.todos.business.entity.Task;
import de.atron.todos.resources.TaskDTO;
import de.atron.todos.servlet.filters.Logged;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Stateless
public class TasksResourceImpl implements de.atron.todos.resources.TasksResource {

    private MapperFacade mapperFacade;

    {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Task.class, TaskDTO.class);
        this.mapperFacade = mapperFactory.getMapperFacade();
    }

    @Inject
    ITaskDao taskDao;

    @Override
    @Logged
    public List<TaskDTO> listAll() {
        return this.mapperFacade.mapAsList(this.taskDao.listAll(), TaskDTO.class);
    }

    @Override
    @Logged
    public TaskDTO get(@PathParam("id") final long taskId) {
        return this.mapperFacade.map(this.taskDao.get(taskId), TaskDTO.class);
    }

    @Override
    @Logged
    public Response save(@Valid final TaskDTO task) {
        final long taskId = this.taskDao.save(this.mapperFacade.map(task, Task.class));

        URI taskUri = URI.create("/tasks/" + taskId);
        return Response.created(taskUri).build();
    }

    @Override
    @Logged
    public Response update(@PathParam("id") final long taskId, @Valid final TaskDTO task) throws Exception {
        task.setId(taskId);
        this.taskDao.update(this.mapperFacade.map(task, Task.class));
        return Response.noContent().build();
    }

    @Override
    @Logged
    public Response delete(@PathParam("id") final long taskId) {
        this.taskDao.delete(taskId);
        return Response.noContent().build();
    }

}
