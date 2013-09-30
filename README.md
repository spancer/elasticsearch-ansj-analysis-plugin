elasticsearch-ansj-analysis-plugin
==================================

ansj analysis elasticsearch plugin

Integrated the lastest ansj analyzer code, and added stopwords functionality.



Steps for Installation
----------------------

1. Download 3 jar files (ansj_seg-0.9 jar,  tree_split-1.0.1.jar, elasticsearch-ansj-analyzer-plugin-0.90.0.jar) and put them under ES plugins directory. Should create analysis-ansj directory under plugins
   directory. Download URL :https://github.com/spancer/elasticsearch-ansj-analysis-plugin/tree/master/lib

2. Create directory ansj under ES config directory. Download the library.zip and decompress it to ansj directory.
   Download URL： https://github.com/spancer/elasticsearch-ansj-analysis-plugin/blob/master/ansj/library.zip

3. Get ES started.


Documentation
-------------
Configuration in elasticsearch.yml
<pre>
index:
  analysis:                   
    analyzer:      
       ansj:
          alias: [ansj_analyzer]
          type: org.elasticsearch.index.analysis.AnsjAnalyzerFactory
          is_standard: true
 </pre>
 Or
 <pre>
index.analysis.analyzer.ansj.type : "ansj"
</pre>

Params Setting Guide
---------------------
Actually, while indexing, we reecommend using the index analysis instead of using a standard analysis or search analysis so
as to get the most granular segmentations to make our search much accurater.

Config an index analyzer using the param is_standard under 'false' value. e.g.:
 <pre>
 index:
  analysis:                   
    analyzer: 
        ansj:
          alias: [ansj_analyzer]
          type: org.elasticsearch.index.analysis.AnsjAnalyzerFactory
          is_standard: false
</pre>

Config a search or standard analysis, set the is_standard to 'true'. e.g.:
<pre>
index:
  analysis:                   
    analyzer: 
       ansj_search_anlayzer:
          type: ansj
          is_standard: true
</pre>
