package org.elasticsearch.index.analysis;

import org.ansj.analysis.lucene.AnsjAnalyzer;
import org.ansj.splitWord.analysis.ToAnalysis;
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
		if (settings.get("is_standard", "true").equals("true"))
			analyzer = new AnsjAnalyzer(indexSettings, version, ToAnalysis.class);
		else
			analyzer = new AnsjAnalyzer(indexSettings, version);

	}

	@Override
	public AnsjAnalyzer get()
	{
		return this.analyzer;
	}
}
