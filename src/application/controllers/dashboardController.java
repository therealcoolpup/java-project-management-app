package application.controllers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import application.Model;
import application.objects.Project;
import application.objects.User;
import application.dao.projectDAO;
import application.dao.userDAO;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class dashboardController {
	
	String[] quotes = {"You are epic smart", "I owe you kfc", "You are a chad"};
	
	private Stage stage;
	
	@FXML
	private TabPane tabpane_mainTab;
	
	@FXML
	private TabPane tab_projects;
	
	@FXML
	private Label lbl_fname;
	
	@FXML
	private Label lbl_inspirationalQuote;

	@FXML
	private Button btn_createProject;
		
	
    @FXML
    private TextField txtFieldFName;
    
    @FXML
    private TextField txtFieldLName;
    
    @FXML
    private TextField txtFieldusername;
    
    @FXML
    private TextField txtFieldPassword;
    
    @FXML
    private ImageView profilePic;
    
    @FXML
    private File newProfile = null;
	
	
	int userId;
	String username;
	String firstName;
	
	public User currentUser = new User();
	
	private Model model = new Model();
	
	
	
    //Open file explorer and let user choose profile image.
    public void chooseProfile(ActionEvent event) throws Exception {  	
    	
    	System.out.println("Uploading image.");
    	FileChooser fileChooser = new FileChooser();
    	
    	//Add filters so only images can be added.
        FileChooser.ExtensionFilter JPG_Filter = new FileChooser.ExtensionFilter("JPG images (*.JPG)", "*.JPG");
        FileChooser.ExtensionFilter jpg_Filter = new FileChooser.ExtensionFilter("jpg images (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter jpeg_Filter = new FileChooser.ExtensionFilter("jpeg images (*.jpeg)", "*.jpeg");
        FileChooser.ExtensionFilter PNG_Filter = new FileChooser.ExtensionFilter("PNG images (*.PNG)", "*.PNG");
        FileChooser.ExtensionFilter png_Filter = new FileChooser.ExtensionFilter("png images (*.png)", "*.png");
        
    	
    	fileChooser.getExtensionFilters().addAll(JPG_Filter, jpg_Filter, jpeg_Filter, PNG_Filter, png_Filter);
    	fileChooser.setTitle("Select image");
    	newProfile = fileChooser.showOpenDialog(stage);
    	System.out.println("File chosen: " + newProfile);

    	try {
    		InputStream fileInputStream = new FileInputStream(newProfile);
    		Image selectedImage = new Image(fileInputStream);
    		System.out.println("Chosen image " + selectedImage);
    		
    		profilePic.setImage(selectedImage);
    		System.out.println("Set " + newProfile + " as preview.");
		} catch (Exception e) {
			System.out.println("Error uploading custom profile: " + e);
			System.out.println("Image url: " + newProfile);
		}

    }
	
	
	
	//Load user details in profile tab.
	public void loadUser(User currentUser) {
		this.currentUser = currentUser;
		
		
		txtFieldFName.setText(currentUser.getFirstName());
		txtFieldLName.setText(currentUser.getLastName());
		txtFieldusername.setText(currentUser.getUsername());
		txtFieldPassword.setText(currentUser.getPassword());
		Image profile = new Image(currentUser.getProfilePicture());
		profilePic.setImage(profile);
		
		
	}
	
    //Get the input fields and update the user details with them.
	public void saveProfileChanges(ActionEvent event) throws Exception {  	
		System.out.println("Saving changes to " + currentUser.getUserID() + " " + currentUser.getFirstName());
		
		if (newProfile == null) {
			System.out.println("Saving changes with no new profile picture.");
			model.getUserDAO().saveProfileChanges(currentUser.getUserID(), txtFieldFName.getText(), txtFieldLName.getText(), txtFieldPassword.getText(), currentUser.getProfilePicture());
		}
		else {
			System.out.println("Saving changes and uploading new profile picture.");
			BufferedInputStream newProfileStream = new BufferedInputStream(new FileInputStream(newProfile));
			model.getUserDAO().saveProfileChanges(currentUser.getUserID(), txtFieldFName.getText(), txtFieldLName.getText(), txtFieldPassword.getText(), newProfileStream);
		}
		
		
	}
	
	
	
	//Show all of the user's projects.
	public void showProjects(int userID) throws Exception {
		//Load all projects into userProjects ArrayList.
		ArrayList<Project> userProjects = model.getProjectDAO().loadProjects(userID);
						
		for (Project project : userProjects) {
			
			//Create a tab for each project.
			Tab tab = new Tab(project.getProjectName());
			ScrollPane scrollPane = new ScrollPane();
			tab.setContent(scrollPane);
			Pane tabContent = new Pane();
			
			Label lbl_notification = new Label();
			lbl_notification.setLayoutX(100);
			lbl_notification.setLayoutY(100);
			Button deleteButton = new Button("Delete Project");
			deleteButton.setLayoutX(10);
			deleteButton.setLayoutY(10);
			
			//Add behaviour to delete button
			deleteButton.setOnAction(new EventHandler<ActionEvent>() {
				
				//Delete project.
				@Override
				public void handle(ActionEvent arg0) {
					
					//Deletes the project and notifies the user.
					try {
						model.getProjectDAO().deleteProject(project.getProjectID());
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}					
					lbl_notification.setText(project.getProjectName() + " has been deleted!");
						        	
		            //Refreshes the page on the same tab.
		        	try {
		        		//Prepare to load dashboard.
		        		FXMLLoader dashboardScene = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
			        	Parent root = dashboardScene.load();
			        	dashboardController dashboardController = dashboardScene.getController();
			        	
			        	//Prepare user details.
			        	dashboardController.setUserID(userID);			     
			        				        
			        	User tmpUser = model.getUserDAO().getUser(userID);
			        	
			        	//Apply parameters to dashboard controller so appropriate name and projects are shown.
			        	dashboardController.setWelcomeMessage(tmpUser.getFirstName());
			        	dashboardController.setQuote();
			        	dashboardController.showProjects(userID);
			        	dashboardController.tabpane_mainTab.getSelectionModel().select(1);
						
			        	//Load the dashboard.
			        	stage = (Stage)((Node)arg0.getSource()).getScene().getWindow();
						Scene scene = new Scene(root);
						stage.setScene(scene);
						stage.show();
						
			        	
					} catch (Exception e) {
						System.out.println("Error: " + e);
					}
				}
			});
			
			
			//Display delete button.
			lbl_notification.setText("fat");
			tabContent.getChildren().addAll(lbl_notification, deleteButton);
			tab.setContent(tabContent);
			tab_projects.getTabs().add(tab);
		}
		
		

	}
	
	//Creates an inspirational quote.
	public void setQuote() {
		int randomInt = new Random().nextInt(quotes.length);
		lbl_inspirationalQuote.setText(quotes[randomInt]);
	}
	
	public void setUserID(int userID) {
		this.userId = userID;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getFirstName() {
		return firstName;
	}
		
	public void setWelcomeMessage(String firstName) {
		lbl_fname.setText(firstName);
	}
	
	
	//Open the window for adding the new project.
	public void addProjectWindow(ActionEvent event) throws IOException {
		//Print log of opening add project window.
		System.out.println("Opening add project window.");
		System.out.println("User ID: " + userId);
		
		//Prepare new project scene.
		FXMLLoader newProjectScene = new FXMLLoader(getClass().getResource("NewProject.fxml"));
		Parent root = newProjectScene.load();
		
		//Apply parameters to the newproject scene.
		newProjectController newProjectController = newProjectScene.getController();
		newProjectController.setUserID(userId);
		
		//Load the new project window.
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();

	}
	

	
	public void saveProjectChanges(ActionEvent event, int projectID, String projectName) {
		
	}
	
	public void deleteProject(ActionEvent event, int projectID) {
		
	}
	
	public void logout(ActionEvent event) throws IOException {
		//Print log of logging out.
		System.out.println("logging out.");
		
		//Prepare home scene.
		FXMLLoader homeScene = new FXMLLoader(getClass().getResource("Home.fxml"));
		Parent root = homeScene.load();
		
		//Load home scene.
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	
	
	public void addTaskColumn(ActionEvent event, int taskID, int projectID, String taskName, Date dueDate, String description) {
		
	}
	
	public void saveTaskColumnChanges(ActionEvent event,String taskName, Date dueDate, String description) {
		
	}
	
	public void deleteTaskColumn(ActionEvent event, int taskID) {
		
	}
	
	
	
	public void addTask(ActionEvent event, int taskID, String description, boolean completed) {
		
	}
	
	public void saveTaskChanges(ActionEvent event, String description, boolean completed) {
		
	}
	
	public void deleteTask(ActionEvent event, int taskItemID) {
		
	}
	
}