package application.dao;

import java.io.File;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import application.User;

public class userDAOImpl implements userDAO {
	
	baseDao baseDao = new baseDao();
	
	public userDAOImpl() {
		
	}
	
	@Override
	public User getUser(int userID) throws SQLException {
		User user = new User();
		
		Statement getUserStatement = baseDao.connect().createStatement();
		String query = "SELECT `first_name`, `last_name`, `username`, `password`, `profile` FROM `users` WHERE `id` = '" + userID + "'";
		ResultSet rs = getUserStatement.executeQuery(query);
		
		while (rs.next()) {
			user.setFirstName(rs.getString(1));
		}
		
		return user;
	}
	
	@Override
	public boolean userExists(String firstName, String lastName) throws SQLException {
		PreparedStatement ps_userExists = baseDao.connect().prepareStatement("SELECT * FROM `users` WHERE `first_name` = ? AND `last_name` = ?");
		ps_userExists.setString(1, firstName);
		ps_userExists.setString(2, lastName);
		
		ResultSet rs_userExists = ps_userExists.executeQuery();
		if (rs_userExists.next()) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public User loginUser(String username, String password) throws SQLException {
		User currentUser = new User();
		Statement loginStatement = baseDao.connect().createStatement();
		ResultSet rs_login = loginStatement.executeQuery("SELECT * FROM `users` WHERE `username` = '" + username + "' AND `password` = '" + password + "'");
		
		if (rs_login.next()) {
			currentUser.setUserID(rs_login.getInt(1));
			return currentUser;
		}
		else {
			return null;
		}
	}
	
	@Override
	public void addUser(String firstName, String lastName, String username, String password, InputStream profileImage) throws SQLException {
		PreparedStatement ps_addUser = baseDao.connect().prepareStatement("INSERT INTO `users` (`id`, `first_name`, `last_name`, `username`, `password`, `profile`) VALUES (null,?,?,?,?,?)");
	
		if (profileImage == null) {
			System.out.println("Using default image");
		}
		else {
			System.out.println("Uploading profile: " + profileImage);
		}
		ps_addUser.setString(1, firstName);
		ps_addUser.setString(2, lastName);
		ps_addUser.setString(3, username);
		ps_addUser.setString(4, password);
		ps_addUser.setBinaryStream(5, profileImage);
		ps_addUser.execute();
		
	}
	
	@Override
	public void saveProfileChanges(String firstName, String lastName, File newProfile) {
		
	}


}