package org.elasticsearch.index.analysis;

import org.ansj.analysis.lucene.AnsjAnalyzer;
import org.ansj.analysis.lucene.util.AnsjEnvironmentInitor;
import org.apache.lucene.analysis.util.CharArraySet;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;

/**
 * @author spancer
 */
public class AnsjAnalyzerFactory extends AbstractIndexAnalyzerProvider<AnsjAnalyzer>
{
	private final AnsjAnalyzer analyzer;

	@Inject
	public AnsjAnalyzerFactory(Index index, @IndexSettings Settings indexSettings, Environment env,
			@Assisted String name, @Assisted Settings settings)
	{
		
		super(index, indexSettings, name, settings);
        final CharArraySet stopWords = AnsjAnalyzer.getDefaultStopSet(env, settings, version);
        final AnsjEnvironmentInitor.Mode mode = AnsjEnvironmentInitor.getMode(settings);
        AnsjEnvironmentInitor.loadUserDictionary(env, settings);
        analyzer = new AnsjAnalyzer(version, mode, stopWords);

	}

	@Override
	public AnsjAnalyzer get()
	{
		return this.analyzer;
	}
}
