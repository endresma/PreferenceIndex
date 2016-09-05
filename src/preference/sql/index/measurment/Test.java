//package preference.sql.index.measurment;
//
//import explicit.mdc.MetaDataCursor;
//import preference.sql.SQLEngine;
//import preference.sql.index.CachedRelation;
//import preference.sql.parser.PSQLExecutor;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
////import xxl.core.cursors.MetaDataCursor;
//
//public class Test {
//
//	public static String DBUSER = "psqldbuser";
//	static String DBPASS = "psqldbpwd"; //
//	public static String DBDRIVER = "org.postgresql.Driver";
//
//	//public static String DBURL = "jdbc:postgresql://gemini.informatik.uni-augsburg.de/psqldb";
//	public static String DBURL = "jdbc:postgresql://gemini.informatik.uni-augsburg.de:5432/jmdb";
//
//	public static void main(String[] args) {
//
//		liveDB();
//
//	}
//
//	static void liveDB() {
//		String sql;
//
//		Connection sqlCon = null;
//		try {
//			Class.forName(DBDRIVER);
//			sqlCon = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
//
//			SQLEngine sqlEng = new SQLEngine(sqlCon, DBUSER, DBPASS);
//			PSQLExecutor psqlExecutor = new PSQLExecutor(sqlEng);
//
//			sql = "select * from information_schema.tables;";
//			sql = "SELECT schemaname,relname,n_live_tup FROM pg_stat_user_tables ORDER BY n_live_tup DESC;";
//			sql = "select votes, count(*) as count from ratings group by votes order by votes desc;";
//			sql = "select * from ratings;";
//			//sql = "select * from car;"; // 300
//			//sql = "select count(*) as count, price from notebooks group by price order by price;"; // 16200
//			boolean isResultSet = psqlExecutor.execute(sql);
//			MetaDataCursor rs = null;
//			CachedRelation ct = null;
//
//			if (isResultSet) {
//
//				rs = psqlExecutor.getResultSet();
//				ct = new CachedRelation(rs);
//
//				ct.print(10);
//
//			} else {
//				System.out.println("updateCount: " + psqlExecutor.getUpdateCount());
//			}
//
//			System.out.println("OK");
//
//		} catch (
//
//		Exception e) {
//			e.printStackTrace();
//			System.err.println(e);
//			if (e instanceof SQLException)
//				System.err.println("SQL State: " + ((SQLException) e).getSQLState());
//		} finally {
//			try {
//				if (sqlCon != null && !sqlCon.isClosed())
//					sqlCon.close();
//			} catch (Exception e) {
//			}
//		}
//	}
//
//}
