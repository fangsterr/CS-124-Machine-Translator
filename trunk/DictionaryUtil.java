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
	 * 
	 * Uncapitalizes foreign word before placing it in dictionary, although the
	 * English casing is preserved.
	 */
	public static Map<String, String> importDictionary(String filename) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = in.readLine()) != null) {
				if (line.isEmpty() || line.charAt(0) == '#')
					continue;
				StringTokenizer st = new StringTokenizer(line, "|");
				String foreign = st.nextToken().trim().toLowerCase();
				String english = st.nextToken().trim();

				map.put(foreign, english);
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
	public static void translateText(Map<String, String> dict, String inFile,
			String outFile) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(inFile));
				// foreign text
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
				// english output
			
			String fSentence;
			while ((fSentence = in.readLine()) != null) {
				if (fSentence.isEmpty()) continue;
				
				String eSentence = "";
				StringTokenizer st = new StringTokenizer(fSentence);
				while (st.hasMoreTokens()) {
					String fWord = st.nextToken().toLowerCase();
					boolean eos = false; // end of sentence
					if (fWord.charAt(fWord.length()-1) == '.') {
						// get rid of end of sentence periods
						fWord = fWord.substring(0, fWord.length()-1);
						eos = true;
					}
					String eWord = dict.get(fWord);
					if (eWord == null) eWord = fWord;
					eSentence += eWord + (eos ? "." : "") + " ";
				}
				out.write(eSentence.trim());
				out.newLine();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Map<String, String> dict = DictionaryUtil
				.importDictionary("dictionary.txt");
		DictionaryUtil.translateText(dict, "indo_input.txt", "english.txt");
	}
}
