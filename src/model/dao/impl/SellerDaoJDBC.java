package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
		        "INSERT INTO seller "
				+ "(name, email, birthDate, base_salary, id_department) "
		        + "VALUES "
				+ "(?, ?, ?, ?, ? ",
				Statement.RETURN_GENERATED_KEYS
					
			);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error! No rows affected!");
			}
			
			
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
		        "UPDATE seller "
				+ "SET name = ?, email = ?, birthDate = ?, baseSalary = ? "
		        + "id_department = ? "
		        + "WHERE id = ?"		
			);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
		    st.executeUpdate();
		    System.out.println("Update Completed");
			
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
			    "SELECT seller.*, department.name as DepName "
				+ "FROM seller INNER JOIN department "
			    + "ON seller.id_department = department.id "
				+ "WHERE seller.id = ?"
			);
			
			st.setInt(1, id);
			
			rs = st.executeQuery();
			
			if(rs.next()) {
				
				Department dep = instantiateDepartment(rs);	
				Seller obj = instantiateSeller(rs, dep);
				
				return obj;
				
			}
			
			return null;
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement (
			    "SELECT seller.*, department.name as DepName "
				+ "FROM seller INNER JOIN department "
			    + "ON seller.id_department = department.id "
			    + "ORDER BY name"
			);
			
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while(rs.next()) {
				
				Department dep = map.get(rs.getInt("id_department"));
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("id_department"), dep);
				}
				
				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);
				
			}
			
			return list;
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dp = new Department();
		
		dp.setId(rs.getInt("id_department"));
		dp.setName(rs.getString("DepName"));
		
		return dp;
	}
	
	private Seller instantiateSeller(ResultSet rs, Department dp) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("id"));
		obj.setName(rs.getString("name"));
		obj.setEmail(rs.getString("email"));
		obj.setBaseSalary(rs.getDouble("base_salary"));
		obj.setBirthDate(rs.getDate("birth_date"));
		obj.setDepartment(dp);
		
		return obj;
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement (
			    "SELECT seller.*, department.name as DepName "
				+ "FROM seller INNER JOIN department "
			    + "ON seller.id_department = department.id "
				+ "WHERE id_department = ? "
			    + "ORDER BY name"
			);
			
			st.setInt(1, department.getId());
			
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while(rs.next()) {
				
				Department dep = map.get(rs.getInt("id_department"));
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("id_department"), dep);
				}
				
				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);
				
			}
			
			return list;
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("DELETE FROM seller WHERE id = ?");
			st.setInt(1, id);
			st.executeUpdate();
			System.out.println("Delete Completed");
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

}
