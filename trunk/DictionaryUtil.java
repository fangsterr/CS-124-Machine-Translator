import java.io.*;
import java.util.*;

/**
 * Class: DictionaryUtil
 * 
 * This class is a utility to import a dictionary mapping a foreign word to an
 * English word.
 */
class DictionaryUtil {
	/** CONSTANTS **/
	private static final char TAGGED_DELIM = '_';

	/**
	 * Imports from a file and returns a Map mapping foreign word Strings to
	 * English word Strings. Each line in the file should be of the format
	 * "foreign|english".
	 * 
	 * Uncapitalizes foreign word before placing it in dictionary, although the
	 * English casing is preserved.
	 * 
	 * @param filename
	 * @return dictionary mapping foreign to english
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
				String foreign = st.nextToken().trim();
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
	 * Converts a String to title-case
	 */
	private static String toTitleCase(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	private static String getPrePunctuation(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isLetter(s.charAt(i))) {
				return s.substring(0, i);
			}
		}

		return "";
	}

	private static String getPostPunctuation(String s) {
		for (int i = s.length() - 1; i >= 0; i--) {
			if (Character.isLetter(s.charAt(i))) {
				if (i == s.length() - 1)
					return "";
				return s.substring(i + 1);
			}
		}
		return "";
	}

	/**
	 * Filters non-letter from a non-numeric word
	 */
	private static String filterWord(String s) {
		String out = "";

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isDigit(c))
				return s; // don't filter numbers
			if (Character.isLetter(c) || c == '-') {
				out += c;
			}
		}

		return out;
	}

	/**
	 * The bulk of the translating occurs in this method. Translates an
	 * Indonesian word into an English word, given a dictionary.
	 */
	private static String translateIndonesian(String fWord,
			Map<String, String> dict) {
		String eWord = dict.get(fWord);
		if (eWord == null) {
			if (dict.get(fWord.toLowerCase()) != null) {
				// if foreign word is capitalized for some reason
				eWord = toTitleCase(dict.get(fWord.toLowerCase()));
			} else if (fWord.indexOf('-') != -1) {
				// check to see if the word is repeated e.g. "obat-obat"
				// repeated words means plural
				int hyphen = fWord.indexOf('-');
				if (fWord.substring(0, hyphen).equals(
						fWord.substring(hyphen + 1))) {
					eWord = dict.get(fWord.substring(hyphen + 1));
					if (eWord != null) {
						eWord += "s";
					}
				}
			}
		}

		return (eWord == null) ? fWord : eWord;
	}

	/**
	 * 
	 */
	public static void translateRawText(Map<String, String> dict,
			String inFile, String outFile) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(inFile));
			// foreign text
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
			// english output

			String fSentence;
			while ((fSentence = in.readLine()) != null) {
				if (fSentence.isEmpty())
					continue;

				String eSentence = "";
				StringTokenizer st = new StringTokenizer(fSentence);
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					String fWord = filterWord(token);

					/* Punctuation parsing */
					String prePunc, postPunc;
					prePunc = postPunc = "";
					if (fWord.length() != token.length()) {
						// if filtering occurred
						prePunc = getPrePunctuation(token);
						postPunc = getPostPunctuation(token);
						if (fWord.isEmpty())
							continue;
					}

					/* Translating into English */
					String eWord = translateIndonesian(fWord, dict);
					eSentence += prePunc + eWord + postPunc + " ";
				}
				eSentence = eSentence.trim();
				eSentence = Character.toUpperCase(eSentence.charAt(0))
						+ eSentence.substring(1);
				out.write(eSentence.trim());
				out.newLine();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateTaggedText(String taggedFile, String outFile) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(taggedFile));
			// foreign text
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
			// english output

			String tagged;
			while ((tagged = in.readLine()) != null) {
				if (tagged.isEmpty())
					continue;
				StringTokenizer st = new StringTokenizer(tagged);
				ArrayList<String> taggedWords = new ArrayList<String>();
				while (st.hasMoreTokens()) {
					taggedWords.add(st.nextToken());
				}
				out.write(toTitleCase(rewriteSentence(taggedWords)));
				out.newLine();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Given an ArrayList of tagged words (which all should have _ separators),
	 * outputs a rewritten English sentence.
	 */
	public static String rewriteSentence(ArrayList<String> tagged) {
		// Rule: Switch NOUN-ADJ to ADJ-NOUN
		// (also sets up for Rule 2
		for (int i = 0; i < tagged.size() - 1; i++) {
			String word1 = tagged.get(i);
			String word2 = tagged.get(i + 1);
			if (isNoun(word1) && isAdjective(word2)) {
				// System.out.println(word2 + " " + word1);
				tagged.set(i, word2 + "THE");
				tagged.set(i + 1, word1);
			}

		}

		// Rule: Adjust time phrases

		// Rule:

		// Rule: Switch NOT-modal to modal-NOT (not will -> will not)

		// Rule: Break up consecutive nouns into pairs and add 'of' after
		int count = 0;
		for (int i = 0; i < tagged.size(); i++) {
			String word = tagged.get(i);
			if (isNoun(word) && !isProperNoun(word)) {
				count++;
			} else {
				count = 0;
			}

			if (count > 2) {
				tagged.set(i, word + "OF");
				count = 1;
			}
		}

		// Rule: Switch NOUN-NOUN phrases (team badminton -> badminton team)
		for (int i = 0; i < tagged.size() - 1; i++) {
			String word1 = tagged.get(i);
			String word2 = tagged.get(i + 1);
			if (isNoun(word1) && !isProperNoun(word1) && !word1.contains("OF")
					&& isNoun(word2) && !isProperNoun(word2)) {
				// two consecutive nouns (not already separated by 'of')
				if (word2.contains("OF")) {
					word2 = word2.substring(0, word2.length() - 2);
					word1 = word1 + "OF";
				}
				tagged.set(i, word2);
				tagged.set(i + 1, word1);
				System.out.println(word2 + ", " + word1);
			}
		}

		// Rule: Add articles to all noun phrases
		if (isNoun(tagged.get(0))) {
			tagged.set(0, tagged.get(0) + "THE");
		}
		for (int i = 1; i < tagged.size(); i++) {
			String word = tagged.get(i);
			String wordPrev = tagged.get(i - 1);
			if (isNoun(word) && !isProperNoun(word) && !isAdjective(wordPrev)
					&& !isNoun(wordPrev)) {
				tagged.set(i, tagged.get(i) + "THE");
			}
		}

		/** Process into actual sentence **/
		String out = "";
		for (int i = 0; i < tagged.size(); i++) {
			String word = tagged.get(i);
			String processed = word.substring(0, word.indexOf(TAGGED_DELIM));
			if (word.contains("THE")) {
				processed = "the " + processed;
			}
			if (word.contains("OF")) {
				processed = "of " + processed;
			}
			out += processed + " ";
		}
		return out.trim();
	}

	private static boolean isNoun(String taggedWord) {
		int index = taggedWord.indexOf(TAGGED_DELIM);
		if (index == -1)
			return false;
		return (taggedWord.charAt(index + 1) == 'N');
	}

	private static boolean isProperNoun(String taggedWord) {
		int index = taggedWord.indexOf(TAGGED_DELIM);
		if (index == -1)
			return false;
		String taggedData = taggedWord.substring(index + 1);
		return taggedData.contains("NNP");
	}

	private static boolean isAdjective(String taggedWord) {
		int index = taggedWord.indexOf(TAGGED_DELIM);
		if (index == -1)
			return false;
		String taggedData = taggedWord.substring(index + 1);
		return (taggedWord.charAt(index + 1) == 'J')
				|| (taggedData.startsWith("DT"));
	}

	public static void main(String[] args) {
		Map<String, String> dict = DictionaryUtil
				.importDictionary("dictionary.txt");
		DictionaryUtil.translateRawText(dict, "indo_input.txt",
				"english_output.txt");
		DictionaryUtil.updateTaggedText("english_tagged.txt",
				"final_english.txt");
	}
}
