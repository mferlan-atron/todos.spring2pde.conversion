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

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import de.atron.todos.business.dao.ITaskDao;
import de.atron.todos.business.entity.Task;

import java.net.URI;
import java.util.List;

@Path("tasks")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@Named
public class TasksResource {

	@Inject
    ITaskDao taskDao;

    @GET
    public List<Task> listAll() {
        return taskDao.listAll();
    }

    @GET
    @Path("{id}")
    public Task get(@PathParam("id") final long taskId) {
        return taskDao.get(taskId);
    }

    @POST
    public Response save(@Valid final Task task) {
        final long taskId = taskDao.save(task);

        URI taskUri = URI.create("/tasks/" + taskId);
        return Response.created(taskUri).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") final long taskId, @Valid final Task task) throws Exception {
        task.setId(taskId);
        taskDao.update(task);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") final long taskId) {
        taskDao.delete(taskId);
        return Response.noContent().build();
    }

}
