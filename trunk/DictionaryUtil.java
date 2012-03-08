import java.io.*;
import java.util.*;

/**
 * Class: DictionaryUtil
 * 
 * This class is a utility to import a dictionary mapping a foreign word to an
 * English word.
 */
class DictionaryUtil {
	/**
	 * Imports from a file and returns a Map mapping foreign word Strings to
	 * English word Strings. Each line in the file should be of the format
	 * "foreign|english".
	 */
	public static Map<String, String> importDictionary(String filename) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = in.readLine()) != null) {
				if (line.isEmpty() || line.charAt(0) == '#') continue;
				StringTokenizer st = new StringTokenizer(line, "|");
				String foreign = st.nextToken().trim();
				String english = st.nextToken().trim();

				map.put(foreign, english);
				System.out.println(foreign + ", " + english);
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return map;
		}
	}

	/**
	 * 
	 */
	public static void translateText(Map<String,String> dict, String inFile,
									 String outFile) {
		try {
			BufferedReader in  = new BufferedReader(new FileReader(inFile));
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
			// out.write()
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
