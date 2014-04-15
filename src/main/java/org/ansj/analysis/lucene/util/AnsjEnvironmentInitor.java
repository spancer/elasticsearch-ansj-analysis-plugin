package org.ansj.analysis.lucene.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import love.cq.util.IOUtil;

import org.ansj.util.MyStaticValue;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;

public class AnsjEnvironmentInitor {

	/**
	 * Tokenization mode: this determines how the tokenizer handles compound and
	 * unknown words.
	 */
	public static enum Mode {
		/**
		 * baseanalysis in ansj.
		 */
		BASE,
		/**
		 * index analysis in ansj
		 */
		NORMAL,

		/**
		 * search analyzer, using to analyzer.
		 */
		SEARCH,

		/**
		 * smart mode, usually using nlp analysis in Ansj.
		 * 
		 */
		SMART
	}

	/**
	 * Default tokenization mode. Currently this is {@link Mode#NORMAL}.
	 */
	public static final Mode DEFAULT_MODE = Mode.NORMAL;

	public static ESLogger logger = Loggers.getLogger("ansj-analyzer");

	public static void loadUserDictionary(Environment env, Settings settings) {
		File path = new File(env.configFile(), settings.get("user_path",
				AnsjConstant.DEFAULT_USER_LIB_PATH));
		MyStaticValue.userLibrary = path.getAbsolutePath();
		logger.debug("User library path:{}", MyStaticValue.userLibrary);
		// customer dic
		path = new File(env.configFile(), settings.get("ambiguity",
				AnsjConstant.DEFAULT_AMB_FILE_LIB_PATH));
		MyStaticValue.ambiguityLibrary = path.getAbsolutePath();
		logger.debug("Ambiguity library path:{}",
				MyStaticValue.ambiguityLibrary);

		MyStaticValue.isNameRecognition = settings.getAsBoolean("is_name",
				AnsjConstant.DEFAULT_IS_NAME_RECOGNITION);

		MyStaticValue.isNumRecognition = settings.getAsBoolean("is_num",
				AnsjConstant.DEFAULT_IS_NUM_RECOGNITION);

		MyStaticValue.isQuantifierRecognition = settings.getAsBoolean(
				"is_quantifier", AnsjConstant.DEFAUT_IS_QUANTIFIE_RRECOGNITION);

	}

	public static Set<String> loadFilters(Environment env, Settings settings) {
		Set<String> filters = new HashSet<String>();
		String stopLibraryPath = settings.get("stop_path",
				AnsjConstant.DEFAULT_STOP_FILE_LIB_PATH);

		if (stopLibraryPath == null) {
			return filters;
		}

		File stopLibrary = new File(env.configFile(), stopLibraryPath);
		logger.debug("Stop-word library path:{}", stopLibrary.getAbsolutePath());
		if (!stopLibrary.isFile()) {
			logger.info("Can't find the file:" + stopLibraryPath
					+ ", no such file or directory exists!");
		}

		BufferedReader br;
		try {
			br = IOUtil.getReader(stopLibrary.getAbsolutePath(), "UTF-8");
			String temp = null;
			while ((temp = br.readLine()) != null) {
				filters.add(temp);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Ansj stop-word library loaded!");
		return filters;
	}

	public static Mode getMode(Settings settings) {
		Mode mode = DEFAULT_MODE;
		String modeSetting = settings.get("mode", null);
		if (modeSetting != null) {
			if ("search".equalsIgnoreCase(modeSetting)) {
				mode = Mode.SEARCH;
			} else if ("base".equalsIgnoreCase(modeSetting)) {
				mode = Mode.BASE;
			} else if ("smart".equalsIgnoreCase(modeSetting)) {
				mode = Mode.SMART;
			} else if ("normal".equalsIgnoreCase(modeSetting)) {
				mode = Mode.NORMAL;
			}

		}
		return mode;
	}

}
