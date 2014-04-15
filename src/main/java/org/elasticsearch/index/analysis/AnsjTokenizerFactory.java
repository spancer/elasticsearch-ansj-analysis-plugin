package org.elasticsearch.index.analysis;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Set;

import org.ansj.analysis.lucene.util.AnsjEnvironmentInitor;
import org.ansj.lucene.util.AnsjTokenizer;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;

public class AnsjTokenizerFactory extends AbstractTokenizerFactory
{
	boolean pstemming = false;
	
	Set<String> stopwords;
	
	AnsjEnvironmentInitor.Mode mode = AnsjEnvironmentInitor.DEFAULT_MODE;
	@Inject
	public AnsjTokenizerFactory(Index index, Settings indexSettings, String name, Settings settings)
	{
		super(index, indexSettings, name, settings);
	}

	@Inject
    public AnsjTokenizerFactory(Index index, @IndexSettings Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
        pstemming = settings.getAsBoolean("pstemming", false);
        mode = AnsjEnvironmentInitor.getMode(settings);
        AnsjEnvironmentInitor.loadUserDictionary(env, settings);
        stopwords = AnsjEnvironmentInitor.loadFilters(env, settings);
    }


	@Override
	public Tokenizer create(Reader reader) {
		
		if (mode.equals(AnsjEnvironmentInitor.Mode.BASE))
			return new AnsjTokenizer(new BaseAnalysis(new BufferedReader(reader)), reader, stopwords, pstemming);
		else if (mode.equals(AnsjEnvironmentInitor.Mode.SEARCH))
			return new AnsjTokenizer(new ToAnalysis(new BufferedReader(reader)), reader, stopwords, pstemming);
		else if (mode.equals(AnsjEnvironmentInitor.Mode.SMART))
			return new AnsjTokenizer(new NlpAnalysis(new BufferedReader(reader)), reader, stopwords, pstemming);
		else
			return new AnsjTokenizer(new IndexAnalysis(new BufferedReader(reader)), reader, stopwords, pstemming);
			
	}
	

}
