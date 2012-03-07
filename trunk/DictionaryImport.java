import java.io.*;
import java.util.*;

/**
 * Class: DictionaryImport
 * 
 * This class is a utility to import a dictionary mapping a foreign word to an
 * English word.
 */

/**
 * Imports from a file and returns a Map mapping foreign word Strings to English
 * word Strings.
 * Each line in the file should be of the format "foreign|english".
 */
public class DictionaryImport {
	public static Map<String, String> importDictionary(String filename) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			HashMap<String, String> map = new HashMap<String, String>();
			String line;
			while ((line = in.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, "|");
				String foreign = st.nextToken().trim();
				String english = st.nextToken().trim();
				
				map.put(foreign, english);
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
