package cn.edu.cug.cs.gtl.se.app.cmd;

import cn.edu.cug.cs.gtl.filter.FileFilter;
import cn.edu.cug.cs.gtl.protos.SqlQueryStatement;
import cn.edu.cug.cs.gtl.protoswrapper.DocumentMapperWrapper;
import cn.edu.cug.cs.gtl.protoswrapper.SqlWrapper;
import org.apache.commons.cli.*;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MapSolrParams;

import java.io.IOException;
import java.util.*;

public class QueryCommand implements AppCommand{
    String serverURL = "http://120.24.168.173:8983/solr";
    String collection="gtl";
    String fl ="id,title,contents";
    String fq="(contents:beam OR contents:lucene) AND (title:beam)";
    String sort="id asc";//order by
    String q="*:*";
    List<String> fieldList = null;

    public  void run(String[] args){
        if(args.length<=4){
            String[] arg ={
                    "-h", //help information
                    "-s","http://120.24.168.173:8983/solr",
                    "-c","gtl",
                    "-l","id,title,contents",//本地目录，里面存放的是需要入库的文件，可以是WORD、PDF等
                    "-q", "*:*",
                    "-w", "\"(contents:beam OR contents:lucene) AND (title:beam)\"",
                    "-o", "\'id asc\'"
            };
            args=arg;
        };
        getLogger().debug("query",args);
        QueryCommand cmd=this;
        cmd.parseLine(addOptions(),args);
        try {
            SolrClient solrClient = new HttpSolrClient
                    .Builder()
                    .withBaseSolrUrl(cmd.serverURL)
                    .withConnectionTimeout(10000)
                    .withSocketTimeout(60000)
                    .build();

            final Map<String, String> queryParamMap = new HashMap<>();
            queryParamMap.put("q", cmd.q);
            queryParamMap.put("fq", cmd.fq);

            queryParamMap.put("fl", cmd.fl);
            queryParamMap.put("sort", cmd.sort);
            MapSolrParams queryParams = new MapSolrParams(queryParamMap);

            final QueryResponse response = solrClient.query(cmd.collection, queryParams);
            final SolrDocumentList documents = response.getResults();
            getLogger().debug("Found " + documents.getNumFound() + " documents");
            for(SolrDocument document : documents) {
                for(String s : cmd.fieldList){
                    final String tmp = (String) document.getFirstValue(s);
                    getLogger().debug(s + ": " + tmp);
                }
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
                hf.printHelp("QueryCommand", options, true);
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

            if (commandLine.hasOption('l')) {
                 this.fl =commandLine.getOptionValue('l');
                //去除引号
                this.fl=this.fl.replace("\"","");
                this.fl=this.fl.replace("\'","");
                fieldList=Arrays.asList(this.fl.split(","));
            }

            if (commandLine.hasOption('w')) {
                this.fq =commandLine.getOptionValue('w');
                //去除引号
                this.fq=this.fq.replace("\"","");
                this.fq=this.fq.replace("\'","");
            }

            if (commandLine.hasOption('o')) {
                this.sort =commandLine.getOptionValue('o');
                this.sort=this.sort.replace("\"","");
                this.sort=this.sort.replace("\'","");
            }

            if (commandLine.hasOption('q')) {
                this.q =commandLine.getOptionValue('q');
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

        opt = new Option("l", "fieldList", true, "fields,for example, id,title,contents");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("w", "where", true, "field question string");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("o", "orderBy", true, "order by, eg, id asc");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("q", "question", true, "always *:*");
        opt.setRequired(false);
        options.addOption(opt);

        return options;
    }
}
