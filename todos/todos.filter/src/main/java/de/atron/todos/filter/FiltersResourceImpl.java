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

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.atron.todos.jaxrs.client.JaxRsClientInvocationHandler;
import de.atron.todos.resources.FilterDTO;
import de.atron.todos.resources.FiltersResource;
import de.atron.todos.resources.TaskDTO;
import de.atron.todos.resources.TasksResource;

@Stateless
public class FiltersResourceImpl implements FiltersResource {
	Logger logger = LoggerFactory.getLogger(getClass());

    TasksResource tasksProxy;

    {
    	String hostname = System.getenv("todos_service_host");
    	hostname = hostname == null ? "localhost" : hostname;
    	String port = System.getenv("todos_service_port");
    	port = port == null ? "8090" : port;
        String host = hostname + ":" + port;
        logger.info("Creating client for {}", host );
		this.tasksProxy = (TasksResource)Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {
            TasksResource.class,
        }, new JaxRsClientInvocationHandler(TasksResource.class, "http", host, "resources"));
    }

    @Override
    @de.atron.todos.servlet.filters.Logged
    public List<TaskDTO> filterTasks(@Valid final FilterDTO filter) {
        List<TaskDTO> tasks = this.tasksProxy.listAll();
        FilterMatcher f = new FilterMatcher(filter);
        return tasks.parallelStream().filter(f::matches).collect(Collectors.toList());
    }

    class FilterMatcher {

        private final FilterDTO filter;

        /**
         * @param filter
         */
        public FilterMatcher(FilterDTO filter) {
            super();
            this.filter = filter;
        }

        /**
         * 
         * @param filter
         * @param task
         * @return
         */
        public boolean matches(final TaskDTO task) {
            String text = this.filter.getText();
            Set<String> contexts = this.filter.getContexts();
            Integer priorityThreshold = this.filter.getPriorityThreshold();

            if ((text != null) && !task.getName().toLowerCase().contains(text.toLowerCase())) {
                return false;
            }

            if (!contexts.isEmpty() && task.getContexts().stream().noneMatch(contexts::contains)) {
                return false;
            }
            // if (!contexts.isEmpty()) {
            // boolean match = false;
            // for (String context: task.getContexts()) {
            // if (contexts.contains(context)) {
            // match = true;
            // break;
            // }
            // }
            // if (!match) {
            // return false;
            // }
            // }

            if (priorityThreshold != null) {
                if ((task.getPriority() == null) || (priorityThreshold.compareTo(task.getPriority()) > 0)) {
                    return false;
                }
            }
            return true;
        }

    }

}
