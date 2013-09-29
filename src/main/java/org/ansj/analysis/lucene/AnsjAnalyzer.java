package org.ansj.analysis.lucene;

import java.io.Reader;
import java.util.Set;

import org.ansj.splitWord.Analysis;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.UserDicLoader;

/**
 * Standard ansj analyzer.
 * @author spancer
 *
 */
public class AnsjAnalyzer extends Analyzer
{

	private Class<? extends Analysis> analysis;

	private final Version matchVersion;

	private final CharArraySet stopwords;

	public AnsjAnalyzer(Settings indexSettings, final Version matchVersion)
	{
		this(indexSettings, matchVersion, null);
	}

	public AnsjAnalyzer(Settings indexSettings, final Version matchVersion, Class<? extends Analysis> analysis)
	{
		super();
		UserDicLoader.getInstance().init(indexSettings);
		Set<String> filterWords = UserDicLoader.getInstance().FILTERS;
		this.matchVersion = matchVersion;
		this.analysis = analysis == null ? IndexAnalysis.class : analysis;
		this.stopwords = filterWords == null ? CharArraySet.EMPTY_SET : CharArraySet.unmodifiableSet(CharArraySet.copy(
				matchVersion, filterWords));

	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader)
	{
		Analysis in;
		try
		{
			in = analysis.getConstructor(Reader.class).newInstance(reader);
		} catch (Exception e)
		{
			throw new RuntimeException("Ansj analysis can't be instance!");
		}

		final Tokenizer source = new AnsjTokenizer(reader, in);

		TokenStreamComponents result;
		if (stopwords.isEmpty())
		{
			result = new TokenStreamComponents(source);
		} else
		{
			result = new TokenStreamComponents(source, new StopFilter(matchVersion, source, stopwords));
		}

		return result;
	}
}
