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
package de.longri.fx.utils;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.Comparator;
import java.util.HashMap;

public abstract class TableViewData<T> extends AbstractFilteredList<T> {
    protected final HashMap<String, TableColumn> columnHashMap = new HashMap<>();


    protected abstract void initialCellValueFactories();

    protected abstract void initialCellComparator();

    public void setTableView(TableView<T> tableView, TableColumn<T, ?>... columns) {
        TableColumn[] var3 = columns;
        int var4 = columns.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            TableColumn<T, ?> col = var3[var5];
            this.addColumn(col, col.getId());
        }
        this.initialCellValueFactories();
        this.initialCellComparator();

        tableView.setItems(this);
    }

    protected void addColumn(TableColumn<T, ?> column, String columnName) {
        column.setCellValueFactory(new PropertyValueFactory(columnName));
        this.columnHashMap.put(columnName, column);
    }

    protected TableColumn getColumn(String name) {
        return (TableColumn) this.columnHashMap.get(name);
    }

    protected void setCellValueFactory(String columnName, Callback<TableColumn.CellDataFeatures<T, ?>, ObservableValue<?>> valueCallback) {
        TableColumn col = this.getColumn(columnName);
        if (col != null) {
            col.setCellValueFactory(valueCallback);
        }
    }

    protected void setComparator(String columnName, Comparator comparator) {
        TableColumn col = this.getColumn(columnName);
        if (col != null) {
            col.setComparator(comparator);
        }
    }
}
