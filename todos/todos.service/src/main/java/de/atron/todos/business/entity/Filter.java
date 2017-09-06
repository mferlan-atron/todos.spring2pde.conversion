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

package de.atron.todos.business.entity;

import java.util.HashSet;
import java.util.Set;

public class Filter {

    private String text;

    private Set<String> contexts = new HashSet<>();

    private Integer priorityThreshold;

    public boolean matches(final Task task) {
        if (text != null && !task.getName().toLowerCase().contains(text.toLowerCase())) {
            return false;
        }

//        if (!contexts.isEmpty() && task.getContexts().stream().noneMatch(contexts::contains)) {
//            return false;
//        }
        if (!contexts.isEmpty()) {
        	boolean match = false;
        	for(String context : task.getContexts()){
        		if(contexts.contains(context)){
        			match = true;
        			break;
        		}
        	}
        	if(!match)return false;
        }

        if (priorityThreshold != null) {
            if (task.getPriority() == null || priorityThreshold.compareTo(task.getPriority()) > 0) {
                return false;
            }
        }
        return true;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Set<String> getContexts() {
        return contexts;
    }

    public void setContexts(Set<String> contexts) {
        this.contexts = contexts;
    }

    public Integer getPriorityThreshold() {
        return priorityThreshold;
    }

    public void setPriorityThreshold(Integer priorityThreshold) {
        this.priorityThreshold = priorityThreshold;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "text='" + text + '\'' +
                ", contexts=" + contexts +
                ", priorityThreshold=" + priorityThreshold +
                '}';
    }

}
