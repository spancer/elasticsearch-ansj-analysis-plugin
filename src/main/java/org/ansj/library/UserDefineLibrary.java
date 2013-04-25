package org.ansj.library;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import org.ansj.domain.Forest;
import org.ansj.domain.Value;
import org.ansj.util.IOUtil;
import org.ansj.util.MyStaticValue;
import org.ansj.util.StringUtil;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;

/**
 * 用户自定义词典操作类
 * 
 * @author ansj
 */
public class UserDefineLibrary
{

	public static Forest FOREST = null;
	private static ESLogger logger = Loggers.getLogger("ansj-analyzer");
	private static final String[] PARAMER = { "userDefine", "1000" };
	private static Environment environment = null;

	public static void init(Settings settings)
	{
		environment = new Environment(settings);
		File userDic = new File(environment.configFile(), MyStaticValue.USER_LIBRARY_DIRECTORY);
		try
		{
			//load system library
			BufferedReader br = MyStaticValue.getSystemLibraryReader();
			FOREST = new Forest();
			String temp = null;
			while ((temp = br.readLine()) != null)
			{
				if (StringUtil.isBlank(temp))
				{
					continue;
				} else
				{
					Library.insertWord(FOREST, temp);
				}
			}
			//load user dic directory
			if ((userDic != null))
			{
				if (userDic.isFile())
				{
					br = IOUtil.getReader(userDic.getAbsolutePath(), "UTF-8");
					while ((temp = br.readLine()) != null)
					{
						if (StringUtil.isBlank(temp))
						{
							continue;
						} else
						{
							Library.insertWord(FOREST, temp);
						}
					}
				} else if (userDic.isDirectory())
				{
					File[] files = userDic.listFiles();
					for (File file : files)
					{
						if (file.getName().trim().endsWith(".dic"))
						{
							br = IOUtil.getReader(file.getAbsolutePath(), "UTF-8");
							while ((temp = br.readLine()) != null)
							{
								if (StringUtil.isBlank(temp))
								{
									continue;
								} else
								{
									Library.insertWord(FOREST, temp);
								}
							}
						}
					}
				} else
				{
					logger.info("Can't find the file:" + MyStaticValue.USER_LIBRARY_DIRECTORY + ", no such file or directory exists!");
				}
			}

		} catch (Exception e)
		{
			logger.info("[Dict Loading] {},Load error!", userDic.toString());
		}

	}
	
	/**
	 * 
	 * @param settings
	 * @return set of stop words from the user defined stop words directory
	 */
	public static Set<String> loadStopWords(Settings settings)
	{
		logger.info("Now in the load stop words method");
		Set<String> filters = new HashSet<String>();
		if (null == environment)
			environment = new Environment(settings);
		File stopword = new File(environment.configFile(), MyStaticValue.STOP_WORD_DICTIONARY);
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
							filters.add(strs[0]);
							logger.info(strs[0]);
						}
					}
				} else
				{
					logger.info("Can't find the file:" + MyStaticValue.STOP_WORD_DICTIONARY + ", no such file or directory exists!");
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
		return filters;

	}


	/**
	 * 关键词增加
	 * 
	 * @param keyWord
	 *            所要增加的关键词
	 * @param nature
	 *            关键词的词性
	 * @param freq
	 *            关键词的词频
	 */
	public static void insertWord(String keyword, String nature, int freq)
	{
		String[] paramers = new String[2];
		paramers[0] = nature;
		paramers[1] = String.valueOf(freq);
		Value value = new Value(keyword, paramers);
		Library.insertWord(FOREST, value);
	}

	private static void initSystemLibrary(Forest FOREST)
	{
		// TODO Auto-generated method stub
		String temp = null;
		BufferedReader br = null;

		br = MyStaticValue.getSystemLibraryReader();

		try
		{
			while ((temp = br.readLine()) != null)
			{
				if (StringUtil.isBlank(temp))
				{
					continue;
				} else
				{
					Library.insertWord(FOREST, temp);
				}
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			IOUtil.close(br);
		}
	}

	// 单个文件价值词典
	public static void loadFile(Forest forest, File file)
	{
		// TODO Auto-generated method stub
		if (!file.canRead())
		{
			System.err.println("file in path " + file.getAbsolutePath() + " can not to read!");
			return;
		}
		String temp = null;
		BufferedReader br = null;
		String[] strs = null;
		Value value = null;
		try
		{
			br = IOUtil.getReader(new FileInputStream(file), "UTF-8");
			while ((temp = br.readLine()) != null)
			{
				if (StringUtil.isBlank(temp))
				{
					continue;
				} else
				{
					strs = temp.split("\t");
					if (strs.length != 3)
					{
						value = new Value(strs[0], PARAMER);
					} else
					{
						value = new Value(strs[0], strs[1], strs[2]);
					}
					Library.insertWord(forest, value);
				}
			}
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			IOUtil.close(br);
			br = null;
		}
	}

	/**
	 * 用户自定义自己的词典,生成
	 * 
	 * @param isSystem
	 *            是否加载系统词典
	 * @param libraryPaths
	 *            词典路径,可以是目录,也可以是具体的文件.如果是目录.只加载后缀为dic的文件
	 * @return 返回的词典结构.
	 */
	public static Forest makeUserDefineForest(boolean isSystem, String... libraryPaths)
	{
		Forest forest = new Forest();
		if (isSystem)
		{
			initSystemLibrary(forest);
		}
		for (String path : libraryPaths)
		{
			loadLibrary(forest, path);
		}
		return forest;
	}

	/**
	 * 加载词典,传入一本词典的路径.或者目录.词典后缀必须为.dic
	 */
	public static void loadLibrary(Forest forest, String temp)
	{
		System.out.println("calling loadLibrary function, the input path is :" + temp);
		// 加载用户自定义词典
		File file = null;
		if ((temp != null))
		{
			file = new File(temp);
			if (file.isFile())
			{
				loadFile(forest, file);
			} else if (file.isDirectory())
			{
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					if (file.getName().trim().endsWith(".dic"))
					{
						loadFile(forest, files[i]);
					}
				}
			} else
			{
				System.err.println("init user library  error :" + temp + " because : can not find that file !");
			}
		}
	}

	/**
	 * 删除关键词
	 */
	public static void removeWord(String word)
	{
		Library.removeWord(FOREST, word);
	}

	/**
	 * 将用户自定义词典清空
	 */
	public static void clear()
	{
		FOREST.clear();
	}

}
