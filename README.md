elasticsearch-ansj-analysis-plugin
==================================

ansj analysis elasticsearch plugin

In this feature, I added the stopword functionality to the ansj analyzer. 
To make the plugin runnable, what you should to is put your stopword library
stopLibrary.dic (you should name it as stopLibrary.dic) under directory 
'%ES_HOME%/config/ansj/library/stop'.

At the same time, you may want to load user library, in order to do that, you 
can easily put your dic files under '%ES_HOME%/config/ansj/library/userLibrary',
the plugin shall automatically load your dic files.
