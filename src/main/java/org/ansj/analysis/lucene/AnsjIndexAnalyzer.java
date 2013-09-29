package org.ansj.analysis.lucene;

import java.io.Reader;
import java.util.Set;

import org.ansj.splitWord.analysis.IndexAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.UserDicLoader;

/**
 * analyzer used while indexing
 * custom stop words are loaded while indexing.
 * TODO whether used stop words should be configurable in future. 
 * 
 * @author spancer
 *
 */
public class AnsjIndexAnalyzer extends Analyzer {

	private final Version matchVersion;

	private final CharArraySet stopwords;
	
	public AnsjIndexAnalyzer(Settings indexSettings, final Version matchVersion) {
		super();
	    UserDicLoader.getInstance().init(indexSettings);
		Set<String> filterWords = UserDicLoader.getInstance().FILTERS;
		this.matchVersion = matchVersion;
	    this.stopwords = filterWords == null ? CharArraySet.EMPTY_SET : CharArraySet
	        .unmodifiableSet(CharArraySet.copy(matchVersion, filterWords));
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		final Tokenizer source = new AnsjTokenizer(reader,new IndexAnalysis(reader));
		
		TokenStreamComponents result;
		if (stopwords.isEmpty()) 
			result = new TokenStreamComponents(source);
		else 
			result = new TokenStreamComponents(source,new StopFilter(matchVersion, source, stopwords));
		
		return result;
	}

}
