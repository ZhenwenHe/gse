package cn.edu.cug.cs.gtl.se.app.cmd;

import cn.edu.cug.cs.gtl.config.Config;
import cn.edu.cug.cs.gtl.filter.FileFilter;
import cn.edu.cug.cs.gtl.protos.DocumentMapper;
import cn.edu.cug.cs.gtl.protoswrapper.DocumentMapperWrapper;
import cn.edu.cug.cs.gtl.se.solr.document.DocumentCreator;
import org.apache.commons.cli.*;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.util.List;

/**
 * 从本地文件获取数据，更新Solr数据库中的数据
 */
public class UpdateCommand implements AppCommand{

    String serverURL = "http://120.24.168.173:8983/solr";
    String collection="gtl";
    String dataDirectory="/User/zhenwenhe/git/data";
    DocumentMapper docMapper= DocumentMapperWrapper.rawMapper();
    FileFilter fileFilter=FileFilter.officesFileFilter();

    public  void run(String [] args){
        if(args.length<=4){
            String[] arg ={
                    "-h", //help information
                    "-s","http://120.24.168.173:8983/solr",
                    "-c","gtl",
                    "-d","/User/zhenwenhe/git/data",//本地目录，里面存放的是需要入库的文件，可以是WORD、PDF等
                    "-m", "fileMapper",
                    "-f", "officesFilter"
            };
            args=arg;
        };
        getLogger().debug("update",args);
        UpdateCommand cmd=this;
        cmd.parseLine(addOptions(),args);
        SolrClient solrClient = new HttpSolrClient
                .Builder()
                .withBaseSolrUrl(cmd.serverURL)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();

        try {
            List<SolrInputDocument> docs = DocumentCreator
                    .of(cmd.dataDirectory, cmd.fileFilter, DocumentMapperWrapper.paragraphMapper())
                    .execute();
            for(SolrInputDocument s: docs){
                final UpdateResponse updateResponse = solrClient.add("gtl", s);
                // Indexed documents must be committed
                solrClient.commit("gtl");
                getLogger().debug(updateResponse.jsonStr());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
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
                hf.printHelp("UpdateCommand", options, true);
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

            if (commandLine.hasOption('s')) {
                this.serverURL =commandLine.getOptionValue('s');
            }

            if (commandLine.hasOption('c')) {
                this.collection =commandLine.getOptionValue('c');
            }

            if (commandLine.hasOption('d')) {
                this.dataDirectory =commandLine.getOptionValue('d');
            }

            if (commandLine.hasOption('m')) {
                String tmp =commandLine.getOptionValue('m');
                if(tmp.toLowerCase().contains("file")){
                    this.docMapper=DocumentMapperWrapper.fileMapper();
                }
                else if(tmp.toLowerCase().contains("line")){
                    this.docMapper=DocumentMapperWrapper.lineMapper();
                }
                else if(tmp.toLowerCase().contains("paragraph")){
                    this.docMapper=DocumentMapperWrapper.paragraphMapper();
                }
                else {
                    this.docMapper=DocumentMapperWrapper.rawMapper();
                }
            }

            if (commandLine.hasOption('f')) {
                String tmp =commandLine.getOptionValue('f');
                //offices,texts,images,shapes,all,raw or auto
                if(tmp.toLowerCase().contains("offices")){
                    this.fileFilter=FileFilter.officesFileFilter();
                }
                else if(tmp.toLowerCase().contains("texts")){
                    this.fileFilter=FileFilter.textsFileFilter();
                }
                else if(tmp.toLowerCase().contains("images")){
                    this.fileFilter=FileFilter.imagesFileFilter();
                }
                else if(tmp.toLowerCase().contains("shapes")){
                    this.fileFilter=FileFilter.shapesFileFilter();
                }
                else //if(tmp.toLowerCase().contains("all"))
                    this.fileFilter=FileFilter.allFileFilter();
            }

        }
        catch (ParseException e) {
            hf.printHelp("UpdateCommand", options, true);
        }

    }
    protected static Options addOptions( ) {
        Options options = new Options();
        Option opt = new Option("h", "help", false, "Print help");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("s", "serverURL", true, "Solr Server URL, eg, http://120.24.168.173:8983/solr");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("c", "collection", true, "current collection in solr database");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("d", "dataDirectory", true, "the root directory of the data set to be inserted");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("m", "mapper", true, "the type of document mapper, it maybe paragraphMapper,lineMapper,fileMapper or rawMapper");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("f", "fileFilter", true, "the type of file filter, it maybe offices,texts,images,shapes,all,raw or auto");
        opt.setRequired(false);
        options.addOption(opt);

        return options;
    }

}
