elasticsearch-ansj-analysis-plugin
==================================

ansj analysis elasticsearch plugin

Integrated the lastest ansj analyzer code, and added stopwords functionality.



Steps for Installation
----------------------

1. Download 3 jar files (ansj_seg-1.3.jar,  tree_split-1.3.jar, elasticsearch-ansj-analyzer-plugin-1.1.0.jar) and put them under ES plugins directory. Should create analysis-ansj directory under plugins
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
        ansj_normal:
          alias: [ansj_normal]
          type: org.elasticsearch.index.analysis.AnsjAnalyzerFactory
          mode: normal		  
        ansj_search:
          alias: [ansj_search]
          type: org.elasticsearch.index.analysis.AnsjAnalyzerFactory
          mode: search
          is_name: true
        ansj_smart:
          alias: [ansj_smart]
          type: org.elasticsearch.index.analysis.AnsjAnalyzerFactory
          mode: smart
          is_name: true
 </pre>
 Or
 <pre>
index.analysis.analyzer.ansj.type : "ansj"
</pre>

Params Setting Guide
---------------------
Actually, while indexing, we reecommend using the index analysis instead of using a standard analysis or search analysis so
as to get the most granular segmentations to make our search much accurater.

Config an index analyzer using the param mode under 'normal' value. e.g.:
 <pre>
 index:
  analysis:                   
    analyzer: 
        ansj_normal:
          alias: [ansj_analyzer_normal]
          type: org.elasticsearch.index.analysis.AnsjAnalyzerFactory
          mode: normal
</pre>

Config a search or standard analysis, set the mode to 'search'. e.g.:
<pre>
index:
  analysis:                   
    analyzer: 
       ansj_search_anlayzer:
          type: ansj
          mode: search
</pre>


Advice OR Notice:
-----------------
<pre>

Ansj is somewhat unstable, this happens between analyze the similar words and sentences, or even words seperated with whitespace.

It's not accurate, you may not get what you want after analyzed. 

And I suggest using smart chinese instead, it's stable, and supported and tested by ES Team.

Most important is that, it's based on HHMM algorithm, same as ICTCLAS itself.Notice: custom-dic not supported.

URL: https://github.com/elasticsearch/elasticsearch-analysis-smartcn

For Chinese users, maybe IK and mmseg is alternative choice.
</pre>
