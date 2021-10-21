package application.controllers;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javax.imageio.ImageIO;

import application.Model;
import application.controllers.add.newColumnController;
import application.controllers.add.newProjectController;
import application.controllers.add.newTaskController;
import application.controllers.edit.checklistController;
import application.controllers.edit.editColumnController;
import application.controllers.edit.editProjectController;
import application.controllers.edit.editTaskController;
import application.domains.ActionItem;
import application.domains.Checklist;
import application.domains.Column;
import application.domains.Project;
import application.domains.Task;
import application.domains.User;
import application.dao.projectDAO;
import application.dao.userDAO;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.awt.image.BufferedImage;

public class dashboardController {
	
	String[] quotes = {"You are epic smart", "I owe you kfc", "You are a chad", "You are epic cool", "You smell nice today"};

	private Stage stage;
	
	@FXML
	public TabPane tabpane_mainTab;
	
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
    private Label label_userInfo;
    
    @FXML
    private ImageView imageView_Profile;
    
    @FXML
    private File newProfile;
    
    private InputStream newProfileStream;
	
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
    	FileChooser.ExtensionFilter png_Filter = new FileChooser.ExtensionFilter("png images (*.png)", "*.png");
    	FileChooser.ExtensionFilter jpg_Filter = new FileChooser.ExtensionFilter("jpg images (*.jpg)", "*.jpg");
    	FileChooser.ExtensionFilter gif_Filter = new FileChooser.ExtensionFilter("gif images (*.gif)", "*.gif");
    	fileChooser.getExtensionFilters().addAll(png_Filter, jpg_Filter, gif_Filter);
    	
    	fileChooser.setTitle("Select image");
    	newProfile = fileChooser.showOpenDialog(stage);
    	System.out.println("File chosen: " + newProfile);

    	try {
    		InputStream fileInputStream = new FileInputStream(newProfile);
    		Image selectedImage = new Image(fileInputStream);
    		System.out.println("Chosen image " + selectedImage);
    		
    		imageView_Profile.setImage(selectedImage);
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
		
		label_userInfo.setText("Username: " + currentUser.getUsername());
		
		newProfileStream = currentUser.getProfilePicture();
		Image profile = new Image(newProfileStream);
		imageView_Profile.setImage(profile);
		
		
	}
	
    //Get the input fields and update the user details with them.
	public void saveProfileChanges(ActionEvent event) throws Exception {  	
		System.out.println("Saving changes to " + currentUser.getUserID() + " " + currentUser.getFirstName());
		
		//Save changes but no new profile picture.
		if (newProfile == null) {
			System.out.println("Saving changes with no new profile picture.");
			
			BufferedImage bImage = SwingFXUtils.fromFXImage(imageView_Profile.getImage(), null);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			try {
			    ImageIO.write(bImage, "png", outputStream);
			    byte[] res  = outputStream.toByteArray();
			    InputStream inputStream = new ByteArrayInputStream(res);
			    model.getUserDAO().saveProfileChanges(currentUser.getUserID(), txtFieldFName.getText(), txtFieldLName.getText(), inputStream);
			} catch (IOException e) {
			    e.printStackTrace();
			}
			
		//Save changes with new profile picture.
		}
		else {
			System.out.println("Saving changes and uploading new profile picture.");
			BufferedInputStream newProfileUpload = new BufferedInputStream(new FileInputStream(newProfile));
			model.getUserDAO().saveProfileChanges(currentUser.getUserID(), txtFieldFName.getText(), txtFieldLName.getText(), newProfileUpload);
		}
				
		//Refresh dashboard on this tab.
		FXMLLoader dashboardScene = new FXMLLoader(getClass().getResource("/application/views/Dashboard.fxml"));
    	Parent root = dashboardScene.load();
    	dashboardController dashboardController = dashboardScene.getController();    	     
    			        
    	User tmpUser = model.getUserDAO().getUser(currentUser.getUserID());
    	
    	//Apply parameters to dashboard controller so appropriate name and projects are shown.
    	dashboardController.setUserID(tmpUser.getUserID());
    	dashboardController.setWelcomeMessage(tmpUser.getFirstName());
    	dashboardController.setQuote();
    	dashboardController.showProjects(currentUser.getUserID());
    	dashboardController.loadUser(tmpUser);
    	dashboardController.tabpane_mainTab.getSelectionModel().select(2);
		
    	//Load the dashboard.
    	stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();		
	}
	
	
	
	//Show all of the user's projects.
	public void showProjects(int userID) throws Exception {
		//Load all projects into userProjects ArrayList.
		ArrayList<Project> userProjects = model.getProjectDAO().loadProjects(userID);
						
		for (Project project : userProjects) {
							
			//Create a tab for each project.
			Tab tab_project = new Tab(project.getProjectName());
			ScrollPane scrollPane = new ScrollPane();
						
			Pane pane_tabContent = new Pane();

			Button btn_editProject = new Button("Edit Project");
			btn_editProject.setLayoutX(10);
			btn_editProject.setLayoutY(10);
			
			Button btn_newColumn = new Button("New Column");
			btn_newColumn.setLayoutX(110);
			btn_newColumn.setLayoutY(10);

			Button btn_deleteProject = new Button("Delete Project");
			btn_deleteProject.setLayoutX(220);
			btn_deleteProject.setLayoutY(10);
			
			
			//Add behaviour to edit project button (go to edit project page).
			btn_editProject.setOnAction(new EventHandler<ActionEvent>() {
			
				@Override
				public void handle(ActionEvent arg0) {
					System.out.println("Edit project " + project.getProjectName());
					
					//Prepare new project scene.
					FXMLLoader editProjectScene = new FXMLLoader(getClass().getResource("/application/views/EditProject.fxml"));
					
					Parent root;
					try {
						root = editProjectScene.load();
						//Apply parameters to the newcolumn scene.
						
						editProjectController editProjectController = editProjectScene.getController();
						editProjectController.loadProject(project);
						
						//Load the new project window.
						stage = (Stage)((Node)arg0.getSource()).getScene().getWindow();
						Scene scene = new Scene(root);
						stage.setScene(scene);
						stage.show();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					

				}
			
			});
			
			btn_newColumn.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent arg0) {
					//Print log of opening add project window.
					System.out.println("Opening add task window.");
					System.out.println("User ID: " + userId);
					System.out.println("Project ID: " + project.getProjectID());
					
					
					try {
						

						//Prepare new project scene.
						FXMLLoader newColumnScene = new FXMLLoader(getClass().getResource("/application/views/NewColumn.fxml"));
						
						Parent root = newColumnScene.load();
						
						//Apply parameters to the newcolumn scene.
						newColumnController newColumnController = newColumnScene.getController();
						newColumnController.setHeading(project.getProjectName());
						newColumnController.setProjectID(project.getProjectID());
						newColumnController.setUserID(userID);
						//Load the new project window.
						stage = (Stage)((Node)arg0.getSource()).getScene().getWindow();
						Scene scene = new Scene(root);
						stage.setScene(scene);
						stage.show();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					


				}
				
			});
			
			
			//Add behaviour to delete project button. 
			btn_deleteProject.setOnAction(new EventHandler<ActionEvent>() {
				
				//Delete project.
				@Override
				public void handle(ActionEvent arg0) {
					
					Alert alertDeleteProject = new Alert(AlertType.CONFIRMATION);
					alertDeleteProject.setTitle("Delete project " + project.getProjectName() + "?");
					alertDeleteProject.setHeaderText("Are you sure you want to delete project " + project.getProjectName() + "?");
					Optional<ButtonType> choice = alertDeleteProject.showAndWait();
					
					if (choice.isPresent() && choice.get() == ButtonType.OK) {
						//Deletes the project and notifies the user.
						try {
							model.getProjectDAO().deleteProject(project.getProjectID());
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();							
						}					
						
							        	
			            //Refreshes the page on the same tab.
			        	try {
			        		refresh(arg0, userID);	
			        		System.out.println("Refreshed projects tab.");
						} 
			        	
			        	catch (NullPointerException e) {
							System.out.println("0 projects under user " + currentUser.getUsername());
							System.out.println("Error: " + e);
						}
			        	catch (Exception e) {
			        		System.out.println("Error: " + e);
						}			        	
					}				
				}
			});
			
			HBox hboxProjects = new HBox(50);
			
			ArrayList<Column> columns = model.getProjectDAO().loadColumns(project.getProjectID());
			ArrayList<VBox> vboxColumns = new ArrayList<VBox>();
			
			if (columns.isEmpty()) {
				System.out.println("No columns under user " + currentUser.getUsername());
			}
			else {
				

				
				
				for (Column column : columns) {
					
					VBox vboxColumn = new VBox(10);
					vboxColumn.setMaxWidth(250);
					Pane columnDetailsPane = new Pane();

					
					
					Label lbl_columnTitle = new Label("Name: " + column.getColumn_name());
					Label lbl_description = new Label("Description: \n" + column.getDescription());
					lbl_description.setWrapText(true);			
					Label lbl_date = dateDifference(column.getDue_date(), model.getProjectDAO().tasksCompleted(column.getColumnID()));
					
					
					Button btn_editColumn = new Button("Edit column");
					Button btn_deleteColumn = new Button("Delete column");
					columnDetailsPane.getChildren().addAll(lbl_columnTitle, lbl_description, lbl_date, btn_editColumn, btn_deleteColumn);
					
					btn_editColumn.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0)  {
							System.out.println("Edit column " + column.getColumn_name());
							
							//Prepare new project scene.
							FXMLLoader editColumnScene = new FXMLLoader(getClass().getResource("/application/views/EditColumn.fxml"));
							
							Parent root;
							try {
								root = editColumnScene.load();
								//Apply parameters to the newcolumn scene.
								editColumnController editColumnController = editColumnScene.getController();
								editColumnController.loadColumn(column);
								editColumnController.setUserID(userID);
								
								//Load the new project window.
								stage = (Stage)((Node)arg0.getSource()).getScene().getWindow();
								Scene scene = new Scene(root);
								stage.setScene(scene);
								stage.show();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
						}
					});
					
					btn_deleteColumn.setOnAction(new EventHandler<ActionEvent>() {
						
						@Override
						public void handle(ActionEvent arg0) {
							System.out.println("Delete column " + column.getColumn_name());
							Alert alertDeleteColumn = new Alert(AlertType.CONFIRMATION);
							alertDeleteColumn.setTitle("Delete column " + column.getColumn_name() + "?");
							alertDeleteColumn.setHeaderText("Are you sure you want to delete column " + column.getColumn_name() + "?");
							Optional<ButtonType> choice = alertDeleteColumn.showAndWait();
							
							if (choice.isPresent() && choice.get() == ButtonType.OK) {
								try {
									
									model.getProjectDAO().deleteColumn(column.getColumnID());
									System.out.println("Column " + column.getColumnID() + " deleted.");
									
									refresh(arg0, userID);
									
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									System.exit(0);
								}
							}
							
						}
					
					});
					
					
					HBox hbox_columnBtns = new HBox(5);
					hbox_columnBtns.getChildren().addAll(btn_editColumn, btn_deleteColumn);
					
					Label lbl_tasksHeading = new Label("Tasks");
					lbl_tasksHeading.setContentDisplay(ContentDisplay.CENTER);
					
					
					ArrayList<Pane> taskPanes = new ArrayList<Pane>();
					
					
					VBox vboxTasks = new VBox(3);
					ArrayList<Task> tasks = model.getProjectDAO().loadTasks(column.getColumnID());

					if (tasks.isEmpty()) {
						System.out.println("No tasks under user " + currentUser.getUsername());
					}
					else {
						for (Task task : tasks) {
							Checklist checklist = model.getProjectDAO().loadChecklist(task.getTaskID());
							VBox taskVbox = new VBox(3);
							Pane taskPane = new Pane();
							Label taskName = new Label("Task name: " + task.getTaskName());
							Label taskDescription = new Label("Task description: " + task.getDescription());
							//Label taskDueDate = new Label("Task due date: " + task.getDueDate().toString());
							Label taskDueDate = dateDifference(task.getDueDate(), task.isCompleted());
							
							Label taskCompleted = new Label();
							if (task.isCompleted()) {
								taskCompleted.setText("Completed");
							}
							else {
								taskCompleted.setText("Not finished yet");
							}
							
							HBox hboxChecklistItems = new HBox(50);
							Button btn_viewChecklist = new Button("View Checklist");
							
							ArrayList<ActionItem> actionItems = model.getProjectDAO().loadActionItems(checklist.getCheckListID());
							
							
								btn_viewChecklist.setOnAction(new EventHandler<ActionEvent>() {
								
								@Override
								public void handle(ActionEvent arg0) {
									//Prepare new project scene.
									FXMLLoader checklistScene = new FXMLLoader(getClass().getResource("/application/views/Checklist.fxml"));
									
									Parent root;
									try {
										root = checklistScene.load();
										//Apply parameters to the checklist scene.										
										checklistController checklistController = checklistScene.getController();
										checklistController.loadChecklist(checklist.getCheckListID(), project.getUserID());
										
										//Load the new project window.
										stage = (Stage)((Node)arg0.getSource()).getScene().getWindow();
										Scene scene = new Scene(root);
										stage.setScene(scene);
										stage.show();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
								}
								
							});
							
							
							
							
							
							int completedActionItems = 0;
							
							for (ActionItem actionItem : actionItems) {
								if (actionItem.isCompleted()) {
									completedActionItems++;
								}
							}
							
							Label lbl_checkListDetails = new Label("");
							
							if (completedActionItems == actionItems.size() && completedActionItems > 0) {
								//All action items are complete.
								lbl_checkListDetails.setText(completedActionItems + "/" + actionItems.size());
								lbl_checkListDetails.setStyle("-fx-background-color: lightgreen;");
							}
							else if (actionItems.size() == 0) {
								lbl_checkListDetails.setText("No action\nitems");
							}
							else {
								lbl_checkListDetails.setText(completedActionItems + "/" + actionItems.size());
								lbl_checkListDetails.setStyle("-fx-background-color: lightyellow;");
							}
							

							
							lbl_checkListDetails.setTextAlignment(TextAlignment.CENTER);
							lbl_checkListDetails.setWrapText(true);
							
							
							
							
							hboxChecklistItems.getChildren().addAll(btn_viewChecklist, lbl_checkListDetails);
							
							HBox hboxTaskButtons = new HBox(3);

							Button btn_taskEdit = new Button("Edit");
							Button btn_taskDelete = new Button("Delete");
							
							btn_taskEdit.setOnAction(new EventHandler<ActionEvent>() {
								
								@Override
								public void handle(ActionEvent arg0) {
									//Prepare new project scene.
									FXMLLoader editTaskScene = new FXMLLoader(getClass().getResource("/application/views/EditTask.fxml"));
									
									Parent root;
									try {
										root = editTaskScene.load();
										//Apply parameters to the newcolumn scene.
										editTaskController editTaskController = editTaskScene.getController();
										editTaskController.loadEditTask(task, project);
										//Load the new project window.
										stage = (Stage)((Node)arg0.getSource()).getScene().getWindow();
										Scene scene = new Scene(root);
										stage.setScene(scene);
										stage.show();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
								}
								
							});
							
							
							btn_taskDelete.setOnAction(new EventHandler<ActionEvent>() {
								

								
								@Override
								public void handle(ActionEvent arg0) {
									
									Alert alertDeleteTask = new Alert(AlertType.CONFIRMATION);
									alertDeleteTask.setTitle("Delete task " + task.getTaskName() + "?");
									alertDeleteTask.setHeaderText("Are you sure you want to delete task " + task.getTaskName() + "?");
									Optional<ButtonType> choice = alertDeleteTask.showAndWait();
									
									if (choice.isPresent() && choice.get() == ButtonType.OK) {
										System.out.println("Delete task " + task.getTaskName());
										try {
											model.getProjectDAO().deleteTask(task.getTaskID());						
											System.out.println("Task " + task.getTaskID() + " deleted.");
											
											model.getProjectDAO().deleteCheckList(checklist.getCheckListID());
											refresh(arg0, userID);
											
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
											System.exit(0);
										}
									}
									

								}
							
							});
							
							
							
							hboxTaskButtons.getChildren().addAll(btn_taskEdit, btn_taskDelete);
							
							taskVbox.getChildren().addAll(taskName, taskDescription, taskDueDate, taskCompleted, hboxChecklistItems, hboxTaskButtons);
							
							
							taskPane.getChildren().addAll(taskVbox);
							taskPane.setStyle("-fx-border-color: lightgrey; -fx-background-color: white;");
							
							taskPanes.add(taskPane);
							
							
						}
					}
					
					vboxTasks.getChildren().addAll(taskPanes);
					
					Pane pane_columnDetails = new Pane();
					pane_columnDetails.setStyle("-fx-border-color: grey; -fx-padding: 3px; -fx-background-color: white;");
					
					VBox vbox_columnDetails = new VBox(3);
					vbox_columnDetails.getChildren().addAll(lbl_columnTitle, lbl_description, lbl_date, hbox_columnBtns);
					pane_columnDetails.getChildren().addAll(vbox_columnDetails);
					
					Button btn_taskAdd = new Button("Create task");
					
					
					btn_taskAdd.setOnAction(new EventHandler<ActionEvent>() {
						
						@Override
						public void handle(ActionEvent arg0) {
							System.out.println("Add task");
							
							//Prepare new project scene.
							FXMLLoader addTaskScene = new FXMLLoader(getClass().getResource("/application/views/NewTask.fxml"));
							
							Parent root;
							try {
								root = addTaskScene.load();
								//Apply parameters to the newcolumn scene.
								newTaskController newTaskController = addTaskScene.getController();
								
								newTaskController.loadAddTask(column.getColumnID(), userID);
								
								//Load the new project window.
								stage = (Stage)((Node)arg0.getSource()).getScene().getWindow();
								Scene scene = new Scene(root);
								stage.setScene(scene);
								stage.show();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					
					HBox hbox_taskHeading = new HBox(3);
					hbox_taskHeading.getChildren().addAll(lbl_tasksHeading, btn_taskAdd);
					
					
					vboxColumn.getChildren().addAll(pane_columnDetails, hbox_taskHeading, vboxTasks);
					vboxColumns.add(vboxColumn);
				}
			}

			
			hboxProjects.getChildren().addAll(vboxColumns);
			hboxProjects.setLayoutY(60);
			
			pane_tabContent.getChildren().addAll(btn_newColumn, btn_editProject, btn_deleteProject, hboxProjects);
			
			scrollPane.setContent(pane_tabContent);
			
			
			tab_project.setContent(scrollPane);
			tab_projects.getTabs().add(tab_project);
			
			if (project.isDefault()) {
				tab_projects.getSelectionModel().select(tab_project);	
			}
			
		}
	}
	
	
	public void refresh(ActionEvent arg0, int userID) throws Exception {
		
		
		
		//Prepare to load dashboard.
		FXMLLoader dashboardScene = new FXMLLoader(getClass().getResource("/application/views/Dashboard.fxml"));
    	Parent root = dashboardScene.load();
    	dashboardController dashboardController = dashboardScene.getController();
    	
    	//Prepare user details.
    	dashboardController.setUserID(userID);			     
    				        
    	User tmpUser = model.getUserDAO().getUser(userID);
    	
    	//Apply parameters to dashboard controller so appropriate name and projects are shown.
    	dashboardController.setWelcomeMessage(tmpUser.getFirstName());
    	dashboardController.setQuote();
    	dashboardController.showProjects(userID);
    	dashboardController.loadUser(tmpUser);
    	
    	
    	try {
    		dashboardController.tabpane_mainTab.getSelectionModel().select(1);
		} 
    	
    	catch (Exception e) {
			System.out.println("Error: " + e);
		}
    	
    	
    	try {
    		int selectedProjectTab = tab_projects.getSelectionModel().getSelectedIndex();
    		dashboardController.tab_projects.getSelectionModel().select(selectedProjectTab);	
		} 
    	
    	catch (NullPointerException e) {
			System.out.println("0 projects under user " + currentUser.getUsername());
			
		}
    	
    	catch (Exception e) {
			System.out.println("Error: " + e);
		}

    	
    				    				    
		
    	//Load the dashboard.
    	stage = (Stage)((Node)arg0.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	//Check the difference in days between two dates and change
	//the colour accordingly
	public Label dateDifference(Date dueDate, boolean completed) {
		
		Date currentDate = Date.valueOf(LocalDate.now()); //Get current date
		int daysBetween = (int) (dueDate.getTime() - currentDate.getTime()) / 1000 / 60 / 60 / 24; //Get due date
		Label output = new Label(dueDate.toString()); //Prepare the output
		
	
		if (daysBetween <= 7) {
			if (daysBetween < 0) {
				if (completed) {
					//Completed before
					output.setStyle("-fx-background-color: lightgreen; -fx-padding: 5px;");
				}
				else {
					//Over due
					output.setStyle("-fx-background-color: orange; -fx-padding: 5px;");
				}
				
			}
			else {
				//Due date approaching
				output.setStyle("-fx-background-color: yellow; -fx-padding: 5px;");
			}

		}
		else if (completed && daysBetween >= 0) {
			//Completed in time
			output.setStyle("-fx-background-color: lightgreen; -fx-padding: 5px;");
		}
		
		
		return output;
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
		lbl_fname.setText("Welcome " + firstName);
	}
	
	//Open the window for adding the new project.
	public void addProjectWindow(ActionEvent event) throws IOException {
		//Print log of opening add project window.
		System.out.println("Opening add project window.");
		System.out.println("User ID: " + userId);
		
		//Prepare new project scene.
		FXMLLoader newProjectScene = new FXMLLoader(getClass().getResource("/application/views/NewProject.fxml"));
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
		
	public void logout(ActionEvent event) throws IOException {
		//Print log of logging out.
		System.out.println("logging out.");
		
		//Prepare home scene.
		FXMLLoader homeScene = new FXMLLoader(getClass().getResource("/application/views/Home.fxml"));
		Parent root = homeScene.load();
		
		//Load home scene.
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}	
}