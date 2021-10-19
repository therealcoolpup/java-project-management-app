package application.controllers.add;

import java.io.IOException;


import java.sql.SQLException;

import application.Model;
import application.controllers.dashboardController;
import application.objects.User;
import application.dao.projectDAO;
import application.dao.userDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class newProjectController {
	
	private Stage stage;
	
	int userId;

	private Model model = new Model();
	
	@FXML
	private TextField textFieldProjectName;
	
	@FXML
	private CheckBox checkBox_default;
	
	@FXML
	private Label lbl_notification;
		
	
	public void setUserID(int userID) {
		this.userId = userID;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void back(ActionEvent event) throws Exception {
		System.out.println("Back to dashboard");
		
		FXMLLoader dashboardScene = new FXMLLoader(getClass().getResource("/application/views/Dashboard.fxml"));
		Parent root = dashboardScene.load();
		dashboardController dashboardController = dashboardScene.getController();
		dashboardController.setQuote();
		dashboardController.setUserID(userId);
		
		User user = model.getUserDAO().getUser(userId);
		
		dashboardController.loadUser(user);
		dashboardController.setWelcomeMessage(user.getFirstName());
		dashboardController.showProjects(userId);
		dashboardController.tabpane_mainTab.getSelectionModel().select(1);
		
		
		
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	//Add project to database
	public void addProject(ActionEvent event) throws SQLException {
		
		if (textFieldProjectName.getText() != "") {
			
			boolean isDefault = checkBox_default.isSelected();
			
			if (!model.getProjectDAO().addProject(userId, textFieldProjectName.getText(), isDefault)) {
				lbl_notification.setTextFill(Color.RED);
				lbl_notification.setText("Project already exists!");
				System.out.println(textFieldProjectName.getText() + " already exists");
			}
			else {
				lbl_notification.setTextFill(Color.GREEN);
				lbl_notification.setText("Project successfully added!");
				System.out.println(textFieldProjectName.getText() + " added to database under user id: " + userId);
			}
		}
		else {
			System.out.println("Project name hasn't been added.");
			lbl_notification.setText("Please enter the project name");
		}
		
	}
	
}