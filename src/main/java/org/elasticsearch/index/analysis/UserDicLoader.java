package org.elasticsearch.index.analysis;

import static org.ansj.util.MyStaticValue.LIBRARYLOG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import love.cq.domain.Forest;
import love.cq.library.Library;
import love.cq.util.IOUtil;
import love.cq.util.StringUtil;

import org.ansj.analysis.lucene.util.AnsjConstant;
import org.ansj.library.UserDefineLibrary;
import org.ansj.util.MyStaticValue;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;

/**
 * dic loader, singleton, load once.
 */
public class UserDicLoader
{

	private static UserDicLoader singleton;

	/**
	 * 配置对象
	 */
	private ESLogger logger = null;
	private static boolean dictInited = false;
	private Environment environment;
	public Set<String> FILTERS = new HashSet<String>();
	public static Forest FOREST = null;
    public static Forest ambiguityForest = null;

	private UserDicLoader()
	{
		logger = Loggers.getLogger("ansj-analyzer");
	}

	static
	{
		singleton = new UserDicLoader();
	}

	public void init(Settings indexSettings)
	{

		if (!dictInited)
		{
			environment = new Environment(indexSettings);
			initUserLibrary();
			initAmbiguityLibrary();
			loadStopWords();
			dictInited = true;
		}
	}
	
	private void initAmbiguityLibrary() {
        // TODO Auto-generated method stub
        String ambiguityLibrary = MyStaticValue.ambiguityLibrary;
        if (StringUtil.isBlank(ambiguityLibrary)) {
            LIBRARYLOG.warning("init ambiguity  waring :" + ambiguityLibrary
                               + " because : not find that file or can not to read !");
            return;
        }
        try {
            ambiguityLibrary = MyStaticValue.ambiguityLibrary;
        } catch (Exception exception) {
            LIBRARYLOG.warning("init ambiguity  waring :" + ambiguityLibrary
                               + " because : not find that file or can not to read !");
        }
        File file = new File(ambiguityLibrary);
        if (file.isFile() && file.canRead()) {
            try {
                ambiguityForest = Library.makeForest(ambiguityLibrary);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                LIBRARYLOG.warning("init ambiguity  error :" + ambiguityLibrary
                                   + " because : not find that file or can not to read !");
                e.printStackTrace();
            }
            LIBRARYLOG.info("init ambiguityLibrary ok!");
        } else {
            LIBRARYLOG.warning("init ambiguity  waring :" + ambiguityLibrary
                               + " because : not find that file or can not to read !");
        }
    }

	private void initUserLibrary()
	{
		try
		{
			FOREST = new Forest();
			// 加载用户自定义词典
			String userLibrary = MyStaticValue.userLibrary;
			UserDefineLibrary.loadLibrary(FOREST, userLibrary);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		
	}

	/**
	 * 
	 * @return UserDicLoader singleton
	 */
	public static UserDicLoader getSingleton()
	{
		if (singleton == null)
		{
			throw new IllegalStateException(
					"User dic library has not been initialized, please call init method firstly!");
		}
		return singleton;
	}

	public static UserDicLoader getInstance()
	{
		return UserDicLoader.singleton;
	}

	/**
	 * 
	 * @param settings
	 * @return set of stop words from the user defined stop words directory
	 */
	public void loadStopWords()
	{
		logger.info("loading stop words ....");
		File stopword = new File(environment.configFile(), AnsjConstant.STOP_WORD_DICTIONARY);
		BufferedReader br;
		try
		{
			br = IOUtil.getReader(stopword.getAbsolutePath(), "UTF-8");
			if ((stopword != null))
			{
				if (stopword.isFile())
				{
					String temp = null;
					String[] strs = null;
					while ((temp = br.readLine()) != null)
					{
						if (StringUtil.isBlank(temp))
						{
							continue;
						} else
						{
							strs = temp.split("\t");
							FILTERS.add(strs[0]);
							logger.info(strs[0]);
						}
					}
				} else
				{
					logger.info("Can't find the file:" + AnsjConstant.STOP_WORD_DICTIONARY
							+ ", no such file or directory exists!");
				}
			}
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

}
