/*
 * This file is part of the PDF Split And Merge source code
 * Created on 03/mag/2014
 * Copyright 2017 by Sober Lemur S.a.s. di Vacondio Andrea (info@pdfsam.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pdfsam.gui.components.banner;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import javafx.scene.control.Tooltip;
import org.kordamp.ikonli.unicons.UniconsLine;
import org.pdfsam.model.ui.SetActiveDashboardItemRequest;

import static org.pdfsam.eventstudio.StaticStudio.eventStudio;
import static org.pdfsam.i18n.I18nContext.i18n;

/**
 * Button to open the dashboard
 *
 * @author Andrea Vacondio
 */
class DashboardButton extends BannerButton {
    @Inject
    DashboardButton(@Named("defaultDashboardItemId") String defaultDasboardItem) {
        super(UniconsLine.HOME_ALT);
        setOnAction(e -> eventStudio().broadcast(new SetActiveDashboardItemRequest(defaultDasboardItem)));
        setTooltip(new Tooltip(i18n().tr("Dashboard")));
    }
}
