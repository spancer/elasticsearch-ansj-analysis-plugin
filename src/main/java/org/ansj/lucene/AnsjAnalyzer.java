package org.ansj.lucene;

import java.io.Reader;
import java.util.Set;

import org.ansj.library.UserDefineLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;

public class AnsjAnalyzer extends Analyzer
{

	public Set<String> filter;

	public boolean pstemming = false;;

	/**
	 * 如果需要停用词就传入停用词的hashmap
	 * 
	 * @param filter
	 * @param pstemming
	 *            ,是否分析词干
	 */
	public AnsjAnalyzer(Set<String> filter, boolean pstemming)
	{
		this.filter = filter;
		this.pstemming = pstemming;
	}

	public AnsjAnalyzer(boolean pstemming)
	{
		this.pstemming = pstemming;
	}

	public AnsjAnalyzer()
	{
	}

	public AnsjAnalyzer(Settings indexSettings)
	{
		UserDefineLibrary.init(indexSettings);
		this.filter = UserDefineLibrary.loadStopWords(indexSettings);
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader)
	{
		return new AnsjTokenizer(new ToAnalysis(reader), reader, filter, pstemming);
	}

}
