/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.shared.subsys.threads;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.threads.model.QueuelessThreadPool;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;


/**
 * Main view class for Queueless Thread Pools
 *
 * @author Stan Silvert
 */
public class QueuelessThreadPoolView extends AbstractThreadPoolView<QueuelessThreadPool> {

    public QueuelessThreadPoolView(ApplicationMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(QueuelessThreadPool.class, propertyMetaData, dispatcher);
    }

    @Override
    protected String provideDescription() {
        return  Console.CONSTANTS.subsys_threads_queueless_desc();
    }

    @Override
    protected String getEntityDisplayName() {
        return "Queueless Pools";
    }

    @Override
    protected FormAdapter<QueuelessThreadPool> makeAddEntityForm() {
        Form<QueuelessThreadPool> form = new Form(QueuelessThreadPool.class);
        form.setNumColumns(1);
        form.setFields(formMetaData.findAttribute("name").getFormItemForAdd(),
                formMetaData.findAttribute("maxThreads").getFormItemForAdd(),
                formMetaData.findAttribute("keepaliveTimeout").getFormItemForAdd(),
                formMetaData.findAttribute("keepaliveTimeoutUnit").getFormItemForAdd());
        return form;
    }

}
