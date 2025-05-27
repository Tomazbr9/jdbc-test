package entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import db.DB;

public class Product {
	
	public void all() {
		
		Connection conn = null;
		Statement statement = null;
		ResultSet result = null;
		
		try {
			
			conn = DB.getConnection();
			statement = conn.createStatement();
			result = statement.executeQuery("SELECT * FROM produto");
			
			while(result.next()) {
				System.out.println(result.getInt("id") + ", " + result.getString("nome"));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			DB.closeStatement(statement);
			DB.closeResultSet(result);
			DB.closeConnection();
		}
	}
	
	public void insert(String name, double price) {
		String sql = "INSERT INTO produto (nome, preco) VALUES (?, ?)";
		Connection conn = null;
		PreparedStatement statement = null;
		// PAREI AQUI
	}
	
}
