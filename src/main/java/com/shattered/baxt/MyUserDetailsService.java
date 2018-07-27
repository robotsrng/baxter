package com.shattered.baxt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("user")
public class MyUserDetailsService implements UserDetailsService {

	public MyUserDetailsService() {
		connectDB();
	}

	// SQL Server Connection information

	private static Connection dbCon = null ;
	private final String dbURL =  "jdbc:mysql://localhost:3306/mysql" ; 
	private final String username = "root"; 
	private final String password = "toor" ;

	
	
	@Override
	public UserDetails loadUserByUsername(final String username)
			throws UsernameNotFoundException {
		return toUserDetails(username);
	}
	
	
	private UserDetails toUserDetails(String username) {
		List<String> userDetails = getUserDetails(username);
		MyUser tempUser = new MyUser(userDetails.get(0), userDetails.get(1), userDetails.get(2), userDetails.get(3));
		return tempUser ;
	}

	public static List<String> getUserDetails(String username) {
		Statement stmt = null ;
		List<String> userDetails = new ArrayList<>();
		try {
			stmt = dbCon.createStatement();
			String sql = "SELECT user_id, username, password FROM spring_security.user WHERE `username` = '" + username + "' ;";
			ResultSet rs = stmt.executeQuery(sql) ;
			if(rs.next()) {
				userDetails.add(rs.getString(1));
				userDetails.add(rs.getString(2));
				userDetails.add(rs.getString(3));
				sql = "SELECT role FROM spring_security.user_role WHERE `username` = '" + username + "' ;";
				rs = stmt.executeQuery(sql) ;
				rs.next();
				userDetails.add(rs.getString(1));
				return userDetails;
			}else {return null;}
		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}

	//*******************************************
	// USER CHECKS AND SECURITY
	//*******************************************


	//	public User getUserByUsername(String username) { 
	//		ArrayList<User> MyUsers = getAllUsers() ;
	//		for(User user : MyUsers) {                 
	//			if(user.getUsername().equals(username)) {
	//				return user ;
	//			}
	//		}
	//		return null ;
	//	}

	//	public static ArrayList<User> getAllUsers() { 
	//		Statement stmt = null ;
	//		ArrayList<User> users = new ArrayList<User>() ;
	//		try {
	//			stmt = dbCon.createStatement();
	//			String sql = "SELECT * FROM spring_security.user ;";
	//			ResultSet rs = stmt.executeQuery(sql) ;
	//			while(rs.next()) { 
	//				User tempUser = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
	//				users.add(tempUser);
	//			}
	//		} catch (SQLException e) {
	//			e.printStackTrace();
	//		}
	//		return users ; 
	//	} 

	//*******************************************
	// DB Connection Methods
	//*******************************************

	public boolean checkConnection() { 
		try {
			return dbCon.isValid(2) ;
		} catch (SQLException se) {
			se.printStackTrace();
			return false ;
		} catch( NullPointerException e) {
			return false ;
		}
	}

	public void connectDB() {
		try {
			dbCon = DriverManager.getConnection(dbURL, username, password) ;
			System.out.println("Connected") ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//*******************************************
	// USER CREDENTIAL HANDLER METHODS
	//*******************************************

	public static boolean createNewUser(String username, String pass, String email) {
		PasswordEncoder passEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		PreparedStatement stmt = null ;
		try {
			stmt = dbCon.prepareStatement("INSERT INTO spring_security.user VALUES(user_id, ?, ?, ?, '1') ;");
			stmt.setString(1, username);
			stmt.setString(2, passEncoder.encode(pass));
			stmt.setString(3, email);
			stmt.executeUpdate() ;
			String idQry = "SELECT `user_id` FROM spring_security.user WHERE `username` = '" + username + "' ;";
			ResultSet rs = stmt.executeQuery(idQry) ;
			rs.next();
			int userID = rs.getInt(1);
			stmt = dbCon.prepareStatement("INSERT INTO spring_security.user_role VALUES(?, ?, ?) ;");
			stmt.setInt(1, userID);
			stmt.setString(2, username);
			stmt.setString(3, "USER");
			stmt.executeUpdate() ;
			createSchema(username);
		} catch (SQLException e) {
			e.printStackTrace();
			return false ;
		} 
		return true ;
	}

	private static void createSchema(String username) {
		PreparedStatement stmt = null ;
		try {
			String sql = "CREATE SCHEMA IF NOT EXISTS `" + username + "`" ;
			stmt = dbCon.prepareStatement(sql);
			stmt.executeUpdate() ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean doesUserExist(String username) {
		Statement stmt = null ;
		try {
			stmt = dbCon.createStatement();
			String sql = "SELECT `username` FROM spring_security.user WHERE `username` = '" + username + "' ;";

			ResultSet rs = stmt.executeQuery(sql) ;
			if(!rs.next()) { 
				return false ;
				//TODO add second check here to see if there is a schema created.
			}else {
				System.out.println("User Aleady Exists");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return true ;
		}
	}

	public static String getUserAuthority(String username) {  
		PreparedStatement stmt = null ;
		try {
			stmt = dbCon.prepareStatement("select user.username, role from spring_security.user inner join spring_security.user_role on(user.user_id=user_role.user_id) inner join spring_security.role on(user_role.role_id=role.role_id) where user.username= ?") ;
			stmt.setString(1, username); 
			ResultSet rs = stmt.executeQuery() ; 
			if(rs.next()) { 
				return rs.getString(2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("NULL AUTH");
		return null ;
	}

	public static void setUserAuthority(String username, String auth) { 
		if(!auth.equals("1") && !auth.equals("2")) { 
			return ;
		}
		PreparedStatement stmt = null ;
		try {
			stmt = dbCon.prepareStatement("update spring_security.user inner join spring_security.user_role on(user.user_id=user_role.user_id) inner join spring_security.role on(user_role.role_id=role.role_id) set user_role.role_id = ? where user.username= ? ;") ;
			stmt.setInt(1, Integer.valueOf(auth));
			stmt.setString(2, username); 
			stmt.executeUpdate() ; 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ;
	}

	//	public static User getUser(String username) {
	//		Statement stmt = null ;
	//		try { 
	//			stmt = dbCon.createStatement();
	//			String sql = "SELECT username, password, enabled FROM spring_security.users WHERE `username` = ?";
	//			ResultSet rs = stmt.executeQuery(sql) ;
	//			if(!rs.next()) { 
	//				return null ;
	//				//TODO add second check here to see if there is a schema created.
	//			}else {
	//				User user = new User();
	//				user.setName(rs.getString(1));
	//				user.setPassword(rs.getString(2));
	//				return user ;
	//			}
	//		} catch (SQLException e) {
	//			e.printStackTrace();
	//			return null;
	//		}
	//	}

	//	public static List<User> findUser(String search) {
	//		PreparedStatement stmt = null ;
	//		List<User> users = new ArrayList<>() ;
	//		try { 
	//			stmt = dbCon.prepareStatement("SELECT username FROM spring_security.user WHERE `username` LIKE '%" + search + "%' ;") ;
	//			ResultSet rs = stmt.executeQuery() ;
	//			while(rs.next()) {
	//				users.add(getUser(rs.getString(1))) ;
	//			}
	//		} catch (SQLException e) {
	//			e.printStackTrace();
	//			return null;
	//		}
	//		return users;
	//	}

	public static List<String> findUserFiles(String username) {
		List<String> files = new ArrayList<>() ;
		PreparedStatement stmt = null ;
		try {
			stmt = dbCon.prepareStatement("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=?");
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery() ;
			while(rs.next()) {
				files.add(rs.getString(1));
			}
			return files ;
		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}

	public String getHashedPass(String user) {
		PreparedStatement stmt = null ;
		try {
			stmt = dbCon.prepareStatement("SELECT password FROM spring_security.user WHERE `username` = ? ;");
			stmt.setString(1,  user);
			ResultSet rs = stmt.executeQuery() ;
			rs.next() ;
			System.out.println("GET HASHED PASS : " + rs.getString(1));
			return rs.getString(1) ;
		}catch(SQLException se) {
			System.out.println("No user with that username.");
			return null;
		}catch(Exception e) { 
			e.printStackTrace(); 
			return null ; 
		}

	}

	public static boolean changePassword(String user, String oldPassword, String newPassword) {
		PreparedStatement stmt = null ;
		try {
			stmt = dbCon.prepareStatement("UPDATE spring_security.user SET password = ? WHERE username = ?  ;");
			stmt.setString(1, newPassword);
			stmt.setString(2, user);
			System.out.println(stmt) ;
			stmt.executeUpdate() ;
			System.out.println("Password changed for user: " + user) ;
			//JOptionPane.showMessageDialog(null, "Password changed for user: " + user) ;
		} catch (SQLException e) {
			e.printStackTrace();
			return false ;
		}
		return true ;
	}

	public static void deleteUser(String user) {
		PreparedStatement stmt = null ;
		try {
			stmt = dbCon.prepareStatement("delete from spring_security.user WHERE username = ? ;" ); 
			stmt.setString(1, user);
			stmt.executeUpdate() ;
			stmt = dbCon.prepareStatement("delete from spring_security.user_role WHERE username = ? ;" );
			stmt.setString(1, user);
			stmt.executeUpdate() ;
			stmt = dbCon.prepareStatement("DROP SCHEMA " + user + "  ;" );
			stmt.executeUpdate() ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteUserTable(String user, String filename) {
		PreparedStatement stmt = null ;
		try {
			stmt = dbCon.prepareStatement("DROP TABLE " + user + "." + filename + " ;" ); 
			stmt.executeUpdate() ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteUserRecord(String user, String filename, ArrayList<String> record) {
		for(String st : record) { 
			System.out.println(st);
		}
		PreparedStatement stmt = null ;
		try {
			stmt = dbCon.prepareStatement("DELETE FROM " + user + "." + filename + " where id = ? ");
			stmt.setInt(1, Integer.valueOf(record.get(0).replaceFirst(Pattern.quote("["), "")));
			System.out.println(stmt);
			stmt.executeUpdate() ;
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	//	//*******************************************
	//	// USER OBJECT CLASS
	//	//*******************************************
	//
	//	private static class UserObject {
	//		private String name;
	//		private String password;
	//		private String email;
	//		private String role;
	//
	//		public UserObject(String name, String password, String email, String role) {
	//			this.name = name;
	//			this.password = password;
	//			this.email = email;
	//			this.role = role;
	//		}
	//	}



}