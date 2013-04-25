package org.ansj.lucene;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.ansj.domain.Term;
import org.ansj.domain.TermNature;
import org.ansj.splitWord.Analysis;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

public class AnsjTokenizer extends Tokenizer {
	// 当前词
	private CharTermAttribute termAtt;
	// 偏移量
	private OffsetAttribute offsetAtt;
	// 距离
	private PositionIncrementAttribute positionAttr;
	private Analysis ta = null;
	private Set<String> filter;
	private boolean pstemming;
	private static ESLogger logger = Loggers.getLogger("ansj-analyzer");

	private final PorterStemmer stemmer = new PorterStemmer();

	public AnsjTokenizer(Analysis analysis, Reader input, Set<String> filter, boolean pstemming) {
		super(input);
		ta = analysis ;
		termAtt = addAttribute(CharTermAttribute.class);
		offsetAtt = addAttribute(OffsetAttribute.class);
		positionAttr = addAttribute(PositionIncrementAttribute.class);
		this.filter = filter;
		this.pstemming = pstemming;
	}

	@Override
	public boolean incrementToken() throws IOException {
		// TODO Auto-generated method stub
		clearAttributes();
		int position = 0;
		Term term = ta.next();
		String name = null;
		int length = 0;
		while (filter != null && term != null && (filter.contains(term.getName()) || term.getName().length() > 30))
		{
			logger.info("now in the incrementToken method, the term name is :"+ term.getName());
			logger.info("now in the incrementToken method,  filter size is :" + filter.size());
			if (term == null) {
				break;
			}
			length = term.getName().length();
			if (pstemming && term.getTermNatures().termNatures[0] == TermNature.EN) {
				System.out.println(pstemming);
				name = stemmer.stem(term.getName());
				term.setName(name);
			}
			position++;
			term = ta.next();
		} 

		if (term != null) {
			positionAttr.setPositionIncrement(position);
			termAtt.copyBuffer(term.getName().toCharArray(), 0, term.getName().length());
			offsetAtt.setOffset(term.getOffe(), term.getOffe() + length);
			return true;
		} else {
			end();
			return false;
		}
	}
}
