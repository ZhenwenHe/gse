package cn.edu.cug.cs.gtl.se.app.cmd;

import cn.edu.cug.cs.gtl.common.Pair;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java -cp gse-1.0-SNAPSHOT-jar-with-dependencies.jar cn.edu.cug.cs.gtl.se.app.cmd.MainApp -h  -c series.properties  -d /Users/zhenwenhe/git/data+local/UCRArchive_96  -o /Users/zhenwenhe/git/data/outputResult.xls  -p 5,21  -a 3,17   -m  knn  -r  hax
 */
public class MainApp {
    public static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class); //slf4j
    String dataDirectory=null;//输入数据文件的根目录
    List<Pair<String, String>> dataFiles=null;//所有有效的数据集文件对，每对包含一个训练文件和一个测试文件
    String outputFile=null;//输出结果文件
    String configFile =null;//app 配置文件
    String algorithm=null;//方法名
    String representation=null;//表达方式

    public static void main(String[] args) {
        if(args.length<=4){
            String[] arg ={
                    "-h",
                    "-c","series.properties",
                    "-d","/Users/zhenwenhe/git/data/UCRArchive_2018",
                    "-o","/Users/zhenwenhe/git/data/outputResult.xls",
                    "-m", "naivebayes",
                    "-r" , "hax"
            };
            args=arg;
        };
        MainApp mainApp=new MainApp();
        mainApp.parseLine(addOptions(),args);
        mainApp.run();
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

            String tmp;
            this.configFile="series.properties";
            if (commandLine.hasOption('c')) {
                this.configFile=commandLine.getOptionValue('c');
            }

            if (commandLine.hasOption('d')) {
                this.dataDirectory=commandLine.getOptionValue('d');
            }

            if (commandLine.hasOption('o')) {
                this.outputFile=commandLine.getOptionValue('o');
            }

            if (commandLine.hasOption('m')) {
                this.algorithm=commandLine.getOptionValue('m');
            }

            if (commandLine.hasOption('r')) {
                this.representation=commandLine.getOptionValue('r');
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

        opt = new Option("c", "configFile", true, "config properties file");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("p", "paaSizeRange", true, "PAA size range, for example, [5,21]");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("d", "dataDirectory", true, "the root directory of the data set");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("o", "outputFile", true, "the output result file");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("m", "methodName", true, "the method name for example knn, bayes");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("r", "representation", true, "series representation method for example hax, sax");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("a", "alphabetRange", true, "alphabet range , for example, [3,17]");
        opt.setRequired(false);
        options.addOption(opt);


        return options;
    }

    public void run(){
        LOGGER.info(this.dataDirectory);

            System.gc();
    }

}
