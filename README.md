# gse
serach engine wrapper for lucene-solr

/**
 * 查询命令：
 * Java -cp gse-1.0-SNAPSHOT-jar-with-dependencies.jar cn.edu.cug.cs.gtl.se.app.cmd.MainApp
 * -r query    
 * -s http://120.24.168.173:8983/solr
 * -c gtl
 * -l id,title,contents
 * -q *:*
 * -w "(contents:beam OR contents:lucene) AND (title:beam)"
 * -o "id asc"
 * 注意：参数值如果是含有空格的，必须用引号引起来。-r必须是MainApp后面的第一个参数。
 *
 * 更新命令：
 * Java -cp gse-1.0-SNAPSHOT-jar-with-dependencies.jar cn.edu.cug.cs.gtl.se.app.cmd.MainApp
 * -r update
 * -s http://120.24.168.173:8983/solr
 * -c gtl
 * -d /User/zhenwenhe/git/data
 * -m fileMapper
 * -f officesFilter
 * 注意：参数值如果是含有空格的，必须用引号引起来。-r必须是MainApp后面的第一个参数。
 */