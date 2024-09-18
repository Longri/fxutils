/*
 * Copyright (C) 2024 Longri
 *
 * This file is part of fxutils.
 *
 * fxutils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * fxutils is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with fxutils. If not, see <https://www.gnu.org/licenses/>.
 */
package de.longri.fx.file_handle_tree_view;

import de.longri.filetransfer.Local_FileTransferHandle;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * http://java-buddy.blogspot.com/2012/07/simple-treeview-example.html
 */
public class JavaFXtreeTestUi extends Application {


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Local_FileTransferHandle FH_Root = new Local_FileTransferHandle("./testTree/Root");

        if (!FH_Root.exists()) throw new RuntimeException("root not found");

        FileHandleTreeItem treeItemRoot = new FileHandleTreeItem(FH_Root);

        TreeView<String> treeView = new TreeView<>(treeItemRoot);
        StackPane root = new StackPane();
        root.getChildren().add(treeView);

        treeItemRoot.setExpanded(true);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("java-buddy.blogspot.com");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
