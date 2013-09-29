package org.elasticsearch.index.analysis;

import java.io.Reader;

import org.ansj.analysis.lucene.AnsjTokenizer;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;

public class AnsjTokenizerFactory extends AbstractTokenizerFactory
{
	boolean pstemming = false;
	/**
	 * is_standard is used to set the analyzer. while true, toAnalysis is used .
	 */
	boolean isStandard = false;

	@Inject
	public AnsjTokenizerFactory(Index index, Settings indexSettings, String name, Settings settings)
	{
		super(index, indexSettings, name, settings);
		UserDicLoader.getInstance().init(indexSettings);
		if (settings.get("is_standard", "true").equals("true"))
			isStandard = true;
	}

	@Override
	public Tokenizer create(Reader reader)
	{
		return new AnsjTokenizer(reader, isStandard == true ? new ToAnalysis(reader) : new IndexAnalysis(reader));
	}

}
