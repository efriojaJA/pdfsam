/* 
 * This file is part of the PDF Split And Merge source code
 * Created on 07/apr/2014
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
package org.pdfsam.extract;

import static org.pdfsam.module.ModuleDescriptorBuilder.builder;
import static org.pdfsam.ui.io.PdfDestinationPane.DestinationPanelFields.DISCARD_BOOKMARKS;
import static org.pdfsam.ui.support.Views.titledPane;

import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Named;

import jakarta.inject.Named;
import org.pdfsam.context.UserContext;
import org.pdfsam.i18n.I18nContext;
import org.pdfsam.injector.Auto;
import org.pdfsam.injector.Components;
import org.pdfsam.injector.Provides;
import org.pdfsam.module.ModuleCategory;
import org.pdfsam.module.ModuleDescriptor;
import org.pdfsam.module.ModuleInputOutputType;
import org.pdfsam.module.ModulePriority;
import org.pdfsam.ui.commons.ClearModuleEvent;
import org.pdfsam.ui.io.BrowsableOutputDirectoryField;
import org.pdfsam.ui.io.PdfDestinationPane;
import org.pdfsam.ui.module.BaseTaskExecutionModule;
import org.pdfsam.ui.module.Footer;
import org.pdfsam.ui.module.OpenButton;
import org.pdfsam.ui.module.RunButton;
import org.pdfsam.ui.prefix.PrefixPane;
import org.pdfsam.ui.support.Views;
import org.pdfsam.eventstudio.annotation.EventListener;
import org.pdfsam.eventstudio.annotation.EventStation;
import org.sejda.model.prefix.Prefix;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Extract module to let the user extract pages from a single PDF document
 * 
 * @author Andrea Vacondio
 *
 */
@Auto
public class ExtractModule extends BaseTaskExecutionModule {

    private static final String MODULE_ID = "extract";

    private ExtractSelectionPane selectionPane = new ExtractSelectionPane(MODULE_ID);
    private ExtractOptionsPane extractOptions = new ExtractOptionsPane();
    private BrowsableOutputDirectoryField destinationDirectoryField;
    private PdfDestinationPane destinationPane;
    private PrefixPane prefix;

    private ModuleDescriptor descriptor = builder().category(ModuleCategory.SPLIT)
            .inputTypes(ModuleInputOutputType.MULTIPLE_PDF, ModuleInputOutputType.SINGLE_PDF)
            .name(I18nContext.getInstance().i18n("Extract"))
            .description(I18nContext.getInstance().i18n("Extract pages from PDF documents."))
            .priority(ModulePriority.DEFAULT.getPriority()).supportURL("https://pdfsam.org/pdf-extract-pages/").build();

    @Inject
    public ExtractModule(@Named(MODULE_ID + "field") BrowsableOutputDirectoryField destinationDirectoryField,
            @Named(MODULE_ID + "pane") PdfDestinationPane destinationPane, @Named(MODULE_ID + "footer") Footer footer,
            @Named(MODULE_ID + "prefix") PrefixPane prefix) {
        super(footer);
        this.destinationDirectoryField = destinationDirectoryField;
        this.destinationPane = destinationPane;
        this.prefix = prefix;
        initModuleSettingsPanel(settingPanel());
    }

    @Override
    public ModuleDescriptor descriptor() {
        return descriptor;
    }

    @Override
    protected ExtractParametersBuilder getBuilder(Consumer<String> onError) {
        ExtractParametersBuilder builder = new ExtractParametersBuilder();
        extractOptions.apply(builder, onError);
        selectionPane.apply(builder, onError);
        destinationDirectoryField.apply(builder, onError);
        destinationPane.apply(builder, onError);
        prefix.apply(builder, onError);
        return builder;
    }

    @Override
    public void onSaveWorkspace(Map<String, String> data) {
        selectionPane.saveStateTo(data);
        extractOptions.saveStateTo(data);
        destinationPane.saveStateTo(data);
        destinationDirectoryField.saveStateTo(data);
        prefix.saveStateTo(data);
    }

    @Override
    public void onLoadWorkspace(Map<String, String> data) {
        if (data.containsKey("input")) {
            data.put("input.0", data.get("input"));
            data.put("input.password.0", data.get("input.password"));
            data.put("input.size", "1");
        }
        selectionPane.restoreStateFrom(data);
        extractOptions.restoreStateFrom(data);
        destinationPane.restoreStateFrom(data);
        destinationDirectoryField.restoreStateFrom(data);
        prefix.restoreStateFrom(data);
    }

    private VBox settingPanel() {
        VBox pane = new VBox();
        pane.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(selectionPane, Priority.ALWAYS);

        TitledPane prefixTitled = Views.titledPane(I18nContext.getInstance().i18n("File names settings"),
                prefix);
        prefix.addMenuItemFor(Prefix.FILENUMBER);
        prefix.addMenuItemFor("[TOTAL_FILESNUMBER]");

        pane.getChildren().addAll(selectionPane,
                titledPane(I18nContext.getInstance().i18n("Extract settings"), extractOptions),
                titledPane(I18nContext.getInstance().i18n("Output settings"), destinationPane), prefixTitled);
        return pane;
    }

    @Override
    @EventStation
    public String id() {
        return MODULE_ID;
    }

    @EventListener
    public void onClearModule(ClearModuleEvent e) {
        if (e.clearEverything) {
            extractOptions.resetView();
            prefix.resetView();
            destinationPane.resetView();
        }
    }

    @Override
    public Node graphic() {
        return new ImageView("extract.png");
    }

    @Components({ ExtractModule.class })
    public static class ModuleConfig {
        @Provides
        @Named(MODULE_ID + "field")
        public BrowsableOutputDirectoryField destinationDirectoryField() {
            return new BrowsableOutputDirectoryField();
        }

        @Provides
        @Named(MODULE_ID + "pane")
        public PdfDestinationPane destinationPane(@Named(MODULE_ID + "field") BrowsableOutputDirectoryField outputField,
                UserContext userContext) {
            return new PdfDestinationPane(outputField, MODULE_ID, userContext, DISCARD_BOOKMARKS);
        }

        @Provides
        @Named(MODULE_ID + "footer")
        public Footer footer(RunButton runButton, @Named(MODULE_ID + "openButton") OpenButton openButton) {
            return new Footer(runButton, openButton, MODULE_ID);
        }

        @Provides
        @Named(MODULE_ID + "openButton")
        public OpenButton openButton() {
            return new OpenButton(MODULE_ID, ModuleInputOutputType.MULTIPLE_PDF);
        }

        @Provides
        @Named(MODULE_ID + "prefix")
        public PrefixPane prefixPane(UserContext userContext) {
            return new PrefixPane(MODULE_ID, userContext);
        }
    }

}
