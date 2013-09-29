package org.elasticsearch.index.analysis;


/**
 * @author spancer
 */
public class AnsjAnalysisBinderProcessor extends
    AnalysisModule.AnalysisBinderProcessor {
  
  @Override
  public void processTokenFilters(TokenFiltersBindings tokenFiltersBindings) {
  }
  
  @Override
  public void processAnalyzers(AnalyzersBindings analyzersBindings) {
    analyzersBindings.processAnalyzer("ansj", AnsjAnalyzerFactory.class);
    super.processAnalyzers(analyzersBindings);
  }
  
  @Override
  public void processTokenizers(TokenizersBindings tokenizersBindings) {
    tokenizersBindings.processTokenizer("ansj", AnsjTokenizerFactory.class);
    super.processTokenizers(tokenizersBindings);
  }
}
