//Main.java
//Félix Rondeau Félix Charboneau
package TP3;

import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.*;


import java.io.*;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
            File file = new File("src/main/java/TP3/test.txt");
            Util.encryptFile(file);
            Util.decryptFile(new File("C:\\Users\\6223134\\IdeaProjects\\TP2\\src\\main\\java\\TP3\\test.hd"));

            // Création de la barre de menu
            MenuBar menuBar = new MenuBar();

            // Menu Fichier
            Menu fileMenu = new Menu("Fichier");

            MenuItem encodeMenuItem = new MenuItem("Encoder");
            MenuItem decodeMenuItem = new MenuItem("Décoder");
            SeparatorMenuItem separator = new SeparatorMenuItem();
            MenuItem exitMenuItem = new MenuItem("Quitter");

            // Ajouter les sous-menus au menu Fichier
            fileMenu.getItems().addAll(encodeMenuItem, decodeMenuItem, separator, exitMenuItem);

            // Action pour quitter
            exitMenuItem.setOnAction(e -> primaryStage.close());

            // Menu Aide
            Menu helpMenu = new Menu("Aide");

            MenuItem aboutMenuItem = new MenuItem("À propos");
            MenuItem squirrelMenuItem = new MenuItem("Squirrel Wingsuit");

            // Ajouter les sous-menus au menu Aide
            helpMenu.getItems().addAll(aboutMenuItem, squirrelMenuItem);

            // Ajouter les menus à la barre de menu
            menuBar.getMenus().addAll(fileMenu, helpMenu);

            // Créer une disposition principale
            BorderPane root = new BorderPane();
            root.setTop(menuBar);

            // Créer la scènene et configurer la fenêtre principale
            Scene scene = new Scene(root, 600, 500);

            //Action:

            // Ajouter un événement pour "à propos"
            aboutMenuItem.setOnAction(e -> showAboutInfo());

            // Ajouter un événement pour Squirriel
            squirrelMenuItem.setOnAction(e -> showSquirriel());

            // Ajouter un événement pour encoder
            encodeMenuItem.setOnAction(e -> showEncode());

            // Ajouter un événement pour decoder
            decodeMenuItem.setOnAction(e -> showDecode());


            // Configurer le Stage
            primaryStage.setScene(scene);
            primaryStage.show();
        }


    /***
     * Montrer le à propos.
     */
    private void showAboutInfo() {
        Label label = new Label("Ce programme a été conçu par Félix² dans le cadre du TP3 du cours de programation 3\\ndu cégep de Lanaudière.\\n(Félix Charboneau et Félix Rondeau)");
        Pane paneInfo = new Pane(label);

    }

    /***
     * Montrer le petit écureuil.
     */
    private void showSquirriel() {
        Pane paneSquirriel = new Pane();
    }

    /***
     * Action pour encoder.
     */
    public void showEncode() {

    }

    /***
     * Action pour décoder
     */
    public void showDecode() {
    }




    public static void main(String[] args) {
        launch();

    }



}