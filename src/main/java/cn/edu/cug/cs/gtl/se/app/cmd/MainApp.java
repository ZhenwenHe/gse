package cn.edu.cug.cs.gtl.se.app.cmd;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 查询命令：
 * java -cp gse-1.0-SNAPSHOT-jar-with-dependencies.jar cn.edu.cug.cs.gtl.se.app.cmd.MainApp
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
 * java -cp gse-1.0-SNAPSHOT-jar-with-dependencies.jar cn.edu.cug.cs.gtl.se.app.cmd.MainApp
 * -r update
 * -s http://120.24.168.173:8983/solr
 * -c gtl
 * -d /Users/zhenwenhe/git/data
 * -m fileMapper
 * -f officesFilter
 * 注意：参数值如果是含有空格的，必须用引号引起来。-r必须是MainApp后面的第一个参数。
 */
public class MainApp {
    public static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class); //slf4j
    AppCommand command = null;

    public static void main(String[] args) {
        if(args.length<=0){
//            String[] arg =
//                    {
//                    "-r","update", // query, update, or insert command,必须是首个参数
//                    "-s","http://202.114.194.3:8983/solr",
//                    "-c","solr",
//                    "-d", "/Users/zhenwenhe/tmp",
//                    "-m", "paragraphMapper",
//                    "-f", "officesFilter",
//                    "-h" //help information
//            };
            String[] arg =
                    {
                            "-r","query", // query, update, or insert command,必须是首个参数
                            "-s","http://202.114.194.3:8983/solr",
                            "-c","solr",
                            "-l",  "id,title,contents",
                            "-q", "*:*",
                            "-w", "(contents:beam OR contents:lucene) AND (title:beam)",
                            "-o",  "id asc",
                            "-h" //help information
                    };
            args=arg;
        };
        int i = 0;
        for(String s:args){
            if(s.equals("-r")||s.equals("command")) {
                ++i;
                break;
            }
            else
                ++i;
        }
        int c = args.length-i;
        String [] cmdArgs = new String[c];
        String [] mainArgs = new String[i+1];
        for(int j=0;j<i+1;++j){
            mainArgs[j]=args[j];
        }
        MainApp mainApp=new MainApp();
        mainApp.parseLine(addOptions(),mainArgs);
        for(int j=i;j<args.length;++j){
            cmdArgs[j-i]=args[j];
        }
        mainApp.run(cmdArgs);
    }


    protected  void parseLine(Options options, String[] args){

        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(110);
        CommandLine commandLine = null;
        CommandLineParser parser = new PosixParser();
        try {
            commandLine = parser.parse(options, args);
            if (commandLine.hasOption('h')) {
                // 打印使用帮助
                hf.printHelp("MainApp", options, true);
            }


            // 打印opts的名称和值
            System.out.println("--------------------------------------");
            Option[] opts = commandLine.getOptions();
            if (opts != null) {
                for (Option opt1 : opts) {
                    String name = opt1.getLongOpt();
                    String value = commandLine.getOptionValue(name);
                    System.out.println(name + "=>" + value);
                }
            }
            System.out.println("--------------------------------------");


            if (commandLine.hasOption('r')) {
                String tmp =commandLine.getOptionValue('r').toLowerCase().trim();
                if(tmp.contains("update")){
                    this.command=new UpdateCommand();
                }
                else//(tmp.contains("query"))
                    this.command=new QueryCommand();
            }


        }
        catch (ParseException e) {
            hf.printHelp("MainApp", options, true);
        }

    }
    protected static Options addOptions( ) {
        Options options = new Options();
        Option opt = new Option("h", "help", false, "Print help");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("r", "command", true, "query or update");
        opt.setRequired(false);
        options.addOption(opt);

        return options;
    }

    public void run(String [] args){
        if(this.command!=null)
            this.command.run(args);
        System.gc();
    }

}
