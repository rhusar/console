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

package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.SplitEditorPanel;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public class XADataSourceEditor {

    private DataSourcePresenter presenter;
    private DatasourceTable dataSourceTable;
    private XADataSourceDetails details;

    public XADataSourceEditor(DataSourcePresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {

        SplitEditorPanel editorPanel = new SplitEditorPanel();
        LayoutPanel layout = editorPanel.getTopLayout();

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.subsys_jca_newDataSource(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

            }
        }));

        layout.add(topLevelTools);

        // ----

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.getElement().setAttribute("style", "margin:15px; width:95%");

        layout.add(vpanel);

        layout.setWidgetTopHeight(topLevelTools, 0, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(vpanel, 30, Style.Unit.PX, 100, Style.Unit.PCT);

        // ---

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.database());
        horzPanel.add(image);
        horzPanel.add(new ContentHeaderLabel("XA Datasources"));
        image.getElement().getParentElement().setAttribute("width", "25");

        vpanel.add(horzPanel);

        dataSourceTable = new DatasourceTable();

        vpanel.add(dataSourceTable.asWidget());


        // -----------
        details = new XADataSourceDetails(presenter);
        details.bind(dataSourceTable.getCellTable());

        editorPanel.getBottomLayout().add(details.asWidget(), "Details");
        editorPanel.getBottomLayout().add(new HTML("todo"), "Metrics");

        return editorPanel.asWidget();
    }


    public void updateDataSources(List<DataSource> datasources) {
        dataSourceTable.getDataProvider().setList(datasources);

        if(!datasources.isEmpty())
            dataSourceTable.getCellTable().getSelectionModel().setSelected(datasources.get(0), true);

    }

    public void setEnabled(boolean isEnabled) {
        details.setEnabled(isEnabled);
    }
}