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
package de.longri.fx.file_transfer_chooser;

import de.longri.fx.FX_Application;
import de.longri.fx.FxStyles;
import de.longri.fx.SelfLoading_Controller;
import de.longri.fx.file_handle_tree_view.FileHandleTreeItem;
import de.longri.utils.Preferences;
import de.longri.filetransfer.*;
import de.longri.serializable.BitStore;
import de.longri.serializable.NotImplementedException;
import de.longri.serializable.StoreBase;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class FileTransferChooser extends SelfLoading_Controller implements Initializable {

    private static Logger log = LoggerFactory.getLogger(FileTransferChooser.class);

    @FXML
    RadioButton fxRadioButton_File;
    @FXML
    RadioButton fxRadioButton_FTP;
    @FXML
    RadioButton fxRadioButton_SFTP;
    @FXML
    RadioButton fxRadioButton_SMB;

    @FXML
    ToggleGroup TransferType;

    @FXML
    AnchorPane fxRoot;

    @FXML
    Label fxLblAddress;
    @FXML
    Label fxLblPort;
    @FXML
    Label fxLblDomain;
    @FXML
    Label fxLblUser;
    @FXML
    Label fxLblPasswort;
    @FXML
    Button fxBtnCreateDir;
    @FXML
    Button fxBtnCancel;
    @FXML
    Button fxBtnSelect;
    @FXML
    Button fxBtnShowPW;
    @FXML
    TextField fxTxtFieldAddress;
    @FXML
    TextField fxTxtFieldPort;
    @FXML
    TextField fxTxtFieldDomain;
    @FXML
    TextField fxTxtFieldUser;
    @FXML
    TextField fxTxtFieldPasswort;
    @FXML
    TextField fxTxtFieldState;
    @FXML
    PasswordField fxPassField;
    @FXML
    VBox fxVBoxPort;
    @FXML
    TreeView<String> fxDirectoryTree;
    @FXML
    CheckBox fxCheckBoxRemember;
    @FXML
    ListView<IndexedPath> fxListOfFiles;
    @FXML
    Button fxBtnConnect;


    private ObjectProperty<FileTransferHandle> selectedDirectoryProperty = new SimpleObjectProperty<FileTransferHandle>(null);
    private FileTransferHandle actFTH;
    private Map<FileTransferHandle, Task<Void>> runningUpdateTasks = new ConcurrentHashMap<>();
    private TreeItem<String> root;
    private ExecutorService executor;
    private Preferences PREF;
    private FileChooserModel model;

    static final String KEY_SMB_CREDENTIAL_STORE = "smb_cred";
    static final String KEY_REMEMBER_CREDENTIAL_STORE = "remember_cred";
    static final String KEY_REMEMBER_OPTION = "remember_option";
    static final String KEY_PREF_STORED_LOCAL_FILE_HANDLE = "ChooserStored_local_fileHandle";
    static final String KEY_PREF_STORED_SFTP_FILE_HANDLE = "ChooserStored_sftp_fileHandle";
    static final String KEY_PREF_STORED_SMB_FILE_HANDLE = "ChooserStored_smb_fileHandle";
    static final String KEY_PREF_STORED_FTP_FILE_HANDLE = "ChooserStored_ftp_fileHandle";

    private Local_FileTransferHandle local_fileTransferHandle;
    private SFTP_FileTransferHandle sftp_fileTransferHandle;
    private SMB_FileTransferHandle smb_fileTransferHandle;
    private Ftp_FileTransferHandle ftp_fileTransferHandle;
    public String SceneTitel = null;


    public FileTransferChooser() {
        this((Preferences) null);
    }

    public FileTransferChooser(Preferences preferences) {
        super("/views/FileTransferChooser.fxml");

        PREF = preferences;
        // try to use application preferences
        if (FX_Application.INSTANCE != null) {
            PREF = FX_Application.INSTANCE.PREF;
            if (PREF.contains(KEY_REMEMBER_CREDENTIAL_STORE)) {
                boolean remember = PREF.getBoolean(KEY_REMEMBER_CREDENTIAL_STORE);
                fxCheckBoxRemember.selectedProperty().set(remember);
                if (remember && PREF.contains(KEY_REMEMBER_OPTION)) {
                    String option = PREF.getString(KEY_REMEMBER_OPTION);

                    switch (option) {
                        case "fxRadioButton_SMB":
                            fxRadioButton_SMB.selectedProperty().set(true);
                            break;
                        case "fxRadioButton_File":
                            fxRadioButton_File.selectedProperty().set(true);
                            break;
                        case "fxRadioButton_FTP":
                            fxRadioButton_FTP.selectedProperty().set(true);
                            break;
                        case "fxRadioButton_SFTP":
                            fxRadioButton_SFTP.selectedProperty().set(true);
                            break;
                    }
                }
            }
        }
        initial();
    }

    public FileTransferChooser(FileTransferHandle handle) {
        super("/views/FileTransferChooser.fxml");
        // try to use application preferences
        if (FX_Application.INSTANCE != null) {
            PREF = FX_Application.INSTANCE.PREF;
            if (PREF.contains(KEY_REMEMBER_CREDENTIAL_STORE)) {
                boolean remember = PREF.getBoolean(KEY_REMEMBER_CREDENTIAL_STORE);
                fxCheckBoxRemember.selectedProperty().set(remember);
            }
        }
        //set FileTransferHandle options
        if (handle instanceof Local_FileTransferHandle) {
            fxRadioButton_File.selectedProperty().set(true);
            // clear credentials
            fxTxtFieldAddress.setText("");
            fxTxtFieldPort.setText("");
            fxTxtFieldDomain.setText("");
            fxTxtFieldUser.setText("");
            fxTxtFieldPasswort.setText("");
        } else {
            Credentials credentials = null;
            if (handle instanceof SMB_FileTransferHandle) {
                fxRadioButton_SMB.selectedProperty().set(true);
                credentials = ((SMB_FileTransferHandle) handle).getCredentials();
            } else if (handle instanceof SFTP_FileTransferHandle) {
                fxRadioButton_SFTP.selectedProperty().set(true);
                credentials = ((SFTP_FileTransferHandle) handle).getCredentials();
            } else if (handle instanceof Ftp_FileTransferHandle) {
                fxRadioButton_FTP.selectedProperty().set(true);
                credentials = ((Ftp_FileTransferHandle) handle).getCredentials();
            }
            // set credentials
            if (credentials != null) {
                fxTxtFieldAddress.setText(credentials.getAddress());
                fxTxtFieldPort.setText(credentials.getPort());
                fxTxtFieldDomain.setText(credentials.getDomain());
                fxTxtFieldUser.setText(credentials.getUser());
                fxTxtFieldPasswort.setText(credentials.getPassword());
            }
        }
        initial();
    }

    private void initial() {
        initialOptionButtons();
        initialDirectoryTree();
        initialListOfFiles();
        fxPassField.textProperty().bindBidirectional(fxTxtFieldPasswort.textProperty());


        //read preferences
        if (PREF != null) {

            String base64Str = PREF.getString(KEY_PREF_STORED_LOCAL_FILE_HANDLE);
            if (base64Str != null) {
                try {
                    local_fileTransferHandle = (Local_FileTransferHandle) FileTransferHelper.getFileTransferHandle(new BitStore(base64Str));
                } catch (NotImplementedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }

            base64Str = PREF.getString(KEY_PREF_STORED_SFTP_FILE_HANDLE);
            if (base64Str != null) {
                try {
                    sftp_fileTransferHandle = (SFTP_FileTransferHandle) FileTransferHelper.getFileTransferHandle(new BitStore(base64Str));
                } catch (NotImplementedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }

            base64Str = PREF.getString(KEY_PREF_STORED_SMB_FILE_HANDLE);
            if (base64Str != null) {
                try {
                    smb_fileTransferHandle = (SMB_FileTransferHandle) FileTransferHelper.getFileTransferHandle(new BitStore(base64Str));
                } catch (NotImplementedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
            base64Str = PREF.getString(KEY_PREF_STORED_FTP_FILE_HANDLE);
            if (base64Str != null) {
                try {
                    ftp_fileTransferHandle = (Ftp_FileTransferHandle) FileTransferHelper.getFileTransferHandle(new BitStore(base64Str));
                } catch (NotImplementedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }

            String optionStr = PREF.getString(KEY_REMEMBER_OPTION);
            log.debug("initial select option: {}", optionStr);
            if (optionStr != null) switch (optionStr) {
                case "fxRadioButton_SMB":
                    Platform.runLater(() -> {
                        fxRadioButton_SMB.setSelected(true);
                        fxRadioButton_SMB.requestFocus();
                        Platform.runLater(() -> {
                            optionChanged();
                            Platform.runLater(() -> {
                                setDir(smb_fileTransferHandle, true);
                            });
                        });
                    });
                    break;
                case "fxRadioButton_File":
                    Platform.runLater(() -> {
                        fxRadioButton_File.setSelected(true);
                        fxRadioButton_File.requestFocus();
                        Platform.runLater(() -> {
                            optionChanged();
                            Platform.runLater(() -> {
                                if (local_fileTransferHandle == null)
                                    local_fileTransferHandle = new Local_FileTransferHandle("/");
                                setDir(local_fileTransferHandle, true);
                            });
                        });
                    });
                    break;
                case "fxRadioButton_FTP":
                    Platform.runLater(() -> {
                        fxRadioButton_FTP.setSelected(true);
                        fxRadioButton_FTP.requestFocus();
                        Platform.runLater(() -> {
                            optionChanged();
                            Platform.runLater(() -> {
                                setDir(ftp_fileTransferHandle, true);
                            });
                        });
                    });
                    break;
                case "fxRadioButton_SFTP":
                    Platform.runLater(() -> {
                        fxRadioButton_SFTP.setSelected(true);
                        fxRadioButton_SFTP.requestFocus();
                        Platform.runLater(() -> {
                            optionChanged();
                            Platform.runLater(() -> {
                                setDir(sftp_fileTransferHandle, true);
                            });
                        });
                    });
                    break;
            }
            else {
                // optionstr is NULL so set last LocalFileHandle or LocalRootFileHAndle
                Platform.runLater(() -> {
                    fxRadioButton_File.setSelected(true);
                    fxRadioButton_File.requestFocus();
                    Platform.runLater(() -> {
                        optionChanged();
                        Platform.runLater(() -> {
                            if (local_fileTransferHandle == null) {
                                local_fileTransferHandle = new Local_FileTransferHandle("/");//new local root fileHandle
                            }
                            setDir(local_fileTransferHandle, true);
                        });
                    });
                });
            }


        }

    }

    private void initialOptionButtons() {
        Platform.runLater(() -> {
            TransferType.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                    log.debug("Toggle group changed to:" + TransferType.getSelectedToggle());
                    Platform.runLater(() -> optionChanged());
                }
            });
            fxRadioButton_SMB.setUserData("fxRadioButton_SMB");
            fxRadioButton_File.setUserData("fxRadioButton_File");
            fxRadioButton_FTP.setUserData("fxRadioButton_FTP");
            fxRadioButton_SFTP.setUserData("fxRadioButton_SFTP");
        });
    }

    private void optionChanged() {
        Toggle toggle = TransferType.getSelectedToggle();
        String option = (String) toggle.getUserData();
        if (PREF != null && option != null) {
            log.debug("store option: {}", option);
            PREF.put(KEY_REMEMBER_OPTION, option);
        }

        if (toggle == fxRadioButton_File) {
            fxLblAddress.setDisable(true);
            fxLblPort.setDisable(true);
            fxLblDomain.setDisable(true);
            fxLblUser.setDisable(true);
            fxLblPasswort.setDisable(true);
            fxBtnShowPW.setDisable(true);
            fxTxtFieldAddress.setDisable(true);
            fxTxtFieldPort.setDisable(true);
            fxTxtFieldDomain.setDisable(true);
            fxTxtFieldUser.setDisable(true);
            fxTxtFieldPasswort.setDisable(true);
            fxPassField.setDisable(true);

            //rename TextField Port to sharename and rezise
            fxLblPort.setText("Port");
            HBox.setHgrow(fxVBoxPort, null);

        } else if (toggle == fxRadioButton_FTP || toggle == fxRadioButton_SFTP) {
            fxLblAddress.setDisable(false);
            fxLblPort.setDisable(false);
            fxLblDomain.setDisable(true);
            fxLblUser.setDisable(false);
            fxLblPasswort.setDisable(false);
            fxBtnShowPW.setDisable(false);
            fxTxtFieldAddress.setDisable(false);
            fxTxtFieldPort.setDisable(false);
            fxTxtFieldDomain.setDisable(true);
            fxTxtFieldUser.setDisable(false);
            fxTxtFieldPasswort.setDisable(false);
            fxPassField.setDisable(false);
        } else if (toggle == fxRadioButton_SMB) {
            fxLblAddress.setDisable(false);
            fxLblPort.setDisable(false);
            fxLblDomain.setDisable(false);
            fxLblUser.setDisable(false);
            fxLblPasswort.setDisable(false);
            fxBtnShowPW.setDisable(false);
            fxTxtFieldAddress.setDisable(false);
            fxTxtFieldDomain.setDisable(false);
            fxTxtFieldUser.setDisable(false);
            fxTxtFieldPasswort.setDisable(false);
            fxPassField.setDisable(false);
            fxTxtFieldPort.setDisable(false);

            //rename TextField Port to sharename and rezise
            fxLblPort.setText("Share name");
            HBox.setHgrow(fxVBoxPort, Priority.ALWAYS);
        }
        transferHandleChanged();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initial FileTransferChooser");

    }

    private void initialListOfFiles() {
        if (this.model != null)
            this.fxListOfFiles.setItems(this.model.getFilteredPaths());

        this.fxListOfFiles.setOnMouseClicked(this::handleDoubleClickInFilesList);
        this.fxListOfFiles.setCellFactory(e -> new FilesListCell());
        this.fxListOfFiles.getSelectionModel()
                .selectedItemProperty()
                .addListener(l -> {
                    model.setSelectedFile(selectedItem());
                    setStateLineOfSelection();
                });

        this.fxDirectoryTree.getSelectionModel()
                .selectedItemProperty()
                .addListener(l -> {
                    setStateLineOfSelection();
                });


        this.fxListOfFiles.setOnKeyPressed(this::handleEnterKeyOnSelection);
    }

    private void handleDoubleClickInFilesList(MouseEvent event) {
        if (event.getClickCount() == 2) {
            model.setSelectedFile(fxListOfFiles.getSelectionModel().getSelectedItem().fileTransferHandle);
            event.consume();
            setReturnAndClose();
        }
    }

    private FileTransferHandle selected;

    private void setStateLineOfSelection() {
        selected = selectedItem();
        if (selected != null) {
            String path = selected.path();
            fxTxtFieldState.setText(path);
        }
    }

    public FileTransferHandle selectedItem() {
        IndexedPath indexedPath = fxListOfFiles.getSelectionModel().selectedItemProperty().getValue();
        if (indexedPath != null) {
            return indexedPath.fileTransferHandle;
        }
        return ((FileHandleTreeItem) this.fxDirectoryTree.getSelectionModel().selectedItemProperty().get()).getFileTransferHandle();
    }

    private void handleEnterKeyOnSelection(KeyEvent keyevent) {
        if (KeyCode.ENTER.equals(keyevent.getCode()) && !fxListOfFiles.getSelectionModel().isEmpty()) {
            Platform.runLater(() -> this.fxBtnSelect.requestFocus());
        }
    }

    private void initialDirectoryTree() {
        log.debug("Initial directory tree");
        this.executor = Executors.newCachedThreadPool();

        this.fxDirectoryTree.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.RIGHT) {
                keyEvent.consume();
                expandSelectedItem();
            } else if (keyEvent.getCode() == KeyCode.ENTER) {
                fxOnSelectClicked(null);
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                fxOnCancelClicked(null);
                keyEvent.consume();
            }
        });

        this.fxDirectoryTree.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                mouseEvent.consume();
                expandSelectedItem();
            }
        });

        this.fxDirectoryTree.getSelectionModel().selectedItemProperty().addListener((observable, oldItem, newItem) -> {
            if (null == newItem)
                setDir(null, false);

            if (null != newItem && null == ((FileHandleTreeItem) newItem).getFileTransferHandle().path())
                setDir(null, false);

            if (null != newItem && null != ((FileHandleTreeItem) newItem).getFileTransferHandle().path()) {
                FileHandleTreeItem item = (FileHandleTreeItem) newItem;
                if (null == item.getFileTransferHandle().path())
                    setDir(null, false);
                else
                    setDir(item.getFileTransferHandle(), true);
            }
        });
        transferHandleChanged();
    }

    private void setDir(FileTransferHandle handle, boolean expand) {
        log.debug("SetDir {}", handle);
        if (handle == null || handle.getRoot() == null)
            this.root = new FileHandleTreeItem(new Local_FileTransferHandle("/"));
        else
            this.root = new FileHandleTreeItem(handle.getRoot());

        this.fxDirectoryTree.setRoot(root);
        log.debug("setDir() {}", handle);

        selectedDirectoryProperty.set(handle);

        this.fxDirectoryTree.getSelectionModel().selectedItemProperty().get();
        transferHandleChanged();
//        set state text and set Dir for file list

        listFiles(handle);

    }

    @FXML
    protected void fxOnConnectClicked(ActionEvent e) {
        if (e != null) e.consume();
        Toggle toggle = TransferType.getSelectedToggle();
        if (PREF != null) {
            PREF.put(KEY_REMEMBER_CREDENTIAL_STORE, fxCheckBoxRemember.isSelected());
        }

        if (toggle == fxRadioButton_SMB) {

            String servername = fxTxtFieldAddress.getText();
            String sharename = fxTxtFieldPort.getText();
            String username = fxTxtFieldUser.getText();
            String domain = fxTxtFieldDomain.getText();
            String password = fxPassField.getText();

            Credentials cred = null;
            try {
                cred = new Credentials(servername, "", sharename, username, domain, password);

                if (fxCheckBoxRemember.isSelected() && PREF != null) {
                    StoreBase storeBase = new BitStore();
                    try {
                        cred.store(storeBase);
                    } catch (NotImplementedException ex) {
                        throw new RuntimeException(ex);
                    }
                    String credStoreString = storeBase.getBase64String();
                    PREF.put(KEY_SMB_CREDENTIAL_STORE, credStoreString);
                }

                actFTH = new SMB_FileTransferHandle(cred, "/");
            } catch (GeneralSecurityException | IOException ex) {
                actFTH = null;
                log.error("Can't connect to SMB share", e);
            }
        }
    }

    private void transferHandleChanged() {
        //if stored credentials exist, load this!
        if (PREF == null) return;
        Toggle toggle = TransferType.getSelectedToggle();
        if (toggle == fxRadioButton_SMB) {
            if (PREF.contains(KEY_SMB_CREDENTIAL_STORE)) {
                String base64Str = PREF.getString(KEY_SMB_CREDENTIAL_STORE);
                StoreBase storeBase = new BitStore(base64Str);
                try {
                    Credentials cred = new Credentials(storeBase);
                    fxTxtFieldAddress.setText(cred.getAddress());
                    fxTxtFieldPort.setText(cred.getPort());
                    fxTxtFieldUser.setText(cred.getUser());
                    fxTxtFieldDomain.setText(cred.getDomain());
                    fxPassField.setText(cred.getPassword());
                    fxTxtFieldPort.setText(cred.getShareName());
                } catch (NotImplementedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        initialChangedTransferHandle();
    }

    private void initialChangedTransferHandle() {
        Toggle toggle = TransferType.getSelectedToggle();
        if (toggle == fxRadioButton_File) {
            if (actFTH instanceof Local_FileTransferHandle) return;
            actFTH = new Local_FileTransferHandle("/");
//            Platform.runLater(() -> initDirTree());
        } else {
            actFTH = null; //only connect with connect Button
//            Platform.runLater(() -> initDirTree());
        }
    }


    private FileTransferHandle lastRead;

//    public void initDirTree() {
//        Task<Void> init = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                synchronized (root) {
//
//                    //clear file list view
//                    fxListOfFiles.itemsProperty().get().clear();
//
//                    if (actFTH != null) {
//                        if (lastRead == actFTH) return null;
//                        root.getChildren().clear();
//                        root.setValue("empty");
//                        root.setValue("root");
//                        FileTransferHandle[] rootDirectories = actFTH.getRoot().listDirs();
//                        log.debug("Root dir count:" + rootDirectories.length);
//                        for (FileTransferHandle path : rootDirectories) {
//                            // DirectoryTreeItem dirItem = new DirectoryWalker(path).read();
//                            DirectoryTreeItem dirItem = new DirectoryTreeItem(path);
//                            root.getChildren().add(dirItem);
//                        }
//                        lastRead = actFTH;
//                    } else {
//                        root.getChildren().clear();
//                        root.setValue("empty");
//                        lastRead = actFTH;
//                    }
//                }
//                return null;
//            }
//        };
//        executor.submit(init);
//    }

    private void expandSelectedItem() {
        expandItem(this.fxDirectoryTree.getSelectionModel().selectedItemProperty().get());
    }

    private void expandItem(TreeItem<?> item) {
        if (null != item)
            item.setExpanded(true);
    }

//    private void readSubDirsForSelectedItem() {
//        DirectoryTreeItem item = (DirectoryTreeItem) this.fxDirectoryTree
//                .getSelectionModel()
//                .selectedItemProperty().get();
//        if (null != item && null != item.getFullPath()) {
//
//            fxTxtFieldState.setText(item.getFullPath());
//
//            FileTransferHandle selectedPath = item.getFileTransferHandle();
//
//            if (item.getChildren().isEmpty()) {
//                Task<Void> update = runningUpdateTasks.get(selectedPath);
//                if (null == update)
//                    update = createUpdateTask(selectedPath, item);
//                startUpdate(selectedPath, update);
//            } else {
//                listFiles(selectedPath);
//            }
//            selected = selectedPath;
//
//            StoreBase storeBase = new BitStore();
//            //store last selected path to preferences
//            try {
//                FileTransferHelper.serialize(storeBase, selectedPath);
//            } catch (NotImplementedException e) {
//                throw new RuntimeException(e);
//            }
//            if (selectedPath instanceof Local_FileTransferHandle) {
//                local_fileTransferHandle = (Local_FileTransferHandle) selectedPath;
//                PREF.put(KEY_PREF_STORED_LOCAL_FILE_HANDLE, storeBase.getBase64String());
//            } else if (selectedPath instanceof SFTP_FileTransferHandle) {
//                sftp_fileTransferHandle = (SFTP_FileTransferHandle) selectedPath;
//                PREF.put(KEY_PREF_STORED_SFTP_FILE_HANDLE, storeBase.getBase64String());
//            } else if (selectedPath instanceof SMB_FileTransferHandle) {
//                smb_fileTransferHandle = (SMB_FileTransferHandle) selectedPath;
//                PREF.put(KEY_PREF_STORED_SMB_FILE_HANDLE, storeBase.getBase64String());
//            } else if (selectedPath instanceof Ftp_FileTransferHandle) {
//                ftp_fileTransferHandle = (Ftp_FileTransferHandle) selectedPath;
//                PREF.put(KEY_PREF_STORED_FTP_FILE_HANDLE, storeBase.getBase64String());
//            }
//        }
//    }

    private void listFiles(FileTransferHandle handle) {
        if (null == handle) return;
        log.debug("List Files on:" + handle.path());
        this.model = FileChooserModel.startingIn(handle, PathFilter.forFileExtension("*"));
        this.fxListOfFiles.setItems(this.model.getFilteredPaths());
    }

//    private void startUpdate(FileTransferHandle path, Task<Void> update) {
//        runningUpdateTasks.put(path, update);
//        executor.submit(update);
//    }
//
//    private Task<Void> createUpdateTask(FileTransferHandle path, DirectoryTreeItem item) {
//        return new DirectoryTreeUpdateTask(path, item, new Consumer<FileTransferHandle>() {
//            @Override
//            public void accept(FileTransferHandle fileTransferHandle) {
//                runningUpdateTasks.remove(fileTransferHandle);
//                listFiles(fileTransferHandle);
//            }
//        });
//    }

    @Override
    public void show(Object data, Stage stage) {
        log.debug("FileTransferChooser show()");
        if (PREF == null) fxCheckBoxRemember.setVisible(false);
        Platform.runLater(() -> setPwImage());
    }

    @FXML
    public void fxOnCancelClicked(ActionEvent e) {
        selected = null;
        ((Stage) fxRoot.getScene().getWindow()).close();
    }

    @FXML
    public void fxOnSelectClicked(ActionEvent e) {
        e.consume();
        setReturnAndClose();
    }

    private void setReturnAndClose() {
        //TODO set and store a return


        ((Stage) fxRoot.getScene().getWindow()).close();
    }

    @Override
    public void beforeHide() {

    }

    @Override
    public void hide() {

    }

    @Override
    public double getMinWidth() {
        return 500;
    }

    @Override
    public double getMinHeight() {
        return 500;
    }

    @FXML
    ImageView fxImageViewPW;
    boolean showPassword = false;

    @FXML
    public void fxButtonPwClicked(ActionEvent e) {
        showPassword = !showPassword;
        log.debug("Toggle PW button to: {}", showPassword);
        setPwImage();
    }

    private void setPwImage() {
        if (showPassword) {
            fxTxtFieldPasswort.setVisible(true);
            fxPassField.setVisible(false);
            if (FX_Application.INSTANCE == null || FX_Application.INSTANCE.style == null) {
                fxImageViewPW.setImage(new Image(getClass().getResourceAsStream("/icons/no_view.png"), 32, 32, true, true));
            } else {
                switch (FX_Application.INSTANCE.style) {
                    case LIGHT -> {
                        fxImageViewPW.setImage(new Image(getClass().getResourceAsStream("/icons/no_view.png"), 32, 32, true, true));
                    }
                    case DARK -> {
                        fxImageViewPW.setImage(new Image(getClass().getResourceAsStream("/icons/no_view_dark.png"), 32, 32, true, true));
                    }
                }
            }

        } else {
            fxTxtFieldPasswort.setVisible(false);
            fxPassField.setVisible(true);
            if (FX_Application.INSTANCE == null || FX_Application.INSTANCE.style == null) {
                fxImageViewPW.setImage(new Image(getClass().getResourceAsStream("/icons/view.png"), 32, 32, true, true));
            } else {
                switch (FX_Application.INSTANCE.style) {
                    case LIGHT -> {
                        fxImageViewPW.setImage(new Image(getClass().getResourceAsStream("/icons/view.png"), 32, 32, true, true));
                    }
                    case DARK -> {
                        fxImageViewPW.setImage(new Image(getClass().getResourceAsStream("/icons/view_dark.png"), 32, 32, true, true));
                    }
                }
            }

        }
        fxImageViewPW.setFitHeight(30);
        fxImageViewPW.setFitWidth(30);
    }

    @Override
    public void fxStyleChanged(FxStyles style) {


        setPwImage();
    }

    @Override
    public String getTitle() {
        return SceneTitel;
    }

    @Override
    public Node getRootNode() {
        return fxRoot;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    public FileTransferHandle getReturnValue() {
        return selected;
    }
}
