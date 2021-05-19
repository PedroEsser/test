package grupo11.diretosid;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bson.Document;

public class Utils {

	public static final DateTimeFormatter STANDARD_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static Map<String, Range> getSensorRangeFilter() {
		SQLHandler handler = new SQLHandler("jdbc:mysql://localhost:3306/projetosid", "root", "");
		ResultSet result = handler.queryDB("SELECT * FROM sensor");
		Map<String, Range> aux = new HashMap<>();

		try {
			while (result.next()) {
				aux.put(result.getString("tipo") + result.getString("idsensor"),
						new Range(result.getDouble("limiteinferior"), result.getDouble("limitesuperior")));
			}
			return aux;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ArrayList<Culture> getCulturesRangeFilter(String zona, String tipo) {
		SQLHandler handler = new SQLHandler("jdbc:mysql://localhost:3306/projetosid", "root", "");
		ResultSet result = handler.queryDB("SELECT * FROM `parametrocultura` WHERE idzona = '" + zona + "' ");
		ArrayList<Culture> aux = new ArrayList<>();
		try {
			while (result.next()) {
				aux.add(new Culture(result.getString("idcultura"), new Range(
						result.getDouble("min_" + tipo.toLowerCase()), result.getDouble("max_" + tipo.toLowerCase()))));
			}
			return aux;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ArrayList<String> getCultures(String zona) {
		ArrayList<String> res = new ArrayList<>();
		SQLHandler handler = new SQLHandler("jdbc:mysql://localhost:3306/projetosid", "root", "");
		ResultSet result = handler.queryDB("SELECT * FROM `parametrocultura` WHERE idzona = '" + zona + "' ");

		try {
			while (result.next()) {
				res.add(result.getString("idcultura"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static String standardFormat(LocalDateTime date) {
		return STANDARD_DATE_FORMAT.format(date);
	}
	
	public static LocalDateTime stringToDate(String date) {
		return LocalDateTime.parse(date, STANDARD_DATE_FORMAT);
	}

	public static double medianOf(List<Document> docs) {
		List<Double> measurements = new ArrayList<Double>();
		docs.forEach(d -> measurements.add(Double.parseDouble(d.getString("Medicao"))));
		Collections.sort(measurements);
		if (measurements.size() % 2 == 1)
			return measurements.get(measurements.size() / 2);
		return (measurements.get(measurements.size() / 2) + measurements.get(measurements.size() / 2 - 1)) / 2;
	}

	public static LinkedList<LinkedHashMap<String, String>> extractResultSet(ResultSet results) {
		LinkedList<LinkedHashMap<String, String>> rows = new LinkedList<>();
		try {
			ResultSetMetaData metadata = results.getMetaData();
			while (!results.isClosed() && results.next()) {
				LinkedHashMap<String, String> row = new LinkedHashMap<>();
				for (int i = 1; i <= metadata.getColumnCount(); i++) {
					row.put(metadata.getColumnName(i), results.getString(i));
				}
				rows.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rows;
	}

	public static double convert(String str) {
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			System.err.println("Number Formar Error!");
		}
		return 0.0;
	}
}