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

package org.jboss.as.console.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.DelayedBindRegistry;
import org.jboss.as.console.client.core.LoadingPanel;
import org.jboss.as.console.client.core.UIConstants;
import org.jboss.as.console.client.core.UIDebugConstants;
import org.jboss.as.console.client.core.UIMessages;
import org.jboss.as.console.client.core.bootstrap.BootstrapProcess;
import org.jboss.as.console.client.core.bootstrap.ChoseProcessor;
import org.jboss.as.console.client.core.bootstrap.EagerLoadProfiles;
import org.jboss.as.console.client.core.bootstrap.ExecutionMode;
import org.jboss.as.console.client.core.bootstrap.LoadMainApp;
import org.jboss.as.console.client.core.bootstrap.RemoveLoadingPanel;
import org.jboss.as.console.client.core.gin.CoreUI;
import org.jboss.as.console.client.core.message.Message;

import java.util.EnumSet;

/**
 * Main application entry point.
 * Executes a two phased init process:
 * <ol>
 *     <li>Identify management model (standalone vs. domain)
 *     <li>Load main application
 * </ol>
 *
 * @author Heiko Braun
 */
public class Console implements EntryPoint {

    public final static CoreUI MODULES = GWT.create(CoreUI.class);
    public final static UIConstants CONSTANTS = GWT.create(UIConstants.class);
    public final static UIDebugConstants DEBUG_CONSTANTS = GWT.create(UIDebugConstants.class);
    public final static UIMessages MESSAGES = GWT.create(UIMessages.class);

    public void onModuleLoad() {
        // Defer all application initialisation code to onModuleLoad2() so that the
        // UncaughtExceptionHandler can catch any unexpected exceptions.
        Log.setUncaughtExceptionHandler();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onModuleLoad2();
            }
        });
    }

    public void onModuleLoad2() {

        // load console css bundle
        ConsoleResources.INSTANCE.css().ensureInjected();

        // display the loading panel
        final Widget loadingPanel = new LoadingPanel().asWidget();
        RootLayoutPanel.get().add(loadingPanel);

        GWT.runAsync(new RunAsyncCallback() {
            public void onFailure(Throwable caught) {
                Window.alert("Failed to load application components!");
            }

            public void onSuccess() {
                DelayedBindRegistry.bind(MODULES);

                // ordered bootstrap
                BootstrapProcess bootstrap = new BootstrapProcess();

                bootstrap.addHook(new ExecutionMode(MODULES.getBootstrapContext(), MODULES.getDispatchAsync()));
                bootstrap.addHook(new ChoseProcessor(MODULES.getBootstrapContext()));
                bootstrap.addHook(new EagerLoadProfiles());
                bootstrap.addHook(new RemoveLoadingPanel(loadingPanel));
                bootstrap.addHook(new LoadMainApp(MODULES.getBootstrapContext(), MODULES.getPlaceManager(), MODULES.getTokenFormatter()));

                // viz can be loaded in background ...
                //bootstrap.addHook(new LoadGoogleViz());

                bootstrap.execute(new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        error("Bootstrap failed", caught.getMessage());
                    }

                    @Override
                    public void onSuccess(Boolean wasSuccessfull) {
                        if(!wasSuccessfull)
                        {
                            // currently we only deal with authentication errors
                            RootLayoutPanel.get().remove(loadingPanel);

                            HTMLPanel explanation = new HTMLPanel("<center><div style='padding-top:150px;'>The web console could not be loaded. Please refresh this page to start over.</div></center>");
                            RootLayoutPanel.get().add(explanation);
                        }

                    }
                });
            }

        });
    }

    public static void info(String message) {
        MODULES.getMessageCenter().notify(
                new Message(message, Message.Severity.Info)
        );
    }

    public static void error(String message) {
        MODULES.getMessageCenter().notify(
                new Message(message, Message.Severity.Error)
        );
    }

    public static void error(String message, String detail) {
        MODULES.getMessageCenter().notify(
                new Message(message, detail, Message.Severity.Error)
        );
    }

    public static void warning(String message) {
        MODULES.getMessageCenter().notify(
                new Message(message, Message.Severity.Warning)
        );
    }

    public static void warning(String message, boolean sticky) {
        Message msg = sticky ?
                new Message(message, Message.Severity.Warning, EnumSet.of(Message.Option.Sticky)) :
                new Message(message, Message.Severity.Warning);

        MODULES.getMessageCenter().notify(msg);
    }

    public static void warning(String message, String detail, boolean sticky) {
        Message msg = sticky ?
                new Message(message, detail, Message.Severity.Warning, EnumSet.of(Message.Option.Sticky)) :
                new Message(message, detail, Message.Severity.Warning);


        MODULES.getMessageCenter().notify(msg);
    }

    public static void warning(String message, String detail) {
        MODULES.getMessageCenter().notify(
                new Message(message, detail, Message.Severity.Warning)
        );
    }

    public static void schedule(final Command cmd)
    {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                cmd.execute();
            }
        });
    }

    public static native boolean visAPILoaded() /*-{
        if ($wnd['google'] && $wnd.google['load']) {
            return true;
        }
        return false;
    }-*/;



}
