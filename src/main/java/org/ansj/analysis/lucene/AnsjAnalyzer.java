package org.ansj.analysis.lucene;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Set;

import love.cq.domain.Forest;

import org.ansj.analysis.lucene.util.AnsjEnvironmentInitor;
import org.ansj.analysis.lucene.util.AnsjEnvironmentInitor.Mode;
import org.ansj.dic.LearnTool;
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

public final class AnsjAnalyzer extends StopwordAnalyzerBase {

	private final Version matchVersion;

	public static boolean pstemming = false;

	public static Set<String> filter;

	private Mode mode;

	public AnsjAnalyzer(final Version matchVersion,
			AnsjEnvironmentInitor.Mode mode) {
		this(matchVersion, mode, CharArraySet.EMPTY_SET);
	}


	/**
	 * By default, construct the Ansj IndexAnalyzer. And smart ansj default stop
	 * words are loaded.
	 * 
	 * @see org.ansj.splitWord.analysis.IndexAnalysis
	 * @param matchVersion
	 */
	public AnsjAnalyzer(final Version matchVersion) {
		this(matchVersion, AnsjEnvironmentInitor.Mode.NORMAL);
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
		this.mode = mode;
	}
	

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		Analysis analysis;
		if (mode.equals(AnsjEnvironmentInitor.Mode.BASE))
			analysis = new BaseAnalysis(new BufferedReader(reader));
		else if (mode.equals(AnsjEnvironmentInitor.Mode.SEARCH))
			analysis = new ToAnalysis(new BufferedReader(reader));
		else if (mode.equals(AnsjEnvironmentInitor.Mode.SMART))
			analysis = new NlpAnalysis(new BufferedReader(reader), new LearnTool(), new Forest[0]);
		else
			analysis = new IndexAnalysis(new BufferedReader(reader), new Forest[0]);
		final Tokenizer tokenizer = new AnsjTokenizer(analysis, reader, filter,
				pstemming);
		TokenStream stream = new CJKWidthFilter(tokenizer);
		stream = new StopFilter(matchVersion, stream, stopwords);
		stream = new LowerCaseFilter(matchVersion, stream);
		return new TokenStreamComponents(tokenizer, stream);

	}
	
}
