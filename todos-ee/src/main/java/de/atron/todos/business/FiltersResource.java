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
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import de.atron.todos.business.dao.ITaskDao;
import de.atron.todos.business.entity.Filter;
import de.atron.todos.business.entity.Task;

import java.util.List;

@Path("filters")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FiltersResource {

    @Inject
    ITaskDao taskDao;

    @POST
    public List<Task> filterTasks(@Valid final Filter filter) {
        return taskDao.filterAll(filter);
    }
}
