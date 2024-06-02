package jdbc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
	
	static {
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		String url = "jdbc:db2://localhost:50000/STUD2020";

		try (
				Connection con = DriverManager.getConnection(url, "student", "abcdef");
				Scanner ulaz = new Scanner(System.in);
		) {
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			con.setAutoCommit(false);
			
			Statement lockStmt = con.createStatement();
			lockStmt.execute("SET CURRENT LOCK TIMEOUT 5");
			
			try {
				System.out.println("Unesite upit za predmet:");
				String upit = ulaz.next();
				
				System.out.println("\nPronadjeni predmeti:");
				List<Predmet> predmeti = aPronadjiPredmete(con, upit);
				for (Predmet p : predmeti) {
					System.out.println(p);
				}
				
				System.out.println("\nUnesite identifikator predmeta: ");
				Integer idPredmeta = ulaz.nextInt();
				ulaz.nextLine(); // novi red
				
				bOdobriSeminarskeRadove(con, ulaz, idPredmeta);
				
				con.commit();
			} catch (Exception e) {
				con.rollback();
				throw e;
			} finally {
				lockStmt.execute("SET CURRENT LOCK TIMEOUT NULL");
				con.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			
			System.out.println("SQL GRESKA: " + e.getErrorCode() + ", MESSAGE: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			
			System.out.println("GRESKA: " + e.getMessage());
		}
	}
	
	private static List<Predmet> aPronadjiPredmete(Connection con, String upit) throws SQLException {
		List<Predmet> predmeti = new ArrayList<>();
		
		String sql = "SELECT ID, NAZIV FROM DA.PREDMET P WHERE LOWER(NAZIV) LIKE LOWER(?)";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, "%" + upit + "%");
		ResultSet kursor = stmt.executeQuery();
		
		while (kursor.next()) {
			Integer idPredmeta = kursor.getInt(1);
			String nazivPredmeta = kursor.getString(2);
			
			Predmet predmet = new Predmet(idPredmeta, nazivPredmeta);
			predmeti.add(predmet);
		}
		
		kursor.close();
		stmt.close();
		
		return predmeti;
	}

	private static void bOdobriSeminarskeRadove(Connection con, Scanner ulaz, Integer idPredmeta) throws SQLException, IOException {
		String sql = ucitajSQL("odobriSeminarskeRadove.sql");
		PreparedStatement stmt = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE, ResultSet.HOLD_CURSORS_OVER_COMMIT);
		stmt.setInt(1, idPredmeta);
		ResultSet kursor = stmt.executeQuery();
		
		short prethodnaGodina = -1;
		
		boolean imaRedova = false;
		while (true) {
			try {
				imaRedova = kursor.next();
			} catch (SQLException e) {
				if (-913 <= e.getErrorCode() && e.getErrorCode() <= -911) {
		            kursor.close();
		            kursor = obradiCekanje("FETCH", con, stmt, sql);
		            continue;
		        }
		        throw e;
			}
			
			if (!imaRedova) {
				break;
			}
			
			Integer indeks = kursor.getInt(1);
			String ime = kursor.getString(2);
			String prezime = kursor.getString(3);
			String naslov = kursor.getString(4);
			short skGodina = kursor.getShort(5);
			
			if (prethodnaGodina == -1) {
				prethodnaGodina = skGodina;
				System.out.println("\n=========================================");
				System.out.println("Seminarski radovi u skolskoj godini: " + prethodnaGodina);
				System.out.println("=========================================");
			}
			
			if (prethodnaGodina != skGodina) {
				con.commit();
				prethodnaGodina = skGodina;
				
				System.out.println("\n=========================================");
				System.out.println("Seminarski radovi u skolskoj godini: " + prethodnaGodina);
				System.out.println("=========================================");
			}
			
			System.out.printf("\nSeminarski rad: \"%s\" studenta %s %s.\n", naslov, ime, prezime);
		
			try {
				kursor.updateString(6, "N");
				kursor.updateRow();
			} catch (SQLException e) {
				if (-913 <= e.getErrorCode() && e.getErrorCode() <= -911) {
		            kursor.close();
		            kursor = obradiCekanje("UPDATE 1", con, stmt, sql);
		            continue;
		        }
		        throw e;
			}
			
			Savepoint s = con.setSavepoint();
			
			System.out.println("Da li zelite da odobrite seminarski rad? [da/ne]");
			String odgovor = ulaz.nextLine();
			if (odgovor.equals("da")) {
				try {
					kursor.updateString(6, "D");
					kursor.updateRow();
				} catch (SQLException e) {
					if (-913 <= e.getErrorCode() && e.getErrorCode() <= -911) {
			            kursor.close();
			            kursor = obradiCekanje("UPDATE 2", con, stmt, sql);
			            continue;
			        }
			        throw e;
				}
				
				if (cPostojiNeodobrenRad(con, indeks, skGodina, idPredmeta)) {
					System.out.println("Nije moguce odobriti seminarski rad.");
					con.rollback(s);
				}
			}
		}
		
		kursor.close();
		stmt.close();
	}
	
	private static boolean cPostojiNeodobrenRad(Connection con, Integer indeks, Short skGodina, Integer idPredmeta) throws SQLException {
		String sql = "SELECT * FROM DA.SEMINARSKI_RADOVI WHERE INDEKS = ? AND IDPREDMETA = ? AND SKGODINA < ? AND (ODOBREN = 'N' OR ODOBREN IS NULL)";
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setInt(1, indeks);
		stmt.setInt(2, idPredmeta);
		stmt.setShort(3, skGodina);
		ResultSet kursor = stmt.executeQuery();
		
		boolean imaRedova = kursor.next();
		
		kursor.close();
		stmt.close();
		
		return imaRedova;
	}
	
	private static String ucitajSQL(String datoteka) throws IOException {
		StringBuilder sql = new StringBuilder();
		Files.lines(Paths.get(".", "bin", "jdbc", datoteka)).forEach(line -> sql.append(line).append('\n'));
		return sql.toString();
	}
	
	private static ResultSet obradiCekanje(String codeHint, Connection con, Statement stmt, String sql) throws SQLException {
		System.out.printf("[%s] Objekat je zakljucan od strane druge transakcije!\n", codeHint);

	    try {
	        con.rollback();
	    } catch (SQLException e) {}

	    return stmt.executeQuery(sql);
	}
}
