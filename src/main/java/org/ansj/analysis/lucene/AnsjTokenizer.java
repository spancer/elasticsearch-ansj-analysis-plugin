package org.ansj.analysis.lucene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.ansj.domain.Term;
import org.ansj.splitWord.Analysis;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

public class AnsjTokenizer extends Tokenizer {
	
	private Analysis analysis;
	
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
	
	public AnsjTokenizer(Reader input) {
		super(input);
	}
	
	/**
     * @param analysis 搜索时用精准分词，索引时用面向索引的分词
     */
	public AnsjTokenizer(Reader input, Analysis analysis) {
		super(input);
		this.analysis = analysis;
	}

	@Override
	public boolean incrementToken() throws IOException {
		clearAttributes();
		
		Term term = analysis.next();
		
		//分词结束
		if (null == term) {
			end();
			return false;
		}
		
		String name = term.getName();
		int length = name.length();
        
		termAtt.copyBuffer(name.toCharArray(), 0, length);
		offsetAtt.setOffset(term.getOffe(), term.getOffe()+length);
		
		return true;
	}
	
	@Override
	public void reset() throws IOException {
		super.reset();
		analysis.resetContent(new BufferedReader(input));
	}
}
