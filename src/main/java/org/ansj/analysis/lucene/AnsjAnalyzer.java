package org.ansj.analysis.lucene;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.ansj.analysis.lucene.util.AnsjEnvironmentInitor;
import org.ansj.lucene.util.AnsjTokenizer;
import org.ansj.splitWord.Analysis;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cjk.CJKWidthFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;

public final class AnsjAnalyzer extends StopwordAnalyzerBase {

	private final Version matchVersion;

	public static boolean pstemming = false;

	public static Set<String> filter;

	private Class<? extends Analysis> analysis;

	public AnsjAnalyzer(final Version matchVersion,
			AnsjEnvironmentInitor.Mode mode) {
		this(matchVersion, mode, DefaultSetHolder.DEFAULT_STOP_SET);
	}

	public static CharArraySet getDefaultStopSet(Environment env, Settings settings, Version version) {
		Set<String> stopWords = AnsjEnvironmentInitor.loadFilters(env, settings);
		if(stopWords.isEmpty())
			return DefaultSetHolder.DEFAULT_STOP_SET;
		else
			return CharArraySet.copy(version, stopWords);
	}

	/**
	 * By default, construct the Ansj IndexAnalyzer. And smart ansj default stop
	 * words are loaded.
	 * 
	 * @see org.ansj.splitWord.analysis.IndexAnalysis
	 * @param matchVersion
	 */
	public AnsjAnalyzer(final Version matchVersion) {
		this(matchVersion, AnsjEnvironmentInitor.Mode.NORMAL, DefaultSetHolder.DEFAULT_STOP_SET);
	}

	/**
	 * 
	 * @param matchVersion
	 * @param mode
	 * @param stopwords
	 */
	public AnsjAnalyzer(Version matchVersion, AnsjEnvironmentInitor.Mode mode,
			CharArraySet stopwords) {
		super(matchVersion, stopwords);
		this.matchVersion = matchVersion;
		if (mode.equals(AnsjEnvironmentInitor.Mode.BASE))
			this.analysis = BaseAnalysis.class;
		else if (mode.equals(AnsjEnvironmentInitor.Mode.SEARCH))
			this.analysis = ToAnalysis.class;
		else if (mode.equals(AnsjEnvironmentInitor.Mode.SMART))
			this.analysis = NlpAnalysis.class;
		else
			this.analysis = IndexAnalysis.class;
	}

	/**
	 * Atomically loads DEFAULT_STOP_SET in a lazy fashion once the outer class
	 * accesses the static final set the first time.
	 */
	private static class DefaultSetHolder {
		static final CharArraySet DEFAULT_STOP_SET;

		static {
			try {
				DEFAULT_STOP_SET = loadStopwordSet(true, AnsjAnalyzer.class,
						"stopwords.txt", "#"); // ignore case
			} catch (IOException ex) {
				// default set should always be present as it is part of the
				// distribution (JAR)
				throw new RuntimeException(
						"Unable to load default stopword or stoptag set");
			}
		}
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		Analysis in;
		try {
			in = analysis.getConstructor(Reader.class).newInstance(reader);
		} catch (Exception e) {
			throw new RuntimeException(
					"Smart Ansj analysis can't be instanced, for original ansj analysis can't be instanced!");
		}

		final Tokenizer tokenizer = new AnsjTokenizer(in, reader, filter,
				pstemming);
		TokenStream stream = new CJKWidthFilter(tokenizer);
		stream = new StopFilter(matchVersion, stream, stopwords);
		stream = new LowerCaseFilter(matchVersion, stream);
		return new TokenStreamComponents(tokenizer, stream);

	}
	
}
